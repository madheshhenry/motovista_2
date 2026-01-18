package com.example.motovista_deep;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.motovista_deep.api.ApiService;
import com.example.motovista_deep.api.RetrofitClient;
import com.example.motovista_deep.models.ForgotPasswordRequest;
import com.example.motovista_deep.models.GenericResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText etEmail;
    private Button btnSendOtp;
    private ImageView btnBack;
    private ProgressBar loadingProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        etEmail = findViewById(R.id.et_email);
        btnSendOtp = findViewById(R.id.btn_send_otp);
        btnBack = findViewById(R.id.btn_back);
        loadingProgress = findViewById(R.id.loading_progress);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnSendOtp.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            if (email.isEmpty()) {
                etEmail.setError("Email is required");
                return;
            }
            sendOtp(email);
        });
    }

    private void sendOtp(String email) {
        setLoading(true);
        ApiService apiService = RetrofitClient.getApiService();
        apiService.customerForgotPassword(new ForgotPasswordRequest(email)).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                setLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        Toast.makeText(ForgotPasswordActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(ForgotPasswordActivity.this, VerifyOtpActivity.class);
                        intent.putExtra("email", email);
                        startActivity(intent);
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Failed to send OTP", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                setLoading(false);
                Toast.makeText(ForgotPasswordActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setLoading(boolean isLoading) {
        loadingProgress.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnSendOtp.setEnabled(!isLoading);
        etEmail.setEnabled(!isLoading);
    }
}
