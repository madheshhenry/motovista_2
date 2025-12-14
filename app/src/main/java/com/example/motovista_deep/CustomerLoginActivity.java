package com.example.motovista_deep;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.motovista_deep.models.User;

import androidx.appcompat.app.AppCompatActivity;

import com.example.motovista_deep.api.ApiService;
import com.example.motovista_deep.api.RetrofitClient;
import com.example.motovista_deep.helpers.SharedPrefManager;
import com.example.motovista_deep.models.LoginRequest;
import com.example.motovista_deep.models.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class    CustomerLoginActivity extends AppCompatActivity {

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

        // FIXED AUTO LOGIN
        String role = SharedPrefManager.getInstance(this).getRole();
        if (SharedPrefManager.getInstance(this).isLoggedIn() && "customer".equals(role)) {
            navigateToHome();
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
            etEmail.setError("Please enter email or username");
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

                    if ("success".equals(loginResponse.getStatus())) {

                        User customer = loginResponse.getData().getCustomer();
                        String token = loginResponse.getData().getToken();

                        SharedPrefManager.getInstance(CustomerLoginActivity.this)
                                .saveCustomerLogin(customer, token);

                        Toast.makeText(CustomerLoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();

                        navigateToHome();

                    } else {
                        Toast.makeText(CustomerLoginActivity.this,
                                loginResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(CustomerLoginActivity.this,
                            "Login failed (server error)", Toast.LENGTH_SHORT).show();
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
        startActivity(new Intent(this, CustomerHomeActivity.class));
        finish();
    }
}
