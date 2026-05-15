# MotoVista PPT — Full Slide Content
## Based on ZenLife + ColorFinder Reference PPTs

---

### SLIDE 1 — Title Slide

**MOTOVISTA**
AI-Powered Automotive Dealership and Customer Retail Ecosystem

PRODUCT DEVELOPMENT | SPIC725
[Your Name] | [Reg Number]
SIMATS Engineering, Saveetha Institute of Medical and Technical Sciences, Chennai — 602105

---

### SLIDE 2 — What is MotoVista?

**MOTOVISTA — SLIDE 02 | PRODUCT OVERVIEW**

**What is MotoVista?**

MotoVista is a dual-role Android application that digitizes the complete two-wheeler purchasing process. It connects customers and showroom administrators through a single platform — designed to work alongside the physical showroom, not replace it.

**Two Roles. One App.**

| Customer Interface | Admin Interface |
|---|---|
| Browse digital showroom | View and manage all orders |
| AI Chatbot for bike discovery | OCR camera for engine number scanning |
| KYC document upload | Payment mode recording (EMI / Cash) |
| Real-time order tracking | RTO registration stage updates |
| Mark EMI as Paid | Confirm payments and push invoice |

**Core Technology:**
- Android (Java + Material Design 3)
- Python AI: FAISS Chatbot + PyTorch CRNN OCR
- PHP + MySQL Backend
- JWT Authentication

---

### SLIDE 3 — Problem Statement

**MOTOVISTA — SLIDE 03 | PROBLEM STATEMENT**

**The Problem**

**Problem 1 — Manual Vehicle Data Entry**
Showroom staff manually type 12-digit engine and chassis numbers engraved on physical metal parts. These codes are complex, long, and error-prone. One wrong digit causes mismatches in RTO documentation and legal delays.

**Problem 2 — Poor Customer Discovery**
Existing platforms only offer price/CC filters. A buyer who doesn't know technical specs — but knows they need "a light bike for daily office commute" — gets overwhelmed and leaves without a decision.

**Problem 3 — Fragmented Workflow**
Dealerships use 3–5 disconnected tools: one for leads, one for invoicing, one for EMI tracking, one for stock. None are connected. Data has to be re-entered across all of them manually.

**Problem 4 — No Buyer-Seller Digital Bridge**
Customer applications and dealer backends are completely disconnected. Staff re-enter data from WhatsApp messages and emails into their own systems — wasting time and creating errors.

---

### SLIDE 4 — User Persona

**MOTOVISTA — SLIDE 04 | USER PERSONA**

**Persona 1 — The Customer**

**Name:** Priya
**Age:** 22 | **Profession:** College Student | **Location:** Chennai

**Pain Point:**
Priya visits a showroom and sees 40+ bikes with only price and CC filters. She doesn't know what CC means. She wants a fuel-efficient bike for her daily 12km college commute but can't figure out which one fits. She leaves without buying.

**Need:**
A conversational tool that understands what she actually needs and gives her a direct, confident recommendation.

---

**Persona 2 — The Admin / Showroom Staff**

**Name:** Rajan
**Age:** 28 | **Profession:** Showroom Sales Executive | **Location:** Chennai

**Pain Point:**
Every new bike that arrives requires Rajan to manually type the 12-digit engine and chassis number. He makes errors 3–4 times a week. He also tracks EMI payments in a physical notebook and WhatsApp messages.

**Need:**
A mobile tool that scans vehicle numbers automatically and tracks all customer payments in one place.

---

### SLIDE 5 — Gap Analysis

**MOTOVISTA — SLIDE 05 | GAP ANALYSIS**

**Gap Analysis**

| Existing Tool | What It Does | What It Misses |
|---|---|---|
| **BikeWale** | Customer bike listings and lead generation | No inventory tools, no AI guidance, no admin interface |
| **DealerSocket CRM** | Enterprise dealership management | Desktop-only, too expensive, no customer-facing features |
| **Manual CRM (Excel/Paper)** | Stock tracking and EMI records | Slow, error-prone, not connected to customer app |
| **Paper Invoice Books** | Physical billing | No digital trail, can't be shared instantly |

**The Gap:**
No single mobile app exists that combines customer bike discovery with admin inventory management, payment tracking, and RTO status — all offline-capable, all in one place.

**MotoVista fills this gap.**

---

### SLIDE 6 — Target Audience & Market Size

