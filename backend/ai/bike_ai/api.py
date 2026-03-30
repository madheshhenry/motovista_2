from flask import Flask, request, jsonify
import pickle
import json
import faiss
import numpy as np
from sentence_transformers import SentenceTransformer
from chat_engine import ai_engine

# --------------------------------
# CREATE APP
# --------------------------------
app = Flask(__name__)

# --------------------------------
# LOAD AI ENGINE (Data Layer)
# --------------------------------
print("⏳ Loading AI Models...")

# Load Embeddings
with open("embeddings/bike_embeddings.pkl", "rb") as f:
    data = pickle.load(f)

embeddings = np.array(data["embeddings"]).astype("float32")

# Load Text Data (Metadata + Descriptions)
with open("data/bike_ai_text.json", "r", encoding="utf-8") as f:
    bike_data = json.load(f)

# FAISS Vector Index
dimension = embeddings.shape[1]
index = faiss.IndexFlatL2(dimension)
index.add(embeddings)

# Transformer Model
model = SentenceTransformer("all-MiniLM-L6-v2")

print("✅ AI Engine Ready & Loaded")

# --------------------------------
# REASONING LOGIC (Score & Rank)
# --------------------------------
import re

def extract_price(text):
    # Pattern to find "approx 1.47 lakh" or "about 1.5 lakh"
    # Returns price in Rupees (e.g., 1.47 -> 147000)
    match = re.search(r"(\d+\.?\d*)\s?lakh", text, re.IGNORECASE)
    if match:
        try:
            lakhs = float(match.group(1))
            return int(lakhs * 100000)
        except:
            return 0
    return 0

def rank_bikes(candidates, constraints):
    """
    Ranks bikes using Weighted Scoring Logic on Structured Data.
    """
    ranked = []

    # 0. DIRECT LOOKUP (Insta-Search)
    if constraints.get("specific_bike"):
         target = constraints["specific_bike"].lower()
         # Search in all known data, not just candidates
         # bike_data is global
         for bike in bike_data:
              # permissive match: name contains target
              if target in bike["name"].lower():
                   return [bike]
    
    # 1. Parse Budget (Hard Constraint)
    budget_limit = 9999999
    budget_str = constraints.get("budget", "").lower()
    
    if "under 1" in budget_str: budget_limit = 100000
    elif "1 - 1.5" in budget_str: budget_limit = 150000
    elif "1.5 - 2" in budget_str: budget_limit = 200000
    elif "flexible" in budget_str: budget_limit = 9999999
    
    # 2. Scoring Weights
    priority = constraints.get("priority", "").lower()
    usage = constraints.get("usage", "").lower()
    style = constraints.get("style", "").lower()
    brand_pref = constraints.get("brand", "Any").lower()

    for bike in candidates:
        score = 0
        
        # --- HARD FILTERS ---
        # Price Filter (Softened for Brand Loyalty)
        # If user explicitly wants this brand, allow a 20% budget stretch
        limit = budget_limit
        if brand_pref != "any" and brand_pref in bike["brand"].lower():
             limit = budget_limit * 1.2
             
        if bike["price"] > limit:
            continue
            
        # --- SCORING LOGIC ---
        
        # A. Brand Match
        if brand_pref != "any" and brand_pref in bike["brand"].lower():
             score += 15 # Huge boost for brand preference
             
        # B. Priority Matches
        if "mileage" in priority:
            # Higher mileage = Higher score
            if bike["mileage"] > 60: score += 10 # Hero Splendor, Honda SP 125
            elif bike["mileage"] > 50: score += 7 # Honda Unicorn, Suzuki Access
            elif bike["mileage"] > 40: score += 4 # Yamaha MT 15, R15
            
        if "power" in priority or "performance" in priority:
            # Higher CC = Higher score (Adjusted for <200cc market)
            if bike["cc"] > 180: score += 10 # Apache 200, Pulsar NS200
            elif bike["cc"] > 150: score += 7 # R15, MT15, Unicorn
            elif bike["cc"] > 120: score += 5 # SP 125, Access 125
            
        if "comfort" in priority:
            if bike["type"] in ["Commuter", "Scooter"]: score += 8 # Comfort kings
            if bike["brand"] == "Honda": score += 3 # Honda known for comfort
            
        if "not sure" in priority or "balanced" in priority:
             # balanced approach, give points for everything decently spec'd
             if bike["mileage"] > 45: score += 3
             if bike["cc"] > 150: score += 3
             score += 5 # General points
            
        # B. Usage Matches
        if "city" in usage:
            if bike["type"] in ["Commuter", "Scooter", "Sport"]: score += 5 
            if bike.get("weight", 200) < 145: score += 5 # Lightweight favored
            
        if "highway" in usage or "touring" in usage:
            if bike["type"] in ["Sport", "Cruiser"] and bike["cc"] > 150: score += 8 # Stable
            if bike["cc"] > 300: score += 5
            
        if "adventure" in usage or "off-road" in usage:
            # Score boosters for Adventure bikes
            if bike["type"] == "Adventure": score += 15 # Huge boost
            if "ground clearance" in bike.get("text", "").lower(): score += 5 # If text mentions GC
            
        # C. Style Matches
        if "sport" in style and bike["type"] == "Sport": score += 5
        if "relax" in style and bike["type"] in ["Cruiser", "Commuter", "Scooter"]: score += 5
        
        ranked.append({"bike": bike, "score": score})
        
    # Sort by score desc
    ranked.sort(key=lambda x: x["score"], reverse=True)
    return [r["bike"] for r in ranked[:3]]

