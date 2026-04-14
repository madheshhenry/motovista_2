package com.example.motovista_deep;

import android.content.Intent;
import android.graphics.Typeface;
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
    private View dotHome, dotBikes, dotEmiCalculator, dotOrders, dotProfile;


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

        // Dots
        dotHome = findViewById(R.id.dotHome);
        dotBikes = findViewById(R.id.dotBikes);
        dotEmiCalculator = findViewById(R.id.dotEmiCalculator);
        dotOrders = findViewById(R.id.dotOrders);
        dotProfile = findViewById(R.id.dotProfile);

    }

    private void setupClickListeners() {
        tabHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, CustomerHomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        });

        tabBikes.setOnClickListener(v -> {
            startActivity(new Intent(this, BikeCatalogActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });

        tabEmiCalculator.setOnClickListener(v -> {
            startActivity(new Intent(this, EmiCalculatorActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });

        tabOrders.setOnClickListener(v -> {
            // Already here
            setActiveTab(tabOrders);
        });

        tabProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, CustomerProfileScreenActivity.class));
            overridePendingTransition(0, 0);
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
        Typeface boldTypeface = Typeface.create("sans-serif-bold", Typeface.NORMAL);

        if (activeTab == tabHome) {
            ivHome.setImageResource(R.drawable.ic_home_filled);
            ivHome.setColorFilter(activeColor);
            tvHome.setTextColor(activeColor);
            tvHome.setTypeface(boldTypeface);
        } else if (activeTab == tabBikes) {
            ivBikes.setImageResource(R.drawable.ic_two_wheeler);
            ivBikes.setColorFilter(activeColor);
            tvBikes.setTextColor(activeColor);
            tvBikes.setTypeface(boldTypeface);
        } else if (activeTab == tabEmiCalculator) {
            ivEmiCalculator.setImageResource(R.drawable.ic_calculate);
            ivEmiCalculator.setColorFilter(activeColor);
            tvEmiCalculator.setTextColor(activeColor);
            tvEmiCalculator.setTypeface(boldTypeface);
        } else if (activeTab == tabOrders) {
            ivOrders.setImageResource(R.drawable.ic_receipt_long_filled);
            ivOrders.setColorFilter(activeColor);
            tvOrders.setTextColor(activeColor);
            tvOrders.setTypeface(boldTypeface);
        } else if (activeTab == tabProfile) {
            ivProfile.setImageResource(R.drawable.ic_person_filled);
            ivProfile.setColorFilter(activeColor);
            tvProfile.setTextColor(activeColor);
            tvProfile.setTypeface(boldTypeface);
        }

        showActiveDot(activeTab);
    }

    private void showActiveDot(LinearLayout activeTab) {
        dotHome.setVisibility(activeTab == tabHome ? View.VISIBLE : View.INVISIBLE);
        dotBikes.setVisibility(activeTab == tabBikes ? View.VISIBLE : View.INVISIBLE);
        dotEmiCalculator.setVisibility(activeTab == tabEmiCalculator ? View.VISIBLE : View.INVISIBLE);
        dotOrders.setVisibility(activeTab == tabOrders ? View.VISIBLE : View.INVISIBLE);
        dotProfile.setVisibility(activeTab == tabProfile ? View.VISIBLE : View.INVISIBLE);

        View activeDot = null;
        if (activeTab == tabHome) activeDot = dotHome;
        else if (activeTab == tabBikes) activeDot = dotBikes;
        else if (activeTab == tabEmiCalculator) activeDot = dotEmiCalculator;
        else if (activeTab == tabOrders) activeDot = dotOrders;
        else if (activeTab == tabProfile) activeDot = dotProfile;

        if (activeDot != null) {
            activeDot.setScaleX(0);
            activeDot.setScaleY(0);
            activeDot.animate().scaleX(1).scaleY(1).setDuration(200).start();
        }
    }

    private void resetAllTabs() {
        int inactiveColor = ContextCompat.getColor(this, R.color.gray_400);
        Typeface mediumTypeface = Typeface.create("sans-serif-medium", Typeface.NORMAL);

        // Reset Home
        ivHome.setImageResource(R.drawable.ic_home_filled);
        ivHome.setColorFilter(inactiveColor);
        tvHome.setTextColor(inactiveColor);
        tvHome.setTypeface(mediumTypeface);

        // Reset Bikes
        ivBikes.setImageResource(R.drawable.ic_two_wheeler);
        ivBikes.setColorFilter(inactiveColor);
        tvBikes.setTextColor(inactiveColor);
        tvBikes.setTypeface(mediumTypeface);

        // Reset EMI Calculator
        ivEmiCalculator.setImageResource(R.drawable.ic_calculate);
        ivEmiCalculator.setColorFilter(inactiveColor);
        tvEmiCalculator.setTextColor(inactiveColor);
        tvEmiCalculator.setTypeface(mediumTypeface);

        // Reset Orders
        ivOrders.setImageResource(R.drawable.ic_receipt_long);
        ivOrders.setColorFilter(inactiveColor);
        tvOrders.setTextColor(inactiveColor);
        tvOrders.setTypeface(mediumTypeface);

        // Reset Profile
        ivProfile.setImageResource(R.drawable.ic_person);
        ivProfile.setColorFilter(inactiveColor);
        tvProfile.setTextColor(inactiveColor);
        tvProfile.setTypeface(mediumTypeface);
    }

    private void showDeleteConfirmationDialog(CustomerRequest order) {
        String status = order.getStatus();
        if (status != null && (status.equalsIgnoreCase("completed") || status.equalsIgnoreCase("delivered"))) {
            Toast.makeText(this, "Completed orders cannot be deleted", Toast.LENGTH_SHORT).show();
            return;
        }

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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, CustomerHomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }
}
