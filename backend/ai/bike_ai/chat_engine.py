import random
import json
import re

class ResponseVariations:
    def __init__(self):
        self.greetings = [
            "Hi 👋 I am your Bike Decision Intelligence.",
            "Hello! Ready to find your dream bike?",
            "Hey there! Let's get you on the road.",
            "Welcome to MotoVista AI! 🏍️",
            "Greetings! I'm here to help you choose the best ride.",
            "Hi! I'm your personal bike assistant.",
            "Hello friend! Looking for a new two-wheeler?",
            "Namaste! 🙏 Let's find a perfect bike for you.",
            "Hey! Need help picking a bike?",
            "Welcome! I can compare specs and suggest bikes instantly.",
            "Hi there! I'm trained to know everything about Indian bikes.",
            "Hello! Tell me what you need, and I'll find it.",
            "Hey! Let's zoom into the bike world.",
            "Greetings! Your perfect ride is just a few questions away.",
            "Hi! I'm the smartest bike bot in town.",
            "Hello! Speed, comfort, or mileage? I know them all.",
            "Hey! Let's start your biking journey.",
            "Welcome back! Ready to explore?",
            "Hi! I can help you decide between brands.",
            "Hello! Let's find a bike that fits your budget and style."
        ]
        
        self.budget_questions = [
            "What is your budget range?",
            "How much are you planning to spend?",
            "What's the price bracket you are looking at?",
            "Do you have a budget limit in mind?",
            "Let's talk money first. What's your range?",
            "How much do you wish to invest in your new ride?",
            "To find the best options, I need your budget.",
            "What is your maximum budget for this purchase?",
            "Are you looking for something budget-friendly or premium?",
            "Help me filter by price. What's your limit?",
            "What is the budget you have set aside?",
            "Can you tell me your price range?",
            "Let's narrow it down. What's your budget?",
            "How much is your wallet ready for?",
            "What is your financial plan for the bike?"
        ]
        
        self.brand_questions = [
            "Do you have a preferred brand?",
            "Any specific brand you love?",
            "Are you loyal to any particular company (e.g., TVS, Yamaha)?",
            "Which badge do you want on your tank?",
            "Do you have a favorite bike manufacturer?",
            "Is there a brand you trust more?",
            "Any specific brand in mind?",
            "Do you prefer Honda, Hero, or something else?",
            "Which brand rules your heart?",
            "Are you looking for a specific make?",
            "Do you want to stick to a certain brand?",
            "Should I look for a specific company's bikes?",
            "Any brand bias I should know about?",
            "Who is your favorite bike maker?",
            "Do you have a dream brand?"
        ]
        
        self.usage_questions = [
            "Where will you primarily ride?",
            "What will be the main use of this bike?",
            "Are you riding for city commutes or highway tours?",
            "How do you plan to use this machine?",
            "Is it for office, college, or adventure?",
            "Where will this bike spend most of its time?",
            "Do you need it for heavy traffic or open roads?",
            "What's your daily riding pattern?",
            "Will you be riding off-road or on tarmac?",
            "Is this for daily commuting?",
            "Do you plan long trips or short city runs?",
            "What kind of roads will you conquer?",
            "Is it mostly for family use or solo rides?",
            "Where are you taking this beast?"
        ]
        
        self.priority_questions = [
            "What is slightly more important to you?",
            "What matters most: Mileage, Power, or Comfort?",
            "What is your top priority?",
            "If you had to pick one: Speed or Efficiency?",
            "What do you value most in a bike?",
            "Are you chasing performance or fuel economy?",
            "Do you prefer a comfortable ride or a fast one?",
            "What's the most critical factor for you?",
            "Is mileage the king for you?",
            "Do you want raw power or smooth comfort?",
            "What defines a perfect bike for you?",
            "What feature is a deal-breaker?",
            "Should I focus on engine power or seat comfort?",
            "What is your number one requirement?"
        ]

    def get_greeting(self): return random.choice(self.greetings)
    def ask_budget(self): return random.choice(self.budget_questions)
    def ask_brand(self): return random.choice(self.brand_questions)
    def ask_usage(self): return random.choice(self.usage_questions)
    def ask_priority(self): return random.choice(self.priority_questions)


