package com.example.motovista_deep;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.motovista_deep.api.ApiService;
import com.example.motovista_deep.api.RetrofitClient;
import com.example.motovista_deep.helpers.SharedPrefManager;
import com.example.motovista_deep.models.LoginRequest;
import com.example.motovista_deep.models.LoginResponse;
import com.example.motovista_deep.models.User;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerLoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private ImageView ivPasswordToggle;
    private Button btnLogin;
    private TextView tvForgotPassword, tvSignUp;
    private boolean isPasswordVisible = false;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        makeFullScreen();
        setContentView(R.layout.activity_customer_login);

        // FIXED AUTO LOGIN - We'll check profile completion in navigateToHome()
        String role = SharedPrefManager.getInstance(this).getRole();
        if (SharedPrefManager.getInstance(this).isLoggedIn() && "customer".equals(role)) {
            navigateToHome(); // This will now check profile completion
            return;
        }

        apiService = RetrofitClient.getApiService();

        initViews();
        setupClickListeners();
    }

    private void makeFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(params);
        }

        getWindow().setStatusBarColor(Color.TRANSPARENT);
    }

    private void initViews() {
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        ivPasswordToggle = findViewById(R.id.iv_password_toggle);
        btnLogin = findViewById(R.id.btn_login);
        tvForgotPassword = findViewById(R.id.tv_forgot_password);
        tvSignUp = findViewById(R.id.tv_sign_up);
    }

    private void setupClickListeners() {
        ivPasswordToggle.setOnClickListener(v -> togglePasswordVisibility());
        btnLogin.setOnClickListener(v -> attemptLogin());
        tvSignUp.setOnClickListener(v ->
                startActivity(new Intent(this, CustomerSignUpActivity.class))
        );
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

    private void attemptLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty()) {
            etEmail.setError("Please enter email");
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Please enter password");
            return;
        }

        btnLogin.setText("Logging in...");
        btnLogin.setEnabled(false);

        performLogin(email, password);
    }

    private void performLogin(String email, String password) {
        LoginRequest loginRequest = new LoginRequest(email, password);
        Call<LoginResponse> call = apiService.login(loginRequest);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                btnLogin.setText("Login");
                btnLogin.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();

                    // Check if login was successful (PHP backend uses boolean success)
                    if (loginResponse.isSuccess()) {

                        // Check if email verification is required
                        if (loginResponse.getData() != null &&
                                loginResponse.getData().isRequires_verification()) {
                            Toast.makeText(CustomerLoginActivity.this,
                                    "Please verify your email first. Check your inbox.",
                                    Toast.LENGTH_LONG).show();
                            return;
                        }

                        // Get customer and token
                        User customer = loginResponse.getData().getCustomer();
                        String token = loginResponse.getData().getToken();

                        // ✅ ADD DEBUG LOGS HERE
                        Log.d("LOGIN_DEBUG", "========== LOGIN DEBUG START ==========");
                        Log.d("LOGIN_DEBUG", "Customer object: " + customer);
                        Log.d("LOGIN_DEBUG", "Customer ID: " + customer.getId());
                        Log.d("LOGIN_DEBUG", "Customer Name: " + customer.getFull_name());
                        Log.d("LOGIN_DEBUG", "Profile Completed Flag: " + customer.isIs_profile_completed());

                        // Also log the raw JSON response
                        try {
                            Log.d("LOGIN_DEBUG", "Raw JSON Response: " + new Gson().toJson(loginResponse));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Log.d("LOGIN_DEBUG", "========== LOGIN DEBUG END ==========");

                        // Save to SharedPreferences
                        SharedPrefManager.getInstance(CustomerLoginActivity.this)
                                .saveCustomerLogin(customer, token);

                        Toast.makeText(CustomerLoginActivity.this,
                                "Login successful!", Toast.LENGTH_SHORT).show();

                        // ✅ CHECK THE FLOW LOGIC
                        if (!customer.isIs_profile_completed()) {
                            Log.d("LOGIN_DEBUG", "DECISION: Sending to Profile Setup");
                            Intent intent = new Intent(CustomerLoginActivity.this, CustomerProfileActivity.class);
                            startActivity(intent);
                        } else {
                            Log.d("LOGIN_DEBUG", "DECISION: Sending directly to Home");
                            Intent intent = new Intent(CustomerLoginActivity.this, CustomerHomeActivity.class);
                            startActivity(intent);
                        }
                        finish();

                    } else {
                        // Show error message from PHP backend
                        Toast.makeText(CustomerLoginActivity.this,
                                loginResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    // Handle server errors
                    String errorMsg = "Login failed";
                    if (response.code() == 401) {
                        errorMsg = "Invalid email or password";
                    } else if (response.code() == 500) {
                        errorMsg = "Server error. Please try again later.";
                    }
                    Toast.makeText(CustomerLoginActivity.this,
                            errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                btnLogin.setText("Login");
                btnLogin.setEnabled(true);
                Toast.makeText(CustomerLoginActivity.this,
                        "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToHome() {
        // Get current user from SharedPreferences
        User currentUser = SharedPrefManager.getInstance(this).getCustomer();

        if (currentUser != null) {
            // ✅ CHECK PROFILE COMPLETION FOR AUTO LOGIN TOO
            if (!currentUser.isIs_profile_completed()) {
                // Profile not completed -> go to ProfileSetupActivity
                Intent intent = new Intent(CustomerLoginActivity.this, CustomerProfileActivity.class);
                startActivity(intent);
            } else {
                // Profile completed -> go to Home
                Intent intent = new Intent(CustomerLoginActivity.this, CustomerHomeActivity.class);
                startActivity(intent);
            }
        } else {
            // Fallback if user data is not available
            Intent intent = new Intent(CustomerLoginActivity.this, CustomerHomeActivity.class);
            startActivity(intent);
        }
        finish();
    }
}