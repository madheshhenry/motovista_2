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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderSummaryActivity extends AppCompatActivity {

    // Views
    private ImageView btnBack;
    private TextView tvStatus, tvDate;
    private TextView tvCustomerName, tvCustomerPhone;
    private ImageView ivCustomerProfile, btnCallCustomer, btnChatCustomer;
    
    private TextView tvBrand, tvBikeName, tvEdition, tvBikeColorName;
    private ImageView ivBikeImage;
    private LinearLayout statusBadgeContainer;
    
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
        
        if (requestId != -1) {
            fetchOrderDetails(requestId);
        } else {
            Toast.makeText(this, "Invalid Request ID", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        
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
        
        // Date (if available in model/intent - currently using dummy placeholder if not in OrderSummaryData)
        // If OrderSummaryData has created_at, use it. Otherwise rely on Intent or Today.
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
            tvBikeColorName.setBackgroundColor(Color.parseColor(hexCode));
            
            // Adjust Text Color based on background brightness
            int color = Color.parseColor(hexCode);
            double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
            if (darkness < 0.5) {
                tvBikeColorName.setTextColor(Color.BLACK); // Light background
            } else {
                tvBikeColorName.setTextColor(Color.WHITE); // Dark background
            }
            
        } catch (Exception e) {
            tvBikeColorName.setBackgroundColor(Color.BLACK);
            tvBikeColorName.setTextColor(Color.WHITE);
        }
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
        
        if (status.equals("approved") || status.equals("accepted") || status.equals("completed")) {
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

    // Helper to load images (Simplified for brevity, similar to previous implementation)
    private void loadProfileImage(String path) {
        if (path == null || path.isEmpty()) return;
        Glide.with(this).load(constructUrl(path)).placeholder(R.drawable.sample_profile).into(ivCustomerProfile);
    }
    
    private void loadBikeImage(String paths) {
        if (paths == null || paths.isEmpty()) return;
        String path = paths.split(",")[0]; // First image
        Glide.with(this).load(constructUrl(path)).placeholder(R.drawable.featured_bike_1).into(ivBikeImage);
    }
    
    private String constructUrl(String itemPath) {
        if (itemPath == null || itemPath.isEmpty()) return "";

        // Clean path (remove JSON artifacts if any)
        itemPath = itemPath.replace("\"", "").replace("\\", "").replace("[", "").replace("]", "").trim();

        // If full URL, return as is
        if (itemPath.startsWith("http")) return itemPath;

        // Base logic
        // Current Base URL for API is likely http://192.168.0.102/motovista_backend/api/
        // We need http://192.168.0.102/motovista_backend/
        String baseUrl = RetrofitClient.BASE_URL; // e.g., .../api/
        String serverBase = baseUrl;
        if (serverBase.endsWith("api/")) {
            serverBase = serverBase.replace("api/", "");
        }

        // Ensure path starts with uploads/ if not present and not absolute
        if (!itemPath.startsWith("uploads/") && !itemPath.startsWith("/")) {
             // Heuristic/Default? Or just append?
             // Assuming paths might be just filename or relative
        }

        // Avoid double slashes or missing slashes
        if (serverBase.endsWith("/") && itemPath.startsWith("/")) {
            itemPath = itemPath.substring(1);
        } else if (!serverBase.endsWith("/") && !itemPath.startsWith("/")) {
            serverBase += "/";
        }
        
        // Handle "uploads/" duplication if path already has it
        // If path is "uploads/foo.jpg" and logic adds "uploads/", check first
        // But here we are just concatenating serverBase + itemPath
        
        return serverBase + itemPath;
    }
}