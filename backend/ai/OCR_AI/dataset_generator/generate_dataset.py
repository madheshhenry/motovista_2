import os, random, csv
import numpy as np
import cv2
from PIL import Image, ImageDraw, ImageFont

OUT_DIR = "synthetic_ocr_dataset"
TOTAL = 100000  # Google Lens level requires massive data
IMG_W, IMG_H = 520, 130

CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"

FONTS_DIR = "fonts"

def list_fonts():
    fonts = []
    for f in os.listdir(FONTS_DIR):
        if f.lower().endswith(".ttf"):
            fonts.append(os.path.join(FONTS_DIR, f))
    if not fonts:
        raise Exception("No fonts found! Put .ttf files inside fonts/ folder")
    return fonts

FONTS = list_fonts()

def mkdirs():
    for split in ["train", "val", "test"]:
        os.makedirs(os.path.join(OUT_DIR, "images", split), exist_ok=True)

def random_engine_chassis():
    # mostly realistic patterns + random patterns mix
    if random.random() < 0.65:
        a = "".join(random.choice("ABCDEFGHIJKLMNOPQRSTUVWXYZ") for _ in range(2))
        b = "".join(random.choice("0123456789") for _ in range(2))
        c = "".join(random.choice("ABCDEFGHIJKLMNOPQRSTUVWXYZ") for _ in range(2))
        d = "".join(random.choice("0123456789") for _ in range(random.randint(4, 9)))
        return a + b + c + d
    else:
        length = random.randint(10, 17)
        return "".join(random.choice(CHARS) for _ in range(length))

def metal_background():
    # Base metal color
    base_color = random.randint(140, 200)
    base = np.full((IMG_H, IMG_W, 3), base_color, dtype=np.uint8)

    # 1. Metal Grain/Noise
    noise = np.random.randint(0, 40, (IMG_H, IMG_W, 1), dtype=np.uint8)
    base = cv2.add(base, np.repeat(noise, 3, axis=2))

    # 2. Brushed Metal Effect (Horizontal strokes)
    k_width = random.choice([15, 25, 35])
    base = cv2.blur(base, (k_width, 1))

    # 3. Non-uniform Lighting (Gradients)
    if random.random() < 0.8:
        gradient = np.zeros((IMG_H, IMG_W), dtype=np.float32)
        if random.random() < 0.5: # Linear
            for i in range(IMG_W):
                gradient[:, i] = i / IMG_W
        else: # Radial/Spotlight
            cx, cy = random.randint(0, IMG_W), random.randint(0, IMG_H)
            curr_x, curr_y = np.meshgrid(np.arange(IMG_W), np.arange(IMG_H))
            dist = np.sqrt((curr_x - cx)**2 + (curr_y - cy)**2)
            gradient = 1.0 - (dist / np.max(dist))
        
        # Adjust gradient intensity
        alpha = random.uniform(0.1, 0.4)
        base = base.astype(np.float32)
        for c in range(3):
            base[:, :, c] = base[:, :, c] * (1.0 - alpha + alpha * gradient)
        base = np.clip(base, 0, 255).astype(np.uint8)

    return base

def add_scratches(img):
    scratch_count = random.randint(5, 15)
    overlay = img.copy()
    for _ in range(scratch_count):
        x1 = random.randint(0, IMG_W-1)
        y1 = random.randint(0, IMG_H-1)
        x2 = x1 + random.randint(-150, 150)
        y2 = y1 + random.randint(-20, 20)
        thickness = random.randint(1, 2)
        bright = random.randint(180, 255)
        cv2.line(overlay, (x1, y1), (x2, y2), (bright, bright, bright), thickness)
    
    # Smudges/Stains
    if random.random() < 0.3:
        for _ in range(random.randint(1, 4)):
            cx, cy = random.randint(0, IMG_W), random.randint(0, IMG_H)
            axes = (random.randint(10, 40), random.randint(5, 20))
            angle = random.randint(0, 180)
            cv2.ellipse(overlay, (cx, cy), axes, angle, 0, 360, (50, 50, 50), -1)
            overlay = cv2.GaussianBlur(overlay, (21, 21), 0)

    return cv2.addWeighted(img, 0.7, overlay, 0.3, 0)

def salt_and_pepper_noise(img, prob=0.01):
    output = img.copy()
    thres = 1 - prob
    for i in range(img.shape[0]):
        for j in range(img.shape[1]):
            rdn = random.random()
            if rdn < prob:
                output[i][j] = 0
            elif rdn > thres:
                output[i][j] = 255
    return output

