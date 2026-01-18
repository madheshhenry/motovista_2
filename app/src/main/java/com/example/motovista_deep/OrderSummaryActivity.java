package com.example.motovista_deep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.motovista_deep.api.ApiService;
import com.example.motovista_deep.api.RetrofitClient;
import com.example.motovista_deep.models.CustomerRequest;
import com.example.motovista_deep.models.GetOrderSummaryResponse;
import com.example.motovista_deep.models.OrderSummaryData;
import com.example.motovista_deep.models.GenericResponse;
import com.example.motovista_deep.utils.ImageUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OrderSummaryActivity extends AppCompatActivity {

    // Views
    private ImageView btnBack;
    private TextView tvStatus, tvDate;
    private CardView cardOutOfStock; // New
    private TextView tvCustomerName, tvCustomerPhone;
    private ImageView ivCustomerProfile, btnCallCustomer, btnChatCustomer;
    
    private TextView tvBrand, tvBikeName, tvEdition, tvBikeColorName;
    private ImageView ivBikeImage;
    private LinearLayout statusBadgeContainer, llFittingsContainer;
    private CardView cardFittings;
    
    // Bottom Bar
    private LinearLayout layoutActionButtons;
    private Button btnAccept, btnReject, btnNext;

    private int requestId = -1;
    private String currentStatus = "pending";
    private String customerNameStr, customerPhoneStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_summary);

        initializeViews();
        handleIntentData();
        setupClickListeners();
        
        if (requestId == -1) {
            Toast.makeText(this, "Invalid Request ID", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (requestId != -1) {
            fetchOrderDetails(requestId);
        }
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        
        // Stock Warning
        cardOutOfStock = findViewById(R.id.cardOutOfStock);

        // Status Card
        tvStatus = findViewById(R.id.tvStatus);
        statusBadgeContainer = findViewById(R.id.statusBadgeContainer);
        tvDate = findViewById(R.id.tvDate);
        
        // Customer Card
        tvCustomerName = findViewById(R.id.tvCustomerName);
        tvCustomerPhone = findViewById(R.id.tvCustomerPhone);
        ivCustomerProfile = findViewById(R.id.ivCustomerProfile);
        btnCallCustomer = findViewById(R.id.btnCallCustomer);
        btnChatCustomer = findViewById(R.id.btnChatCustomer);
        
        // Bike Card
        tvBrand = findViewById(R.id.tvBrand);
        tvBikeName = findViewById(R.id.tvBikeName);
        tvEdition = findViewById(R.id.tvEdition);
        tvBikeColorName = findViewById(R.id.tvBikeColorName);
        ivBikeImage = findViewById(R.id.ivBikeImage);
        
        // Bottom Bar
        layoutActionButtons = findViewById(R.id.layoutActionButtons);
        btnAccept = findViewById(R.id.btnAccept);
        btnReject = findViewById(R.id.btnReject);
        btnNext = findViewById(R.id.btnNext);
        
        // Fittings
        cardFittings = findViewById(R.id.cardFittings);
        llFittingsContainer = findViewById(R.id.llFittingsContainer);
    }
    
    private void handleIntentData() {
        Intent intent = getIntent();
        requestId = intent.getIntExtra("request_id", -1);
        customerNameStr = intent.getStringExtra("customer_name");
        customerPhoneStr = intent.getStringExtra("customer_phone");
        currentStatus = intent.getStringExtra("status");
        
        updateUIBasedOnStatus(currentStatus);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
        
        btnAccept.setOnClickListener(v -> updateRequestStatus("approved"));
        btnReject.setOnClickListener(v -> updateRequestStatus("rejected"));
        
        btnNext.setOnClickListener(v -> {
            Intent intent = new Intent(OrderSummaryActivity.this, PaymentTypeActivity.class);
            intent.putExtra("request_id", requestId);
            intent.putExtra("customer_name", customerNameStr);
            intent.putExtra("customer_phone", customerPhoneStr);
            startActivity(intent);
        });
        
        // Call/Chat placeholders
        btnCallCustomer.setOnClickListener(v -> Toast.makeText(this, "Calling " + customerPhoneStr, Toast.LENGTH_SHORT).show());
        btnChatCustomer.setOnClickListener(v -> Toast.makeText(this, "Opening Chat...", Toast.LENGTH_SHORT).show());
    }

    private void fetchOrderDetails(int id) {
        ApiService apiService = RetrofitClient.getApiService();
        apiService.getOrderSummary(id).enqueue(new Callback<GetOrderSummaryResponse>() {
            @Override
            public void onResponse(Call<GetOrderSummaryResponse> call, Response<GetOrderSummaryResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    populateData(response.body().getData());
                }
            }

            @Override
            public void onFailure(Call<GetOrderSummaryResponse> call, Throwable t) {
                Toast.makeText(OrderSummaryActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateData(OrderSummaryData data) {
        // Customer
        tvCustomerName.setText(data.getCustomerName());
        tvCustomerPhone.setText(data.getCustomerPhone());
        
        // Load Profile Image
        loadProfileImage(data.getCustomerProfile());
        
        // Bike Info
        tvBrand.setText(data.getBrand());
        tvBikeName.setText(data.getBikeName());
        tvEdition.setText(data.getBikeVariant() != null ? data.getBikeVariant() : "Standard");
        
        // Color
        updateColorUI(data.getBikeColor());
        
        // Bike Image
        loadBikeImage(data.getImagePaths());
        
        // Bike Image
        loadBikeImage(data.getImagePaths());
        
        // Date
        setDate(data.getCreatedAt());
        
        // Fittings
        displaySelectedFittings(data.getSelectedFittings());
        
        // Stock Check Warning
        if (!data.isInStock() && "pending".equalsIgnoreCase(data.getStatus())) {
             cardOutOfStock.setVisibility(View.VISIBLE);
             // Optional: Disable Accept button?
             // btnAccept.setEnabled(false);
             // btnAccept.setAlpha(0.5f);
        } else {
             cardOutOfStock.setVisibility(View.GONE);
             // btnAccept.setEnabled(true);
             // btnAccept.setAlpha(1.0f);
        }
    }

    private void setDate(String dateString) {
        if (dateString == null) return;
        try {
            // Input: "2026-01-14 10:30:00" (SQL Format)
            SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = input.parse(dateString);
            
            // Output: "Received on 14 Jan 2026"
            SimpleDateFormat output = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            tvDate.setText("Received on " + output.format(date));
        } catch (Exception e) {
             tvDate.setText("Received on " + dateString);
        }
    }
    
    private void updateColorUI(String colorName) {
        if (colorName == null) return;
        
        String displayName = colorName;
        String hexCode = "#000000";

        if (colorName.contains("|")) {
             String[] parts = colorName.split("\\|");
             if (parts.length >= 2) {
                 displayName = parts[0];
                 hexCode = parts[1];
             }
        }
        
        tvBikeColorName.setText(displayName);
        
        try {
            int color = Color.parseColor(hexCode);
            
            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setCornerRadius(100f); // Fully rounded pill shape
            drawable.setColor(color);
            
            // Add border for light colors to make them visible against white
            if (isColorLight(color)) {
                drawable.setStroke(2, Color.parseColor("#E5E7EB")); // Light gray border
                tvBikeColorName.setTextColor(Color.BLACK);
            } else {
                tvBikeColorName.setTextColor(Color.WHITE);
            }
            
            tvBikeColorName.setBackground(drawable);
            
        } catch (Exception e) {
            tvBikeColorName.setBackgroundColor(Color.BLACK);
            tvBikeColorName.setTextColor(Color.WHITE);
        }
    }

    private boolean isColorLight(int color) {
        double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        return darkness < 0.5;
    }
    
    private void updateRequestStatus(String newStatus) {
        ApiService apiService = RetrofitClient.getApiService();
        // Create Request Object
        com.example.motovista_deep.models.UpdateRequestStatusRequest request = new com.example.motovista_deep.models.UpdateRequestStatusRequest(requestId, newStatus);
        
        // Use GenericResponse
        apiService.updateRequestStatus(request).enqueue(new Callback<com.example.motovista_deep.models.GenericResponse>() {
            @Override
            public void onResponse(Call<com.example.motovista_deep.models.GenericResponse> call, Response<com.example.motovista_deep.models.GenericResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    currentStatus = newStatus;
                    updateUIBasedOnStatus(newStatus);
                    Toast.makeText(OrderSummaryActivity.this, "Application " + newStatus, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(OrderSummaryActivity.this, "Failed to update", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<com.example.motovista_deep.models.GenericResponse> call, Throwable t) {
                Toast.makeText(OrderSummaryActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUIBasedOnStatus(String status) {
        if (status == null) status = "pending";
        status = status.toLowerCase();
        
        // Badge Colors
        int bgColor, textColor;
        String label;
        
        if (status.equals("completed")) {
            bgColor = Color.parseColor("#ecfdf5"); // Green-50
            textColor = Color.parseColor("#047857"); // Green-700
            label = "COMPLETED";
            
            // Hide All Buttons
            layoutActionButtons.setVisibility(View.GONE);
            btnNext.setVisibility(View.GONE);
            
        } else if (status.equals("approved") || status.equals("accepted")) {
            bgColor = Color.parseColor("#ecfdf5"); // Green-50
            textColor = Color.parseColor("#047857"); // Green-700
            label = "ACCEPTED";
            
            // Show Next Button, Hide Actions
            layoutActionButtons.setVisibility(View.GONE);
            btnNext.setVisibility(View.VISIBLE);
            
        } else if (status.equals("rejected")) {
            bgColor = Color.parseColor("#fef2f2"); // Red-50
            textColor = Color.parseColor("#b91c1c"); // Red-700
            label = "REJECTED";
            
            // Hide All Buttons
            layoutActionButtons.setVisibility(View.GONE);
            btnNext.setVisibility(View.GONE);
            
        } else {
            bgColor = Color.parseColor("#eff6ff"); // Blue-50
            textColor = Color.parseColor("#1d4ed8"); // Blue-700
            label = "NEW REQUEST";
            
            // Show Actions, Hide Next
            layoutActionButtons.setVisibility(View.VISIBLE);
            btnNext.setVisibility(View.GONE);
        }
        
        // Apply to Badge
        GradientDrawable bg = new GradientDrawable();
        bg.setShape(GradientDrawable.RECTANGLE);
        bg.setCornerRadius(100f);
        bg.setColor(bgColor);
        statusBadgeContainer.setBackground(bg);
        
        tvStatus.setText(label);
        tvStatus.setTextColor(textColor);
    }

    private void displaySelectedFittings(String json) {
        if (json == null || json.isEmpty() || json.equals("[]")) {
            cardFittings.setVisibility(View.GONE);
            return;
        }

        try {
            cardFittings.setVisibility(View.VISIBLE);
            llFittingsContainer.removeAllViews();

            com.google.gson.Gson gson = new com.google.gson.Gson();
            java.lang.reflect.Type listType = new com.google.gson.reflect.TypeToken<java.util.List<com.example.motovista_deep.models.CustomFitting>>() {}.getType();
            java.util.List<com.example.motovista_deep.models.CustomFitting> fittings = gson.fromJson(json, listType);

            if (fittings == null || fittings.isEmpty()) {
                cardFittings.setVisibility(View.GONE);
                return;
            }

            for (com.example.motovista_deep.models.CustomFitting fitting : fittings) {
                View itemView = getLayoutInflater().inflate(R.layout.item_fitting_summary, llFittingsContainer, false);
                TextView tvName = itemView.findViewById(R.id.tvFittingName);
                TextView tvPrice = itemView.findViewById(R.id.tvFittingPrice);

                tvName.setText(fitting.getName());
                
                if (fitting.isMandatory()) {
                    tvName.setText(fitting.getName() + " (Mandatory)");
                    tvPrice.setText("Included");
                    tvPrice.setTextColor(Color.parseColor("#16a34a")); // Green
                } else {
                    tvPrice.setText("₹ " + fitting.getPrice());
                    tvPrice.setTextColor(Color.parseColor("#111718"));
                }

                llFittingsContainer.addView(itemView);
            }
        } catch (Exception e) {
            cardFittings.setVisibility(View.GONE);
        }
    }

    // Helper to load images using centralized ImageUtils
    private void loadProfileImage(String path) {
        String imageUrl = ImageUtils.getFullImageUrl(path, ImageUtils.PATH_PROFILE_PICS);
        if (!imageUrl.isEmpty()) {
            Glide.with(this).load(imageUrl).placeholder(R.drawable.sample_profile).into(ivCustomerProfile);
        }
    }
    
    private void loadBikeImage(String paths) {
        if (paths == null || paths.isEmpty()) return;
        String path = paths.split(",")[0]; // First image
        String imageUrl = ImageUtils.getFullImageUrl(path, ImageUtils.PATH_BIKES);
        if (!imageUrl.isEmpty()) {
            Glide.with(this).load(imageUrl).placeholder(R.drawable.featured_bike_1).into(ivBikeImage);
        }
    }
    
}