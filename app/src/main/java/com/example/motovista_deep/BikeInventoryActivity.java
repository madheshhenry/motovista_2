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
import com.example.motovista_deep.models.GetBikesResponse;
import com.example.motovista_deep.helpers.SharedPrefManager;
import com.example.motovista_deep.api.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class BikeInventoryActivity extends AppCompatActivity implements BikeAdapter.OnBikeClickListener {

    private ConstraintLayout rootLayout;
    private ImageButton btnBack;
    private EditText etSearch;
    private ImageView icClear;
    private RecyclerView rvBikes;
    private LinearLayout tabDashboard, tabInventory, tabBikes, tabCustomers, tabSettings;

    private ImageView ivDashboard, ivInventory, ivBikes, ivCustomers, ivSettings;
    private TextView tvDashboard, tvInventory, tvBikes, tvCustomers, tvSettings;

    private BikeAdapter bikeAdapter;
    private List<BikeModel> bikeList = new ArrayList<>();
    private List<BikeModel> filteredList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_inventory);

        initializeViews();
        setupBottomNavigation();
        setupSearch();
        setupRecyclerView();
        loadBikesFromAPI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh bikes when returning to this screen
        loadBikesFromAPI();
    }

    private void initializeViews() {
        rootLayout = findViewById(R.id.rootLayout);
        btnBack = findViewById(R.id.btnBack);
        etSearch = findViewById(R.id.etSearch);
        icClear = findViewById(R.id.icClear);
        rvBikes = findViewById(R.id.rvBikes);

        tabDashboard = findViewById(R.id.tabDashboard);
        tabInventory = findViewById(R.id.tabInventory);
        tabBikes = findViewById(R.id.tabBikes);
        tabCustomers = findViewById(R.id.tabCustomers);
        tabSettings = findViewById(R.id.tabSettings);

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

        setActiveTab(tabBikes);
    }

    private void setupBottomNavigation() {
        btnBack.setOnClickListener(v -> onBackPressed());

        tabDashboard.setOnClickListener(v -> {
            Intent intent = new Intent(BikeInventoryActivity.this, AdminDashboardActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        tabInventory.setOnClickListener(v ->
                Toast.makeText(this, "Inventory screen coming soon", Toast.LENGTH_SHORT).show());

        tabBikes.setOnClickListener(v -> {
            setActiveTab(tabBikes);
            loadBikesFromAPI(); // Refresh when clicking bikes tab
        });

        tabCustomers.setOnClickListener(v ->
                Toast.makeText(this, "Customers screen coming soon", Toast.LENGTH_SHORT).show());

        tabSettings.setOnClickListener(v ->
                Toast.makeText(this, "Settings screen coming soon", Toast.LENGTH_SHORT).show());
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                icClear.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                filterBikes(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        icClear.setOnClickListener(v -> etSearch.setText(""));
    }

    private void loadBikesFromAPI() {
        String token = SharedPrefManager.getInstance(this).getToken();

        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String authToken = "Bearer " + token;

        Call<GetBikesResponse> call = RetrofitClient.getApiService().getAllBikes(authToken);

        call.enqueue(new Callback<GetBikesResponse>() {
            @Override
            public void onResponse(Call<GetBikesResponse> call, Response<GetBikesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GetBikesResponse apiResponse = response.body();

                    if ("success".equals(apiResponse.getStatus())) {
                        updateBikeList(apiResponse.getData());

                        int bikeCount = bikeList.size();
                        if (bikeCount > 0) {
                            Toast.makeText(BikeInventoryActivity.this,
                                    bikeCount + " bikes loaded from database",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(BikeInventoryActivity.this,
                                    "No bikes found in database",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(BikeInventoryActivity.this,
                                apiResponse.getMessage(),
                                Toast.LENGTH_SHORT).show();
                        showEmptyState();
                    }
                } else {
                    Toast.makeText(BikeInventoryActivity.this,
                            "Failed to load bikes. Please check your connection.",
                            Toast.LENGTH_SHORT).show();
                    showEmptyState();
                }
            }

            @Override
            public void onFailure(Call<GetBikesResponse> call, Throwable t) {
                Toast.makeText(BikeInventoryActivity.this,
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
                showEmptyState();
            }
        });
    }

    private void updateBikeList(List<BikeModel> apiBikes) {
        bikeList.clear();

        if (apiBikes != null && !apiBikes.isEmpty()) {
            for (BikeModel apiBike : apiBikes) {
                // Get image URL from API
                String imageUrl = apiBike.getImageUrl();

                // If image path exists, prepend base URL
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    // Check if it's already a full URL
                    if (!imageUrl.startsWith("http")) {
                        // Prepend base URL for relative paths
                        imageUrl = RetrofitClient.BASE_URL + imageUrl;
                    }
                }

                // Create new bike object from API data
                BikeModel bike = new BikeModel(
                        apiBike.getId(),
                        apiBike.getBrand() != null ? apiBike.getBrand() : "",
                        apiBike.getModel() != null ? apiBike.getModel() : "",
                        apiBike.getPrice() != null ? apiBike.getPrice() : "0",
                        apiBike.getCondition() != null ? apiBike.getCondition() : "Good",
                        imageUrl,  // Use processed image URL
                        apiBike.getType() != null ? apiBike.getType() : "NEW",
                        apiBike.getIsFeatured()  // Use getIsFeatured() not isFeatured()
                );

                // Set additional properties based on bike type
                if ("NEW".equals(apiBike.getType())) {
                    bike.setOnRoadPrice(apiBike.getOnRoadPrice());
                    bike.setEngineCC(apiBike.getEngineCC());
                    bike.setMileage(apiBike.getMileage());
                    bike.setTopSpeed(apiBike.getTopSpeed());
                    bike.setBrakingType(apiBike.getBrakingType());
                    bike.setFeatures(apiBike.getFeatures());
                } else if ("SECOND_HAND".equals(apiBike.getType())) {
                    bike.setYear(apiBike.getYear());
                    bike.setOdometer(apiBike.getOdometer());
                    bike.setOwnerDetails(apiBike.getOwnerDetails());
                    bike.setFeatures(apiBike.getFeatures());
                }

                bikeList.add(bike);
            }
        }

        filteredList.clear();
        filteredList.addAll(bikeList);

        if (bikeAdapter != null) {
            bikeAdapter.updateList(filteredList);
        }

        // Show/hide empty state
        if (bikeList.isEmpty()) {
            showEmptyState();
        } else {
            hideEmptyState();
        }
    }

    private void showEmptyState() {
        // You can show a "No bikes found" message here
        Toast.makeText(this, "No bikes available in inventory", Toast.LENGTH_LONG).show();
    }

    private void hideEmptyState() {
        // Hide empty state if you have one
    }

    private void setupRecyclerView() {
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
                String brand = bike.getBrand() != null ? bike.getBrand().toLowerCase() : "";
                String model = bike.getModel() != null ? bike.getModel().toLowerCase() : "";
                String type = bike.getType() != null ? bike.getType().toLowerCase() : "";

                if (brand.contains(lowerCaseQuery) ||
                        model.contains(lowerCaseQuery) ||
                        type.contains(lowerCaseQuery)) {
                    filteredList.add(bike);
                }
            }
        }

        if (bikeAdapter != null) {
            bikeAdapter.updateList(filteredList);
        }
    }

    private void setActiveTab(LinearLayout activeTab) {
        resetAllTabs();

        int primaryColor = ContextCompat.getColor(this, R.color.primary_color);
        int grayColor = ContextCompat.getColor(this, R.color.gray_400);

        if (activeTab == tabDashboard) {
            ivDashboard.setColorFilter(primaryColor);
            tvDashboard.setTextColor(primaryColor);
            tvDashboard.setTypeface(tvDashboard.getTypeface(), android.graphics.Typeface.BOLD);
        } else if (activeTab == tabInventory) {
            ivInventory.setColorFilter(primaryColor);
            tvInventory.setTextColor(primaryColor);
            tvInventory.setTypeface(tvInventory.getTypeface(), android.graphics.Typeface.BOLD);
        } else if (activeTab == tabBikes) {
            ivBikes.setColorFilter(primaryColor);
            tvBikes.setTextColor(primaryColor);
            tvBikes.setTypeface(tvBikes.getTypeface(), android.graphics.Typeface.BOLD);
        } else if (activeTab == tabCustomers) {
            ivCustomers.setColorFilter(primaryColor);
            tvCustomers.setTextColor(primaryColor);
            tvCustomers.setTypeface(tvCustomers.getTypeface(), android.graphics.Typeface.BOLD);
        } else if (activeTab == tabSettings) {
            ivSettings.setColorFilter(primaryColor);
            tvSettings.setTextColor(primaryColor);
            tvSettings.setTypeface(tvSettings.getTypeface(), android.graphics.Typeface.BOLD);
        }
    }

    private void resetAllTabs() {
        int grayColor = ContextCompat.getColor(this, R.color.gray_400);

        ivDashboard.setColorFilter(grayColor);
        tvDashboard.setTextColor(grayColor);
        tvDashboard.setTypeface(null, android.graphics.Typeface.NORMAL);

        ivInventory.setColorFilter(grayColor);
        tvInventory.setTextColor(grayColor);
        tvInventory.setTypeface(null, android.graphics.Typeface.NORMAL);

        ivBikes.setColorFilter(grayColor);
        tvBikes.setTextColor(grayColor);
        tvBikes.setTypeface(null, android.graphics.Typeface.NORMAL);

        ivCustomers.setColorFilter(grayColor);
        tvCustomers.setTextColor(grayColor);
        tvCustomers.setTypeface(null, android.graphics.Typeface.NORMAL);

        ivSettings.setColorFilter(grayColor);
        tvSettings.setTextColor(grayColor);
        tvSettings.setTypeface(null, android.graphics.Typeface.NORMAL);
    }

    @Override
    public void onBikeClick(BikeModel bike) {
        Intent intent = new Intent(this, BikeDetailsActivity.class);
        intent.putExtra("BIKE_MODEL", bike);
        intent.putExtra("BIKE_TYPE", bike.getType());
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, AdminDashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}