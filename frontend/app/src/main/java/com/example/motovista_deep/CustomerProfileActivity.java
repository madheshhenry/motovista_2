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
import android.widget.FrameLayout;
import androidx.cardview.widget.CardView;

import com.example.motovista_deep.api.ApiService;
import com.example.motovista_deep.api.RetrofitClient;
import com.example.motovista_deep.helpers.FileUtil;
import com.example.motovista_deep.helpers.SharedPrefManager;
import com.example.motovista_deep.models.GenericResponse;
import com.example.motovista_deep.models.ProfileUpdateRequest;
import com.example.motovista_deep.models.ProfileUpdateResponse;
import com.example.motovista_deep.models.User;
import com.example.motovista_deep.utils.SystemUIHelper;

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

    private ImageView btnBack;
    private CardView btnAddPhoto;
    private FrameLayout profileImageContainer;
    private LinearLayout btnAadharFront, btnAadharBack;
    private TextView btnAddPhotoText, btnSkip;
    private EditText etDOB, etHouseNo, etStreet, etCity, etState, etPincode;
    private Button btnCompleteSetup;

    // Preview views
    private ImageView profilePreview;
    private ImageView aadharFrontPreview;
    private ImageView panCardPreview;

    private Uri profileImageUri, aadharFrontUri, panCardUri;
    private final Calendar calendar = Calendar.getInstance();

    private static final int PICK_PROFILE_IMAGE = 100;
    private static final int PICK_AADHAR_FRONT = 101;
    private static final int PICK_PAN_CARD = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_profile);

        initViews();
        SystemUIHelper.setupEdgeToEdgeWithScroll(this, 
                findViewById(R.id.root_layout), 
                btnBack, 
                findViewById(R.id.main_scroll));

        initPreviewImageViews();
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

        btnAadharFront = findViewById(R.id.btnAadharFront);
        btnAadharBack = findViewById(R.id.btnAadharBack); // Used for PAN Card upload

        btnCompleteSetup = findViewById(R.id.btnCompleteSetup);
        btnSkip = findViewById(R.id.btnSkip);
    }

    private void initPreviewImageViews() {
        profilePreview = new ImageView(this);
        profilePreview.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
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

        panCardPreview = new ImageView(this);
        panCardPreview.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        ));
        panCardPreview.setScaleType(ImageView.ScaleType.CENTER_CROP);
        btnAadharBack.addView(panCardPreview);
    }

    private void setClickListeners() {
        btnBack.setOnClickListener(v -> onBackPressed());

        View.OnClickListener pickProfile = v -> pickImage(PICK_PROFILE_IMAGE);
        btnAddPhoto.setOnClickListener(pickProfile);
        btnAddPhotoText.setOnClickListener(pickProfile);
        profileImageContainer.setOnClickListener(pickProfile);

        etDOB.setOnClickListener(v -> showDatePicker());

        btnAadharFront.setOnClickListener(v -> pickImage(PICK_AADHAR_FRONT));
        btnAadharBack.setOnClickListener(v -> pickImage(PICK_PAN_CARD));

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
        if (profileImageUri == null) {
            toast("Please upload profile photo");
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

        btnCompleteSetup.setText("Saving...");
        btnCompleteSetup.setEnabled(false);

        MultipartBody.Part profileBody = filePart("profile_image", profileImageUri);
        MultipartBody.Part frontBody = filePart("aadhar_front", aadharFrontUri);
        MultipartBody.Part panBody = filePart("aadhar_back", panCardUri); // Sending PAN card in aadhar_back field

        RequestBody dob = text(etDOB);
        RequestBody house_no = text(etHouseNo);
        RequestBody street = text(etStreet);
        RequestBody city = text(etCity);
        RequestBody state = text(etState);
        RequestBody pincode = text(etPincode);

        ApiService api = RetrofitClient.getApiService();

        Call<ProfileUpdateResponse> call = api.updateProfile(
                "Bearer " + token,
                profileBody,
                frontBody,
                panBody,
                null, 
                null, 
                null, 
                dob,
                house_no,
                street,
                city,
                state,
                pincode,
                null // No PAN text number
        );

        call.enqueue(new Callback<ProfileUpdateResponse>() {
            @Override
            public void onResponse(Call<ProfileUpdateResponse> call, Response<ProfileUpdateResponse> response) {
                btnCompleteSetup.setText("Finish Setup");
                btnCompleteSetup.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    ProfileUpdateResponse updateResponse = response.body();
                    if (updateResponse.isSuccess()) {
                        User user = updateResponse.getUser();
                        if (user != null) {
                            SharedPrefManager.getInstance(CustomerProfileActivity.this).saveCustomerLogin(user, token);
                        }
                        toast("Profile completed successfully!");
                        startActivity(new Intent(CustomerProfileActivity.this, CustomerHomeActivity.class));
                        finish();
                    } else {
                        toast(updateResponse.getMessage());
                    }
                } else {
                    toast("Update failed");
                }
            }

            @Override
            public void onFailure(Call<ProfileUpdateResponse> call, Throwable t) {
                btnCompleteSetup.setText("Finish Setup");
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
            } else if (code == PICK_AADHAR_FRONT) {
                aadharFrontUri = uri;
                aadharFrontPreview.setImageURI(uri);
            } else if (code == PICK_PAN_CARD) {
                panCardUri = uri;
                panCardPreview.setImageURI(uri);
            }
        }
    }
}