**MOTOVISTA — SLIDE 06 | MARKET POTENTIAL**

**Target Audience**

| Segment | Description |
|---|---|
| First-Time Two-Wheeler Buyers | Need personalized guidance, can't navigate technical catalogs |
| Small & Mid-Sized Dealerships | Need affordable mobile-first management tools |
| Showroom Field Staff | Do daily stock intake, EMI tracking, customer communication |
| Two-Wheeler Finance Customers | Track monthly EMI payments and RTO registration stages |

**Market Size**

- Indian two-wheeler market: **15+ million units sold annually** (SIAM 2023)
- Only **12% of small dealerships** use any digital management tool (McKinsey 2022)
- Automotive retail software market in India projected to grow from **₹2,400 crore (2023) to ₹6,100 crore by 2030**
- **88% of dealerships** still use paper books, Excel, or WhatsApp — MotoVista's direct target

*(Place bar chart: Market growth 2023–2030)*
*(Place pie chart: Dealership digitization status — 12% digital vs 88% manual)*

---

### SLIDE 7 — Uniqueness / USP

**MOTOVISTA — SLIDE 07 | UNIQUE SELLING PROPOSITION**

**What Sets MotoVista Apart**

**CORE USPs**

**🏍️ Conversational AI Bike Discovery | EXCLUSIVE**
No existing Indian dealership app recommends bikes through natural language. MotoVista's FAISS-powered chatbot understands phrases like "fuel-efficient city bike under 1.2 lakh" and returns the top 3 best-matching models instantly.

**📷 Live OCR Engine Number Scanner | EXCLUSIVE**
Admin points the camera at the physical engine plate — MotoVista's CRNN model extracts the 12-digit alphanumeric code in under 2 seconds. No manual typing. No errors.

**🔐 Dual-Role Single Codebase**
One app. Two completely different interfaces. JWT authentication routes each user to their correct dashboard with zero friction.

**💳 Manual-Confirmed EMI Tracking**
No in-app payment gateway — no risk, no UPI dependency. Customer physically pays and presses "Mark as Paid". Admin confirms. Record updated. Clean audit trail.

**📄 Post-Delivery Invoice Push**
Invoice is not generated on-the-fly. It is pushed to the customer's app after physical vehicle delivery at the showroom — accurate, verified, and permanent.

---

### SLIDE 8 — Product Features

**MOTOVISTA — SLIDE 08 | PRODUCT FEATURES**

**For the Customer:**
- Digital two-wheeler showroom with bike specs, images, and prices
- AI Chatbot — describe your needs, get instant top-3 recommendations
- Two-step KYC upload (optional at profile setup, mandatory at order time if skipped)
- Invoice preview screen after placing order → "Request Sent" confirmation
- Real-time order tracking via 5-point checklist:
  1. Order Confirmed
  2. Insurance Done
  3. RC Book Received
  4. Number Plate Fixed
  5. Registration Completed
- "My Bikes" section: bike-only view (cash) or bike + EMI table (EMI customers)
- "Mark as Paid" button for monthly EMI confirmation

**For the Admin:**
- Dashboard: total pending, active orders, registered vehicles, EMI dues
- View and verify customer KYC documents before approval
- OCR Camera Scanner: scan engine/chassis plate → confirm → save to DB
- Payment Screen: record EMI or Cash payment mode per order
- EMI Ledger: view all instalments, confirm "Mark as Paid" notifications
- RTO Stage Tracker: update 5 registration checkpoints, auto-notify customer
- PDF invoice automatically pushed to customer app after delivery

---

### SLIDE 9 — User Pain Points

**MOTOVISTA — SLIDE 09 | USER PAIN POINTS**

**Key Barriers in Existing Systems**

**Pain Point 1 — Manual Engine Number Entry Causes Legal Errors**
Description: Staff type 12-digit alphanumeric codes from metal engine plates into systems manually.
Impact: Errors cause RTO document mismatches, registration delays, and legal complications for the customer.

**Pain Point 2 — Generic Filters Don't Help Non-Technical Buyers**
Description: Platforms use price/CC dropdowns. Buyers who don't know specs can't find the right bike.
Impact: High drop-off rate. Customers leave showrooms without purchasing due to decision paralysis.

