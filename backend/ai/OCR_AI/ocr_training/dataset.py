import os
import cv2
import random
import numpy as np
import pandas as pd
import torch
from torch.utils.data import Dataset
from config import IMG_H, IMG_W, char2idx

class OCRDataset(Dataset):
    def __init__(self, dataset_dir, split="train"):
        self.dataset_dir = dataset_dir
        self.split = split

        csv_path = os.path.join(dataset_dir, "labels.csv")
        df = pd.read_csv(csv_path)
        df = df[df["split"] == split].reset_index(drop=True)

        self.items = df.to_dict("records")

    def __len__(self):
        return len(self.items)

    def encode_text(self, text):
        text = text.strip().upper()
        return [char2idx[c] for c in text if c in char2idx]

    def __getitem__(self, idx):
        item = self.items[idx]
        filename = item["filename"]
        text = item["text"]

        img_path = os.path.join(self.dataset_dir, "images", self.split, filename)
        img = cv2.imread(img_path, cv2.IMREAD_GRAYSCALE)

        if img is None:
            raise Exception(f"Image not found: {img_path}")

        # resize keeping aspect ratio then pad
        h, w = img.shape
        scale = IMG_H / h
        new_w = int(w * scale)
        img = cv2.resize(img, (new_w, IMG_H))

        if new_w < IMG_W:
            pad = IMG_W - new_w
            # Randomize padding (left/right split)
            left_pad = random.randint(0, pad)
            right_pad = pad - left_pad
            img = cv2.copyMakeBorder(img, 0, 0, left_pad, right_pad, cv2.BORDER_CONSTANT, value=255)
        else:
            img = cv2.resize(img, (IMG_W, IMG_H))

        # --- On-the-fly Augmentation ---
        if self.split == "train":
            # 1. Random Brightness/Contrast
            if random.random() < 0.5:
                alpha = random.uniform(0.8, 1.2)
                beta = random.randint(-20, 20)
                img = cv2.convertScaleAbs(img, alpha=alpha, beta=beta)
            
            # 2. Random Blur
            if random.random() < 0.3:
                k = random.choice([3, 5])
                img = cv2.GaussianBlur(img, (k, k), 0)

            # 3. Random Pixel Noise
            if random.random() < 0.2:
                noise = np.random.randint(0, 15, img.shape, dtype='uint8')
                img = cv2.add(img, noise)

        img = img.astype("float32") / 255.0
        img = torch.tensor(img).unsqueeze(0)  # (1,H,W)

        target = torch.tensor(self.encode_text(text), dtype=torch.long)
        return img, target, text

def collate_fn(batch):
    images, targets, texts = zip(*batch)

    images = torch.stack(images, dim=0)

    target_lengths = torch.tensor([len(t) for t in targets], dtype=torch.long)
    targets_concat = torch.cat(targets)

    return images, targets_concat, target_lengths, texts
