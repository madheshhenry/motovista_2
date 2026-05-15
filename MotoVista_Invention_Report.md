# TITLE: MOTOVISTA — AI-Powered Automotive Dealership and Customer Retail Ecosystem

## ABSTRACT

MotoVista is a mobile application built for Android that digitizes the complete two-wheeler purchasing process. It connects customers and showroom administrators through a single, unified platform with two specialized interfaces — one for buyers and one for dealership staff. The application uses a conversational AI chatbot to help customers find the right bike through natural language inputs, and an OCR camera scanner to help showroom staff extract engine and chassis numbers directly from physical vehicles without manual typing. It also handles KYC document uploads, EMI calculation, RTO registration tracking, and PDF invoice generation — all from a smartphone. MotoVista eliminates the need for paper forms, manual data entry, and disconnected software tools that currently slow down the vehicle sales process.

---

## THE FIELD OF INVENTION

This invention belongs to the following technology domains:

* Mobile application development for the automotive retail sector
* Artificial Intelligence and Natural Language Processing (NLP) for conversational vehicle search
* Computer Vision and Optical Character Recognition (OCR) for physical vehicle data extraction
* Secure digital document management and financial ledger tracking
* Dual-role enterprise mobile software for inventory and customer management

---

## BACKGROUND OF THE INVENTION

Two-wheeler dealerships in India still follow many manual processes that slow down operations and lead to avoidable mistakes. The key problems that this invention addresses are:

**Manual Vehicle Data Entry**
When a new vehicle arrives at the showroom, staff have to manually type the 12-digit engine and chassis numbers into their system. These numbers are long, complex, and engraved on metal parts — making errors very common. A single wrong digit can create legal and documentation problems during RTO registration.

**Poor Customer Discovery Tools**
Most existing platforms (like BikeWale or OLX Bikes) only allow customers to filter bikes by price or engine size. They don't help a buyer who doesn't know technical specs but knows what they need — for example, "a fuel-efficient bike for my daily office commute." There is no conversational or intelligent guidance available.

**Fragmented Workflow for Dealerships**
Showroom staff currently use one app for customer leads, a separate billing software for invoices, a third tool for EMI tracking, and paper books for registration status. There is no single tool that combines all these tasks in one place that works from a mobile device.

**No Unified Buyer-Seller Connection**
The customer's booking application and the dealership's backend management are completely disconnected in existing systems. This forces staff to re-enter data from customer emails or WhatsApp messages into their own systems, wasting time and creating errors.

---

## SUMMARY OF THE INVENTION

MotoVista solves all of the above problems by providing a single Android application with two distinct, role-based interfaces.

For the **Customer**, the app provides:
* A digital showroom to browse all available bikes with specifications and images
* A conversational AI chatbot that recommends vehicles based on what the customer describes in their own words
* A booking system with a two-step KYC verification process (documents collected at profile setup or at order time)
* Real-time order status tracking

For the **Admin/Dealership Staff**, the app provides:
* A dashboard showing all pending customer orders and their uploaded documents
* A live OCR camera scanner that reads engine and chassis numbers directly from the physical vehicle and stores them in the database
* An EMI ledger to track monthly payment schedules for each customer
* An RTO registration tracker to monitor the vehicle's legal registration status
* A one-tap PDF invoice generator that creates and saves the final bill

The application uses a JWT-based dual-role login system to route users to the correct interface after authentication. All AI processing (chatbot and OCR) runs on a local Python server for speed and privacy.

---

## SPECIFICATION

**System Components**

| Component | Description |
| :--- | :--- |
| NLP AI Chatbot Engine | Converts user text queries into vectors and finds the closest matching bike using FAISS |
| OCR Camera Extraction Engine | Uses computer vision to read alphanumeric vehicle codes from live camera images |
| JWT Dual-Role Auth Module | Routes users to Admin or Customer interface based on their verified login token |
| Financial Ledger Module | Calculates and stores EMI schedules using standard amortization formulas |
| RTO Status Tracker | Admin-updated registration status visible to the customer in real time |
| KYC Document Handler | Accepts images, PDFs, and DOCX files for identity verification |
| PDF Invoice Generator | Renders a professional invoice document from live order data on the device |
| MySQL Relational Database | Stores all users, vehicles, orders, ledgers, and documents in structured tables |
| Retrofit API Network Client | Handles all Android-to-PHP communication securely |

**Algorithms Used**
* FAISS (Facebook AI Similarity Search) — for fast vector-based bike matching
* Sentence-Transformers (all-MiniLM-L6-v2) — for converting natural language into search vectors
* CRNN + CTC Decoding — for OCR character recognition from engine images
* CLAHE Image Enhancement — for improving image clarity before OCR processing
* HMAC-SHA256 — for generating and validating secure JWT tokens
* Amortization Formula — for accurate EMI schedule calculation

**Supported Data Formats**
* Text (Alphanumeric strings for OCR extraction)
* Images (JPEG/PNG for KYC uploads and OCR input)
* Documents (PDF, DOCX for KYC identity verification)
* Exported Invoices (PDF, saved locally on device)

---

## CONTENT AND DATA