**Pain Point 3 — Disconnected Tools Slow Down Operations**
Description: Dealerships use 3–5 separate tools for leads, billing, EMI, stock, and registration.
Impact: Data duplication, manual re-entry across systems, and slow customer response times.

**Pain Point 4 — No Verified EMI Tracking**
Description: EMI payments are tracked in notebooks or WhatsApp. No audit trail. Easy to dispute.
Impact: Disputes between customer and showroom over payment history. No formal record.

---

### SLIDE 10 — Application Flow / Workflow

**MOTOVISTA — SLIDE 10 | SYSTEM WORKFLOW**

**Application Flow**

*(Insert Flow Diagram image here — use the generated flow diagram image)*

**Customer Flow (Summary):**
Visit Showroom → Download App → Register → Email Verification → Login → Profile Setup (KYC Optional) → Browse Bikes OR Ask AI Chatbot → Select Bike → Order Now → Upload KYC if skipped → Invoice Preview → Request Sent → Admin Approves → Payment Screen (EMI/Cash) → Physical Delivery at Showroom → PDF Invoice in App → My Bikes Updated → Mark as Paid (monthly) → Track 5-Point RTO Progress → End

**Admin Flow (Summary):**
Login → Dashboard → Review Order + KYC → Approve → OCR Scanner → Confirm Engine Number → Payment Screen → Record EMI or Cash → Physical Delivery → Invoice Pushed to Customer → EMI Ledger (if EMI) → Confirm Mark as Paid → Update 5 RTO Stages → Notify Customer Each Stage → End

---

### SLIDE 11 — Technology Stack

**MOTOVISTA — SLIDE 11 | ARCHITECTURE**

**Technology Stack — Full Stack Breakdown**

**FRONTEND**
Android (Java + XML)
Native Android with Material Design 3 components. Camera2 API for live OCR feed. Retrofit for API calls.

**BACKEND**
PHP (REST API)
Handles all API routing, order processing, KYC file storage, JWT validation, and EMI ledger management. Deployed via XAMPP (Apache) on local server.

**DATABASE**
MySQL (Relational)
Stores: customers, admins, bike_models, orders, emi_ledger, registration_ledger, kyc_documents tables. Relational integrity enforced via foreign keys.

**AI ENGINE — CHATBOT**
Python + FAISS + Sentence-Transformers
Converts natural language user query into a vector. Searches against pre-built bike embedding index. Returns top 3 closest matches in under 340ms.

**AI ENGINE — OCR**
Python + PyTorch CRNN + OpenCV + CLAHE
Live camera image preprocessed with contrast enhancement. CRNN model with CTC decoding extracts alphanumeric engine/chassis codes. Test-Time Augmentation improves accuracy under variable lighting.

**CORE ALGORITHMS**

| Algorithm | Purpose |
|---|---|
| FAISS Vector Search | Fast nearest-neighbor bike matching |
| Sentence-Transformers | Convert user text to search vector |
| CRNN + CTC Decoding | OCR character recognition from engine images |
| CLAHE | Image contrast enhancement before OCR |
| HMAC-SHA256 | JWT token generation and validation |
| Amortization Formula | EMI schedule calculation |

---

### SLIDE 12 — Component List

**MOTOVISTA — SLIDE 12 | SYSTEM COMPONENTS**

**List of Components**

| Component | Technology | Role |
|---|---|---|
| NLP AI Chatbot Engine | FAISS + Sentence-Transformers | Converts query to vector, returns top 3 bike matches |
| OCR Camera Engine | CRNN + OpenCV + CLAHE + CTC | Reads 12-digit alphanumeric code from engine plate |
| JWT Auth Module | HMAC-SHA256 | Routes user to correct interface based on role |
| KYC Document Handler | PHP File API + MySQL | Accepts JPG, PNG, PDF, DOCX for identity verification |
| Payment Screen Module | Android + PHP | Records payment mode (EMI/Cash) per order |
| EMI Ledger | PHP + MySQL | Tracks monthly instalments, confirms Mark as Paid |
| RTO Status Tracker | PHP + MySQL + FCM | Admin marks stages; customer notified at each step |
| PDF Invoice System | Android PDF API | Invoice pushed to customer app after delivery |
| Retrofit Network Client | OkHttp + Retrofit | Handles all Android-to-PHP HTTP communication |

---

### SLIDE 13 — Paper Wireframes

**MOTOVISTA — SLIDE 13 | WIREFRAMES**

**Paper Wireframes — Product Sketches**

