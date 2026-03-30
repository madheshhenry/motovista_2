import os
import cv2
import torch
import random

from model import CRNN
from config import CHARS, IMG_H, IMG_W, idx2char

def preprocess(img_path):
    img = cv2.imread(img_path, cv2.IMREAD_GRAYSCALE)
    h, w = img.shape
    scale = IMG_H / h
    new_w = int(w * scale)
    img = cv2.resize(img, (new_w, IMG_H))

    if new_w < IMG_W:
        pad = IMG_W - new_w
        img = cv2.copyMakeBorder(img, 0, 0, 0, pad, cv2.BORDER_CONSTANT, value=255)
    else:
        img = cv2.resize(img, (IMG_W, IMG_H))

    img = img.astype("float32") / 255.0
    img = torch.tensor(img).unsqueeze(0).unsqueeze(0)  # (1,1,H,W)
    return img

def ctc_decode(preds, blank=0):
    pred_labels = preds.argmax(2).transpose(0,1)  # (B,T)
    seq = pred_labels[0].cpu().numpy()

    prev = -1
    out = []
    for p in seq:
        if p != blank and p != prev:
            out.append(p)
        prev = p
    return out

def indices_to_text(indices):
    return "".join([idx2char[i] for i in indices if i in idx2char])

def main():
    device = "cpu"
    num_classes = 1 + len(CHARS)

    model = CRNN(num_classes=num_classes).to(device)
    model.load_state_dict(torch.load(r"checkpoints\ocr_epoch_16.pth", map_location=device))
    model.eval()

    # pick a random test image
    test_dir = r"D:\OCR_AI\dataset_generator\synthetic_ocr_dataset\images\test"
    img_file = random.choice(os.listdir(test_dir))
    img_path = os.path.join(test_dir, img_file)

    img = preprocess(img_path).to(device)

    with torch.no_grad():
        preds = model(img)  # (T,B,C)
        decoded = ctc_decode(preds, blank=0)
        text = indices_to_text(decoded)

    print("✅ Image:", img_path)
    print("✅ Pred :", text)

if __name__ == "__main__":
    main()
