package com.example.motovista_deep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class EmiLedgerActivity extends AppCompatActivity {

    // Views
    private ImageView btnBack, btnSearch;
    private TextView tvTitle;
    private android.widget.Button chipAll, chipRunning, chipCompleted;
    private androidx.recyclerview.widget.RecyclerView recyclerView;
    private android.widget.ProgressBar progressBar;

    // Data
    private com.example.motovista_deep.adapters.EmiLedgerAdapter adapter;
    private java.util.List<com.example.motovista_deep.models.EmiLedgerItem> emiList = new java.util.ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emi_ledger);

        // Initialize views
        initializeViews();

        // Setup RecyclerView
        setupRecyclerView();

        // Setup click listeners
        setupClickListeners();

        // Set initial active filter (All)
        setActiveFilter(chipAll);

        // Fetch Data
        fetchLedgerData();
    }

    private void initializeViews() {
        // Header views
        btnBack = findViewById(R.id.btnBack);
        btnSearch = findViewById(R.id.btnSearch);
        tvTitle = findViewById(R.id.tvTitle);

        // Filter chips
        chipAll = findViewById(R.id.chipAll);
        chipRunning = findViewById(R.id.chipRunning);
        chipCompleted = findViewById(R.id.chipCompleted);

        // RecyclerView & Progress
        recyclerView = findViewById(R.id.recyclerViewEmiLedger);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
        adapter = new com.example.motovista_deep.adapters.EmiLedgerAdapter(this, emiList, new com.example.motovista_deep.adapters.EmiLedgerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(com.example.motovista_deep.models.EmiLedgerItem item) {
                openCustomerDetails(item);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void fetchLedgerData() {
        progressBar.setVisibility(View.VISIBLE);
        
        com.example.motovista_deep.api.ApiService apiService = com.example.motovista_deep.api.RetrofitClient.getApiService();
        apiService.getEmiLedgers().enqueue(new retrofit2.Callback<com.example.motovista_deep.models.GetEmiLedgersResponse>() {
            @Override
            public void onResponse(retrofit2.Call<com.example.motovista_deep.models.GetEmiLedgersResponse> call, retrofit2.Response<com.example.motovista_deep.models.GetEmiLedgersResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    emiList = response.body().getData();
                    adapter.updateData(emiList);
                    
                    // Re-apply current filter if needed
                    // For now, default is All, so just updating is fine
                } else {
                    Toast.makeText(EmiLedgerActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.example.motovista_deep.models.GetEmiLedgersResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(EmiLedgerActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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

        // Search button click
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EmiLedgerActivity.this, "Search functionality coming soon", Toast.LENGTH_SHORT).show();
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

    private void openCustomerDetails(com.example.motovista_deep.models.EmiLedgerItem item) {
        // Navigate to EMI details screen
        Intent intent = new Intent(EmiLedgerActivity.this, EmiDetailsActivity.class);
        intent.putExtra("CUSTOMER_NAME", item.getCustomerName());
        intent.putExtra("STATUS", item.getStatus());
        intent.putExtra("LEDGER_ID", item.getId());
        // Pass more details if needed
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}