package com.example.motovista_deep;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.motovista_deep.api.ApiService;
import com.example.motovista_deep.api.RetrofitClient;
import com.example.motovista_deep.helpers.SharedPrefManager;
import com.example.motovista_deep.models.BikeModel;
import com.example.motovista_deep.models.GetBikeByIdResponse;
import com.example.motovista_deep.models.CustomerRequest;
import com.example.motovista_deep.models.RequestResponse;
import com.example.motovista_deep.models.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BikeDetailsScreenActivity extends AppCompatActivity {

    private static final String TAG = "BikeDetailsActivity";

    // Header views
    private ImageView btnBack;
    private TextView tvTitle;

    // Bottom buttons
    private TextView btnViewInvoice, btnOrderBike;

    // Header & Top Views
    private ImageView ivBikeImage;
    private TextView tvBikeTitle, tvBikePrice, tvBikeVariant, tvModelYear;
    private TextView tvEngineValue, tvMileageValue, tvFuelValue;

    // Row Views
    private View rowBrand, rowModel, rowVariant, rowYear, rowEngine, rowFuel, rowTrans, rowBraking;
    private View rowMileage, rowTank, rowWeight, rowSeat, rowClearance;

    // Bike data

    private String bikeMileage;
    private String bikeEngine;
    private String bikeFuel;
    private String bikeWeight;
    private String bikeSeat;
    private String bikeClearance;
    
    // Color Selection
    private String selectedColor = null;
    private android.view.View selectedColorView = null;

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

        setupClickListeners();

        if (bikeId != -1) {
            fetchBikeDetails(bikeId);
        } else {
            populateBasicData();
        }
    }

    private void getIntentData() {
        Intent intent = getIntent();

        // Get bike ID
        bikeId = intent.getIntExtra("BIKE_ID", -1);
        Log.d(TAG, "getIntentData: Received BIKE_ID = " + bikeId);

        // Get basic details from intent (as placeholder while loading)
        bikeName = intent.getStringExtra("BIKE_NAME");
        bikePrice = intent.getStringExtra("BIKE_PRICE");
        bikeVariant = intent.getStringExtra("BIKE_VARIANT");
        bikeYear = intent.getStringExtra("BIKE_YEAR");
        bikeBrand = intent.getStringExtra("BIKE_BRAND");
        bikeModelName = intent.getStringExtra("BIKE_MODEL");
        bikeImage = intent.getStringExtra("BIKE_IMAGE");
        
        Log.d(TAG, "getIntentData: Name=" + bikeName + ", Image=" + bikeImage);
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

        // Specification values (Quick Stats)
        tvEngineValue = findViewById(R.id.tvEngineValue);
        tvMileageValue = findViewById(R.id.tvMileageValue);
        tvFuelValue = findViewById(R.id.tvFuelValue);

        // Basic details rows
        rowBrand = findViewById(R.id.rowBrand);
        rowModel = findViewById(R.id.rowModel);
        rowVariant = findViewById(R.id.rowVariant);
        rowYear = findViewById(R.id.rowYear);
        rowEngine = findViewById(R.id.rowEngine);
        rowFuel = findViewById(R.id.rowFuel);
        rowTrans = findViewById(R.id.rowTrans);
        rowBraking = findViewById(R.id.rowBraking);

        // Spec rows
        rowMileage = findViewById(R.id.rowMileage);
        rowTank = findViewById(R.id.rowTank);
        rowWeight = findViewById(R.id.rowWeight);
        rowSeat = findViewById(R.id.rowSeat);
        rowClearance = findViewById(R.id.rowClearance);
        
        // Initialize Labels
        setRowLabel(rowBrand, "Brand");
        setRowLabel(rowModel, "Model Name");
        setRowLabel(rowVariant, "Variant");
        setRowLabel(rowYear, "Model Year");
        setRowLabel(rowEngine, "Engine CC");
        setRowLabel(rowFuel, "Fuel Type");
        setRowLabel(rowTrans, "Transmission");
        setRowLabel(rowBraking, "Braking Type");
        
        setRowLabel(rowMileage, "Mileage");
        setRowLabel(rowTank, "Fuel Tank Capacity");
        setRowLabel(rowWeight, "Kerb Weight");
        setRowLabel(rowSeat, "Seat Height");
        setRowLabel(rowClearance, "Ground Clearance");
    }

    private void setRowLabel(View row, String label) {
        if (row != null) {
            TextView tvLabel = row.findViewById(R.id.tvLabel);
            if (tvLabel != null) tvLabel.setText(label);
        }
    }
    
    private void setRowValue(View row, String value) {
        if (row != null) {
            TextView tvValue = row.findViewById(R.id.tvValue);
            if (tvValue != null) tvValue.setText(value != null ? value : "-");
        }
    }

    private void fetchBikeDetails(int id) {
        String token = SharedPrefManager.getInstance(this).getToken();
        Log.d(TAG, "Fetching details for ID: " + id + ", Token available: " + (token != null));
        
        if (token == null) {
            Log.e(TAG, "Token is null, aborting fetch");
            populateBasicData();
            return;
        }

        com.example.motovista_deep.api.ApiService apiService = RetrofitClient.getApiService();
        Call<GetBikeByIdResponse> call = apiService.getBikeById("Bearer " + token, id);
        
        // Log the URL being called (for debugging)
        Log.d(TAG, "Request URL: " + call.request().url());


        call.enqueue(new Callback<GetBikeByIdResponse>() {
            @Override
            public void onResponse(Call<GetBikeByIdResponse> call, Response<GetBikeByIdResponse> response) {
                Log.d(TAG, "API Response Code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    GetBikeByIdResponse bikeResponse = response.body();
                    Log.d(TAG, "API Status: " + bikeResponse.getStatus());
                    
                    if ("success".equals(bikeResponse.getStatus()) && bikeResponse.getData() != null) {
                        Log.d(TAG, "Bike data found: " + bikeResponse.getData().getModel());
                        currentBike = bikeResponse.getData();
                        populateFullData(currentBike);
                    } else {
                        Log.e(TAG, "Bike details not found in API response");
                        Toast.makeText(BikeDetailsScreenActivity.this, "Details not found", Toast.LENGTH_SHORT).show();
                        populateBasicData();
                    }
                } else {
                    Log.e(TAG, "Fetch failed: " + response.code() + ", " + response.message());
                    populateBasicData();
                }
            }

            @Override
            public void onFailure(Call<GetBikeByIdResponse> call, Throwable t) {
                Log.e(TAG, "Network error fetching details", t);
                populateBasicData();
            }
        });
    }

    private void populateBasicData() {
        tvBikeTitle.setText(bikeName != null ? bikeName : "");
        tvBikePrice.setText(bikePrice != null ? bikePrice : "");
        tvBikeVariant.setText((bikeVariant != null ? bikeVariant : "") + " Variant");
        tvModelYear.setText((bikeYear != null ? bikeYear : "") + " Model");

        setRowValue(rowBrand, bikeBrand);
        setRowValue(rowModel, bikeModelName);
        setRowValue(rowVariant, bikeVariant);
        setRowValue(rowYear, bikeYear);

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

        // Quick Stats
        tvEngineValue.setText(bike.getEngineCC() != null ? bike.getEngineCC() : "-");
        tvMileageValue.setText(bike.getMileage() != null ? bike.getMileage() : "-");
        tvFuelValue.setText(bike.getFuelType() != null ? bike.getFuelType() : "-");

        // Basic details
        setRowValue(rowBrand, bike.getBrand());
        setRowValue(rowModel, bike.getModel());
        setRowValue(rowVariant, bike.getVariant());
        setRowValue(rowYear, bike.getYear());
        setRowValue(rowEngine, bike.getEngineCC());
        setRowValue(rowFuel, bike.getFuelType());
        setRowValue(rowTrans, bike.getTransmission());
        setRowValue(rowBraking, bike.getBrakingType());

        // Specs
        setRowValue(rowMileage, bike.getMileage());
        setRowValue(rowTank, bike.getFuelTankCapacity());
        setRowValue(rowWeight, bike.getKerbWeight());
        setRowValue(rowSeat, bike.getSeatHeight());
        setRowValue(rowClearance, bike.getGroundClearance());

        // Image
        String imageUrl = bike.getImageUrl();
        if ((imageUrl == null || imageUrl.isEmpty()) && bike.getAllImages() != null && !bike.getAllImages().isEmpty()) {
            imageUrl = bike.getAllImages().get(0);
        }
        
        if (imageUrl != null && !imageUrl.isEmpty()) {
            loadImage(imageUrl);
        }

    // Populate Colors
        android.widget.LinearLayout layoutColors = findViewById(R.id.layoutColors);
        android.widget.TextView tvSelectedColorName = findViewById(R.id.tvSelectedColorName);
        layoutColors.removeAllViews();
        
        if (bike.getColors() != null) {
            for (String colorName : bike.getColors()) {
                // Container (The Ring)
                android.widget.FrameLayout frame = new android.widget.FrameLayout(this);
                int size = (int)(48 * getResources().getDisplayMetrics().density); // 48dp container
                int margin = (int)(12 * getResources().getDisplayMetrics().density);
                
                android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(size, size);
                params.setMargins(0, 0, margin, 0);
                frame.setLayoutParams(params);
                
                // Color Dot (The Inner Circle)
                View colorDot = new View(this);
                // Unselected size: 32dp
                int dotSize = (int)(32 * getResources().getDisplayMetrics().density);
                android.widget.FrameLayout.LayoutParams dotParams = new android.widget.FrameLayout.LayoutParams(dotSize, dotSize);
                dotParams.gravity = android.view.Gravity.CENTER;
                colorDot.setLayoutParams(dotParams);
                colorDot.setBackgroundResource(R.drawable.circle_dot);
                
                int colorValue = getColorFromName(colorName);
                colorDot.setBackgroundTintList(android.content.res.ColorStateList.valueOf(colorValue));
                
                frame.addView(colorDot);
                
                final String finalColorName = colorName;
                
                // Restore state if previously selected (though populate usually runs once, logic helps if re-run)
                if (selectedColor != null && selectedColor.equals(finalColorName)) {
                     // Selected State
                     frame.setBackgroundResource(R.drawable.ring_selection);
                     frame.setBackgroundTintList(android.content.res.ColorStateList.valueOf(colorValue));
                     
                     // Smaller dot when selected? Design says "gap". 
                     // If Container is 48dp and Dot is 32dp, there is (48-32)/2 = 8dp gap/ring thickness combined.
                     // Ring stroke is 2dp. So 6dp gap. Looks good.
                     // Don't need to change dot size necessarily, but could make it slightly smaller like 24dp if needed.
                     // Let's keep 32dp for now, 8dp padding around is plenty for gap + ring.
                     
                     selectedColorView = frame; // Store the FRAME as the selected view handle
                     tvSelectedColorName.setText(finalColorName);
                     tvSelectedColorName.setTextColor(android.graphics.Color.BLACK);
                }

                frame.setOnClickListener(v -> {
                    if (selectedColor != null && selectedColor.equals(finalColorName)) {
                        // Deselect
                        selectedColor = null;
                        
                        // Reset Visuals
                        frame.setBackground(null); // Remove ring
                        
                        // Can reset dot size if we changed it, but we didn't.
                        
                        selectedColorView = null;
                        
                        tvSelectedColorName.setText("Select a color");
                        tvSelectedColorName.setTextColor(getResources().getColor(R.color.gray_500));
                        
                        // Toast.makeText(BikeDetailsScreenActivity.this, "Color deselected", Toast.LENGTH_SHORT).show();
                    } else {
                        // Select New
                        
                        // 1. Reset Previous
                        if (selectedColorView != null) {
                            selectedColorView.setBackground(null); // Remove ring from old
                        }
                        
                        // 2. Set New
                        selectedColor = finalColorName;
                        selectedColorView = frame;
                        
                        // Apply Ring
                        frame.setBackgroundResource(R.drawable.ring_selection);
                        frame.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getColorFromName(finalColorName)));
                        
                        // Update Text
                        tvSelectedColorName.setText(finalColorName);
                         // Make text black or dark
                        tvSelectedColorName.setTextColor(android.graphics.Color.BLACK);
                        
                        // Toast.makeText(BikeDetailsScreenActivity.this, "Selected: " + finalColorName, Toast.LENGTH_SHORT).show();
                    }
                });
                
                layoutColors.addView(frame);
            }
        }
    }

    private int getColorFromName(String colorName) {
        if (colorName == null) return android.graphics.Color.GRAY;
        String lower = colorName.toLowerCase().trim();
        if (lower.contains("blue")) return 0xFF2563EB;
        if (lower.contains("black")) return 0xFF0F172A;
        if (lower.contains("red")) return 0xFFDC2626;
        if (lower.contains("silver") || lower.contains("grey") || lower.contains("gray")) return 0xFF9CA3AF;
        if (lower.contains("white")) return 0xFFFFFFFF;
        if (lower.contains("green")) return 0xFF16A34A;
        if (lower.contains("orange")) return 0xFFEA580C;
        if (lower.contains("yellow")) return 0xFFCA8A04;
        return android.graphics.Color.GRAY; // Default
    }

    private void loadImage(String imageUrl) {
        // Construct full URL
        String baseUrl = RetrofitClient.BASE_URL; // e.g. http://.../api/
        if (baseUrl.endsWith("api/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 4); // Remove "api/" -> http://.../
        }
        
        if (!imageUrl.startsWith("http")) {
            // If path doesn't start with uploads/, prepend it
            if (!imageUrl.startsWith("uploads/")) {
                if (imageUrl.startsWith("bikes/") || imageUrl.startsWith("second_hand_bikes/")) {
                   imageUrl = "uploads/" + imageUrl;
                } else {
                   imageUrl = "uploads/bikes/" + imageUrl;
                }
            }
             // Ensure no double slash if baseUrl ends with / and imageUrl starts with /
             if (baseUrl.endsWith("/") && imageUrl.startsWith("/")) {
                 imageUrl = imageUrl.substring(1);
             }
             
            imageUrl = baseUrl + imageUrl;
        }

        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_bike)
                .error(R.drawable.placeholder_bike)
                // Use fitCenter or centerCrop based on preference, CenterCrop fills 4:3
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
        // Safe check for title
        String title = currentBike != null ? (currentBike.getBrand() + " " + currentBike.getModel()) : bikeName;
        Toast.makeText(this, "Viewing invoice sample for " + title, Toast.LENGTH_SHORT).show();
        // Intent invoiceIntent = new Intent(this, InvoiceSampleActivity.class);
        // invoiceIntent.putExtra("BIKE_NAME", title);
        // startActivity(invoiceIntent);
    }



    private void placeOrder() {
        if (selectedColor == null) {
            Toast.makeText(this, "Please select the colour", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if user is logged in
        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
             Toast.makeText(this, "Please login to place an order", Toast.LENGTH_LONG).show();
             startActivity(new Intent(this, CustomerLoginActivity.class));
             return;
        }
        
        User user = SharedPrefManager.getInstance(this).getCustomer();
        if (user == null) {
             Toast.makeText(this, "User data error. Please re-login.", Toast.LENGTH_SHORT).show();
             return;
        }

        // Prepare Data
        String name = currentBike != null ? (currentBike.getBrand() + " " + currentBike.getModel()) : bikeName;
        String price = currentBike != null ? (currentBike.getOnRoadPrice() != null ? currentBike.getOnRoadPrice() : currentBike.getPrice()) : bikePrice;
        String variant = currentBike != null ? currentBike.getVariant() : bikeVariant;
        String brand = currentBike != null ? currentBike.getBrand() : bikeBrand;
        String model = currentBike != null ? currentBike.getModel() : bikeModelName;
        String year = currentBike != null ? currentBike.getYear() : bikeYear;
        
        // Disable button to prevent double clicks
        btnOrderBike.setEnabled(false);
        btnOrderBike.setText("Processing...");

        // Create Request Object
        CustomerRequest request = new CustomerRequest(
            user.getId(),
            user.getFull_name(),
            user.getPhone(),
            user.getProfile_image(), // Optional if backend supports it
            bikeId != -1 ? bikeId : 0, // Fallback ID
            name,
            variant,
            selectedColor,
            price
        );
        
        // Call API
        ApiService apiService = RetrofitClient.getApiService();
        Call<RequestResponse> call = apiService.addCustomerRequest(request);
        
        call.enqueue(new Callback<RequestResponse>() {
            @Override
            public void onResponse(Call<RequestResponse> call, Response<RequestResponse> response) {
                btnOrderBike.setEnabled(true);
                btnOrderBike.setText("ORDER NOW");

                if (response.isSuccessful() && response.body() != null) {
                    RequestResponse res = response.body();
                    if (res.isSuccess()) {
                         // Success - Go to Request Sent Screen
                        Intent requestSentIntent = new Intent(BikeDetailsScreenActivity.this, RequestSentActivity.class);
                        requestSentIntent.putExtra("BIKE_NAME", name);
                        requestSentIntent.putExtra("BIKE_PRICE", price);
                        requestSentIntent.putExtra("BIKE_VARIANT", variant);
                        requestSentIntent.putExtra("BIKE_Color", selectedColor); 
                        requestSentIntent.putExtra("BIKE_BRAND", brand);
                        requestSentIntent.putExtra("BIKE_MODEL", model);
                        requestSentIntent.putExtra("BIKE_YEAR", year);
                        
                        // Use Order ID from Backend if valid
                        if (res.getOrderId() != null) {
                            requestSentIntent.putExtra("ORDER_ID", res.getOrderId());
                        }

                        startActivity(requestSentIntent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish(); // Optional: Close details screen? Maybe not.
                    } else {
                        Toast.makeText(BikeDetailsScreenActivity.this, "Failed: " + res.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(BikeDetailsScreenActivity.this, "Server Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RequestResponse> call, Throwable t) {
                btnOrderBike.setEnabled(true);
                btnOrderBike.setText("ORDER NOW");
                Toast.makeText(BikeDetailsScreenActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}