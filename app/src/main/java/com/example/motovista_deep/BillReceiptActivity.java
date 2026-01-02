package com.example.motovista_deep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BillReceiptActivity extends AppCompatActivity {

    // UI Components
    private CardView btnBack, btnShare, btnPrint;
    private TextView tvShopName, tvShopAddress, tvShopPhone, tvReceiptNumber, tvReceiptDate;
    private TextView tvCustomerName, tvAmount, tvAmountWords, tvPaymentMode, tvPurpose, tvVehicleModel;
    private ImageView ivSignature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_receipt_preview);

        // Initialize views
        initializeViews();

        // Get data from intent
        handleIntentData();

        // Setup click listeners
        setupClickListeners();
    }

    private void initializeViews() {
        // Header
        btnBack = findViewById(R.id.btnBack);

        // Shop details
        tvShopName = findViewById(R.id.tvShopName);
        tvShopAddress = findViewById(R.id.tvShopAddress);
        tvShopPhone = findViewById(R.id.tvShopPhone);

        // Receipt details
        tvReceiptNumber = findViewById(R.id.tvReceiptNumber);
        tvReceiptDate = findViewById(R.id.tvReceiptDate);

        // Customer details
        tvCustomerName = findViewById(R.id.tvCustomerName);
        tvAmount = findViewById(R.id.tvAmount);
        tvAmountWords = findViewById(R.id.tvAmountWords);
        tvPaymentMode = findViewById(R.id.tvPaymentMode);
        tvPurpose = findViewById(R.id.tvPurpose);
        tvVehicleModel = findViewById(R.id.tvVehicleModel);

        // Signature
        ivSignature = findViewById(R.id.ivSignature);

        // Action buttons
        btnShare = findViewById(R.id.btnShare);
        btnPrint = findViewById(R.id.btnPrint);
    }

    private void handleIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            // Get receipt data
            String receiptNumber = intent.getStringExtra("receipt_number");
            String customerName = intent.getStringExtra("customer_name");
            double amount = intent.getDoubleExtra("amount", 50000);
            String paymentMode = intent.getStringExtra("payment_mode");
            String purpose = intent.getStringExtra("purpose");
            String vehicleModel = intent.getStringExtra("vehicle_model");

            // Set data to views
            if (receiptNumber != null) {
                tvReceiptNumber.setText(receiptNumber);
            }

            if (customerName != null) {
                tvCustomerName.setText(customerName);
            }

            // Format and set amount
            tvAmount.setText(formatIndianCurrency(amount));
            tvAmountWords.setText("(" + convertToWords(amount) + ")");

            if (paymentMode != null) {
                tvPaymentMode.setText(paymentMode);
            }

            if (purpose != null) {
                tvPurpose.setText(purpose);
            }

            if (vehicleModel != null) {
                tvVehicleModel.setText(vehicleModel);
            }

            // Set current date if not provided
            String date = intent.getStringExtra("date");
            if (date != null) {
                tvReceiptDate.setText(date);
            } else {
                tvReceiptDate.setText(getCurrentDate());
            }

            // Shop details can be set from shared preferences or default
            // For now, using hardcoded values
            tvShopName.setText("Santhosh Bikes");
            tvShopAddress.setText("123, Main Street, Chennai, TN");
            tvShopPhone.setText("+91 98765 43210");
        }
    }

    private void setupClickListeners() {
        // Back button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        // Share button
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareReceipt();
            }
        });

        // Print button
        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printReceipt();
            }
        });
    }

    private String formatIndianCurrency(double amount) {
        try {
            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
            String formatted = formatter.format(amount);
            return formatted.replace(".00", "");
        } catch (Exception e) {
            return "₹0";
        }
    }

    private String getCurrentDate() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            return sdf.format(new Date());
        } catch (Exception e) {
            return "24 Oct 2023";
        }
    }

    private String convertToWords(double amount) {
        // Simple conversion for demo
        // You can implement proper number to words conversion here
        if (amount == 50000) {
            return "Fifty Thousand Rupees Only";
        } else if (amount == 25000) {
            return "Twenty Five Thousand Rupees Only";
        } else {
            return "Rupees Only";
        }
    }

    private void shareReceipt() {
        // TODO: Implement share functionality
        // Create a bitmap of the receipt and share it
        android.widget.Toast.makeText(this, "Share receipt functionality", android.widget.Toast.LENGTH_SHORT).show();
    }

    private void printReceipt() {
        // TODO: Implement print functionality
        android.widget.Toast.makeText(this, "Print receipt functionality", android.widget.Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}