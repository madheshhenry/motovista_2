package com.example.motovista_deep;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.motovista_deep.helpers.SharedPrefManager;
import com.example.motovista_deep.managers.WorkflowManager;
import com.example.motovista_deep.models.User;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 3000; // 3 seconds
    private String deepLinkScreen;
    private String deepLinkId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Check for deep link extras
        if (getIntent() != null) {
            deepLinkScreen = getIntent().getStringExtra("screen");
            deepLinkId = getIntent().getStringExtra("id");
        }

        // Remove title bar and make full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);

        // Add animations
        animateViews();

        // Start progress animation
        animateProgress();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPrefManager prefManager = SharedPrefManager.getInstance(SplashActivity.this);

                if (prefManager.isLoggedIn()) {
                    String role = prefManager.getRole();

                    // --- DEEP LINK LOGIC ---
                    if (deepLinkScreen != null) {
                        try {
                            Intent intent = null;
                            if ("OrderStatusActivity".equals(deepLinkScreen)) {
                                intent = new Intent(SplashActivity.this, OrderStatusActivity.class);
                                intent.putExtra("ORDER_ID", deepLinkId);
                            } else if ("OrderSummaryActivity".equals(deepLinkScreen)) {
                                intent = new Intent(SplashActivity.this, OrderSummaryActivity.class);
                                if (deepLinkId != null) {
                                    intent.putExtra("request_id", Integer.parseInt(deepLinkId));
                                }
                            } else if ("CustomerDetailsActivity".equals(deepLinkScreen)) {
                                intent = new Intent(SplashActivity.this, CustomerDetailsActivity.class);
                                if (deepLinkId != null) {
                                    intent.putExtra("customer_id", Integer.parseInt(deepLinkId));
                                }
                            }

                            if (intent != null) {
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                                return;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if ("admin".equals(role)) {
                        // 1. Check for persistent Admin Workflow (Redirection to specific stage)
                        if (WorkflowManager.checkAndRedirect(SplashActivity.this)) {
                            return; // Already finished in checkAndRedirect
                        }
                        // 2. Normal Admin Dashboard
                        startActivity(new Intent(SplashActivity.this, AdminDashboardActivity.class));
                    } else if ("customer".equals(role)) {
                        User user = prefManager.getUser();
                        if (user != null && user.isIs_profile_completed()) {
                            // Profile complete -> Home
                            startActivity(new Intent(SplashActivity.this, CustomerHomeActivity.class));
                        } else {
                            // Profile incomplete -> Profile Activity
                            startActivity(new Intent(SplashActivity.this, CustomerProfileActivity.class));
                        }
                    } else {
                        // Unknown role -> Role Selection
                        startActivity(new Intent(SplashActivity.this, RoleSelectionActivity.class));
                    }
                } else {
                    // Not logged in -> Role Selection
                    startActivity(new Intent(SplashActivity.this, RoleSelectionActivity.class));
                }

                finish();
            }
        }, SPLASH_DURATION);
    }

    private void animateViews() {
        // Get the new logo view
        ImageView splashLogo = findViewById(R.id.img_splash_logo);

        if (splashLogo != null) {
            // Load a simple fade-in animation
            Animation fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
            fadeIn.setDuration(1200);

            // Apply animation
            splashLogo.startAnimation(fadeIn);
        }
    }

    private void animateProgress() {
        final View progressFill = findViewById(R.id.progress_fill);
        final FrameLayout progressContainer = findViewById(R.id.progress_container);

        // Wait for layout to be measured
        progressContainer.post(new Runnable() {
            @Override
            public void run() {
                // Get container width
                int containerWidth = progressContainer.getWidth();

                // Set initial width (30%)
                ViewGroup.LayoutParams params = progressFill.getLayoutParams();
                params.width = (int) (containerWidth * 0.3);
                progressFill.setLayoutParams(params);

                // Animate to full width
                ValueAnimator animator = ValueAnimator.ofInt(params.width, containerWidth);
                animator.setDuration(2500);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int val = (Integer) animation.getAnimatedValue();
                        ViewGroup.LayoutParams params = progressFill.getLayoutParams();
                        params.width = val;
                        progressFill.setLayoutParams(params);
                    }
                });
                animator.start();
            }
        });
    }
}