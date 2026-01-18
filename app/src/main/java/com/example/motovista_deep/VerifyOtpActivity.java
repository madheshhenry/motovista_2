package com.example.motovista_deep;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.motovista_deep.api.ApiService;
import com.example.motovista_deep.api.RetrofitClient;
import com.example.motovista_deep.models.ForgotPasswordRequest;
import com.example.motovista_deep.models.GenericResponse;
import com.example.motovista_deep.models.OtpRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyOtpActivity extends AppCompatActivity {

    private EditText et1, et2, et3, et4, et5, et6;
    private Button btnVerify;
    private TextView tvResend, tvInstruction;
    private ImageView btnBack;
    private ProgressBar loadingProgress;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);

        email = getIntent().getStringExtra("email");
        
        initializeViews();
        setupOtpInputs();
        setupClickListeners();

        if (email != null) {
            tvInstruction.setText("Enter the 6-digit code sent to\n" + email);
        }
    }

    private void initializeViews() {
        et1 = findViewById(R.id.et_otp1);
        et2 = findViewById(R.id.et_otp2);
        et3 = findViewById(R.id.et_otp3);
        et4 = findViewById(R.id.et_otp4);
        et5 = findViewById(R.id.et_otp5);
        et6 = findViewById(R.id.et_otp6);
        btnVerify = findViewById(R.id.btn_verify_otp);
        tvResend = findViewById(R.id.tv_resend_otp);
        tvInstruction = findViewById(R.id.tv_instruction);
        btnBack = findViewById(R.id.btn_back);
        loadingProgress = findViewById(R.id.loading_progress);
    }

    private void setupOtpInputs() {
        et1.addTextChangedListener(new OtpTextWatcher(et1, et2));
        et2.addTextChangedListener(new OtpTextWatcher(et2, et3));
        et3.addTextChangedListener(new OtpTextWatcher(et3, et4));
        et4.addTextChangedListener(new OtpTextWatcher(et4, et5));
        et5.addTextChangedListener(new OtpTextWatcher(et5, et6));
        et6.addTextChangedListener(new OtpTextWatcher(et6, null));
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
        
        btnVerify.setOnClickListener(v -> {
            String otp = getOtpString();
            if (otp.length() < 6) {
                Toast.makeText(this, "Please enter full 6-digit code", Toast.LENGTH_SHORT).show();
                return;
            }
            verifyOtp(otp);
        });

        tvResend.setOnClickListener(v -> {
            resendOtp();
        });
    }

    private String getOtpString() {
        return et1.getText().toString() + et2.getText().toString() + et3.getText().toString() +
               et4.getText().toString() + et5.getText().toString() + et6.getText().toString();
    }

    private void verifyOtp(String otp) {
        setLoading(true);
        ApiService apiService = RetrofitClient.getApiService();
        apiService.customerVerifyOtp(new OtpRequest(email, otp)).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                setLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        Intent intent = new Intent(VerifyOtpActivity.this, ResetPasswordActivity.class);
                        intent.putExtra("email", email);
                        intent.putExtra("otp", otp);
                        startActivity(intent);
                    } else {
                        Toast.makeText(VerifyOtpActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(VerifyOtpActivity.this, "Verification failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                setLoading(false);
                Toast.makeText(VerifyOtpActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resendOtp() {
        setLoading(true);
        ApiService apiService = RetrofitClient.getApiService();
        apiService.customerForgotPassword(new ForgotPasswordRequest(email)).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                setLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(VerifyOtpActivity.this, "New code sent to your email", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                setLoading(false);
            }
        });
    }

    private void setLoading(boolean isLoading) {
        loadingProgress.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnVerify.setEnabled(!isLoading);
    }

    private class OtpTextWatcher implements TextWatcher {
        private View currentView;
        private View nextView;

        public OtpTextWatcher(View currentView, View nextView) {
            this.currentView = currentView;
            this.nextView = nextView;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() == 1 && nextView != null) {
                nextView.requestFocus();
            }
        }
    }
}
