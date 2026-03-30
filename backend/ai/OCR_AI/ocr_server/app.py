import os
import re
import cv2
import csv
import uuid
import shutil
import numpy as np
import torch
from flask import Flask, request, jsonify
from datetime import datetime

from model import CRNN
from config import CHARS, IMG_H, IMG_W, idx2char

app = Flask(__name__)

DEVICE = "cuda" if torch.cuda.is_available() else "cpu"
MODEL_PATH = "ocr_final.pth"
PENDING_DIR = r"d:\OCR_AI\ocr_server\uploads\pending"
REAL_DATA_DIR = r"d:\OCR_AI\ocr_server\real_data"
REAL_IMAGES_DIR = os.path.join(REAL_DATA_DIR, "images")
REAL_LABELS_CSV = os.path.join(REAL_DATA_DIR, "labels.csv")

# Ensure dirs exist
os.makedirs(PENDING_DIR, exist_ok=True)
os.makedirs(REAL_IMAGES_DIR, exist_ok=True)

# Load model once
num_classes = 1 + len(CHARS)
model = None
model_mtime = 0

def load_model():
    global model, model_mtime
    if os.path.exists(MODEL_PATH):
        print(f"🔄 Loading model from {MODEL_PATH}")
        mtime = os.path.getmtime(MODEL_PATH)
        new_model = CRNN(num_classes=num_classes, in_h=IMG_H, in_w=IMG_W).to(DEVICE)
        new_model.load_state_dict(torch.load(MODEL_PATH, map_location=DEVICE))
        new_model.eval()
        model = new_model
        model_mtime = mtime
    else:
        print("⚠️ Model file not found, initializing empty model")
        model = CRNN(num_classes=num_classes, in_h=IMG_H, in_w=IMG_W).to(DEVICE)
        model.eval()

load_model()

def check_model_reload():
    global model_mtime
    if os.path.exists(MODEL_PATH):
        mtime = os.path.getmtime(MODEL_PATH)
        if mtime > model_mtime:
            print("🔔 New model detected, reloading...")
            load_model()

def preprocess_image(img_bgr):
    img = cv2.cvtColor(img_bgr, cv2.COLOR_BGR2GRAY)

    # 1. Contrast Enhancement (CLAHE)
    clahe = cv2.createCLAHE(clipLimit=2.0, tileGridSize=(8, 8))
    img = clahe.apply(img)

    # 2. Resize maintaining aspect ratio
    h, w = img.shape
    scale = IMG_H / h
    new_w = int(w * scale)
    img = cv2.resize(img, (new_w, IMG_H))

    # 3. Padding (Center text)
    if new_w < IMG_W:
        pad = IMG_W - new_w
        left = pad // 2
        right = pad - left
        # Use a gray color for padding (180 is typical for our metal backgrounds)
        img = cv2.copyMakeBorder(img, 0, 0, left, right, cv2.BORDER_CONSTANT, value=180)
    else:
        img = cv2.resize(img, (IMG_W, IMG_H))

    img = img.astype("float32") / 255.0
    img = torch.tensor(img).unsqueeze(0).unsqueeze(0)  # (1,1,H,W)
    return img

def ctc_decode(preds, blank=0):
    pred_labels = preds.argmax(2).transpose(0, 1)  # (B,T)
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

def clean_engine_chassis(text):
    text = text.strip().upper()
    # allow only A-Z0-9
    text = re.sub(r"[^A-Z0-9]", "", text)
    return text

def get_tta_variations(img):
    """Generate variations of the image for Test-Time Augmentation."""
    variations = [img]
    
    # Variation 1: Sharpened
    kernel = np.array([[-1,-1,-1], [-1,9,-1], [-1,-1,-1]])
    variations.append(cv2.filter2D(img, -1, kernel))
    
    # Variation 2: Brighter
    variations.append(cv2.convertScaleAbs(img, alpha=1.2, beta=20))
    
    # Variation 3: Darker (high contrast)
    variations.append(cv2.convertScaleAbs(img, alpha=0.9, beta=-10))
    
    return variations

@app.route("/", methods=["GET"])
def home():
    return jsonify({"status": "OCR Server Running ✅"})

@app.route("/ocr/engine-chassis", methods=["POST"])
def ocr_engine_chassis():
    if "image" not in request.files:
        return jsonify({"success": False, "message": "image field missing"}), 400

    file = request.files["image"]
    if file.filename == "":
        return jsonify({"success": False, "message": "empty filename"}), 400

    # Read image bytes into OpenCV
    file_bytes = np.frombuffer(file.read(), np.uint8)
    img = cv2.imdecode(file_bytes, cv2.IMREAD_COLOR)

    if img is None:
        return jsonify({"success": False, "message": "invalid image"}), 400

    # TTA: Test-Time Augmentation
    tta_imgs = get_tta_variations(img)
    all_results = []

    with torch.no_grad():
        for tta_img in tta_imgs:
            x = preprocess_image(tta_img).to(DEVICE)
            preds = model(x)
            decoded = ctc_decode(preds, blank=0)
            text = indices_to_text(decoded)
            text = clean_engine_chassis(text)
            if text:
                all_results.append(text)

    # Pick the most common result (Majority Voting)
    if not all_results:
        final_text = ""
    else:
        from collections import Counter
        counts = Counter(all_results)
        final_text = counts.most_common(1)[0][0]

    # --- MLOps: Save to pending for future auto-train ---
    sample_id = str(uuid.uuid4())
    pending_path = os.path.join(PENDING_DIR, f"{sample_id}.jpg")
    cv2.imwrite(pending_path, img)

    return jsonify({
        "success": True,
        "text": final_text,
        "sample_id": sample_id,
        "tta_candidates": all_results
    })

@app.route("/ocr/feedback", methods=["POST"])
def ocr_feedback():
    check_model_reload()  # Opportunistic reload check
    
    data = request.json
    sample_id = data.get("sample_id")
    corrected_text = data.get("corrected_text")

    if not sample_id or not corrected_text:
        return jsonify({"success": False, "message": "Missing sample_id or corrected_text"}), 400

    # Clean and validate text
    corrected_text = clean_engine_chassis(corrected_text)
    if len(corrected_text) < 8 or len(corrected_text) > 20:
        return jsonify({"success": False, "message": "Invalid text length (8-20 chars only)"}), 400

    pending_path = os.path.join(PENDING_DIR, f"{sample_id}.jpg")
    if not os.path.exists(pending_path):
        return jsonify({"success": False, "message": "Sample ID not found in pending"}), 404

    # Move to real_data
    final_filename = f"{sample_id}.jpg"
    final_path = os.path.join(REAL_IMAGES_DIR, final_filename)
    shutil.move(pending_path, final_path)

    # Append to labels.csv
    file_exists = os.path.isfile(REAL_LABELS_CSV)
    with open(REAL_LABELS_CSV, "a", newline="", encoding="utf-8") as f:
        writer = csv.writer(f)
        if not file_exists:
            writer.writerow(["filename", "text"])
        writer.writerow([final_filename, corrected_text])

    return jsonify({
        "success": True, 
        "message": f"Feedback stored for {sample_id}. Model will improve in next auto-train."
    })

@app.before_request
def before_request():
    check_model_reload()

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5001, debug=False) # Debug False for cleaner logs
