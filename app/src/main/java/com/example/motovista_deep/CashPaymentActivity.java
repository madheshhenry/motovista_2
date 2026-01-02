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
    private double totalAmount = 150000;
    private String selectedPaymentMode = "Cash";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_payment);

        Toast.makeText(this, "Cash Payment Screen", Toast.LENGTH_SHORT).show();

        // Initialize views
        initializeViews();

        // Get data from intent
        handleIntentData();

        // Setup payment options
        setupPaymentOptions();

        // Setup click listeners
        setupClickListeners();

        // Update UI with data
        updateUI();
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

    private void handleIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            // Get vehicle data
            String vehicleModel = intent.getStringExtra("vehicle_model");
            double vehiclePrice = intent.getDoubleExtra("vehicle_price", 150000);

            if (vehicleModel != null) {
                tvVehicleModel.setText(vehicleModel);
            }

            if (vehiclePrice > 0) {
                totalAmount = vehiclePrice;
            }
        }
    }

    private void setupPaymentOptions() {
        // Cash option click
        optionCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPaymentMode("Cash");
            }
        });

        // GPay option click
        optionGPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPaymentMode("GPay");
            }
        });

        // PhonePe option click
        optionPhonePe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPaymentMode("PhonePe");
            }
        });
    }

    private void selectPaymentMode(String mode) {
        selectedPaymentMode = mode;

        // Reset all backgrounds and checkmarks
        optionCash.setBackgroundResource(R.drawable.payment_option_bg);
        optionGPay.setBackgroundResource(R.drawable.payment_option_bg);
        optionPhonePe.setBackgroundResource(R.drawable.payment_option_bg);

        checkCash.setVisibility(View.INVISIBLE);
        checkGPay.setVisibility(View.INVISIBLE);
        checkPhonePe.setVisibility(View.INVISIBLE);

        // Update selected option
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
        // Format currency
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        String formattedAmount = formatter.format(totalAmount).replace(".00", "");

        // Update price displays
        tvTotalPrice.setText(formattedAmount);
        tvAmountToPay.setText(formattedAmount);

        // Update confirmation text
        String confirmationText = "I certify that the payment of " + formattedAmount +
                " has been physically received from the customer.";
        tvConfirmationText.setText(confirmationText);
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

        // Confirm Payment button
        // In CashPaymentActivity.java, modify the btnConfirmPayment click listener:

        btnConfirmPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cbPaymentConfirm.isChecked()) {
                    Toast.makeText(CashPaymentActivity.this,
                            "Please confirm payment receipt",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // Navigate to Payment Confirmed screen
                Intent intent = new Intent(CashPaymentActivity.this, PaymentConfirmedActivity.class);
                intent.putExtra("customer_name", "Rahul Sharma"); // Get from your data
                intent.putExtra("vehicle_model", tvVehicleModel.getText().toString());
                intent.putExtra("payment_mode", selectedPaymentMode);
                intent.putExtra("amount_paid", totalAmount);
                intent.putExtra("transaction_id", "#TXN-" + System.currentTimeMillis());
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        // Checkbox listener
        cbPaymentConfirm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Enable/disable confirm button based on checkbox
                btnConfirmPayment.setEnabled(isChecked);
                btnConfirmPayment.setAlpha(isChecked ? 1.0f : 0.5f);
            }
        });
    }

    private String formatIndianCurrency(double amount) {
        try {
            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
            return formatter.format(amount).replace(".00", "");
        } catch (Exception e) {
            return "₹0";
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}