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

    private EditText etBrand, etModel, etYear, etOdometer, etPrice, etOwnerDetails, etFeatures;
    private Spinner spinnerCondition;
    private Button btnSaveBike, btnCancel;
    private ImageView btnBack;
    private LinearLayout uploadContainer, imagePreviewContainer;
    private TextView tvSelectedCount;

    private ArrayList<Uri> imageUris = new ArrayList<>();
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_second_hand_bike);

        initializeViews();
        setupConditionSpinner();
        setupClickListeners();
    }

    private void initializeViews() {
        etBrand = findViewById(R.id.etBrand);
        etModel = findViewById(R.id.etModel);
        etYear = findViewById(R.id.etYear);
        etOdometer = findViewById(R.id.etOdometer);
        etPrice = findViewById(R.id.etPrice);
        etOwnerDetails = findViewById(R.id.etOwnerDetails);
        etFeatures = findViewById(R.id.etFeatures);
        spinnerCondition = findViewById(R.id.spinnerCondition);
        btnSaveBike = findViewById(R.id.btnSaveBike);
        btnCancel = findViewById(R.id.btnCancel);
        btnBack = findViewById(R.id.btnBack);
        uploadContainer = findViewById(R.id.uploadContainer);

        // Initialize image preview container
        imagePreviewContainer = findViewById(R.id.imagePreviewContainer);
        if (imagePreviewContainer == null) {
            // Create a new container if not in XML
            imagePreviewContainer = new LinearLayout(this);
            imagePreviewContainer.setOrientation(LinearLayout.HORIZONTAL);
        }

        // Initialize selected count text view
        tvSelectedCount = new TextView(this);
        tvSelectedCount.setTextSize(12);
        tvSelectedCount.setTextColor(getResources().getColor(R.color.gray_600));
        tvSelectedCount.setVisibility(View.GONE);
    }

    private void setupConditionSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.bike_conditions, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCondition.setAdapter(adapter);
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

    private void saveBike() {
        if (!validateInputs()) {
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving bike...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        if (!imageUris.isEmpty()) {
            uploadImagesThenSaveBike(progressDialog);
        } else {
            saveBikeData("", progressDialog);
        }
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
        return true;
    }

    private void uploadImagesThenSaveBike(ProgressDialog progressDialog) {
        try {
            List<MultipartBody.Part> imageParts = new ArrayList<>();

            // ✅ IMPORTANT: Create bike_type parameter
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

            // ✅ Call correct method with bike_type
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
                                    saveBikeData(imagePaths, progressDialog);
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(AddSecondHandBikeActivity.this,
                                            "Upload failed: " + response.body().getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                progressDialog.dismiss();
                                // Log error
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
                            t.printStackTrace(); // Add this to see full error
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
            // Try to get real path first
            String realPath = getRealPathFromUri(uri);
            if (realPath != null) {
                File file = new File(realPath);
                if (file.exists()) return file;
            }

            // If not, create temp file from stream
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
        SecondHandBikeRequest request = new SecondHandBikeRequest(
                etBrand.getText().toString().trim(),
                etModel.getText().toString().trim(),
                etYear.getText().toString().trim(),
                etOdometer.getText().toString().trim(),
                etPrice.getText().toString().trim(),
                spinnerCondition.getSelectedItem().toString(),
                etOwnerDetails.getText().toString().trim(),
                etFeatures.getText().toString().trim(),
                imagePaths
        );

        String token = SharedPrefManager.getInstance(this).getToken();
        if (token == null || token.isEmpty()) {
            progressDialog.dismiss();
            Toast.makeText(this, "Please login again", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = RetrofitClient.getApiService();
        apiService.addSecondHandBike("Bearer " + token, request)
                .enqueue(new Callback<GenericResponse>() {
                    @Override
                    public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                        progressDialog.dismiss();

                        if (response.isSuccessful() && response.body() != null) {
                            String status = response.body().getStatus();
                            if ("success".equals(status)) {
                                Toast.makeText(AddSecondHandBikeActivity.this,
                                        "Second-hand bike saved successfully!",
                                        Toast.LENGTH_SHORT).show();
                                new Handler().postDelayed(() -> {
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

                    @Override
                    public void onFailure(Call<GenericResponse> call, Throwable t) {
                        progressDialog.dismiss();
                        Toast.makeText(AddSecondHandBikeActivity.this,
                                "Network error: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, AdminDashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}