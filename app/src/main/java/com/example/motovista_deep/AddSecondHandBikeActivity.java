package com.example.motovista_deep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

public class AddSecondHandBikeActivity extends AppCompatActivity {

    private EditText etBrand, etModel, etYear, etOdometer, etPrice, etOwnerDetails, etFeatures;
    private Spinner spinnerCondition;
    private Button btnSaveBike, btnCancel;
    private ImageView btnBack;
    private View uploadContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_second_hand_bike);

        // Initialize views
        initializeViews();

        // Setup condition spinner
        setupConditionSpinner();

        // Setup click listeners
        setupClickListeners();
    }

    private void initializeViews() {
        // Text inputs
        etBrand = findViewById(R.id.etBrand);
        etModel = findViewById(R.id.etModel);
        etYear = findViewById(R.id.etYear);
        etOdometer = findViewById(R.id.etOdometer);
        etPrice = findViewById(R.id.etPrice);
        etOwnerDetails = findViewById(R.id.etOwnerDetails);
        etFeatures = findViewById(R.id.etFeatures);

        // Spinner
        spinnerCondition = findViewById(R.id.spinnerCondition);

        // Buttons
        btnSaveBike = findViewById(R.id.btnSaveBike);
        btnCancel = findViewById(R.id.btnCancel);
        btnBack = findViewById(R.id.btnBack);

        // Upload container
        uploadContainer = findViewById(R.id.uploadContainer);
    }

    private void setupConditionSpinner() {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.bike_conditions, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinnerCondition.setAdapter(adapter);
    }

    private void setupClickListeners() {
        // Back button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Cancel button
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCancelConfirmation();
            }
        });

        // Save Bike button
        btnSaveBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveBike();
            }
        });

        // Upload container click
        uploadContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });
    }

    private void saveBike() {
        // Get all input values
        String brand = etBrand.getText().toString().trim();
        String model = etModel.getText().toString().trim();
        String year = etYear.getText().toString().trim();
        String odometer = etOdometer.getText().toString().trim();
        String price = etPrice.getText().toString().trim();
        String condition = spinnerCondition.getSelectedItem().toString();
        String ownerDetails = etOwnerDetails.getText().toString().trim();
        String features = etFeatures.getText().toString().trim();

        // Validate required fields
        if (TextUtils.isEmpty(brand)) {
            etBrand.setError("Brand is required");
            etBrand.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(model)) {
            etModel.setError("Model is required");
            etModel.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(year)) {
            etYear.setError("Year is required");
            etYear.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(odometer)) {
            etOdometer.setError("Odometer is required");
            etOdometer.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(price)) {
            etPrice.setError("Price is required");
            etPrice.requestFocus();
            return;
        }

        // Create bike data object
        BikeData bikeData = new BikeData();
        bikeData.setBrand(brand);
        bikeData.setModel(model);
        bikeData.setYear(year);
        bikeData.setOdometer(odometer);
        bikeData.setPrice(price);
        bikeData.setCondition(condition);
        bikeData.setOwnerDetails(ownerDetails);
        bikeData.setFeatures(features);
        bikeData.setType("second_hand"); // Mark as second-hand bike

        // Show success message
        Toast.makeText(this, "Second-hand bike saved successfully!", Toast.LENGTH_SHORT).show();

        // Navigate back to admin dashboard
        Intent intent = new Intent(AddSecondHandBikeActivity.this, AdminDashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void openImagePicker() {
        // TODO: Implement image picker
        Toast.makeText(this, "Image upload functionality coming soon!", Toast.LENGTH_SHORT).show();
    }

    private void showCancelConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cancel")
                .setMessage("Are you sure you want to cancel? All unsaved data will be lost.")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Navigate back to admin dashboard
                    onBackPressed();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, AdminDashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    // Model class for bike data
    private static class BikeData {
        private String brand;
        private String model;
        private String year;
        private String odometer;
        private String price;
        private String condition;
        private String ownerDetails;
        private String features;
        private String type;

        public String getBrand() { return brand; }
        public void setBrand(String brand) { this.brand = brand; }

        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }

        public String getYear() { return year; }
        public void setYear(String year) { this.year = year; }

        public String getOdometer() { return odometer; }
        public void setOdometer(String odometer) { this.odometer = odometer; }

        public String getPrice() { return price; }
        public void setPrice(String price) { this.price = price; }

        public String getCondition() { return condition; }
        public void setCondition(String condition) { this.condition = condition; }

        public String getOwnerDetails() { return ownerDetails; }
        public void setOwnerDetails(String ownerDetails) { this.ownerDetails = ownerDetails; }

        public String getFeatures() { return features; }
        public void setFeatures(String features) { this.features = features; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
    }
}