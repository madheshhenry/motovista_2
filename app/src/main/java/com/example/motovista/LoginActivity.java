package com.example.motovista;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.motovista.api.ApiClient;
import com.example.motovista.api.ApiService;
import com.example.motovista.models.ApiResponse;
import com.example.motovista.models.LoginRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText etLogin, etPassword;
    ImageButton btnTogglePassword;
    Button btnLogin;
    TextView tvSignUp;
    boolean passwordVisible = false;

    ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etLogin = findViewById(R.id.etEmailOrUsername);
        etPassword = findViewById(R.id.etPassword);
        btnTogglePassword = findViewById(R.id.btnTogglePassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignUp = findViewById(R.id.tvSignUp);

        apiService = ApiClient.getClient().create(ApiService.class);

        // Password Toggle Button
        btnTogglePassword.setOnClickListener(v -> {
            passwordVisible = !passwordVisible;
            if (passwordVisible) {
                etPassword.setTransformationMethod(null);
            } else {
                etPassword.setTransformationMethod(new PasswordTransformationMethod());
            }
            etPassword.setSelection(etPassword.getText().length());
        });

        // Go to Signup
        tvSignUp.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
        });

        btnLogin.setOnClickListener(v -> performLogin());
    }

    private void performLogin() {
        String login = etLogin.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();

        if (login.isEmpty()) {
            etLogin.setError("Enter email or username");
            return;
        }
        if (pass.isEmpty()) {
            etPassword.setError("Enter password");
            return;
        }

        btnLogin.setEnabled(false);

        Call<ApiResponse> call = apiService.login(new LoginRequest(login, pass));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                btnLogin.setEnabled(true);

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(LoginActivity.this, "Server error", Toast.LENGTH_LONG).show();
                    return;
                }

                ApiResponse api = response.body();

                if (api.success) {

                    // Save user in SharedPreferences
                    SharedPreferences sp = getSharedPreferences("app_prefs", MODE_PRIVATE);
                    sp.edit()
                            .putInt("user_id", api.user.id)
                            .putString("user_name", api.user.name)
                            .putString("user_email", api.user.email)
                            .apply();

                    Toast.makeText(LoginActivity.this, "Login success", Toast.LENGTH_SHORT).show();

                    // Go to Dashboard
                    startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                    finish();

                } else {
                    Toast.makeText(LoginActivity.this, api.message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                btnLogin.setEnabled(true);
                Toast.makeText(LoginActivity.this, "Request failed: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
