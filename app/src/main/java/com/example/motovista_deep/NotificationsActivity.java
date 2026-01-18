package com.example.motovista_deep;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.motovista_deep.adapter.AdminNotificationsAdapter;
import com.example.motovista_deep.api.ApiService;
import com.example.motovista_deep.api.RetrofitClient;
import com.example.motovista_deep.models.AdminVerification;
import com.example.motovista_deep.models.AdminVerificationResponse;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsActivity extends AppCompatActivity implements AdminNotificationsAdapter.OnNotificationClickListener {

    private RecyclerView rvNotifications;
    private ImageView btnBack;
    private AdminNotificationsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        rvNotifications = findViewById(R.id.rvNotifications);
        btnBack = findViewById(R.id.btnBack);

        rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        btnBack.setOnClickListener(v -> onBackPressed());

        fetchVerifications();
    }

    private void fetchVerifications() {
        ApiService apiService = RetrofitClient.getApiService();
        apiService.getPendingVerifications().enqueue(new Callback<AdminVerificationResponse>() {
            @Override
            public void onResponse(Call<AdminVerificationResponse> call, Response<AdminVerificationResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    setupAdapter(response.body().getData());
                } else {
                    Toast.makeText(NotificationsActivity.this, "Failed to fetch notifications", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AdminVerificationResponse> call, Throwable t) {
                Toast.makeText(NotificationsActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupAdapter(List<AdminVerification> verifications) {
        adapter = new AdminNotificationsAdapter(verifications, this);
        rvNotifications.setAdapter(adapter);
    }

    @Override
    public void onVerificationClick(AdminVerification verification) {
        // Navigate to EmiDetailsActivity for approval
        Intent intent = new Intent(this, EmiDetailsActivity.class);
        intent.putExtra("LEDGER_ID", verification.getLedgerId());
        intent.putExtra("IS_CUSTOMER_VIEW", false);
        startActivity(intent);
    }
}