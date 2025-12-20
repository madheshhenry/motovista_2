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
        // Load from SharedPreferences if you want to pre-fill
        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        String name = prefs.getString("fullName", "");
        String mobile = prefs.getString("mobileNumber", "");

        // Set hints or pre-filled values
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

        // Save user data to SharedPreferences for future use
        saveUserData(fullName, mobile);

        // Show success message
        Toast.makeText(this, "✓ Request submitted successfully!", Toast.LENGTH_LONG).show();

        // For demo: Show submitted data
        String message = "Submitted Details:\n" +
                "Name: " + fullName + "\n" +
                "Mobile: " + mobile + "\n" +
                "Brand: " + brand + "\n" +
                "Model: " + model + "\n" +
                "Features: " + (features.isEmpty() ? "None" : features) + "\n" +
                "Email: " + (email.isEmpty() ? "Not provided" : email);

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

        // Clear form
        clearForm();

        // Go back after delay
        btnSubmit.postDelayed(this::finish, 2000);
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