def emboss_text(draw, x, y, text, font):
    # engraved look (shadow/highlight)
    draw.text((x+2, y+2), text, font=font, fill=(220, 220, 220))  # highlight
    draw.text((x-2, y-2), text, font=font, fill=(60, 60, 60))     # shadow
    draw.text((x, y), text, font=font, fill=(30, 30, 30))         # core

def apply_distortions(img):
    # 1. Perspective + Shear
    if random.random() < 0.85:
        pts1 = np.float32([[0,0],[IMG_W,0],[0,IMG_H],[IMG_W,IMG_H]])
        shift = random.randint(15, 25)
        pts2 = np.float32([
            [random.randint(0,shift), random.randint(0,shift)],
            [IMG_W-random.randint(0,shift), random.randint(0,shift)],
            [random.randint(0,shift), IMG_H-random.randint(0,shift)],
            [IMG_W-random.randint(0,shift), IMG_H-random.randint(0,shift)]
        ])
        P = cv2.getPerspectiveTransform(pts1, pts2)
        img = cv2.warpPerspective(img, P, (IMG_W, IMG_H), borderMode=cv2.BORDER_REFLECT)

    # 2. Shear (Skew)
    if random.random() < 0.4:
        shear_factor = random.uniform(-0.15, 0.15)
        M = np.float32([[1, shear_factor, 0], [0, 1, 0]])
        img = cv2.warpAffine(img, M, (IMG_W, IMG_H), borderMode=cv2.BORDER_REFLECT)

    # 3. Rotate
    angle = random.uniform(-10, 10)
    M = cv2.getRotationMatrix2D((IMG_W/2, IMG_H/2), angle, 1.0)
    img = cv2.warpAffine(img, M, (IMG_W, IMG_H), borderMode=cv2.BORDER_REFLECT)

    # 4. Occlusion (Simulate dirt/scratches covering letters)
    if random.random() < 0.25:
        for _ in range(random.randint(1, 3)):
            ox = random.randint(0, IMG_W-20)
            oy = random.randint(0, IMG_H-20)
            ow = random.randint(5, 30)
            oh = random.randint(5, 30)
            color = random.randint(0, 80)
            cv2.rectangle(img, (ox, oy), (ox+ow, oy+oh), (color, color, color), -1)

    # 5. Blur & Noise
    if random.random() < 0.5:
        k = random.choice([3, 5, 7])
        img = cv2.GaussianBlur(img, (k, k), 0)

    # 6. Motion Blur (Vertical or Horizontal)
    if random.random() < 0.2:
        size = random.randint(3, 9)
        kernel = np.zeros((size, size))
        if random.random() < 0.5:
            kernel[int((size-1)/2), :] = np.ones(size)
        else:
            kernel[:, int((size-1)/2)] = np.ones(size)
        kernel /= size
        img = cv2.filter2D(img, -1, kernel)

    # 7. Brightness/Contrast
    alpha = random.uniform(0.7, 1.4)
    beta = random.randint(-40, 40)
    img = cv2.convertScaleAbs(img, alpha=alpha, beta=beta)

    return img

def choose_split(i):
    r = i / TOTAL
    if r < 0.80:
        return "train"
    elif r < 0.90:
        return "val"
    else:
        return "test"

def generate_one(text):
    bg = metal_background()
    bg = add_scratches(bg)

    img_pil = Image.fromarray(bg)
    draw = ImageDraw.Draw(img_pil)

    font_path = random.choice(FONTS)
    font_size = random.randint(50, 75)
    font = ImageFont.truetype(font_path, font_size)

    # center text roughly
    bbox = draw.textbbox((0,0), text, font=font)
    w = bbox[2] - bbox[0]
    h = bbox[3] - bbox[1]
    
    x = max(5, (IMG_W - w)//2 + random.randint(-20, 20))
    y = max(5, (IMG_H - h)//2 + random.randint(-15, 15))

    emboss_text(draw, x, y, text, font)

    img = np.array(img_pil)
    img = apply_distortions(img)

    # Final pixel noise
    if random.random() < 0.3:
        img = salt_and_pepper_noise(img, prob=0.005)

    return img

def main():
    mkdirs()
    csv_path = os.path.join(OUT_DIR, "labels.csv")

    with open(csv_path, "w", newline="", encoding="utf-8") as f:
        writer = csv.writer(f)
        writer.writerow(["filename", "text", "split"])

        for i in range(TOTAL):
            text = random_engine_chassis()
            split = choose_split(i)

            filename = f"img_{i:06d}.png"
            out_path = os.path.join(OUT_DIR, "images", split, filename)

            img = generate_one(text)
            cv2.imwrite(out_path, img)

            writer.writerow([filename, text, split])

            if i % 500 == 0:
                print("Generated:", i)

    print("\n✅ DONE. Dataset saved at:", OUT_DIR)

if __name__ == "__main__":
    main()
