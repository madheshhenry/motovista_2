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
import com.example.motovista_deep.models.AdminNotification;
import com.example.motovista_deep.models.AdminNotificationListResponse;
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

        fetchNotifications();
    }

    private void fetchNotifications() {
        ApiService apiService = RetrofitClient.getApiService();
        apiService.getAdminNotificationList().enqueue(new Callback<AdminNotificationListResponse>() {
            @Override
            public void onResponse(Call<AdminNotificationListResponse> call, Response<AdminNotificationListResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    setupAdapter(response.body().getData());
                } else {
                    Toast.makeText(NotificationsActivity.this, "No notifications found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AdminNotificationListResponse> call, Throwable t) {
                Toast.makeText(NotificationsActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupAdapter(List<AdminNotification> notifications) {
        adapter = new AdminNotificationsAdapter(notifications, this);
        rvNotifications.setAdapter(adapter);
    }

    @Override
    public void onNotificationClick(AdminNotification notification) {
        String screen = notification.getTargetScreen();
        String id = notification.getItemId();

        if (screen != null) {
            Intent intent = null;
            if ("EmiDetailsActivity".equals(screen)) {
                intent = new Intent(this, EmiDetailsActivity.class);
                intent.putExtra("LEDGER_ID", id != null ? Integer.parseInt(id) : -1);
                intent.putExtra("IS_CUSTOMER_VIEW", false);
            } else if ("OrderSummaryActivity".equals(screen)) {
                intent = new Intent(this, OrderSummaryActivity.class);
                if (id != null) intent.putExtra("request_id", Integer.parseInt(id));
            } else if ("CustomerDetailsActivity".equals(screen)) {
                intent = new Intent(this, CustomerDetailsActivity.class);
                if (id != null) intent.putExtra("customer_id", Integer.parseInt(id));
            }

            if (intent != null) {
                startActivity(intent);
                return;
            }
        }

        // Fallback or general notification
        Toast.makeText(this, notification.getTitle(), Toast.LENGTH_SHORT).show();
    }
}