package com.example.motovista_deep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class OrderCompletedActivity extends AppCompatActivity {

    // UI Components
    private CardView btnBack, btnEmiLedger, btnBackDashboard;
    private TextView tvCustomerName, tvBikeModel, tvPaymentType;

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_completed);

        // Initialize views
        initializeViews();

        // Get data from intent
        handleIntentData();

        // Setup click listeners
        setupClickListeners();

        // Animate entrance
        animateEntrance();
    }

    private void initializeViews() {
        // Header
        btnBack = findViewById(R.id.btnBack);

        // Text views
        tvCustomerName = findViewById(R.id.tvCustomerName);
        tvBikeModel = findViewById(R.id.tvBikeModel);
        tvPaymentType = findViewById(R.id.tvPaymentType);

        // Action buttons
        btnEmiLedger = findViewById(R.id.btnEmiLedger);
        btnBackDashboard = findViewById(R.id.btnBackDashboard);

        // Set initial alpha for animations
        btnEmiLedger.setAlpha(0f);
        btnBackDashboard.setAlpha(0f);
    }

    private void handleIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            // Get data from AdminDocumentsActivity
            String customerName = intent.getStringExtra("customer_name");
            String vehicleModel = intent.getStringExtra("vehicle_model");
            String paymentType = intent.getStringExtra("payment_type");

            // Update UI with received data
            if (customerName != null && !customerName.isEmpty()) {
                tvCustomerName.setText(customerName);
            }

            if (vehicleModel != null && !vehicleModel.isEmpty()) {
                tvBikeModel.setText(vehicleModel);
            }

            if (paymentType != null && !paymentType.isEmpty()) {
                tvPaymentType.setText(paymentType);
            }

            // You can add more data handling as needed
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

        // EMI Ledger Button
        btnEmiLedger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButtonClick(btnEmiLedger);
                showToast("Opening EMI Ledger...");

                // TODO: Implement EMI Ledger functionality
                // Intent intent = new Intent(OrderCompletedActivity.this, EmiLedgerActivity.class);
                // startActivity(intent);
            }
        });

        // Back to Dashboard Button
        btnBackDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButtonClick(btnBackDashboard);

                // Navigate back to Admin Dashboard
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(OrderCompletedActivity.this, AdminDashboardActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                }, 150);
            }
        });
    }

    private void animateEntrance() {
        // Animate buttons entrance with delay
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                btnEmiLedger.animate()
                        .alpha(1f)
                        .setDuration(400)
                        .start();
            }
        }, 200);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                btnBackDashboard.animate()
                        .alpha(1f)
                        .setDuration(400)
                        .start();
            }
        }, 400);
    }

    private void animateButtonClick(CardView button) {
        button.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(100)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        button.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(100)
                                .start();
                    }
                })
                .start();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}