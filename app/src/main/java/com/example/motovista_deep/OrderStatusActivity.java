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

public class OrderStatusActivity extends AppCompatActivity {

    // Header
    private ImageView btnBack;
    private TextView tvTitle;

    // Order Details
    private ImageView ivBike;
    private TextView tvBikeName, tvOrderId, tvOrderDate;

    // Action Buttons
    private Button btnConfirmDocuments, btnNeedHelp;

    // Bottom Navigation
    private LinearLayout tabHome, tabBikes, tabEmiCalculator, tabOrders, tabProfile;
    private ImageView ivHome, ivBikes, ivEmiCalculator, ivOrders, ivProfile;
    private TextView tvHome, tvBikes, tvEmiCalculator, tvOrders, tvProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        // Initialize views
        initializeViews();

        // Setup click listeners
        setupClickListeners();

        // Set active tab (Orders is active by default)
        setActiveTab(tabOrders);

        // Load order data (will be dynamic later)
        loadOrderData();
    }

    private void initializeViews() {
        // Header
        btnBack = findViewById(R.id.btnBack);
        tvTitle = findViewById(R.id.tvTitle);

        // Order Details
        ivBike = findViewById(R.id.ivBike);
        tvBikeName = findViewById(R.id.tvBikeName);
        tvOrderId = findViewById(R.id.tvOrderId);
        tvOrderDate = findViewById(R.id.tvOrderDate);

        // Action Buttons
        btnConfirmDocuments = findViewById(R.id.btnConfirmDocuments);
        btnNeedHelp = findViewById(R.id.btnNeedHelp);

        // Bottom Navigation
        tabHome = findViewById(R.id.tabHome);
        tabBikes = findViewById(R.id.tabBikes);
        tabEmiCalculator = findViewById(R.id.tabEmiCalculator);
        tabOrders = findViewById(R.id.tabOrders);
        tabProfile = findViewById(R.id.tabProfile);

        ivHome = findViewById(R.id.ivHome);
        ivBikes = findViewById(R.id.ivBikes);
        ivEmiCalculator = findViewById(R.id.ivEmiCalculator);
        ivOrders = findViewById(R.id.ivOrders);
        ivProfile = findViewById(R.id.ivProfile);

        tvHome = findViewById(R.id.tvHome);
        tvBikes = findViewById(R.id.tvBikes);
        tvEmiCalculator = findViewById(R.id.tvEmiCalculator);
        tvOrders = findViewById(R.id.tvOrders);
        tvProfile = findViewById(R.id.tvProfile);
    }

    private void setupClickListeners() {
        // Back button
        btnBack.setOnClickListener(v -> {
            finish();
        });

        // Confirm Documents button
        btnConfirmDocuments.setOnClickListener(v -> {
            Toast.makeText(this, "Documents confirmed successfully!", Toast.LENGTH_SHORT).show();
            // TODO: Update order status in backend
        });

        // Need Help button
        btnNeedHelp.setOnClickListener(v -> {
            Intent intent = new Intent(OrderStatusActivity.this, CustomerSupportActivity.class);
            // Pass order ID for context
            intent.putExtra("order_id", tvOrderId.getText().toString());
            startActivity(intent);
        });

        // Bottom Navigation
        // In OrderStatusActivity's setupClickListeners()
        tabHome.setOnClickListener(v -> {
            Intent intent = new Intent(OrderStatusActivity.this, CustomerHomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        tabBikes.setOnClickListener(v -> {
            Intent intent = new Intent(OrderStatusActivity.this, BikeCatalogActivity.class);
            startActivity(intent);
            finish();
        });

        tabEmiCalculator.setOnClickListener(v -> {
            Intent intent = new Intent(OrderStatusActivity.this, EmiCalculatorActivity.class);
            startActivity(intent);
            finish();
        });

        tabOrders.setOnClickListener(v -> {
            // Already on Orders screen, just update tab
            setActiveTab(tabOrders);
        });

        tabProfile.setOnClickListener(v -> {
            Intent intent = new Intent(OrderStatusActivity.this, CustomerProfileScreenActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void setActiveTab(LinearLayout activeTab) {
        resetAllTabs();

        if (activeTab == tabHome) {
            ivHome.setImageResource(R.drawable.ic_home_filled);
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
        } else if (activeTab == tabOrders) {
            ivOrders.setImageResource(R.drawable.ic_receipt_long_filled);
            ivOrders.setColorFilter(ContextCompat.getColor(this, R.color.primary_color));
            tvOrders.setTextColor(ContextCompat.getColor(this, R.color.primary_color));
            tvOrders.setTypeface(tvOrders.getTypeface(), android.graphics.Typeface.BOLD);
        } else if (activeTab == tabProfile) {
            ivProfile.setColorFilter(ContextCompat.getColor(this, R.color.primary_color));
            tvProfile.setTextColor(ContextCompat.getColor(this, R.color.primary_color));
            tvProfile.setTypeface(tvProfile.getTypeface(), android.graphics.Typeface.BOLD);
        }
    }

    private void resetAllTabs() {
        ivHome.setImageResource(R.drawable.ic_home);
        ivHome.setColorFilter(ContextCompat.getColor(this, R.color.gray_400));
        tvHome.setTextColor(ContextCompat.getColor(this, R.color.gray_400));
        tvHome.setTypeface(null, android.graphics.Typeface.NORMAL);

        ivBikes.setColorFilter(ContextCompat.getColor(this, R.color.gray_400));
        tvBikes.setTextColor(ContextCompat.getColor(this, R.color.gray_400));

        ivEmiCalculator.setColorFilter(ContextCompat.getColor(this, R.color.gray_400));
        tvEmiCalculator.setTextColor(ContextCompat.getColor(this, R.color.gray_400));

        ivOrders.setImageResource(R.drawable.ic_receipt_long);
        ivOrders.setColorFilter(ContextCompat.getColor(this, R.color.gray_400));
        tvOrders.setTextColor(ContextCompat.getColor(this, R.color.gray_400));

        ivProfile.setColorFilter(ContextCompat.getColor(this, R.color.gray_400));
        tvProfile.setTextColor(ContextCompat.getColor(this, R.color.gray_400));
    }

    private void loadOrderData() {
        // TODO: Load order data from intent or API
        // For now, using static data

        // Example of dynamic data loading (will be implemented later)
        /*
        String bikeName = getIntent().getStringExtra("bike_name");
        String orderId = getIntent().getStringExtra("order_id");
        String orderDate = getIntent().getStringExtra("order_date");

        if (bikeName != null) tvBikeName.setText(bikeName);
        if (orderId != null) tvOrderId.setText(orderId);
        if (orderDate != null) tvOrderDate.setText(orderDate);
        */
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}