def generate_reasoning(bike, constraints):
    """
    Generates a PERSUASIVE, DATA-DRIVEN explanation.
    """
    reasons = []
    

    
    priority = constraints.get("priority", "").lower()
    usage = constraints.get("usage", "").lower()
    
    # 1. Mileage Logic
    if "mileage" in priority or "balanced" in priority:
        kmpl = bike.get("mileage", 0)
        if kmpl > 60:
            reasons.append(f"offers unmatched fuel efficiency of {kmpl} kmpl")
        elif kmpl > 45:
            reasons.append(f"delivers a solid {kmpl} kmpl mileage")
            
    # 2. Power Logic
    if "power" in priority or "performance" in priority:
        cc = bike.get("cc", 0)
        if cc > 180:
             reasons.append(f"packs a powerful {cc}cc engine for thrilling acceleration")
        elif cc > 150:
             reasons.append(f"features a punchy {cc}cc engine perfect for city overtakes")
             
    # 3. Usage Logic
    if "city" in usage:
        if bike.get("weight", 200) < 145:
            reasons.append("is extremely lightweight and easy to handle in traffic")
        else:
            reasons.append("is tuned for comfortable daily commuting")
            
    if "highway" in usage:
        reasons.append("offers great high-speed stability")
        
    if "adventure" in usage:
        reasons.append("is built to handle rough terrains and off-road trails")

    # 4. Brand Logic (if selected)
    brand_pref = constraints.get("brand", "Any")
    if brand_pref != "Any" and brand_pref.lower() in bike["brand"].lower():
        # Start with this strong point
        reasons.insert(0, f"is the best choice from {brand_pref}")

    # Fallback
    if not reasons:
        reasons.append("is a well-balanced machine matching your needs")
        
    # Formatting
    # Join nicely: "It is the best choice from Honda, offers 65kmpl, and is lightweight."
    return "Recommended because it " + ", ".join(reasons) + "."

# --------------------------------
# ROUTES
# --------------------------------
@app.route("/", methods=["GET"])
def home():
    return "Bike Intelligence AI Status: ONLINE"

# Serve Local Images
@app.route('/images/<path:filename>')
def serve_image(filename):
    from flask import send_from_directory
    return send_from_directory('images', filename)

@app.route("/chat", methods=["POST"])
def chat():
    """
    Main Chat Loop:
    1. Receive user input or valid option.
    2. Pass to AI State Machine.
    3. If 'question', return next question.
    4. If 'result', perform RAG Search & Reasoning.
    """
    req = request.get_json()
    user_input = req.get("message", "")
    
    # 1. Domain Guard (Simple Keyword Check for safety)
    if "car" in user_input.lower() or "truck" in user_input.lower():
         return jsonify({
             "message": "I can help only with bike-related decisions.",
             "options": ["Restart"]
         })

    # 2. Process via State Machine
    # The new Chat Engine handles state transitions (IDLE -> ACTIVE) internally.
    if user_input.upper() == "START":
        ai_engine.reset_context()
        
    response = ai_engine.process_input(user_input)
    
    # Handle Comparison Response (Now returned by process_input)
    if response.get("type") == "comparison":
          # ------------------------------------
          # GENERATE COMPARISON TABLE (PRO FEATURE)
          # ------------------------------------
          bikes = response["data"]
          if not bikes:
              return jsonify({"message": "Nothing to compare yet.", "options": ["Start"]})
              
          # Simple Text Table
          table = "Here is the comparison:\n\n"
          for b in bikes:
              table += f"🏍️ *{b['name']}*\n"
              table += f"💰 Price: ₹{b.get('price', 'N/A')}\n"
              table += f"⛽ Mileage: {b.get('mileage', 'N/A')} kmpl\n"
              table += f"⚙️ Engine: {b.get('cc', 'N/A')} cc\n"
              table += "-------------------\n"
          
          return jsonify({
              "message": table,
              "options": ["Start Over"]
          })

    # 3. Handle Result Generation
    if response.get("complete"):
        query_data = response["query"]
        
        # A. Semantic Search (Broad Retrieval)
        # Construct a rich search query
        search_text = f"Two wheeler for {query_data['usage']} use. {query_data['priority']} priority. {query_data['style']} style."
        vector = model.encode([search_text]).astype("float32")
        distances, indices = index.search(vector, 10) # Get top 10 candidates
        
        candidates = [bike_data[idx] for idx in indices[0]]
        
        # B. Reasoning & Ranking (Strict Logic)
        final_selection = rank_bikes(candidates, query_data)
        
        if not final_selection:
            return jsonify({
                "message": "I analyzed the options, but couldn't find a perfect match within that strict budget. Would you like to see slightly higher priced options?",
                "options": ["Yes, show slightly higher", "Restart"]
            })

        # C. Response Formatting
        rec_cards = []
        ai_engine.state["last_results"] = [] # Clear old history
        
        for match in final_selection:
             # Add to Memory for Comparison
             ai_engine.state["last_results"].append(match)
             
             rec_cards.append({
                 "id": match.get("bike_id", 0),
                 "name": match["text"].split(" is ")[0], # Crude name extraction
                 "description": generate_reasoning(match, query_data), # DYNAMIC REASONING
                 "image": f"http://192.168.0.103:5000/images/{match.get('image', 'placeholder_bike.png').split('/')[-1]}", 
                 "price": match.get("price", "Check Showroom")
             })
             
        return jsonify({
            "type": "recommendation",
            "message": "Based on your focus on " + query_data['priority'] + ", here are the best matches:",
            "data": rec_cards,
            "options": ["Compare Details", "Start Over"]
        })

    # 4. Return Normal Question
    return jsonify({
        "message": response["message"],
        "options": response.get("options", [])
    })

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)
