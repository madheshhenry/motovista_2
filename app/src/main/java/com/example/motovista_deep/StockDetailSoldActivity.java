package com.example.motovista_deep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.motovista_deep.models.BikeStock;

public class StockDetailSoldActivity extends AppCompatActivity {

    // UI Components
    private ImageView btnBack;
    private TextView tvTitle, tvBrand, tvModel, tvEngineNo, tvChassisNo, tvStockDate;
    private TextView tvCustomerName, tvDeliveryDate;

    // Data
    private BikeStock bikeStock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_detail_sold);

        // Get bike data from intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("BIKE_DATA")) {
            bikeStock = (BikeStock) intent.getSerializableExtra("BIKE_DATA");
        } else {
            // Use sample data if no intent data
            bikeStock = new BikeStock(2, "MT-15 V2", "E564738291",
                    "C102938475", "Oct 22, 2023", "Rajesh Kumar", "Yamaha");
        }

        // Initialize views
        initializeViews();

        // Populate data
        populateData();

        // Setup click listeners
        setupClickListeners();
    }

    private void initializeViews() {
        // Top header
        btnBack = findViewById(R.id.btnBack);
        tvTitle = findViewById(R.id.tvTitle);

        // Vehicle information
        tvBrand = findViewById(R.id.tvBrand);
        tvModel = findViewById(R.id.tvModel);
        tvEngineNo = findViewById(R.id.tvEngineNo);
        tvChassisNo = findViewById(R.id.tvChassisNo);
        tvStockDate = findViewById(R.id.tvStockDate);

        // Sale note
        tvCustomerName = findViewById(R.id.tvCustomerName);
        tvDeliveryDate = findViewById(R.id.tvDeliveryDate);
    }

    private void populateData() {
        if (bikeStock != null) {
            tvBrand.setText(bikeStock.getBrandName());
            tvModel.setText(bikeStock.getModel());
            tvEngineNo.setText(bikeStock.getEngineNo());
            tvChassisNo.setText(bikeStock.getChassisNo());
            tvStockDate.setText(bikeStock.getStockDate());

            // For sold bikes, customer is in the customer field
            tvCustomerName.setText(bikeStock.getCustomer());

            // Delivery date would come from backend, using stock date as example
            tvDeliveryDate.setText(bikeStock.getStockDate());
        }
    }

    private void setupClickListeners() {
        // Back button
        btnBack.setOnClickListener(v -> onBackPressed());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}