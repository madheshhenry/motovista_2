package com.example.motovista_deep;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.motovista_deep.adapter.CustomerOrdersAdapter;
import com.example.motovista_deep.api.RetrofitClient;
import com.example.motovista_deep.helpers.SharedPrefManager;
import com.example.motovista_deep.models.CustomerRequest;
import com.example.motovista_deep.models.GetCustomerRequestsResponse;
import com.example.motovista_deep.models.User;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerOrdersActivity extends AppCompatActivity {

    private RecyclerView rvOrders;
    private LinearLayout layoutEmpty;
    private CustomerOrdersAdapter adapter;
    private List<CustomerRequest> orderList = new ArrayList<>();

    // Bottom Navigation
    private LinearLayout tabHome, tabBikes, tabEmiCalculator, tabOrders, tabProfile;
    private ImageView ivHome, ivBikes, ivEmiCalculator, ivOrders, ivProfile;
    private TextView tvHome, tvBikes, tvEmiCalculator, tvOrders, tvProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_orders); // Use our new layout

        initializeViews();
        setupClickListeners();
        
        // Highlight correct tab
        setActiveTab(tabOrders);

        // Fetch Data
        User user = SharedPrefManager.getInstance(this).getUser();
        if (user != null) {
            fetchOrders(user.getId());
        } else {
            Toast.makeText(this, "Please Login first", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initializeViews() {
        rvOrders = findViewById(R.id.rvOrders);
        layoutEmpty = findViewById(R.id.layoutEmpty);

        rvOrders.setLayoutManager(new LinearLayoutManager(this));

        // Bottom Navigation (Included Layout)
        tabHome = findViewById(R.id.tabHome);
        tabBikes = findViewById(R.id.tabBikes);
        tabEmiCalculator = findViewById(R.id.tabEmiCalculator);
        tabOrders = findViewById(R.id.tabOrders);
        tabProfile = findViewById(R.id.tabProfile);

        ivHome = findViewById(R.id.ivHome);
        ivBikes = findViewById(R.id.ivBikes);
        ivEmiCalculator = findViewById(R.id.ivEmiCalculator);
        ivOrders = findViewById(R.id.ivOrders);
        ivProfile = findViewById(R.id.ivProfile);

        tvHome = findViewById(R.id.tvHome);
        tvBikes = findViewById(R.id.tvBikes);
        tvEmiCalculator = findViewById(R.id.tvEmiCalculator);
        tvOrders = findViewById(R.id.tvOrders);
        tvProfile = findViewById(R.id.tvProfile);
    }

    private void setupClickListeners() {
        // Bottom Navigation Logic
        tabHome.setOnClickListener(v -> {
            startActivity(new Intent(this, CustomerHomeActivity.class));
            finish();
        });
        tabBikes.setOnClickListener(v -> {
            startActivity(new Intent(this, BikeCatalogActivity.class));
            finish();
        });
        tabEmiCalculator.setOnClickListener(v -> {
            startActivity(new Intent(this, CustomerEmiDetailsActivity.class));
            finish();
        });
        tabOrders.setOnClickListener(v -> {
            // Already here
        });
        tabProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, CustomerProfileScreenActivity.class));
            finish();
        });
    }

    private void fetchOrders(int userId) {
        // Show loading? (Optional)

        RetrofitClient.getApiService().getCustomerOrders(userId).enqueue(new Callback<GetCustomerRequestsResponse>() {
            @Override
            public void onResponse(Call<GetCustomerRequestsResponse> call, Response<GetCustomerRequestsResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<CustomerRequest> data = response.body().getData();
                    if (data != null && !data.isEmpty()) {
                        orderList.clear();
                        orderList.addAll(data);
                        
                        adapter = new CustomerOrdersAdapter(CustomerOrdersActivity.this, orderList, new CustomerOrdersAdapter.OnLongClickListener() {
                            @Override
                            public void onLongClick(CustomerRequest order) {
                                showDeleteConfirmationDialog(order);
                            }
                        });
                        rvOrders.setAdapter(adapter);
                        
                        rvOrders.setVisibility(View.VISIBLE);
                        layoutEmpty.setVisibility(View.GONE);
                    } else {
                        rvOrders.setVisibility(View.GONE);
                        layoutEmpty.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(CustomerOrdersActivity.this, "Failed to load orders", Toast.LENGTH_SHORT).show();
                    layoutEmpty.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<GetCustomerRequestsResponse> call, Throwable t) {
                Toast.makeText(CustomerOrdersActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                layoutEmpty.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setActiveTab(LinearLayout activeTab) {
        resetAllTabs();
        int activeColor = ContextCompat.getColor(this, R.color.primary_color);

        if (activeTab == tabHome) {
            ivHome.setColorFilter(activeColor);
            tvHome.setTextColor(activeColor);
        } else if (activeTab == tabBikes) {
            ivBikes.setColorFilter(activeColor);
            tvBikes.setTextColor(activeColor);
        } else if (activeTab == tabEmiCalculator) {
            ivEmiCalculator.setColorFilter(activeColor);
            tvEmiCalculator.setTextColor(activeColor);
        } else if (activeTab == tabOrders) {
            ivOrders.setImageResource(R.drawable.ic_receipt_long_filled);
            ivOrders.setColorFilter(activeColor);
            tvOrders.setTextColor(activeColor);
            tvOrders.setTypeface(tvOrders.getTypeface(), android.graphics.Typeface.BOLD);
        } else if (activeTab == tabProfile) {
            ivProfile.setColorFilter(activeColor);
            tvProfile.setTextColor(activeColor);
        }
    }

    private void resetAllTabs() {
        int inactiveColor = ContextCompat.getColor(this, R.color.gray_400);

        ivHome.setImageResource(R.drawable.ic_home);
        ivHome.setColorFilter(inactiveColor);
        tvHome.setTextColor(inactiveColor);
        tvHome.setTypeface(null, android.graphics.Typeface.NORMAL);

        ivBikes.setColorFilter(inactiveColor);
        tvBikes.setTextColor(inactiveColor);
        tvBikes.setTypeface(null, android.graphics.Typeface.NORMAL);

        ivEmiCalculator.setColorFilter(inactiveColor);
        tvEmiCalculator.setTextColor(inactiveColor);
        tvEmiCalculator.setTypeface(null, android.graphics.Typeface.NORMAL);

        ivOrders.setImageResource(R.drawable.ic_receipt_long);
        ivOrders.setColorFilter(inactiveColor);
        tvOrders.setTextColor(inactiveColor);
        tvOrders.setTypeface(null, android.graphics.Typeface.NORMAL);

        ivProfile.setColorFilter(inactiveColor);
        tvProfile.setTextColor(inactiveColor);
        tvProfile.setTypeface(null, android.graphics.Typeface.NORMAL);
    }
    private void showDeleteConfirmationDialog(CustomerRequest order) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete Order")
                .setMessage("Are you sure you want to delete this order? This will remove all associated Registration and EMI records.")
                .setPositiveButton("Delete", (dialog, which) -> deleteOrder(order))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteOrder(CustomerRequest order) {
        RetrofitClient.getApiService().deleteOrder(new com.example.motovista_deep.models.DeleteRequestRequest(order.getId())).enqueue(new Callback<com.example.motovista_deep.models.GenericResponse>() {
            @Override
            public void onResponse(Call<com.example.motovista_deep.models.GenericResponse> call, Response<com.example.motovista_deep.models.GenericResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(CustomerOrdersActivity.this, "Order Deleted", Toast.LENGTH_SHORT).show();
                    User user = SharedPrefManager.getInstance(CustomerOrdersActivity.this).getUser();
                    if (user != null) fetchOrders(user.getId());
                } else {
                    String msg = (response.body() != null) ? response.body().getMessage() : "Deletion failed";
                    Toast.makeText(CustomerOrdersActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<com.example.motovista_deep.models.GenericResponse> call, Throwable t) {
                Toast.makeText(CustomerOrdersActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
