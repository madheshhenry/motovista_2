package com.example.motovista;
import com.example.motovista.api.ApiService;
import com.example.motovista.api.ApiClient;
import com.example.motovista.models.ApiResponse;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.bumptech.glide.Glide;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.io.File;
import java.io.IOException;
import java.util.Locale;

public class AddBikeActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PICK = 1001;
    private static final int REQUEST_READ_STORAGE = 1002;
    private ImageView ivPreview;
    private Uri selectedImageUri;
    private EditText etBrand, etModel, etExShowroom, etRto, etInsurance, etEngine, etMileage, etTopSpeed, etBraking, etFeatures;
    private Spinner spinnerType;
    private Button btnSave, btnCancel;
    private ApiService apiService;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bike);

        // Views
        ivPreview = findViewById(R.id.ivPreview);
        etBrand = findViewById(R.id.etBrand);
        etModel = findViewById(R.id.etModel);
        etExShowroom = findViewById(R.id.etExShowroom);
        etRto = findViewById(R.id.etRto);
        etInsurance = findViewById(R.id.etInsurance);
        etEngine = findViewById(R.id.etEngine);
        etMileage = findViewById(R.id.etMileage);
        etTopSpeed = findViewById(R.id.etTopSpeed);
        etBraking = findViewById(R.id.etBraking);
        etFeatures = findViewById(R.id.etFeatures);
        spinnerType = findViewById(R.id.spinnerType);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        progressBar = new ProgressBar(this);
        progressBar.setVisibility(View.GONE);

        // Spinner setup
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                new String[]{"Sports", "Commuter", "Cruiser", "Scooter", "Other"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);

        // Setup ApiService - use your ApiClient.getClient() if you have one
        apiService = ApiClient.getClient().create(ApiService.class);

        ivPreview.setOnClickListener(v -> pickImageWithPermissionCheck());

        btnCancel.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> {
            if (validateInputs()) uploadBike();

        });

        // optionally prefill from SharedPreferences (example of SharedPreferences usage)
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String lastBrand = prefs.getString("last_brand", "");
        if (!lastBrand.isEmpty()) etBrand.setText(lastBrand);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> onBackPressed());

    }

    private void pickImageWithPermissionCheck() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ → Request READ_MEDIA_IMAGES
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                        REQUEST_READ_STORAGE
                );
            } else {
                pickImage();
            }

        } else {
            // Android 10–12 → Request READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_READ_STORAGE
                );
            } else {
                pickImage();
            }
        }
    }


    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Image"), REQUEST_CODE_PICK);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_READ_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImage();
            } else {
                Toast.makeText(this, "Storage permission required to pick image", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            // preview with Glide (centerCrop so it fits nicely)
            Glide.with(this).load(selectedImageUri).centerCrop().into(ivPreview);
        }
    }

    private boolean validateInputs() {
        if (etBrand.getText().toString().trim().isEmpty()) { etBrand.setError("Required"); return false; }
        if (etModel.getText().toString().trim().isEmpty()) { etModel.setError("Required"); return false; }
        if (selectedImageUri == null) { Toast.makeText(this, "Please upload an image", Toast.LENGTH_SHORT).show(); return false; }
        return true;
    }

    private void uploadBike() {
        btnSave.setEnabled(false);
        btnSave.setText("Uploading...");

        try {
            // Prepare RequestBody fields
            RequestBody brand = RequestBody.create(MediaType.parse("text/plain"), etBrand.getText().toString().trim());
            RequestBody model = RequestBody.create(MediaType.parse("text/plain"), etModel.getText().toString().trim());
            RequestBody ex = RequestBody.create(MediaType.parse("text/plain"), etExShowroom.getText().toString().trim());
            RequestBody rto = RequestBody.create(MediaType.parse("text/plain"), etRto.getText().toString().trim());
            RequestBody ins = RequestBody.create(MediaType.parse("text/plain"), etInsurance.getText().toString().trim());
            RequestBody engine = RequestBody.create(MediaType.parse("text/plain"), etEngine.getText().toString().trim());
            RequestBody mileage = RequestBody.create(MediaType.parse("text/plain"), etMileage.getText().toString().trim());
            RequestBody top = RequestBody.create(MediaType.parse("text/plain"), etTopSpeed.getText().toString().trim());
            RequestBody braking = RequestBody.create(MediaType.parse("text/plain"), etBraking.getText().toString().trim());
            RequestBody type = RequestBody.create(MediaType.parse("text/plain"), spinnerType.getSelectedItem().toString());
            RequestBody colors = RequestBody.create(MediaType.parse("text/plain"), "red,blue,black"); // hook this to actual selected colors
            RequestBody features = RequestBody.create(MediaType.parse("text/plain"), etFeatures.getText().toString().trim());

            // Image file part
            String filePath = FileUtils.getPath(this, selectedImageUri);
            if (filePath == null) {
                Toast.makeText(this, "Unable to read image, try another", Toast.LENGTH_SHORT).show();
                btnSave.setEnabled(true);
                btnSave.setText("Save Bike");
                return;
            }
            File file = new File(filePath);
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);

            Call<ApiResponse> call = apiService.uploadBike(brand, model, ex, rto, ins, engine, mileage, top, braking, type, colors, features, body);
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    btnSave.setEnabled(true);
                    btnSave.setText("Save Bike");
                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse res = response.body();
                        if (res.success) {
                            // Optionally save last brand in SharedPreferences
                            getSharedPreferences("app_prefs", MODE_PRIVATE).edit().putString("last_brand", etBrand.getText().toString().trim()).apply();
                            Toast.makeText(AddBikeActivity.this, "Bike saved successfully", Toast.LENGTH_LONG).show();
                            finish(); // go back to admin dashboard or wherever
                        } else {
                            Toast.makeText(AddBikeActivity.this, res.message, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(AddBikeActivity.this, "Server error", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    btnSave.setEnabled(true);
                    btnSave.setText("Save Bike");
                    Toast.makeText(AddBikeActivity.this, "Upload failed: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("AddBike", "onFailure", t);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            btnSave.setEnabled(true);
            btnSave.setText("Save Bike");
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
