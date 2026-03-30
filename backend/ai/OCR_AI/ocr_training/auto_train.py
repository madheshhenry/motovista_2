import os
import csv
import time
import shutil
import torch
import torch.nn as nn
from torch.utils.data import DataLoader, Dataset
from datetime import datetime
import pandas as pd
import random
import cv2

# Import project modules
from model import CRNN
from config import CHARS, BATCH_SIZE, LR, IMG_H, IMG_W, char2idx
from dataset import collate_fn

# Paths
SYNTHETIC_DIR = r"D:\OCR_AI\dataset_generator\synthetic_ocr_dataset"
REAL_DATA_DIR = r"D:\OCR_AI\ocr_server\real_data"
REAL_IMAGES_DIR = os.path.join(REAL_DATA_DIR, "images")
REAL_LABELS_CSV = os.path.join(REAL_DATA_DIR, "labels.csv")

MODEL_PATH = "ocr_final.pth"
BACKUP_DIR = "backups"
LOG_DIR = "logs"
LAST_COUNT_FILE = "last_train_count.txt"

# Params
OVERSAMPLE_REAL = 10  # Repeat real samples 10x for importance
FINE_TUNE_EPOCHS = 5
MIN_NEW_SAMPLES = 100 # Trigger training after this many new samples

DEVICE = "cuda" if torch.cuda.is_available() else "cpu"

os.makedirs(BACKUP_DIR, exist_ok=True)
os.makedirs(LOG_DIR, exist_ok=True)

class CombinedDataset(Dataset):
    def __init__(self):
        self.items = []
        
        # 1. Load Synthetic (Train split only)
        if os.path.exists(SYNTHETIC_DIR):
            syn_csv = os.path.join(SYNTHETIC_DIR, "labels.csv")
            df_syn = pd.read_csv(syn_csv)
            df_syn = df_syn[df_syn["split"] == "train"]
            for _, row in df_syn.iterrows():
                self.items.append({
                    "path": os.path.join(SYNTHETIC_DIR, "images", "train", row["filename"]),
                    "text": row["text"]
                })
        
        # 2. Load Real and Oversample
        if os.path.exists(REAL_LABELS_CSV):
            df_real = pd.read_csv(REAL_LABELS_CSV)
            real_items = []
            for _, row in df_real.iterrows():
                real_items.append({
                    "path": os.path.join(REAL_IMAGES_DIR, row["filename"]),
                    "text": row["text"]
                })
            
            # Add to list multiple times
            self.items.extend(real_items * OVERSAMPLE_REAL)
            print(f"✅ Loaded {len(real_items)} real samples (Oversampled to {len(real_items)*OVERSAMPLE_REAL})")

    def encode_text(self, text):
        return [char2idx[c] for c in str(text).upper() if c in char2idx]

    def __len__(self):
        return len(self.items)

    def __getitem__(self, idx):
        item = self.items[idx]
        img = cv2.imread(item["path"], cv2.IMREAD_GRAYSCALE)
        
        if img is None:
            # Fallback for missing file
            img = np.zeros((IMG_H, IMG_W), dtype=np.uint8)

        # Preprocess matching the training pipeline
        h, w = img.shape
        scale = IMG_H / h
        new_w = int(w * scale)
        img = cv2.resize(img, (new_w, IMG_H))

        if new_w < IMG_W:
            pad = IMG_W - new_w
            left = random.randint(0, pad)
            right = pad - left
            img = cv2.copyMakeBorder(img, 0, 0, left, right, cv2.BORDER_CONSTANT, value=255)
        else:
            img = cv2.resize(img, (IMG_W, IMG_H))

        # Random augmentations for stability
        if random.random() < 0.3:
            alpha = random.uniform(0.9, 1.1)
            img = cv2.convertScaleAbs(img, alpha=alpha, beta=random.randint(-10, 10))

        img = img.astype("float32") / 255.0
        img = torch.tensor(img).unsqueeze(0)
        target = torch.tensor(self.encode_text(item["text"]), dtype=torch.long)
        
        return img, target, item["text"]

def train_iteration():
    print(f"🚀 Starting Auto-Train at {datetime.now()}")
    
    # 1. Load counts
    current_count = 0
    if os.path.exists(REAL_LABELS_CSV):
        with open(REAL_LABELS_CSV, 'r') as f:
            current_count = sum(1 for line in f) - 1 # exclude header
    
    last_count = 0
    if os.path.exists(LAST_COUNT_FILE):
        with open(LAST_COUNT_FILE, 'r') as f:
            last_count = int(f.read().strip())

    new_samples = current_count - last_count
    if new_samples < MIN_NEW_SAMPLES:
        print(f"ℹ️ Not enough new samples ({new_samples}/{MIN_NEW_SAMPLES}). Skipping.")
        return

    # 2. Prepare Data
    dataset = CombinedDataset()
    loader = DataLoader(dataset, batch_size=BATCH_SIZE, shuffle=True, num_workers=0, collate_fn=collate_fn)

    # 3. Load Model
    num_classes = 1 + len(CHARS)
    model = CRNN(num_classes, in_h=IMG_H, in_w=IMG_W).to(DEVICE)
    if os.path.exists(MODEL_PATH):
        model.load_state_dict(torch.load(MODEL_PATH, map_location=DEVICE))
    
    criterion = nn.CTCLoss(blank=0, zero_infinity=True)
    optimizer = torch.optim.Adam(model.parameters(), lr=LR * 0.1) # Lower LR for fine-tuning

    # 4. Loop
    model.train()
    for epoch in range(FINE_TUNE_EPOCHS):
        total_loss = 0
        for images, targets, target_lengths, _ in loader:
            images = images.to(DEVICE)
            targets = targets.to(DEVICE)
            
            preds = model(images)
            T, B, C = preds.size()
            input_lengths = torch.full(size=(B,), fill_value=T, dtype=torch.long).to(DEVICE)
            
            log_probs = preds.log_softmax(2)
            loss = criterion(log_probs, targets, input_lengths, target_lengths.to(DEVICE))
            
            optimizer.zero_grad()
            loss.backward()
            optimizer.step()
            total_loss += loss.item()
            
        print(f"Epoch {epoch+1}/{FINE_TUNE_EPOCHS} Loss: {total_loss/len(loader):.4f}")

    # 5. Save & Swap
    timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
    backup_path = os.path.join(BACKUP_DIR, f"ocr_backup_{timestamp}.pth")
    
    if os.path.exists(MODEL_PATH):
        shutil.copy(MODEL_PATH, backup_path)
    
    torch.save(model.state_dict(), MODEL_PATH)
    
    with open(LAST_COUNT_FILE, "w") as f:
        f.write(str(current_count))
        
    log_path = os.path.join(LOG_DIR, f"train_{timestamp}.log")
    with open(log_path, "w") as f:
        f.write(f"Train completed at {timestamp}\nNew samples: {new_samples}\nFinal Loss: {total_loss/len(loader):.4f}")

    print(f"✅ Auto-Train successful. Model updated and backed up to {backup_path}")

if __name__ == "__main__":
    while True:
        try:
            train_iteration()
        except Exception as e:
            print(f"❌ Error in auto-train loop: {e}")
        
        # Check every 1 hour
        print("💤 Waiting for 1 hour...")
        time.sleep(3600)
