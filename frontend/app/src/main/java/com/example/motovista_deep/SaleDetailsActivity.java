package com.example.motovista_deep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Color;
import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;

public class SaleDetailsActivity extends AppCompatActivity {

    private ImageView btnBack, btnCallCustomer;
    private ImageView btnViewInvoice, btnDownloadInvoice;
    private ImageView btnViewDeliveryNote, btnDownloadDeliveryNote;

    private TextView tvBikeModel, tvBikeColor;
    private TextView tvEngineNumber, tvChassisNumber;
    private TextView tvCustomerName, tvCustomerPhone, tvCustomerAddress;
    private TextView tvSaleDate, tvPaymentType, tvDownPayment, tvTotalValue;
    private TextView tvInvoiceNumber, tvDeliveryNote;
    private ImageView ivBike;
    private MaterialCardView cardColorChip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_details);

        // Initialize views
        initializeViews();

        // Setup click listeners
        setupClickListeners();

        // Get data from intent
        getDataFromIntent();
    }

    private void initializeViews() {
        // Header
        btnBack = findViewById(R.id.btnBack);

        // Bike Details
        tvBikeModel = findViewById(R.id.tvBikeModel);
        tvBikeColor = findViewById(R.id.tvBikeColor);
        tvEngineNumber = findViewById(R.id.tvEngineNumber);
        tvChassisNumber = findViewById(R.id.tvChassisNumber);

        // Customer Details
        tvCustomerName = findViewById(R.id.tvCustomerName);
        tvCustomerPhone = findViewById(R.id.tvCustomerPhone);
        tvCustomerAddress = findViewById(R.id.tvCustomerAddress);
        btnCallCustomer = findViewById(R.id.btnCallCustomer);

        // Sale & Payment Details
        tvSaleDate = findViewById(R.id.tvSaleDate);
        tvPaymentType = findViewById(R.id.tvPaymentType);
        tvDownPayment = findViewById(R.id.tvDownPayment);
        tvTotalValue = findViewById(R.id.tvTotalValue);

        // Document Details
        tvInvoiceNumber = findViewById(R.id.tvInvoiceNumber);
        tvDeliveryNote = findViewById(R.id.tvDeliveryNote);
        btnViewInvoice = findViewById(R.id.btnViewInvoice);
        btnDownloadInvoice = findViewById(R.id.btnDownloadInvoice);
        btnViewDeliveryNote = findViewById(R.id.btnViewDeliveryNote);
        btnDownloadDeliveryNote = findViewById(R.id.btnDownloadDeliveryNote);

        // Bike Image & Color
        ivBike = findViewById(R.id.ivBike);
        cardColorChip = findViewById(R.id.cardColorChip);
    }

    private void setupClickListeners() {
        // Back button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Call Customer button
        btnCallCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = tvCustomerPhone.getText().toString();
                makePhoneCall(phoneNumber);
            }
        });

        // Document buttons
        btnViewInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SaleDetailsActivity.this, "Viewing Invoice", Toast.LENGTH_SHORT).show();
                // TODO: Implement PDF viewer for invoice
            }
        });

        btnDownloadInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SaleDetailsActivity.this, "Downloading Invoice", Toast.LENGTH_SHORT).show();
                // TODO: Implement download functionality
            }
        });

        btnViewDeliveryNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SaleDetailsActivity.this, "Viewing Delivery Note", Toast.LENGTH_SHORT).show();
                // TODO: Implement PDF viewer for delivery note
            }
        });

        btnDownloadDeliveryNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SaleDetailsActivity.this, "Downloading Delivery Note", Toast.LENGTH_SHORT).show();
                // TODO: Implement download functionality
            }
        });
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            // Bike Details
            String bikeName = intent.getStringExtra("BIKE_NAME");
            String bikeColor = intent.getStringExtra("BIKE_COLOR");
            String colorHex = intent.getStringExtra("BIKE_COLOR_HEX");
            String imageUrl = intent.getStringExtra("BIKE_IMAGE");
            String engineNumber = intent.getStringExtra("ENGINE_NUMBER");
            String chassisNumber = intent.getStringExtra("CHASSIS_NUMBER");

            // Customer Details
            String customerName = intent.getStringExtra("CUSTOMER_NAME");
            String customerPhone = intent.getStringExtra("CUSTOMER_PHONE");
            String customerAddress = intent.getStringExtra("CUSTOMER_ADDRESS");

            // Sale Details
            String saleDate = intent.getStringExtra("SALE_DATE");
            String paymentType = intent.getStringExtra("PAYMENT_TYPE");
            String downPayment = intent.getStringExtra("DOWN_PAYMENT");
            String totalValue = intent.getStringExtra("TOTAL_VALUE");

            // Documents
            String invoiceNumber = intent.getStringExtra("INVOICE_NUMBER");
            String deliveryNote = intent.getStringExtra("DELIVERY_NOTE");

            // Set data to views if available
            if (bikeName != null) tvBikeModel.setText(bikeName);

            // Handle Bike Color Name (Strip JSON or Pipe if needed)
            if (bikeColor != null) {
                if (bikeColor.contains("|")) {
                    String[] parts = bikeColor.split("\\|");
                    bikeColor = parts[0].trim();
                    if (colorHex == null || colorHex.isEmpty() || colorHex.equals("#808080")) {
                         colorHex = parts[1].trim();
                    }
                }
                
                if (bikeColor.startsWith("[\"") || bikeColor.contains("\"")) {
                    // It's a JSON string fragment
                    bikeColor = bikeColor.replace("[", "").replace("]", "").replace("\"", "").trim();
                    if (bikeColor.contains(",")) {
                        String[] parts = bikeColor.split(",");
                        bikeColor = parts[0].trim();
                        if (colorHex == null || colorHex.isEmpty() || colorHex.equals("#808080")) {
                             colorHex = parts[1].trim();
                        }
                    }
                }
                tvBikeColor.setText(bikeColor);
            }

            // Set Color Chip
            if (colorHex != null && !colorHex.isEmpty()) {
                try {
                    // Ensure hex code starts with #
                    if (!colorHex.startsWith("#") && colorHex.matches("^[0-9a-fA-F]{6}$")) {
                        colorHex = "#" + colorHex;
                    }
                    cardColorChip.setCardBackgroundColor(Color.parseColor(colorHex));
                    cardColorChip.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    cardColorChip.setVisibility(View.GONE);
                }
            } else {
                cardColorChip.setVisibility(View.GONE);
            }

            // Load Bike Image
            if (imageUrl != null && !imageUrl.isEmpty()) {
                // Construct the absolute image URL based on RetrofitClient's BASE_URL
                String finalUrl = imageUrl;
                if (!imageUrl.startsWith("http")) {
                    // Use the dynamic BASE_URL from RetrofitClient
                    String baseUrl = com.example.motovista_deep.api.RetrofitClient.BASE_URL;
                    
                    // Calculation: If BASE_URL is ".../motovista_backend/api/", 
                    // then backendRoot is ".../motovista_backend/" 
                    String backendRoot = baseUrl;
                    if (baseUrl.endsWith("api/")) {
                        backendRoot = baseUrl.substring(0, baseUrl.length() - 4);
                    } else if (baseUrl.endsWith("api")) {
                        backendRoot = baseUrl.substring(0, baseUrl.length() - 3);
                    }
                    
                    // Usually images are stored in "uploads/bikes/" if the path in DB is just a filename
                    // If the path in the database already includes "uploads/", append it directly to root
                    if (!imageUrl.contains("uploads/")) {
                        finalUrl = backendRoot + "uploads/bikes/" + imageUrl;
                    } else {
                        // Ensure no double slashes if backendRoot already has a trailing slash
                        if (backendRoot.endsWith("/") && imageUrl.startsWith("/")) {
                            finalUrl = backendRoot + imageUrl.substring(1);
                        } else if (!backendRoot.endsWith("/") && !imageUrl.startsWith("/")) {
                            finalUrl = backendRoot + "/" + imageUrl;
                        } else {
                            finalUrl = backendRoot + imageUrl;
                        }
                    }
                }
                
                Glide.with(this)
                        .load(finalUrl)
                        .placeholder(R.drawable.ic_bike_placeholder)
                        .error(R.drawable.ic_bike_placeholder)
                        .into(ivBike);
            }

            if (engineNumber != null) tvEngineNumber.setText(engineNumber);
            if (chassisNumber != null) tvChassisNumber.setText(chassisNumber);

            if (customerName != null) tvCustomerName.setText(customerName);
            if (customerPhone != null) tvCustomerPhone.setText(customerPhone);
            if (customerAddress != null) tvCustomerAddress.setText(customerAddress);

            if (saleDate != null) tvSaleDate.setText(saleDate);
            if (paymentType != null) tvPaymentType.setText(paymentType);
            if (downPayment != null) tvDownPayment.setText(downPayment);
            if (totalValue != null) tvTotalValue.setText(totalValue);

            if (invoiceNumber != null) tvInvoiceNumber.setText(invoiceNumber);
            if (deliveryNote != null) tvDeliveryNote.setText(deliveryNote);
        }
    }

    private void makePhoneCall(String phoneNumber) {
        try {
            // Remove any non-digit characters except +
            phoneNumber = phoneNumber.replaceAll("[^+0-9]", "");

            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Cannot make call", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}