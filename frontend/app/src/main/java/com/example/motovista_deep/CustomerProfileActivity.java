package com.example.motovista_deep;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.motovista_deep.api.ApiService;
import com.example.motovista_deep.api.RetrofitClient;
import com.example.motovista_deep.helpers.FileUtil;
import com.example.motovista_deep.helpers.SharedPrefManager;
import com.example.motovista_deep.models.GenericResponse;
import com.example.motovista_deep.models.ProfileUpdateRequest;
import com.example.motovista_deep.models.ProfileUpdateResponse;
import com.example.motovista_deep.models.User;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerProfileActivity extends AppCompatActivity {

    private ImageView btnBack, btnAddPhoto;
    private LinearLayout profileImageContainer, btnAadharFront, btnAadharBack;
    private TextView btnAddPhotoText, btnSkip;
    private EditText etDOB, etHouseNo, etStreet, etCity, etState, etPincode, etPAN;
    private Button btnCompleteSetup;

    // 🔥 PREVIEW IMAGE VIEWS (created dynamically)
    private ImageView profilePreview;
    private ImageView aadharFrontPreview;
    private ImageView aadharBackPreview;

    private Uri profileImageUri, aadharFrontUri, aadharBackUri;
    private final Calendar calendar = Calendar.getInstance();

    private static final int PICK_PROFILE_IMAGE = 100;
    private static final int PICK_AADHAR_FRONT = 101;
    private static final int PICK_AADHAR_BACK = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_profile);

        initViews();
        initPreviewImageViews();   // ✅ VERY IMPORTANT
        setClickListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnAddPhoto = findViewById(R.id.btnAddPhoto);
        btnAddPhotoText = findViewById(R.id.btnAddPhotoText);
        profileImageContainer = findViewById(R.id.profileImageContainer);

        etDOB = findViewById(R.id.etDOB);
        etHouseNo = findViewById(R.id.etHouseNo);
        etStreet = findViewById(R.id.etStreet);
        etCity = findViewById(R.id.etCity);
        etState = findViewById(R.id.etState);
        etPincode = findViewById(R.id.etPincode);
        etPAN = findViewById(R.id.etPAN);

        btnAadharFront = findViewById(R.id.btnAadharFront);
        btnAadharBack = findViewById(R.id.btnAadharBack);

        btnCompleteSetup = findViewById(R.id.btnCompleteSetup);
        btnSkip = findViewById(R.id.btnSkip);
    }

    /**
     * 🔥 Create preview ImageViews programmatically
     * XML is untouched
     */
    private void initPreviewImageViews() {

        profilePreview = new ImageView(this);
        profilePreview.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        ));
        profilePreview.setScaleType(ImageView.ScaleType.CENTER_CROP);
        profileImageContainer.addView(profilePreview);

        aadharFrontPreview = new ImageView(this);
        aadharFrontPreview.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        ));
        aadharFrontPreview.setScaleType(ImageView.ScaleType.CENTER_CROP);
        btnAadharFront.addView(aadharFrontPreview);

        aadharBackPreview = new ImageView(this);
        aadharBackPreview.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        ));
        aadharBackPreview.setScaleType(ImageView.ScaleType.CENTER_CROP);
        btnAadharBack.addView(aadharBackPreview);
    }

    private void setClickListeners() {

        btnBack.setOnClickListener(v -> onBackPressed());

        View.OnClickListener pickProfile = v -> pickImage(PICK_PROFILE_IMAGE);
        btnAddPhoto.setOnClickListener(pickProfile);
        btnAddPhotoText.setOnClickListener(pickProfile);
        profileImageContainer.setOnClickListener(pickProfile);

        etDOB.setOnClickListener(v -> showDatePicker());

        btnAadharFront.setOnClickListener(v -> pickImage(PICK_AADHAR_FRONT));
        btnAadharBack.setOnClickListener(v -> pickImage(PICK_AADHAR_BACK));

        btnCompleteSetup.setOnClickListener(v -> {
            if (validateInputs()) uploadProfile();
        });

        btnSkip.setOnClickListener(v -> {
            Toast.makeText(this, "You can complete profile later", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, CustomerHomeActivity.class));
            finish();
        });
    }

    private void pickImage(int code) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, code);
    }

    private void showDatePicker() {
        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (DatePicker view, int y, int m, int d) -> {
                    calendar.set(y, m, d);
                    etDOB.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            .format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        dialog.show();
    }

    private boolean validateInputs() {

        if (TextUtils.isEmpty(etDOB.getText())) {
            toast("Select DOB");
            return false;
        }

        if (!TextUtils.isEmpty(etPAN.getText()) && etPAN.getText().length() != 10) {
            toast("PAN must be 10 characters");
            return false;
        }

        if (!TextUtils.isEmpty(etPincode.getText()) && etPincode.getText().length() != 6) {
            toast("Pincode must be 6 digits");
            return false;
        }

        return true;
    }

    private void uploadProfile() {
        String token = SharedPrefManager.getInstance(this).getToken();
        if (token == null) {
            toast("Login expired");
            return;
        }

        // 1. Show loading state
        btnCompleteSetup.setText("Saving...");
        btnCompleteSetup.setEnabled(false);

        // 2. Prepare Multipart Parts
        MultipartBody.Part profileBody = filePart("profile_image", profileImageUri);
        MultipartBody.Part frontBody = filePart("aadhar_front", aadharFrontUri);
        MultipartBody.Part backBody = filePart("aadhar_back", aadharBackUri);

        RequestBody dob = text(etDOB);
        RequestBody house_no = text(etHouseNo);
        RequestBody street = text(etStreet);
        RequestBody city = text(etCity);
        RequestBody state = text(etState);
        RequestBody pincode = text(etPincode);
        RequestBody pan = text(etPAN);

        ApiService api = RetrofitClient.getApiService();

        // 3. Call Multipart Endpoint
        Call<ProfileUpdateResponse> call = api.updateProfile(
                "Bearer " + token,
                profileBody,
                frontBody,
                backBody,
                null, // full_name (not updating here)
                null, // email
                null, // phone
                dob,
                house_no,
                street,
                city,
                state,
                pincode,
                pan
        );

        // 4. Execute
        call.enqueue(new Callback<ProfileUpdateResponse>() {
            @Override
            public void onResponse(Call<ProfileUpdateResponse> call, Response<ProfileUpdateResponse> response) {
                btnCompleteSetup.setText("Complete Setup");
                btnCompleteSetup.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    ProfileUpdateResponse updateResponse = response.body();

                    if (updateResponse.isSuccess()) {
                        // Update local user object
                        User currentUser = SharedPrefManager.getInstance(CustomerProfileActivity.this).getUser();
                        if (currentUser != null) {
                            currentUser.setIs_profile_completed(true);
                            currentUser.setDob(etDOB.getText().toString().trim());
                            // Update other fields if needed, but the server response 'user' object is best
                            // Actually, let's use the user from response if available
                            if (updateResponse.getUser() != null) {
                                SharedPrefManager.getInstance(CustomerProfileActivity.this)
                                        .saveCustomerLogin(updateResponse.getUser(), token);
                            } else {
                                // Fallback update local fields
                                currentUser.setCity(etCity.getText().toString().trim());
                                SharedPrefManager.getInstance(CustomerProfileActivity.this)
                                        .saveCustomerLogin(currentUser, token);
                            }
                        }

                        toast("Profile completed successfully!");
                        startActivity(new Intent(CustomerProfileActivity.this, CustomerHomeActivity.class));
                        finish();
                    } else {
                        toast(updateResponse.getMessage());
                    }
                } else {
                    toast("Update failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ProfileUpdateResponse> call, Throwable t) {
                btnCompleteSetup.setText("Complete Setup");
                btnCompleteSetup.setEnabled(true);
                toast("Network error: " + t.getMessage());
            }
        });
    }

    private MultipartBody.Part filePart(String key, Uri uri) {
        if (uri == null) return null;
        File file = new File(FileUtil.getPath(this, uri));
        RequestBody rb = RequestBody.create(file, MediaType.parse("image/*"));
        return MultipartBody.Part.createFormData(key, file.getName(), rb);
    }

    private RequestBody text(EditText et) {
        return RequestBody.create(et.getText().toString().trim(), MultipartBody.FORM);
    }

    private void toast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int code, int result, @Nullable Intent data) {
        super.onActivityResult(code, result, data);

        if (result == RESULT_OK && data != null) {
            Uri uri = data.getData();

            if (code == PICK_PROFILE_IMAGE) {
                profileImageUri = uri;
                profilePreview.setImageURI(uri);
            }
            else if (code == PICK_AADHAR_FRONT) {
                aadharFrontUri = uri;
                aadharFrontPreview.setImageURI(uri);
            }
            else if (code == PICK_AADHAR_BACK) {
                aadharBackUri = uri;
                aadharBackPreview.setImageURI(uri);
            }
        }
    }
}
