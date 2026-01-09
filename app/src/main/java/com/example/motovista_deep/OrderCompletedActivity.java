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

    private com.example.motovista_deep.helpers.OrderSessionManager sessionManager;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_completed);

        sessionManager = new com.example.motovista_deep.helpers.OrderSessionManager(this);
        // Persist Completed State
        sessionManager.setStep(com.example.motovista_deep.helpers.OrderSessionManager.Step.COMPLETED);

        // Initialize views
        initializeViews();

        // Get data from intent
        handleIntentData();

        // Setup click listeners
        setupClickListeners();

        // Animate entrance
        animateEntrance();
    }

    // ... (Views and Intent Data handling remains same) ...

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
            String customerName = intent.getStringExtra("customer_name");
            String vehicleModel = intent.getStringExtra("vehicle_model");
            String paymentType = intent.getStringExtra("payment_type");

            if (customerName != null) tvCustomerName.setText(customerName);
            if (vehicleModel != null) tvBikeModel.setText(vehicleModel);
            if (paymentType != null) tvPaymentType.setText(paymentType);

            int requestId = intent.getIntExtra("request_id", -1);
            if (requestId == -1 && sessionManager.isSessionActive()) {
                requestId = sessionManager.getRequestId();
            }
            
            String orderType = intent.getStringExtra("order_type");
            if (orderType != null && orderType.equals("Full Cash")) {
                btnEmiLedger.setVisibility(View.GONE);
            }
            
            if (requestId != -1) {
                completeOrder(requestId);
            }
        }
    }
    
    private void completeOrder(int requestId) {
        com.example.motovista_deep.api.ApiService apiService = com.example.motovista_deep.api.RetrofitClient.getApiService();
        com.example.motovista_deep.models.CompleteOrderRequest request = new com.example.motovista_deep.models.CompleteOrderRequest(requestId);
        
        apiService.completeOrder(request).enqueue(new retrofit2.Callback<com.example.motovista_deep.models.GenericResponse>() {
            @Override
            public void onResponse(retrofit2.Call<com.example.motovista_deep.models.GenericResponse> call, retrofit2.Response<com.example.motovista_deep.models.GenericResponse> response) {}
            @Override
            public void onFailure(retrofit2.Call<com.example.motovista_deep.models.GenericResponse> call, Throwable t) {}
        });
    }

    private void setupClickListeners() {
        // Back button -> Dashboard & Clear Session
        btnBack.setOnClickListener(v -> navigateToDashboard());

        // EMI Ledger Button
        btnEmiLedger.setOnClickListener(v -> {
            animateButtonClick(btnEmiLedger);
            showToast("Opening EMI Ledger...");
            // TODO: Implement Intent
        });

        // Back to Dashboard Button -> Dashboard & Clear Session
        btnBackDashboard.setOnClickListener(v -> {
            animateButtonClick(btnBackDashboard);
            navigateToDashboard();
        });
    }

    private void navigateToDashboard() {
        // Clear the session as the order is fully done and user is leaving
        sessionManager.clearSession();

        Intent intent = new Intent(OrderCompletedActivity.this, AdminDashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void animateEntrance() {
        handler.postDelayed(() -> btnEmiLedger.animate().alpha(1f).setDuration(400).start(), 200);
        handler.postDelayed(() -> btnBackDashboard.animate().alpha(1f).setDuration(400).start(), 400);
    }

    private void animateButtonClick(CardView button) {
        button.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100)
                .withEndAction(() -> button.animate().scaleX(1f).scaleY(1f).setDuration(100).start()).start();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        // Phone Back -> Dashboard & Clear Session
        navigateToDashboard();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}