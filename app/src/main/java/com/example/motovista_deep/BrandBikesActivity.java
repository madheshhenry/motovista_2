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

public class BrandBikesActivity extends AppCompatActivity {

    private RecyclerView rvBikes;
    private TextView tvBrandTitle, tvEmpty;
    private ImageView btnBack;
    private InventoryBikeAdapter adapter;
    private List<InventoryBike> bikeList = new ArrayList<>();

@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brand_bikes);

        rvBikes = findViewById(R.id.rvBikes);
        tvBrandTitle = findViewById(R.id.tvBrandTitle);
        tvEmpty = findViewById(R.id.tvEmpty);
        btnBack = findViewById(R.id.btnBack);
        
        // Stats
        TextView tvTotal = findViewById(R.id.tvTotalCount);
        TextView tvSold = findViewById(R.id.tvSoldCount);
        TextView tvStock = findViewById(R.id.tvStockCount);
        
        // Search
        android.widget.EditText etSearch = findViewById(R.id.etSearch);

        rvBikes.setLayoutManager(new LinearLayoutManager(this));
        
        Intent intent = getIntent();
        String brandName = intent.getStringExtra("BRAND_NAME");

        if (brandName != null) {
            tvBrandTitle.setText(brandName + " Ledger");
            fetchBrandBikes(brandName, tvTotal, tvSold, tvStock);
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
    }

    private void fetchBrandBikes(String brandName, TextView tvTotal, TextView tvSold, TextView tvStock) {
        String token = com.example.motovista_deep.helpers.SharedPrefManager.getInstance(this).getToken();
        com.example.motovista_deep.api.ApiService apiService = com.example.motovista_deep.api.RetrofitClient.getApiService();

        apiService.getBikesByBrand("Bearer " + token, brandName).enqueue(new retrofit2.Callback<com.example.motovista_deep.models.BikeListResponse>() {
            @Override
            public void onResponse(retrofit2.Call<com.example.motovista_deep.models.BikeListResponse> call, retrofit2.Response<com.example.motovista_deep.models.BikeListResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    bikeList.clear();
                    bikeList.addAll(response.body().getData());
                    
                    // Initialize or Refresh Adapter
                    if (adapter == null) {
                         adapter = new InventoryBikeAdapter(BrandBikesActivity.this, bikeList);
                         rvBikes.setAdapter(adapter);
                    } else {
                         adapter.notifyDataSetChanged();
                    }
                    
                    tvEmpty.setVisibility(bikeList.isEmpty() ? View.VISIBLE : View.GONE);
                    
                    // Calculate Stats
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
                    
                } else {
                    android.widget.Toast.makeText(BrandBikesActivity.this, "Failed to load ledger", android.widget.Toast.LENGTH_SHORT).show();
                }
             }

             @Override
             public void onFailure(retrofit2.Call<com.example.motovista_deep.models.BikeListResponse> call, Throwable t) {
                 android.widget.Toast.makeText(BrandBikesActivity.this, "Error: " + t.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
             }
        });
    }

    private void filterBikes(String query) {
        if (adapter == null) return;
        
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
        
        // Update adapter list? Adapter takes reference. 
        // Better to update the list inside adapter or create new adapter?
        // Let's create new adapter for simplicity or add method to adapter.
        // Or if adapter uses the `bikeList` reference, we modify it? 
        // No, we should validly filter.
        // Let's just set new adapter for now or update list.
        adapter = new InventoryBikeAdapter(this, filtered);
        rvBikes.setAdapter(adapter);
    }
}
