package com.example.motovista_deep;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.motovista_deep.api.ApiService;
import com.example.motovista_deep.api.RetrofitClient;
import com.example.motovista_deep.helpers.SharedPrefManager;
import com.example.motovista_deep.models.BikeModel;
import com.example.motovista_deep.models.GetBikeByIdResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BikeDetailsScreenActivity extends AppCompatActivity {

    private static final String TAG = "BikeDetailsActivity";

    // Header views
    private ImageView btnBack;
    private TextView tvTitle;

    // Bottom buttons
    private Button btnViewInvoice, btnOrderBike;

    // Bike details views
    private ImageView ivBikeImage;
    private TextView tvBikeTitle, tvBikePrice, tvBikeVariant, tvModelYear;

    // Specification values
    private TextView tvEngineValue, tvMileageValue, tvFuelValue, tvGearValue;

    // Basic details
    private TextView tvBrandName, tvModelName, tvDetailVariant, tvDetailYear;
    private TextView tvDetailEngine, tvDetailFuel, tvTransmission, tvBraking;

    // Specifications
    private TextView tvSpecMileage, tvFuelCapacity, tvKerbWeight, tvSeatHeight, tvGroundClearance;

    // Bike data
    private int bikeId;
    private BikeModel currentBike;

    // Fallback data from intent
    private String bikeName, bikePrice, bikeImage;
    private String bikeVariant, bikeYear, bikeBrand, bikeModelName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_details_screen);

        // Initialize all views
        initializeViews();

        // Get data from intent
        getIntentData();

        // Setup click listeners
        setupClickListeners();

        // Fetch full details if ID is available
        if (bikeId != -1) {
            fetchBikeDetails(bikeId);
        } else {
            // Populate with basic data from intent if no ID (fallback)
            populateBasicData();
        }
    }

    private void getIntentData() {
        Intent intent = getIntent();

        // Get bike ID
        bikeId = intent.getIntExtra("BIKE_ID", -1);

        // Get basic details from intent (as placeholder while loading)
        bikeName = intent.getStringExtra("BIKE_NAME");
        bikePrice = intent.getStringExtra("BIKE_PRICE");
        bikeVariant = intent.getStringExtra("BIKE_VARIANT");
        bikeYear = intent.getStringExtra("BIKE_YEAR");
        bikeBrand = intent.getStringExtra("BIKE_BRAND");
        bikeModelName = intent.getStringExtra("BIKE_MODEL");
        bikeImage = intent.getStringExtra("BIKE_IMAGE");
    }

    private void initializeViews() {
        // Header
        btnBack = findViewById(R.id.btnBack);
        tvTitle = findViewById(R.id.tvTitle);

        // Bottom buttons
        btnViewInvoice = findViewById(R.id.btnViewInvoice);
        btnOrderBike = findViewById(R.id.btnOrderBike);

        // Top section
        ivBikeImage = findViewById(R.id.ivBikeImage);
        tvBikeTitle = findViewById(R.id.tvBikeTitle);
        tvBikePrice = findViewById(R.id.tvBikePrice);
        tvBikeVariant = findViewById(R.id.tvBikeVariant);
        tvModelYear = findViewById(R.id.tvModelYear);

        // Specification values
        tvEngineValue = findViewById(R.id.tvEngineValue);
        tvMileageValue = findViewById(R.id.tvMileageValue);
        tvFuelValue = findViewById(R.id.tvFuelValue);
        tvGearValue = findViewById(R.id.tvGearValue);

        // Basic details
        tvBrandName = findViewById(R.id.tvBrandName);
        tvModelName = findViewById(R.id.tvModelName);
        tvDetailVariant = findViewById(R.id.tvDetailVariant);
        tvDetailYear = findViewById(R.id.tvDetailYear);
        tvDetailEngine = findViewById(R.id.tvDetailEngine);
        tvDetailFuel = findViewById(R.id.tvDetailFuel);
        tvTransmission = findViewById(R.id.tvTransmission);
        tvBraking = findViewById(R.id.tvBraking);

        // Specifications
        tvSpecMileage = findViewById(R.id.tvSpecMileage);
        tvFuelCapacity = findViewById(R.id.tvFuelCapacity);
        tvKerbWeight = findViewById(R.id.tvKerbWeight);
        tvSeatHeight = findViewById(R.id.tvSeatHeight);
        tvGroundClearance = findViewById(R.id.tvGroundClearance);
    }

    private void fetchBikeDetails(int id) {
        String token = SharedPrefManager.getInstance(this).getToken();
        if (token == null) {
            populateBasicData();
            return;
        }

        com.example.motovista_deep.api.ApiService apiService = RetrofitClient.getApiService();
        Call<GetBikeByIdResponse> call = apiService.getBikeById("Bearer " + token, id);

        call.enqueue(new Callback<GetBikeByIdResponse>() {
            @Override
            public void onResponse(Call<GetBikeByIdResponse> call, Response<GetBikeByIdResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GetBikeByIdResponse bikeResponse = response.body();
                    if ("success".equals(bikeResponse.getStatus()) && bikeResponse.getData() != null) {
                        currentBike = bikeResponse.getData();
                        populateFullData(currentBike);
                    } else {
                        Toast.makeText(BikeDetailsScreenActivity.this, "Details not found", Toast.LENGTH_SHORT).show();
                        populateBasicData();
                    }
                } else {
                    Log.e(TAG, "Fetch failed: " + response.code());
                    populateBasicData();
                }
            }

            @Override
            public void onFailure(Call<GetBikeByIdResponse> call, Throwable t) {
                Log.e(TAG, "Network error", t);
                populateBasicData();
            }
        });
    }

    private void populateBasicData() {
        tvBikeTitle.setText(bikeName != null ? bikeName : "");
        tvBikePrice.setText(bikePrice != null ? bikePrice : "");
        tvBikeVariant.setText((bikeVariant != null ? bikeVariant : "") + " Variant");
        tvModelYear.setText((bikeYear != null ? bikeYear : "") + " Model");

        tvBrandName.setText(bikeBrand != null ? bikeBrand : "");
        tvModelName.setText(bikeModelName != null ? bikeModelName : "");
        tvDetailVariant.setText(bikeVariant != null ? bikeVariant : "");
        tvDetailYear.setText(bikeYear != null ? bikeYear : "");

        // Load image if available from intent
        if (bikeImage != null && !bikeImage.isEmpty()) {
            loadImage(bikeImage);
        }
    }

    private void populateFullData(BikeModel bike) {
        // Main details
        tvBikeTitle.setText(bike.getBrand() + " " + bike.getModel());
        
        String price = bike.getOnRoadPrice();
        if (price == null || price.isEmpty()) price = bike.getPrice();
        tvBikePrice.setText(price != null ? "₹ " + price : "Price on request");
        
        tvBikeVariant.setText(bike.getVariant() + " Variant");
        tvModelYear.setText(bike.getYear() + " Model");

        // Basic details
        tvBrandName.setText(bike.getBrand());
        tvModelName.setText(bike.getModel());
        tvDetailVariant.setText(bike.getVariant());
        tvDetailYear.setText(bike.getYear());
        
        tvDetailEngine.setText(bike.getEngineCC() != null ? bike.getEngineCC() : "-");
        tvDetailFuel.setText(bike.getFuelType() != null ? bike.getFuelType() : "-");
        tvTransmission.setText(bike.getTransmission() != null ? bike.getTransmission() : "-");
        tvBraking.setText(bike.getBrakingType() != null ? bike.getBrakingType() : "-");

        // Specs View
        tvEngineValue.setText(bike.getEngineCC() != null ? bike.getEngineCC() : "-");
        tvMileageValue.setText(bike.getMileage() != null ? bike.getMileage() : "-");
        tvFuelValue.setText(bike.getFuelType() != null ? bike.getFuelType() : "-");
        // Ensure getTransmission() exists or use another field
        tvGearValue.setText(bike.getTransmission() != null ? bike.getTransmission() : "-");

        // More Specs
        tvSpecMileage.setText(bike.getMileage() != null ? bike.getMileage() : "-");
        tvFuelCapacity.setText(bike.getFuelTankCapacity() != null ? bike.getFuelTankCapacity() : "-");
        tvKerbWeight.setText(bike.getKerbWeight() != null ? bike.getKerbWeight() : "-");
        tvSeatHeight.setText(bike.getSeatHeight() != null ? bike.getSeatHeight() : "-");
        tvGroundClearance.setText(bike.getGroundClearance() != null ? bike.getGroundClearance() : "-");

        // Image
        String imageUrl = bike.getImageUrl();
        // If not in main object, check images list
        if ((imageUrl == null || imageUrl.isEmpty()) && bike.getAllImages() != null && !bike.getAllImages().isEmpty()) {
            imageUrl = bike.getAllImages().get(0);
        }
        
        if (imageUrl != null && !imageUrl.isEmpty()) {
            loadImage(imageUrl);
        }
    }

    private void loadImage(String imageUrl) {
        // Construct full URL if needed (similar to Adapter logic)
        String baseUrl = RetrofitClient.BASE_URL;
        if (baseUrl != null && !baseUrl.endsWith("/")) baseUrl += "/";
        
        if (!imageUrl.startsWith("http")) {
            // Simple check to append base url if relative
             if (!imageUrl.contains("uploads/")) {
                if (imageUrl.startsWith("bikes/") || imageUrl.startsWith("second_hand_bikes/")) {
                    imageUrl = "uploads/" + imageUrl;
                } else {
                    imageUrl = "uploads/bikes/" + imageUrl;
                }
            }
            imageUrl = baseUrl + imageUrl;
        }

        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_bike)
                .error(R.drawable.placeholder_bike)
                .centerCrop()
                .into(ivBikeImage);
    }

    private void setupClickListeners() {
        // Back button
        btnBack.setOnClickListener(v -> onBackPressed());

        // View Invoice Sample button
        btnViewInvoice.setOnClickListener(v -> viewInvoiceSample());

        // Order Now button
        btnOrderBike.setOnClickListener(v -> placeOrder());
    }

    private void viewInvoiceSample() {
        Toast.makeText(this, "Viewing invoice sample for " + tvBikeTitle.getText().toString(), Toast.LENGTH_SHORT).show();
        // Intent invoiceIntent = new Intent(this, InvoiceSampleActivity.class);
        // invoiceIntent.putExtra("BIKE_NAME", tvBikeTitle.getText().toString());
        // startActivity(invoiceIntent);
    }

    private void placeOrder() {
        Intent requestSentIntent = new Intent(BikeDetailsScreenActivity.this, RequestSentActivity.class);

        // Pass bike details from current loaded data
        requestSentIntent.putExtra("BIKE_NAME", tvBikeTitle.getText().toString());
        requestSentIntent.putExtra("BIKE_PRICE", tvBikePrice.getText().toString());
        requestSentIntent.putExtra("BIKE_VARIANT", tvDetailVariant.getText().toString());
        requestSentIntent.putExtra("BIKE_BRAND", tvBrandName.getText().toString());
        requestSentIntent.putExtra("BIKE_MODEL", tvModelName.getText().toString());
        requestSentIntent.putExtra("BIKE_YEAR", tvDetailYear.getText().toString());

        // Generate order ID
        String orderId = "#ORD" + System.currentTimeMillis() % 1000000;
        requestSentIntent.putExtra("ORDER_ID", orderId);

        startActivity(requestSentIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}