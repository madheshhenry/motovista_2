package com.example.motovista_deep;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.motovista_deep.api.ApiService;
import com.example.motovista_deep.api.RetrofitClient;
import com.example.motovista_deep.helpers.SharedPrefManager;
import com.example.motovista_deep.models.AddBikeRequest;
import com.example.motovista_deep.models.Bike;
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

public class AddBikeActivity extends AppCompatActivity {

    // UI Components
    private EditText etBrand, etModel, etOnRoadPrice;
    private EditText etEngineCC, etMileage, etTopSpeed, etBrakingType, etFeatures;
    private Spinner spinnerType;
    private Button btnSaveBike, btnCancel;
    private ImageView btnBack, ivUploadIcon;

    // Image handling
    private LinearLayout imagePreviewContainer;
    private ArrayList<Uri> imageUris = new ArrayList<>();

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bike);

        initializeViews();
        setupSpinner();
        setupClickListeners();
    }

    private void initializeViews() {
        etBrand = findViewById(R.id.etBrand);
        etModel = findViewById(R.id.etModel);
        etOnRoadPrice = findViewById(R.id.etOnRoadPrice);
        etEngineCC = findViewById(R.id.etEngineCC);
        etMileage = findViewById(R.id.etMileage);
        etTopSpeed = findViewById(R.id.etTopSpeed);
        etBrakingType = findViewById(R.id.etBrakingType);
        etFeatures = findViewById(R.id.etFeatures);
        spinnerType = findViewById(R.id.spinnerType);
        btnSaveBike = findViewById(R.id.btnSaveBike);
        btnCancel = findViewById(R.id.btnCancel);
        btnBack = findViewById(R.id.btnBack);
        ivUploadIcon = findViewById(R.id.ivUploadIcon);

        // Initialize image preview container
        imagePreviewContainer = findViewById(R.id.imagePreviewContainer);
        if (imagePreviewContainer != null) {
            imagePreviewContainer.setVisibility(View.GONE);
        }
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.bike_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);

        // Set default to "Sports"
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).toString().equalsIgnoreCase("Sports")) {
                spinnerType.setSelection(i);
                break;
            }
        }
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnCancel.setOnClickListener(v -> finish());

        View uploadContainer = findViewById(R.id.cardUploadImage);
        uploadContainer.setOnClickListener(v -> openImageChooser());
        ivUploadIcon.setOnClickListener(v -> openImageChooser());

        btnSaveBike.setOnClickListener(v -> saveBike());
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // Enable multiple selection
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

                    // Show first image in upload icon
                    if (!imageUris.isEmpty()) {
                        ivUploadIcon.setImageURI(imageUris.get(0));
                        ivUploadIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        ivUploadIcon.setColorFilter(null);
                        ivUploadIcon.setBackgroundResource(0);
                    }

                    showImagePreviews();
                    Toast.makeText(this, count + " image(s) selected", Toast.LENGTH_SHORT).show();

                } else if (data.getData() != null) {
                    // Single image selected
                    imageUris.add(data.getData());

                    ivUploadIcon.setImageURI(data.getData());
                    ivUploadIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    ivUploadIcon.setColorFilter(null);
                    ivUploadIcon.setBackgroundResource(0);

                    showImagePreviews();
                    Toast.makeText(this, "1 image selected", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void showImagePreviews() {
        // Clear previous previews
        if (imagePreviewContainer != null) {
            imagePreviewContainer.removeAllViews();

            if (!imageUris.isEmpty()) {
                imagePreviewContainer.setVisibility(View.VISIBLE);

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
                imagePreviewContainer.setVisibility(View.GONE);
            }
        }
    }

    private void saveBike() {
        if (!validateInputs()) {
            return;
        }

        String brand = etBrand.getText().toString().trim();
        String model = etModel.getText().toString().trim();
        String onRoadPrice = etOnRoadPrice.getText().toString().trim();
        String engineCC = etEngineCC.getText().toString().trim();
        String mileage = etMileage.getText().toString().trim();
        String topSpeed = etTopSpeed.getText().toString().trim();
        String brakingType = etBrakingType.getText().toString().trim();
        String type = spinnerType.getSelectedItem().toString();
        String features = etFeatures.getText().toString().trim();

        Bike bike = new Bike(brand, model, onRoadPrice, engineCC, mileage, topSpeed, brakingType, type, features, "");
        saveBikeToDatabase(bike);
    }

    private boolean validateInputs() {
        if (etBrand.getText().toString().trim().isEmpty()) {
            etBrand.setError("Brand is required");
            etBrand.requestFocus();
            return false;
        }
        if (etModel.getText().toString().trim().isEmpty()) {
            etModel.setError("Model is required");
            etModel.requestFocus();
            return false;
        }
        if (etOnRoadPrice.getText().toString().trim().isEmpty()) {
            etOnRoadPrice.setError("On-road price is required");
            etOnRoadPrice.requestFocus();
            return false;
        }
        if (etEngineCC.getText().toString().trim().isEmpty()) {
            etEngineCC.setError("Engine CC is required");
            etEngineCC.requestFocus();
            return false;
        }
        return true;
    }

    private void saveBikeToDatabase(Bike bike) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving bike...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        if (!imageUris.isEmpty()) {
            uploadImagesThenSaveBike(bike, progressDialog);
        } else {
            saveBikeData(bike, "", progressDialog);
        }
    }

    private void uploadImagesThenSaveBike(Bike bike, ProgressDialog progressDialog) {
        try {
            // Prepare multiple image files
            List<MultipartBody.Part> imageParts = new ArrayList<>();

            // Add bike type (new)
            RequestBody bikeType = RequestBody.create(MediaType.parse("text/plain"), "new");

            for (int i = 0; i < imageUris.size(); i++) {
                Uri uri = imageUris.get(i);
                File imageFile = getFileFromUri(uri);
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
                                    String imagePaths = response.body().getAllPathsAsJson();
                                    saveBikeData(bike, imagePaths, progressDialog);
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(AddBikeActivity.this,
                                            "Upload failed: " + response.body().getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                progressDialog.dismiss();
                                // Log the error
                                try {
                                    String errorBody = response.errorBody().string();
                                    Toast.makeText(AddBikeActivity.this,
                                            "Server error: " + errorBody,
                                            Toast.LENGTH_LONG).show();
                                } catch (Exception e) {
                                    Toast.makeText(AddBikeActivity.this,
                                            "Server error: " + response.code(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<UploadBikeImageResponse> call, Throwable t) {
                            progressDialog.dismiss();
                            Toast.makeText(AddBikeActivity.this,
                                    "Network error: " + t.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            progressDialog.dismiss();
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void saveBikeData(Bike bike, String imagePaths, ProgressDialog progressDialog) {
        AddBikeRequest request = new AddBikeRequest(
                bike.getBrand(),
                bike.getModel(),
                bike.getOnRoadPrice(),
                bike.getEngineCC(),
                bike.getMileage(),
                bike.getTopSpeed(),
                bike.getBrakingType(),
                bike.getType(),
                bike.getFeatures(),
                imagePaths
        );

        String token = SharedPrefManager.getInstance(this).getToken();
        if (token == null || token.isEmpty()) {
            progressDialog.dismiss();
            Toast.makeText(this, "Please login again", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = RetrofitClient.getApiService();
        apiService.addBike("Bearer " + token, request)
                .enqueue(new Callback<GenericResponse>() {
                    @Override
                    public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                        progressDialog.dismiss();

                        if (response.isSuccessful() && response.body() != null) {
                            String status = response.body().getStatus();
                            if ("success".equals(status)) {
                                Toast.makeText(AddBikeActivity.this,
                                        "Bike saved successfully!",
                                        Toast.LENGTH_SHORT).show();
                                clearForm();
                                new Handler().postDelayed(() -> finish(), 1500);
                            } else {
                                Toast.makeText(AddBikeActivity.this,
                                        response.body().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(AddBikeActivity.this,
                                    "Server error: " + response.code(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<GenericResponse> call, Throwable t) {
                        progressDialog.dismiss();
                        Toast.makeText(AddBikeActivity.this,
                                "Network error: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void clearForm() {
        etBrand.setText("");
        etModel.setText("");
        etOnRoadPrice.setText("");
        etEngineCC.setText("");
        etMileage.setText("");
        etTopSpeed.setText("");
        etBrakingType.setText("");
        etFeatures.setText("");
        spinnerType.setSelection(0);

        // Clear images
        imageUris.clear();
        if (imagePreviewContainer != null) {
            imagePreviewContainer.removeAllViews();
            imagePreviewContainer.setVisibility(View.GONE);
        }

        // Reset upload icon
        ivUploadIcon.setImageResource(R.drawable.ic_upload);
        ivUploadIcon.setColorFilter(getResources().getColor(R.color.gray_400));
        ivUploadIcon.setScaleType(ImageView.ScaleType.FIT_CENTER);
        ivUploadIcon.setBackgroundResource(R.drawable.upload_background);
    }
}