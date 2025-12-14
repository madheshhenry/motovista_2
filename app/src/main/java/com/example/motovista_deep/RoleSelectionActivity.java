package com.example.motovista_deep;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

public class RoleSelectionActivity extends AppCompatActivity {

    private CardView cardCustomer, cardAdmin;
    private ImageView ivCustomerSelected, ivAdminSelected;
    private Button btnContinue;
    private String selectedRole = "";
    private ConstraintLayout rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // STEP 1: MAKE FULL SCREEN - EDGE TO EDGE
        makeFullScreen();

        setContentView(R.layout.activity_role_selection);

        // STEP 2: INITIALIZE VIEWS
        initViews();

        // STEP 3: ADJUST FOR SYSTEM BARS (Notch, Navigation Bar)
        adjustForSystemBars();

        // STEP 4: SETUP CLICK LISTENERS
        setupClickListeners();
    }

    private void makeFullScreen() {
        // Remove title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Make activity full screen
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        // For Android 10+ (API 29+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // Extend display cutout (notch) area
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(params);
        }

        // Hide navigation bar
        hideNavigationBar();

        // Make status bar transparent
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        // Make navigation bar transparent
        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        // For Android 11+ - Use WindowInsetsController
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(
                    getWindow(),
                    getWindow().getDecorView()
            );
            controller.hide(WindowInsetsCompat.Type.systemBars());
            controller.setSystemBarsBehavior(
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            );
        }
    }

    private void hideNavigationBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Hide navigation bar again when activity resumes
        hideNavigationBar();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideNavigationBar();
        }
    }

    private void initViews() {
        cardCustomer = findViewById(R.id.card_customer);
        cardAdmin = findViewById(R.id.card_admin);
        ivCustomerSelected = findViewById(R.id.iv_customer_selected);
        ivAdminSelected = findViewById(R.id.iv_admin_selected);
        btnContinue = findViewById(R.id.btn_continue);
        rootLayout = findViewById(R.id.root_layout);

        // Set button initially disabled with lower opacity
        btnContinue.setEnabled(false);
        btnContinue.setAlpha(0.5f);
    }

    private void adjustForSystemBars() {
        // Listen for layout completion
        rootLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        // Remove listener to avoid multiple calls
                        rootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                        // Apply padding for system bars
                        applySystemBarPadding();
                    }
                }
        );
    }

    private void applySystemBarPadding() {
        int statusBarHeight = 0;
        int navigationBarHeight = 0;

        // Get status bar height
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }

        // Get navigation bar height
        resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            navigationBarHeight = getResources().getDimensionPixelSize(resourceId);
        }

        // Apply padding to root layout
        rootLayout.setPadding(
                rootLayout.getPaddingLeft(),
                statusBarHeight, // Top padding for status bar
                rootLayout.getPaddingRight(),
                navigationBarHeight // Bottom padding for navigation bar
        );
    }

    private void setupClickListeners() {
        // Customer card click
        cardCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectRole("customer");
            }
        });

        // Admin card click
        cardAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectRole("admin");
            }
        });

        // Continue button click
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!selectedRole.isEmpty()) {
                    navigateToNextScreen();
                }
            }
        });
    }

    private void selectRole(String role) {
        // Reset all selections
        resetSelections();

        // Set selected role
        selectedRole = role;

        // Update UI based on selection
        if (role.equals("customer")) {
            // Highlight customer card
            cardCustomer.setCardBackgroundColor(getResources().getColor(R.color.primary_light));
            cardCustomer.setCardElevation(12f);
            ivCustomerSelected.setVisibility(View.VISIBLE);
        } else if (role.equals("admin")) {
            // Highlight admin card
            cardAdmin.setCardBackgroundColor(getResources().getColor(R.color.primary_light));
            cardAdmin.setCardElevation(12f);
            ivAdminSelected.setVisibility(View.VISIBLE);
        }

        // Enable continue button
        btnContinue.setEnabled(true);
        btnContinue.setAlpha(1f);
    }

    private void resetSelections() {
        // Reset customer card
        cardCustomer.setCardBackgroundColor(getResources().getColor(android.R.color.white));
        cardCustomer.setCardElevation(6f);
        ivCustomerSelected.setVisibility(View.GONE);

        // Reset admin card
        cardAdmin.setCardBackgroundColor(getResources().getColor(android.R.color.white));
        cardAdmin.setCardElevation(6f);
        ivAdminSelected.setVisibility(View.GONE);
    }

    private void navigateToNextScreen() {
        Intent intent;

        if (selectedRole.equals("customer")) {
            // Navigate to Customer Login/Home
            intent = new Intent(RoleSelectionActivity.this, CustomerLoginActivity.class);
        } else {
            // Navigate to Admin Login
            intent = new Intent(RoleSelectionActivity.this, AdminLoginActivity.class);
        }

        // Pass selected role
        intent.putExtra("SELECTED_ROLE", selectedRole);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    @Override
    public void onBackPressed() {
        // Exit app or go to previous screen
        finishAffinity();
    }
}