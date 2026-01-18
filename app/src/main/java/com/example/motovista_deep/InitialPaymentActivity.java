package com.example.motovista_deep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class InitialPaymentActivity extends AppCompatActivity {

    // UI Components
    private CardView btnBack, btnConfirmPayment;
    private CardView cardCash, cardGPay, cardPhonePe;
    private View checkboxContainer;
    private ImageView ivCheckmark;

    // TextViews
    private TextView tvVehicleName, tvVehicleDetails, tvOrderReference;
    private TextView tvTotalAmount;

    // ImageViews for checkmarks
    private ImageView ivCashCheck, ivGPayCheck, ivPhonePeCheck;

    // Payment selection variables
    private String selectedPaymentMode = "Cash";
    private boolean isConfirmed = false;

    // Data from previous screen
    private double vehiclePrice = 0;
    private double downPayment = 0;
    private String vehicleName = "";
    private String vehicleDetails = "";
    private double monthlyEMI = 0;
    private int durationMonths = 0;
    private double interestRate = 0;
    private double totalPayable = 0;
    private int requestId = -1;
    private String customerName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_payment);

        // Initialize views
        initializeViews();

        // Get data from intent
        getIntentData();

        // Setup click listeners
        setupClickListeners();

        // Set initial states
        setInitialStates();
    }

    private void initializeViews() {
        // Header
        btnBack = findViewById(R.id.btnBack);

        // TextViews
        tvVehicleName = findViewById(R.id.tvVehicleName);
        tvVehicleDetails = findViewById(R.id.tvVehicleDetails);
        tvOrderReference = findViewById(R.id.tvOrderReference);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);

        // Payment mode cards
        cardCash = findViewById(R.id.cardCash);
        cardGPay = findViewById(R.id.cardGPay);
        cardPhonePe = findViewById(R.id.cardPhonePe);

        // Checkmarks
        ivCashCheck = findViewById(R.id.ivCashCheck);
        ivGPayCheck = findViewById(R.id.ivGPayCheck);
        ivPhonePeCheck = findViewById(R.id.ivPhonePeCheck);

        // Checkbox
        checkboxContainer = findViewById(R.id.checkboxContainer);
        ivCheckmark = findViewById(R.id.ivCheckmark);

        // Confirm button
        btnConfirmPayment = findViewById(R.id.btnConfirmPayment);
    }

    private void getIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            vehiclePrice = intent.getDoubleExtra("vehicle_price", 0);
            downPayment = intent.getDoubleExtra("down_payment", 0);
            vehicleName = intent.getStringExtra("vehicle_name");
            vehicleDetails = intent.getStringExtra("vehicle_details");
            monthlyEMI = intent.getDoubleExtra("monthly_emi", 0);
            durationMonths = intent.getIntExtra("duration_months", 0);
            interestRate = intent.getDoubleExtra("interest_rate", 0);
            totalPayable = intent.getDoubleExtra("total_payable", 0);
            requestId = intent.getIntExtra("request_id", -1);
            customerName = intent.getStringExtra("customer_name");

            // If no specific data, use defaults
            if (vehicleName == null || vehicleName.isEmpty()) {
                vehicleName = "Hero Splendor+";
            }
            if (vehicleDetails == null || vehicleDetails.isEmpty()) {
                vehicleDetails = "Matte Black • Drum Brake";
            }

            // Update UI with data
            updateUIWithData();
        }
    }

    private void updateUIWithData() {
        tvVehicleName.setText(vehicleName);
        tvVehicleDetails.setText(vehicleDetails);

        // Format down payment amount
        String amountText = "₹ " + formatIndianCurrency(downPayment);
        tvTotalAmount.setText(amountText);

        // Update confirmation message
        TextView tvConfirmationMessage = findViewById(R.id.tvConfirmationMessage);
        String message = "I verify that the initial amount of ₹" +
                formatIndianCurrency(downPayment) +
                " has been successfully collected from the customer.";
        tvConfirmationMessage.setText(message);
    }

    private void setInitialStates() {
        // Set Cash as initially selected
        selectPaymentMode("Cash");

        // Initially checkbox is not checked
        setCheckboxState(false);

        // Initially button is disabled
        btnConfirmPayment.setAlpha(0.5f);
        btnConfirmPayment.setClickable(false);
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

        // Payment mode selection
        cardCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPaymentMode("Cash");
            }
        });

        cardGPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPaymentMode("GPay");
            }
        });

        cardPhonePe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPaymentMode("PhonePe");
            }
        });

        // Checkbox click
        checkboxContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleCheckbox();
            }
        });

        // Confirm payment button
        btnConfirmPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmPayment();
            }
        });
    }

    private void selectPaymentMode(String mode) {
        // Reset all checkmarks
        ivCashCheck.setVisibility(View.INVISIBLE);
        ivGPayCheck.setVisibility(View.INVISIBLE);
        ivPhonePeCheck.setVisibility(View.INVISIBLE);

        // Reset all card backgrounds
        cardCash.setCardBackgroundColor(getColor(R.color.white));
        cardGPay.setCardBackgroundColor(getColor(R.color.white));
        cardPhonePe.setCardBackgroundColor(getColor(R.color.white));

        // Reset all text colors
        TextView tvCashLabel = findViewById(R.id.tvCashLabel);
        TextView tvGPayLabel = findViewById(R.id.tvGPayLabel);
        TextView tvPhonePeLabel = findViewById(R.id.tvPhonePeLabel);

        tvCashLabel.setTextColor(getColor(R.color.text_admin));
        tvGPayLabel.setTextColor(getColor(R.color.text_admin));
        tvPhonePeLabel.setTextColor(getColor(R.color.text_admin));

        // Reset all icon colors
        ImageView ivCashIcon = findViewById(R.id.ivCashIcon);
        ImageView ivGPayIcon = findViewById(R.id.ivGPayIcon);
        ImageView ivPhonePeIcon = findViewById(R.id.ivPhonePeIcon);

        ivCashIcon.setColorFilter(getColor(R.color.text_admin));
        ivGPayIcon.setColorFilter(getColor(R.color.text_admin));
        ivPhonePeIcon.setColorFilter(getColor(R.color.text_admin));

        // Set selected mode
        selectedPaymentMode = mode;

        switch (mode) {
            case "Cash":
                ivCashCheck.setVisibility(View.VISIBLE);
                cardCash.setCardBackgroundColor(getColor(R.color.primary_light));
                tvCashLabel.setTextColor(getColor(R.color.text_dark));
                ivCashIcon.setColorFilter(getColor(R.color.primary));
                break;

            case "GPay":
                ivGPayCheck.setVisibility(View.VISIBLE);
                cardGPay.setCardBackgroundColor(getColor(R.color.primary_light));
                tvGPayLabel.setTextColor(getColor(R.color.text_dark));
                ivGPayIcon.setColorFilter(getColor(R.color.primary));
                break;

            case "PhonePe":
                ivPhonePeCheck.setVisibility(View.VISIBLE);
                cardPhonePe.setCardBackgroundColor(getColor(R.color.primary_light));
                tvPhonePeLabel.setTextColor(getColor(R.color.text_dark));
                ivPhonePeIcon.setColorFilter(getColor(R.color.primary));
                break;
        }
    }

    private void toggleCheckbox() {
        isConfirmed = !isConfirmed;
        setCheckboxState(isConfirmed);

        // Enable/disable confirm button based on checkbox
        if (isConfirmed) {
            btnConfirmPayment.setAlpha(1f);
            btnConfirmPayment.setClickable(true);
        } else {
            btnConfirmPayment.setAlpha(0.5f);
            btnConfirmPayment.setClickable(false);
        }
    }

    private void setCheckboxState(boolean checked) {
        if (checked) {
            checkboxContainer.setBackgroundResource(R.drawable.checkbox_background_checked);
            ivCheckmark.setVisibility(View.VISIBLE);
        } else {
            checkboxContainer.setBackgroundResource(R.drawable.checkbox_background);
            ivCheckmark.setVisibility(View.INVISIBLE);
        }
    }

    private void confirmPayment() {
        // Validate
        if (!isConfirmed) {
            Toast.makeText(this, "Please confirm receipt first", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading state
        btnConfirmPayment.setEnabled(false);
        Toast.makeText(this, "Creating EMI Order...", Toast.LENGTH_SHORT).show();

        // Create API Request
        com.example.motovista_deep.models.CreateEmiOrderRequest request = 
            new com.example.motovista_deep.models.CreateEmiOrderRequest(
                requestId,
                customerName != null ? customerName : "Unknown",
                vehicleName,
                totalPayable, // Total amount for ledger (Principal + Interest)
                downPayment, // Paid amount
                monthlyEMI,
                durationMonths,
                interestRate
            );

        // Call API
        com.example.motovista_deep.api.ApiService apiService = com.example.motovista_deep.api.RetrofitClient.getApiService();
        apiService.createEmiOrder(request).enqueue(new retrofit2.Callback<com.example.motovista_deep.models.CreateOrderResponse>() {
            @Override
            public void onResponse(retrofit2.Call<com.example.motovista_deep.models.CreateOrderResponse> call, retrofit2.Response<com.example.motovista_deep.models.CreateOrderResponse> response) {
                btnConfirmPayment.setEnabled(true);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    String ref = response.body().getData().getOrderReference();
                    proceedToConfirmation(ref);
                } else {
                    Toast.makeText(InitialPaymentActivity.this, "Failed to create EMI Order: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.example.motovista_deep.models.CreateOrderResponse> call, Throwable t) {
                btnConfirmPayment.setEnabled(true);
                Toast.makeText(InitialPaymentActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void proceedToConfirmation(String transactionId) {
        // Navigate to Payment Confirmed screen
        Intent intent = new Intent(this, PaymentConfirmedActivity.class);

        // Pass all necessary data
        intent.putExtra("customer_name", customerName);
        intent.putExtra("vehicle_model", vehicleName);
        intent.putExtra("payment_mode", selectedPaymentMode);
        intent.putExtra("amount_paid", downPayment);
        intent.putExtra("transaction_id", transactionId);
        intent.putExtra("order_type", "EMI");

        // Pass additional data if needed
        intent.putExtra("vehicle_price", vehiclePrice);
        intent.putExtra("vehicle_details", vehicleDetails);
        intent.putExtra("request_id", requestId); // Pass ID forward

        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish(); // Finish this activity so user can't go back
    }

    private String formatIndianCurrency(double amount) {
        try {
            // Simple formatting
            if (amount >= 100000) {
                return String.format("%.1fL", amount / 100000);
            } else if (amount >= 1000) {
                return String.format("%.1fK", amount / 1000);
            } else {
                return String.format("%.0f", amount);
            }
        } catch (Exception e) {
            return "0";
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}