package com.example.motovista;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.motovista.api.ApiClient;
import com.example.motovista.api.ApiService;
import com.example.motovista.models.ApiResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Calendar;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.*;

public class ProfileSetupActivity extends AppCompatActivity {

    ImageView imgProfile, imgAadharFront, imgAadharBack;
    LinearLayout layoutFront, layoutBack;
    ImageButton btnAddPhoto;

    EditText etDob, etHouse, etStreet, etCity, etState, etPincode, etPan;
    Button btnComplete;

    Uri profileUri, frontUri, backUri;
    int PICK = 0;

    ApiService apiService;
    SharedPreferences prefs;

    ActivityResultLauncher<String> picker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);

        apiService = ApiClient.getClient().create(ApiService.class);
        prefs = getSharedPreferences("APP_PREFS", MODE_PRIVATE);

        init();
        initPicker();
        listeners();
    }

    private void init() {
        imgProfile = findViewById(R.id.imgProfile);
        imgAadharFront = findViewById(R.id.imgAadharFront);
        imgAadharBack = findViewById(R.id.imgAadharBack);

        btnAddPhoto = findViewById(R.id.btnAddPhoto);
        layoutFront = findViewById(R.id.layoutFront);
        layoutBack = findViewById(R.id.layoutBack);

        etDob = findViewById(R.id.etDob);
        etHouse = findViewById(R.id.etHouse);
        etStreet = findViewById(R.id.etStreet);
        etCity = findViewById(R.id.etCity);
        etState = findViewById(R.id.etState);
        etPincode = findViewById(R.id.etPincode);
        etPan = findViewById(R.id.etPan);

        btnComplete = findViewById(R.id.btnComplete);
    }

    private void initPicker() {
        picker = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                if (PICK == 1) { profileUri = uri; imgProfile.setImageURI(uri); }
                if (PICK == 2) { frontUri = uri; imgAadharFront.setImageURI(uri); }
                if (PICK == 3) { backUri = uri; imgAadharBack.setImageURI(uri); }
            }
        });
    }

    private void listeners() {

        btnAddPhoto.setOnClickListener(v -> {
            PICK = 1;
            picker.launch("image/*");
        });

        layoutFront.setOnClickListener(v -> {
            PICK = 2;
            picker.launch("image/*");
        });

        layoutBack.setOnClickListener(v -> {
            PICK = 3;
            picker.launch("image/*");
        });

        etDob.setOnClickListener(v -> showDate());

        btnComplete.setOnClickListener(v -> {
            if (validate()) upload();
        });
    }

    private void showDate() {
        Calendar c = Calendar.getInstance();
        DatePickerDialog dp = new DatePickerDialog(this,
                (v, y, m, d) -> etDob.setText(d + "/" + (m + 1) + "/" + y),
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH));
        dp.show();
    }

    private boolean validate() {
        if (profileUri == null) { toast("Select profile photo"); return false; }
        if (frontUri == null || backUri == null) { toast("Upload Aadhar images"); return false; }

        if (etDob.getText().toString().isEmpty() ||
                etHouse.getText().toString().isEmpty() ||
                etStreet.getText().toString().isEmpty() ||
                etCity.getText().toString().isEmpty() ||
                etState.getText().toString().isEmpty() ||
                etPincode.getText().toString().isEmpty() ||
                etPan.getText().toString().isEmpty()) {
            toast("Fill all fields");
            return false;
        }
        return true;
    }

    private void upload() {
        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Uploading...");
        pd.setCancelable(false);
        pd.show();

        int userId = prefs.getInt("user_id", 0); // make sure login saved this
        if (userId == 0) {
            pd.dismiss();
            toast("User not logged in");
            return;
        }

        try {
            MultipartBody.Part profile = createPart(profileUri, "profile_photo");
            MultipartBody.Part front = createPart(frontUri, "aadhar_front");
            MultipartBody.Part back = createPart(backUri, "aadhar_back");

            Call<ApiResponse> call = apiService.completeProfile(
                    createTextPart(String.valueOf(userId)),
                    createTextPart(etDob.getText().toString()),
                    createTextPart(etHouse.getText().toString()),
                    createTextPart(etStreet.getText().toString()),
                    createTextPart(etCity.getText().toString()),
                    createTextPart(etState.getText().toString()),
                    createTextPart(etPincode.getText().toString()),
                    createTextPart(etPan.getText().toString()),
                    profile, front, back
            );

            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> res) {
                    pd.dismiss();
                    if (res.isSuccessful() && res.body() != null) {
                        toast(res.body().message);
                    } else {
                        // show server raw error if available
                        try {
                            String err = res.errorBody() != null ? res.errorBody().string() : "Unknown server error";
                            toast("Server error: " + err);
                        } catch (Exception e) {
                            toast("Server error");
                        }
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    pd.dismiss();
                    toast("Network error: " + t.getMessage());
                }
            });

        } catch (Exception e) {
            pd.dismiss();
            toast("Internal error: " + e.getMessage());
        }
    }

    private RequestBody createTextPart(String value) {
        return RequestBody.create(MediaType.parse("text/plain"), value);
    }

    private MultipartBody.Part createPart(Uri uri, String name) throws Exception {
        InputStream is = getContentResolver().openInputStream(uri);
        File file = new File(getCacheDir(), "temp_" + System.currentTimeMillis() + ".jpg");
        FileOutputStream fos = new FileOutputStream(file);

        byte[] buffer = new byte[1024];
        int read;
        while ((read = is.read(buffer)) != -1) fos.write(buffer, 0, read);

        is.close();
        fos.close();

        RequestBody req = RequestBody.create(MediaType.parse("image/*"), file);
        return MultipartBody.Part.createFormData(name, file.getName(), req);
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
