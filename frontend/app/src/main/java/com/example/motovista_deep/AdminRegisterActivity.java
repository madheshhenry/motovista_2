package com.example.motovista_deep;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.motovista_deep.api.RetrofitClient;
import com.example.motovista_deep.models.GenericResponse;
import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminRegisterActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_register);

        findViewById(R.id.btn_register_admin).setOnClickListener(v -> register());
    }

    private void register() {
        String masterKey = ((EditText)findViewById(R.id.et_master_key)).getText().toString().trim();
        String username = ((EditText)findViewById(R.id.et_username)).getText().toString().trim();
        String email = ((EditText)findViewById(R.id.et_email)).getText().toString().trim();
        String password = ((EditText)findViewById(R.id.et_password)).getText().toString().trim();

        if(masterKey.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> body = new HashMap<>();
        body.put("master_key", masterKey);
        body.put("username", username);
        body.put("email", email);
        body.put("password", password);

        RetrofitClient.getApiService().registerAdmin(body).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                if(response.isSuccessful() && response.body() != null && response.body().isSuccess()){
                    Toast.makeText(AdminRegisterActivity.this, "OTP sent to email!", Toast.LENGTH_LONG).show();
                    // Navigate to OTP verification (optional for now, or direct to login)
                    finish();
                } else {
                    Toast.makeText(AdminRegisterActivity.this, "Registration failed: " + (response.body() != null ? response.body().getMessage() : "Error"), Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                Toast.makeText(AdminRegisterActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
