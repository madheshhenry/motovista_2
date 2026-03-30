package com.example.motovista_deep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DeliveryNoteActivity extends AppCompatActivity {

    // UI Components
    private CardView btnBack, btnShare, btnPrint;
    private TextView tvShopName, tvShopAddress, tvShopContact, tvDeliveryNoteNo, tvDeliveryDate;
    private TextView tvCustomerName, tvFatherName, tvCustomerPhone, tvCustomerAddress;
    private TextView tvVehicleName, tvVehicleColor, tvEngineNo, tvChassisNo, tvRemarks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_note_preview);

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
        tvShopContact = findViewById(R.id.tvShopContact);

        // Delivery note details
        tvDeliveryNoteNo = findViewById(R.id.tvDeliveryNoteNo);
        tvDeliveryDate = findViewById(R.id.tvDeliveryDate);

        // Customer details
        tvCustomerName = findViewById(R.id.tvCustomerName);
        tvFatherName = findViewById(R.id.tvFatherName);
        tvCustomerPhone = findViewById(R.id.tvCustomerPhone);
        tvCustomerAddress = findViewById(R.id.tvCustomerAddress);

        // Vehicle details
        tvVehicleName = findViewById(R.id.tvVehicleName);
        tvVehicleColor = findViewById(R.id.tvVehicleColor);
        tvEngineNo = findViewById(R.id.tvEngineNo);
        tvChassisNo = findViewById(R.id.tvChassisNo);

        // Remarks
        tvRemarks = findViewById(R.id.tvRemarks);

        // Action buttons
        btnShare = findViewById(R.id.btnShare);
        btnPrint = findViewById(R.id.btnPrint);
    }

    private void handleIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            // Get delivery note data
            String deliveryNoteNo = intent.getStringExtra("delivery_note_no");
            String customerName = intent.getStringExtra("customer_name");
            String fatherName = intent.getStringExtra("father_name");
            String customerPhone = intent.getStringExtra("customer_phone");
            String customerAddress = intent.getStringExtra("customer_address");
            String vehicleName = intent.getStringExtra("vehicle_name");
            String vehicleColor = intent.getStringExtra("vehicle_color");
            String engineNo = intent.getStringExtra("engine_no");
            String chassisNo = intent.getStringExtra("chassis_no");
            String remarks = intent.getStringExtra("remarks");

            // Set data to views
            if (deliveryNoteNo != null) {
                tvDeliveryNoteNo.setText(deliveryNoteNo);
            }

            if (customerName != null) {
                tvCustomerName.setText(customerName);
            }

            if (fatherName != null) {
                tvFatherName.setText(fatherName);
            }

            if (customerPhone != null) {
                tvCustomerPhone.setText(customerPhone);
            }

            if (customerAddress != null) {
                tvCustomerAddress.setText(customerAddress);
            }

            if (vehicleName != null) {
                tvVehicleName.setText(vehicleName);
            }

            if (vehicleColor != null) {
                tvVehicleColor.setText(vehicleColor);
            }

            if (engineNo != null) {
                tvEngineNo.setText(engineNo);
            }

            if (chassisNo != null) {
                tvChassisNo.setText(chassisNo);
            }

            if (remarks != null) {
                tvRemarks.setText(remarks);
            }

            // Set current date if not provided
            String date = intent.getStringExtra("date");
            if (date != null) {
                tvDeliveryDate.setText(date);
            } else {
                tvDeliveryDate.setText(getCurrentDate());
            }

            // Shop details can be set from shared preferences or default
            tvShopName.setText("Santhosh Bikes");
            tvShopAddress.setText("123, Gandhi Road, Chennai, 600001");
            tvShopContact.setText("Ph: +91 98765 43210 | support@santhoshbikes.com");
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
                shareDeliveryNote();
            }
        });

        // Print button
        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printDeliveryNote();
            }
        });
    }

    private String getCurrentDate() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            return sdf.format(new Date());
        } catch (Exception e) {
            return "24 Oct 2023";
        }
    }

    private void shareDeliveryNote() {
        // TODO: Implement share functionality
        android.widget.Toast.makeText(this, "Share delivery note functionality", android.widget.Toast.LENGTH_SHORT).show();
    }

    private void printDeliveryNote() {
        // TODO: Implement print functionality
        android.widget.Toast.makeText(this, "Print delivery note functionality", android.widget.Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}