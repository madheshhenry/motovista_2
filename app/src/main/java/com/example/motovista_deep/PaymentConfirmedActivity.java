package com.example.motovista_deep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.Locale;

public class PaymentConfirmedActivity extends AppCompatActivity {

    // UI Components
    private CardView btnBack, btnProceed;
    private TextView tvCustomerName, tvModel, tvPaymentMode, tvAmountPaid, tvTransactionId;
    private View outerGlow;

    // Animation
    private Handler handler = new Handler();
    private Runnable pulseRunnable;
    private boolean isPulsing = false;

    // Data
    private int requestId = -1;
    private String orderType = "Full Cash";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_confirmed);

        // Initialize views
        initializeViews();

        // Get data from intent
        handleIntentData();

        // Setup animations
        setupAnimations();

        // Setup click listeners
        setupClickListeners();

        // Start animations
        startAnimations();
    }

    private void initializeViews() {
        // Header
        btnBack = findViewById(R.id.btnBack);

        // Success icon
        outerGlow = findViewById(R.id.outerGlow);

        // Text views
        tvCustomerName = findViewById(R.id.tvCustomerName);
        tvModel = findViewById(R.id.tvModel);
        tvPaymentMode = findViewById(R.id.tvPaymentMode);
        tvAmountPaid = findViewById(R.id.tvAmountPaid);
        tvTransactionId = findViewById(R.id.tvTransactionId);

        // Footer button
        btnProceed = findViewById(R.id.btnProceed);
    }

    private void handleIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            requestId = intent.getIntExtra("request_id", -1);
            
            // Get customer data
            String customerName = intent.getStringExtra("customer_name");
            String vehicleModel = intent.getStringExtra("vehicle_model");
            String paymentMode = intent.getStringExtra("payment_mode");
            double amountPaid = intent.getDoubleExtra("amount_paid", 25000);
            String transactionId = intent.getStringExtra("transaction_id");
            if (intent.hasExtra("order_type")) {
                orderType = intent.getStringExtra("order_type");
            }

            // Set data to views
            if (customerName != null) {
                tvCustomerName.setText(customerName);
            }

            if (vehicleModel != null) {
                tvModel.setText(vehicleModel);
            }

            if (paymentMode != null) {
                tvPaymentMode.setText(paymentMode);
            }

            // Format and set amount
            tvAmountPaid.setText(formatIndianCurrency(amountPaid));

            if (transactionId != null) {
                tvTransactionId.setText("Transaction ID: " + transactionId);
            }
        }
    }

    private void setupAnimations() {
        // Create pulse animation runnable
        pulseRunnable = new Runnable() {
            @Override
            public void run() {
                if (isPulsing) {
                    animateGlow();
                    handler.postDelayed(this, 1500); // Repeat every 1.5 seconds
                }
            }
        };
    }

    private void startAnimations() {
        // Start success icon animation
        animateSuccessIcon();

        // Start glow pulse animation
        startGlowPulse();
    }

    private void animateSuccessIcon() {
        // Get the success icon card view
        CardView successIcon = findViewById(R.id.btnProceed);
        View parentLayout = findViewById(R.id.outerGlow);

        if (successIcon != null && parentLayout != null) {
            // Scale animation
            ScaleAnimation scaleAnimation = new ScaleAnimation(
                    0.8f, 1.0f, // Start and end X scale
                    0.8f, 1.0f, // Start and end Y scale
                    Animation.RELATIVE_TO_SELF, 0.5f, // Pivot X
                    Animation.RELATIVE_TO_SELF, 0.5f  // Pivot Y
            );
            scaleAnimation.setDuration(500);
            scaleAnimation.setFillAfter(true);
            successIcon.startAnimation(scaleAnimation);

            // Fade in animation for the outer glow
            outerGlow.setAlpha(0f);
            outerGlow.animate()
                    .alpha(1f)
                    .setDuration(800)
                    .start();
        }
    }

    private void startGlowPulse() {
        isPulsing = true;
        handler.post(pulseRunnable);
    }

    private void stopGlowPulse() {
        isPulsing = false;
        handler.removeCallbacks(pulseRunnable);
    }

    private void animateGlow() {
        ValueAnimator animator = ValueAnimator.ofFloat(1.0f, 1.3f, 1.0f);
        animator.setDuration(1500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                outerGlow.setScaleX(value);
                outerGlow.setScaleY(value);

                // Adjust alpha based on scale
                float alpha = 0.3f * (2 - value); // Fade out as it expands
                outerGlow.setAlpha(alpha);
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                outerGlow.setScaleX(1.0f);
                outerGlow.setScaleY(1.0f);
                outerGlow.setAlpha(0.3f);
            }
        });
        animator.start();
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

        // Proceed button (View Documents)
        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Documents screen
                Intent intent = new Intent(PaymentConfirmedActivity.this, DocumentsActivity.class);

                // Pass all necessary data including request_id
                intent.putExtra("request_id", requestId);
                intent.putExtra("customer_name", tvCustomerName.getText().toString());
                intent.putExtra("vehicle_model", tvModel.getText().toString());
                intent.putExtra("vehicle_model", tvModel.getText().toString()); // Duplicate line in original? No, just ensuring context.
                intent.putExtra("payment_mode", tvPaymentMode.getText().toString());
                intent.putExtra("order_type", orderType);

                intent.putExtra("transaction_id",
                        tvTransactionId.getText().toString().replace("Transaction ID: ", ""));
                intent.putExtra("amount_paid",
                        Double.parseDouble(tvAmountPaid.getText().toString()
                                .replace("₹", "")
                                .replace(",", "")));

                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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

    private void showToast(String message) {
        // You can use Toast or a custom snackbar
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopGlowPulse();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isPulsing) {
            startGlowPulse();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopGlowPulse();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}