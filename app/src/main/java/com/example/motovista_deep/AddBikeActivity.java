package com.example.motovista_deep;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
// Your existing imports...
import android.graphics.Bitmap; // If you have this
import android.graphics.BitmapFactory; // Add this line
import androidx.appcompat.app.AppCompatActivity;

import com.example.motovista_deep.api.ApiService;
import com.example.motovista_deep.api.RetrofitClient;
import com.example.motovista_deep.helpers.SharedPrefManager;
import com.example.motovista_deep.models.AddBikeRequest;
import com.example.motovista_deep.models.GenericResponse;
import com.example.motovista_deep.models.UpdateBikeRequest;
import com.example.motovista_deep.models.UploadBikeImageResponse;
import com.example.motovista_deep.models.GetBikeByIdResponse;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.database.Cursor;

public class AddBikeActivity extends AppCompatActivity {

    // UI Components
    private EditText etBrand, etModel, etVariant, etEngineCC, etMileage;
    private EditText etFuelTank, etKerbWeight, etSeatHeight, etGroundClearance;
    private EditText etExShowroom, etInsurance, etRegistration, etLTRT, etTotalOnRoad;
    private EditText etFreeServices, etRegistrationProof, etPriceDisclaimer;

    private Spinner spinnerYear, spinnerFuelType, spinnerTransmission;
    private Spinner spinnerBrakingType, spinnerWarranty;

    private Button btnSaveBike, btnCancel, btnAddCustomFitting;
    private ImageView btnBack, ivUploadIcon;

    private LinearLayout uploadContainer, imagePreviewContainer;
    private LinearLayout mandatoryFittingsContainer, additionalFittingsContainer;
    private TextView tvSelectedCount, tvTitle;

    // Data holders
    private ArrayList<Uri> imageUris = new ArrayList<>();
    private Map<String, Double> mandatoryFittings = new HashMap<>();
    private Map<String, Double> additionalFittings = new HashMap<>();
    private List<CustomFitting> customFittings = new ArrayList<>();

    private boolean isEditMode = false;
    private int bikeId = 0;
    private ArrayList<String> existingImagePaths = new ArrayList<>();

    private static final int PICK_IMAGE_REQUEST = 1;

    private String[] mandatoryItems = {
            "Crash Bar", "Saree Guard", "Mirror Set",
            "Front & Rear Number Plate", "Side Stand", "Foot Rest"
    };

