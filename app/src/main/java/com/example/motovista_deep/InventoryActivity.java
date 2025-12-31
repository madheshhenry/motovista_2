package com.example.motovista_deep;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.motovista_deep.adapter.BrandAdapter;
import com.example.motovista_deep.models.Brand;

import java.util.ArrayList;
import java.util.List;

public class InventoryActivity extends AppCompatActivity implements
        BrandAdapter.OnBrandClickListener,
        BrandAdapter.OnBrandLongClickListener {

    private LinearLayout tabDashboard, tabInventory, tabBikes, tabCustomers, tabSettings;
    private RecyclerView rvBrands;
    private BrandAdapter brandAdapter;
    private List<Brand> brandList = new ArrayList<>();

    // Bottom navigation ImageViews and TextViews
    private ImageView ivDashboard, ivInventory, ivBikes, ivCustomers, ivSettings;
    private TextView tvDashboard, tvInventory, tvBikes, tvCustomers, tvSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        // Initialize views
        initializeViews();

        // Setup brands list
        setupBrands();

        // Setup RecyclerView
        setupRecyclerView();

        // Setup click listeners
        setupClickListeners();

        // Set initial active tab (Inventory)
        setActiveTab(tabInventory);
    }

    private void initializeViews() {
        // Initialize bottom navigation tabs
        tabDashboard = findViewById(R.id.tabDashboard);
        tabInventory = findViewById(R.id.tabInventory);
        tabBikes = findViewById(R.id.tabBikes);
        tabCustomers = findViewById(R.id.tabCustomers);
        tabSettings = findViewById(R.id.tabSettings);

        // Initialize RecyclerView
        rvBrands = findViewById(R.id.rvBrands);

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

    private void setupBrands() {
        // Clear existing list
        brandList.clear();

        // Add brands with different colors and icons
        brandList.add(new Brand(1, "Hero", R.drawable.ic_two_wheeler,
                R.drawable.brand_icon_bg_primary, R.color.primary_color));
        brandList.add(new Brand(2, "Honda", R.drawable.ic_two_wheeler,
                R.drawable.brand_icon_bg_red, R.color.icon_red));
        brandList.add(new Brand(3, "TVS", R.drawable.ic_two_wheeler,
                R.drawable.brand_icon_bg_blue, R.color.icon_blue));
        brandList.add(new Brand(4, "Bajaj", R.drawable.ic_two_wheeler,
                R.drawable.brand_icon_bg_orange, R.color.icon_yellow));
        brandList.add(new Brand(5, "Yamaha", R.drawable.ic_two_wheeler,
                R.drawable.brand_icon_bg_purple, R.color.icon_purple));
        brandList.add(new Brand(6, "Royal Enfield", R.drawable.ic_two_wheeler,
                R.drawable.brand_icon_bg_yellow, android.R.color.holo_orange_dark));
        brandList.add(new Brand(7, "Suzuki", R.drawable.ic_two_wheeler,
                R.drawable.brand_icon_bg_indigo, R.color.icon_indigo));
        brandList.add(new Brand(8, "Kawasaki", R.drawable.ic_two_wheeler,
                R.drawable.brand_icon_bg_green, R.color.icon_green));
        brandList.add(new Brand(9, "Ather", R.drawable.ic_electric_moped,
                R.drawable.brand_icon_bg_teal, R.color.teal_700));
    }

    private void setupRecyclerView() {
        // Set GridLayoutManager with 2 columns
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        rvBrands.setLayoutManager(layoutManager);

        // Create and set adapter
        brandAdapter = new BrandAdapter(this, brandList);
        brandAdapter.setOnBrandClickListener(this);
        brandAdapter.setOnBrandLongClickListener(this);
        rvBrands.setAdapter(brandAdapter);
    }

    private void setupClickListeners() {
        // Bottom navigation tabs
        tabDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToDashboard();
            }
        });

        tabInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Already on inventory page
                setActiveTab(tabInventory);
                Toast.makeText(InventoryActivity.this, "Inventory selected", Toast.LENGTH_SHORT).show();
            }
        });

        tabBikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToBikes();
            }
        });

        tabCustomers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToCustomers();
            }
        });

        tabSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToSettings();
            }
        });
    }

    @Override
    public void onBrandClick(int position, Brand brand) {
        Toast.makeText(this, brand.getName() + " selected", Toast.LENGTH_SHORT).show();
        // TODO: Navigate to brand stock screen
        // Intent intent = new Intent(this, BrandStockActivity.class);
        // intent.putExtra("BRAND_NAME", brand.getName());
        // startActivity(intent);
    }

    @Override
    public void onBrandLongClick(int position, Brand brand, View view) {
        showDeleteDialog(position, brand);
    }

    private void showDeleteDialog(final int position, final Brand brand) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Brand");
        builder.setMessage("Are you sure you want to delete " + brand.getName() + "?");

        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteBrand(position, brand);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteBrand(int position, Brand brand) {
        // Remove from list
        brandList.remove(position);

        // Update adapter
        brandAdapter.notifyItemRemoved(position);
        brandAdapter.notifyItemRangeChanged(position, brandList.size());

        // Show confirmation
        Toast.makeText(this, brand.getName() + " deleted", Toast.LENGTH_SHORT).show();

        // TODO: Add database deletion here when backend is ready
        // deleteBrandFromDatabase(brand.getId());
    }

    private void navigateToDashboard() {
        Intent intent = new Intent(this, AdminDashboardActivity.class);
        startActivity(intent);
        finish(); // Close current activity
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
        // Navigate back to dashboard
        navigateToDashboard();
    }
}