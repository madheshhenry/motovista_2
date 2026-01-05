package com.example.motovista_deep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.motovista_deep.api.ApiService;
import com.example.motovista_deep.api.RetrofitClient;
import com.example.motovista_deep.helpers.SharedPrefManager;
import com.example.motovista_deep.models.GetProfileResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerHomeActivity extends AppCompatActivity {

    // Featured cards
    private CardView cardFeatured1, cardFeatured2, cardFeatured3;

    // Quick action cards
    private CardView cardTestRide, cardService, cardRequestBike;

    // New arrivals cards
    private CardView cardRaptor, cardCruiser, cardTrailBlazer;

    // Buttons
    private Button btnLearnMoreRaptor, btnLearnMoreCruiser, btnLearnMoreTrailBlazer;
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
        setupClickListeners();
        setActiveTab(tabHome);
        loadUserProfile();
    }

    private void initializeViews() {
        // Top header
        btnNotifications = findViewById(R.id.btnNotifications);
        tvWelcome = findViewById(R.id.tvWelcome);
        ivUserProfile = findViewById(R.id.ivUserProfile);

        // Featured cards
        cardFeatured1 = findViewById(R.id.cardFeatured1);
        cardFeatured2 = findViewById(R.id.cardFeatured2);
        cardFeatured3 = findViewById(R.id.cardFeatured3);

        // Quick action cards
        cardTestRide = findViewById(R.id.cardTestRide);
        cardService = findViewById(R.id.cardService);
        cardRequestBike = findViewById(R.id.cardRequestBike);

        // New arrivals cards
        cardRaptor = findViewById(R.id.cardRaptor);
        cardCruiser = findViewById(R.id.cardCruiser);
        cardTrailBlazer = findViewById(R.id.cardTrailBlazer);

        // Learn More buttons
        btnLearnMoreRaptor = findViewById(R.id.btnLearnMoreRaptor);
        btnLearnMoreCruiser = findViewById(R.id.btnLearnMoreCruiser);
        btnLearnMoreTrailBlazer = findViewById(R.id.btnLearnMoreTrailBlazer);

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
                Toast.makeText(this, "Notifications", Toast.LENGTH_SHORT).show());

        ivUserProfile.setOnClickListener(v ->
                startActivity(new Intent(this, CustomerProfileScreenActivity.class)));

        // Bottom navigation
        tabHome.setOnClickListener(v -> setActiveTab(tabHome));
        tabBikes.setOnClickListener(v -> {
            setActiveTab(tabBikes);
            startActivity(new Intent(this, BikeCatalogActivity.class));
        });
        tabEmiCalculator.setOnClickListener(v -> {
            setActiveTab(tabEmiCalculator);
            startActivity(new Intent(this, EmiCalculatorActivity.class));
        });
        tabOrders.setOnClickListener(v -> {
            setActiveTab(tabOrders);
            startActivity(new Intent(this, OrderStatusActivity.class));
        });
        tabProfile.setOnClickListener(v -> {
            setActiveTab(tabProfile);
            startActivity(new Intent(this, CustomerProfileScreenActivity.class));
        });

        // AI Chatbot
        btnChatBot.setOnClickListener(v ->
                startActivity(new Intent(this, AIChatbotActivity.class)));

        // Featured cards
        cardFeatured1.setOnClickListener(v ->
                Toast.makeText(this, "SB-X 2024 Featured", Toast.LENGTH_SHORT).show());
        cardFeatured2.setOnClickListener(v ->
                Toast.makeText(this, "Monsoon Service Offer", Toast.LENGTH_SHORT).show());
        cardFeatured3.setOnClickListener(v ->
                Toast.makeText(this, "Community Ride", Toast.LENGTH_SHORT).show());

        // Quick action cards
        cardTestRide.setOnClickListener(v ->
                Toast.makeText(this, "Book Test Ride", Toast.LENGTH_SHORT).show());
        cardService.setOnClickListener(v ->
                startActivity(new Intent(this, MyBikesActivity.class)));
        cardRequestBike.setOnClickListener(v ->
                startActivity(new Intent(this, RequestBikeActivity.class)));

        // New arrivals Learn More buttons
        btnLearnMoreRaptor.setOnClickListener(v ->
                Toast.makeText(this, "SB-Raptor 500 Details", Toast.LENGTH_SHORT).show());
        btnLearnMoreCruiser.setOnClickListener(v ->
                Toast.makeText(this, "SB-Cruiser King Details", Toast.LENGTH_SHORT).show());
        btnLearnMoreTrailBlazer.setOnClickListener(v ->
                Toast.makeText(this, "SB-TrailBlazer Details", Toast.LENGTH_SHORT).show());

        // New arrivals cards (optional - if you want the entire card clickable)
        cardRaptor.setOnClickListener(v ->
                Toast.makeText(this, "SB-Raptor 500", Toast.LENGTH_SHORT).show());
        cardCruiser.setOnClickListener(v ->
                Toast.makeText(this, "SB-Cruiser King", Toast.LENGTH_SHORT).show());
        cardTrailBlazer.setOnClickListener(v ->
                Toast.makeText(this, "SB-TrailBlazer", Toast.LENGTH_SHORT).show());
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
                if (response.isSuccessful() && response.body() != null && response.body().status) {
                    GetProfileResponse.Data user = response.body().data;

                    // Update welcome text
                    if (user.full_name != null && !user.full_name.isEmpty()) {
                        tvWelcome.setText("Hello, " + user.full_name);
                    } else {
                        setDefaultWelcome();
                    }

                    // Load profile image
                    if (user.profile_image != null && !user.profile_image.isEmpty()) {
                        String imageUrl = RetrofitClient.BASE_URL.replace("api/", "")
                                + "uploads/profile_pics/" + user.profile_image;

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