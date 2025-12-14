package com.example.motovista_deep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class CustomerEditProfileActivity extends AppCompatActivity {

    // UI Components
    private ImageView btnBack, btnEditPhoto;
    private EditText etFullName, etEmail, etPhone;
    private CardView btnSaveChanges, btnCancel;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_edit_profile);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("CustomerPrefs", MODE_PRIVATE);

        // Initialize views
        initializeViews();

        // Load user data
        loadUserData();

        // Setup click listeners
        setupClickListeners();
    }

    private void initializeViews() {
        // Header
        btnBack = findViewById(R.id.btnBack);

        // Profile Picture
        btnEditPhoto = findViewById(R.id.btnEditPhoto);

        // Form Fields
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);

        // Buttons
        btnSaveChanges = findViewById(R.id.btnSaveChanges);
        btnCancel = findViewById(R.id.btnCancel);
    }

    private void loadUserData() {
        // Load data from SharedPreferences or use defaults
        String fullName = sharedPreferences.getString("customer_name", "Santhosh Kumar");
        String email = sharedPreferences.getString("customer_email", "santhosh.k@example.com");
        String phone = sharedPreferences.getString("customer_phone", "+91 98765 43210");

        // Set data to views
        etFullName.setText(fullName);
        etEmail.setText(email);
        etPhone.setText(phone);
    }

    private void saveUserData() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        // Basic validation
        if (fullName.isEmpty()) {
            etFullName.setError("Full name is required");
            etFullName.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }

        if (phone.isEmpty()) {
            etPhone.setError("Phone number is required");
            etPhone.requestFocus();
            return;
        }

        // Save to SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("customer_name", fullName);
        editor.putString("customer_email", email);
        editor.putString("customer_phone", phone);
        editor.apply();

        // Show success message
        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();

        // Set result and finish
        Intent resultIntent = new Intent();
        resultIntent.putExtra("updated_name", fullName);
        resultIntent.putExtra("updated_email", email);
        resultIntent.putExtra("updated_phone", phone);
        setResult(RESULT_OK, resultIntent);

        // Navigate back
        finish();
    }

    private void setupClickListeners() {
        // Back Button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Edit Photo Button
        btnEditPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Implement photo selection functionality
                Toast.makeText(CustomerEditProfileActivity.this, "Edit profile photo", Toast.LENGTH_SHORT).show();
            }
        });

        // Save Changes Button
        btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserData();
            }
        });

        // Cancel Button
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Just go back without saving
                finish();
            }
        });

        // Handle Enter key in fields
        etFullName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_NEXT) {
                etEmail.requestFocus();
                return true;
            }
            return false;
        });

        etEmail.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_NEXT) {
                etPhone.requestFocus();
                return true;
            }
            return false;
        });

        etPhone.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                saveUserData();
                return true;
            }
            return false;
        });
    }

    @Override
    public void onBackPressed() {
        // Optionally show confirmation dialog if changes were made
        finish();
    }
}