import json
import pickle
from sentence_transformers import SentenceTransformer

# Load bike text
with open("data/bike_ai_text.json", "r", encoding="utf-8") as f:
    bike_data = json.load(f)

texts = [item["text"] for item in bike_data]
bike_ids = [item["bike_id"] for item in bike_data]

# Load embedding model (NO LLM)
model = SentenceTransformer("all-MiniLM-L6-v2")

# Generate embeddings
embeddings = model.encode(texts, show_progress_bar=True)

# Save embeddings to file
with open("embeddings/bike_embeddings.pkl", "wb") as f:
    pickle.dump(
        {
            "bike_ids": bike_ids,
            "embeddings": embeddings
        },
        f
    )

print("✅ Bike embeddings saved successfully!")
