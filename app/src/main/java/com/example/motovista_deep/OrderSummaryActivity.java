package com.example.motovista_deep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class OrderSummaryActivity extends AppCompatActivity {

    // Views
    private TextView tvCustomerName, tvCustomerPhone, tvStatus;
    private View statusDot;
    private android.widget.LinearLayout statusBadgeLayout;
    
    // Bike Spec Views
    private TextView tvBrand, tvBikeName, tvEdition;
    private TextView tvEngineNumber, tvChassisNumber;
    private TextView tvEngineCC, tvFuelType, tvMileage, tvFuelCapacity;
    private TextView tvKerbWeight, tvSeatHeight, tvGroundClearance;
    private TextView tvPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_summary);

        // Initialize Views
        CardView btnBack = findViewById(R.id.btnBack);
        CardView btnNext = findViewById(R.id.btnNext);
        tvCustomerName = findViewById(R.id.tvCustomerName);
        tvCustomerPhone = findViewById(R.id.tvCustomerPhone);
        tvStatus = findViewById(R.id.tvStatus);
        statusDot = findViewById(R.id.statusDot);
        statusBadgeLayout = findViewById(R.id.statusBadgeLayout);
        
        // Bike Views
        tvBrand = findViewById(R.id.tvBrand);
        tvBikeName = findViewById(R.id.tvBikeName);
        tvEdition = findViewById(R.id.tvEdition);
        tvEngineNumber = findViewById(R.id.tvEngineNumber);
        tvChassisNumber = findViewById(R.id.tvChassisNumber);
        tvEngineCC = findViewById(R.id.tvEngineCC);
        tvFuelType = findViewById(R.id.tvFuelType);
        tvMileage = findViewById(R.id.tvMileage);
        tvFuelCapacity = findViewById(R.id.tvFuelCapacity);
        tvKerbWeight = findViewById(R.id.tvKerbWeight);
        tvSeatHeight = findViewById(R.id.tvSeatHeight);
        tvGroundClearance = findViewById(R.id.tvGroundClearance);
        tvPrice = findViewById(R.id.tvPrice);

        // Get Data from Intent
        int requestId = getIntent().getIntExtra("request_id", -1);
        String customerName = getIntent().getStringExtra("customer_name");
        String customerPhone = getIntent().getStringExtra("customer_phone");
        String status = getIntent().getStringExtra("status");

        // Set Initial Customer Data (Fallback/Placeholder)
        if (customerName != null) tvCustomerName.setText(customerName);
        if (customerPhone != null) tvCustomerPhone.setText(customerPhone);

        // Handle Status Logic
        updateStatusUI(status, btnNext);

        // Fetch Full Details
        if (requestId != -1) {
            fetchOrderDetails(requestId);
        } else {
             Toast.makeText(this, "Invalid Request ID", Toast.LENGTH_SHORT).show();
        }

        // Back button
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // NEXT BUTTON
        if (btnNext != null) {
            btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(OrderSummaryActivity.this, PaymentTypeActivity.class);
                        intent.putExtra("request_id", requestId); // Pass ID
                        intent.putExtra("customer_name", customerName);
                        intent.putExtra("customer_phone", customerPhone);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    } catch (Exception e) {
                        Toast.makeText(OrderSummaryActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void updateStatusUI(String status, View btnNext) {
        if (status != null) {
            String lowerStatus = status.toLowerCase();
            if (lowerStatus.contains("reject") || lowerStatus.contains("completed")) {
                tvStatus.setText(lowerStatus.contains("reject") ? "Rejected" : "Completed");
                tvStatus.setTextColor(getResources().getColor(lowerStatus.contains("reject") ? R.color.red_600 : R.color.icon_green));
                statusDot.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(lowerStatus.contains("reject") ? R.color.red_600 : R.color.icon_green)));
                btnNext.setVisibility(View.GONE);
            } else {
                tvStatus.setText("Approved");
                tvStatus.setTextColor(getResources().getColor(R.color.icon_green));
                statusDot.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.icon_green)));
                btnNext.setVisibility(View.VISIBLE);
            }
        } else {
            btnNext.setVisibility(View.VISIBLE);
        }
    }
    
    private void fetchOrderDetails(int requestId) {
        com.example.motovista_deep.api.ApiService apiService = com.example.motovista_deep.api.RetrofitClient.getApiService();
        retrofit2.Call<com.example.motovista_deep.models.GetOrderSummaryResponse> call = apiService.getOrderSummary(requestId);
        
        call.enqueue(new retrofit2.Callback<com.example.motovista_deep.models.GetOrderSummaryResponse>() {
            @Override
            public void onResponse(retrofit2.Call<com.example.motovista_deep.models.GetOrderSummaryResponse> call, retrofit2.Response<com.example.motovista_deep.models.GetOrderSummaryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    com.example.motovista_deep.models.GetOrderSummaryResponse res = response.body();
                    if (res.isSuccess() && res.getData() != null) {
                        populateData(res.getData());
                    } else {
                        Toast.makeText(OrderSummaryActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.example.motovista_deep.models.GetOrderSummaryResponse> call, Throwable t) {
                Toast.makeText(OrderSummaryActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void populateData(com.example.motovista_deep.models.OrderSummaryData data) {
        // Customer
        tvCustomerName.setText(data.getCustomerName());
        tvCustomerPhone.setText(data.getCustomerPhone());

        // Load Customer Profile
        String profilePath = data.getCustomerProfile();
        if (profilePath != null && !profilePath.isEmpty()) {
            // Handle profile URL
            String finalProfileUrl;
            
            // Clean the path first using the helper (removes quotes, etc.)
            profilePath = profilePath.replace("\"", "").replace("\\", "").replace("[", "").replace("]", "").trim();
            
            if (profilePath.startsWith("http")) {
                finalProfileUrl = profilePath;
            } else {
                // Base URL
                String baseUrl = com.example.motovista_deep.api.RetrofitClient.BASE_URL;
                String serverBase = baseUrl;
                if (serverBase.endsWith("api/")) {
                    serverBase = serverBase.replace("api/", "");
                }
                
                // Append uploads/profile_pics/ if missing
                if (!profilePath.contains("profile_pics") && !profilePath.startsWith("uploads/")) {
                    profilePath = "uploads/profile_pics/" + profilePath;
                } else if (profilePath.startsWith("profile_pics/") && !profilePath.startsWith("uploads/")) {
                     profilePath = "uploads/" + profilePath;
                }
                
                // Concat
                 if (serverBase.endsWith("/") && profilePath.startsWith("/")) {
                    profilePath = profilePath.substring(1);
                } else if (!serverBase.endsWith("/") && !profilePath.startsWith("/")) {
                    serverBase += "/";
                }
                finalProfileUrl = serverBase + profilePath;
            }
            
             android.widget.ImageView ivCustomerProfile = findViewById(R.id.ivCustomerProfile);
             if (ivCustomerProfile != null) {
                  com.bumptech.glide.Glide.with(this)
                        .load(finalProfileUrl)
                        .placeholder(R.drawable.sample_profile) 
                        .error(R.drawable.sample_profile)
                        .centerCrop()
                        .into(ivCustomerProfile);
             }
        }
        
        // Bike
        if (tvBrand != null) tvBrand.setText(data.getBrand());
        if (tvBikeName != null) tvBikeName.setText(data.getBikeName());
        if (tvEdition != null) tvEdition.setText(data.getBikeVariant() != null ? data.getBikeVariant() : "Standard");
        
        // Image Loading
        String imagePathsStr = data.getImagePaths();
        if (imagePathsStr != null && !imagePathsStr.isEmpty()) {
            String[] paths = imagePathsStr.split(",");
            if (paths.length > 0) {
                String rawPath = paths[0].trim();
                String finalUrl = constructImageUrl(rawPath);
                
                android.widget.ImageView ivBikeImage = findViewById(R.id.ivBikeImage);
                if (ivBikeImage != null) {
                    com.bumptech.glide.Glide.with(this)
                        .load(finalUrl)
                        .placeholder(R.drawable.featured_bike_1)
                        .error(R.drawable.featured_bike_1)
                        .centerCrop()
                        .into(ivBikeImage);
                }
            }
        }

        // Specs - Specs might be null in DB, handle gracefully
        if (tvEngineCC != null) tvEngineCC.setText(data.getEngineCc() != null ? data.getEngineCc() : "--");
        if (tvFuelType != null) tvFuelType.setText(data.getFuelType() != null ? data.getFuelType() : "--");
        if (tvMileage != null) tvMileage.setText(data.getMileage() != null ? data.getMileage() : "--");
        if (tvFuelCapacity != null) tvFuelCapacity.setText(data.getFuelTankCapacity() != null ? data.getFuelTankCapacity() : "--");
        if (tvKerbWeight != null) tvKerbWeight.setText(data.getKerbWeight() != null ? data.getKerbWeight() : "--");
        if (tvSeatHeight != null) tvSeatHeight.setText(data.getSeatHeight() != null ? data.getSeatHeight() : "--");
        if (tvGroundClearance != null) tvGroundClearance.setText(data.getGroundClearance() != null ? data.getGroundClearance() : "--");

        // Numbers
        if (tvEngineNumber != null) tvEngineNumber.setText(data.getEngineNumber() != null ? data.getEngineNumber() : "N/A");
        if (tvChassisNumber != null) tvChassisNumber.setText(data.getChassisNumber() != null ? data.getChassisNumber() : "N/A");

        // Price
        if (tvPrice != null) tvPrice.setText("₹ " + data.getOnRoadPrice());
        
        // Color
        updateColorUI(data.getBikeColor(), data.getColors());
    }

    private String constructImageUrl(String itemPath) {
        if (itemPath == null || itemPath.isEmpty()) return "";

        // Clean path (remove JSON artifacts if any)
        itemPath = itemPath.replace("\"", "").replace("\\", "").replace("[", "").replace("]", "").trim();

        // If full URL, return as is
        if (itemPath.startsWith("http")) return itemPath;

        // Base logic
        // Current Base URL for API is likely http://192.168.0.102/motovista_backend/api/
        // We need http://192.168.0.102/motovista_backend/
        String baseUrl = com.example.motovista_deep.api.RetrofitClient.BASE_URL; // e.g., .../api/
        String serverBase = baseUrl;
        if (serverBase.endsWith("api/")) {
            serverBase = serverBase.replace("api/", "");
        }

        // Ensure path starts with uploads/ if not present and not absolute
        if (!itemPath.startsWith("uploads/") && !itemPath.startsWith("/")) {
             // Heuristic/Default
        }

        // Avoid double slashes
        if (serverBase.endsWith("/") && itemPath.startsWith("/")) {
            itemPath = itemPath.substring(1);
        } else if (!serverBase.endsWith("/") && !itemPath.startsWith("/")) {
            serverBase += "/";
        }

        return serverBase + itemPath;
    }
    
    private void updateColorUI(String colorName, String colorsJson) {
        TextView tvBikeColorName = findViewById(R.id.tvBikeColorName);
        View viewColorChip = findViewById(R.id.viewColorChip);
        
        if (tvBikeColorName == null || viewColorChip == null) return;
        
        if (colorName != null && !colorName.isEmpty()) {
            String displayName = colorName;
            String hexCode = "#000000"; // Default
            boolean foundHex = false;

            // Check if colorName itself contains the hex (Format: "Name|#Hex")
            if (colorName.contains("|")) {
                String[] parts = colorName.split("\\|");
                if (parts.length >= 2) {
                    displayName = parts[0].trim();
                    hexCode = parts[1].trim();
                    foundHex = true;
                }
            }
            
            tvBikeColorName.setText(displayName);
            
            // If hex not found in name, try JSON lookup
            if (!foundHex && colorsJson != null && !colorsJson.isEmpty()) {
                try {
                    org.json.JSONArray jsonArray = new org.json.JSONArray(colorsJson);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        // Handle "Name|Hex" format (String)
                        Object item = jsonArray.get(i);
                        if (item instanceof String) {
                            String colorStr = (String) item;
                            String[] parts = colorStr.split("\\|");
                            if (parts.length >= 2) {
                                String name = parts[0].trim();
                                if (name.equalsIgnoreCase(displayName)) {
                                    hexCode = parts[1].trim();
                                    break;
                                }
                            }
                        } else if (item instanceof org.json.JSONObject) {
                             // Handle Object format (fallback)
                            org.json.JSONObject colorObj = (org.json.JSONObject) item;
                            String cName = colorObj.optString("name", "");
                            if (cName.equalsIgnoreCase(displayName)) {
                                hexCode = colorObj.optString("hex", "#000000");
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
            try {
                viewColorChip.setBackgroundColor(android.graphics.Color.parseColor(hexCode));
            } catch (Exception e) {
                viewColorChip.setBackgroundColor(android.graphics.Color.BLACK);
            }
        } else {
             // Hide if no color info
             ((View)tvBikeColorName.getParent()).setVisibility(View.GONE);
        }
    }
}