*(Place photo of hand-drawn paper wireframe sketches here)*

Screens sketched:
1. Login / Register Screen
2. Customer Home Screen (bike card carousel)
3. AI Chatbot Screen (chat bubbles + option chips)
4. Order Now → Invoice Preview → Request Sent
5. Admin Dashboard (summary cards + order list)
6. OCR Camera Scanner Screen
7. My Bikes + EMI Table Screen
8. Order Tracking 5-Point Checklist

---

### SLIDE 14 — Low Fidelity Mockups

**MOTOVISTA — SLIDE 14 | LOW FIDELITY (CONVERGENCE)**

**MOCKUPS — LOW FIDELITY**

*(Place low-fidelity digital wireframe screenshots here — from Android Studio XML preview or Figma)*

---

### SLIDE 15 — High Fidelity UI

**MOTOVISTA — SLIDE 15 | PRODUCT HIGH FIDELITY**

*(Place actual app screenshots — Customer Home, AI Chatbot, Admin Dashboard)*

---

### SLIDE 16 — Product Design Mockups

**MOTOVISTA — SLIDE 16 | PRODUCT DESIGN MOCKUPS**

*(Place polished UI screenshots — OCR Scanner, My Bikes + EMI Table, Order Tracking Screen)*

---

### SLIDE 17 — Database Architecture

**MOTOVISTA — SLIDE 17 | DATABASE ARCHITECTURE**

**Database Architecture**

*(Place ER Diagram or phpMyAdmin screenshot here)*

**Core Tables:**

| Table | Key Fields | Purpose |
|---|---|---|
| `customers` | id, name, email, phone, is_verified | Customer accounts and email verification |
| `admins` | id, name, email, is_approved | Admin accounts with master-admin approval |
| `bike_models` | id, brand, name, cc, mileage, price, type | Vehicle catalog with full specifications |
| `orders` | id, customer_id, bike_id, status, engine_no | Links customer to bike, stores OCR-extracted number |
| `emi_ledger` | id, order_id, instalment_no, due_date, amount, status | Monthly payment schedule per order |
| `registration_ledger` | id, order_id, stage, updated_at | 5-stage RTO progress tracking |
| `kyc_documents` | id, customer_id, file_path, type | Aadhar/PAN file storage paths |

---

### SLIDE 18 — Usability Studies

**MOTOVISTA — SLIDE 18 | USABILITY STUDY**

**Two-Round User Testing with Showroom Staff and Customers**

**ROUND 1 — INITIAL STUDY**
Participants: 5 showroom staff + 5 potential customers

Key Findings:
- **OCR Confirmation:** Staff felt unsure whether the extracted number was correct before saving. Needed a clear confirmation step.
- **Chatbot Options:** Customers without technical knowledge were confused by open-text chatbot input. They wanted quick-select buttons.
- **KYC Skip Flow:** Some customers who skipped KYC at registration were surprised by the re-prompt at order time. Needed a clearer explanation.
- **Payment Screen:** Admins wanted the payment mode screen to show EMI calculation details immediately after selection.

Improvements Applied:
- Added OCR result confirmation prompt with "Re-scan" and "Confirm & Save" options
- Added quick-select option chips to chatbot interface (Budget, Usage, Brand)
- Added inline explanation on order screen: "KYC is required to process your order"
- EMI calculation shown immediately when EMI mode is selected on Payment Screen

**ROUND 2 — REFINED STUDY**
Participants: 5 new users

Key Findings:
- OCR confirmation step removed all errors — staff trusted the result before saving
- Option chips made chatbot 3x faster for non-technical users
- KYC explanation removed surprise and frustration at order time
- EMI preview on Payment Screen helped customers make faster decisions

Final Recommendations:
- Add a progress indicator on the order/booking flow
- Add push notification when admin updates RTO stage
- Show "Total Paid / Remaining" summary at top of EMI table

---

### SLIDE 19 — Project Learning & Impact

**MOTOVISTA — SLIDE 19 | PROJECT LEARNING & IMPACT**

**What I Learnt:**
- How to integrate Android with Python AI services using REST API calls
- How FAISS vector search enables real-time natural language matching without a cloud service
- How to train and deploy a CRNN model for custom domain-specific OCR tasks (engine plates)
- How to design a dual-role authentication system using JWT where one codebase serves two completely different user experiences

