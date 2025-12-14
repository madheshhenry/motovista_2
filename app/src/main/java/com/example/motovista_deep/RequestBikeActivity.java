package com.example.motovista_deep;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class RequestBikeActivity extends AppCompatActivity {

    // Input fields
    private EditText etBikeBrand, etBikeModel, etFeatures, etEmail;
    private TextView tvFullName, tvMobileNumber;

    // Buttons
    private LinearLayout btnBack;
    private Button btnSubmit;

    // User info
    private String fullName, mobileNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_bike);

        // Remove action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize views
        initializeViews();

        // Load user data
        loadUserData();

        // Setup listeners
        setupClickListeners();
        setupFocusListeners();
    }

    private void initializeViews() {
        // Back button
        btnBack = findViewById(R.id.btnBack);

        // Input fields
        etBikeBrand = findViewById(R.id.etBikeBrand);
        etBikeModel = findViewById(R.id.etBikeModel);
        etFeatures = findViewById(R.id.etFeatures);
        etEmail = findViewById(R.id.etEmail);

        // Text views for read-only fields
        tvFullName = findViewById(R.id.tvFullName);
        tvMobileNumber = findViewById(R.id.tvMobileNumber);

        // Submit button
        btnSubmit = findViewById(R.id.btnSubmit);
    }

    private void loadUserData() {
        // Load from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        fullName = sharedPreferences.getString("fullName", "Santhosh Kumar");
        mobileNumber = sharedPreferences.getString("mobileNumber", "+91 98765 43210");

        // Set text
        tvFullName.setText(fullName);
        tvMobileNumber.setText(mobileNumber);
    }

    private void setupFocusListeners() {
        View.OnFocusChangeListener focusListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    v.setBackgroundResource(R.drawable.input_background_focused);
                } else {
                    v.setBackgroundResource(R.drawable.input_background);
                }
            }
        };

        etBikeBrand.setOnFocusChangeListener(focusListener);
        etBikeModel.setOnFocusChangeListener(focusListener);
        etFeatures.setOnFocusChangeListener(focusListener);
        etEmail.setOnFocusChangeListener(focusListener);
    }

    private void setupClickListeners() {
        // Back button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Submit button
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitRequest();
            }
        });
    }

    private void submitRequest() {
        // Get values
        String bikeBrand = etBikeBrand.getText().toString().trim();
        String bikeModel = etBikeModel.getText().toString().trim();
        String features = etFeatures.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        // Validation
        if (bikeBrand.isEmpty()) {
            etBikeBrand.setError("Please enter bike brand");
            etBikeBrand.requestFocus();
            return;
        }

        if (bikeModel.isEmpty()) {
            etBikeModel.setError("Please enter bike model");
            etBikeModel.requestFocus();
            return;
        }

        // Email validation (optional)
        if (!email.isEmpty() && !isValidEmail(email)) {
            etEmail.setError("Please enter a valid email address");
            etEmail.requestFocus();
            return;
        }

        // Show success
        Toast.makeText(this, "Bike request submitted successfully!", Toast.LENGTH_SHORT).show();

        // For demo, show what was submitted
        String message = "Brand: " + bikeBrand + "\n" +
                "Model: " + bikeModel + "\n" +
                "Features: " + features + "\n" +
                "Email: " + (email.isEmpty() ? "Not provided" : email);

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

        // Clear form
        clearForm();

        // Go back after delay
        btnSubmit.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 2000);
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void clearForm() {
        etBikeBrand.setText("");
        etBikeModel.setText("");
        etFeatures.setText("");
        etEmail.setText("");

        // Remove errors
        etBikeBrand.setError(null);
        etBikeModel.setError(null);
        etEmail.setError(null);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}