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

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 3000; // 3 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Remove title bar and make full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);

        // Add animations
        animateViews();

        // Start progress animation
        animateProgress();

        // Navigate to MainActivity after delay
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, RoleSelectionActivity.class);
                startActivity(intent);
                finish(); // Close splash activity
            }
        }, SPLASH_DURATION);
    }

    private void animateViews() {
        // Get views
        CardView iconContainer = findViewById(R.id.icon_bike_container);
        TextView appName = findViewById(R.id.tv_app_name);
        TextView motovista = findViewById(R.id.tv_motovista);
        TextView tagline = findViewById(R.id.tv_tagline);

        // Load animations - Using correct animation resources
        Animation fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        Animation slideUp = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);

        // Create slide from right animation (we'll create it manually since it doesn't exist)
        Animation slideFromRight = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        slideFromRight.setInterpolator(this, android.R.anim.accelerate_decelerate_interpolator);

        // Set animation durations
        fadeIn.setDuration(1000);
        slideUp.setDuration(800);
        slideFromRight.setDuration(800);

        // For slide from right, we need to create it differently
        // Let's use translate animation
        Animation translateFromRight = new android.view.animation.TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 1.0f,  // From X = 100% (right)
                Animation.RELATIVE_TO_PARENT, 0.0f,  // To X = 0% (original)
                Animation.RELATIVE_TO_SELF, 0.0f,    // From Y = 0%
                Animation.RELATIVE_TO_SELF, 0.0f     // To Y = 0%
        );
        translateFromRight.setDuration(800);
        translateFromRight.setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator());

        // Apply animations
        iconContainer.startAnimation(fadeIn);
        appName.startAnimation(slideUp);
        motovista.startAnimation(translateFromRight);
        tagline.startAnimation(fadeIn);
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