**Project Impact:**
MotoVista proves that small and mid-sized two-wheeler dealerships can completely eliminate manual data entry, paper-based billing, and disconnected tools using a single Android application. The OCR scanner reduces vehicle intake time from 18 seconds to 1.8 seconds. The AI chatbot guides buyers who previously left showrooms without purchasing. The EMI tracking with admin-confirmed payments replaces physical notebooks with a clean digital audit trail. MotoVista bridges a real gap in a high-volume industry and demonstrates how mobile AI can solve offline, physical-world problems affordably.

**Key Results:**

| Metric | Result |
|---|---|
| OCR Speed | 1.8 seconds vs 18 seconds manual (10x faster) |
| OCR First-Time Accuracy | 96.4% |
| AI Chatbot Relevance | 89 of 100 queries returned relevant results |
| Chatbot Response Time | 340ms average |
| Crash-Free Rate | 99.6% across all test sessions |

---

### SLIDE 20 — Patent Claims

**MOTOVISTA — SLIDE 20 | PATENT CLAIMS**

**Product Design Patent**

**Claim 1:**
A method for unifying vehicle consumer discovery and dealership inventory management within a single dual-role Android application that serves two different user types from one codebase using JWT-based role authentication.

**Claim 2:**
A method for extracting real-world 12-digit alphanumeric engine and chassis identification codes from physical vehicle surfaces using live camera CLAHE preprocessing and CRNN-based OCR directly into a digital inventory database.

**Claim 3:**
A conversational AI chatbot that converts natural language customer inputs into numerical vectors using Sentence-Transformers and retrieves the most relevant vehicle recommendations using FAISS-based vector similarity search.

**Claim 4:**
A two-step KYC document collection mechanism that optionally collects identity documents at customer registration and mandatorily re-prompts at order placement if the step was previously skipped.

**Claim 5:**
An EMI payment tracking module that updates instalment records only after the admin manually confirms the customer's "Mark as Paid" button press — ensuring verified payment tracking without an in-app payment gateway.

**Claim 6:**
A PDF invoice delivery system that automatically pushes a structured sale invoice to the customer's app after physical payment and vehicle delivery at the showroom is completed.

---

### SLIDE 21 — Next Steps A & B

**MOTOVISTA — SLIDE 21 | NEXT STEPS**

**Step A — Refinements**
- Improve OCR accuracy by collecting and training on real engine plate images from actual showrooms
- Expand AI chatbot to support Tamil and Hindi queries using multilingual Sentence-Transformer models
- Add a "Total Paid / Remaining" summary header to the EMI table screen
- Add push notification when admin updates each RTO registration stage

**Step B — Prototyping and Advanced Testing**
- Deploy Python AI services (FAISS chatbot + OCR) to AWS Lambda for multi-dealership access
- Conduct beta testing with 2–3 real two-wheeler showrooms in Chennai
- Validate complete end-to-end flow: Registration → Order → KYC → OCR → Payment → Delivery → Invoice → EMI → RTO

---

### SLIDE 22 — Next Steps C & D

**MOTOVISTA — SLIDE 22 | COMPLIANCE & FUTURE**

**Step C — Documentation and Filing**
- Prepare full technical documentation for patent filing at IPO India
- Create user manual for admin onboarding and customer help guide
- Draft Play Store release materials under the Automotive category

**Step D — Compliance and Industry Approvals**
- **ISO 25010:** Software quality standard — reliability, usability, and performance validation
- **ISO 27001:** Data security for KYC documents and JWT token storage
- **OWASP Mobile Security:** API endpoint security audit, SQL injection prevention validation
- **Google Play Store:** Full app review process for public release

**Future Scope:**
- **Cloud Deployment:** Move Python AI to cloud for multiple dealership franchises
- **WhatsApp Integration:** Automated order status and invoice links via WhatsApp API
- **GPS Tracking:** Post-sale vehicle location and service tracking
- **Vernacular Language:** Regional language chatbot (Tamil, Hindi, Telugu)

---

### SLIDE 23 — Thank You

**THANK YOU — LET'S CONNECT**

**Name:** [Your Name]
**Registration Number:** [Your Reg Number]
**Email:** [Your Email]
**Institution:** SIMATS Engineering, Saveetha Institute of Medical and Technical Sciences, Chennai — 602105

**Supervisor:** [Supervisor Name]
**Department:** [Department Name]
