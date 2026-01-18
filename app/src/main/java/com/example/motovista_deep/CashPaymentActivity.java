package com.example.motovista_deep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.Locale;

public class CashPaymentActivity extends AppCompatActivity {

    // UI Components
    private CardView btnBack, btnConfirmPayment;
    private LinearLayout optionCash, optionGPay, optionPhonePe;
    private ImageView checkCash, checkGPay, checkPhonePe;
    private CheckBox cbPaymentConfirm;
    private TextView tvVehicleModel, tvTotalPrice, tvAmountToPay, tvConfirmationText;

    // Payment data
    private int requestId = -1;
    private String customerName;
    private double totalAmount = 0;
    private String selectedPaymentMode = "Cash";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_payment);

        // Initialize views
        initializeViews();

        // Get data from intent
        Intent intent = getIntent();
        if (intent != null) {
            requestId = intent.getIntExtra("request_id", -1);
        }

        if (requestId != -1) {
            fetchPaymentDetails(requestId);
        } else {
             Toast.makeText(this, "Error: Invalid Request ID", Toast.LENGTH_SHORT).show();
             tvVehicleModel.setText("Error loading data");
        }

        // Setup payment options
        setupPaymentOptions();

        // Setup click listeners
        setupClickListeners();
    }

    private void initializeViews() {
        // Header
        btnBack = findViewById(R.id.btnBack);

        // Payment options
        optionCash = findViewById(R.id.optionCash);
        optionGPay = findViewById(R.id.optionGPay);
        optionPhonePe = findViewById(R.id.optionPhonePe);

        // Check marks
        checkCash = findViewById(R.id.checkCash);
        checkGPay = findViewById(R.id.checkGPay);
        checkPhonePe = findViewById(R.id.checkPhonePe);

        // Checkbox
        cbPaymentConfirm = findViewById(R.id.cbPaymentConfirm);

        // Text views
        tvVehicleModel = findViewById(R.id.tvVehicleModel);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        tvAmountToPay = findViewById(R.id.tvAmountToPay);
        tvConfirmationText = findViewById(R.id.tvConfirmationText);

        // Footer button
        btnConfirmPayment = findViewById(R.id.btnConfirmPayment);
    }

    private void fetchPaymentDetails(int id) {
        com.example.motovista_deep.api.ApiService apiService = com.example.motovista_deep.api.RetrofitClient.getApiService();
        retrofit2.Call<com.example.motovista_deep.models.GetOrderSummaryResponse> call = apiService.getOrderSummary(id);

        call.enqueue(new retrofit2.Callback<com.example.motovista_deep.models.GetOrderSummaryResponse>() {
            @Override
            public void onResponse(retrofit2.Call<com.example.motovista_deep.models.GetOrderSummaryResponse> call, retrofit2.Response<com.example.motovista_deep.models.GetOrderSummaryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess() && response.body().getData() != null) {
                        com.example.motovista_deep.models.OrderSummaryData data = response.body().getData();
                        
                        // Populate UI
                        String brand = data.getBrand() != null ? data.getBrand() : "";
                        String model = data.getBikeName() != null ? data.getBikeName() : "";
                        String vehicleFullName = brand + " " + model;
                        tvVehicleModel.setText(vehicleFullName);
                        
                        // Price
                        String priceStr = data.getOnRoadPrice(); // e.g., "1,50,000" or raw number
                        // Remove non-numeric characters for parsing if needed
                        if (priceStr != null) {
                           priceStr = priceStr.replaceAll("[^\\d.]", "");
                           try {
                               totalAmount = Double.parseDouble(priceStr);
                               updateUI();
                           } catch (NumberFormatException e) {
                               totalAmount = 0;
                           }
                        }
                        
                        customerName = data.getCustomerName();
                        
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.example.motovista_deep.models.GetOrderSummaryResponse> call, Throwable t) {
                Toast.makeText(CashPaymentActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupPaymentOptions() {
        // ... (Same as before) ...
        optionCash.setOnClickListener(v -> selectPaymentMode("Cash"));
        optionGPay.setOnClickListener(v -> selectPaymentMode("GPay"));
        optionPhonePe.setOnClickListener(v -> selectPaymentMode("PhonePe"));
    }
    
    // ... (selectPaymentMode stays same) ...

    private void selectPaymentMode(String mode) {
        selectedPaymentMode = mode;
        
        optionCash.setBackgroundResource(R.drawable.payment_option_bg);
        optionGPay.setBackgroundResource(R.drawable.payment_option_bg);
        optionPhonePe.setBackgroundResource(R.drawable.payment_option_bg);

        checkCash.setVisibility(View.INVISIBLE);
        checkGPay.setVisibility(View.INVISIBLE);
        checkPhonePe.setVisibility(View.INVISIBLE);

        switch (mode) {
            case "Cash":
                optionCash.setBackgroundResource(R.drawable.payment_option_selected_bg);
                checkCash.setVisibility(View.VISIBLE);
                break;
            case "GPay":
                optionGPay.setBackgroundResource(R.drawable.payment_option_selected_bg);
                checkGPay.setVisibility(View.VISIBLE);
                break;
            case "PhonePe":
                optionPhonePe.setBackgroundResource(R.drawable.payment_option_selected_bg);
                checkPhonePe.setVisibility(View.VISIBLE);
                break;
        }
        Toast.makeText(this, mode + " selected", Toast.LENGTH_SHORT).show();
    }

    private void updateUI() {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        String formattedAmount = formatter.format(totalAmount).replace(".00", "");

        tvTotalPrice.setText(formattedAmount);
        tvAmountToPay.setText(formattedAmount);

        String confirmationText = "I certify that the payment of " + formattedAmount +
                " has been physically received from the customer.";
        tvConfirmationText.setText(confirmationText);
    }

    private void setupClickListeners() {
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }

        if (btnConfirmPayment != null) {
            btnConfirmPayment.setOnClickListener(v -> {
                if (!cbPaymentConfirm.isChecked()) {
                    Toast.makeText(CashPaymentActivity.this, "Please confirm payment receipt", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Persist State
                com.example.motovista_deep.managers.WorkflowManager.updateStage(CashPaymentActivity.this, "PAYMENT_CONFIRMED", requestId, new com.example.motovista_deep.managers.WorkflowManager.WorkflowCallback() {
                    @Override
                    public void onSuccess() {
                        Intent intent = new Intent(CashPaymentActivity.this, PaymentConfirmedActivity.class);
                        intent.putExtra("customer_name", customerName);
                        intent.putExtra("vehicle_model", tvVehicleModel.getText().toString());
                        intent.putExtra("payment_mode", selectedPaymentMode);
                        intent.putExtra("amount_paid", totalAmount);
                        intent.putExtra("request_id", requestId);
                        intent.putExtra("transaction_id", "#TXN-" + System.currentTimeMillis());
                        intent.putExtra("order_type", "Full Cash");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                    
                    @Override
                    public void onError(String message) {
                        Toast.makeText(CashPaymentActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                    }
                });
            });
        }

        cbPaymentConfirm.setOnCheckedChangeListener((buttonView, isChecked) -> {
            btnConfirmPayment.setEnabled(isChecked);
            btnConfirmPayment.setAlpha(isChecked ? 1.0f : 0.5f);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}