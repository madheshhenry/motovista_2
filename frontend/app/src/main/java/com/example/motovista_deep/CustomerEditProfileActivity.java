package com.example.motovista_deep;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.motovista_deep.api.ApiService;
import com.example.motovista_deep.api.RetrofitClient;
import com.example.motovista_deep.helpers.FileUtil;
import com.example.motovista_deep.helpers.SharedPrefManager;
import com.example.motovista_deep.models.GenericResponse;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerEditProfileActivity extends AppCompatActivity {

    private ImageView btnBack, btnEditPhoto, ivProfilePicture;
    private ImageView ivAadharIcon, ivPanIcon;
    private android.widget.LinearLayout btnUpdateAadhar, btnUpdatePan;
    private EditText etFullName, etEmail, etPhone, etDOB, etHouseNo, etStreet, etCity, etState, etPincode;
    private CardView btnSaveChanges, btnCancel;

    private Uri selectedImageUri = null;
    private Uri aadharUri = null;
    private Uri panUri = null;
    private static final int PICK_IMAGE_REQUEST = 200;
    private static final int PICK_AADHAR_REQUEST = 201;
    private static final int PICK_PAN_REQUEST = 202;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_edit_profile);

        initializeViews();
        loadCurrentData();
        setupClickListeners();
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        btnEditPhoto = findViewById(R.id.btnEditPhoto);
        ivProfilePicture = findViewById(R.id.ivProfilePicture);
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etDOB = findViewById(R.id.etDOB);
        etHouseNo = findViewById(R.id.etHouseNo);
        etStreet = findViewById(R.id.etStreet);
        etCity = findViewById(R.id.etCity);
        etState = findViewById(R.id.etState);
        etPincode = findViewById(R.id.etPincode);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);
        btnCancel = findViewById(R.id.btnCancel);
        btnUpdateAadhar = findViewById(R.id.btnUpdateAadhar);
        btnUpdatePan = findViewById(R.id.btnUpdatePan);
        ivAadharIcon = findViewById(R.id.ivAadharIcon);
        ivPanIcon = findViewById(R.id.ivPanIcon);
    }

    private void loadCurrentData() {
        com.example.motovista_deep.models.User user = SharedPrefManager.getInstance(this).getUser();
        if (user != null) {
            etFullName.setText(user.getFull_name());
            etEmail.setText(user.getEmail());
            etPhone.setText(user.getPhone());
            etDOB.setText(user.getDob());
            etHouseNo.setText(user.getHouse_no());
            etStreet.setText(user.getStreet());
            etCity.setText(user.getCity());
            etState.setText(user.getState());
            etPincode.setText(user.getPincode());
        }
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnCancel.setOnClickListener(v -> finish());

        // Select Photo
        btnEditPhoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        etDOB.setOnClickListener(v -> showDatePicker());

        // Update Documents
        btnUpdateAadhar.setOnClickListener(v -> pickDocument(PICK_AADHAR_REQUEST));
        btnUpdatePan.setOnClickListener(v -> pickDocument(PICK_PAN_REQUEST));

        // Update Profile on Server
        btnSaveChanges.setOnClickListener(v -> saveProfileChanges());
    }

    private void pickDocument(int code) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        String[] mimeTypes = {"image/*", "application/pdf", "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(intent, code);
    }

    private void showDatePicker() {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        new android.app.DatePickerDialog(
                this,
                (view, y, m, d) -> {
                    String dob = String.format(java.util.Locale.getDefault(), "%02d/%02d/%d", d, m + 1, y);
                    etDOB.setText(dob);
                },
                calendar.get(java.util.Calendar.YEAR),
                calendar.get(java.util.Calendar.MONTH),
                calendar.get(java.util.Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void saveProfileChanges() {
        String name = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String dobVal = etDOB.getText().toString().trim();
        String houseVal = etHouseNo.getText().toString().trim();
        String streetVal = etStreet.getText().toString().trim();
        String cityVal = etCity.getText().toString().trim();
        String stateVal = etState.getText().toString().trim();
        String pinVal = etPincode.getText().toString().trim();
        
        String token = SharedPrefManager.getInstance(this).getToken();

        // Convert Strings to RequestBody for Multipart
        RequestBody namePart = RequestBody.create(name, MultipartBody.FORM);
        RequestBody emailPart = RequestBody.create(email, MultipartBody.FORM);
        RequestBody phonePart = RequestBody.create(phone, MultipartBody.FORM);
        RequestBody dobPart = RequestBody.create(dobVal, MultipartBody.FORM);
        RequestBody housePart = RequestBody.create(houseVal, MultipartBody.FORM);
        RequestBody streetPart = RequestBody.create(streetVal, MultipartBody.FORM);
        RequestBody cityPart = RequestBody.create(cityVal, MultipartBody.FORM);
        RequestBody statePart = RequestBody.create(stateVal, MultipartBody.FORM);
        RequestBody pinPart = RequestBody.create(pinVal, MultipartBody.FORM);

        MultipartBody.Part imagePart = getFilePart("profile_image", selectedImageUri);
        MultipartBody.Part aadharPart = getFilePart("aadhar_front", aadharUri);
        MultipartBody.Part panPart = getFilePart("aadhar_back", panUri);

        btnSaveChanges.setEnabled(false);
        ApiService api = RetrofitClient.getApiService();

        api.updateProfile(
                "Bearer " + token,
                imagePart,
                aadharPart, // aadhar_front
                panPart, // aadhar_back
                namePart,
                emailPart,
                phonePart,
                dobPart,
                housePart,
                streetPart,
                cityPart,
                statePart,
                pinPart,
                null  // No text-based PAN number change here
        ).enqueue(new Callback<com.example.motovista_deep.models.ProfileUpdateResponse>() {
            @Override
            public void onResponse(Call<com.example.motovista_deep.models.ProfileUpdateResponse> call, Response<com.example.motovista_deep.models.ProfileUpdateResponse> response) {
                btnSaveChanges.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        Toast.makeText(CustomerEditProfileActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                        
                        // Update SharedPrefs with new data if returned
                        if (response.body().getUser() != null) {
                            SharedPrefManager.getInstance(CustomerEditProfileActivity.this)
                                    .saveCustomerLogin(response.body().getUser(), token);
                        }
                        
                        setResult(RESULT_OK);
                        finish();
                    } else {
                         Toast.makeText(CustomerEditProfileActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CustomerEditProfileActivity.this, "Update failed: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<com.example.motovista_deep.models.ProfileUpdateResponse> call, Throwable t) {
                btnSaveChanges.setEnabled(true);
                Toast.makeText(CustomerEditProfileActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private MultipartBody.Part getFilePart(String key, Uri uri) {
        if (uri == null) return null;
        String path = FileUtil.getPath(this, uri);
        if (path == null) return null;
        File file = new File(path);
        String mimeType = getContentResolver().getType(uri);
        if (mimeType == null) mimeType = "application/octet-stream";
        RequestBody rb = RequestBody.create(file, MediaType.parse(mimeType));
        return MultipartBody.Part.createFormData(key, file.getName(), rb);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri == null) return;
            
            if (requestCode == PICK_IMAGE_REQUEST) {
                selectedImageUri = uri;
                ivProfilePicture.setImageURI(uri); // Show preview
            } else if (requestCode == PICK_AADHAR_REQUEST) {
                aadharUri = uri;
                ivAadharIcon.setImageResource(android.R.drawable.presence_online); // Generic green dot
                ivAadharIcon.setColorFilter(android.graphics.Color.GREEN);
            } else if (requestCode == PICK_PAN_REQUEST) {
                panUri = uri;
                ivPanIcon.setImageResource(android.R.drawable.presence_online); // Generic green dot
                ivPanIcon.setColorFilter(android.graphics.Color.GREEN);
            }
        }
    }
}