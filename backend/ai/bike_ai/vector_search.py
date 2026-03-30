import pickle
import faiss
import numpy as np

# Load embeddings
with open("embeddings/bike_embeddings.pkl", "rb") as f:
    data = pickle.load(f)

bike_ids = data["bike_ids"]
embeddings = data["embeddings"]

# Convert to float32 (FAISS requirement)
embeddings = np.array(embeddings).astype("float32")

# Create FAISS index
dimension = embeddings.shape[1]
index = faiss.IndexFlatL2(dimension)
index.add(embeddings)

print("✅ FAISS index created")
print("Total bikes indexed:", index.ntotal)


from sentence_transformers import SentenceTransformer

# Load embedding model
model = SentenceTransformer("all-MiniLM-L6-v2")

# Sample user query
query = "Best bike for city use with high mileage"

# Convert query to vector
query_vector = model.encode([query]).astype("float32")

# Search top 5 bikes
k = 5
distances, indices = index.search(query_vector, k)

print("\nTop matching bikes (indexes):", indices[0])
print("Distances:", distances[0])


import json

# Load original bike data
with open("data/bike_ai_text.json", "r", encoding="utf-8") as f:
    bike_data = json.load(f)

print("\n🔍 Top Recommended Bikes:\n")

for idx in indices[0]:
    bike = bike_data[idx]
    print(f"- Bike ID: {bike['bike_id']}")
    print(f"  Description: {bike['text']}\n")
