package com.example.motovista_deep;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.motovista_deep.models.Bike; // Add this import

public class AddBikeActivity extends AppCompatActivity {

    // UI Components
    private EditText etBrand, etModel, etExShowroomPrice, etRTOCharges, etInsurance;
    private EditText etEngineCC, etMileage, etTopSpeed, etBrakingType, etFeatures;
    private Spinner spinnerType;
    private Button btnSaveBike, btnCancel, btnSelectImage;
    private ImageView btnBack, ivUploadIcon;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bike);

        // Initialize views
        initializeViews();

        // Setup spinner
        setupSpinner();

        // Set click listeners
        setupClickListeners();
    }

    private void initializeViews() {
        etBrand = findViewById(R.id.etBrand);
        etModel = findViewById(R.id.etModel);
        etExShowroomPrice = findViewById(R.id.etExShowroomPrice);
        etRTOCharges = findViewById(R.id.etRTOCharges);
        etInsurance = findViewById(R.id.etInsurance);
        etEngineCC = findViewById(R.id.etEngineCC);
        etMileage = findViewById(R.id.etMileage);
        etTopSpeed = findViewById(R.id.etTopSpeed);
        etBrakingType = findViewById(R.id.etBrakingType);
        etFeatures = findViewById(R.id.etFeatures);
        spinnerType = findViewById(R.id.spinnerType);
        btnSaveBike = findViewById(R.id.btnSaveBike);
        btnCancel = findViewById(R.id.btnCancel);
        btnBack = findViewById(R.id.btnBack);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        ivUploadIcon = findViewById(R.id.ivUploadIcon);
    }

    private void setupSpinner() {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.bike_types, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinnerType.setAdapter(adapter);
    }

    private void setupClickListeners() {
        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Cancel button
        btnCancel.setOnClickListener(v -> finish());

        // Select Image button
        btnSelectImage.setOnClickListener(v -> openImageChooser());

        // Save Bike button
        btnSaveBike.setOnClickListener(v -> saveBike());
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            // You can display a preview of the image here
            Toast.makeText(this, "Image selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveBike() {
        // Validate inputs
        if (!validateInputs()) {
            return;
        }

        // Get all values
        String brand = etBrand.getText().toString().trim();
        String model = etModel.getText().toString().trim();
        String exShowroomPrice = etExShowroomPrice.getText().toString().trim();
        String rtoCharges = etRTOCharges.getText().toString().trim();
        String insurance = etInsurance.getText().toString().trim();
        String engineCC = etEngineCC.getText().toString().trim();
        String mileage = etMileage.getText().toString().trim();
        String topSpeed = etTopSpeed.getText().toString().trim();
        String brakingType = etBrakingType.getText().toString().trim();
        String type = spinnerType.getSelectedItem().toString();
        String features = etFeatures.getText().toString().trim();
        String imageUriString = imageUri != null ? imageUri.toString() : "";

        // Create Bike object
        Bike bike = new Bike();
        bike.setBrand(brand);
        bike.setModel(model);
        bike.setExShowroomPrice(exShowroomPrice);
        bike.setRtoCharges(rtoCharges);
        bike.setInsurance(insurance);
        bike.setEngineCC(engineCC);
        bike.setMileage(mileage);
        bike.setTopSpeed(topSpeed);
        bike.setBrakingType(brakingType);
        bike.setType(type);
        bike.setFeatures(features);
        bike.setImageUri(imageUriString);

        // Save to database
        saveBikeToDatabase(bike);
    }

    private boolean validateInputs() {
        boolean isValid = true;

        if (etBrand.getText().toString().trim().isEmpty()) {
            etBrand.setError("Brand is required");
            etBrand.requestFocus();
            isValid = false;
        }

        if (etModel.getText().toString().trim().isEmpty()) {
            etModel.setError("Model is required");
            etModel.requestFocus();
            isValid = false;
        }

        if (etExShowroomPrice.getText().toString().trim().isEmpty()) {
            etExShowroomPrice.setError("Ex-showroom price is required");
            etExShowroomPrice.requestFocus();
            isValid = false;
        }

        if (etEngineCC.getText().toString().trim().isEmpty()) {
            etEngineCC.setError("Engine CC is required");
            etEngineCC.requestFocus();
            isValid = false;
        }

        return isValid;
    }

    private void saveBikeToDatabase(Bike bike) {
        // For now, show a success message and save to local database
        // TODO: Implement SQLite or API call here

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving bike...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Simulate saving process
        new android.os.Handler().postDelayed(
                () -> {
                    progressDialog.dismiss();

                    // Show success message
                    Toast.makeText(this, "Bike saved successfully!", Toast.LENGTH_SHORT).show();

                    // Clear all fields
                    clearForm();

                    // Optional: Go back to admin dashboard
                    // Intent intent = new Intent(this, AdminDashboardActivity.class);
                    // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    // startActivity(intent);
                    // finish();
                },
                1500
        );
    }

    private void clearForm() {
        etBrand.setText("");
        etModel.setText("");
        etExShowroomPrice.setText("");
        etRTOCharges.setText("");
        etInsurance.setText("");
        etEngineCC.setText("");
        etMileage.setText("");
        etTopSpeed.setText("");
        etBrakingType.setText("");
        etFeatures.setText("");
        spinnerType.setSelection(0);
        imageUri = null;
    }
}