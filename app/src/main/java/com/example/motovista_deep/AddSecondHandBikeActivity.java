package com.example.motovista_deep;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import com.example.motovista_deep.api.ApiService;
import com.example.motovista_deep.api.RetrofitClient;
import com.example.motovista_deep.helpers.SharedPrefManager;
import com.example.motovista_deep.models.SecondHandBikeRequest;
import com.example.motovista_deep.models.GenericResponse;
import com.example.motovista_deep.models.UploadBikeImageResponse;

import java.io.File;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.database.Cursor;

public class AddSecondHandBikeActivity extends AppCompatActivity {

    private EditText etBrand, etModel, etYear, etOdometer, etPrice, etOwnerDetails, etConditionDetails, etEngineCC, etFeatures;
    private Spinner spinnerCondition, spinnerOwnership, spinnerBrakingType;
    private Button btnSaveBike, btnCancel;
    private ImageView btnBack;
    private LinearLayout uploadContainer, imagePreviewContainer;
    private TextView tvSelectedCount, tvTitle;

    // New fields for edit mode
    private boolean isEditMode = false;
    private int bikeId = 0;
    private ArrayList<String> existingImagePaths = new ArrayList<>();

    private ArrayList<Uri> imageUris = new ArrayList<>();
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_second_hand_bike);

        // Check if in edit mode
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("EDIT_MODE")) {
            isEditMode = intent.getBooleanExtra("EDIT_MODE", false);
            bikeId = intent.getIntExtra("BIKE_ID", 0);
        }

        initializeViews();
        setupSpinners();
        setupClickListeners();

        if (isEditMode && bikeId > 0) {
            loadBikeData();
            btnSaveBike.setText("Update Bike");
            tvTitle.setText("Edit Second-Hand Bike");
        }
    }

    private void initializeViews() {
        etBrand = findViewById(R.id.etBrand);
        etModel = findViewById(R.id.etModel);
        etYear = findViewById(R.id.etYear);
        etOdometer = findViewById(R.id.etOdometer);
        etPrice = findViewById(R.id.etPrice);
        etOwnerDetails = findViewById(R.id.etOwnerDetails);
        etConditionDetails = findViewById(R.id.etConditionDetails);
        etEngineCC = findViewById(R.id.etEngineCC);
        etFeatures = findViewById(R.id.etFeatures);
        spinnerCondition = findViewById(R.id.spinnerCondition);
        spinnerOwnership = findViewById(R.id.spinnerOwnership);
        spinnerBrakingType = findViewById(R.id.spinnerBrakingType);
        btnSaveBike = findViewById(R.id.btnSaveBike);
        btnCancel = findViewById(R.id.btnCancel);
        btnBack = findViewById(R.id.btnBack);
        uploadContainer = findViewById(R.id.uploadContainer);
        tvTitle = findViewById(R.id.tvTitle);

        // Initialize selected count text view
        tvSelectedCount = findViewById(R.id.tvSelectedCount);
        if (tvSelectedCount == null) {
            tvSelectedCount = new TextView(this);
            tvSelectedCount.setTextSize(12);
            tvSelectedCount.setTextColor(getResources().getColor(R.color.gray_600));
            tvSelectedCount.setVisibility(View.GONE);
        }

        // Initialize image preview container
        imagePreviewContainer = findViewById(R.id.imagePreviewContainer);
        if (imagePreviewContainer == null) {
            imagePreviewContainer = new LinearLayout(this);
            imagePreviewContainer.setOrientation(LinearLayout.HORIZONTAL);
        }
    }

    private void setupSpinners() {
        // Condition spinner
        ArrayAdapter<CharSequence> conditionAdapter = ArrayAdapter.createFromResource(this,
                R.array.bike_conditions, android.R.layout.simple_spinner_item);
        conditionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCondition.setAdapter(conditionAdapter);

        // Ownership spinner
        ArrayAdapter<CharSequence> ownershipAdapter = ArrayAdapter.createFromResource(this,
                R.array.ownership_types, android.R.layout.simple_spinner_item);
        ownershipAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOwnership.setAdapter(ownershipAdapter);

        // Braking type spinner for second-hand bikes
        ArrayAdapter<CharSequence> brakingAdapter = ArrayAdapter.createFromResource(this,
                R.array.braking_types_sh, android.R.layout.simple_spinner_item);
        brakingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBrakingType.setAdapter(brakingAdapter);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> onBackPressed());
        btnCancel.setOnClickListener(v -> onBackPressed());
        btnSaveBike.setOnClickListener(v -> saveBike());
        uploadContainer.setOnClickListener(v -> openImageChooser());
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Pictures"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                imageUris.clear();

                if (data.getClipData() != null) {
                    // Multiple images selected
                    int count = data.getClipData().getItemCount();

                    for (int i = 0; i < count; i++) {
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        imageUris.add(imageUri);
                    }

                    Toast.makeText(this, count + " image(s) selected", Toast.LENGTH_SHORT).show();
                } else if (data.getData() != null) {
                    // Single image selected
                    imageUris.add(data.getData());
                    Toast.makeText(this, "1 image selected", Toast.LENGTH_SHORT).show();
                }

                showImagePreviews();
            }
        }
    }

    private void showImagePreviews() {
        if (imagePreviewContainer != null) {
            imagePreviewContainer.removeAllViews();

            if (!imageUris.isEmpty()) {
                // Add selected count text
                if (tvSelectedCount != null) {
                    tvSelectedCount.setText(imageUris.size() + " image(s) selected");
                    tvSelectedCount.setVisibility(View.VISIBLE);
                }

                // Show small previews of all selected images
                for (Uri uri : imageUris) {
                    ImageView imageView = new ImageView(this);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            80, 80
                    );
                    params.setMargins(0, 0, 8, 0);
                    imageView.setLayoutParams(params);

                    imageView.setImageURI(uri);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    imageView.setBackgroundResource(R.drawable.image_preview_border);

                    imagePreviewContainer.addView(imageView);
                }
            } else {
                if (tvSelectedCount != null) {
                    tvSelectedCount.setVisibility(View.GONE);
                }
            }
        }
    }

    private void loadBikeData() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading bike data...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String token = SharedPrefManager.getInstance(this).getToken();
        if (token == null || token.isEmpty()) {
            progressDialog.dismiss();
            Toast.makeText(this, "Please login again", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = RetrofitClient.getApiService();
        apiService.getSecondHandBikeById("Bearer " + token, bikeId)
                .enqueue(new Callback<com.example.motovista_deep.models.GetSecondHandBikeByIdResponse>() {
                    @Override
                    public void onResponse(Call<com.example.motovista_deep.models.GetSecondHandBikeByIdResponse> call,
                                           Response<com.example.motovista_deep.models.GetSecondHandBikeByIdResponse> response) {
                        progressDialog.dismiss();

                        if (response.isSuccessful() && response.body() != null) {
                            com.example.motovista_deep.models.GetSecondHandBikeByIdResponse apiResponse = response.body();
                            if ("success".equals(apiResponse.getStatus())) {
                                populateForm(apiResponse.getData());
                            } else {
                                Toast.makeText(AddSecondHandBikeActivity.this,
                                        apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(AddSecondHandBikeActivity.this,
                                    "Failed to load bike data", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<com.example.motovista_deep.models.GetSecondHandBikeByIdResponse> call, Throwable t) {
                        progressDialog.dismiss();
                        Toast.makeText(AddSecondHandBikeActivity.this,
                                "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void populateForm(com.example.motovista_deep.models.GetSecondHandBikeByIdResponse.SecondHandBikeData bikeData) {
        etBrand.setText(bikeData.getBrand());
        etModel.setText(bikeData.getModel());
        etYear.setText(bikeData.getYear());
        etOdometer.setText(bikeData.getOdometer());
        etPrice.setText(bikeData.getPrice());
        etEngineCC.setText(bikeData.getEngine_cc());
        etOwnerDetails.setText(bikeData.getOwner_details());
        etConditionDetails.setText(bikeData.getCondition_details());
        etFeatures.setText(bikeData.getFeatures());

        // Set spinners
        setSpinnerSelection(spinnerCondition, bikeData.getCondition());
        setSpinnerSelection(spinnerOwnership, bikeData.getOwnership());
        setSpinnerSelection(spinnerBrakingType, bikeData.getBraking_type());

        // Load existing images
        if (bikeData.getImage_paths() != null && !bikeData.getImage_paths().isEmpty()) {
            loadExistingImages(bikeData.getImage_paths());
        }
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        if (value == null || value.isEmpty()) return;

        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private void loadExistingImages(String imagePaths) {
        try {
            existingImagePaths.clear();

            String cleaned = imagePaths.trim();
            if (cleaned.startsWith("[") && cleaned.endsWith("]")) {
                cleaned = cleaned.substring(1, cleaned.length() - 1);
            }
            cleaned = cleaned.replace("\"", "");

            String[] paths = cleaned.split(",");
            for (String path : paths) {
                path = path.trim();
                if (!path.isEmpty()) {
                    existingImagePaths.add(path);
                }
            }

            if (!existingImagePaths.isEmpty()) {
                tvSelectedCount.setText(existingImagePaths.size() + " existing image(s)");
                tvSelectedCount.setVisibility(View.VISIBLE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveBike() {
        if (!validateInputs()) {
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(isEditMode ? "Updating bike..." : "Saving bike...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        if (!imageUris.isEmpty()) {
            uploadImagesThenSaveBike(progressDialog);
        } else {
            // If no new images but we have existing images in edit mode
            if (isEditMode && !existingImagePaths.isEmpty()) {
                saveBikeData(formatImagePaths(existingImagePaths), progressDialog);
            } else {
                saveBikeData("", progressDialog);
            }
        }
    }

    private String formatImagePaths(List<String> paths) {
        if (paths == null || paths.isEmpty()) return "[]";

        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < paths.size(); i++) {
            sb.append("\"").append(paths.get(i)).append("\"");
            if (i < paths.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    private boolean validateInputs() {
        if (TextUtils.isEmpty(etBrand.getText().toString().trim())) {
            etBrand.setError("Brand is required");
            etBrand.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(etModel.getText().toString().trim())) {
            etModel.setError("Model is required");
            etModel.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(etYear.getText().toString().trim())) {
            etYear.setError("Year is required");
            etYear.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(etOdometer.getText().toString().trim())) {
            etOdometer.setError("Odometer is required");
            etOdometer.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(etPrice.getText().toString().trim())) {
            etPrice.setError("Price is required");
            etPrice.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(etEngineCC.getText().toString().trim())) {
            etEngineCC.setError("Engine CC is required");
            etEngineCC.requestFocus();
            return false;
        }
        // Note: etFeatures is optional, so don't validate it
        return true;
    }

    private void uploadImagesThenSaveBike(ProgressDialog progressDialog) {
        try {
            List<MultipartBody.Part> imageParts = new ArrayList<>();

            // Create bike_type parameter
            RequestBody bikeType = RequestBody.create(MediaType.parse("text/plain"), "second_hand");

            for (int i = 0; i < imageUris.size(); i++) {
                File imageFile = getFileFromUri(imageUris.get(i));
                if (imageFile != null && imageFile.exists()) {
                    RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
                    MultipartBody.Part imagePart = MultipartBody.Part.createFormData(
                            "bike_images[]",
                            "bike_image_" + i + ".jpg",
                            requestFile
                    );
                    imageParts.add(imagePart);
                }
            }

            if (imageParts.isEmpty()) {
                progressDialog.dismiss();
                Toast.makeText(this, "No valid image files", Toast.LENGTH_SHORT).show();
                return;
            }

            String token = SharedPrefManager.getInstance(this).getToken();
            if (token == null || token.isEmpty()) {
                progressDialog.dismiss();
                Toast.makeText(this, "Please login again", Toast.LENGTH_SHORT).show();
                return;
            }

            ApiService apiService = RetrofitClient.getApiService();

            apiService.uploadBikeImages("Bearer " + token, bikeType, imageParts)
                    .enqueue(new Callback<UploadBikeImageResponse>() {
                        @Override
                        public void onResponse(Call<UploadBikeImageResponse> call, Response<UploadBikeImageResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                String status = response.body().getStatus();
                                if ("success".equals(status)) {
                                    String imagePaths = "";
                                    if (response.body().getData() != null) {
                                        imagePaths = response.body().getAllPathsAsJson();
                                    }
                                    // In edit mode, combine with existing images
                                    if (isEditMode && !existingImagePaths.isEmpty()) {
                                        // Merge existing and new images
                                        List<String> allImages = new ArrayList<>(existingImagePaths);
                                        List<String> newImages = response.body().getData();
                                        if (newImages != null) {
                                            allImages.addAll(newImages);
                                        }
                                        saveBikeData(formatImagePaths(allImages), progressDialog);
                                    } else {
                                        saveBikeData(imagePaths, progressDialog);
                                    }
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(AddSecondHandBikeActivity.this,
                                            "Upload failed: " + response.body().getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                progressDialog.dismiss();
                                try {
                                    if (response.errorBody() != null) {
                                        String error = response.errorBody().string();
                                        Log.e("UPLOAD_ERROR", "Error: " + error);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                Toast.makeText(AddSecondHandBikeActivity.this,
                                        "Server error: " + response.code(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<UploadBikeImageResponse> call, Throwable t) {
                            progressDialog.dismiss();
                            Toast.makeText(AddSecondHandBikeActivity.this,
                                    "Network error: " + t.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            t.printStackTrace();
                        }
                    });
        } catch (Exception e) {
            progressDialog.dismiss();
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private File getFileFromUri(Uri uri) {
        try {
            String realPath = getRealPathFromUri(uri);
            if (realPath != null) {
                File file = new File(realPath);
                if (file.exists()) return file;
            }

            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream == null) return null;

            File tempFile = File.createTempFile("bike_img_", ".jpg", getCacheDir());
            FileOutputStream outputStream = new FileOutputStream(tempFile);

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            inputStream.close();
            outputStream.close();
            return tempFile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getRealPathFromUri(Uri uri) {
        Cursor cursor = null;
        try {
            String[] projection = { MediaStore.Images.Media.DATA };
            cursor = getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                return cursor.getString(columnIndex);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }

    private void saveBikeData(String imagePaths, ProgressDialog progressDialog) {
        // ✅ FIX: Ensure imagePaths is proper JSON
        if (imagePaths == null || imagePaths.isEmpty()) {
            imagePaths = "[]";
        } else if (!imagePaths.trim().startsWith("[")) {
            // If it's not JSON, wrap it as JSON array
            imagePaths = "[\"" + imagePaths + "\"]";
        }

        Log.d("SECOND_HAND_BIKE", "Final Image Paths: " + imagePaths);

        String token = SharedPrefManager.getInstance(this).getToken();
        if (token == null || token.isEmpty()) {
            progressDialog.dismiss();
            Toast.makeText(this, "Please login again", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = RetrofitClient.getApiService();

        if (isEditMode) {
            // Update existing bike
            com.example.motovista_deep.models.UpdateSecondHandBikeRequest request =
                    new com.example.motovista_deep.models.UpdateSecondHandBikeRequest(
                            bikeId,
                            etBrand.getText().toString().trim(),
                            etModel.getText().toString().trim(),
                            etYear.getText().toString().trim(),
                            etOdometer.getText().toString().trim(),
                            etPrice.getText().toString().trim(),
                            spinnerCondition.getSelectedItem().toString(),
                            spinnerOwnership.getSelectedItem().toString(),
                            etEngineCC.getText().toString().trim(),
                            spinnerBrakingType.getSelectedItem().toString(),
                            etOwnerDetails.getText().toString().trim(),
                            etConditionDetails.getText().toString().trim(),
                            etFeatures.getText().toString().trim(),
                            imagePaths
                    );

            apiService.updateSecondHandBike("Bearer " + token, request)
                    .enqueue(new Callback<GenericResponse>() {
                        @Override
                        public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                            handleSaveResponse(response, progressDialog, "Bike updated successfully!");
                        }

                        @Override
                        public void onFailure(Call<GenericResponse> call, Throwable t) {
                            handleSaveFailure(t, progressDialog);
                        }
                    });
        } else {
            // Add new bike
            SecondHandBikeRequest request = new SecondHandBikeRequest(
                    etBrand.getText().toString().trim(),
                    etModel.getText().toString().trim(),
                    etYear.getText().toString().trim(),
                    etOdometer.getText().toString().trim(),
                    etPrice.getText().toString().trim(),
                    spinnerCondition.getSelectedItem().toString(),
                    spinnerOwnership.getSelectedItem().toString(),
                    etEngineCC.getText().toString().trim(),
                    spinnerBrakingType.getSelectedItem().toString(),
                    etOwnerDetails.getText().toString().trim(),
                    etConditionDetails.getText().toString().trim(),
                    etFeatures.getText().toString().trim(),
                    imagePaths
            );

            apiService.addSecondHandBike("Bearer " + token, request)
                    .enqueue(new Callback<GenericResponse>() {
                        @Override
                        public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                            handleSaveResponse(response, progressDialog, "Bike saved successfully!");
                        }

                        @Override
                        public void onFailure(Call<GenericResponse> call, Throwable t) {
                            handleSaveFailure(t, progressDialog);
                        }
                    });
        }
    }

    private void handleSaveResponse(Response<GenericResponse> response, ProgressDialog progressDialog, String successMessage) {
        progressDialog.dismiss();

        if (response.isSuccessful() && response.body() != null) {
            String status = response.body().getStatus();
            if ("success".equals(status)) {
                Toast.makeText(AddSecondHandBikeActivity.this, successMessage, Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(() -> {
                    setResult(RESULT_OK);
                    finish();
                }, 1500);
            } else {
                Toast.makeText(AddSecondHandBikeActivity.this,
                        response.body().getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(AddSecondHandBikeActivity.this,
                    "Server error: " + response.code(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void handleSaveFailure(Throwable t, ProgressDialog progressDialog) {
        progressDialog.dismiss();
        Toast.makeText(AddSecondHandBikeActivity.this,
                "Network error: " + t.getMessage(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}