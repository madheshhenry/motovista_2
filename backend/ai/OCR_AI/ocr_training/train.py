import os
import torch
import torch.nn as nn
from torch.utils.data import DataLoader
from tqdm import tqdm

from config import EPOCHS, LR, BATCH_SIZE, CHARS, IMG_H, IMG_W
from dataset import OCRDataset, collate_fn
from model import CRNN

def ctc_decode(preds, blank=0):
    # preds: (T,B,C) -> best path
    pred_labels = preds.argmax(2).transpose(0,1)  # (B,T)
    results = []
    for seq in pred_labels:
        prev = -1
        s = []
        for p in seq.cpu().numpy():
            if p != blank and p != prev:
                s.append(p)
            prev = p
        results.append(s)
    return results

def indices_to_text(indices, idx2char):
    return "".join([idx2char[i] for i in indices if i in idx2char])

def main():
    # CHANGE THIS PATH to your dataset path
    DATASET_DIR = r"D:\OCR_AI\dataset_generator\synthetic_ocr_dataset"

    device = "cuda" if torch.cuda.is_available() else "cpu"
    print("✅ Device:", device)

    num_classes = 1 + len(CHARS)  # + blank
    model = CRNN(num_classes=num_classes, in_h=IMG_H, in_w=IMG_W).to(device)

    criterion = nn.CTCLoss(blank=0, zero_infinity=True)
    optimizer = torch.optim.Adam(model.parameters(), lr=LR, weight_decay=1e-5)
    scheduler = torch.optim.lr_scheduler.ReduceLROnPlateau(optimizer, mode='max', factor=0.5, patience=3)

    train_ds = OCRDataset(DATASET_DIR, "train")
    val_ds = OCRDataset(DATASET_DIR, "val")

    train_loader = DataLoader(train_ds, batch_size=BATCH_SIZE, shuffle=True, num_workers=0, collate_fn=collate_fn)
    val_loader = DataLoader(val_ds, batch_size=BATCH_SIZE, shuffle=False, num_workers=0, collate_fn=collate_fn)

    from config import idx2char

    for epoch in range(EPOCHS):
        model.train()
        total_loss = 0

        for images, targets, target_lengths, texts in tqdm(train_loader, desc=f"Epoch {epoch+1}/{EPOCHS}"):
            images = images.to(device)
            targets = targets.to(device)

            preds = model(images)  # (T,B,C)
            T, B, C = preds.size()

            input_lengths = torch.full(size=(B,), fill_value=T, dtype=torch.long).to(device)

            log_probs = preds.log_softmax(2)
            loss = criterion(log_probs, targets, input_lengths, target_lengths.to(device))

            optimizer.zero_grad()
            loss.backward()
            optimizer.step()

            total_loss += loss.item()

        avg_loss = total_loss / len(train_loader)

        # validation quick accuracy
        model.eval()
        correct = 0
        total = 0

        with torch.no_grad():
            for images, targets, target_lengths, gt_texts in val_loader:
                images = images.to(device)
                preds = model(images)
                decoded = ctc_decode(preds, blank=0)

                for d, gt in zip(decoded, gt_texts):
                    pred_text = indices_to_text(d, idx2char)
                    if pred_text == gt:
                        correct += 1
                    total += 1

        acc = (correct / total) * 100 if total else 0
        print(f"\n✅ Epoch {epoch+1} Loss: {avg_loss:.4f} | Val Exact Acc: {acc:.2f}% | LR: {optimizer.param_groups[0]['lr']}\n")

        # Step scheduler based on validation accuracy
        scheduler.step(acc)

        # save checkpoint
        os.makedirs("checkpoints", exist_ok=True)
        torch.save(model.state_dict(), f"checkpoints/ocr_epoch_{epoch+1}.pth")

    # final save
    torch.save(model.state_dict(), "ocr_final.pth")
    print("✅ Training finished. Model saved: ocr_final.pth")

if __name__ == "__main__":
    main()
