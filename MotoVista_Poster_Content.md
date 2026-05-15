# MotoVista — Poster Content Guide
## Based on ZenLife Reference Posters (5 Posters)

---

## POSTER 1 — Main Content Poster
*(Matches reference 1.png — Two-column layout, white background)*

---

### [TOP CENTER — TITLE]
**MotoVista — AI-Powered Automotive Dealership and Customer Retail Ecosystem**

---

### [LEFT COLUMN]

#### Concept:
*(Place MotoVista logo here — motorcycle icon in a dark circle)*

MotoVista is an Android application that digitizes the complete two-wheeler purchasing process for both customers and dealership staff. It is designed to work alongside the physical showroom — customers always visit the dealership in person first, and the app handles the entire process digitally from that point. Customers use an AI chatbot to find the right bike, while showroom admins use an OCR camera scanner to extract engine numbers directly from the physical vehicle. The app handles KYC verification, payment tracking, RTO registration, and invoice delivery — all from a smartphone.

---

#### Competitive Analysis:

**1. BikeWale**
**Pros:**
✅ Large database of two-wheeler listings
✅ Useful for customer lead generation and editorial reviews

**Cons:**
❌ No admin or inventory management tools
❌ No AI-based personalized guidance for buyers
❌ Does not connect customer discovery to dealer backend

---

**2. DealerSocket CRM**
**Pros:**
✅ Comprehensive enterprise-level dealership management
✅ Supports multi-franchise inventory tracking

**Cons:**
❌ Desktop-only — not designed for mobile showroom use
❌ Expensive, too complex for small and mid-sized dealerships
❌ No customer-facing discovery or AI tools

---

#### MotoVista Competitive Edge:

- 🏍️ **Conversational AI Chatbot:** Customers describe what they need in plain words — the AI recommends the best matching bike from the showroom inventory
- 📷 **Live OCR Scanner:** Admin points the camera at the bike's engine plate — the 12-digit code is extracted instantly without manual typing
- 🔐 **Dual-Role Single App:** One codebase, two completely different interfaces — JWT authentication routes each user to their correct dashboard
- 💳 **EMI Tracking with Confirmation:** Customers mark EMI as paid in-app; admin cross-checks and confirms — no notebook required
- 📄 **Invoice Delivery:** After physical delivery at showroom, the PDF invoice is automatically pushed to the customer's app

---

### [RIGHT COLUMN]

#### User Persona:

**Age:** 23
**Profession:** Showroom Staff / First-Time Two-Wheeler Buyer
**Location:** Chennai, Tamil Nadu

**Pain Point:** Showroom staff waste 15–20 minutes per vehicle typing complex engine numbers manually. Customers without technical knowledge cannot choose a bike from generic price/CC filters and leave without making a decision.

**Needs:** A fast mobile tool that scans vehicle data in seconds and guides buyers to the right bike through a simple conversation.

---

#### Problem Statement:

Rajan is a sales executive at a local two-wheeler dealership in Chennai. Every time a new vehicle arrives, he must manually type the 12-digit engine and chassis numbers — a task that is slow, error-prone, and often results in document mismatches at the RTO. Priya, a first-time buyer, walks into the same showroom but struggles to choose from 40+ bikes listed with only price and CC filters. She leaves without buying. Both problems — one operational, one discovery-related — exist because no single mobile tool addresses both.

---

#### Solution:

MotoVista addresses both problems in one app. For Rajan, the OCR scanner reads engine numbers from the physical vehicle in under 2 seconds, eliminating manual typing entirely. For Priya, the AI chatbot understands natural language — she can say "I want a fuel-efficient bike for my daily office commute under ₹1.2 lakh" and get instant, relevant suggestions. The same app also manages the full purchase pipeline: KYC verification, payment confirmation, RTO tracking, and invoice delivery.

---

#### Business Potential:

