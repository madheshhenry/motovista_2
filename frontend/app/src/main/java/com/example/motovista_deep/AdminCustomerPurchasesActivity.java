package com.example.motovista_deep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.motovista_deep.adapter.MyBikesAdapter;
import com.example.motovista_deep.api.ApiService;
import com.example.motovista_deep.api.RetrofitClient;
import com.example.motovista_deep.models.MyBikeModel;
import com.example.motovista_deep.models.MyBikesResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminCustomerPurchasesActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView tvTitle;
    private RecyclerView rvMyBikes;
    private MyBikesAdapter adapter;
    private List<MyBikeModel> bikeList = new ArrayList<>();
    private LinearLayout emptyStateLayout;
    private ProgressBar progressBar;
    private ApiService apiService;
    
    private int customerId;
    private String customerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_customer_purchases);

        // Get data from Intent
        customerId = getIntent().getIntExtra("CUSTOMER_ID", 0);
        customerName = getIntent().getStringExtra("CUSTOMER_NAME");

        apiService = RetrofitClient.getApiService();

        initializeViews();
        setupRecyclerView();
        setupClickListeners();
        
        // Dynamically set title to customer's name
        if (customerName != null && !customerName.isEmpty()) {
            tvTitle.setText(customerName);
        }

        loadBikesData();
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        tvTitle = findViewById(R.id.tvTitle);
        rvMyBikes = findViewById(R.id.rvMyBikes);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupRecyclerView() {
        rvMyBikes.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyBikesAdapter(this, bikeList);
        rvMyBikes.setAdapter(adapter);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadBikesData() {
        if (customerId == 0) {
            Toast.makeText(this, "Invalid customer data", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        apiService.getMyBikes(customerId).enqueue(new Callback<MyBikesResponse>() {
            @Override
            public void onResponse(Call<MyBikesResponse> call, Response<MyBikesResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    MyBikesResponse bikesResponse = response.body();
                    if (bikesResponse.isSuccess()) {
                        bikeList.clear();
                        if (bikesResponse.getData() != null && !bikesResponse.getData().isEmpty()) {
                            bikeList.addAll(bikesResponse.getData());
                            emptyStateLayout.setVisibility(View.GONE);
                            rvMyBikes.setVisibility(View.VISIBLE);
                        } else {
                            emptyStateLayout.setVisibility(View.VISIBLE);
                            rvMyBikes.setVisibility(View.GONE);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(AdminCustomerPurchasesActivity.this, bikesResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<MyBikesResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AdminCustomerPurchasesActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
