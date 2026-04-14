package com.example.motovista_deep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Typeface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.WindowCompat;
import android.util.Log;

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
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.motovista_deep.utils.SystemUIHelper;

public class CustomerHomeActivity extends AppCompatActivity {

    // Dynamic sections
    private RecyclerView rvShuffledBikes;
    private BikeAdapter shuffledAdapter;
    private List<BikeModel> shuffledBikesList = new ArrayList<>();
    private LinearLayout brandSectionsContainer;

    // Quick action buttons
    private android.view.View cardTestRide, cardService, cardRequestBike;
    private CardView btnChatBot;

    // Bottom navigation
    private LinearLayout tabHome, tabBikes, tabEmiCalculator, tabOrders, tabProfile;
    private ImageView ivHome, ivBikes, ivEmiCalculator, ivOrders, ivProfile;
    private TextView tvHome, tvBikes, tvEmiCalculator, tvOrders, tvProfile;
    private View dotHome, dotBikes, dotEmiCalculator, dotOrders, dotProfile;

    // Top header
    private ImageView btnNotifications, ivUserProfile;
    private TextView tvWelcome, tvGreeting;

    private ApiService apiService;
    private SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home);

        // Enable edge-to-edge display and dynamic insets
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        
        // Pass null for headerView as we'll handle its premium padding manually below
        SystemUIHelper.setupEdgeToEdgeWithScroll(this, 
            findViewById(R.id.rootLayout), 
            null, 
            findViewById(R.id.scrollView),
            findViewById(R.id.bottomNavigation));

        // Manual targeted fix for Premium Header overlap
        // Called AFTER SystemUIHelper setup as per user requirements
        applyHeaderWindowInsets();

        sharedPrefManager = SharedPrefManager.getInstance(this);
        apiService = RetrofitClient.getApiService();

        initializeViews();
        updateGreeting();
        setupRecyclerViews();
        setupClickListeners();
        setActiveTab(tabHome);
        loadUserProfile();
        loadShuffledBikes();
        requestNotificationPermission();
        com.example.motovista_deep.utils.FcmTokenManager.init(this);

        applyEntryAnimations();
        startNotificationPulse();
        setupParallaxEffect();
    }

    private void applyHeaderWindowInsets() {
        View premiumHeader = findViewById(R.id.premiumHeader);
        if (premiumHeader == null) return;

        // Increased baseline padding for absolute notch safety (Standard 16dp + 36dp design margin)
        final int baselinePadding = (int) (52 * getResources().getDisplayMetrics().density);

        ViewCompat.setOnApplyWindowInsetsListener(premiumHeader, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.statusBars());
            v.setPadding(
                v.getPaddingLeft(),
                systemBars.top + baselinePadding, // Dynamic Status Bar Height + Premium Margin
                v.getPaddingRight(),
                v.getPaddingBottom()
            );
            return insets;
        });

        // Ensure insets are actually triggered
        ViewCompat.requestApplyInsets(premiumHeader);
    }

    private void setupParallaxEffect() {
        androidx.core.widget.NestedScrollView scrollView = findViewById(R.id.scrollView);
        View premiumHeader = findViewById(R.id.premiumHeader);
        
        scrollView.setOnScrollChangeListener((androidx.core.widget.NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            premiumHeader.setTranslationY(scrollY * 0.4f);
        });
    }

    private void applyEntryAnimations() {
        View premiumHeader = findViewById(R.id.premiumHeader);
        View profileContainer = findViewById(R.id.profileContainer);
        View quickActions = findViewById(R.id.scrollView).findViewById(R.id.scrollView); // Grid layout child
        
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        
        premiumHeader.startAnimation(fadeIn);
        profileContainer.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_left));
    }

    private void startNotificationPulse() {
        Animation pulse = AnimationUtils.loadAnimation(this, R.anim.pulse);
        btnNotifications.startAnimation(pulse);
    }

    private void initializeViews() {
        // Top header
        btnNotifications = findViewById(R.id.btnNotifications);
        tvWelcome = findViewById(R.id.tvWelcome);
        tvGreeting = findViewById(R.id.tvGreeting);
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
        new LinearSnapHelper().attachToRecyclerView(rvShuffledBikes);
    }

    private void loadShuffledBikes() {
        ApiService apiService = RetrofitClient.getApiService();
        Call<GetShuffledBikesResponse> call = apiService.getShuffledBikes();

        call.enqueue(new Callback<GetShuffledBikesResponse>() {
            @Override
            public void onResponse(Call<GetShuffledBikesResponse> call, Response<GetShuffledBikesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (!response.body().isSuccess()) {
                        com.example.motovista_deep.utils.AuthHelper.handleAuthFailure(CustomerHomeActivity.this, response.body().getMessage());
                        return;
                    }
                    List<BikeModel> allBikes = response.body().getData(); 
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
                } else {
                    if (com.example.motovista_deep.utils.AuthHelper.handleAuthFailure(CustomerHomeActivity.this, response.code(), response.message())) {
                        return;
                    }
                    Toast.makeText(CustomerHomeActivity.this, "Failed to load fleet", Toast.LENGTH_SHORT).show();
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
                    overridePendingTransition(0, 0);
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
            new LinearSnapHelper().attachToRecyclerView(rvBrandBikes);
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

        // Dots
        dotHome = tabHome.findViewById(R.id.dotHome);
        dotBikes = tabBikes.findViewById(R.id.dotBikes);
        dotEmiCalculator = tabEmiCalculator.findViewById(R.id.dotEmiCalculator);
        dotOrders = tabOrders.findViewById(R.id.dotOrders);
        dotProfile = tabProfile.findViewById(R.id.dotProfile);
    }

    private void setupClickListeners() {
        // Top header
        btnNotifications.setOnClickListener(v ->
                startActivity(new Intent(this, CustomerNotificationsActivity.class)));

        ivUserProfile.setOnClickListener(v ->
                startActivity(new Intent(this, CustomerProfileScreenActivity.class)));

        // Bottom navigation
        tabHome.setOnClickListener(v -> {
            // Already here
            setActiveTab(tabHome);
        });

        tabBikes.setOnClickListener(v -> {
            startActivity(new Intent(this, BikeCatalogActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });

        tabEmiCalculator.setOnClickListener(v -> {
            startActivity(new Intent(this, EmiCalculatorActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });

        tabOrders.setOnClickListener(v -> {
            startActivity(new Intent(this, CustomerOrdersActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });

        tabProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, CustomerProfileScreenActivity.class));
            overridePendingTransition(0, 0);
            finish();
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

        // Use standard primary color from theme
        int activeColor = ContextCompat.getColor(this, R.color.primary_color);
        Typeface boldTypeface = Typeface.create("sans-serif-bold", Typeface.NORMAL);

        if (activeTab == tabHome) {
            ivHome.setImageResource(R.drawable.ic_home_filled);
            ivHome.setColorFilter(activeColor);
            tvHome.setTextColor(activeColor);
            tvHome.setTypeface(boldTypeface);
        } else if (activeTab == tabBikes) {
            ivBikes.setImageResource(R.drawable.ic_two_wheeler); // Ensure filled if available
            ivBikes.setColorFilter(activeColor);
            tvBikes.setTextColor(activeColor);
            tvBikes.setTypeface(boldTypeface);
        } else if (activeTab == tabEmiCalculator) {
            ivEmiCalculator.setImageResource(R.drawable.ic_calculate); // Ensure consistency
            ivEmiCalculator.setColorFilter(activeColor);
            tvEmiCalculator.setTextColor(activeColor);
            tvEmiCalculator.setTypeface(boldTypeface);
        } else if (activeTab == tabOrders) {
            ivOrders.setImageResource(R.drawable.ic_receipt_long_filled);
            ivOrders.setColorFilter(activeColor);
            tvOrders.setTextColor(activeColor);
            tvOrders.setTypeface(boldTypeface);
        } else if (activeTab == tabProfile) {
            ivProfile.setImageResource(R.drawable.ic_person_filled);
            ivProfile.setColorFilter(activeColor);
            tvProfile.setTextColor(activeColor);
            tvProfile.setTypeface(boldTypeface);
        }

        // Show pill indicator for active tab
        showActiveDot(activeTab);
    }

    private void showActiveDot(LinearLayout activeTab) {
        dotHome.setVisibility(activeTab == tabHome ? View.VISIBLE : View.INVISIBLE);
        dotBikes.setVisibility(activeTab == tabBikes ? View.VISIBLE : View.INVISIBLE);
        dotEmiCalculator.setVisibility(activeTab == tabEmiCalculator ? View.VISIBLE : View.INVISIBLE);
        dotOrders.setVisibility(activeTab == tabOrders ? View.VISIBLE : View.INVISIBLE);
        dotProfile.setVisibility(activeTab == tabProfile ? View.VISIBLE : View.INVISIBLE);
        
        // Optional: Animate dot
        View activeDot = null;
        if (activeTab == tabHome) activeDot = dotHome;
        else if (activeTab == tabBikes) activeDot = dotBikes;
        else if (activeTab == tabEmiCalculator) activeDot = dotEmiCalculator;
        else if (activeTab == tabOrders) activeDot = dotOrders;
        else if (activeTab == tabProfile) activeDot = dotProfile;
        
        if (activeDot != null) {
            activeDot.setScaleX(0);
            activeDot.setScaleY(0);
            activeDot.animate().scaleX(1).scaleY(1).setDuration(200).start();
        }
    }

    private void resetAllTabs() {
        // Use colorOutline from theme for consistent inactive state
        int inactiveColor = ContextCompat.getColor(this, R.color.gray_400); 
        Typeface mediumTypeface = Typeface.create("sans-serif-medium", Typeface.NORMAL);

        // Reset Home
        ivHome.setImageResource(R.drawable.ic_home_filled);
        ivHome.setColorFilter(inactiveColor);
        tvHome.setTextColor(inactiveColor);
        tvHome.setTypeface(mediumTypeface);

        // Reset Bikes
        ivBikes.setImageResource(R.drawable.ic_two_wheeler);
        ivBikes.setColorFilter(inactiveColor);
        tvBikes.setTextColor(inactiveColor);
        tvBikes.setTypeface(mediumTypeface);

        // Reset EMI Calculator
        ivEmiCalculator.setImageResource(R.drawable.ic_calculate);
        ivEmiCalculator.setColorFilter(inactiveColor);
        tvEmiCalculator.setTextColor(inactiveColor);
        tvEmiCalculator.setTypeface(mediumTypeface);

        // Reset Orders
        ivOrders.setImageResource(R.drawable.ic_receipt_long);
        ivOrders.setColorFilter(inactiveColor);
        tvOrders.setTextColor(inactiveColor);
        tvOrders.setTypeface(mediumTypeface);

        // Reset Profile
        ivProfile.setImageResource(R.drawable.ic_person);
        ivProfile.setColorFilter(inactiveColor);
        tvProfile.setTextColor(inactiveColor);
        tvProfile.setTypeface(mediumTypeface);
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
                    Log.d("API_DEBUG", "Profile Load Success: " + response.body().message);
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
                        ivUserProfile.setColorFilter(null); // Remove tint for actual photo
                        Glide.with(CustomerHomeActivity.this)
                                .load(imageUrl)
                                .placeholder(R.drawable.ic_profile_placeholder)
                                .error(R.drawable.ic_profile_placeholder)
                                .transform(new CircleCrop())
                                .into(ivUserProfile);
                    } else {
                        // No logic needed, default ic_person with tint from XML is handled or we can set it here
                        ivUserProfile.setImageResource(R.drawable.ic_person);
                        ivUserProfile.setColorFilter(ContextCompat.getColor(CustomerHomeActivity.this, R.color.primary_color));
                    }
                } else {
                    Log.d("API_DEBUG", "Profile Load Error: " + response.code() + " - " + response.message());
                    setDefaultWelcome();
                }
            }

            @Override
            public void onFailure(Call<GetProfileResponse> call, Throwable t) {
                Log.d("API_DEBUG", "Profile Load Failure: " + t.getMessage());
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
        updateGreeting();
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != 
                android.content.pm.PackageManager.PERMISSION_GRANTED) {
                androidx.core.app.ActivityCompat.requestPermissions(this, 
                    new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
    }

    private void updateGreeting() {
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        String greeting;
        if (timeOfDay >= 5 && timeOfDay < 12) {
            greeting = "Good Morning,";
        } else if (timeOfDay >= 12 && timeOfDay < 17) {
            greeting = "Good Afternoon,";
        } else if (timeOfDay >= 17 && timeOfDay < 21) {
            greeting = "Good Evening,";
        } else {
            greeting = "Good Night,";
        }

        if (tvGreeting != null) {
            tvGreeting.setText(greeting);
        }
    }

    @Override
    public void onBackPressed() {
        // Exit app from home
        moveTaskToBack(true);
    }
}