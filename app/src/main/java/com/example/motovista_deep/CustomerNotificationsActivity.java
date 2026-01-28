package com.example.motovista_deep;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.motovista_deep.adapter.CustomerNotificationAdapter;
import com.example.motovista_deep.models.CustomerNotification;
import java.util.ArrayList;
import java.util.List;

public class CustomerNotificationsActivity extends AppCompatActivity {

    private RecyclerView rvNotifications;
    private ImageView btnBack;
    private LinearLayout emptyStateLayout;
    private CustomerNotificationAdapter adapter;
    private List<CustomerNotification> notificationList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_notifications);

        initializeViews();
        setupRecyclerView();
        loadNotifications();

        btnBack.setOnClickListener(v -> finish());
    }

    private void initializeViews() {
        rvNotifications = findViewById(R.id.rvNotifications);
        btnBack = findViewById(R.id.btnBack);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
    }

    private void setupRecyclerView() {
        rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CustomerNotificationAdapter(this, notificationList);
        rvNotifications.setAdapter(adapter);
    }

    private void loadNotifications() {
        com.example.motovista_deep.models.User user = com.example.motovista_deep.helpers.SharedPrefManager.getInstance(this).getUser();
        if (user == null) return;

        com.example.motovista_deep.api.ApiService apiService = com.example.motovista_deep.api.RetrofitClient.getApiService();
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

    private void updateUI() {
        if (notificationList.isEmpty()) {
            emptyStateLayout.setVisibility(View.VISIBLE);
            rvNotifications.setVisibility(View.GONE);
        } else {
            emptyStateLayout.setVisibility(View.GONE);
            rvNotifications.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
        }
    }
}
