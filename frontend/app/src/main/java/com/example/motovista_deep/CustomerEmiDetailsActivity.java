package com.example.motovista_deep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CustomerEmiDetailsActivity extends AppCompatActivity {

    // Views
    private ImageView btnBack; // Removed Search for customer view
    private TextView tvTitle;
    private android.widget.Button chipAll, chipRunning, chipCompleted;
    private androidx.recyclerview.widget.RecyclerView recyclerView;
    private android.widget.ProgressBar progressBar;

    // Data
    private com.example.motovista_deep.adapters.EmiLedgerAdapter adapter;
    private java.util.List<com.example.motovista_deep.models.EmiLedgerItem> emiList = new java.util.ArrayList<>();

    private com.example.motovista_deep.helpers.SharedPrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emi_ledger); // Reusing the same layout

        prefManager = com.example.motovista_deep.helpers.SharedPrefManager.getInstance(this);

        // Initialize views
        initializeViews();

        // Setup RecyclerView
        setupRecyclerView();

        // Setup click listeners
        setupClickListeners();

        // Set initial active filter (All)
        setActiveFilter(chipAll);

        // Fetch Data
        fetchMyLedgerData();
    }

    private void initializeViews() {
        // Header views
        btnBack = findViewById(R.id.btnBack);
        // btnSearch = findViewById(R.id.btnSearch); // Assuming reusing layout, it might be there
        // Just hide it if possible or ignore it
        ImageView btnSearch = findViewById(R.id.btnSearch);
        if(btnSearch != null) btnSearch.setVisibility(View.GONE);

        tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText("My EMI Orders");

        // Filter chips
        chipAll = findViewById(R.id.chipAll);
        chipRunning = findViewById(R.id.chipRunning);
        chipCompleted = findViewById(R.id.chipCompleted);

        // RecyclerView & Progress
        recyclerView = findViewById(R.id.recyclerViewEmiLedger);
        progressBar = findViewById(R.id.progressBar);
        
        // Ensure progress bar is hidden initially
        if(progressBar != null) progressBar.setVisibility(View.GONE);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
        adapter = new com.example.motovista_deep.adapters.EmiLedgerAdapter(this, emiList, new com.example.motovista_deep.adapters.EmiLedgerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(com.example.motovista_deep.models.EmiLedgerItem item) {
                openEmiDetails(item);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void fetchMyLedgerData() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        
        String token = prefManager.getToken();
        if (token == null) {
             Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
             return;
        }

        com.example.motovista_deep.api.ApiService apiService = com.example.motovista_deep.api.RetrofitClient.getApiService();
        apiService.getMyEmiLedgers("Bearer " + token).enqueue(new retrofit2.Callback<com.example.motovista_deep.models.GetEmiLedgersResponse>() {
            @Override
            public void onResponse(retrofit2.Call<com.example.motovista_deep.models.GetEmiLedgersResponse> call, retrofit2.Response<com.example.motovista_deep.models.GetEmiLedgersResponse> response) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                
                if (response.isSuccessful() && response.body() != null) {
                     if(response.body().isSuccess()) {
                        emiList = response.body().getData();
                        if(emiList == null) emiList = new java.util.ArrayList<>();
                        
                        adapter.updateData(emiList);
                        
                        // Handle empty state if needed
                        if(emiList.isEmpty()) {
                            Toast.makeText(CustomerEmiDetailsActivity.this, "No EMI Orders found", Toast.LENGTH_SHORT).show();
                        }
                     } else {
                         Toast.makeText(CustomerEmiDetailsActivity.this, "Failed: " + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                     }
                } else {
                    Toast.makeText(CustomerEmiDetailsActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.example.motovista_deep.models.GetEmiLedgersResponse> call, Throwable t) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                Toast.makeText(CustomerEmiDetailsActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupClickListeners() {
        // Back button click
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Filter chip clicks
        chipAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveFilter(chipAll);
                adapter.filterList("All");
            }
        });

        chipRunning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveFilter(chipRunning);
                adapter.filterList("active"); // Backend status is 'active'
            }
        });

        chipCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveFilter(chipCompleted);
                adapter.filterList("completed");
            }
        });
    }

    private void setActiveFilter(android.widget.Button activeChip) {
        // Reset all chips
        chipAll.setBackgroundResource(R.drawable.outline_button);
        chipAll.setTextColor(ContextCompat.getColor(this, R.color.text_primary_light));

        chipRunning.setBackgroundResource(R.drawable.outline_button);
        chipRunning.setTextColor(ContextCompat.getColor(this, R.color.text_primary_light));

        chipCompleted.setBackgroundResource(R.drawable.outline_button);
        chipCompleted.setTextColor(ContextCompat.getColor(this, R.color.text_primary_light));

        // Set active chip
        activeChip.setBackgroundResource(R.drawable.primary_button);
        activeChip.setTextColor(ContextCompat.getColor(this, R.color.white));
    }

    private void openEmiDetails(com.example.motovista_deep.models.EmiLedgerItem item) {
        // Navigate to EMI details screen with CUSTOMER FLAG
        Intent intent = new Intent(CustomerEmiDetailsActivity.this, EmiDetailsActivity.class);
        intent.putExtra("CUSTOMER_NAME", item.getCustomerName());
        intent.putExtra("STATUS", item.getStatus());
        intent.putExtra("LEDGER_ID", item.getId());
        intent.putExtra("IS_CUSTOMER_VIEW", true); // KEY FLAG
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