**Static Data**
* Pre-loaded vehicle catalog with specifications: Engine CC, Mileage, Weight, Price, Type, and Brand
* FAISS vector embeddings built from vehicle descriptions
* Pre-trained OCR model weights (CRNN architecture, stored as `.pth` file)
* Standardized PDF invoice layout template

**Dynamic Data**
* Customer registration details and JWT session tokens
* Conversational chatbot query inputs and AI-generated responses
* OCR-extracted engine and chassis number strings from physical vehicles
* Customer-uploaded KYC documents (Aadhar/PAN)
* EMI payment schedules calculated per order
* RTO registration status updates made by admin
* Generated PDF invoice files per completed sale

---

## FEATURES

**Core Features**
* Digital two-wheeler showroom with bike specifications and images
* Live camera OCR scanner for 12-digit engine and chassis code extraction
* Customer booking system with two-step KYC document upload
* Admin review panel for verifying customer applications and assigning vehicles
* PDF invoice delivered to the customer's app after physical payment and vehicle delivery at the showroom

**Advanced Features**
* Conversational NLP chatbot that recommends bikes based on lifestyle descriptions
* Single-app dual-role interface transformation (Admin view vs. Customer view) using JWT
* FAISS-powered vector search for real-time, relevant bike recommendations
* EMI financial tracking ledger with instalment-wise payment status
* RTO registration status monitoring with timestamped admin updates
* Test-Time Augmentation (TTA) in OCR processing for better accuracy under different lighting

---

## DESCRIPTION

When a new user opens MotoVista, they are presented with a login screen that has two separate entry points — one for customers and one for dealership admins. A new customer registers by providing their name, email, and phone number. They receive a verification link on their email to activate their account. After logging in, the customer is directed to a Profile Setup screen where they can optionally upload their Aadhar and PAN card. If they choose to skip this step, the app will prompt them again when they place a bike order — at that point the upload becomes mandatory before the order can be submitted.

Once inside the app, the customer sees a clean digital showroom displaying all available bikes with photos, prices, and key specifications. A floating "Ask AI" button opens the conversational chatbot. The customer can type anything like "I want a bike for long highway rides under 1.5 lakh" and the AI will respond with the top 3 most relevant bikes from the database. The AI works by converting the customer's sentence into a numerical vector using Sentence-Transformers, then comparing it against pre-built vectors of all bike descriptions using FAISS to find the closest matches.

When the customer selects a bike and places an order, their application — including any uploaded documents — is sent to the admin dashboard. The admin can see all incoming orders along with the uploaded KYC files. After reviewing the documents, the admin approves the order and proceeds to assign the physical vehicle from the showroom's stock. To do this, they open the OCR Scanner screen and point the phone camera at the engine plate of the selected bike. The app captures the image, preprocesses it using CLAHE contrast enhancement, and runs it through the trained CRNN model to extract the exact alphanumeric code. The extracted number is shown to the admin for confirmation before it is saved to the database.

Once the vehicle is assigned via OCR, the admin contacts the customer to discuss the payment mode. The admin opens a **Payment Screen** and records whether the customer will pay via **full Cash** or **EMI instalments**. If EMI is selected, the system calculates the monthly schedule based on the loan amount, interest rate, and duration. The customer then visits the showroom for the physical handover of the vehicle. After delivery is completed at the showroom, the **PDF invoice is automatically pushed to the customer's app** — containing vehicle details, total amount, payment mode, and dealership information.

For EMI customers, the purchased bike and its full instalment table appear in the "My Bikes" section of the app. Since there is no in-app payment gateway, the customer physically visits or calls the showroom each month. After making a payment, the customer taps the **"Mark as Paid" button** in the app. The admin receives an instant notification, manually cross-checks the payment, and confirms it — which updates that instalment's status in the EMI ledger. For RTO registration, the showroom calls the customer to bring the vehicle. The registration is done by the showroom staff, and each of the five registration stages is marked by the admin in the app, triggering a push notification to the customer.

---

## CLAIMS

* **Claim 1:** A method for unifying vehicle consumer discovery and dealership inventory management within a single dual-role Android application that serves two different user types from one codebase using JWT-based authentication.

* **Claim 2:** A method for extracting real-world 12-digit alphanumeric engine and chassis identification codes from physical vehicle surfaces using a live camera feed, CLAHE preprocessing, and CRNN-based OCR directly into a digital inventory database.

* **Claim 3:** A conversational AI chatbot that converts natural language customer inputs into numerical vectors using Sentence-Transformers and retrieves the most relevant vehicle recommendations using FAISS-based vector similarity search.

* **Claim 4:** A two-step KYC document collection mechanism that optionally collects identity documents at the time of customer registration and mandatorily re-prompts for upload at the point of order placement if the step was previously skipped.

* **Claim 5:** An integrated financial management module that calculates EMI payment schedules and updates instalment statuses only after the admin manually confirms a customer's "Mark as Paid" button press, ensuring accurate and verified payment tracking without an in-app payment gateway.

* **Claim 6:** A PDF invoice delivery system that automatically pushes a structured sale invoice to the customer's app after the physical payment and vehicle delivery process is completed at the showroom.
