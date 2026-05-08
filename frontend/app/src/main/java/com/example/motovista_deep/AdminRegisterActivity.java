package com.example.motovista_deep;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.LinearLayout;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.motovista_deep.api.RetrofitClient;
import com.example.motovista_deep.models.GenericResponse;
import com.example.motovista_deep.models.OtpRequest;
import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminRegisterActivity extends AppCompatActivity {
    
    private EditText etUsername, etEmail, etPassword, etOtp;
    private LinearLayout layoutOtp;
    private Button btnRegister;
    private boolean isOtpSent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_register);

        etUsername = findViewById(R.id.et_username);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etOtp = findViewById(R.id.et_otp);
        layoutOtp = findViewById(R.id.layout_otp);
        btnRegister = findViewById(R.id.btn_register_admin);

        btnRegister.setOnClickListener(v -> {
            if (!isOtpSent) {
                sendOtp();
            } else {
                verifyOtp();
            }
        });
    }

    private void sendOtp() {
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if(username.isEmpty() || email.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        btnRegister.setEnabled(false);
        btnRegister.setText("Sending...");

        Map<String, String> body = new HashMap<>();
        body.put("username", username);
        body.put("email", email);
        body.put("password", password);

        RetrofitClient.getApiService().registerAdmin(body).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                btnRegister.setEnabled(true);
                if(response.isSuccessful() && response.body() != null && response.body().isSuccess()){
                    Toast.makeText(AdminRegisterActivity.this, "OTP sent to your email!", Toast.LENGTH_LONG).show();
                    isOtpSent = true;
                    layoutOtp.setVisibility(android.view.View.VISIBLE);
                    
                    // Hide original fields to lock them in
                    etUsername.setEnabled(false);
                    etEmail.setEnabled(false);
                    etPassword.setEnabled(false);
                    
                    btnRegister.setText("Verify & Submit Request");
                } else {
                    btnRegister.setText("SEND OTP");
                    Toast.makeText(AdminRegisterActivity.this, "Failed: " + (response.body() != null ? response.body().getMessage() : "Error"), Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                btnRegister.setEnabled(true);
                btnRegister.setText("SEND OTP");
                Toast.makeText(AdminRegisterActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void verifyOtp() {
        String email = etEmail.getText().toString().trim();
        String otp = etOtp.getText().toString().trim();

        if (otp.isEmpty() || otp.length() < 6) {
            Toast.makeText(this, "Enter valid 6-digit OTP", Toast.LENGTH_SHORT).show();
            return;
        }

        btnRegister.setEnabled(false);
        btnRegister.setText("Verifying...");

        RetrofitClient.getApiService().adminRegisterVerify(new OtpRequest(email, otp)).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                btnRegister.setEnabled(true);
                if(response.isSuccessful() && response.body() != null && response.body().isSuccess()){
                    Toast.makeText(AdminRegisterActivity.this, "Request sent to Master Admin!", Toast.LENGTH_LONG).show();
                    finish(); // Go back to login
                } else {
                    btnRegister.setText("Verify & Submit Request");
                    Toast.makeText(AdminRegisterActivity.this, "Verification failed: " + (response.body() != null ? response.body().getMessage() : "Error"), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                btnRegister.setEnabled(true);
                btnRegister.setText("Verify & Submit Request");
                Toast.makeText(AdminRegisterActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
