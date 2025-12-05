package com.example.motovista;

import androidx.appcompat.app.AppCompatActivity;
import com.example.motovista.api.ApiClient;
import com.example.motovista.api.ApiService;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminLoginActivity extends AppCompatActivity {

    EditText edtUsername, edtPassword;
    Button btnAdminLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnAdminLogin = findViewById(R.id.btnAdminLogin);

        btnAdminLogin.setOnClickListener(v -> {

            String u = edtUsername.getText().toString().trim();
            String p = edtPassword.getText().toString().trim();

            if (u.isEmpty() || p.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            Call<ResponseBody> call = ApiClient.getClient()
                    .create(ApiService.class)
                    .adminLogin(u, p);


            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        String result = response.body().string();
                        JSONObject json = new JSONObject(result);

                        if (json.getBoolean("status")) {

                            Toast.makeText(AdminLoginActivity.this,
                                    "Login Successful", Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(AdminLoginActivity.this,
                                    AdminDashboardActivity.class));

                            finish();

                        } else {
                            Toast.makeText(AdminLoginActivity.this,
                                    json.getString("message"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        Toast.makeText(AdminLoginActivity.this,
                                "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(AdminLoginActivity.this,
                            "Server Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
