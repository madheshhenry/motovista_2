import os
import json
import difflib

IMAGE_DIR = r"d:\bike_ai\images"
JSON_PATH = r"d:\bike_ai\data\bike_ai_text.json"
BASE_URL = "http://192.168.0.102:5000/images/"

def sanitize_filename(name):
    """
    Replaces spaces with underscores and removes special chars to make filename URL-safe.
    """
    name = name.lower().replace(" ", "_")
    name = name.replace("(", "").replace(")", "").replace("'", "")
    return name

def rename_images():
    print("Renaming images...")
    files = os.listdir(IMAGE_DIR)
    renamed_map = {} # old -> new (basename)
    
    for filename in files:
        if not filename.lower().endswith(('.png', '.jpg', '.jpeg', '.webp')):
            continue
            
        old_path = os.path.join(IMAGE_DIR, filename)
        
        # Create safe name
        safe_name = sanitize_filename(filename)
        
        # Preserve original extension case or force lowercase? Force lowercase extension
        base, ext = os.path.splitext(safe_name)
        # But wait, sanitize includes extension. 
        # sanitize_filename("TVS Raider.png") -> "tvs_raider.png"
        
        new_path = os.path.join(IMAGE_DIR, safe_name)
        
        if old_path != new_path:
            try:
                os.rename(old_path, new_path)
                print(f"Renamed: '{filename}' -> '{safe_name}'")
            except OSError as e:
                print(f"Error renaming {filename}: {e}")
        
        renamed_map[filename] = safe_name
        
    return [f for f in os.listdir(IMAGE_DIR) if f.lower().endswith(('.png', '.jpg'))]

def map_images(image_files):
    print("\nMapping images to bike data...")
    try:
        with open(JSON_PATH, "r") as f:
            bikes = json.load(f)
    except Exception as e:
        print(f"Error reading JSON: {e}")
        return

    count = 0
    # Create a map for faster lookup if possible, but fuzzy matching is needed
    # We will match bike['name'] against available image filenames
    
    # Pre-process image names for matching: remove extension
    img_bases = {img: os.path.splitext(img)[0].replace("_", " ") for img in image_files}
    # value is like "tvs raider 125" (normalized for matching)
    
    for bike in bikes:
        bike_name = bike["name"].lower().replace("  ", " ").strip()
        
        # Find best match among image files
        # We compare bike_name with the "normalized" image names
        best_match = None
        highest_ratio = 0.0
        
        for img_file, img_normalized_name in img_bases.items():
            # calculate similarity
            # 1. Direct containment
            if bike_name in img_normalized_name or img_normalized_name in bike_name:
                 ratio = 1.0
                 # usage of difflib to break ties or confirm
            
            ratio = difflib.SequenceMatcher(None, bike_name, img_normalized_name).ratio()
            
            if ratio > highest_ratio:
                highest_ratio = ratio
                best_match = img_file
        
        # Threshold
        if highest_ratio > 0.6: # Pretty loose, but names are similar
            # Construct URL with the ACTUAL filename (safe_name)
            new_url = BASE_URL + best_match
            bike["image"] = new_url
            print(f"Mapped: {bike['name']} -> {best_match} (Score: {highest_ratio:.2f})")
            count += 1
        else:
            print(f"WARNING: No image found for {bike['name']}")
            bike["image"] = "" # Clear invalid images? Or keep old? Better clear to avoid broken links.

    print(f"\nTotal Mapped: {count}/{len(bikes)}")
    
    with open(JSON_PATH, "w") as f:
        json.dump(bikes, f, indent=4)
    print("JSON updated.")

if __name__ == "__main__":
    current_images = rename_images()
    map_images(current_images)
