import json
import os
from difflib import get_close_matches

# Configuration
DATA_FILE = "d:/bike_ai/data/bike_ai_text.json"
IMAGE_DIR = "d:/bike_ai/images"
BASE_URL = "http://192.168.0.104:5000/images/"

def map_images():
    # 1. Load Data
    with open(DATA_FILE, "r") as f:
        bikes = json.load(f)
        
    # 2. Get Local Images
    images = os.listdir(IMAGE_DIR)
    # Simple normalization map: "pulser n160.png" -> "pulsar n160"
    
    print(f"Found {len(images)} images and {len(bikes)} bikes.")
    
    mapping_count = 0
    
    for bike in bikes:
        # Construct target name (e.g., "TVS Apache RTR 160 4V")
        target_name = bike["name"].lower()
        
        # Try finding a match in images
        # We look for image filenames that are contained in the bike name or vice versa
        # Logic: Does "Apache RTR 160 4V.png" exist?
        
        best_match = None
        
        # Strategy A: Direct Match (ignoring case/extension)
        for img in images:
            img_name = img.lower().rsplit(".", 1)[0] # remove extension
            
            # Check 1: Exact Name Match
            # If bike name is "TVS Raider 125" and image is "TVS Raider 125.png"
            if img_name == target_name:
                best_match = img
                break
                
            # Check 2: Partial Match Logic
            # bike="TVS Apache RTR 160 4V", img="TVS Apache RTR 160 4V" -> Match
            # bike="Hero Splendor Plus XTEC", img="Hero Splendor Plus" -> Acceptable?
            
        if not best_match:
             # Strategy B: Fuzzy Match
             # Find filenames closest to bike name
             matches = get_close_matches(target_name, [i.lower() for i in images], n=1, cutoff=0.6)
             if matches:
                  # Retrieve original case filename
                  for img in images:
                       if img.lower() == matches[0]:
                            best_match = img
                            break

        if best_match:
            new_url = BASE_URL + best_match
            bike["image"] = new_url
            mapping_count += 1
            print(f"Mapped: {bike['name']} -> {best_match}")
        else:
            print(f"WARNING: No image found for {bike['name']}")

    # 3. Save Data
    with open(DATA_FILE, "w") as f:
        json.dump(bikes, f, indent=2)
        
    print(f"\nSuccessfully mapped {mapping_count}/{len(bikes)} bikes.")

if __name__ == "__main__":
    map_images()
