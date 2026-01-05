package com.example.motovista_deep;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.motovista_deep.api.ApiService;
import com.example.motovista_deep.api.RetrofitClient;
import com.example.motovista_deep.helpers.SharedPrefManager;
import com.example.motovista_deep.models.LoginRequest;
import com.example.motovista_deep.models.LoginResponse;
import com.example.motovista_deep.models.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminLoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword, etOtp;
    private android.widget.LinearLayout layoutOtp;
    private ImageView ivPasswordToggle;
    private Button btnLogin;
    private boolean isPasswordVisible = false;
    private boolean isOtpSent = false;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeFullScreen();
        setContentView(R.layout.activity_admin_login);

        // Check if Admin is already logged in
        if (SharedPrefManager.getInstance(this).isLoggedIn() &&
                "admin".equals(SharedPrefManager.getInstance(this).getRole())) {
            startActivity(new Intent(this, AdminDashboardActivity.class));
            finish();
            return;
        }

        apiService = RetrofitClient.getApiService();
        initViews();
        setupClickListeners();
    }

    private void makeFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(params);
        }
        getWindow().setStatusBarColor(Color.TRANSPARENT);
    }

    private void initViews() {
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etOtp = findViewById(R.id.et_otp);
        layoutOtp = findViewById(R.id.layout_otp);
        ivPasswordToggle = findViewById(R.id.iv_password_toggle);
        btnLogin = findViewById(R.id.btn_login);
        
        // Initial State
        btnLogin.setText("Send OTP");
        layoutOtp.setVisibility(android.view.View.GONE);
    }

    private void setupClickListeners() {
        ivPasswordToggle.setOnClickListener(v -> togglePasswordVisibility());
        btnLogin.setOnClickListener(v -> handleLoginButton());
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            etPassword.setTransformationMethod(new PasswordTransformationMethod());
            ivPasswordToggle.setImageResource(R.drawable.ic_visibility_off);
        } else {
            etPassword.setTransformationMethod(null);
            ivPasswordToggle.setImageResource(R.drawable.ic_visibility);
        }
        isPasswordVisible = !isPasswordVisible;
        etPassword.setSelection(etPassword.getText().length());
    }

    private void handleLoginButton() {
        if (!isOtpSent) {
            sendOtp();
        } else {
            verifyOtpAndLogin();
        }
    }

    private void sendOtp() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter Email and Password", Toast.LENGTH_SHORT).show();
            return;
        }

        btnLogin.setText("Sending...");
        btnLogin.setEnabled(false);

        apiService.adminSendOtp(new LoginRequest(email, password)).enqueue(new Callback<com.example.motovista_deep.models.GenericResponse>() {
            @Override
            public void onResponse(Call<com.example.motovista_deep.models.GenericResponse> call, Response<com.example.motovista_deep.models.GenericResponse> response) {
                btnLogin.setEnabled(true);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(AdminLoginActivity.this, "OTP Sent to Email", Toast.LENGTH_SHORT).show();
                    
                    // Switch UI to OTP mode
                    isOtpSent = true;
                    layoutOtp.setVisibility(android.view.View.VISIBLE);
                    btnLogin.setText("Verify & Login");
                    
                    // Disable inputs
                    etEmail.setEnabled(false);
                    etPassword.setEnabled(false);
                } else {
                     btnLogin.setText("Send OTP");
                     String msg = (response.body() != null) ? response.body().getMessage() : "Failed to send OTP";
                     Toast.makeText(AdminLoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<com.example.motovista_deep.models.GenericResponse> call, Throwable t) {
                btnLogin.setEnabled(true);
                btnLogin.setText("Send OTP");
                Toast.makeText(AdminLoginActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void verifyOtpAndLogin() {
        String email = etEmail.getText().toString().trim();
        String otp = etOtp.getText().toString().trim();

        if (otp.isEmpty() || otp.length() < 6) {
            Toast.makeText(this, "Please enter valid OTP", Toast.LENGTH_SHORT).show();
            return;
        }

        btnLogin.setText("Verifying...");
        btnLogin.setEnabled(false);

        apiService.adminVerifyOtp(new com.example.motovista_deep.models.OtpRequest(email, otp)).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                btnLogin.setEnabled(true);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    // Login Success
                    SharedPrefManager.getInstance(AdminLoginActivity.this)
                            .saveAdminLogin(response.body().getData().getCustomer(), response.body().getData().getToken());

                    Toast.makeText(AdminLoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AdminLoginActivity.this, AdminDashboardActivity.class));
                    finish();
                } else {
                    btnLogin.setText("Verify & Login");
                    String msg = (response.body() != null) ? response.body().getMessage() : "Verification Failed";
                    Toast.makeText(AdminLoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                btnLogin.setEnabled(true);
                btnLogin.setText("Verify & Login");
                Toast.makeText(AdminLoginActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}