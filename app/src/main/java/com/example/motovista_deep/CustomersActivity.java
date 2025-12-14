package com.example.motovista_deep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.motovista_deep.adapter.CustomerAdapter;
import com.example.motovista_deep.api.RetrofitClient;
import com.example.motovista_deep.helpers.SharedPrefManager;
import com.example.motovista_deep.models.GetCustomersResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomersActivity extends AppCompatActivity {

    private ImageView btnBack;
    private EditText etSearch;
    private RecyclerView rvCustomers;
    private LinearLayout tabDashboard, tabInventory, tabBikes, tabCustomers, tabSettings;

    // Bottom navigation ImageViews and TextViews
    private ImageView ivDashboard, ivInventory, ivBikes, ivCustomers, ivSettings;
    private TextView tvDashboard, tvInventory, tvBikes, tvCustomers, tvSettings;

    // Adapter
    private CustomerAdapter customerAdapter;
    private List<GetCustomersResponse.CustomerItem> customerList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customers);

        initializeViews();
        setupClickListeners();
        setupRecyclerView();  // Setup RecyclerView FIRST
        setupSearchFunctionality();

        setActiveTab(tabCustomers);

        // Load customers from backend
        loadCustomers();
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        etSearch = findViewById(R.id.etSearch);
        rvCustomers = findViewById(R.id.rvCustomers);

        tabDashboard = findViewById(R.id.tabDashboard);
        tabInventory = findViewById(R.id.tabInventory);
        tabBikes = findViewById(R.id.tabBikes);
        tabCustomers = findViewById(R.id.tabCustomers);
        tabSettings = findViewById(R.id.tabSettings);

        ivDashboard = (ImageView) tabDashboard.getChildAt(0);
        ivInventory = (ImageView) tabInventory.getChildAt(0);
        ivBikes = (ImageView) tabBikes.getChildAt(0);
        ivCustomers = (ImageView) tabCustomers.getChildAt(0);
        ivSettings = (ImageView) tabSettings.getChildAt(0);

        tvDashboard = (TextView) tabDashboard.getChildAt(1);
        tvInventory = (TextView) tabInventory.getChildAt(1);
        tvBikes = (TextView) tabBikes.getChildAt(1);
        tvCustomers = (TextView) tabCustomers.getChildAt(1);
        tvSettings = (TextView) tabSettings.getChildAt(1);
    }

    private void setupRecyclerView() {
        rvCustomers.setLayoutManager(new LinearLayoutManager(this));
        customerAdapter = new CustomerAdapter(customerList);
        rvCustomers.setAdapter(customerAdapter);
    }

    private void loadCustomers() {
        String token = SharedPrefManager.getInstance(this).getToken();
        if (token == null) {
            Toast.makeText(this, "Admin not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        RetrofitClient.getApiService()
                .getCustomers("Bearer " + token)
                .enqueue(new Callback<GetCustomersResponse>() {
                    @Override
                    public void onResponse(Call<GetCustomersResponse> call,
                                           Response<GetCustomersResponse> response) {

                        if (!response.isSuccessful() || response.body() == null || !response.body().status) {
                            Toast.makeText(CustomersActivity.this,
                                    "Failed to load customers",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Update the list with ALL customers
                        customerList.clear();
                        customerList.addAll(response.body().data);

                        // Use adapter's updateList method
                        if (customerAdapter != null) {
                            customerAdapter.updateList(customerList);
                        }
                    }

                    @Override
                    public void onFailure(Call<GetCustomersResponse> call, Throwable t) {
                        Toast.makeText(CustomersActivity.this,
                                "Server error: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setupSearchFunctionality() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (customerAdapter != null) {
                    customerAdapter.filter(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setupClickListeners() {
        // Back button click
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tabDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomersActivity.this, AdminDashboardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });

        tabBikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CustomersActivity.this, "Bikes screen coming soon", Toast.LENGTH_SHORT).show();
            }
        });

        tabInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CustomersActivity.this, "Inventory screen coming soon", Toast.LENGTH_SHORT).show();
            }
        });

        tabSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CustomersActivity.this, "Settings screen coming soon", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setActiveTab(LinearLayout activeTab) {
        resetAllTabs();
        ivCustomers.setColorFilter(ContextCompat.getColor(this, R.color.primary_color));
        tvCustomers.setTextColor(ContextCompat.getColor(this, R.color.primary_color));
        tvCustomers.setTypeface(tvCustomers.getTypeface(), android.graphics.Typeface.BOLD);
    }

    private void resetAllTabs() {
        ivDashboard.setColorFilter(ContextCompat.getColor(this, R.color.gray_400));
        ivInventory.setColorFilter(ContextCompat.getColor(this, R.color.gray_400));
        ivBikes.setColorFilter(ContextCompat.getColor(this, R.color.gray_400));
        ivCustomers.setColorFilter(ContextCompat.getColor(this, R.color.gray_400));
        ivSettings.setColorFilter(ContextCompat.getColor(this, R.color.gray_400));

        tvDashboard.setTextColor(ContextCompat.getColor(this, R.color.gray_400));
        tvInventory.setTextColor(ContextCompat.getColor(this, R.color.gray_400));
        tvBikes.setTextColor(ContextCompat.getColor(this, R.color.gray_400));
        tvCustomers.setTextColor(ContextCompat.getColor(this, R.color.gray_400));
        tvSettings.setTextColor(ContextCompat.getColor(this, R.color.gray_400));

        tvDashboard.setTypeface(null, android.graphics.Typeface.NORMAL);
        tvInventory.setTypeface(null, android.graphics.Typeface.NORMAL);
        tvBikes.setTypeface(null, android.graphics.Typeface.NORMAL);
        tvCustomers.setTypeface(null, android.graphics.Typeface.NORMAL);
        tvSettings.setTypeface(null, android.graphics.Typeface.NORMAL);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}