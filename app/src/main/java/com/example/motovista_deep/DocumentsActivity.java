package com.example.motovista_deep;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DocumentsActivity extends AppCompatActivity {

    private CardView btnBack;
    private Button btnDownloadDeliveryNote, btnDownloadCashReceipt;
    private int requestId = -1;
    private com.example.motovista_deep.helpers.OrderSessionManager sessionManager;
    
    // Data variables
    private String customerName = "";
    private String customerAddress = ""; 
    private String vehicleName = "";
    private String vehicleColor = "";
    private String vehiclePrice = "";
    private String transactionId = "";
    
    // Permission code
    private static final int PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents);

        sessionManager = new com.example.motovista_deep.helpers.OrderSessionManager(this);
        // Persist that we are in Documents Step
        sessionManager.setStep(com.example.motovista_deep.helpers.OrderSessionManager.Step.DOCUMENTS);

        initializeViews();
        handleIntentData();
        
        if (requestId != -1) {
            fetchOrderDetails(requestId);
        } else {
            // Restore from session
            if (sessionManager.isSessionActive()) {
                requestId = sessionManager.getRequestId();
                fetchOrderDetails(requestId);
            } else if (getIntent().hasExtra("customer_name")) {
                 // Check if data came via direct intent extras (fallback)
                 customerName = getIntent().getStringExtra("customer_name");
                 vehicleName = getIntent().getStringExtra("vehicle_model");
                 // We might miss price here if not passed, but let's see
                 double amount = getIntent().getDoubleExtra("amount_paid", 0);
                 vehiclePrice = String.valueOf(amount);
                 transactionId = getIntent().getStringExtra("transaction_id");
                 if(transactionId == null) transactionId = "TXN" + System.currentTimeMillis();
            } else {
                 Toast.makeText(this, "Error: No Data Found", Toast.LENGTH_SHORT).show();
            }
        }

        setupClickListeners();
    }

    private String paymentMode = "Cash"; // Default
    private String orderType = "Full Cash";

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        btnDownloadDeliveryNote = findViewById(R.id.btnDownloadDeliveryNote);
        btnDownloadCashReceipt = findViewById(R.id.btnDownloadCashReceipt);
        // Initialize Next Button
        Button btnNext = findViewById(R.id.btnNext);
        btnNext.setOnClickListener(v -> navigateToOrderCompleted());
    }

    private void navigateToOrderCompleted() {
        com.example.motovista_deep.managers.WorkflowManager.updateStage(this, "ORDER_COMPLETED", requestId, new com.example.motovista_deep.managers.WorkflowManager.WorkflowCallback() {
            @Override
            public void onSuccess() {
                Intent intent = new Intent(DocumentsActivity.this, OrderCompletedActivity.class);
                intent.putExtra("customer_name", customerName);
                intent.putExtra("vehicle_model", vehicleName);
                intent.putExtra("payment_type", paymentMode);
                intent.putExtra("order_type", orderType);
                intent.putExtra("request_id", requestId);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(DocumentsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("request_id")) {
                requestId = intent.getIntExtra("request_id", -1);
            }
            if (intent.hasExtra("payment_mode")) {
                paymentMode = intent.getStringExtra("payment_mode");
            }
            if (intent.hasExtra("order_type")) {
                orderType = intent.getStringExtra("order_type");
            }
        }
    }
    
    private void fetchOrderDetails(int id) {
        com.example.motovista_deep.api.ApiService apiService = com.example.motovista_deep.api.RetrofitClient.getApiService();
        retrofit2.Call<com.example.motovista_deep.models.GetOrderSummaryResponse> call = apiService.getOrderSummary(id);

        call.enqueue(new retrofit2.Callback<com.example.motovista_deep.models.GetOrderSummaryResponse>() {
            @Override
            public void onResponse(retrofit2.Call<com.example.motovista_deep.models.GetOrderSummaryResponse> call, retrofit2.Response<com.example.motovista_deep.models.GetOrderSummaryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess() && response.body().getData() != null) {
                        com.example.motovista_deep.models.OrderSummaryData data = response.body().getData();
                        customerName = data.getCustomerName();
                        String brand = data.getBrand() != null ? data.getBrand() : "";
                        String model = data.getBikeName() != null ? data.getBikeName() : "";
                        vehicleName = brand + " " + model;
                        vehicleColor = data.getBikeVariant() != null ? data.getBikeVariant() : "";
                        
                        // If color name is actually the "variant" in current code logic:
                        if (vehicleColor.isEmpty()) vehicleColor = "Standard";
                        
                        vehiclePrice = data.getOnRoadPrice();
                        if (transactionId.isEmpty()) transactionId = "TXN" + System.currentTimeMillis();
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.example.motovista_deep.models.GetOrderSummaryResponse> call, Throwable t) {
                Toast.makeText(DocumentsActivity.this, "Failed to load details from API", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupClickListeners() {
        // Special requirement: Back button goes to Order Completed
        btnBack.setOnClickListener(v -> navigateToOrderCompleted());
        
        btnDownloadDeliveryNote.setOnClickListener(v -> {
            if (checkPermission()) {
                generateDeliveryNote();
            } else {
                requestPermission();
            }
        });
        
        btnDownloadCashReceipt.setOnClickListener(v -> {
            if (checkPermission()) {
                generateCashReceipt();
            } else {
                requestPermission();
            }
        });
    }

    @Override
    public void onBackPressed() {
        navigateToOrderCompleted();
    }

    private boolean checkPermission() {
        return true; // Simplified for Android 10+ (MediaStore)
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    // --- DIGITAL PDF GENERATION ---

    private void generateDeliveryNote() {
        try {
            PdfDocument pdfDocument = new PdfDocument();
            // Standard A4 Size @ 72 DPI (595 x 842)
            int pageWidth = 595;
            int pageHeight = 842;
            
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
            PdfDocument.Page page = pdfDocument.startPage(pageInfo);
            
            Canvas canvas = page.getCanvas();
            Paint paint = new Paint();
            Paint titlePaint = new Paint();
            Paint boldPaint = new Paint();
            
            // Standard Paint
            paint.setColor(Color.BLACK);
            paint.setTextSize(12);
            paint.setAntiAlias(true);
            
            // Title Paint (Blue, Big)
            titlePaint.setColor(Color.BLUE); 
            titlePaint.setTextSize(26);
            titlePaint.setFakeBoldText(true);
            titlePaint.setTextAlign(Paint.Align.CENTER);
            titlePaint.setAntiAlias(true);
            
            // Bold Paint
            boldPaint.setColor(Color.BLACK);
            boldPaint.setTextSize(14);
            boldPaint.setFakeBoldText(true);
            boldPaint.setAntiAlias(true);

            int xStart = 50; // Left margin
            int xEnd = pageWidth - 50; // Right margin
            int y = 50;

            // --- HEADER ROW 1 ---
            paint.setTextAlign(Paint.Align.LEFT);
            paint.setTextSize(10);
            canvas.drawText("|| Jai Mahaveer ||", xStart, y, paint);
            
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setFakeBoldText(true);
            canvas.drawText("LIVE & LET ALIVE", pageWidth / 2, y, paint);
            paint.setFakeBoldText(false);
            
            paint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText("|| Jai Guru Misri ||", xEnd, y, paint);
            
            y += 40;
            
            // --- SHOP NAME ---
            canvas.drawText("SANTHOSH BIKES", pageWidth / 2, y, titlePaint);
            
            y += 20;
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTextSize(11);
            canvas.drawText("No.433, C.T.H. Road, Kavarapalayam, Avadi, Chennai - 54.", pageWidth / 2, y, paint);
            y += 15;
            canvas.drawText("(Near Anchaneyar Temple)", pageWidth / 2, y, paint);
            
            y += 20;
            // Phone Numbers (Aligned right under header block roughly)
            paint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText("Cell: 81229 99809", xEnd, y, paint);
            y += 15;
            canvas.drawText("76048 38750", xEnd, y, paint);
            
            // --- DELIVERY NOTE BOX ---
            y += 30;
            // Draw box
            int boxWidth = 180;
            int boxHeight = 28;
            int boxX = (pageWidth - boxWidth) / 2;
            int boxY = y;
            
            Paint boxPaint = new Paint();
            boxPaint.setStyle(Paint.Style.STROKE);
            boxPaint.setStrokeWidth(2);
            boxPaint.setColor(Color.BLACK);
            canvas.drawRect(boxX, boxY - 20, boxX + boxWidth, boxY + 10, boxPaint);
            
            // Text inside box
            boldPaint.setTextAlign(Paint.Align.CENTER);
            boldPaint.setTextSize(16);
            canvas.drawText("DELIVERY NOTE", pageWidth / 2, y, boldPaint);
            
            // --- NO. and DATE ---
            y += 40;
            paint.setTextAlign(Paint.Align.LEFT);
            paint.setTextSize(12);
            boldPaint.setTextSize(12);
            boldPaint.setTextAlign(Paint.Align.LEFT);
            
            canvas.drawText("No: ", xStart, y, boldPaint);
            canvas.drawText(transactionId.substring(Math.max(0, transactionId.length() - 6)), xStart + 25, y, paint);
            
            String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
            paint.setTextAlign(Paint.Align.RIGHT);
            boldPaint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText("Date: " + currentDate, xEnd, y, paint);
            
            // --- CUSTOMER DETAILS SECTION ---
            y += 40;
            int lineSpacing = 30;
            
            // Name
            boldPaint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText("Name :", xStart, y, boldPaint);
            paint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText(customerName, xStart + 60, y, paint);
            // Underline line
            canvas.drawLine(xStart + 60, y + 4, xEnd, y + 4, paint);
            
            y += lineSpacing;
            // S/o
            canvas.drawText("S/o :", xStart, y, boldPaint);
            canvas.drawLine(xStart + 60, y + 4, xEnd, y + 4, paint);
            
            y += lineSpacing;
            // Address Line 1
            canvas.drawText("Address :", xStart, y, boldPaint);
            canvas.drawText("Chennai, Tamil Nadu", xStart + 80, y, paint);
            canvas.drawLine(xStart + 80, y + 4, xEnd, y + 4, paint);
            
            y += lineSpacing;
            // Address Line 2 (Dotted equivalent)
            canvas.drawLine(xStart, y + 4, xEnd, y + 4, paint);
            
            y += lineSpacing;
            // Pin Code
            canvas.drawLine(xStart, y + 4, pageWidth - 160, y + 4, paint); // Long line
            canvas.drawText("Pin :", pageWidth - 150, y, boldPaint);
            canvas.drawLine(pageWidth - 110, y + 4, xEnd, y + 4, paint); // Pin line
            
            // --- VEHICLE DETAILS ---
            y += 45;
            // Chassis No
            canvas.drawText("Chassis No :", xStart, y, boldPaint);
            // Draw line up to mid point
            canvas.drawLine(xStart + 90, y + 4, (pageWidth / 2) - 10, y + 4, paint);
            
            // Engine No
            canvas.drawText("Engine No :", (pageWidth / 2) + 10, y, boldPaint);
            canvas.drawLine((pageWidth / 2) + 100, y + 4, xEnd, y + 4, paint);
            
            y += lineSpacing;
            // Name of Vehicle
            canvas.drawText("Name of Vehicle :", xStart, y, boldPaint);
            canvas.drawText(vehicleName, xStart + 120, y, paint);
            canvas.drawLine(xStart + 120, y + 4, xEnd - 150, y + 4, paint);
            
            // Colour
            canvas.drawText("Colour :", xEnd - 140, y, boldPaint);
            canvas.drawText(vehicleColor, xEnd - 80, y, paint);
            canvas.drawLine(xEnd - 80, y + 4, xEnd, y + 4, paint);

            // --- DECLARATION ---
            y += 50;
            paint.setTextSize(11);
            paint.setColor(Color.DKGRAY);
            canvas.drawText("Today I have taken delivery of the above cited vehicle and I have received in good condition.", xStart, y, paint);
            y += 18;
            canvas.drawText("The entire risk is being borne by me from this date.", xStart, y, paint);
            
            // --- CHECKLIST ---
            paint.setColor(Color.BLACK);
            paint.setTextSize(12);
            y += 40;
            canvas.drawText("DOCUMENTS", xStart, y, boldPaint);
            
            int col2 = (pageWidth / 2) + 30;
            int checklistSpacing = 22;
            
            y += 25;
            canvas.drawText("1. Company Name : ", xStart, y, paint);
            canvas.drawText("5. Aadhar No.", col2, y, paint);
            
            y += checklistSpacing;
            canvas.drawText("2. H.P.", xStart, y, paint);
            canvas.drawText("6. PAN No.", col2, y, paint);
            
            y += checklistSpacing;
            canvas.drawText("3. Free Service Coupon Book : ", xStart, y, paint);
            canvas.drawText("7. Battery No. :", col2, y, paint);
            
            y += checklistSpacing;
            canvas.drawText("4. Tool Kit :", xStart, y, paint);
            canvas.drawText("8. Photo :", col2, y, paint);
            
            y += checklistSpacing;
            canvas.drawText("9. Balance Moven :", col2, y, paint);
            
            // --- FOOTER LINES ---
            y += 50;
            paint.setStrokeWidth(1);
            canvas.drawLine(xStart, y, xEnd, y, paint); // Top Divider
            
            y += 30;
            boldPaint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText("For SANTHOSH BIKES", xEnd, y, boldPaint);
            
            y += 70; // Space for sign
            
            paint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText("Customer's Signature", xStart, y, paint);
            
            boldPaint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText("(Delivery Section)", xEnd, y, paint);
            
            y += 20;
            paint.setStrokeWidth(1);
            canvas.drawLine(xStart, y, xEnd, y, paint); // Bottom Divider
            
            y += 30;
            paint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText("Shortage / Remarks: ", xStart + 20, y, paint);
            
            // Footer Text
            y += 40;
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setFakeBoldText(true);
            canvas.drawText("LIVE & LET ALIVE", pageWidth / 2, y, paint);

            pdfDocument.finishPage(page);
            savePdf(pdfDocument, "Delivery_Note_" + System.currentTimeMillis() + ".pdf");
            
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error generating PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void generateCashReceipt() {
         try {
            PdfDocument pdfDocument = new PdfDocument();
            // Using standard A4 width (595), and height approx half (420)
            int pageWidth = 595;
            int pageHeight = 420;
            
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
            PdfDocument.Page page = pdfDocument.startPage(pageInfo);
            
            Canvas canvas = page.getCanvas();
            Paint paint = new Paint();
            Paint titlePaint = new Paint();
            Paint boldPaint = new Paint();
            
            paint.setColor(Color.BLACK);
            paint.setTextSize(12);
            paint.setAntiAlias(true);
            
            titlePaint.setColor(Color.BLUE);
            titlePaint.setTextSize(26);
            titlePaint.setFakeBoldText(true);
            titlePaint.setTextAlign(Paint.Align.CENTER);
            titlePaint.setAntiAlias(true);
            
            boldPaint.setColor(Color.BLACK);
            boldPaint.setTextSize(14);
            boldPaint.setFakeBoldText(true);
            boldPaint.setAntiAlias(true);
            
            int xStart = 50;
            int xEnd = pageWidth - 50;
            int y = 50;
            
            // --- HEADER ---
            // "CASH RECEIPT" Vertical text is hard to do easily here without rotation matrix.
            // Let's put "CASH RECEIPT" as a subtitle or corner text.
            
            // Main Title
            canvas.drawText("SANTHOSH BIKES", pageWidth / 2, y, titlePaint);
            
            y += 20;
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTextSize(11);
            canvas.drawText("No.433, C.T.H. Road, Kavarapalayam, Avadi, Chennai - 600 054.", pageWidth / 2, y, paint);
            y += 15;
            canvas.drawText("(Near Anjaneyar Temple)", pageWidth / 2, y, paint);
            
            // Title Box
            y += 30;
            Paint boxPaint = new Paint();
            boxPaint.setStyle(Paint.Style.STROKE);
            boxPaint.setStrokeWidth(2);
            canvas.drawRect((pageWidth/2) - 60, y - 20, (pageWidth/2) + 60, y + 10, boxPaint);
            
            boldPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("CASH RECEIPT", pageWidth / 2, y, boldPaint);
            
            // No and Date
            y += 40;
            paint.setTextAlign(Paint.Align.LEFT);
            boldPaint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText("No. ", xStart, y, boldPaint);
            canvas.drawText(transactionId.substring(Math.max(0, transactionId.length() - 5)), xStart + 35, y, paint);
            
            String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
            paint.setTextAlign(Paint.Align.RIGHT);
            boldPaint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText("Date: " + currentDate, xEnd, y, boldPaint);
            
            // --- CONTENT LINES ---
            int lineSpacing = 35;
            y += 45;
            
            // Received from
            paint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText("Received with thanks from Shri / Smt.", xStart, y, paint);
            canvas.drawText(customerName, xStart + 220, y, boldPaint);
            canvas.drawLine(xStart + 220, y + 4, xEnd, y + 4, paint);
            
            y += lineSpacing;
            // Sum of Rupees
            canvas.drawText("the sum of Rupees", xStart, y, paint);
            // Amount in words (Placeholder line)
            canvas.drawLine(xStart + 110, y + 4, xEnd, y + 4, paint);
            
            y += lineSpacing;
            // By Cash..
            canvas.drawText("by Cash / Draft / Cheque of", xStart, y, paint);
            canvas.drawLine(xStart + 160, y + 4, xEnd, y + 4, paint);
            
            y += lineSpacing;
            // Payment of
            canvas.drawText("towards the payment of", xStart, y, paint);
            canvas.drawText(vehicleName + " (" + vehicleColor + ")", xStart + 140, y, boldPaint);
            canvas.drawLine(xStart + 140, y + 4, xEnd, y + 4, paint);

            y += lineSpacing;
            // Empty line
            canvas.drawLine(xStart, y + 4, xEnd, y + 4, paint);

            // --- FOOTER ---
            y += 50;
            
            // Amount Box (Total)
            Paint totalBoxPaint = new Paint();
            totalBoxPaint.setStyle(Paint.Style.STROKE);
            totalBoxPaint.setStrokeWidth(2);
            totalBoxPaint.setColor(Color.BLACK);
            
            canvas.drawRect(xStart, y - 25, xStart + 150, y + 15, totalBoxPaint);
            
            Paint amountPaint = new Paint();
            amountPaint.setColor(Color.BLACK);
            amountPaint.setTextSize(18);
            amountPaint.setFakeBoldText(true);
            amountPaint.setAntiAlias(true);
            
            canvas.drawText("Rs. " + vehiclePrice, xStart + 10, y + 5, amountPaint);
            
            // Signature
            boldPaint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText("For SANTHOSH BIKES", xEnd, y, boldPaint);
            
            y += 50;
            paint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText("Authorized Signatory", xEnd, y, paint);
            
            y += 20;
            paint.setTextAlign(Paint.Align.LEFT);
            paint.setTextSize(10);
            paint.setColor(Color.DKGRAY);
            canvas.drawText("Note : Delivery Subject to Availability.", xStart, y, paint);
            y += 15;
            canvas.drawText("Price at time of delivery (Subject to Realization)", xStart, y, paint);

            pdfDocument.finishPage(page);
            savePdf(pdfDocument, "Cash_Receipt_" + System.currentTimeMillis() + ".pdf");
            
        } catch (Exception e) {
            e.printStackTrace();
             Toast.makeText(this, "Error generating PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void savePdf(PdfDocument pdfDocument, String filename) {
        // Use MediaStore for Android 10+ compatibility
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            android.content.ContentValues values = new android.content.ContentValues();
            values.put(android.provider.MediaStore.MediaColumns.DISPLAY_NAME, filename);
            values.put(android.provider.MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
            values.put(android.provider.MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

            android.net.Uri uri = getContentResolver().insert(android.provider.MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);

            try {
                if (uri != null) {
                    java.io.OutputStream outputStream = getContentResolver().openOutputStream(uri);
                    pdfDocument.writeTo(outputStream);
                    if (outputStream != null) outputStream.close();
                    Toast.makeText(this, "PDF Saved to Downloads", Toast.LENGTH_LONG).show();
                    openPdf(uri);
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error saving PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            // Legacy approach
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename);
            try {
                pdfDocument.writeTo(new FileOutputStream(file));
                Toast.makeText(this, "PDF Saved to Downloads: " + filename, Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error saving PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        pdfDocument.close();
    }
    
    private void openPdf(android.net.Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            startActivity(intent);
        } catch (android.content.ActivityNotFoundException e) {
            Toast.makeText(this, "No PDF Viewer found", Toast.LENGTH_SHORT).show();
        }
    }
}
