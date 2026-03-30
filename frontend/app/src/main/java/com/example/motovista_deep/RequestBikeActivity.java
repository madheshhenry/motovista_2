package com.example.motovista_deep;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.motovista_deep.helpers.SharedPrefManager;
import com.example.motovista_deep.models.User;

public class RequestBikeActivity extends AppCompatActivity {

    // Input fields
    private EditText etBikeBrand, etBikeModel, etFeatures, etEmail, etFullName, etMobileNumber;
    private LinearLayout btnBack;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_bike);

        // Hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Set status bar color
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(0xFFF8F9FA); // Same as background

        // Initialize views
        initializeViews();

        // Load user data (optional - can be loaded from SharedPreferences)
        loadUserData();

        // Setup listeners
        setupListeners();
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);

        etBikeBrand = findViewById(R.id.etBikeBrand);
        etBikeModel = findViewById(R.id.etBikeModel);
        etFeatures = findViewById(R.id.etFeatures);
        etEmail = findViewById(R.id.etEmail);
        etFullName = findViewById(R.id.etFullName);
        etMobileNumber = findViewById(R.id.etMobileNumber);

        btnSubmit = findViewById(R.id.btnSubmit);
    }

    private void loadUserData() {
        // First try to load from logged-in user profile
        User user = SharedPrefManager.getInstance(this).getUser();
        if (user != null) {
            etFullName.setText(user.getFull_name());
            etMobileNumber.setText(user.getPhone());
            if (user.getEmail() != null) etEmail.setText(user.getEmail());
            return;
        }

        // Fallback to local shared preferences
        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        String name = prefs.getString("fullName", "");
        String mobile = prefs.getString("mobileNumber", "");

        if (!name.isEmpty()) {
            etFullName.setText(name);
        }

        if (!mobile.isEmpty()) {
            etMobileNumber.setText(mobile);
        }
    }

    private void setupListeners() {
        // Back button
        btnBack.setOnClickListener(v -> onBackPressed());

        // Submit button
        btnSubmit.setOnClickListener(v -> submitForm());

        // Form validation listeners
        TextWatcher validationWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                validateForm();
            }
        };

        etBikeBrand.addTextChangedListener(validationWatcher);
        etBikeModel.addTextChangedListener(validationWatcher);
        etFullName.addTextChangedListener(validationWatcher);
        etMobileNumber.addTextChangedListener(validationWatcher);
        etEmail.addTextChangedListener(validationWatcher);
    }

    private void validateForm() {
        String brand = etBikeBrand.getText().toString().trim();
        String model = etBikeModel.getText().toString().trim();
        String fullName = etFullName.getText().toString().trim();
        String mobile = etMobileNumber.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        boolean isValid = !brand.isEmpty() && !model.isEmpty() &&
                !fullName.isEmpty() && !mobile.isEmpty();

        if (!email.isEmpty()) {
            isValid = isValid && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }

        // Mobile number validation (basic - at least 10 digits)
        if (!mobile.isEmpty()) {
            String digitsOnly = mobile.replaceAll("[^0-9]", "");
            isValid = isValid && digitsOnly.length() >= 10;
        }

        btnSubmit.setEnabled(isValid);
        btnSubmit.setAlpha(isValid ? 1.0f : 0.5f);
    }

    private void submitForm() {
        String brand = etBikeBrand.getText().toString().trim();
        String model = etBikeModel.getText().toString().trim();
        String features = etFeatures.getText().toString().trim();
        String fullName = etFullName.getText().toString().trim();
        String mobile = etMobileNumber.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        // Validation
        if (brand.isEmpty()) {
            etBikeBrand.setError("Please enter bike brand");
            etBikeBrand.requestFocus();
            return;
        }

        if (model.isEmpty()) {
            etBikeModel.setError("Please enter bike model");
            etBikeModel.requestFocus();
            return;
        }

        if (fullName.isEmpty()) {
            etFullName.setError("Please enter your full name");
            etFullName.requestFocus();
            return;
        }

        if (mobile.isEmpty()) {
            etMobileNumber.setError("Please enter your mobile number");
            etMobileNumber.requestFocus();
            return;
        }

        // Mobile validation
        String digitsOnly = mobile.replaceAll("[^0-9]", "");
        if (digitsOnly.length() < 10) {
            etMobileNumber.setError("Please enter a valid mobile number (at least 10 digits)");
            etMobileNumber.requestFocus();
            return;
        }

        if (!email.isEmpty() && !isValidEmail(email)) {
            etEmail.setError("Please enter a valid email address");
            etEmail.requestFocus();
            return;
        }
        
        // Show Loading
        btnSubmit.setEnabled(false);
        btnSubmit.setText("Submitting...");

        // Get User ID if logged in
        User user = SharedPrefManager.getInstance(this).getUser();
        Integer userId = (user != null) ? user.getId() : null;

        // Create Request Object
        com.example.motovista_deep.models.BikeRequest request = 
            new com.example.motovista_deep.models.BikeRequest(brand, model, features, fullName, mobile, email, userId);

        // API Call
        com.example.motovista_deep.api.RetrofitClient.getApiService().addBikeRequest(request).enqueue(new retrofit2.Callback<com.example.motovista_deep.models.GenericResponse>() {
            @Override
            public void onResponse(retrofit2.Call<com.example.motovista_deep.models.GenericResponse> call, retrofit2.Response<com.example.motovista_deep.models.GenericResponse> response) {
                btnSubmit.setEnabled(true);
                btnSubmit.setText("Submit Request");
                
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(RequestBikeActivity.this, "✓ Request submitted successfully!", Toast.LENGTH_LONG).show();
                    saveUserData(fullName, mobile);
                    clearForm();
                    new android.os.Handler().postDelayed(() -> finish(), 1500);
                } else {
                    String msg = (response.body() != null) ? response.body().getMessage() : "Submission failed";
                    Toast.makeText(RequestBikeActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.example.motovista_deep.models.GenericResponse> call, Throwable t) {
                btnSubmit.setEnabled(true);
                btnSubmit.setText("Submit Request");
                Toast.makeText(RequestBikeActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserData(String fullName, String mobileNumber) {
        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("fullName", fullName);
        editor.putString("mobileNumber", mobileNumber);
        editor.apply();
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void clearForm() {
        etBikeBrand.setText("");
        etBikeModel.setText("");
        etFeatures.setText("");
        etEmail.setText("");

        // Don't clear full name and mobile number - keep them for convenience
        // etFullName.setText("");
        // etMobileNumber.setText("");

        // Clear errors
        etBikeBrand.setError(null);
        etBikeModel.setError(null);
        etFullName.setError(null);
        etMobileNumber.setError(null);
        etEmail.setError(null);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}