package com.example.motovista_deep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.motovista_deep.adapter.BikeAdapter;
import com.example.motovista_deep.api.ApiService;
import com.example.motovista_deep.api.RetrofitClient;
import com.example.motovista_deep.helpers.SharedPrefManager;
import com.example.motovista_deep.models.BikeModel;
import com.example.motovista_deep.models.GetProfileResponse;
import com.example.motovista_deep.models.GetShuffledBikesResponse;
import com.example.motovista_deep.utils.ImageUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerHomeActivity extends AppCompatActivity {

    // Dynamic sections
    private RecyclerView rvShuffledBikes;
    private BikeAdapter shuffledAdapter;
    private List<BikeModel> shuffledBikesList = new ArrayList<>();
    private LinearLayout brandSectionsContainer;

    // Quick action cards
    private CardView cardTestRide, cardService, cardRequestBike;
    private CardView btnChatBot;

    // Bottom navigation
    private LinearLayout tabHome, tabBikes, tabEmiCalculator, tabOrders, tabProfile;
    private ImageView ivHome, ivBikes, ivEmiCalculator, ivOrders, ivProfile;
    private TextView tvHome, tvBikes, tvEmiCalculator, tvOrders, tvProfile;

    // Top header
    private ImageView btnNotifications, ivUserProfile;
    private TextView tvWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home);

        initializeViews();
        setupRecyclerViews();
        setupClickListeners();
        setActiveTab(tabHome);
        loadUserProfile();
        loadShuffledBikes();
    }

    private void initializeViews() {
        // Top header
        btnNotifications = findViewById(R.id.btnNotifications);
        tvWelcome = findViewById(R.id.tvWelcome);
        ivUserProfile = findViewById(R.id.ivUserProfile);

        // RecyclerViews and containers
        rvShuffledBikes = findViewById(R.id.rvShuffledBikes);
        brandSectionsContainer = findViewById(R.id.brandSectionsContainer);

        // Quick action cards
        cardTestRide = findViewById(R.id.cardTestRide);
        cardService = findViewById(R.id.cardService);
        cardRequestBike = findViewById(R.id.cardRequestBike);

        // AI Chatbot button
        btnChatBot = findViewById(R.id.btnChatBot);

        // Bottom navigation tabs
        tabHome = findViewById(R.id.tabHome);
        tabBikes = findViewById(R.id.tabBikes);
        tabEmiCalculator = findViewById(R.id.tabEmiCalculator);
        tabOrders = findViewById(R.id.tabOrders);
        tabProfile = findViewById(R.id.tabProfile);

        // Initialize bottom navigation icons and text
        initializeBottomNavViews();
    }

    private void setupRecyclerViews() {
        // Horizontal list for featured
        rvShuffledBikes.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        shuffledAdapter = new BikeAdapter(this, shuffledBikesList, R.layout.item_home_featured, bike -> {
            Intent intent = new Intent(this, BikeDetailsCustomerActivity.class);
            intent.putExtra("bike_id", bike.getId());
            startActivity(intent);
        });
        rvShuffledBikes.setAdapter(shuffledAdapter);
    }

    private void loadShuffledBikes() {
        ApiService apiService = RetrofitClient.getApiService();
        Call<GetShuffledBikesResponse> call = apiService.getShuffledBikes();

        call.enqueue(new Callback<GetShuffledBikesResponse>() {
            @Override
            public void onResponse(Call<GetShuffledBikesResponse> call, Response<GetShuffledBikesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<BikeModel> allBikes = response.body().getData(); // Assuming getData() returns the list
                    if (allBikes != null && !allBikes.isEmpty()) {
                        // 1. Shuffled for global randomness
                        Collections.shuffle(allBikes);

                        // 2. Extract Featured (First 5)
                        shuffledBikesList.clear();
                        int featuredCount = Math.min(allBikes.size(), 5);
                        shuffledBikesList.addAll(allBikes.subList(0, featuredCount));
                        shuffledAdapter.notifyDataSetChanged();

                        // 3. Group by Brand
                        groupAndDisplayByBrand(allBikes);
                    }
                }
            }

            @Override
            public void onFailure(Call<GetShuffledBikesResponse> call, Throwable t) {
                Toast.makeText(CustomerHomeActivity.this, "Failed to load fleet", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void groupAndDisplayByBrand(List<BikeModel> bikes) {
        brandSectionsContainer.removeAllViews();

        if (bikes == null || bikes.isEmpty()) return;

        // Grouping logic
        Map<String, List<BikeModel>> brandMap = new LinkedHashMap<>();
        for (BikeModel bike : bikes) {
            String brand = bike.getBrand() != null ? bike.getBrand() : "Other Brands";
            if (!brandMap.containsKey(brand)) {
                brandMap.put(brand, new ArrayList<>());
            }
            brandMap.get(brand).add(bike);
        }

        // Inflate sections for each brand
        LayoutInflater inflater = LayoutInflater.from(this);
        for (Map.Entry<String, List<BikeModel>> entry : brandMap.entrySet()) {
            String brandName = entry.getKey();
            List<BikeModel> brandBikes = entry.getValue();

            View sectionView = inflater.inflate(R.layout.item_brand_section, brandSectionsContainer, false);
            TextView tvBrandHeader = sectionView.findViewById(R.id.tvBrandName);
            RecyclerView rvBrandBikes = sectionView.findViewById(R.id.rvBrandBikes);

            tvBrandHeader.setText("Bikes by " + brandName);

            View btnViewAll = sectionView.findViewById(R.id.btnViewAll);
            if (btnViewAll != null) {
                btnViewAll.setOnClickListener(v -> {
                    Intent intent = new Intent(this, BikeCatalogActivity.class);
                    intent.putExtra("BRAND_FILTER", brandName);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                });
            }

            // Horizontal Adapter for this brand
            BikeAdapter brandAdapter = new BikeAdapter(this, brandBikes, R.layout.item_home_new_arrival, bike -> {
                Intent intent = new Intent(this, BikeDetailsCustomerActivity.class);
                intent.putExtra("bike_id", bike.getId());
                startActivity(intent);
            });

            rvBrandBikes.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            rvBrandBikes.setAdapter(brandAdapter);
            rvBrandBikes.setNestedScrollingEnabled(false); // Smooth scrolling inside NestedScrollView

            brandSectionsContainer.addView(sectionView);
        }
    }

    private void initializeBottomNavViews() {
        // Get ImageViews from tab layouts
        ivHome = (ImageView) tabHome.getChildAt(0);
        ivBikes = (ImageView) tabBikes.getChildAt(0);
        ivEmiCalculator = (ImageView) tabEmiCalculator.getChildAt(0);
        ivOrders = (ImageView) tabOrders.getChildAt(0);
        ivProfile = (ImageView) tabProfile.getChildAt(0);

        // Get TextViews from tab layouts
        tvHome = (TextView) tabHome.getChildAt(1);
        tvBikes = (TextView) tabBikes.getChildAt(1);
        tvEmiCalculator = (TextView) tabEmiCalculator.getChildAt(1);
        tvOrders = (TextView) tabOrders.getChildAt(1);
        tvProfile = (TextView) tabProfile.getChildAt(1);
    }

    private void setupClickListeners() {
        // Top header
        btnNotifications.setOnClickListener(v ->
                startActivity(new Intent(this, CustomerNotificationsActivity.class)));

        ivUserProfile.setOnClickListener(v ->
                startActivity(new Intent(this, CustomerProfileScreenActivity.class)));

        // Bottom navigation
        tabHome.setOnClickListener(v -> setActiveTab(tabHome));
        tabBikes.setOnClickListener(v -> {
            setActiveTab(tabBikes);
            startActivity(new Intent(this, BikeCatalogActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
        tabEmiCalculator.setOnClickListener(v -> {
            setActiveTab(tabEmiCalculator);
            startActivity(new Intent(this, EmiCalculatorActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
        tabOrders.setOnClickListener(v -> {
            setActiveTab(tabOrders);
            startActivity(new Intent(this, CustomerOrdersActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
        tabProfile.setOnClickListener(v -> {
            setActiveTab(tabProfile);
            startActivity(new Intent(this, CustomerProfileScreenActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        // AI Chatbot
        btnChatBot.setOnClickListener(v ->
                startActivity(new Intent(this, AIChatbotActivity.class)));

        // Quick action cards
        cardTestRide.setOnClickListener(v ->
                Toast.makeText(this, "Book Test Ride", Toast.LENGTH_SHORT).show());
        cardService.setOnClickListener(v ->
                startActivity(new Intent(this, MyBikesActivity.class)));
        cardRequestBike.setOnClickListener(v ->
                startActivity(new Intent(this, RequestBikeActivity.class)));
    }

    private void setActiveTab(LinearLayout activeTab) {
        resetAllTabs();

        int activeColor = ContextCompat.getColor(this, R.color.primary_color);
        int inactiveColor = ContextCompat.getColor(this, R.color.gray_400);

        if (activeTab == tabHome) {
            ivHome.setImageResource(R.drawable.ic_home_filled);
            ivHome.setColorFilter(activeColor);
            tvHome.setTextColor(activeColor);
            tvHome.setTypeface(tvHome.getTypeface(), android.graphics.Typeface.BOLD);
        } else if (activeTab == tabBikes) {
            ivBikes.setColorFilter(activeColor);
            tvBikes.setTextColor(activeColor);
            tvBikes.setTypeface(tvBikes.getTypeface(), android.graphics.Typeface.BOLD);
        } else if (activeTab == tabEmiCalculator) {
            ivEmiCalculator.setColorFilter(activeColor);
            tvEmiCalculator.setTextColor(activeColor);
            tvEmiCalculator.setTypeface(tvEmiCalculator.getTypeface(), android.graphics.Typeface.BOLD);
        } else if (activeTab == tabOrders) {
            ivOrders.setImageResource(R.drawable.ic_receipt_long_filled);
            ivOrders.setColorFilter(activeColor);
            tvOrders.setTextColor(activeColor);
            tvOrders.setTypeface(tvOrders.getTypeface(), android.graphics.Typeface.BOLD);
        } else if (activeTab == tabProfile) {
            ivProfile.setColorFilter(activeColor);
            tvProfile.setTextColor(activeColor);
            tvProfile.setTypeface(tvProfile.getTypeface(), android.graphics.Typeface.BOLD);
        }
    }

    private void resetAllTabs() {
        int inactiveColor = ContextCompat.getColor(this, R.color.gray_400);

        // Reset Home
        ivHome.setImageResource(R.drawable.ic_home);
        ivHome.setColorFilter(inactiveColor);
        tvHome.setTextColor(inactiveColor);
        tvHome.setTypeface(null, android.graphics.Typeface.NORMAL);

        // Reset Bikes
        ivBikes.setColorFilter(inactiveColor);
        tvBikes.setTextColor(inactiveColor);
        tvBikes.setTypeface(null, android.graphics.Typeface.NORMAL);

        // Reset EMI Calculator
        ivEmiCalculator.setColorFilter(inactiveColor);
        tvEmiCalculator.setTextColor(inactiveColor);
        tvEmiCalculator.setTypeface(null, android.graphics.Typeface.NORMAL);

        // Reset Orders
        ivOrders.setImageResource(R.drawable.ic_receipt_long);
        ivOrders.setColorFilter(inactiveColor);
        tvOrders.setTextColor(inactiveColor);
        tvOrders.setTypeface(null, android.graphics.Typeface.NORMAL);

        // Reset Profile
        ivProfile.setColorFilter(inactiveColor);
        tvProfile.setTextColor(inactiveColor);
        tvProfile.setTypeface(null, android.graphics.Typeface.NORMAL);
    }

    private void loadUserProfile() {
        String token = SharedPrefManager.getInstance(this).getToken();
        if (token == null || token.isEmpty()) {
            setDefaultWelcome();
            return;
        }

        ApiService apiService = RetrofitClient.getApiService();
        apiService.getProfile("Bearer " + token).enqueue(new Callback<GetProfileResponse>() {
            @Override
            public void onResponse(Call<GetProfileResponse> call, Response<GetProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    GetProfileResponse.Data user = response.body().data;

                    // Update welcome text
                    if (user.full_name != null && !user.full_name.isEmpty()) {
                        tvWelcome.setText("Hello, " + user.full_name);
                    } else {
                        setDefaultWelcome();
                    }

                    // Load profile image
                    if (user.profile_image != null && !user.profile_image.isEmpty()) {
                        String imageUrl = ImageUtils.getFullImageUrl(user.profile_image, ImageUtils.PATH_PROFILE_PICS);

                        Glide.with(CustomerHomeActivity.this)
                                .load(imageUrl)
                                .placeholder(R.drawable.ic_profile_placeholder)
                                .error(R.drawable.ic_profile_placeholder)
                                .transform(new CircleCrop())
                                .into(ivUserProfile);
                    }
                } else {
                    setDefaultWelcome();
                }
            }

            @Override
            public void onFailure(Call<GetProfileResponse> call, Throwable t) {
                setDefaultWelcome();
                // Optional: Log error for debugging
                // Log.e("CustomerHomeActivity", "Failed to load profile: " + t.getMessage());
            }
        });
    }

    private void setDefaultWelcome() {
        // 1. Get the User object from SharedPref
        com.example.motovista_deep.models.User user = SharedPrefManager.getInstance(this).getUser();

        // 2. Check if user exists and get the name
        if (user != null && user.getFull_name() != null) {
            tvWelcome.setText("Hello, " + user.getFull_name());
        } else {
            tvWelcome.setText("Hello, Customer");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh profile when returning to home
        loadUserProfile();
        loadShuffledBikes();
    }

    @Override
    public void onBackPressed() {
        // Exit app or go to home screen
        if (isTaskRoot()) {
            // If this is the root activity, show exit confirmation
            moveTaskToBack(true);
        } else {
            super.onBackPressed();
        }
    }
}