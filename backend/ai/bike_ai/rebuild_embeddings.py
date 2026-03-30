import json
import pickle
import numpy as np
from sentence_transformers import SentenceTransformer

def rebuild():
    print("⏳ Loading new bike data...")
    with open("data/bike_ai_text.json", "r", encoding="utf-8") as f:
        bike_data = json.load(f)
        
    print(f"✅ Loaded {len(bike_data)} bikes.")
    
    print("⏳ generating embeddings (this might take a moment)...")
    model = SentenceTransformer("all-MiniLM-L6-v2")
    
    texts = [bike["text"] for bike in bike_data]
    embeddings = model.encode(texts)
    
    print(f"✅ Generated {embeddings.shape} matrix.")
    
    # Save to pickle
    with open("embeddings/bike_embeddings.pkl", "wb") as f:
        pickle.dump({"embeddings": embeddings}, f)
        
    print("🎉 embeddings/bike_embeddings.pkl updated successfully!")

if __name__ == "__main__":
    rebuild()
