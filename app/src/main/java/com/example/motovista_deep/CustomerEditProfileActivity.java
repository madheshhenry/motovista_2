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
    private EditText etFullName, etEmail, etPhone;
    private CardView btnSaveChanges, btnCancel;

    private Uri selectedImageUri = null;
    private static final int PICK_IMAGE_REQUEST = 200;

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
        btnSaveChanges = findViewById(R.id.btnSaveChanges);
        btnCancel = findViewById(R.id.btnCancel);
    }

    private void loadCurrentData() {
        com.example.motovista_deep.models.User user = SharedPrefManager.getInstance(this).getUser();
        if (user != null) {
            etFullName.setText(user.getFull_name());
            etEmail.setText(user.getEmail());
            etPhone.setText(user.getPhone());
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

        // Update Profile on Server
        btnSaveChanges.setOnClickListener(v -> saveProfileChanges());
    }

    private void saveProfileChanges() {
        String name = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String token = SharedPrefManager.getInstance(this).getToken();

        // Convert Strings to RequestBody for Multipart
        RequestBody namePart = RequestBody.create(name, MultipartBody.FORM);
        RequestBody emailPart = RequestBody.create(email, MultipartBody.FORM);
        RequestBody phonePart = RequestBody.create(phone, MultipartBody.FORM);

        MultipartBody.Part imagePart = null;
        if (selectedImageUri != null) {
            String path = FileUtil.getPath(this, selectedImageUri);
            if (path != null) {
                File file = new File(path);
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
                imagePart = MultipartBody.Part.createFormData("profile_image", file.getName(), requestFile);
            }
        }

        btnSaveChanges.setEnabled(false);
        ApiService api = RetrofitClient.getApiService();

        // Pass NULL for address fields (not editing them here)
        api.updateProfile(
                "Bearer " + token,
                imagePart,
                null, // aadhar_front
                null, // aadhar_back
                namePart,
                emailPart,
                phonePart,
                null, // dob
                null, // house_no
                null, // street
                null, // city
                null, // state
                null, // pincode
                null  // pan_no
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            ivProfilePicture.setImageURI(selectedImageUri); // Show preview
        }
    }
}