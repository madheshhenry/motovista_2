package com.example.motovista_deep;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.motovista_deep.adapter.CustomerNotificationAdapter;
import com.example.motovista_deep.models.CustomerNotification;
import java.util.ArrayList;
import java.util.List;

public class CustomerNotificationsActivity extends AppCompatActivity implements CustomerNotificationAdapter.OnNotificationDeleteListener {

    private RecyclerView rvNotifications;
    private ImageView btnBack;
    private TextView btnClearAll;
    private LinearLayout emptyStateLayout;
    private CustomerNotificationAdapter adapter;
    private List<CustomerNotification> notificationList = new ArrayList<>();
    private com.example.motovista_deep.api.ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_notifications);

        apiService = com.example.motovista_deep.api.RetrofitClient.getApiService();

        initializeViews();
        setupRecyclerView();
        loadNotifications();

        btnBack.setOnClickListener(v -> finish());
        
        btnClearAll.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Clear All")
                .setMessage("Are you sure you want to delete all notifications?")
                .setPositiveButton("Clear All", (dialog, which) -> clearAllNotifications())
                .setNegativeButton("Cancel", null)
                .show();
        });
    }

    private void initializeViews() {
        rvNotifications = findViewById(R.id.rvNotifications);
        btnBack = findViewById(R.id.btnBack);
        btnClearAll = findViewById(R.id.btnClearAll);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
    }

    private void setupRecyclerView() {
        rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CustomerNotificationAdapter(this, notificationList, this);
        rvNotifications.setAdapter(adapter);
    }

    private void loadNotifications() {
        com.example.motovista_deep.models.User user = com.example.motovista_deep.helpers.SharedPrefManager.getInstance(this).getUser();
        if (user == null) return;

        apiService.getCustomerNotifications(user.getId()).enqueue(new retrofit2.Callback<com.example.motovista_deep.models.GetCustomerNotificationsResponse>() {
            @Override
            public void onResponse(retrofit2.Call<com.example.motovista_deep.models.GetCustomerNotificationsResponse> call, retrofit2.Response<com.example.motovista_deep.models.GetCustomerNotificationsResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    notificationList.clear();
                    if (response.body().getData() != null) {
                        notificationList.addAll(response.body().getData());
                    }
                    updateUI();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.example.motovista_deep.models.GetCustomerNotificationsResponse> call, Throwable t) {
                updateUI();
            }
        });
    }

    @Override
    public void onDeleteClick(CustomerNotification notification, int position) {
        java.util.Map<String, Integer> body = new java.util.HashMap<>();
        body.put("id", Integer.parseInt(notification.getId()));

        apiService.deleteNotification(body).enqueue(new retrofit2.Callback<com.example.motovista_deep.models.GenericResponse>() {
            @Override
            public void onResponse(retrofit2.Call<com.example.motovista_deep.models.GenericResponse> call, retrofit2.Response<com.example.motovista_deep.models.GenericResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    notificationList.remove(position);
                    adapter.notifyItemRemoved(position);
                    updateUI();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.example.motovista_deep.models.GenericResponse> call, Throwable t) {
                android.widget.Toast.makeText(CustomerNotificationsActivity.this, "Delete failed: " + t.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearAllNotifications() {
        com.example.motovista_deep.models.User user = com.example.motovista_deep.helpers.SharedPrefManager.getInstance(this).getUser();
        if (user == null) return;

        java.util.Map<String, Integer> body = new java.util.HashMap<>();
        body.put("customer_id", user.getId());

        apiService.clearAllNotifications(body).enqueue(new retrofit2.Callback<com.example.motovista_deep.models.GenericResponse>() {
            @Override
            public void onResponse(retrofit2.Call<com.example.motovista_deep.models.GenericResponse> call, retrofit2.Response<com.example.motovista_deep.models.GenericResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    notificationList.clear();
                    updateUI();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.example.motovista_deep.models.GenericResponse> call, Throwable t) {
                android.widget.Toast.makeText(CustomerNotificationsActivity.this, "Clear failed: " + t.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI() {
        if (notificationList.isEmpty()) {
            emptyStateLayout.setVisibility(View.VISIBLE);
            rvNotifications.setVisibility(View.GONE);
            btnClearAll.setVisibility(View.GONE);
        } else {
            emptyStateLayout.setVisibility(View.GONE);
            rvNotifications.setVisibility(View.VISIBLE);
            btnClearAll.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
        }
    }
}
