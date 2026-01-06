package com.example.motovista_deep;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.motovista_deep.adapter.InventoryBrandAdapter;
import com.example.motovista_deep.api.ApiService;
import com.example.motovista_deep.api.RetrofitClient;
import com.example.motovista_deep.helpers.SharedPrefManager;
import com.example.motovista_deep.models.InventoryBrand;
import com.example.motovista_deep.models.InventoryResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InventoryActivity extends AppCompatActivity {

    private RecyclerView rvInventory;
    private ProgressBar progressBar;
    private ImageView btnBack;
    private InventoryBrandAdapter adapter;
    private List<InventoryBrand> brandList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        rvInventory = findViewById(R.id.rvInventory);
        progressBar = findViewById(R.id.progressBar);
        btnBack = findViewById(R.id.btnBack);

        // Use Grid Layout for cards (2 columns looks good)
        rvInventory.setLayoutManager(new GridLayoutManager(this, 2));
        
        adapter = new InventoryBrandAdapter(this, brandList);
        rvInventory.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());

        loadInventory();
    }

    private void loadInventory() {
        progressBar.setVisibility(View.VISIBLE);
        
        String token = SharedPrefManager.getInstance(this).getToken();
        ApiService apiService = RetrofitClient.getApiService();

        apiService.getInventory("Bearer " + token).enqueue(new Callback<InventoryResponse>() {
            @Override
            public void onResponse(Call<InventoryResponse> call, Response<InventoryResponse> response) {
                progressBar.setVisibility(View.GONE);
                
                if (response.isSuccessful() && response.body() != null) {
                    InventoryResponse inventoryResponse = response.body();
                    if (inventoryResponse.getData() != null) {
                        brandList.clear();
                        brandList.addAll(inventoryResponse.getData());
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(InventoryActivity.this, "No inventory data found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(InventoryActivity.this, "Failed to load inventory", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<InventoryResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(InventoryActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}