*(Place bar chart here — Indian two-wheeler market size: ₹1.8L crore in 2023, projected ₹3.2L crore by 2030)*
*(Place pie chart here — Revenue breakdown: showroom management SaaS 50%, AI discovery tools 30%, invoice/document services 20%)*

The Indian two-wheeler market is the largest in the world — over 15 million units sold annually. Only 12% of small dealerships currently use any digital management tool. MotoVista targets this 88% gap with a mobile-first, affordable platform that requires no desktop infrastructure.

---

## POSTER 2 — Flow Diagram Poster
*(Matches reference 2.png — Flowchart + Languages Used, white background)*

---

### [TOP — TITLE]
**MotoVista — Application Workflow**

---

### [MAIN — TWO FLOWCHARTS SIDE BY SIDE]

**CUSTOMER FLOW:**

```
[Start]
   ↓
[Visit Showroom]
   ↓
[Download App & Register]
   ↓
[Email Verification]
   ↓
[Login → Role: Customer]
   ↓
[Profile Setup — KYC Upload (Optional)]
   ↓
[Customer Home Screen]
   ↓ (two paths)
[Browse Bikes]  OR  [Ask AI Chatbot → Get Recommendation]
   ↓
[Select Bike → "Order Now"]
   ↓
[Upload KYC — if skipped earlier (Mandatory here)]
   ↓
[Invoice Preview Screen]
   ↓
["Request Sent" Confirmation]
   ↓
[Admin Approves → Payment Screen]
   ↓
[EMI or Cash — Decided with Admin]
   ↓
[Physical Delivery at Showroom]
   ↓
[PDF Invoice Pushed to Customer App]
   ↓
["My Bikes" Updated]
   ↓
[EMI: Monthly Pay → "Mark as Paid" → Admin Confirms]
   ↓
[Track Order via 5-Point Checklist]
   ↓
[End]
```

**ADMIN FLOW:**

```
[Start]
   ↓
[Login → Role: Admin]
   ↓
[Admin Dashboard]
   ↓
[View Pending Customer Orders]
   ↓
[Review KYC Documents]
   ↓
[Approve Order]
   ↓
[Open OCR Scanner]
   ↓
[Point Camera at Engine Plate]
   ↓
[Number Extracted → Confirm → Save to DB]
   ↓
[Payment Screen — Ask: EMI or Cash?]
   ↓
[Record Payment Mode]
   ↓
[Customer Visits for Physical Delivery]
   ↓
[PDF Invoice Pushed to Customer App]
   ↓
[EMI Ledger Created (if EMI)]
   ↓
[Receive "Mark as Paid" Notification]
   ↓
[Cross-Check → Confirm Payment]
   ↓
[Update RTO Stages (5 Checkpoints)]
   ↓
[Each Stage → Notification to Customer]
   ↓
[End]
```

---

### [BOTTOM — LANGUAGES USED]

**Languages Used:**

Frontend Development: Java & XML (Android Studio)
AI Chatbot Engine: Python (FAISS, Sentence-Transformers)
OCR Engine: Python (PyTorch CRNN, OpenCV, CLAHE)
Backend Development: PHP (REST API)
Database: MySQL
Server: XAMPP (Apache)
Networking: Retrofit HTTP Client

---

## POSTER 3 — Wireframes + Features Poster
*(Matches reference 3.png — Left: paper wireframes photo + features list / Right: Figma/app mockups)*

---

### [TOP LEFT — HEADING]
**WIREFRAMES:**

*(Place photo of hand-drawn paper wireframe sketches here)*

Screens sketched:
1. Login / Register screen
2. Customer Home Screen
3. AI Chatbot Screen
4. Admin Dashboard
5. OCR Camera Scanner
6. My Bikes + EMI Table
7. Order Tracking Screen

---

### [TOP RIGHT — HEADING]
**PROTOTYPING / APP MOCKUPS:**

