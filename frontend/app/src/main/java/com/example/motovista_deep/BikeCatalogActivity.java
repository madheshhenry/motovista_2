package com.example.motovista_deep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.graphics.Typeface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.motovista_deep.adapter.BikeAdapter;
import com.example.motovista_deep.adapter.CustomerBikeAdapter;
import com.example.motovista_deep.api.ApiService;
import com.example.motovista_deep.api.RetrofitClient;
import com.example.motovista_deep.helpers.SharedPrefManager;
import com.example.motovista_deep.models.BikeModel;
import com.example.motovista_deep.models.GetBikesResponse;
import com.example.motovista_deep.models.User;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BikeCatalogActivity extends AppCompatActivity implements CustomerBikeAdapter.OnBikeClickListener {

    private static final String TAG = "BikeCatalogActivity";

    // Top header
    private ImageView btnBack;
    private TextView tvTitle;

    // Filter chips
    private CardView chipAll, chipPetrol, chipEBikes, chipSecondHand;

    // RecyclerView
    private RecyclerView rvBikes;
    private CustomerBikeAdapter bikeAdapter;
    private List<BikeModel> allBikesList = new ArrayList<>();
    private String brandFilter;
    private String currentFilter = "ALL";
    private EditText etSearch;

    // Bottom navigation
    private LinearLayout tabHome, tabBikes, tabEmiCalculator, tabOrders, tabProfile;
    private ImageView ivHome, ivBikes, ivEmiCalculator, ivOrders, ivProfile;
    private TextView tvHome, tvBikes, tvEmiCalculator, tvOrders, tvProfile;
    private View dotHome, dotBikes, dotEmiCalculator, dotOrders, dotProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_catalog);

        // Initialize views
        initializeViews();

        // Update greeting
        TextView tvGreeting = findViewById(R.id.tvGreeting);
        if (tvGreeting != null) {
            User user = SharedPrefManager.getInstance(this).getUser();
            if (user != null && user.getFull_name() != null && !user.getFull_name().isEmpty()) {
                String firstName = user.getFull_name().split(" ")[0];
                tvGreeting.setText("Hey " + firstName + ", find your");
            }
        }

        // Setup RecyclerView
        setupRecyclerView();

        // Setup click listeners
        setupClickListeners();

        // Set active tab (Bikes is active by default)
        setActiveTab(tabBikes);

        // Set active filter chip (All is active by default)
        setActiveChip(chipAll);

        // Fetch bikes from API
        brandFilter = getIntent().getStringExtra("BRAND_FILTER");
        fetchBikes();
    }

    private void initializeViews() {
        // Top header
        btnBack = findViewById(R.id.btnBack);
        tvTitle = findViewById(R.id.tvTitle);

        // Filter chips
        chipAll = findViewById(R.id.chipAll);
        chipPetrol = findViewById(R.id.chipPetrol);
        chipEBikes = findViewById(R.id.chipEBikes);
        chipSecondHand = findViewById(R.id.chipSecondHand);

        // Search bar
        etSearch = findViewById(R.id.etSearch);

        // RecyclerView
        rvBikes = findViewById(R.id.rvBikes);

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

        // Bottom navigation dots
        dotHome = findViewById(R.id.dotHome);
        dotBikes = findViewById(R.id.dotBikes);
        dotEmiCalculator = findViewById(R.id.dotEmiCalculator);
        dotOrders = findViewById(R.id.dotOrders);
        dotProfile = findViewById(R.id.dotProfile);
    }

    private void setupRecyclerView() {
        rvBikes.setLayoutManager(new LinearLayoutManager(this));
        bikeAdapter = new CustomerBikeAdapter(this, new ArrayList<>(), this);
        rvBikes.setAdapter(bikeAdapter);
    }

    private void fetchBikes() {
        // Show loading if needed? 
        // Ideally we should have a ProgressBar in the layout. For now, we just fetch.
        
        String token = SharedPrefManager.getInstance(this).getToken();
        if (token == null) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = RetrofitClient.getApiService();
        Call<GetBikesResponse> call = apiService.getAllBikes("Bearer " + token);

        call.enqueue(new Callback<GetBikesResponse>() {
            @Override
            public void onResponse(Call<GetBikesResponse> call, Response<GetBikesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GetBikesResponse bikesResponse = response.body();
                    if (bikesResponse.isSuccess() && bikesResponse.getData() != null) {
                        allBikesList = bikesResponse.getData();
                        if (brandFilter != null && !brandFilter.isEmpty()) {
                            tvTitle.setText(brandFilter);
                            filterBikesByBrand(brandFilter);
                        } else {
                            bikeAdapter.updateList(allBikesList);
                        }
                        Log.d(TAG, "Fetched " + allBikesList.size() + " bikes");
                    } else {
                        String errorMsg = bikesResponse.getMessage();
                        if (com.example.motovista_deep.utils.AuthHelper.handleAuthFailure(BikeCatalogActivity.this, errorMsg)) {
                            return;
                        }
                        if (errorMsg == null || errorMsg.isEmpty()) errorMsg = "No bikes found";
                        Toast.makeText(BikeCatalogActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (com.example.motovista_deep.utils.AuthHelper.handleAuthFailure(BikeCatalogActivity.this, response.code(), response.message())) {
                        return;
                    }
                    Toast.makeText(BikeCatalogActivity.this, "Failed to load bikes: " + response.message(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Fetch failed: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<GetBikesResponse> call, Throwable t) {
                Toast.makeText(BikeCatalogActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Network error", t);
            }
        });
    }

    private void filterBikesByBrand(String brand) {
        if (allBikesList == null) return;
        List<BikeModel> filteredList = new ArrayList<>();
        for (BikeModel bike : allBikesList) {
            if (brand.equalsIgnoreCase(bike.getBrand())) {
                filteredList.add(bike);
            }
        }
        bikeAdapter.updateList(filteredList);
    }

    private void filterBikes(String filterType) {
        if (allBikesList == null || allBikesList.isEmpty()) return;

        this.currentFilter = filterType;
        executeFiltering();
    }

    private void executeFiltering() {
        String query = etSearch.getText().toString().toLowerCase().trim();
        List<BikeModel> filteredList = new ArrayList<>();

        for (BikeModel bike : allBikesList) {
            // Check Category Filter
            boolean matchesCategory = false;
            switch (currentFilter) {
                case "ALL":
                    matchesCategory = true;
                    break;
                case "PETROL":
                    if ("Petrol".equalsIgnoreCase(bike.getFuelType())) matchesCategory = true;
                    break;
                case "ELECTRIC":
                    if ("Electric".equalsIgnoreCase(bike.getFuelType())) matchesCategory = true;
                    break;
                case "SECOND_HAND":
                    if ("USED".equalsIgnoreCase(bike.getType())) matchesCategory = true;
                    break;
            }

            // Check Search Query
            boolean matchesSearch = true;
            if (!query.isEmpty()) {
                String brand = bike.getBrand().toLowerCase();
                String model = bike.getModel().toLowerCase();
                if (!brand.contains(query) && !model.contains(query)) {
                    matchesSearch = false;
                }
            }

            if (matchesCategory && matchesSearch) {
                filteredList.add(bike);
            }
        }
        bikeAdapter.updateList(filteredList);
    }

    private void setupClickListeners() {
        // Back button
        btnBack.setOnClickListener(v -> onBackPressed());

        // Search Bar functionality
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                executeFiltering();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Filter chips
        chipAll.setOnClickListener(v -> {
            setActiveChip(chipAll);
            filterBikes("ALL");
        });

        chipPetrol.setOnClickListener(v -> {
            setActiveChip(chipPetrol);
            filterBikes("PETROL");
        });

        chipEBikes.setOnClickListener(v -> {
            setActiveChip(chipEBikes);
            filterBikes("ELECTRIC");
        });

        chipSecondHand.setOnClickListener(v -> {
            setActiveChip(chipSecondHand);
            filterBikes("SECOND_HAND");
        });

        // Bottom navigation tabs
        tabHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, CustomerHomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        });

        tabBikes.setOnClickListener(v -> {
            // Already here
            setActiveTab(tabBikes);
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
    }

    @Override
    public void onBikeClick(BikeModel bike) {
        Intent intent = new Intent(BikeCatalogActivity.this, BikeDetailsCustomerActivity.class);
        intent.putExtra("bike_id", bike.getId());
        // Pass other details if BikeDetailsCustomerActivity is updated to use them
        // intent.putExtra("BIKE_NAME", bike.getBrand() + " " + bike.getModel());
        
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void setActiveTab(LinearLayout activeTab) {
        resetAllTabs();

        int activeColor = ContextCompat.getColor(this, R.color.primary_color);
        Typeface boldTypeface = Typeface.create("sans-serif-bold", Typeface.NORMAL);

        if (activeTab == tabHome) {
            ivHome.setImageResource(R.drawable.ic_home_filled);
            ivHome.setColorFilter(activeColor);
            tvHome.setTextColor(activeColor);
            tvHome.setTypeface(boldTypeface);
        } else if (activeTab == tabBikes) {
            ivBikes.setImageResource(R.drawable.ic_two_wheeler);
            ivBikes.setColorFilter(activeColor);
            tvBikes.setTextColor(activeColor);
            tvBikes.setTypeface(boldTypeface);
        } else if (activeTab == tabEmiCalculator) {
            ivEmiCalculator.setImageResource(R.drawable.ic_calculate);
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

        showActiveDot(activeTab);
    }

    private void showActiveDot(LinearLayout activeTab) {
        dotHome.setVisibility(activeTab == tabHome ? View.VISIBLE : View.INVISIBLE);
        dotBikes.setVisibility(activeTab == tabBikes ? View.VISIBLE : View.INVISIBLE);
        dotEmiCalculator.setVisibility(activeTab == tabEmiCalculator ? View.VISIBLE : View.INVISIBLE);
        dotOrders.setVisibility(activeTab == tabOrders ? View.VISIBLE : View.INVISIBLE);
        dotProfile.setVisibility(activeTab == tabProfile ? View.VISIBLE : View.INVISIBLE);

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

    private void setActiveChip(CardView activeChip) {
        // Reset all chips
        resetChip(chipAll, "All Bikes");
        resetChip(chipPetrol, "Petrol");
        resetChip(chipEBikes, "Electric");
        resetChip(chipSecondHand, "Pre-owned");

        // Set active chip
        TypedValue typedValue = new TypedValue();
        if (getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true)) {
            activeChip.setCardBackgroundColor(typedValue.data);
        }
        
        if (getTheme().resolveAttribute(R.attr.colorOnPrimary, typedValue, true)) {
            ((TextView) activeChip.getChildAt(0)).setTextColor(typedValue.data);
        }
        ((TextView) activeChip.getChildAt(0)).setTypeface(null, android.graphics.Typeface.BOLD);
    }

    private void resetChip(CardView chip, String text) {
        TypedValue typedValue = new TypedValue();
        int surfaceVariant = R.attr.colorSurfaceVariant;
        if (getTheme().resolveAttribute(surfaceVariant, typedValue, true)) {
            chip.setCardBackgroundColor(typedValue.data);
        }
        
        TextView tv = (TextView) chip.getChildAt(0);
        tv.setText(text);
        if (getTheme().resolveAttribute(R.attr.colorOnSurfaceVariant, typedValue, true)) {
            tv.setTextColor(typedValue.data);
        }
        tv.setTypeface(null, android.graphics.Typeface.NORMAL);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, CustomerHomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }
}