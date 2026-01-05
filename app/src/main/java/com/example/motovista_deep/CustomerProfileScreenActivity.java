package com.example.motovista_deep;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.motovista_deep.api.ApiService;
import com.example.motovista_deep.api.RetrofitClient;
import com.example.motovista_deep.helpers.SharedPrefManager;
import com.example.motovista_deep.models.GetProfileResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerProfileScreenActivity extends AppCompatActivity {

    // Header Views
    private ImageView btnBack, ivProfilePicture, btnEditProfile;
    private TextView tvUserName, tvUserEmail;

    // Personal Information
    private EditText etFullName, etEmail, etPhone;

    // Settings Section
    private LinearLayout btnNotifications, btnDownloadedInvoice, btnPurchases;

    // Support Section
    private LinearLayout btnHelpSupport, btnFAQ;

    // Bottom Navigation
    private LinearLayout tabHome, tabBikes, tabEmiCalculator, tabOrders, tabProfile;
    private ImageView ivHome, ivBikes, ivEmiCalculator, ivOrders, ivProfile;
    private TextView tvHome, tvBikes, tvEmiCalculator, tvOrders, tvProfile;

    // Logout
    private CardView btnLogout;

    // Loading
    private ProgressBar progressBar;
    private ConstraintLayout rootLayout;
    private NestedScrollView scrollView;

    private static final int EDIT_PROFILE_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_profile_screen);

        initializeViews();
        loadUserData();
        setupClickListeners();
        setActiveTab(tabProfile); // Set profile tab as active
    }

    private void initializeViews() {
        // Header
        btnBack = findViewById(R.id.btnBack);
        ivProfilePicture = findViewById(R.id.ivProfilePicture);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        tvUserName = findViewById(R.id.tvUserName);
        tvUserEmail = findViewById(R.id.tvUserEmail);

        // Personal Information
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);

        // Settings Section
        btnNotifications = findViewById(R.id.btnNotifications);
        btnDownloadedInvoice = findViewById(R.id.btnDownloadedInvoice);
        btnPurchases = findViewById(R.id.btnPurchases);

        // Support Section
        btnHelpSupport = findViewById(R.id.btnHelpSupport);
        btnFAQ = findViewById(R.id.btnFAQ);

        // Bottom Navigation
        tabHome = findViewById(R.id.tabHome);
        tabBikes = findViewById(R.id.tabBikes);
        tabEmiCalculator = findViewById(R.id.tabEmiCalculator);
        tabOrders = findViewById(R.id.tabOrders);
        tabProfile = findViewById(R.id.tabProfile);

        ivHome = findViewById(R.id.ivHome);
        ivBikes = findViewById(R.id.ivBikes);
        ivEmiCalculator = findViewById(R.id.ivEmiCalculator);
        ivOrders = findViewById(R.id.ivOrders);
        ivProfile = findViewById(R.id.ivProfile);

        tvHome = findViewById(R.id.tvHome);
        tvBikes = findViewById(R.id.tvBikes);
        tvEmiCalculator = findViewById(R.id.tvEmiCalculator);
        tvOrders = findViewById(R.id.tvOrders);
        tvProfile = findViewById(R.id.tvProfile);

        // Logout
        btnLogout = findViewById(R.id.btnLogout);

        // Loading
        progressBar = findViewById(R.id.progressBar);
        rootLayout = findViewById(R.id.rootLayout);
        scrollView = findViewById(R.id.scrollView);

        // Make EditTexts non-editable (read-only for display)
        setEditTextsReadOnly(true);
    }

    private void setEditTextsReadOnly(boolean readOnly) {
        etFullName.setFocusable(!readOnly);
        etFullName.setFocusableInTouchMode(!readOnly);
        etFullName.setClickable(!readOnly);
        etFullName.setCursorVisible(false);

        etEmail.setFocusable(!readOnly);
        etEmail.setFocusableInTouchMode(!readOnly);
        etEmail.setClickable(!readOnly);
        etEmail.setCursorVisible(false);

        etPhone.setFocusable(!readOnly);
        etPhone.setFocusableInTouchMode(!readOnly);
        etPhone.setClickable(!readOnly);
        etPhone.setCursorVisible(false);
    }

    private void loadUserData() {
        String token = SharedPrefManager.getInstance(this).getToken();
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_SHORT).show();
            navigateToLogin();
            return;
        }

        showLoading(true);

        ApiService api = RetrofitClient.getApiService();
        api.getProfile("Bearer " + token).enqueue(new Callback<GetProfileResponse>() {
            @Override
            public void onResponse(Call<GetProfileResponse> call, Response<GetProfileResponse> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null && response.body().status) {
                    GetProfileResponse.Data userData = response.body().data;
                    updateUIWithUserData(userData);

                    // Cache user data for offline use
                    cacheUserData(userData);
                } else {
                    handleProfileLoadError("Failed to load profile data");
                    loadCachedData();
                }
            }

            @Override
            public void onFailure(Call<GetProfileResponse> call, Throwable t) {
                showLoading(false);
                Toast.makeText(CustomerProfileScreenActivity.this,
                        "Network error. Please check your connection.", Toast.LENGTH_SHORT).show();
                loadCachedData();
            }
        });
    }

    private void updateUIWithUserData(GetProfileResponse.Data userData) {
        // Update header TextViews
        if (!TextUtils.isEmpty(userData.full_name)) {
            tvUserName.setText(userData.full_name);
            etFullName.setText(userData.full_name);
        }

        if (!TextUtils.isEmpty(userData.email)) {
            tvUserEmail.setText(userData.email);
            etEmail.setText(userData.email);
        }

        if (!TextUtils.isEmpty(userData.phone)) {
            etPhone.setText(userData.phone);
        } else {
            etPhone.setText("Not provided");
        }

        // Load profile image
        if (!TextUtils.isEmpty(userData.profile_image)) {
            String baseUrl = RetrofitClient.BASE_URL.replace("api/", "");
            String imageUrl = baseUrl + "uploads/profile_pics/" + userData.profile_image;

            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .error(R.drawable.ic_profile_placeholder)
                    .transform(new CircleCrop())
                    .into(ivProfilePicture);
        } else {
            ivProfilePicture.setImageResource(R.drawable.ic_profile_placeholder);
        }
    }

    private void cacheUserData(GetProfileResponse.Data userData) {
        SharedPrefManager prefManager = SharedPrefManager.getInstance(this);
        if (!TextUtils.isEmpty(userData.full_name)) {
            prefManager.setUserName(userData.full_name);
        }
        if (!TextUtils.isEmpty(userData.email)) {
            prefManager.setUserEmail(userData.email);
        }
        if (!TextUtils.isEmpty(userData.phone)) {
            prefManager.setUserPhone(userData.phone);
        }
    }

    private void loadCachedData() {
        SharedPrefManager prefManager = SharedPrefManager.getInstance(this);

        String cachedName = prefManager.getUserName();
        if (!TextUtils.isEmpty(cachedName)) {
            tvUserName.setText(cachedName);
            etFullName.setText(cachedName);
        } else {
            tvUserName.setText("User");
        }

        String cachedEmail = prefManager.getUserEmail();
        if (!TextUtils.isEmpty(cachedEmail)) {
            tvUserEmail.setText(cachedEmail);
            etEmail.setText(cachedEmail);
        } else {
            tvUserEmail.setText("No email");
        }

        String cachedPhone = prefManager.getUserPhone();
        if (!TextUtils.isEmpty(cachedPhone)) {
            etPhone.setText(cachedPhone);
        } else {
            etPhone.setText("Not provided");
        }
    }

    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }

        if (scrollView != null) {
            scrollView.setVisibility(show ? View.GONE : View.VISIBLE);
        }

        // Disable interaction while loading
        btnEditProfile.setEnabled(!show);
        btnLogout.setEnabled(!show);
        if (btnNotifications != null) btnNotifications.setEnabled(!show);
        if (btnDownloadedInvoice != null) btnDownloadedInvoice.setEnabled(!show);
        if (btnPurchases != null) btnPurchases.setEnabled(!show);
        if (btnHelpSupport != null) btnHelpSupport.setEnabled(!show);
        if (btnFAQ != null) btnFAQ.setEnabled(!show);
    }

    private void setupClickListeners() {
        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Edit Profile button
        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, CustomerEditProfileActivity.class);
            startActivityForResult(intent, EDIT_PROFILE_REQUEST_CODE);
        });

        // Profile picture click
        ivProfilePicture.setOnClickListener(v -> {
            // Optional: Open full-screen image view
            Toast.makeText(this, "Profile Picture", Toast.LENGTH_SHORT).show();
        });

        // Settings Section
        if (btnNotifications != null) {
            btnNotifications.setOnClickListener(v -> {
                Toast.makeText(this, "Notifications Settings", Toast.LENGTH_SHORT).show();
                // Navigate to notifications settings
                // startActivity(new Intent(this, NotificationsActivity.class));
            });
        }

        if (btnDownloadedInvoice != null) {
            btnDownloadedInvoice.setOnClickListener(v -> {
                Toast.makeText(this, "Downloaded Invoices", Toast.LENGTH_SHORT).show();
                // Navigate to downloaded invoices
                // startActivity(new Intent(this, DownloadedInvoicesActivity.class));
            });
        }

        if (btnPurchases != null) {
            btnPurchases.setOnClickListener(v -> {
                Toast.makeText(this, "Purchases History", Toast.LENGTH_SHORT).show();
                // Navigate to purchases history
                // startActivity(new Intent(this, PurchasesActivity.class));
            });
        }

        // Support Section
        if (btnHelpSupport != null) {
            btnHelpSupport.setOnClickListener(v -> {
                Toast.makeText(this, "Help & Support", Toast.LENGTH_SHORT).show();
                // Navigate to help & support
                // startActivity(new Intent(this, HelpSupportActivity.class));
            });
        }

        if (btnFAQ != null) {
            btnFAQ.setOnClickListener(v -> {
                Toast.makeText(this, "Frequently Asked Questions", Toast.LENGTH_SHORT).show();
                // Navigate to FAQ
                // startActivity(new Intent(this, FAQActivity.class));
            });
        }

        // Logout button
        btnLogout.setOnClickListener(v -> showLogoutDialog());

        // Bottom Navigation
        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        tabHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, CustomerHomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        tabBikes.setOnClickListener(v -> {
            startActivity(new Intent(this, BikeCatalogActivity.class));
            finish();
        });

        tabEmiCalculator.setOnClickListener(v -> {
            startActivity(new Intent(this, EmiCalculatorActivity.class));
            finish();
        });

        tabOrders.setOnClickListener(v -> {
            startActivity(new Intent(this, OrderStatusActivity.class));
            finish();
        });

        tabProfile.setOnClickListener(v -> {
            // Already on profile page, just update tab
            setActiveTab(tabProfile);
        });
    }

    private void setActiveTab(LinearLayout activeTab) {
        resetAllTabs();

        int activeColor = ContextCompat.getColor(this, R.color.primary_color);
        int inactiveColor = ContextCompat.getColor(this, R.color.gray_400);

        if (activeTab == tabHome) {
            ivHome.setImageResource(R.drawable.ic_home_filled);
            ivHome.setColorFilter(activeColor);
            tvHome.setTextColor(activeColor);
            tvHome.setTypeface(tvHome.getTypeface(), android.graphics.Typeface.BOLD);
        } else if (activeTab == tabBikes) {
            ivBikes.setColorFilter(activeColor);
            tvBikes.setTextColor(activeColor);
            tvBikes.setTypeface(tvBikes.getTypeface(), android.graphics.Typeface.BOLD);
        } else if (activeTab == tabEmiCalculator) {
            ivEmiCalculator.setColorFilter(activeColor);
            tvEmiCalculator.setTextColor(activeColor);
            tvEmiCalculator.setTypeface(tvEmiCalculator.getTypeface(), android.graphics.Typeface.BOLD);
        } else if (activeTab == tabOrders) {
            ivOrders.setImageResource(R.drawable.ic_receipt_long_filled);
            ivOrders.setColorFilter(activeColor);
            tvOrders.setTextColor(activeColor);
            tvOrders.setTypeface(tvOrders.getTypeface(), android.graphics.Typeface.BOLD);
        } else if (activeTab == tabProfile) {
            ivProfile.setImageResource(R.drawable.ic_person_filled);
            ivProfile.setColorFilter(activeColor);
            tvProfile.setTextColor(activeColor);
            tvProfile.setTypeface(tvProfile.getTypeface(), android.graphics.Typeface.BOLD);
        }
    }

    private void resetAllTabs() {
        int inactiveColor = ContextCompat.getColor(this, R.color.gray_400);

        // Reset Home
        ivHome.setImageResource(R.drawable.ic_home);
        ivHome.setColorFilter(inactiveColor);
        tvHome.setTextColor(inactiveColor);
        tvHome.setTypeface(null, android.graphics.Typeface.NORMAL);

        // Reset Bikes
        ivBikes.setColorFilter(inactiveColor);
        tvBikes.setTextColor(inactiveColor);
        tvBikes.setTypeface(null, android.graphics.Typeface.NORMAL);

        // Reset EMI Calculator
        ivEmiCalculator.setColorFilter(inactiveColor);
        tvEmiCalculator.setTextColor(inactiveColor);
        tvEmiCalculator.setTypeface(null, android.graphics.Typeface.NORMAL);

        // Reset Orders
        ivOrders.setImageResource(R.drawable.ic_receipt_long);
        ivOrders.setColorFilter(inactiveColor);
        tvOrders.setTextColor(inactiveColor);
        tvOrders.setTypeface(null, android.graphics.Typeface.NORMAL);

        // Reset Profile
        ivProfile.setImageResource(R.drawable.ic_person);
        ivProfile.setColorFilter(inactiveColor);
        tvProfile.setTextColor(inactiveColor);
        tvProfile.setTypeface(null, android.graphics.Typeface.NORMAL);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDIT_PROFILE_REQUEST_CODE && resultCode == RESULT_OK) {
            loadUserData();
            Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Log Out")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Log Out", (dialog, which) -> performLogout())
                .setNegativeButton("Cancel", null)
                .setCancelable(true)
                .show();
    }

    private void performLogout() {
        SharedPrefManager.getInstance(this).clear();

        Intent intent = new Intent(this, RoleSelectionActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
    }

    private void handleProfileLoadError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void navigateToLogin() {
        SharedPrefManager.getInstance(this).clear();
        Intent intent = new Intent(this, RoleSelectionActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserData();
    }
}