*(Place actual app screenshots here — 3 screens in a column)*
- Screenshot 1: Login Screen
- Screenshot 2: Customer Home Screen with bike cards
- Screenshot 3: AI Chatbot conversation screen

---

### [BOTTOM LEFT — FEATURES]
**FEATURES OF MOTOVISTA — AI RETAIL ECOSYSTEM:**

**1. Seamless Purchase Journey**
- Customer visits showroom → Downloads app → Registers → Browses bikes or uses AI chat
- Full order-to-delivery process managed digitally from one app

**2. Real-Time AI Vehicle Discovery**
- Conversational chatbot understands natural language queries
- FAISS vector search returns the top 3 most relevant bikes instantly

**3. OCR-Powered Stock Intake**
- Admin scans engine/chassis plate with live camera
- CRNN model extracts the 12-digit code in under 2 seconds — no manual typing

**4. Verified Payment Tracking**
- Customer physically pays → Presses "Mark as Paid" in app → Admin confirms
- EMI ledger updated only after admin cross-check — no in-app payment gateway required

---

## POSTER 4 — High Fidelity Screens (Customer Side)
*(Matches reference 4.png — 3 app screens side by side)*

---

### [TOP — HEADING]
**MotoVista — Customer Interface Screens**

*(Place 3 high-fidelity app screenshots side by side)*

**Screen 1 — Customer Home Screen:**
- Header: "Hello, [Customer Name]" with profile icon
- Featured bike cards in horizontal carousel (image, name, price)
- Floating "Ask AI" button at bottom
- Bottom navigation: Home | My Orders | My Bikes | Profile

**Screen 2 — AI Chatbot Screen:**
- Chat-style interface with message bubbles
- Bot greeting: "Hi! I'm your Bike Decision Intelligence 🏍️"
- Quick-select option chips: "Under 1 Lakh", "City Commute", "Mileage & Efficiency"
- Bike recommendation card with name, image, price, "View Details" button

**Screen 3 — Order Tracking Screen:**
- 5-point vertical progress checklist
  - ✅ Order Confirmed
  - ✅ Insurance Done
  - ⏳ RC Book Received
  - ⬜ Number Plate Fixed
  - ⬜ Registration Completed
- Each item has a timestamp and admin note
- Notification bell icon at top right

---

## POSTER 5 — High Fidelity Screens (Admin Side)
*(Matches reference 5.png — 3 app screens side by side)*

---

### [TOP — HEADING]
**MotoVista — Admin Interface Screens**

*(Place 3 high-fidelity app screenshots side by side)*

**Screen 1 — Admin Dashboard:**
- Summary cards: Total Pending Requests | Active Orders | Registered Vehicles | EMI Dues
- Recent customer request list with color-coded status labels:
  - 🟡 Yellow = Pending
  - 🔵 Blue = Under Review
  - 🟢 Green = Approved
- Bottom navigation: Dashboard | Orders | OCR Scanner | EMI | Profile

**Screen 2 — OCR Camera Scanner Screen:**
- Live camera viewfinder with a glowing teal rectangular scan zone in the center
- Prompt: "Point camera at engine or chassis plate"
- When number detected: highlighted bounding box around text
- Extracted number shown below: e.g., "MT15X7890123"
- "Confirm & Save" button | "Re-scan" option

**Screen 3 — EMI Ledger Screen:**
- Customer name and bike model at top
- Table layout:
  | Inst. # | Due Date | Amount | Status |
  |---------|----------|--------|--------|
  | 1 | 01-Jun-2025 | ₹4,250 | ✅ Paid |
  | 2 | 01-Jul-2025 | ₹4,250 | ⏳ Pending |
  | 3 | 01-Aug-2025 | ₹4,250 | ⬜ Upcoming |
- "Confirm Payment" button active for pending instalments
- Total Paid / Total Remaining summary at bottom

---

*Note: Insert actual app screenshots in Poster 4 and Poster 5. The content above describes exactly what each screen should show so you know which screenshots to take from your app.*
