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
    private com.example.motovista_deep.helpers.OrderSessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_confirmed);

        sessionManager = new com.example.motovista_deep.helpers.OrderSessionManager(this);

        // Initialize views
        initializeViews();

        // Get data from intent or session
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
        // User requested NOT to go back. Hide or disable back button.
        btnBack.setVisibility(View.GONE);

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
        
        // Check if we have fresh data from Payment Flow
        if (intent != null && intent.hasExtra("request_id")) {
            requestId = intent.getIntExtra("request_id", -1);
            
            // Start Persistent Session
            if (requestId != -1) {
                sessionManager.startSession(requestId);
            }

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
            if (customerName != null) tvCustomerName.setText(customerName);
            if (vehicleModel != null) tvModel.setText(vehicleModel);
            if (paymentMode != null) tvPaymentMode.setText(paymentMode);
            tvAmountPaid.setText(formatIndianCurrency(amountPaid));
            if (transactionId != null) tvTransactionId.setText("Transaction ID: " + transactionId);
            
        } else {
            // Restore from Session if App was restarted
            if (sessionManager.isSessionActive()) {
                requestId = sessionManager.getRequestId();
                // Note: We might lose ephemeral data (name, amount) if not stored. 
                // ideally we fetch details from server using requestId, but for now 
                // we will rely on minimal restoration or placeholder.
                // Or better, assume intent extras are unavailable and user just sees "Payment Confirmed".
                tvCustomerName.setText("Resuming Session...");
                tvAmountPaid.setText("Paid");
            }
        }
    }

    // ... animations ...

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
        // Back button is HIDDEN/GONE.

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
                intent.putExtra("vehicle_model", tvModel.getText().toString()); 
                intent.putExtra("payment_mode", tvPaymentMode.getText().toString());
                intent.putExtra("order_type", orderType);

                intent.putExtra("transaction_id",
                        tvTransactionId.getText().toString().replace("Transaction ID: ", ""));
                // Safely parse amount
                double amt = 0;
                try {
                     amt = Double.parseDouble(tvAmountPaid.getText().toString()
                                .replace("₹", "")
                                .replace(",", ""));
                } catch(Exception e) {}
                intent.putExtra("amount_paid", amt);

                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                // We do NOT finish() so users can't back into here easily? 
                // Actually user WANTS to stay here if they close app. 
                // But if they go forward to Documents, Documents becomes the active step.
                // So DocumentsActivity SHOULD handle the state update to DOCUMENTS.
                // And accessing PaymentConfirmed again should verify state.
                finish(); 
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
        // BLOCKED: User requested strictly NO BACK ACTION.
        showToast("Please proceed to view documents.");
        // super.onBackPressed(); // Removed to disable back press
    }
}