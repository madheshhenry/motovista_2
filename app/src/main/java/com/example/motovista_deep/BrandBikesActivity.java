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

        rvBikes.setLayoutManager(new LinearLayoutManager(this));
        
        // Get Data from Intent
        Intent intent = getIntent();
        String brandName = intent.getStringExtra("BRAND_NAME");
        List<InventoryBike> passedList = (List<InventoryBike>) intent.getSerializableExtra("BIKE_LIST");

        if (brandName != null) {
            tvBrandTitle.setText(brandName);
        }

        if (passedList != null && !passedList.isEmpty()) {
            bikeList.addAll(passedList);
            adapter = new InventoryBikeAdapter(this, bikeList);
            rvBikes.setAdapter(adapter);
            tvEmpty.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.VISIBLE);
        }

        btnBack.setOnClickListener(v -> finish());
    }
}
