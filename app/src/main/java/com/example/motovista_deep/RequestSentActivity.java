package com.example.motovista_deep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RequestSentActivity extends AppCompatActivity {

    // Views
    private ImageButton btnBack;
    private Button btnGoToOrders;

    // Order details views
    private TextView tvOrderId;
    private TextView tvBikeName;
    private TextView tvBikeVariant;
    private TextView tvBikePrice;
    private TextView tvRequestTime;
    private TextView tvOrderStatus;

    // Animation views
    private View outerRing;

    // Data from intent
    private String bikeName;
    private String bikePrice;
    private String bikeVariant;
    private String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_sent);

        // Get data from intent
        getIntentData();

        // Initialize views
        initializeViews();

        // Setup click listeners
        setupClickListeners();

        // Start animations
        startSuccessAnimation();

        // Update order details
        updateOrderDetails();
    }

    private void getIntentData() {
        Intent intent = getIntent();

        bikeName = intent.getStringExtra("BIKE_NAME");
        bikePrice = intent.getStringExtra("BIKE_PRICE");
        bikeVariant = intent.getStringExtra("BIKE_VARIANT");

        // Generate order ID if not provided
        orderId = intent.getStringExtra("ORDER_ID");
        if (orderId == null) {
            orderId = "#ORD" + System.currentTimeMillis() % 1000000;
        }

        // Set default values if not provided
        if (bikeName == null) bikeName = "Yamaha YZF R15 V4";
        if (bikePrice == null) bikePrice = "₹2,450";
        if (bikeVariant == null) bikeVariant = "Racing Blue";
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        btnGoToOrders = findViewById(R.id.btnGoToOrders);

        tvOrderId = findViewById(R.id.tvOrderId);
        tvBikeName = findViewById(R.id.tvBikeName);
        tvBikeVariant = findViewById(R.id.tvBikeVariant);
        tvBikePrice = findViewById(R.id.tvBikePrice);
        tvRequestTime = findViewById(R.id.tvRequestTime);
        tvOrderStatus = findViewById(R.id.tvOrderStatus);

        outerRing = findViewById(R.id.outerRing);
    }

    private void setupClickListeners() {
        // Back button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Go to Orders button
        btnGoToOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToOrders();
            }
        });
    }

    private void startSuccessAnimation() {
        // Pulsing animation for outer ring
        ObjectAnimator scaleAnimator = ObjectAnimator.ofFloat(outerRing, "scaleX", 1f, 1.2f, 1f);
        scaleAnimator.setDuration(2000);
        scaleAnimator.setRepeatCount(ValueAnimator.INFINITE);
        scaleAnimator.setRepeatMode(ValueAnimator.RESTART);
        scaleAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator scaleAnimatorY = ObjectAnimator.ofFloat(outerRing, "scaleY", 1f, 1.2f, 1f);
        scaleAnimatorY.setDuration(2000);
        scaleAnimatorY.setRepeatCount(ValueAnimator.INFINITE);
        scaleAnimatorY.setRepeatMode(ValueAnimator.RESTART);
        scaleAnimatorY.setInterpolator(new AccelerateDecelerateInterpolator());

        // Alpha animation for fading effect
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(outerRing, "alpha", 0.3f, 0.8f, 0.3f);
        alphaAnimator.setDuration(2000);
        alphaAnimator.setRepeatCount(ValueAnimator.INFINITE);
        alphaAnimator.setRepeatMode(ValueAnimator.RESTART);

        // Start animations
        scaleAnimator.start();
        scaleAnimatorY.start();
        alphaAnimator.start();

        // Success icon animation
        final View successIcon = findViewById(R.id.ivSuccessIcon);
        successIcon.setScaleX(0);
        successIcon.setScaleY(0);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ObjectAnimator scaleUp = ObjectAnimator.ofFloat(successIcon, "scaleX", 0f, 1.2f, 1f);
                ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(successIcon, "scaleY", 0f, 1.2f, 1f);

                scaleUp.setDuration(600);
                scaleUpY.setDuration(600);
                scaleUp.setInterpolator(new AccelerateDecelerateInterpolator());
                scaleUpY.setInterpolator(new AccelerateDecelerateInterpolator());

                scaleUp.start();
                scaleUpY.start();
            }
        }, 300);
    }

    private void updateOrderDetails() {
        // Set order details
        tvOrderId.setText(orderId);
        tvBikeName.setText(bikeName);
        tvBikeVariant.setText(bikeVariant);
        tvBikePrice.setText(bikePrice);

        // Set current time
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        String currentTime = sdf.format(new Date());
        tvRequestTime.setText(currentTime);

        // Set status
        tvOrderStatus.setText("Pending Review");
    }

    private void goToOrders() {
        // Navigate to orders screen
        // Intent ordersIntent = new Intent(this, OrdersActivity.class);
        // startActivity(ordersIntent);
        // finish();

        // For now, just go back
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}