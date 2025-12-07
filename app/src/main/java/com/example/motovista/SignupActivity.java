package com.example.motovista;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.gson.Gson;
import java.io.IOException;


import com.example.motovista.api.ApiClient;
import com.example.motovista.api.ApiService;
import com.example.motovista.models.ApiResponse;
import com.example.motovista.models.SignupRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {

    EditText etName, etEmail, etPhone, etPassword, etConfirm;
    CheckBox chkTerms;
    Button btnSignUp;
    TextView tvLogin;
    ApiService apiService;
    SharedPreferences prefs;   // ✅ ADDED

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        etConfirm = findViewById(R.id.etConfirmPassword);
        chkTerms = findViewById(R.id.chkTerms);
        btnSignUp = findViewById(R.id.btnSignUp);
        tvLogin = findViewById(R.id.tvLogin);

        apiService = ApiClient.getClient().create(ApiService.class);
        prefs = getSharedPreferences("APP_PREFS", MODE_PRIVATE);  // ✅ ADDED

        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            finish();
        });

        btnSignUp.setOnClickListener(v -> doSignUp());
    }

    private void doSignUp() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String pass = etPassword.getText().toString();
        String conf = etConfirm.getText().toString();

        if (name.isEmpty()) { etName.setError("Required"); return; }
        if (email.isEmpty()) { etEmail.setError("Required"); return; }
        if (phone.isEmpty()) { etPhone.setError("Required"); return; }
        if (pass.length() < 6) { etPassword.setError("At least 6 chars"); return; }
        if (!pass.equals(conf)) { etConfirm.setError("Passwords do not match"); return; }
        if (!chkTerms.isChecked()) { Toast.makeText(this, "Accept terms", Toast.LENGTH_SHORT).show(); return; }

        btnSignUp.setEnabled(false);

        SignupRequest req = new SignupRequest(name, email, phone, pass);

        apiService.signup(req).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                btnSignUp.setEnabled(true);

                // 1) Log status & raw response (helpful for debugging)
                Log.d("SIGNUP_NET", "HTTP code: " + response.code());
                try {
                    if (response.errorBody() != null) {
                        String err = response.errorBody().string();
                        if (!err.isEmpty()) {
                            Log.d("SIGNUP_NET", "errorBody: " + err);
                        }
                    }
                } catch (IOException e) {
                    Log.e("SIGNUP_NET", "error reading errorBody", e);
                }

                // 2) If response.body() is null -> log and show message
                if (response.body() == null) {
                    Log.d("SIGNUP_NET", "response.body() is null");
                    Toast.makeText(SignupActivity.this, "Server returned no body (check logcat SIGNUP_NET)", Toast.LENGTH_LONG).show();
                    return;
                }

                // 3) We have a parsed ApiResponse -> log it
                ApiResponse r = response.body();
                Log.d("SIGNUP_NET", "parsed ApiResponse: " + new Gson().toJson(r));
                Toast.makeText(SignupActivity.this, r.message != null ? r.message : "Signed up", Toast.LENGTH_LONG).show();

                // 4) If success -> save user id if exists, then navigate to ProfileSetup
                if (r.success) {

                    // Save user_id if server returned it
                    if (r.user != null) {
                        try {
                            prefs.edit().putInt("user_id", r.user.id).apply();
                            Log.d("SIGNUP_NET", "Saved user_id: " + r.user.id);
                        } catch (Exception ex) {
                            Log.e("SIGNUP_NET", "Failed to save user id", ex);
                        }
                    } else {
                        Log.d("SIGNUP_NET", "r.user is null — server didn't return user data");
                    }

                    // Navigate to profile setup
                    Intent intent = new Intent(SignupActivity.this, ProfileSetupActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Not success
                    Toast.makeText(SignupActivity.this, r.message != null ? r.message : "Signup failed", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                btnSignUp.setEnabled(true);
                Log.e("SIGNUP_NET", "network failure", t);
                Toast.makeText(SignupActivity.this, "Request failed: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


}
