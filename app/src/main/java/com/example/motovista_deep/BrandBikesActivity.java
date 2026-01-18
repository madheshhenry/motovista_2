package com.example.motovista_deep;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.motovista_deep.adapter.InventoryBikeAdapter;
import com.example.motovista_deep.models.InventoryBike;

import java.util.ArrayList;
import java.util.List;

public class BrandBikesActivity extends AppCompatActivity implements InventoryBikeAdapter.OnBikeLongClickListener {

    private RecyclerView rvBikes;
    private TextView tvBrandTitle, tvEmpty;
    private ImageView btnBack;
    private InventoryBikeAdapter adapter;
    private List<InventoryBike> bikeList = new ArrayList<>();
    
    // Store necessary references globally
    private String mBrandName;
    private android.widget.EditText etSearch;
    private TextView tvTotal, tvSold, tvStock;
    
    private androidx.swiperefreshlayout.widget.SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brand_bikes);

        swipeRefresh = findViewById(R.id.swipeRefresh);
        rvBikes = findViewById(R.id.rvBikes);
        tvBrandTitle = findViewById(R.id.tvBrandTitle);
        tvEmpty = findViewById(R.id.tvEmpty);
        btnBack = findViewById(R.id.btnBack);
        
        // Stats
        tvTotal = findViewById(R.id.tvTotalCount);
        tvSold = findViewById(R.id.tvSoldCount);
        tvStock = findViewById(R.id.tvStockCount);
        
        // Search
        etSearch = findViewById(R.id.etSearch);

        rvBikes.setLayoutManager(new LinearLayoutManager(this));
        
        Intent intent = getIntent();
        mBrandName = intent.getStringExtra("BRAND_NAME");

        if (mBrandName != null) {
            tvBrandTitle.setText(mBrandName + " Ledger");
            // Fetch called in onResume for Auto Refresh, so removed here to avoid double fetch
        }

        btnBack.setOnClickListener(v -> finish());
        
        // Implement Search Logic
        etSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterBikes(s.toString());
            }
            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        // Swipe Refresh Logic
        swipeRefresh.setOnRefreshListener(() -> fetchBrandBikes());
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchBrandBikes(); // Auto Refresh when returning to screen
    }

    private void fetchBrandBikes() {
        if (mBrandName == null) {
            if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
            return;
        }
        
        String token = com.example.motovista_deep.helpers.SharedPrefManager.getInstance(this).getToken();
        com.example.motovista_deep.api.ApiService apiService = com.example.motovista_deep.api.RetrofitClient.getApiService();

        // Add Cache Buster
        apiService.getBikesByBrand("Bearer " + token, mBrandName, System.currentTimeMillis()).enqueue(new retrofit2.Callback<com.example.motovista_deep.models.BikeListResponse>() {
            @Override
            public void onResponse(retrofit2.Call<com.example.motovista_deep.models.BikeListResponse> call, retrofit2.Response<com.example.motovista_deep.models.BikeListResponse> response) {
                if (swipeRefresh != null) swipeRefresh.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    bikeList.clear();
                    bikeList.addAll(response.body().getData());
                    
                    // Check if search is active
                    if (etSearch.getText().length() > 0) {
                        filterBikes(etSearch.getText().toString());
                    } else {
                        adapter = new InventoryBikeAdapter(BrandBikesActivity.this, bikeList, BrandBikesActivity.this);
                        rvBikes.setAdapter(adapter);
                    }
                    
                    tvEmpty.setVisibility(bikeList.isEmpty() ? View.VISIBLE : View.GONE);
                    updateStats();
                } else {
                    android.widget.Toast.makeText(BrandBikesActivity.this, "Failed to load ledger", android.widget.Toast.LENGTH_SHORT).show();
                }
             }

             @Override
             public void onFailure(retrofit2.Call<com.example.motovista_deep.models.BikeListResponse> call, Throwable t) {
                 if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
                 android.widget.Toast.makeText(BrandBikesActivity.this, "Error: " + t.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
             }
        });
    }
    
    private void updateStats() {
        int total = bikeList.size();
        int sold = 0;
        int stock = 0;
        
        for (InventoryBike bike : bikeList) {
            String s = bike.getStatus();
            if ("Delivered".equalsIgnoreCase(s) || "Sold".equalsIgnoreCase(s)) {
                sold++;
            } else {
                stock++;
            }
        }
        
        tvTotal.setText(String.valueOf(total));
        tvSold.setText(String.valueOf(sold));
        tvStock.setText(String.valueOf(stock));
    }

    private void filterBikes(String query) {
        List<InventoryBike> filtered = new ArrayList<>();
        if (query.isEmpty()) {
            filtered.addAll(bikeList);
        } else {
            String q = query.toLowerCase();
            for (InventoryBike bike : bikeList) {
                if ((bike.getModel() != null && bike.getModel().toLowerCase().contains(q)) ||
                    (bike.getEngineNumber() != null && bike.getEngineNumber().toLowerCase().contains(q)) ||
                    (bike.getChassisNumber() != null && bike.getChassisNumber().toLowerCase().contains(q)) ||
                    (bike.getCustomerName() != null && bike.getCustomerName().toLowerCase().contains(q))) {
                    filtered.add(bike);
                }
            }
        }
        // Always create new adapter to ensure state is clear
        adapter = new InventoryBikeAdapter(this, filtered, this);
        rvBikes.setAdapter(adapter);
    }

    @Override
    public void onBikeLongClick(InventoryBike bike, int position) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Delete Bike")
            .setMessage("Are you sure you want to delete this bike? This action cannot be undone.")
            .setPositiveButton("Delete", (dialog, which) -> deleteBike(bike, position))
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void deleteBike(InventoryBike bike, int position) {
        android.app.ProgressDialog pd = new android.app.ProgressDialog(this);
        pd.setMessage("Deleting...");
        pd.show();

        String token = com.example.motovista_deep.helpers.SharedPrefManager.getInstance(this).getToken();
        com.example.motovista_deep.api.ApiService apiService = com.example.motovista_deep.api.RetrofitClient.getApiService();
        
        String source = bike.getSourceTable();
        // Fallback or debug
        if (source == null || source.isEmpty()) source = "bikes";

        com.example.motovista_deep.models.DeleteAnyBikeRequest request = new com.example.motovista_deep.models.DeleteAnyBikeRequest(bike.getId(), source);

        apiService.deleteAnyBike("Bearer " + token, request).enqueue(new retrofit2.Callback<com.example.motovista_deep.models.GenericResponse>() {
            @Override
            public void onResponse(retrofit2.Call<com.example.motovista_deep.models.GenericResponse> call, retrofit2.Response<com.example.motovista_deep.models.GenericResponse> response) {
                pd.dismiss();
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    // dialog to PROVE update
                    new androidx.appcompat.app.AlertDialog.Builder(BrandBikesActivity.this)
                        .setTitle("Success")
                        .setMessage("Item deleted. Removing row from screen now.")
                        .setPositiveButton("OK", null)
                        .show();
                    
                    // POSITION BASED REMOVAL - GUARANTEED
                    if (adapter != null) {
                        adapter.removeAt(position);
                    }
                    
                    // Also try to remove from master list to keep it clean
                    InventoryBike toRemove = null;
                    for (InventoryBike b : bikeList) {
                        if (b.getId() == bike.getId()) {
                            toRemove = b;
                            break;
                        }
                    }
                    if (toRemove != null) {
                        bikeList.remove(toRemove);
                    }
                    
                    // Update Stats
                    updateStats();
                    tvEmpty.setVisibility(bikeList.isEmpty() ? View.VISIBLE : View.GONE);
                    
                } else {
                     String msg = response.body() != null ? response.body().getMessage() : "Failed to delete";
                     android.widget.Toast.makeText(BrandBikesActivity.this, msg, android.widget.Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.example.motovista_deep.models.GenericResponse> call, Throwable t) {
                pd.dismiss();
                android.widget.Toast.makeText(BrandBikesActivity.this, "Error: " + t.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }
}
