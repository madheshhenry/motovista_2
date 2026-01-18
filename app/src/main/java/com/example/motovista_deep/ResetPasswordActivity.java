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
import com.example.motovista_deep.models.GenericResponse;
import com.example.motovista_deep.models.ResetPasswordRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText etNewPassword, etConfirmPassword;
    private Button btnReset;
    private ImageView btnBack;
    private ProgressBar loadingProgress;
    private String email, otp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        email = getIntent().getStringExtra("email");
        otp = getIntent().getStringExtra("otp");

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        etNewPassword = findViewById(R.id.et_new_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnReset = findViewById(R.id.btn_reset_password);
        btnBack = findViewById(R.id.btn_back);
        loadingProgress = findViewById(R.id.loading_progress);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnReset.setOnClickListener(v -> {
            String newPass = etNewPassword.getText().toString().trim();
            String confirmPass = etConfirmPassword.getText().toString().trim();

            if (newPass.isEmpty()) {
                etNewPassword.setError("Required");
                return;
            }
            if (!newPass.equals(confirmPass)) {
                etConfirmPassword.setError("Passwords do not match");
                return;
            }

            resetPassword(newPass);
        });
    }

    private void resetPassword(String newPassword) {
        setLoading(true);
        ApiService apiService = RetrofitClient.getApiService();
        apiService.customerResetPassword(new ResetPasswordRequest(email, otp, newPassword)).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                setLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        Toast.makeText(ResetPasswordActivity.this, "Password reset successful! Please login.", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(ResetPasswordActivity.this, CustomerLoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(ResetPasswordActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ResetPasswordActivity.this, "Failed to reset password", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                setLoading(false);
                Toast.makeText(ResetPasswordActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setLoading(boolean isLoading) {
        loadingProgress.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnReset.setEnabled(!isLoading);
    }
}
