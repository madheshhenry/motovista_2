package com.example.motovista_deep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.motovista_deep.models.BikeStock;

import java.util.ArrayList;
import java.util.List;

public class BrandStockActivity extends AppCompatActivity {

    private ImageView btnBack, ivDashboard, ivInventory, ivBikes, ivCustomers, ivSettings;
    private TextView tvBrandName, tvDashboard, tvInventory, tvBikes, tvCustomers, tvSettings;
    private EditText etSearch;
    private LinearLayout tabDashboard, tabInventory, tabBikes, tabCustomers, tabSettings;
    private LinearLayout tableRowsContainer;

    private List<BikeStock> bikeStockList = new ArrayList<>();
    private List<BikeStock> filteredList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brand_stock);

        // Get brand name from intent
        Intent intent = getIntent();
        String brandName = intent.getStringExtra("BRAND_NAME");
        if (brandName == null) {
            brandName = "Yamaha Stock";
        }

        // Initialize views
        initializeViews();

        // Set brand name
        tvBrandName.setText(brandName + " Stock");

        // Setup sample data
        setupSampleData();

        // Setup table rows
        populateTableRows();

        // Setup click listeners
        setupClickListeners();

        // Setup search functionality
        setupSearch();

        // Set initial active tab (Inventory)
        setActiveTab(tabInventory);
    }

    private void initializeViews() {
        // Initialize top header views
        btnBack = findViewById(R.id.btnBack);
        tvBrandName = findViewById(R.id.tvBrandName);
        etSearch = findViewById(R.id.etSearch);

        // Initialize bottom navigation tabs
        tabDashboard = findViewById(R.id.tabDashboard);
        tabInventory = findViewById(R.id.tabInventory);
        tabBikes = findViewById(R.id.tabBikes);
        tabCustomers = findViewById(R.id.tabCustomers);
        tabSettings = findViewById(R.id.tabSettings);

        // Initialize table container
        tableRowsContainer = findViewById(R.id.tableRowsContainer);

        // Initialize bottom navigation ImageViews and TextViews
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

    private void setupSampleData() {
        // Clear existing lists
        bikeStockList.clear();
        filteredList.clear();

        // Add sample data as per HTML
        bikeStockList.add(new BikeStock(1, "YZF R15 V4", "E987654321", "C123456789", "Oct 24, 2023", "Not Sold", "Yamaha"));
        bikeStockList.add(new BikeStock(2, "MT-15 V2", "E564738291", "C102938475", "Oct 22, 2023", "Rajesh Kumar", "Yamaha"));
        bikeStockList.add(new BikeStock(3, "FZS-FI V4", "E112233445", "C556677889", "Oct 20, 2023", "—", "Yamaha"));
        bikeStockList.add(new BikeStock(4, "Fascino 125", "E998877665", "C443322110", "Oct 18, 2023", "Not Sold", "Yamaha"));
        bikeStockList.add(new BikeStock(5, "RayZR 125", "E776655443", "C223344556", "Oct 15, 2023", "Anita Singh", "Yamaha"));
        bikeStockList.add(new BikeStock(6, "Aerox 155", "E334455667", "C998877665", "Oct 12, 2023", "Not Sold", "Yamaha"));
        bikeStockList.add(new BikeStock(7, "MT-15 V2", "E667788990", "C112233445", "Oct 10, 2023", "Santhosh Kumar", "Yamaha"));
        bikeStockList.add(new BikeStock(8, "YZF R15 M", "E223344112", "C556677009", "Oct 05, 2023", "Not Sold", "Yamaha"));

        // Initially, filtered list is same as bike stock list
        filteredList.addAll(bikeStockList);
    }

    private void populateTableRows() {
        // Clear existing rows
        tableRowsContainer.removeAllViews();

        // Inflate and add rows for each bike stock item
        for (int i = 0; i < filteredList.size(); i++) {
            BikeStock bike = filteredList.get(i);
            View rowView = LayoutInflater.from(this).inflate(R.layout.item_bike_stock_row, null);

            // Get views from row
            TextView tvModel = rowView.findViewById(R.id.tvModel);
            TextView tvEngineNo = rowView.findViewById(R.id.tvEngineNo);
            TextView tvChassisNo = rowView.findViewById(R.id.tvChassisNo);
            TextView tvStockDate = rowView.findViewById(R.id.tvStockDate);
            TextView tvCustomer = rowView.findViewById(R.id.tvCustomer);
            ImageView ivChevron = rowView.findViewById(R.id.ivChevron);

            // Set data
            tvModel.setText(bike.getModel());
            tvEngineNo.setText(bike.getEngineNo());
            tvChassisNo.setText(bike.getChassisNo());
            tvStockDate.setText(bike.getStockDate());

            // Set customer text with styling
            String customer = bike.getCustomer();
            tvCustomer.setText(customer);

            // Apply styling based on customer status
            if ("Not Sold".equals(customer) || "—".equals(customer)) {
                tvCustomer.setTextColor(ContextCompat.getColor(this, R.color.gray_400));
                tvCustomer.setTypeface(tvCustomer.getTypeface(), android.graphics.Typeface.ITALIC);
            } else {
                tvCustomer.setTextColor(ContextCompat.getColor(this, R.color.primary_color));
                tvCustomer.setTypeface(tvCustomer.getTypeface(), android.graphics.Typeface.NORMAL);
            }

            // Set click listener for row
            final int position = i;
            rowView.setOnClickListener(v -> onRowClick(position, bike));

            // Add divider for all rows except last
            if (i < filteredList.size() - 1) {
                View divider = new View(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 1);
                params.setMargins(16, 0, 16, 0);
                divider.setLayoutParams(params);
                divider.setBackgroundColor(ContextCompat.getColor(this, R.color.gray_200));

                // Add row and divider to container
                tableRowsContainer.addView(rowView);
                tableRowsContainer.addView(divider);
            } else {
                // Add last row without divider
                tableRowsContainer.addView(rowView);
            }
        }
    }

    private void setupClickListeners() {
        // Back button
        btnBack.setOnClickListener(v -> onBackPressed());

        // Bottom navigation tabs
        tabDashboard.setOnClickListener(v -> navigateToDashboard());
        tabInventory.setOnClickListener(v -> navigateToInventory());
        tabBikes.setOnClickListener(v -> navigateToBikes());
        tabCustomers.setOnClickListener(v -> navigateToCustomers());
        tabSettings.setOnClickListener(v -> navigateToSettings());
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterTable(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterTable(String query) {
        filteredList.clear();

        if (query.isEmpty()) {
            filteredList.addAll(bikeStockList);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (BikeStock bike : bikeStockList) {
                if (bike.getModel().toLowerCase().contains(lowerCaseQuery) ||
                        bike.getEngineNo().toLowerCase().contains(lowerCaseQuery) ||
                        bike.getChassisNo().toLowerCase().contains(lowerCaseQuery)) {
                    filteredList.add(bike);
                }
            }
        }

        populateTableRows();
    }

    private void onRowClick(int position, BikeStock bike) {
        Toast.makeText(this, "Selected: " + bike.getModel(), Toast.LENGTH_SHORT).show();
        // TODO: Navigate to bike details screen
        // Intent intent = new Intent(this, BikeDetailsActivity.class);
        // intent.putExtra("BIKE_ID", bike.getId());
        // startActivity(intent);
    }

    private void navigateToDashboard() {
        Intent intent = new Intent(this, AdminDashboardActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void navigateToInventory() {
        // Already on inventory screen, just go back to brand selection
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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
        // Reset all tabs to inactive state
        resetAllTabs();

        // Set the active tab
        if (activeTab == tabDashboard) {
            ivDashboard.setColorFilter(ContextCompat.getColor(this, R.color.primary_color));
            tvDashboard.setTextColor(ContextCompat.getColor(this, R.color.primary_color));
            tvDashboard.setTypeface(tvDashboard.getTypeface(), android.graphics.Typeface.BOLD);
        }
        else if (activeTab == tabInventory) {
            ivInventory.setColorFilter(ContextCompat.getColor(this, R.color.primary_color));
            tvInventory.setTextColor(ContextCompat.getColor(this, R.color.primary_color));
            tvInventory.setTypeface(tvInventory.getTypeface(), android.graphics.Typeface.BOLD);
        }
        else if (activeTab == tabBikes) {
            ivBikes.setColorFilter(ContextCompat.getColor(this, R.color.primary_color));
            tvBikes.setTextColor(ContextCompat.getColor(this, R.color.primary_color));
            tvBikes.setTypeface(tvBikes.getTypeface(), android.graphics.Typeface.BOLD);
        }
        else if (activeTab == tabCustomers) {
            ivCustomers.setColorFilter(ContextCompat.getColor(this, R.color.primary_color));
            tvCustomers.setTextColor(ContextCompat.getColor(this, R.color.primary_color));
            tvCustomers.setTypeface(tvCustomers.getTypeface(), android.graphics.Typeface.BOLD);
        }
        else if (activeTab == tabSettings) {
            ivSettings.setColorFilter(ContextCompat.getColor(this, R.color.primary_color));
            tvSettings.setTextColor(ContextCompat.getColor(this, R.color.primary_color));
            tvSettings.setTypeface(tvSettings.getTypeface(), android.graphics.Typeface.BOLD);
        }
    }

    private void resetAllTabs() {
        // Reset Dashboard tab
        ivDashboard.setColorFilter(ContextCompat.getColor(this, R.color.gray_400));
        tvDashboard.setTextColor(ContextCompat.getColor(this, R.color.gray_400));
        tvDashboard.setTypeface(null, android.graphics.Typeface.NORMAL);

        // Reset Inventory tab
        ivInventory.setColorFilter(ContextCompat.getColor(this, R.color.gray_400));
        tvInventory.setTextColor(ContextCompat.getColor(this, R.color.gray_400));
        tvInventory.setTypeface(null, android.graphics.Typeface.NORMAL);

        // Reset Bikes tab
        ivBikes.setColorFilter(ContextCompat.getColor(this, R.color.gray_400));
        tvBikes.setTextColor(ContextCompat.getColor(this, R.color.gray_400));
        tvBikes.setTypeface(null, android.graphics.Typeface.NORMAL);

        // Reset Customers tab
        ivCustomers.setColorFilter(ContextCompat.getColor(this, R.color.gray_400));
        tvCustomers.setTextColor(ContextCompat.getColor(this, R.color.gray_400));
        tvCustomers.setTypeface(null, android.graphics.Typeface.NORMAL);

        // Reset Settings tab
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