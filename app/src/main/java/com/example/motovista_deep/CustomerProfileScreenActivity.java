    package com.example.motovista_deep;
    
    import androidx.annotation.Nullable;
    import androidx.appcompat.app.AlertDialog;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.cardview.widget.CardView;
    import androidx.core.content.ContextCompat;
    
    import com.example.motovista_deep.models.GetProfileResponse;
    import com.example.motovista_deep.helpers.SharedPrefManager;
    import com.example.motovista_deep.api.ApiService;
    import com.example.motovista_deep.api.RetrofitClient;
    import com.bumptech.glide.Glide;
    
    import android.content.DialogInterface;
    import android.content.Intent;
    import android.content.SharedPreferences;
    import android.os.Bundle;
    import android.view.View;
    import android.widget.EditText;
    import android.widget.ImageView;
    import android.widget.LinearLayout;
    import android.widget.TextView;
    import android.widget.Toast;
    
    import retrofit2.Call;
    import retrofit2.Callback;
    import retrofit2.Response;
    
    
    public class CustomerProfileScreenActivity extends AppCompatActivity {
    
        // Profile Section
        private ImageView ivProfilePicture, btnEditProfile, btnBack;
        private TextView tvUserName, tvUserEmail;
        private EditText etFullName, etEmail, etPhone;
    
        // Settings Section
        private LinearLayout btnNotifications, btnDownloadedInvoice, btnPurchases;
    
        // Support Section
        private LinearLayout btnHelpSupport, btnFAQ;
    
        // Logout Section
        private CardView btnLogout;
    
        // Bottom Navigation
        private LinearLayout tabHome, tabBikes, tabEmiCalculator, tabOrders, tabProfile;
        private ImageView ivHome, ivBikes, ivEmiCalculator, ivOrders, ivProfile;
        private TextView tvHome, tvBikes, tvEmiCalculator, tvOrders, tvProfile;
    
        private SharedPreferences sharedPreferences;
    
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_customer_profile_screen);
    
            // Initialize SharedPreferences
            sharedPreferences = getSharedPreferences("CustomerPrefs", MODE_PRIVATE);
    
            // Initialize views
            initializeViews();
    
            // Load user data
            loadUserData();
    
            // Setup click listeners
            setupClickListeners();
    
            // Set active tab (Profile is active by default)
            setActiveTab(tabProfile);
        }
    
        private void initializeViews() {
            // Header
            btnBack = findViewById(R.id.btnBack);
    
            // Profile Section
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
    
            // Logout Section
            btnLogout = findViewById(R.id.btnLogout);
    
            // Bottom Navigation Tabs
            tabHome = findViewById(R.id.tabHome);
            tabBikes = findViewById(R.id.tabBikes);
            tabEmiCalculator = findViewById(R.id.tabEmiCalculator);
            tabOrders = findViewById(R.id.tabOrders);
            tabProfile = findViewById(R.id.tabProfile);
    
            // Bottom Navigation Icons
            ivHome = findViewById(R.id.ivHome);
            ivBikes = findViewById(R.id.ivBikes);
            ivEmiCalculator = findViewById(R.id.ivEmiCalculator);
            ivOrders = findViewById(R.id.ivOrders);
            ivProfile = findViewById(R.id.ivProfile);
    
            // Bottom Navigation Text
            tvHome = findViewById(R.id.tvHome);
            tvBikes = findViewById(R.id.tvBikes);
            tvEmiCalculator = findViewById(R.id.tvEmiCalculator);
            tvOrders = findViewById(R.id.tvOrders);
            tvProfile = findViewById(R.id.tvProfile);
        }
    
        private void loadUserData() {
    
            String token = SharedPrefManager.getInstance(this).getToken();
    
            ApiService api = RetrofitClient.getApiService();
            api.getProfile("Bearer " + token).enqueue(new Callback<GetProfileResponse>() {
                @Override
                public void onResponse(Call<GetProfileResponse> call, Response<GetProfileResponse> response) {
    
                    if (response.isSuccessful() && response.body() != null && response.body().status) {
    
                        GetProfileResponse.Data d = response.body().data;
    
                        tvUserName.setText(d.full_name);
                        tvUserEmail.setText(d.email);
                        etFullName.setText(d.full_name);
                        etEmail.setText(d.email);
                        etPhone.setText(d.phone);

                        if (d.profile_image != null && !d.profile_image.isEmpty()) {

                            String imageUrl =
                                    RetrofitClient.BASE_URL
                                            + "uploads/"
                                            + d.profile_image;


                            Glide.with(CustomerProfileScreenActivity.this)
                                    .load(imageUrl)
                                    .placeholder(R.drawable.ic_profile_placeholder)
                                    .error(R.drawable.ic_profile_placeholder)
                                    .into(ivProfilePicture);
                        }

                    }
                }
    
                @Override
                public void onFailure(Call<GetProfileResponse> call, Throwable t) {
                    Toast.makeText(CustomerProfileScreenActivity.this,
                            "Failed to load profile", Toast.LENGTH_SHORT).show();
                }
            });
        }
    
    
        private void saveUserData() {
            String fullName = etFullName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
    
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("customer_name", fullName);
            editor.putString("customer_email", email);
            editor.putString("customer_phone", phone);
            editor.apply();
    
            // Update display name
            tvUserName.setText(fullName);
            tvUserEmail.setText(email);
        }
    
        private void setupClickListeners() {
            // Back Button
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
    
            // Edit Profile Picture
            // Edit Profile Picture - Change this section in CustomerProfileScreenActivity.java
            btnEditProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Navigate to Edit Profile screen
                    Intent intent = new Intent(CustomerProfileScreenActivity.this, CustomerEditProfileActivity.class);
                    startActivityForResult(intent, 1001); // Use request code 1001
                }
            });
    
            // Save data when user finishes editing
            etFullName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        saveUserData();
                    }
                }
            });
    
            etEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        saveUserData();
                    }
                }
            });
    
            etPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        saveUserData();
                    }
                }
            });
    
            // Settings Section
            btnNotifications.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(CustomerProfileScreenActivity.this, "Notifications Settings", Toast.LENGTH_SHORT).show();
                    // Navigate to notifications settings
                    // startActivity(new Intent(CustomerProfileScreenActivity.this, NotificationsSettingsActivity.class));
                }
            });
    
            btnDownloadedInvoice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Navigate to downloaded invoices
                    Intent intent = new Intent(CustomerProfileScreenActivity.this, DownloadedInvoicesActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });
    
            btnPurchases.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Navigate to purchase history
                    Intent intent = new Intent(CustomerProfileScreenActivity.this, CustomerPurchasesActivity.class);
                    startActivity(intent);
                }
            });
    
            // Support Section - UPDATED FOR EDIT PROFILE SCREEN
            btnHelpSupport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Navigate to help & support
                    Intent intent = new Intent(CustomerProfileScreenActivity.this, HelpSupportActivity.class);
                    startActivity(intent);
                }
            });
            btnFAQ.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(CustomerProfileScreenActivity.this, "Frequently Asked Questions", Toast.LENGTH_SHORT).show();
                    // Navigate to FAQ
                    // startActivity(new Intent(CustomerProfileScreenActivity.this, FAQActivity.class));
                }
            });
    
            // Logout Button
            btnLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showLogoutDialog();
                }
            });
    
            // Bottom Navigation Tabs
            tabHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setActiveTab(tabHome);
                    Intent intent = new Intent(CustomerProfileScreenActivity.this, CustomerHomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            });
    
            tabBikes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setActiveTab(tabBikes);
                    Toast.makeText(CustomerProfileScreenActivity.this, "Bikes", Toast.LENGTH_SHORT).show();
                    // Navigate to bikes listing
                    // startActivity(new Intent(CustomerProfileScreenActivity.this, BikesActivity.class));
                }
            });
    
            tabEmiCalculator.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setActiveTab(tabEmiCalculator);
                    Toast.makeText(CustomerProfileScreenActivity.this, "EMI Calculator", Toast.LENGTH_SHORT).show();
                    // Navigate to EMI calculator
                    // startActivity(new Intent(CustomerProfileScreenActivity.this, EmiCalculatorActivity.class));
                }
            });
    
            tabOrders.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setActiveTab(tabOrders);
                    Toast.makeText(CustomerProfileScreenActivity.this, "Orders", Toast.LENGTH_SHORT).show();
                    // Navigate to orders
                    // startActivity(new Intent(CustomerProfileScreenActivity.this, OrdersActivity.class));
                }
            });
    
            tabProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setActiveTab(tabProfile);
                    // Already on profile page
                }
            });
        }
    
        // ADD THIS METHOD TO HANDLE EDIT PROFILE RESULT
        @Override
        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
    
            if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
                // Get updated data from Edit Profile screen
                String updatedName = data.getStringExtra("UPDATED_NAME");
                String updatedEmail = data.getStringExtra("UPDATED_EMAIL");
                String updatedPhone = data.getStringExtra("UPDATED_PHONE");
    
                // Update the display in profile screen
                tvUserName.setText(updatedName);
                tvUserEmail.setText(updatedEmail);
                etFullName.setText(updatedName);
                etEmail.setText(updatedEmail);
                etPhone.setText(updatedPhone);
    
                // Also save to SharedPreferences
                saveUserData();
    
                Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show();
            }
        }
    
        private void showLogoutDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Log Out");
            builder.setMessage("Are you sure you want to log out?");
            builder.setPositiveButton("Log Out", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    performLogout();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }

        private void performLogout() {

            // 1️⃣ Clear SharedPrefManager (token, role, login state)
            SharedPrefManager.getInstance(this).clear();

            // 2️⃣ Clear local SharedPreferences if used
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            // 3️⃣ Go to Role Selection screen
            Intent intent = new Intent(CustomerProfileScreenActivity.this, RoleSelectionActivity.class);

            // 4️⃣ Clear entire back stack
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);
            finish();

            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        }


        private void setActiveTab(LinearLayout activeTab) {
            // Reset all tabs
            resetAllTabs();
    
            // Set active tab
            if (activeTab == tabHome) {
                ivHome.setImageResource(R.drawable.ic_home_filled);
                ivHome.setColorFilter(ContextCompat.getColor(this, R.color.primary_color));
                tvHome.setTextColor(ContextCompat.getColor(this, R.color.primary_color));
                tvHome.setTypeface(tvHome.getTypeface(), android.graphics.Typeface.BOLD);
            } else if (activeTab == tabBikes) {
                ivBikes.setColorFilter(ContextCompat.getColor(this, R.color.primary_color));
                tvBikes.setTextColor(ContextCompat.getColor(this, R.color.primary_color));
                tvBikes.setTypeface(tvBikes.getTypeface(), android.graphics.Typeface.BOLD);
            } else if (activeTab == tabEmiCalculator) {
                ivEmiCalculator.setColorFilter(ContextCompat.getColor(this, R.color.primary_color));
                tvEmiCalculator.setTextColor(ContextCompat.getColor(this, R.color.primary_color));
                tvEmiCalculator.setTypeface(tvEmiCalculator.getTypeface(), android.graphics.Typeface.BOLD);
            } else if (activeTab == tabOrders) {
                ivOrders.setColorFilter(ContextCompat.getColor(this, R.color.primary_color));
                tvOrders.setTextColor(ContextCompat.getColor(this, R.color.primary_color));
                tvOrders.setTypeface(tvOrders.getTypeface(), android.graphics.Typeface.BOLD);
            } else if (activeTab == tabProfile) {
                ivProfile.setImageResource(R.drawable.ic_person_filled);
                ivProfile.setColorFilter(ContextCompat.getColor(this, R.color.primary_color));
                tvProfile.setTextColor(ContextCompat.getColor(this, R.color.primary_color));
                tvProfile.setTypeface(tvProfile.getTypeface(), android.graphics.Typeface.BOLD);
            }
        }
    
        private void resetAllTabs() {
            // Reset Home tab
            ivHome.setImageResource(R.drawable.ic_home_filled);
            ivHome.setColorFilter(ContextCompat.getColor(this, R.color.gray_400));
            tvHome.setTextColor(ContextCompat.getColor(this, R.color.gray_400));
            tvHome.setTypeface(null, android.graphics.Typeface.NORMAL);
    
            // Reset Bikes tab
            ivBikes.setColorFilter(ContextCompat.getColor(this, R.color.gray_400));
            tvBikes.setTextColor(ContextCompat.getColor(this, R.color.gray_400));
            tvBikes.setTypeface(null, android.graphics.Typeface.NORMAL);
    
            // Reset EMI Calculator tab
            ivEmiCalculator.setColorFilter(ContextCompat.getColor(this, R.color.gray_400));
            tvEmiCalculator.setTextColor(ContextCompat.getColor(this, R.color.gray_400));
            tvEmiCalculator.setTypeface(null, android.graphics.Typeface.NORMAL);
    
            // Reset Orders tab
            ivOrders.setColorFilter(ContextCompat.getColor(this, R.color.gray_400));
            tvOrders.setTextColor(ContextCompat.getColor(this, R.color.gray_400));
            tvOrders.setTypeface(null, android.graphics.Typeface.NORMAL);
    
            // Reset Profile tab
            ivProfile.setImageResource(R.drawable.ic_person);
            ivProfile.setColorFilter(ContextCompat.getColor(this, R.color.gray_400));
            tvProfile.setTextColor(ContextCompat.getColor(this, R.color.gray_400));
            tvProfile.setTypeface(null, android.graphics.Typeface.NORMAL);
        }
    
        @Override
        public void onBackPressed() {
            // Save data before going back
            saveUserData();
            super.onBackPressed();
        }
    }