class BikeAI:
    def __init__(self):
        self.state_machine = "IDLE" # IDLE, ACTIVE
        self.context = {} 
        self.last_results = []
        self.variations = ResponseVariations() # Initialize Manager
        # Load Bike Names for Direct Recognition
        self.bike_names = []
        try:
             with open("d:/bike_ai/data/bike_ai_text.json", "r") as f:
                  data = json.load(f)
                  self.bike_names = [b["name"] for b in data]
        except:
             pass
        
        self.reset_context()

    def reset_context(self):
        self.state_machine = "IDLE"
        self.context = {
            "budget": None,
            "brand": None,
            "usage": None,
            "priority": None,
            "style": "Standard" 
        }
        self.last_results = []

    # ------------------------------------
    # 1. OMNI-RECOGNITION ENGINE (The Ear)
    # ------------------------------------
    def extract_entities(self, text):
        text = text.lower()
        extracted = {}
        
        # A. Budget Extraction (Regex + Keyword)
        if "under 1" in text or "cheap" in text or "budget friendly" in text: extracted["budget"] = "Under 1 Lakh"
        elif "1.5" in text and "2" in text: extracted["budget"] = "1.5 - 2 Lakh"
        elif "above 2" in text or "premium" in text: extracted["budget"] = "Above 2 Lakh"
        elif "flexible" in text: extracted["budget"] = "Flexible"
        elif "lakh" in text:
             match = re.search(r"(\d+\.?\d*)", text)
             if match:
                 val = float(match.group(1))
                 if val < 1.05: extracted["budget"] = "Under 1 Lakh"
                 elif val <= 1.5: extracted["budget"] = "1 - 1.5 Lakh"
                 elif val <= 2.0: extracted["budget"] = "1.5 - 2 Lakh"
                 else: extracted["budget"] = "Above 2 Lakh"

        # B. Brand Extraction
        brands = ["Royal Enfield", "Yamaha", "TVS", "Bajaj", "Hero", "Suzuki", "Honda", "KTM"]
        for b in brands:
            if b.lower() in text:
                extracted["brand"] = b
                break
        
        # C. Usage Extraction (Indian Context)
        if any(w in text for w in ["off-road", "dirt", "hills", "adventure", "rough", "village", "himalaya"]): 
            extracted["usage"] = "Adventure"
        elif any(w in text for w in ["highway", "long", "touring", "trip", "ladakh"]): 
            extracted["usage"] = "Highway / Long Rides"
        elif any(w in text for w in ["city", "office", "daily", "traffic", "college", "school", "commute"]): 
            extracted["usage"] = "City Commute"
            
        # D. Priority/Style Extraction
        if any(w in text for w in ["mileage", "efficiency", "average", "economic", "kitna deti hai"]): 
            extracted["priority"] = "Mileage & Efficiency"
        elif any(w in text for w in ["power", "fast", "speed", "performance", "pickup", "beast", "race"]): 
            extracted["priority"] = "Power & Performance"
        elif any(w in text for w in ["comfort", "relax", "family", "smooth", "ladies", "pillion"]): 
            extracted["priority"] = "Comfort & Stability"
            
        return extracted

    # ------------------------------------
    # 2. HYBRID STATE MANAGER (The Brain)
    # ------------------------------------
    def process_input(self, user_input):
        raw_text = user_input.strip()
        
        # GLOBAL CHECKS
        if raw_text.upper() == "START":
             self.reset_context()
             return self.get_welcome_message()
             
        if "compare" in raw_text.lower():
             return self.route_compare()
             
        # CHECK FOR DIRECT BIKE LOOKUP (PRO FEATURE)
        # e.g., "Tell me about Raider"
        for bike_name in self.bike_names:
             # Fuzzy check: if "raider" in input and "raider 125" is the bike name
             # We check if a significant part of the name is in the input
             simplified_name = bike_name.lower().replace("tvs ", "").replace("honda ", "").replace("yamaha ", "").replace("bajaj ", "")
             if simplified_name.split()[0] in raw_text.lower():
                  # Found a specific bike mention!
                  # Route to "Specific Result" logic (handled by API if we return complete=True with special query)
                  # Set context to this bike's attributes implicitly or just return a 'direct_search' result
                  return {
                      "type": "processing",
                      "message": f"Found it! Showing details for {bike_name}...",
                      "complete": True,
                      "query": {"specific_bike": bike_name} 
                  }

        # RUN EXTRACTOR
        slots = self.extract_entities(raw_text)
        
        # IDLE STATE LOGIC
        if self.state_machine == "IDLE":
            # If no meaningful slots found, check for greeting or confirmation
            if not slots:
                triggers = ["find", "buy", "suggest", "help", "need", "want", "looking", "search", "yes", "ok", "sure", "yeah", "yep"]
                if any(t in raw_text.lower() for t in triggers):
                    # Transition to ACTIVE, but we know nothing yet
                    self.state_machine = "ACTIVE"
                    return self.decide_next_step()
                else:
                    # Just a greeting or noise -> Welcome
                    return self.get_welcome_message()
            else:
                # User provided info immediately (e.g., "Bike for daily use") -> Auto-Start
                self.state_machine = "ACTIVE"
                self.merge_context(slots)
                # CHECK FAST TRACK
                # If valid slots are present, maybe we can skip if user said "Best X"
                if "best" in raw_text.lower() or "top" in raw_text.lower():
                     # Fill remaining slots with Defaults/Any to force result
                     self.fill_defaults()
                     
                return self.decide_next_step()
                
        # ACTIVE STATE LOGIC
        else:
            # We are already in a flow. Merge new info.
            if slots:
                self.merge_context(slots)
            elif "flexible" in raw_text.lower():
                 # Handle generic flexible response
                 if "any" in raw_text.lower():
                      if not self.context["brand"]: self.context["brand"] = "Any"
            
            return self.decide_next_step()

    def merge_context(self, new_slots):
        # Only overwrite if new value is present
        for k, v in new_slots.items():
            if v: self.context[k] = v
            
    def fill_defaults(self):
         # Used for "Fast Track" queries
         if not self.context["budget"]: self.context["budget"] = "Flexible"
         if not self.context["brand"]: self.context["brand"] = "Any"
         if not self.context["usage"]: self.context["usage"] = "City Commute"
         if not self.context["priority"]: self.context["priority"] = "Mileage & Efficiency"

    def decide_next_step(self):
        d = self.context
        
        # Step 1: Budget (Most Critical)
        if not d["budget"]:
            return self.ask_budget()
            
        # Step 2: Brand
        if not d["brand"]:
            return self.ask_brand()

        # Step 3: Usage
        if not d["usage"]:
             return self.ask_usage()
             
        # Step 4: Priority
        if not d["priority"]:
             return self.ask_priority()
        
        # Done
        return {
            "type": "processing", 
            "message": "Perfect! I have all the details. Finding your best match...",
            "complete": True,
            "query": d
        }

    # ------------------------------------
    # 3. HELPER METHODS
    # ------------------------------------
    def route_compare(self):
        if not self.last_results:
            return {
                "type": "message",
                "message": "I haven't found any bikes for you yet. Let's find some first!",
                "options": ["Find a New Bike"]
            }
        return {
            "type": "comparison",
            "data": self.last_results
        }
        
    def get_welcome_message(self):
        return {
            "type": "question",
            "message": self.variations.get_greeting(), # RANDOMIZED
            "options": ["Find a New Bike", "Help me Decide"]
        }

    def ask_budget(self):
        msg = self.variations.ask_budget() # RANDOMIZED
        # Contextual prompt override (optional, can stay simple or mix)
        if self.context.get("usage"):
             msg = f"For a {self.context['usage']} bike, " + msg.lower()
        return {
            "type": "question", "message": msg,
            "options": ["Under 1 Lakh", "1 - 1.5 Lakh", "1.5 - 2 Lakh", "Above 2 Lakh", "Flexible"]
        }
        
    def ask_brand(self):
         return {
            "type": "question", "message": self.variations.ask_brand(), # RANDOMIZED
            "options": ["TVS", "Honda", "Yamaha", "Bajaj", "Hero", "Suzuki", "Any"]
        }

    def ask_usage(self):
        return {
            "type": "question", "message": self.variations.ask_usage(), # RANDOMIZED
            "options": ["City Commute", "Highway / Long Rides", "Adventure / Off-Road"]
        }

    def ask_priority(self):
        return {
            "type": "question", "message": self.variations.ask_priority(), # RANDOMIZED
            "options": ["Mileage & Efficiency", "Power & Performance", "Comfort & Daily Use"]
        }
    
    # Compat wrapper for api.py
    @property
    def state(self):
        return {
            "last_results": self.last_results,
            "step": "WELCOME" if self.state_machine == "IDLE" else "PROCESSING"
        }

# Singleton instance
ai_engine = BikeAI()
