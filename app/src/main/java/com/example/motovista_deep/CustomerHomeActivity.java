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

// 🔹 NEW IMPORTS (STEP-5)
import com.bumptech.glide.Glide;
import com.example.motovista_deep.api.ApiService;
import com.example.motovista_deep.api.RetrofitClient;
import com.example.motovista_deep.models.GetProfileResponse;
import com.example.motovista_deep.models.ProfileData;
import com.example.motovista_deep.helpers.SharedPrefManager;

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
    private ImageView btnNotifications;
    private TextView tvWelcome;
    private ImageView ivUserProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home);

        // Initialize views
        initializeViews();

        // Setup click listeners
        setupClickListeners();

        // Set active tab (Home is active by default)
        setActiveTab(tabHome);

        // Default welcome text
        String customerName = getCustomerName();
        tvWelcome.setText("Hello, " + customerName);

        // ✅ STEP-5 : LOAD PROFILE DATA + IMAGE
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

        // Bottom navigation icons
        ivHome = findViewById(R.id.ivHome);
        ivBikes = findViewById(R.id.ivBikes);
        ivEmiCalculator = findViewById(R.id.ivEmiCalculator);
        ivOrders = findViewById(R.id.ivOrders);
        ivProfile = findViewById(R.id.ivProfile);

        // Bottom navigation text
        tvHome = findViewById(R.id.tvHome);
        tvBikes = findViewById(R.id.tvBikes);
        tvEmiCalculator = findViewById(R.id.tvEmiCalculator);
        tvOrders = findViewById(R.id.tvOrders);
        tvProfile = findViewById(R.id.tvProfile);
    }

    private void setupClickListeners() {

        btnNotifications.setOnClickListener(v ->
                Toast.makeText(this, "Notifications", Toast.LENGTH_SHORT).show());

        ivUserProfile.setOnClickListener(v -> {
            setActiveTab(tabProfile);
            startActivity(new Intent(this, CustomerProfileScreenActivity.class));
        });

        tabHome.setOnClickListener(v -> setActiveTab(tabHome));

        tabBikes.setOnClickListener(v -> {
            setActiveTab(tabBikes);
            startActivity(new Intent(this, BikeCatalogActivity.class));
        });

        tabEmiCalculator.setOnClickListener(v -> {
            setActiveTab(tabEmiCalculator);
            startActivity(new Intent(this, EmiCalculatorActivity.class));
        });

        tabProfile.setOnClickListener(v -> {
            setActiveTab(tabProfile);
            startActivity(new Intent(this, CustomerProfileScreenActivity.class));
        });
    }

    private void setActiveTab(LinearLayout activeTab) {
        resetAllTabs();

        if (activeTab == tabHome) {
            ivHome.setColorFilter(ContextCompat.getColor(this, R.color.primary_color));
            tvHome.setTextColor(ContextCompat.getColor(this, R.color.primary_color));
            tvHome.setTypeface(tvHome.getTypeface(), android.graphics.Typeface.BOLD);
        } else if (activeTab == tabBikes) {
            ivBikes.setColorFilter(ContextCompat.getColor(this, R.color.primary_color));
            tvBikes.setTextColor(ContextCompat.getColor(this, R.color.primary_color));
            tvBikes.setTypeface(tvBikes.getTypeface(), android.graphics.Typeface.BOLD);
        } else if (activeTab == tabEmiCalculator) {
            ivEmiCalculator.setColorFilter(ContextCompat.getColor(this, R.color.primary_color));
            tvEmiCalculator.setTextColor(ContextCompat.getColor(this, R.color.primary_color));
            tvEmiCalculator.setTypeface(tvEmiCalculator.getTypeface(), android.graphics.Typeface.BOLD);
        } else if (activeTab == tabProfile) {
            ivProfile.setColorFilter(ContextCompat.getColor(this, R.color.primary_color));
            tvProfile.setTextColor(ContextCompat.getColor(this, R.color.primary_color));
            tvProfile.setTypeface(tvProfile.getTypeface(), android.graphics.Typeface.BOLD);
        }
    }

    private void resetAllTabs() {
        ivHome.setColorFilter(ContextCompat.getColor(this, R.color.gray_400));
        tvHome.setTextColor(ContextCompat.getColor(this, R.color.gray_400));
        tvHome.setTypeface(null, android.graphics.Typeface.NORMAL);

        ivBikes.setColorFilter(ContextCompat.getColor(this, R.color.gray_400));
        tvBikes.setTextColor(ContextCompat.getColor(this, R.color.gray_400));

        ivEmiCalculator.setColorFilter(ContextCompat.getColor(this, R.color.gray_400));
        tvEmiCalculator.setTextColor(ContextCompat.getColor(this, R.color.gray_400));

        ivProfile.setColorFilter(ContextCompat.getColor(this, R.color.gray_400));
        tvProfile.setTextColor(ContextCompat.getColor(this, R.color.gray_400));
    }

    private String getCustomerName() {
        return "Santhosh";
    }

    // 🔥 STEP-5 LOGIC (ONLY NEW METHOD)
    private void loadUserProfile() {

        String token = "Bearer " +
                SharedPrefManager.getInstance(this).getToken();

        ApiService apiService = RetrofitClient.getApiService();

        apiService.getProfile(token).enqueue(new Callback<GetProfileResponse>() {
            @Override
            public void onResponse(Call<GetProfileResponse> call,
                                   Response<GetProfileResponse> response) {

                if (response.isSuccessful()
                        && response.body() != null
                        && response.body().status) {

                    // ✅ EXACT MODEL TYPE
                    GetProfileResponse.Data user = response.body().data;

                    // ✅ Welcome text
                    tvWelcome.setText("Hello, " + user.full_name);

                    // ✅ Profile image (filename only)
                    if (user.profile_image != null &&
                            !user.profile_image.isEmpty()) {

                        String imageUrl =
                                RetrofitClient.BASE_URL +
                                        "uploads/" +
                                        user.profile_image;

                        Glide.with(CustomerHomeActivity.this)
                                .load(imageUrl)
                                .placeholder(R.drawable.ic_profile_placeholder)
                                .error(R.drawable.ic_profile_placeholder)
                                .circleCrop()
                                .into(ivUserProfile);
                    }
                }
            }

            @Override
            public void onFailure(Call<GetProfileResponse> call, Throwable t) {
                // silent fail – no UI disturb
            }
        });
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
