package com.example.motovista_deep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.motovista_deep.models.BikeStock;

public class StockDetailUnsoldActivity extends AppCompatActivity {

    // UI Components
    private ImageView btnBack;
    private TextView tvTitle, tvBrand, tvModel, tvEngineNo, tvChassisNo, tvStockDate;
    private EditText etCustomerName, etDeliveryDate;
    private Button btnMarkAsSold;

    // Bottom Navigation
    private LinearLayout tabDashboard, tabInventory, tabBikes, tabCustomers, tabSettings;
    private ImageView ivDashboard, ivInventory, ivBikes, ivCustomers, ivSettings;
    private TextView tvDashboard, tvInventory, tvBikes, tvCustomers, tvSettings;

    // Data
    private BikeStock bikeStock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_detail_unsold);

        // Get bike data from intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("BIKE_DATA")) {
            bikeStock = (BikeStock) intent.getSerializableExtra("BIKE_DATA");
        } else {
            // Use sample data if no intent data
            bikeStock = new BikeStock(1, "Classic 350", "U3S5F009827",
                    "ME3U3S5F009827", "25 Oct, 2023", "Not Sold", "Royal Enfield");
        }

        // Initialize views
        initializeViews();

        // Populate data
        populateData();

        // Setup click listeners
        setupClickListeners();

        // Set initial active tab
        setActiveTab(tabInventory);
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
        etCustomerName = findViewById(R.id.etCustomerName);
        etDeliveryDate = findViewById(R.id.etDeliveryDate);
        btnMarkAsSold = findViewById(R.id.btnMarkAsSold);

        // Bottom navigation
        tabDashboard = findViewById(R.id.tabDashboard);
        tabInventory = findViewById(R.id.tabInventory);
        tabBikes = findViewById(R.id.tabBikes);
        tabCustomers = findViewById(R.id.tabCustomers);
        tabSettings = findViewById(R.id.tabSettings);

        // Get ImageViews and TextViews from bottom navigation
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

    private void populateData() {
        if (bikeStock != null) {
            tvBrand.setText(bikeStock.getBrandName());
            tvModel.setText(bikeStock.getModel());
            tvEngineNo.setText(bikeStock.getEngineNo());
            tvChassisNo.setText(bikeStock.getChassisNo());
            tvStockDate.setText(bikeStock.getStockDate());
        }
    }

    private void setupClickListeners() {
        // Back button
        btnBack.setOnClickListener(v -> onBackPressed());

        // Mark as Sold button
        btnMarkAsSold.setOnClickListener(v -> markAsSold());

        // Bottom navigation
        tabDashboard.setOnClickListener(v -> navigateToDashboard());
        tabInventory.setOnClickListener(v -> navigateToInventory());
        tabBikes.setOnClickListener(v -> navigateToBikes());
        tabCustomers.setOnClickListener(v -> navigateToCustomers());
        tabSettings.setOnClickListener(v -> navigateToSettings());
    }

    private void markAsSold() {
        String customerName = etCustomerName.getText().toString().trim();
        String deliveryDate = etDeliveryDate.getText().toString().trim();

        if (customerName.isEmpty()) {
            Toast.makeText(this, "Please enter customer name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (deliveryDate.isEmpty()) {
            Toast.makeText(this, "Please select delivery date", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: Implement API call to mark as sold
        Toast.makeText(this, "Bike marked as sold for " + customerName, Toast.LENGTH_SHORT).show();

        // Navigate back or show success message
        finish();
    }

    private void navigateToDashboard() {
        Intent intent = new Intent(this, AdminDashboardActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void navigateToInventory() {
        // Go back to previous screen (BrandStockActivity)
        onBackPressed();
    }

    private void navigateToBikes() {
        Intent intent = new Intent(this, BikeInventoryActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void navigateToCustomers() {
        Intent intent = new Intent(this, CustomersActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void navigateToSettings() {
        Toast.makeText(this, "Settings screen coming soon", Toast.LENGTH_SHORT).show();
        // TODO: Create SettingsActivity
    }

    private void setActiveTab(LinearLayout activeTab) {
        // Reset all tabs
        resetAllTabs();

        // Set active tab
        if (activeTab == tabDashboard) {
            ivDashboard.setColorFilter(ContextCompat.getColor(this, R.color.primary_color));
            tvDashboard.setTextColor(ContextCompat.getColor(this, R.color.primary_color));
            tvDashboard.setTypeface(tvDashboard.getTypeface(), android.graphics.Typeface.BOLD);
        } else if (activeTab == tabInventory) {
            ivInventory.setColorFilter(ContextCompat.getColor(this, R.color.primary_color));
            tvInventory.setTextColor(ContextCompat.getColor(this, R.color.primary_color));
            tvInventory.setTypeface(tvInventory.getTypeface(), android.graphics.Typeface.BOLD);
        } else if (activeTab == tabBikes) {
            ivBikes.setColorFilter(ContextCompat.getColor(this, R.color.primary_color));
            tvBikes.setTextColor(ContextCompat.getColor(this, R.color.primary_color));
            tvBikes.setTypeface(tvBikes.getTypeface(), android.graphics.Typeface.BOLD);
        } else if (activeTab == tabCustomers) {
            ivCustomers.setColorFilter(ContextCompat.getColor(this, R.color.primary_color));
            tvCustomers.setTextColor(ContextCompat.getColor(this, R.color.primary_color));
            tvCustomers.setTypeface(tvCustomers.getTypeface(), android.graphics.Typeface.BOLD);
        } else if (activeTab == tabSettings) {
            ivSettings.setColorFilter(ContextCompat.getColor(this, R.color.primary_color));
            tvSettings.setTextColor(ContextCompat.getColor(this, R.color.primary_color));
            tvSettings.setTypeface(tvSettings.getTypeface(), android.graphics.Typeface.BOLD);
        }
    }

    private void resetAllTabs() {
        ivDashboard.setColorFilter(ContextCompat.getColor(this, R.color.gray_400));
        tvDashboard.setTextColor(ContextCompat.getColor(this, R.color.gray_400));
        tvDashboard.setTypeface(null, android.graphics.Typeface.NORMAL);

        ivInventory.setColorFilter(ContextCompat.getColor(this, R.color.gray_400));
        tvInventory.setTextColor(ContextCompat.getColor(this, R.color.gray_400));
        tvInventory.setTypeface(null, android.graphics.Typeface.NORMAL);

        ivBikes.setColorFilter(ContextCompat.getColor(this, R.color.gray_400));
        tvBikes.setTextColor(ContextCompat.getColor(this, R.color.gray_400));
        tvBikes.setTypeface(null, android.graphics.Typeface.NORMAL);

        ivCustomers.setColorFilter(ContextCompat.getColor(this, R.color.gray_400));
        tvCustomers.setTextColor(ContextCompat.getColor(this, R.color.gray_400));
        tvCustomers.setTypeface(null, android.graphics.Typeface.NORMAL);

        ivSettings.setColorFilter(ContextCompat.getColor(this, R.color.gray_400));
        tvSettings.setTextColor(ContextCompat.getColor(this, R.color.gray_400));
        tvSettings.setTypeface(null, android.graphics.Typeface.NORMAL);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}