    private String[] additionalItems = {
            "Side Box (Fibre)", "Petrol Tank Bag", "Grip Cover", "Bag Hook",
            "Helmet", "Body Cover (Full)", "Indicator Buzzer", "Seat Cover",
            "Ladies Handle", "Engine Guard", "Bumper SS"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bike);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("EDIT_MODE")) {
            isEditMode = intent.getBooleanExtra("EDIT_MODE", false);
            bikeId = intent.getIntExtra("BIKE_ID", 0);
        }

        initializeViews();
        setupSpinners();
        setupFittings();
        setupClickListeners();
        setupPriceCalculators();

        if (isEditMode && bikeId > 0) {
            loadBikeData();
            btnSaveBike.setText("Update Bike");
            tvTitle.setText("Edit Bike");
        }
    }

    private void initializeViews() {
        etBrand = findViewById(R.id.etBrand);
        etModel = findViewById(R.id.etModel);
        etVariant = findViewById(R.id.etVariant);
        etEngineCC = findViewById(R.id.etEngineCC);
        etMileage = findViewById(R.id.etMileage);
        etFuelTank = findViewById(R.id.etFuelTank);
        etKerbWeight = findViewById(R.id.etKerbWeight);
        etSeatHeight = findViewById(R.id.etSeatHeight);
        etGroundClearance = findViewById(R.id.etGroundClearance);
        etExShowroom = findViewById(R.id.etExShowroom);
        etInsurance = findViewById(R.id.etInsurance);
        etRegistration = findViewById(R.id.etRegistration);
        etLTRT = findViewById(R.id.etLTRT);
        etTotalOnRoad = findViewById(R.id.etTotalOnRoad);
        etFreeServices = findViewById(R.id.etFreeServices);
        etRegistrationProof = findViewById(R.id.etRegistrationProof);
        etPriceDisclaimer = findViewById(R.id.etPriceDisclaimer);

        spinnerYear = findViewById(R.id.spinnerYear);
        spinnerFuelType = findViewById(R.id.spinnerFuelType);
        spinnerTransmission = findViewById(R.id.spinnerTransmission);
        spinnerBrakingType = findViewById(R.id.spinnerBrakingType);
        spinnerWarranty = findViewById(R.id.spinnerWarranty);

        btnSaveBike = findViewById(R.id.btnSaveBike);
        btnCancel = findViewById(R.id.btnCancel);
        btnAddCustomFitting = findViewById(R.id.btnAddCustomFitting);
        btnBack = findViewById(R.id.btnBack);
        ivUploadIcon = findViewById(R.id.ivUploadIcon);

        uploadContainer = findViewById(R.id.uploadContainer);
        imagePreviewContainer = findViewById(R.id.imagePreviewContainer);
        mandatoryFittingsContainer = findViewById(R.id.mandatoryFittingsContainer);
        additionalFittingsContainer = findViewById(R.id.additionalFittingsContainer);
        tvSelectedCount = findViewById(R.id.tvSelectedCount);
        tvTitle = findViewById(R.id.tvTitle);

        for (String item : mandatoryItems) {
            mandatoryFittings.put(item, 0.0);
        }
        for (String item : additionalItems) {
            additionalFittings.put(item, 0.0);
        }
    }

    private void setupSpinners() {
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item,
                new String[]{"Select", "2025", "2024", "2023", "2022"}
        );
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(yearAdapter);

        ArrayAdapter<String> fuelAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item,
                new String[]{"Select", "Petrol", "Electric"}
        );
        fuelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFuelType.setAdapter(fuelAdapter);

        ArrayAdapter<String> transmissionAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item,
                new String[]{"Select", "Manual", "Automatic"}
        );
        transmissionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTransmission.setAdapter(transmissionAdapter);

        ArrayAdapter<String> brakingAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item,
                new String[]{"Select", "Drum", "Disc", "ABS"}
        );
        brakingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBrakingType.setAdapter(brakingAdapter);

        ArrayAdapter<String> warrantyAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item,
                new String[]{"2 Years", "5 Years"}
        );
        warrantyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWarranty.setAdapter(warrantyAdapter);
    }

    private void setupFittings() {
        LayoutInflater inflater = LayoutInflater.from(this);

        for (String item : mandatoryItems) {
            View fittingView = inflater.inflate(R.layout.item_fitting, mandatoryFittingsContainer, false);

            CheckBox checkBox = fittingView.findViewById(R.id.cbFitting);
            TextView tvName = fittingView.findViewById(R.id.tvFittingName);
            EditText etPrice = fittingView.findViewById(R.id.etFittingPrice);

            tvName.setText(item);
            checkBox.setChecked(true);
            checkBox.setEnabled(false);

            etPrice.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    try {
                        double price = s.toString().isEmpty() ? 0 : Double.parseDouble(s.toString());
                        mandatoryFittings.put(item, price);
                        calculateTotalPrice();
                    } catch (NumberFormatException e) {
                        mandatoryFittings.put(item, 0.0);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });

            mandatoryFittingsContainer.addView(fittingView);
        }

        for (String item : additionalItems) {
            View fittingView = inflater.inflate(R.layout.item_fitting, additionalFittingsContainer, false);

            CheckBox checkBox = fittingView.findViewById(R.id.cbFitting);
            TextView tvName = fittingView.findViewById(R.id.tvFittingName);
            EditText etPrice = fittingView.findViewById(R.id.etFittingPrice);

            tvName.setText(item);

            if (item.equals("Helmet")) {
                etPrice.setHint("FREE / 0");
            }

            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (!isChecked) {
                    etPrice.setText("");
                    additionalFittings.put(item, 0.0);
                    calculateTotalPrice();
                }
            });

            etPrice.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (checkBox.isChecked()) {
                        try {
                            double price = s.toString().isEmpty() ? 0 : Double.parseDouble(s.toString());
                            additionalFittings.put(item, price);
                            calculateTotalPrice();
                        } catch (NumberFormatException e) {
                            additionalFittings.put(item, 0.0);
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });

            additionalFittingsContainer.addView(fittingView);
        }
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnCancel.setOnClickListener(v -> finish());
        uploadContainer.setOnClickListener(v -> openImageChooser());
        ivUploadIcon.setOnClickListener(v -> openImageChooser());
        btnSaveBike.setOnClickListener(v -> saveBike());
        btnAddCustomFitting.setOnClickListener(v -> addCustomFitting());
    }

    private void setupPriceCalculators() {
        TextWatcher priceWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateTotalPrice();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        etExShowroom.addTextChangedListener(priceWatcher);
        etInsurance.addTextChangedListener(priceWatcher);
        etRegistration.addTextChangedListener(priceWatcher);
        etLTRT.addTextChangedListener(priceWatcher);
    }

    private void calculateTotalPrice() {
        try {
            double exShowroom = etExShowroom.getText().toString().isEmpty() ? 0 :
                    Double.parseDouble(etExShowroom.getText().toString());
            double insurance = etInsurance.getText().toString().isEmpty() ? 0 :
                    Double.parseDouble(etInsurance.getText().toString());
            double registration = etRegistration.getText().toString().isEmpty() ? 0 :
                    Double.parseDouble(etRegistration.getText().toString());
            double ltrt = etLTRT.getText().toString().isEmpty() ? 0 :
                    Double.parseDouble(etLTRT.getText().toString());

            double total = exShowroom + insurance + registration + ltrt;

            for (double price : mandatoryFittings.values()) {
                total += price;
            }
            for (double price : additionalFittings.values()) {
                total += price;
            }
            for (CustomFitting fitting : customFittings) {
                total += fitting.price;
            }

            etTotalOnRoad.setText(String.format("₹%.0f", total));
        } catch (NumberFormatException e) {
            etTotalOnRoad.setText("₹0");
        }
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

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imageUris.clear();

            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    imageUris.add(imageUri);
                }
                Toast.makeText(this, count + " image(s) selected", Toast.LENGTH_SHORT).show();
            } else if (data.getData() != null) {
                imageUris.add(data.getData());
                Toast.makeText(this, "1 image selected", Toast.LENGTH_SHORT).show();
            }

            showImagePreviews();
            calculateTotalPrice();
        }
    }

    private void showImagePreviews() {
        if (imagePreviewContainer != null) {
            imagePreviewContainer.removeAllViews();

            if (!imageUris.isEmpty()) {
                imagePreviewContainer.setVisibility(View.VISIBLE);
                tvSelectedCount.setVisibility(View.VISIBLE);
                tvSelectedCount.setText(imageUris.size() + " image(s) selected");

                for (Uri uri : imageUris) {
                    ImageView imageView = new ImageView(this);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(80, 80);
                    params.setMargins(0, 0, 8, 0);
                    imageView.setLayoutParams(params);
                    imageView.setImageURI(uri);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    imageView.setBackgroundResource(R.drawable.image_preview_border);
                    imagePreviewContainer.addView(imageView);
                }
            } else {
                imagePreviewContainer.setVisibility(View.GONE);
                tvSelectedCount.setVisibility(View.GONE);
            }
        }
    }

    private void addCustomFitting() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View fittingView = inflater.inflate(R.layout.item_fitting, additionalFittingsContainer, false);

        CheckBox checkBox = fittingView.findViewById(R.id.cbFitting);
        TextView tvName = fittingView.findViewById(R.id.tvFittingName);
        EditText etPrice = fittingView.findViewById(R.id.etFittingPrice);

        tvName.setText("Custom Fitting");
        etPrice.setHint("0");

        CustomFitting customFitting = new CustomFitting();
        customFittings.add(customFitting);

        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked) {
                etPrice.setText("");
                customFitting.price = 0;
                calculateTotalPrice();
            }
        });

        etPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (checkBox.isChecked()) {
                    try {
                        customFitting.price = s.toString().isEmpty() ? 0 :
                                Double.parseDouble(s.toString());
                        calculateTotalPrice();
                    } catch (NumberFormatException e) {
                        customFitting.price = 0;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        additionalFittingsContainer.addView(fittingView);
        Toast.makeText(this, "Custom fitting added", Toast.LENGTH_SHORT).show();
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
        apiService.getBikeById("Bearer " + token, bikeId)
                .enqueue(new Callback<GetBikeByIdResponse>() {
                    @Override
                    public void onResponse(Call<GetBikeByIdResponse> call, Response<GetBikeByIdResponse> response) {
                        progressDialog.dismiss();

                        if (response.isSuccessful() && response.body() != null) {
                            GetBikeByIdResponse apiResponse = response.body();
                            if ("success".equals(apiResponse.getStatus())) {
                                populateForm(apiResponse.getData());
                            } else {
                                Toast.makeText(AddBikeActivity.this,
                                        apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(AddBikeActivity.this,
                                    "Failed to load bike data", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<GetBikeByIdResponse> call, Throwable t) {
                        progressDialog.dismiss();
                        Toast.makeText(AddBikeActivity.this,
                                "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void populateForm(GetBikeByIdResponse.BikeData bikeData) {
        etBrand.setText(bikeData.getBrand());
        etModel.setText(bikeData.getModel());
        etEngineCC.setText(bikeData.getEngine_cc());
        etMileage.setText(bikeData.getMileage());

        if (bikeData.getOn_road_price() != null && !bikeData.getOn_road_price().isEmpty()) {
            etExShowroom.setText(bikeData.getOn_road_price());
        }

        setSpinnerSelection(spinnerBrakingType, bikeData.getBraking_type());
        setSpinnerSelection(spinnerTransmission, bikeData.getType());

        if (bikeData.getImage_path() != null && !bikeData.getImage_path().isEmpty()) {
            loadExistingImages(bikeData.getImage_path());
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

    private void loadExistingImages(String imagePath) {
        try {
            existingImagePaths.clear();
            String cleaned = imagePath.trim();
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

    // ========================== MAIN FIX: SAVE BIKE METHOD ==========================
    private void saveBike() {
        if (!validateInputs()) {
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(isEditMode ? "Updating bike..." : "Saving bike...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // First upload images if any
        if (!imageUris.isEmpty()) {
            uploadImagesThenSaveBike(progressDialog);
        } else {
            // Save bike without images or with existing images
            String imagePaths = isEditMode && !existingImagePaths.isEmpty() ?
                    formatImagePaths(existingImagePaths) : "[]";
            saveBikeData(imagePaths, progressDialog);
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
        if (spinnerYear.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select model year", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etEngineCC.getText().toString().trim().isEmpty()) {
            etEngineCC.setError("Engine CC is required");
            etEngineCC.requestFocus();
            return false;
        }
        if (spinnerFuelType.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select fuel type", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (spinnerTransmission.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select transmission", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (spinnerBrakingType.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select braking type", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etExShowroom.getText().toString().trim().isEmpty()) {
            etExShowroom.setError("Ex-showroom price is required");
            etExShowroom.requestFocus();
            return false;
        }
        return true;
    }

    private void uploadImagesThenSaveBike(ProgressDialog progressDialog) {
        try {
            progressDialog.setMessage("Uploading images...");

            List<MultipartBody.Part> imageParts = new ArrayList<>();
            RequestBody bikeType = RequestBody.create(MediaType.parse("text/plain"), "new");

            Log.d("IMAGE_UPLOAD", "Starting image upload. Count: " + imageUris.size());

            // Compress images before uploading
            for (int i = 0; i < imageUris.size(); i++) {
                Uri uri = imageUris.get(i);
                Log.d("IMAGE_UPLOAD", "Processing image " + (i + 1) + ": " + uri);

                File imageFile = getCompressedImageFile(uri);
                if (imageFile != null && imageFile.exists()) {
                    long fileSize = imageFile.length();
                    Log.d("IMAGE_UPLOAD", "File " + (i + 1) + " size: " + fileSize + " bytes");

                    RequestBody requestFile = RequestBody.create(
                            MediaType.parse("image/*"),
                            imageFile
                    );

                    // Use unique filename with timestamp
                    MultipartBody.Part imagePart = MultipartBody.Part.createFormData(
                            "bike_images[]",
                            "bike_" + System.currentTimeMillis() + "_" + i + ".jpg",
                            requestFile
                    );
                    imageParts.add(imagePart);
                    Log.d("IMAGE_UPLOAD", "Added image part " + (i + 1));
                } else {
                    Log.e("IMAGE_UPLOAD", "File " + (i + 1) + " is null or doesn't exist");
                }
            }

            if (imageParts.isEmpty()) {
                progressDialog.dismiss();
                Toast.makeText(this, "No valid image files to upload", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d("IMAGE_UPLOAD", "Total image parts: " + imageParts.size());

            String token = SharedPrefManager.getInstance(this).getToken();
            if (token == null || token.isEmpty()) {
                progressDialog.dismiss();
                Toast.makeText(this, "Please login again", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d("IMAGE_UPLOAD", "Making upload request...");

            ApiService apiService = RetrofitClient.getApiService();
            apiService.uploadBikeImages("Bearer " + token, bikeType, imageParts)
                    .enqueue(new Callback<UploadBikeImageResponse>() {
                        @Override
                        public void onResponse(Call<UploadBikeImageResponse> call, Response<UploadBikeImageResponse> response) {
                            Log.d("IMAGE_UPLOAD", "Response received. Code: " + response.code());

                            if (response.isSuccessful() && response.body() != null) {
                                UploadBikeImageResponse uploadResponse = response.body();
                                String status = uploadResponse.getStatus();
                                Log.d("IMAGE_UPLOAD", "Response status: " + status);

                                if ("success".equals(status)) {
                                    // ✅ ADD DEBUG LOGGING
                                    List<String> uploadedImages = uploadResponse.getData();
                                    if (uploadedImages != null) {
                                        for (int i = 0; i < uploadedImages.size(); i++) {
                                            Log.d("IMAGE_UPLOAD", "Uploaded image " + i + ": " + uploadedImages.get(i));
                                        }
                                    }

                                    String imagePaths = uploadResponse.getAllPathsAsJson();
                                    Log.d("IMAGE_UPLOAD", "Upload successful. Image paths JSON: " + imagePaths);
                                    Log.d("IMAGE_UPLOAD", "Image paths raw data: " + uploadResponse.getData());

                                    // In edit mode, combine with existing images
                                    if (isEditMode && !existingImagePaths.isEmpty()) {
                                        List<String> allImages = new ArrayList<>(existingImagePaths);
                                        List<String> newImages = uploadResponse.getData();
                                        if (newImages != null) {
                                            allImages.addAll(newImages);
                                            Log.d("IMAGE_UPLOAD", "Merged images. Total: " + allImages.size());
                                        }
                                        saveBikeData(formatImagePaths(allImages), progressDialog);
                                    } else {
                                        saveBikeData(imagePaths, progressDialog);
                                    }
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(AddBikeActivity.this,
                                            "Upload failed: " + uploadResponse.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(AddBikeActivity.this,
                                        "Upload failed with code: " + response.code(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<UploadBikeImageResponse> call, Throwable t) {
                            progressDialog.dismiss();
                            Log.e("IMAGE_UPLOAD", "Network error: " + t.getMessage());
                            Toast.makeText(AddBikeActivity.this,
                                    "Network error: " + t.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            progressDialog.dismiss();
            Log.e("IMAGE_UPLOAD", "Exception: " + e.getMessage());
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Add this new method for image compression
    private File getCompressedImageFile(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream == null) return null;

            // Decode with bounds only
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(inputStream, null, options);
            inputStream.close();

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, 1024, 1024);
            options.inJustDecodeBounds = false;

            // Decode bitmap with sample size
            inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
            inputStream.close();

            if (bitmap == null) return null;

            // Compress to JPEG
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos); // 80% quality

            // Save to temp file
            File tempFile = File.createTempFile("bike_img_", ".jpg", getCacheDir());
            FileOutputStream fos = new FileOutputStream(tempFile);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();

            // Recycle bitmap
            bitmap.recycle();

            return tempFile;
        } catch (Exception e) {
            Log.e("IMAGE_COMPRESS", "Error compressing image: " + e.getMessage());
            return getFileFromUri(uri); // Fallback to original method
        }
    }

    // Helper method to calculate sample size
    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight &&
                    (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
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

    // ========================== FIXED SAVE BIKE DATA METHOD ==========================
    private void saveBikeData(String imagePaths, ProgressDialog progressDialog) {
        Log.d("BIKE_SAVE", "=== SAVE BIKE START ===");
        Log.d("BIKE_SAVE", "Edit Mode: " + isEditMode);
        Log.d("BIKE_SAVE", "Image Paths: " + imagePaths);

        // ✅ FIX: Ensure imagePaths is proper JSON
        if (imagePaths == null || imagePaths.isEmpty()) {
            imagePaths = "[]";
        } else if (!imagePaths.trim().startsWith("[")) {
            // If it's not JSON, wrap it as JSON array
            imagePaths = "[\"" + imagePaths + "\"]";
        }

        Log.d("BIKE_SAVE", "Final Image Paths: " + imagePaths);

        // ... rest of your saveBikeData method remains the same
        // Calculate total price
        double totalPrice = 0;
        try {
            double exShowroom = etExShowroom.getText().toString().isEmpty() ? 0 :
                    Double.parseDouble(etExShowroom.getText().toString());
            double insurance = etInsurance.getText().toString().isEmpty() ? 0 :
                    Double.parseDouble(etInsurance.getText().toString());
            double registration = etRegistration.getText().toString().isEmpty() ? 0 :
                    Double.parseDouble(etRegistration.getText().toString());
            double ltrt = etLTRT.getText().toString().isEmpty() ? 0 :
                    Double.parseDouble(etLTRT.getText().toString());

            totalPrice = exShowroom + insurance + registration + ltrt;
            Log.d("BIKE_SAVE", "Total Price: " + totalPrice);
        } catch (NumberFormatException e) {
            totalPrice = 0;
            Log.e("BIKE_SAVE", "Price error: " + e.getMessage());
        }

        // Get form values
        String brand = etBrand.getText().toString().trim();
        String model = etModel.getText().toString().trim();
        String variant = etVariant.getText().toString().trim();
        String year = spinnerYear.getSelectedItem().toString();
        String engineCC = etEngineCC.getText().toString().trim();
        String fuelType = spinnerFuelType.getSelectedItem().toString();
        String transmission = spinnerTransmission.getSelectedItem().toString();
        String brakingType = spinnerBrakingType.getSelectedItem().toString();
        String mileage = etMileage.getText().toString().trim();
        String fuelTank = etFuelTank.getText().toString().trim();
        String kerbWeight = etKerbWeight.getText().toString().trim();
        String seatHeight = etSeatHeight.getText().toString().trim();
        String groundClearance = etGroundClearance.getText().toString().trim();
        String warranty = spinnerWarranty.getSelectedItem().toString();
        String freeServices = etFreeServices.getText().toString().trim();
        String registrationProof = etRegistrationProof.getText().toString().trim();
        String priceDisclaimer = etPriceDisclaimer.getText().toString().trim();
        String insurance = etInsurance.getText().toString().trim();
        String registrationCharge = etRegistration.getText().toString().trim();
        String ltrt = etLTRT.getText().toString().trim();

        // Build features string
        StringBuilder featuresBuilder = new StringBuilder();
        if (!mileage.isEmpty()) {
            featuresBuilder.append("Mileage: ").append(mileage).append(" km/l");
        }
        if (!fuelTank.isEmpty()) {
            if (featuresBuilder.length() > 0) featuresBuilder.append(", ");
            featuresBuilder.append("Fuel Tank: ").append(fuelTank).append(" L");
        }
        if (!kerbWeight.isEmpty()) {
            if (featuresBuilder.length() > 0) featuresBuilder.append(", ");
            featuresBuilder.append("Kerb Weight: ").append(kerbWeight).append(" kg");
        }
        if (!seatHeight.isEmpty()) {
            if (featuresBuilder.length() > 0) featuresBuilder.append(", ");
            featuresBuilder.append("Seat Height: ").append(seatHeight).append(" mm");
        }
        if (!groundClearance.isEmpty()) {
            if (featuresBuilder.length() > 0) featuresBuilder.append(", ");
            featuresBuilder.append("Ground Clearance: ").append(groundClearance).append(" mm");
        }

        String features = featuresBuilder.toString();
        Log.d("BIKE_SAVE", "Features: " + features);

        String token = SharedPrefManager.getInstance(this).getToken();
        if (token == null || token.isEmpty()) {
            progressDialog.dismiss();
            Toast.makeText(this, "Please login again", Toast.LENGTH_SHORT).show();
            Log.e("BIKE_SAVE", "Token is null");
            return;
        }

        Log.d("BIKE_SAVE", "Token length: " + token.length());
        ApiService apiService = RetrofitClient.getApiService();

        if (isEditMode) {
            // UPDATE BIKE
            Log.d("BIKE_SAVE", "Updating bike ID: " + bikeId);

            UpdateBikeRequest request = new UpdateBikeRequest(
                    bikeId,
                    brand, model, variant, year,
                    engineCC, fuelType, transmission,
                    brakingType,
                    String.valueOf(totalPrice),  // on_road_price
                    insurance, registrationCharge, ltrt,
                    mileage, fuelTank, kerbWeight, seatHeight, groundClearance,
                    warranty, freeServices, registrationProof, priceDisclaimer,
                    "NEW", features, imagePaths
            );

            Log.d("BIKE_SAVE", "Update request created");

            apiService.updateBike("Bearer " + token, request)
                    .enqueue(new Callback<GenericResponse>() {
                        @Override
                        public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                            Log.d("BIKE_SAVE", "Update response code: " + response.code());
                            progressDialog.dismiss();

                            if (response.isSuccessful() && response.body() != null) {
                                GenericResponse apiResponse = response.body();
                                Log.d("BIKE_SAVE", "Update status: " + apiResponse.getStatus());

                                if ("success".equals(apiResponse.getStatus())) {
                                    Toast.makeText(AddBikeActivity.this,
                                            "Bike updated successfully",
                                            Toast.LENGTH_SHORT).show();
                                    setResult(RESULT_OK);
                                    finish();
                                } else {
                                    Toast.makeText(AddBikeActivity.this,
                                            "Update failed: " + apiResponse.getMessage(),
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
                            Log.e("BIKE_SAVE", "Update failure: " + t.getMessage());
                            Toast.makeText(AddBikeActivity.this,
                                    "Network error: " + t.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // ADD NEW BIKE
            Log.d("BIKE_SAVE", "Adding new bike");

            AddBikeRequest request = new AddBikeRequest(
                    brand, model, variant, year,
                    engineCC, fuelType, transmission,
                    brakingType,
                    String.valueOf(totalPrice),  // on_road_price
                    insurance, registrationCharge, ltrt,
                    mileage, fuelTank, kerbWeight, seatHeight, groundClearance,
                    warranty, freeServices, registrationProof, priceDisclaimer,
                    "NEW", features, imagePaths
            );

            Log.d("BIKE_SAVE", "Add request created");

            apiService.addBike("Bearer " + token, request)
                    .enqueue(new Callback<GenericResponse>() {
                        @Override
                        public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                            Log.d("BIKE_SAVE", "Add response code: " + response.code());
                            progressDialog.dismiss();

                            if (response.isSuccessful() && response.body() != null) {
                                GenericResponse apiResponse = response.body();
                                Log.d("BIKE_SAVE", "Add status: " + apiResponse.getStatus());

                                if ("success".equals(apiResponse.getStatus())) {
                                    Toast.makeText(AddBikeActivity.this,
                                            "Bike saved successfully",
                                            Toast.LENGTH_SHORT).show();
                                    clearForm();
                                    setResult(RESULT_OK);
                                    finish();
                                } else {
                                    Toast.makeText(AddBikeActivity.this,
                                            "Save failed: " + apiResponse.getMessage(),
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
                            Log.e("BIKE_SAVE", "Add failure: " + t.getMessage());
                            Toast.makeText(AddBikeActivity.this,
                                    "Network error: " + t.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void clearForm() {
        etBrand.setText("");
        etModel.setText("");
        etVariant.setText("");
        etEngineCC.setText("");
        etMileage.setText("");
        etFuelTank.setText("");
        etKerbWeight.setText("");
        etSeatHeight.setText("");
        etGroundClearance.setText("");
        etExShowroom.setText("");
        etInsurance.setText("");
        etRegistration.setText("");
        etLTRT.setText("");
        etTotalOnRoad.setText("");
        etFreeServices.setText("");
        etRegistrationProof.setText("");
        etPriceDisclaimer.setText("");

        spinnerYear.setSelection(0);
        spinnerFuelType.setSelection(0);
        spinnerTransmission.setSelection(0);
        spinnerBrakingType.setSelection(0);
        spinnerWarranty.setSelection(0);

        imageUris.clear();
        existingImagePaths.clear();
        if (imagePreviewContainer != null) {
            imagePreviewContainer.removeAllViews();
            imagePreviewContainer.setVisibility(View.GONE);
        }
        tvSelectedCount.setVisibility(View.GONE);

        for (String item : mandatoryItems) {
            mandatoryFittings.put(item, 0.0);
        }
        for (String item : additionalItems) {
            additionalFittings.put(item, 0.0);
        }
        customFittings.clear();

        ivUploadIcon.setImageResource(R.drawable.ic_upload);
        ivUploadIcon.setColorFilter(getResources().getColor(R.color.gray_500));
    }

    private static class CustomFitting {
        String name;
        double price;
    }
}