package com.example.motovista_deep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.motovista_deep.adapter.BikeAdapter;
import com.example.motovista_deep.models.BikeModel;

import java.util.ArrayList;
import java.util.List;

public class BikeInventoryActivity extends AppCompatActivity implements BikeAdapter.OnBikeClickListener {

    private ConstraintLayout rootLayout;
    private ImageButton btnBack;
    private EditText etSearch;
    private ImageView icClear;
    private RecyclerView rvBikes;
    private LinearLayout tabDashboard, tabInventory, tabBikes, tabCustomers, tabSettings;

    // Add these ImageViews for bottom navigation icons
    private ImageView ivDashboard, ivInventory, ivBikes, ivCustomers, ivSettings;
    private TextView tvDashboard, tvInventory, tvBikes, tvCustomers, tvSettings;

    private BikeAdapter bikeAdapter;
    private List<BikeModel> bikeList = new ArrayList<>();
    private List<BikeModel> filteredList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_inventory);

        // Initialize views
        initializeViews();

        // Setup bottom navigation
        setupBottomNavigation();

        // Setup search functionality
        setupSearch();

        // Load sample data (you'll replace this with API call)
        loadSampleData();

        // Setup RecyclerView
        setupRecyclerView();
    }

    private void initializeViews() {
        rootLayout = findViewById(R.id.rootLayout);
        btnBack = findViewById(R.id.btnBack);
        etSearch = findViewById(R.id.etSearch);
        icClear = findViewById(R.id.icClear);

        // Initialize RecyclerView - IMPORTANT: Make sure you have this ID in your XML
        rvBikes = findViewById(R.id.rvBikes);

        // Initialize bottom navigation tabs
        tabDashboard = findViewById(R.id.tabDashboard);
        tabInventory = findViewById(R.id.tabInventory);
        tabBikes = findViewById(R.id.tabBikes);
        tabCustomers = findViewById(R.id.tabCustomers);
        tabSettings = findViewById(R.id.tabSettings);

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

        // Set initial active tab
        setActiveTab(tabBikes);
    }

    private void setupBottomNavigation() {
        // Back button click
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Bottom navigation tabs
        tabDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BikeInventoryActivity.this, AdminDashboardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        tabInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(BikeInventoryActivity.this, "Inventory screen coming soon", Toast.LENGTH_SHORT).show();
            }
        });

        tabBikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Already on bikes screen
                setActiveTab(tabBikes);
            }
        });

        tabCustomers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(BikeInventoryActivity.this, "Customers screen coming soon", Toast.LENGTH_SHORT).show();
            }
        });

        tabSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(BikeInventoryActivity.this, "Settings screen coming soon", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSearch() {
        // Search text change listener
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Show/hide clear button
                icClear.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);

                // Filter list
                filterBikes(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Clear button click
        icClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etSearch.setText("");
            }
        });
    }

    private void loadSampleData() {
        // Clear any existing data
        bikeList.clear();

        // Add sample bikes (same as in your HTML)
        bikeList.add(new BikeModel(1, "Royal Enfield", "Classic 350", "2,10,000",
                "Excellent", "https://lh3.googleusercontent.com/aida-public/AB6AXuCL-fxH0U0ZsXcpL2B4Re94_JC4GXZVmn7nTucmNIusCpWa-eUfJmPxpZoMXMiDNMg4D7X4bqoKKF9EhiHOym_7_-G-INLRP7U1X21XELIKMVNFX_x7L1tfS88iObaDJ6CoArtzdG5pyM1CAPJTBpMpn106gODuryrbXIa0P5aumti6UI-AT49gyDD2xQnEDlMXT6MDcujkEhn0N_C2Kg6imTq9MpPr2RQMQbJKJZ96xfvfJW6VimvjKO7EwnScxS8x1lOt7G0CRtRO",
                "NEW", true));

        bikeList.add(new BikeModel(2, "Bajaj", "Pulsar NS200", "1,50,000",
                "Excellent", "https://lh3.googleusercontent.com/aida-public/AB6AXuA2oqixRnQHefgquCk_pKUZ2hEB31VM0IJFGiAvWZ5Xi0GO-nhTrZYGybDSqVGlVtX_DoVCxOizMaPrsGeGiS8l7aOwZR-6hrEQ_TBGppj2IWs1G_7LNe8w2p5T_idaCR-zrKyqYnW5ASwPHuAn1CKCH4iDBG4S9k_-A8iepnVik0R9Vm426dLnshRPNbYAaam2H31fa-yANEt7VDkhjHBAvNEfZEdhMD6j2nARY-DYazOqGSn4guBsHvdE4d867qJoHH8Rt41mf2Mw",
                "SECOND_HAND", true));

        bikeList.add(new BikeModel(3, "KTM", "Duke 250", "2,39,000",
                "Excellent", "https://lh3.googleusercontent.com/aida-public/AB6AXuBVkM0PXzzoEo-U4ChLILE4qsvtc3f1JzOsPMX_5aDccWw6UpSSPd9UBHVECkcOjr7bgV953kmy3VRCOpXp96kkoTDkwBgHj8L3yNlBg3DlS9l-8pGa6jwoqMO_KfCm0hMeTSBBayRO6Yb0cCvc56xiVOFwvLLgZFnPZQ_ovUku-yLw7xPiOwRUEGEakBHX757qudwF1aNqNFwCF3ygiXblTUUmm86dNsAcmbNTtOaviJ6YCfbcGjquqtNDfFp3X5gF6gsYMIndj08l",
                "NEW", true));

        bikeList.add(new BikeModel(4, "Yamaha", "YZF R15 V3", "1,35,000",
                "Good", "https://lh3.googleusercontent.com/aida-public/AB6AXuD7OLJtLteGJPYDgiL8rfTJ6LdTX95qpku40ISwXByT6U7I_A_FMHL-zYHCeuw6mUQ0k2xiERh6__er9o2Z0F4wsJmlcZsY9nAVkeo31ec6zPBzb34RLRnhF3m41QjgS68kACqvdBMnjFkHoMxBy8vspYcNauQcDBoQah2pVbXEMraz2NZvkIMd7f64T12mw-BR_cqITxcmwct7pfuMSdYbtiWmXDNsinB1iAONVey6p1q73_TUVWhTR134-wTQ4ul4YpCUlS0ylMbh",
                "SECOND_HAND", true));

        bikeList.add(new BikeModel(5, "TVS", "Apache RR 310", "2,72,000",
                "Excellent", "https://lh3.googleusercontent.com/aida-public/AB6AXuAODLWQzH_B03tEOyEwRRpH-EaXOb6aejmnLls7TLnKMdsK9bjA0D1YX0fJyXfjMVJzWzpG7vsuNWsq_6wWZ9Z1EszTIn5EHpaNUBBOiMNu--Fyl9IE8vxQj5amim8unlOHDcA3VQaMLER_4vvTW6DbTjfCK9jjyDoWu8TAH8hFCNPt5xyoVRYDci4cn-_jHjTJ7bFXEjznZOZXOJurRgGv1mUjFmEuLtLl0TyDRJCe-XiiJ1mNCSdW1sqT6hJyq7Gj_SF3ChS3t6kG",
                "NEW", true));

        // Add filtered list
        filteredList.clear();
        filteredList.addAll(bikeList);
    }

    private void setupRecyclerView() {
        // Setup RecyclerView
        rvBikes.setLayoutManager(new LinearLayoutManager(this));
        bikeAdapter = new BikeAdapter(this, filteredList, this);
        rvBikes.setAdapter(bikeAdapter);
        rvBikes.setHasFixedSize(true);
    }

    private void filterBikes(String query) {
        filteredList.clear();

        if (query.isEmpty()) {
            filteredList.addAll(bikeList);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (BikeModel bike : bikeList) {
                if (bike.getBrand().toLowerCase().contains(lowerCaseQuery) ||
                        bike.getModel().toLowerCase().contains(lowerCaseQuery) ||
                        bike.getType().toLowerCase().contains(lowerCaseQuery)) {
                    filteredList.add(bike);
                }
            }
        }

        bikeAdapter.updateList(filteredList);
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
    public void onBikeClick(BikeModel bike) {
        // Navigate to bike details screen
        Intent intent = new Intent(this, BikeDetailsActivity.class);
        intent.putExtra("BIKE_MODEL", bike);
        intent.putExtra("BIKE_TYPE", bike.getType()); // "NEW" or "SECOND_HAND"
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        // Navigate back to Admin Dashboard
        Intent intent = new Intent(this, AdminDashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}