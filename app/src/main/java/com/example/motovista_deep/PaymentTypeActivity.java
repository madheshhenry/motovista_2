package com.example.motovista_deep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PaymentTypeActivity extends AppCompatActivity {

    // UI Components
    private CardView btnBack, btnNext;
    private CardView cardFullCash, cardPrivateEMI;
    private ImageView ivFullCashSelected, ivPrivateEMISelected;
    private ImageView iconFullCash, iconPrivateEMI;

    // State variables
    private String selectedPaymentType = "full_cash"; // Default selection

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_type);

        Toast.makeText(this, "Payment Type Screen", Toast.LENGTH_SHORT).show();

        // Initialize views
        initializeViews();

        // Get data from intent
        handleIntentData();

        // Setup payment type selection
        setupPaymentSelection();

        // Setup click listeners
        setupClickListeners();
    }

    private void initializeViews() {
        // Header
        btnBack = findViewById(R.id.btnBack);

        // Payment cards
        cardFullCash = findViewById(R.id.cardFullCash);
        cardPrivateEMI = findViewById(R.id.cardPrivateEMI);

        // Selection indicators
        ivFullCashSelected = findViewById(R.id.ivFullCashSelected);
        ivPrivateEMISelected = findViewById(R.id.ivPrivateEMISelected);

        // Icons
        iconFullCash = findViewById(R.id.iconFullCash);
        iconPrivateEMI = findViewById(R.id.iconPrivateEMI);

        // Footer
        btnNext = findViewById(R.id.btnNext);
    }

    private void handleIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            String testData = intent.getStringExtra("test");
            if (testData != null) {
                Toast.makeText(this, "Data: " + testData, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupPaymentSelection() {
        // Set initial selection
        updateSelectionUI();

        // Full Cash payment selection
        cardFullCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPaymentType = "full_cash";
                updateSelectionUI();
                Toast.makeText(PaymentTypeActivity.this, "Full Cash selected", Toast.LENGTH_SHORT).show();
            }
        });

        // Private EMI selection
        cardPrivateEMI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPaymentType = "private_emi";
                updateSelectionUI();
                Toast.makeText(PaymentTypeActivity.this, "Private EMI selected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateSelectionUI() {
        if (selectedPaymentType.equals("full_cash")) {
            // Full Cash selected
            cardFullCash.setCardBackgroundColor(getResources().getColor(R.color.primary_light));
            cardFullCash.setBackgroundResource(R.drawable.card_selected_bg);

            ivFullCashSelected.setImageResource(R.drawable.ic_check_circle);
            ivFullCashSelected.setColorFilter(getResources().getColor(R.color.primary));
            iconFullCash.setColorFilter(getResources().getColor(R.color.primary));

            // Private EMI unselected
            cardPrivateEMI.setCardBackgroundColor(getResources().getColor(R.color.card_bg));
            cardPrivateEMI.setBackgroundResource(0); // Remove border

            ivPrivateEMISelected.setImageResource(R.drawable.ic_radio_unchecked);
            ivPrivateEMISelected.setColorFilter(getResources().getColor(R.color.gray_400));
            iconPrivateEMI.setColorFilter(getResources().getColor(R.color.text_dark));

        } else if (selectedPaymentType.equals("private_emi")) {
            // Private EMI selected
            cardPrivateEMI.setCardBackgroundColor(getResources().getColor(R.color.primary_light));

            // Create border programmatically
            GradientDrawable border = new GradientDrawable();
            border.setColor(getResources().getColor(R.color.primary_light));
            border.setStroke(2, getResources().getColor(R.color.primary));
            border.setCornerRadius(dpToPx(12));
            cardPrivateEMI.setBackground(border);

            ivPrivateEMISelected.setImageResource(R.drawable.ic_check_circle);
            ivPrivateEMISelected.setColorFilter(getResources().getColor(R.color.primary));
            iconPrivateEMI.setColorFilter(getResources().getColor(R.color.primary));

            // Full Cash unselected
            cardFullCash.setCardBackgroundColor(getResources().getColor(R.color.card_bg));
            cardFullCash.setBackgroundResource(0); // Remove border

            ivFullCashSelected.setImageResource(R.drawable.ic_radio_unchecked);
            ivFullCashSelected.setColorFilter(getResources().getColor(R.color.gray_400));
            iconFullCash.setColorFilter(getResources().getColor(R.color.text_dark));
        }
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
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

        // Next button
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPaymentType.equals("full_cash")) {
                    // Navigate to Cash Payment screen
                    Intent intent = new Intent(PaymentTypeActivity.this, CashPaymentActivity.class);

                    // Pass vehicle data (you can get this from previous screens)
                    // TODO: Replace with actual data from your app
                    intent.putExtra("vehicle_model", "Royal Enfield Classic 350");
                    intent.putExtra("vehicle_price", 150000.00);

                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                } else if (selectedPaymentType.equals("private_emi")) {
                    // Navigate to EMI Setup screen
                    Intent intent = new Intent(PaymentTypeActivity.this, EMISetupActivity.class);

                    // Pass vehicle data
                    // TODO: Replace with actual data from your app
                    intent.putExtra("vehicle_model", "Royal Enfield Classic 350");
                    intent.putExtra("vehicle_price", 140000.00);

                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}