package com.example.motovista_deep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RegistrationRecordActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView tvTitle;
    private TextView tvProgressCount;

    private Button btnMarkRegistration;
    private Button btnMarkRcBook;
    private Button btnMarkNumberPlate;

    private CardView cardRegistration;
    private CardView cardRcBook;
    private CardView cardNumberPlate;

    private View ivPendingDot;
    private TextView tvVerificationBadge;

    private int completedSteps = 1;
    private int totalSteps = 4;

    // Animation handler
    private Handler animationHandler = new Handler();
    private Runnable dotAnimationRunnable;
    private boolean isDotAnimating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_record);

        // Initialize views
        initializeViews();

        // Setup click listeners
        setupClickListeners();

        // Start animations
        startPendingDotAnimation();

        // Get data from intent
        getIntentData();
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        tvTitle = findViewById(R.id.tvTitle);
        tvProgressCount = findViewById(R.id.tvProgressCount);

        btnMarkRegistration = findViewById(R.id.btnMarkRegistration);
        btnMarkRcBook = findViewById(R.id.btnMarkRcBook);
        btnMarkNumberPlate = findViewById(R.id.btnMarkNumberPlate);

        cardRegistration = findViewById(R.id.cardRegistration);
        cardRcBook = findViewById(R.id.cardRcBook);
        cardNumberPlate = findViewById(R.id.cardNumberPlate);

        ivPendingDot = findViewById(R.id.ivPendingDot);
        tvVerificationBadge = findViewById(R.id.tvVerificationBadge);

        // Set initial progress
        updateProgressCount();
    }

    private void setupClickListeners() {
        // Back button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Mark Registration as Completed
        btnMarkRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markRegistrationCompleted();
            }
        });

        // Disabled buttons (for demonstration)
        btnMarkRcBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(RegistrationRecordActivity.this,
                        "Complete vehicle registration first", Toast.LENGTH_SHORT).show();
            }
        });

        btnMarkNumberPlate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(RegistrationRecordActivity.this,
                        "Complete vehicle registration first", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            String customerName = intent.getStringExtra("customer_name");
            String customerPhone = intent.getStringExtra("customer_phone");
            String bikeModel = intent.getStringExtra("bike_model");
            String engineNumber = intent.getStringExtra("engine_number");
            String orderId = intent.getStringExtra("order_id");

            if (customerName != null) {
                TextView tvCustomerName = findViewById(R.id.tvCustomerName);
                tvCustomerName.setText(customerName);
            }

            if (customerPhone != null) {
                TextView tvCustomerPhone = findViewById(R.id.tvCustomerPhone);
                tvCustomerPhone.setText(customerPhone);
            }

            if (bikeModel != null) {
                TextView tvBikeModel = findViewById(R.id.tvBikeModel);
                tvBikeModel.setText(bikeModel);
            }

            if (engineNumber != null) {
                TextView tvEngineNumber = findViewById(R.id.tvEngineNumber);
                tvEngineNumber.setText(engineNumber);
            }

            if (orderId != null) {
                TextView tvOrderId = findViewById(R.id.tvOrderId);
                tvOrderId.setText(orderId);
            }

            // Update title with customer name
            if (customerName != null) {
                tvTitle.setText(customerName + "'s Record");
            }
        }
    }

    private void markRegistrationCompleted() {
        // Stop the pending dot animation
        stopPendingDotAnimation();

        // Change card appearance
        cardRegistration.setCardElevation(1f);
        cardRegistration.setAlpha(0.8f);

        // Find the status badge container
        CardView statusBadge = cardRegistration.findViewById(R.id.statusBadgeRegistration);
        if (statusBadge != null) {
            // Change badge background color
            statusBadge.setCardBackgroundColor(ContextCompat.getColor(this, R.color.icon_bg_green));

            // Get the LinearLayout inside the CardView
            LinearLayout badgeLayout = (LinearLayout) statusBadge.getChildAt(0);
            if (badgeLayout != null) {
                // Update the dot (first child)
                View dot = badgeLayout.getChildAt(0);
                if (dot != null) {
                    dot.setBackgroundResource(R.drawable.circle_green);
                }

                // Update the status text (second child)
                TextView statusText = (TextView) badgeLayout.getChildAt(1);
                if (statusText != null) {
                    statusText.setText("Done");
                    statusText.setTextColor(ContextCompat.getColor(this, R.color.icon_green));
                }
            }
        }

        // Change button to disabled
        btnMarkRegistration.setEnabled(false);
        btnMarkRegistration.setBackgroundResource(R.drawable.button_disabled_rounded);
        btnMarkRegistration.setTextColor(ContextCompat.getColor(this, R.color.text_secondary_light));
        btnMarkRegistration.setText("Completed");

        // Enable next step (RC Book)
        btnMarkRcBook.setEnabled(true);
        btnMarkRcBook.setBackgroundResource(R.drawable.button_primary_rounded);
        btnMarkRcBook.setTextColor(Color.WHITE);

        // Update registration status text
        TextView tvRegistrationStatus = findViewById(R.id.tvRegistrationStatus);
        if (tvRegistrationStatus != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            String currentDate = sdf.format(new Date());
            tvRegistrationStatus.setText("Completed on " + currentDate);
        }

        // Update progress
        completedSteps = 2;
        updateProgressCount();

        // Show success message
        Toast.makeText(this, "Vehicle registration marked as completed", Toast.LENGTH_SHORT).show();

        // Add subtle animation
        AlphaAnimation fadeIn = new AlphaAnimation(0.7f, 1.0f);
        fadeIn.setDuration(300);
        cardRegistration.startAnimation(fadeIn);
    }

    private void updateProgressCount() {
        tvProgressCount.setText(completedSteps + "/" + totalSteps + " Completed");
    }

    private void startPendingDotAnimation() {
        isDotAnimating = true;

        dotAnimationRunnable = new Runnable() {
            @Override
            public void run() {
                if (isDotAnimating && ivPendingDot != null) {
                    // Create pulse animation
                    AlphaAnimation pulse = new AlphaAnimation(0.3f, 1.0f);
                    pulse.setDuration(1000);
                    pulse.setRepeatCount(Animation.INFINITE);
                    pulse.setRepeatMode(Animation.REVERSE);
                    ivPendingDot.startAnimation(pulse);
                }
            }
        };

        animationHandler.post(dotAnimationRunnable);
    }

    private void stopPendingDotAnimation() {
        isDotAnimating = false;
        if (ivPendingDot != null) {
            ivPendingDot.clearAnimation();
        }
        if (animationHandler != null && dotAnimationRunnable != null) {
            animationHandler.removeCallbacks(dotAnimationRunnable);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopPendingDotAnimation();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}