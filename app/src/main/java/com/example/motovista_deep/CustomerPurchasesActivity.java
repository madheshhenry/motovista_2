package com.example.motovista_deep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CustomerPurchasesActivity extends AppCompatActivity {

    // Header
    private ImageView btnBack;

    // Purchase Items
    private CardView purchaseItem1, purchaseItem2, purchaseItem3;
    private TextView tvBikeName1, tvPurchaseDate1, tvPrice1;
    private TextView tvBikeName2, tvPurchaseDate2, tvPrice2;
    private TextView tvBikeName3, tvPurchaseDate3, tvPrice3;
    private ImageView btnViewDetails1, btnViewDetails2, btnViewDetails3;

    // Empty State
    private LinearLayout emptyState;

    // Bottom Navigation
    private LinearLayout tabHome, tabBikes, tabEmiCalculator, tabOrders, tabProfile;
    private ImageView ivHome, ivBikes, ivEmiCalculator, ivOrders, ivProfile;
    private TextView tvHome, tvBikes, tvEmiCalculator, tvOrders, tvProfile;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_purchases);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("CustomerPrefs", MODE_PRIVATE);

        // Initialize views
        initializeViews();

        // Load purchase data
        loadPurchaseData();

        // Setup click listeners
        setupClickListeners();

        // Set active tab (Orders is active for this screen)
        setActiveTab(tabOrders);
    }

    private void initializeViews() {
        // Header
        btnBack = findViewById(R.id.btnBack);

        // Purchase Items
        // In initializeViews() method:
        purchaseItem1 = findViewById(R.id.cardPurchase1);
        purchaseItem2 = findViewById(R.id.cardPurchase2);
        purchaseItem3 = findViewById(R.id.cardPurchase3);

        tvBikeName1 = findViewById(R.id.tvBikeName1);
        tvPurchaseDate1 = findViewById(R.id.tvPurchaseDate1);
        tvPrice1 = findViewById(R.id.tvPrice1);

        tvBikeName2 = findViewById(R.id.tvBikeName2);
        tvPurchaseDate2 = findViewById(R.id.tvPurchaseDate2);
        tvPrice2 = findViewById(R.id.tvPrice2);

        tvBikeName3 = findViewById(R.id.tvBikeName3);
        tvPurchaseDate3 = findViewById(R.id.tvPurchaseDate3);
        tvPrice3 = findViewById(R.id.tvPrice3);

        btnViewDetails1 = findViewById(R.id.btnViewDetails1);
        btnViewDetails2 = findViewById(R.id.btnViewDetails2);
        btnViewDetails3 = findViewById(R.id.btnViewDetails3);

        // Empty State
        emptyState = findViewById(R.id.emptyState);

        // Bottom Navigation Tabs
        tabHome = findViewById(R.id.tabHome);
        tabBikes = findViewById(R.id.tabBikes);
        tabEmiCalculator = findViewById(R.id.tabEmiCalculator);
        tabOrders = findViewById(R.id.tabOrders);
        tabProfile = findViewById(R.id.tabProfile);

        // Bottom Navigation Icons
        ivHome = findViewById(R.id.ivHome);
        ivBikes = findViewById(R.id.ivBikes);
        ivEmiCalculator = findViewById(R.id.ivEmiCalculator);
        ivOrders = findViewById(R.id.ivOrders);
        ivProfile = findViewById(R.id.ivProfile);

        // Bottom Navigation Text
        tvHome = findViewById(R.id.tvHome);
        tvBikes = findViewById(R.id.tvBikes);
        tvEmiCalculator = findViewById(R.id.tvEmiCalculator);
        tvOrders = findViewById(R.id.tvOrders);
        tvProfile = findViewById(R.id.tvProfile);
    }

    private void loadPurchaseData() {
        // Load purchase data from SharedPreferences or API
        // For now, we'll use hardcoded data as per HTML

        // Check if there are any purchases
        boolean hasPurchases = sharedPreferences.getBoolean("has_purchases", true);

        if (hasPurchases) {
            emptyState.setVisibility(View.GONE);

            // Load actual data from SharedPreferences if available
            String bike1Name = sharedPreferences.getString("purchase_1_name", "Royal Enfield Classic 350");
            String bike1Date = sharedPreferences.getString("purchase_1_date", "15 Oct 2023");
            String bike1Price = sharedPreferences.getString("purchase_1_price", "₹ 2,25,000");

            String bike2Name = sharedPreferences.getString("purchase_2_name", "Yamaha R15 V4");
            String bike2Date = sharedPreferences.getString("purchase_2_date", "22 May 2022");
            String bike2Price = sharedPreferences.getString("purchase_2_price", "₹ 1,98,000");

            String bike3Name = sharedPreferences.getString("purchase_3_name", "TVS Apache RTR 160");
            String bike3Date = sharedPreferences.getString("purchase_3_date", "10 Jan 2021");
            String bike3Price = sharedPreferences.getString("purchase_3_price", "₹ 1,35,000");

            // Set data to views
            tvBikeName1.setText(bike1Name);
            tvPurchaseDate1.setText("Purchased: " + bike1Date);
            tvPrice1.setText(bike1Price);

            tvBikeName2.setText(bike2Name);
            tvPurchaseDate2.setText("Purchased: " + bike2Date);
            tvPrice2.setText(bike2Price);

            tvBikeName3.setText(bike3Name);
            tvPurchaseDate3.setText("Purchased: " + bike3Date);
            tvPrice3.setText(bike3Price);
        } else {
            // Show empty state
            emptyState.setVisibility(View.VISIBLE);
            purchaseItem1.setVisibility(View.GONE);
            purchaseItem2.setVisibility(View.GONE);
            purchaseItem3.setVisibility(View.GONE);
        }
    }

    private void setupClickListeners() {
        // Back Button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // View Details Buttons
        btnViewDetails1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPurchaseDetails(1);
            }
        });

        btnViewDetails2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPurchaseDetails(2);
            }
        });

        btnViewDetails3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPurchaseDetails(3);
            }
        });

        // Purchase Items Click (whole card)
        purchaseItem1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPurchaseDetails(1);
            }
        });

        purchaseItem2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPurchaseDetails(2);
            }
        });

        purchaseItem3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPurchaseDetails(3);
            }
        });

        // Bottom Navigation Tabs
        tabHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToHome();
            }
        });

        tabBikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToBikes();
            }
        });

        tabEmiCalculator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToEmiCalculator();
            }
        });

        tabOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Already on purchases screen
                setActiveTab(tabOrders);
            }
        });

        tabProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToProfile();
            }
        });
    }

    private void viewPurchaseDetails(int purchaseId) {
        Toast.makeText(this, "Viewing details for purchase #" + purchaseId, Toast.LENGTH_SHORT).show();

        // TODO: Navigate to purchase details screen
        // Intent intent = new Intent(CustomerPurchasesActivity.this, PurchaseDetailsActivity.class);
        // intent.putExtra("purchase_id", purchaseId);
        // startActivity(intent);
    }

    private void navigateToHome() {
        Intent intent = new Intent(CustomerPurchasesActivity.this, CustomerHomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void navigateToBikes() {
        Toast.makeText(this, "Navigate to Bikes", Toast.LENGTH_SHORT).show();
        // TODO: Implement bikes listing navigation
        // Intent intent = new Intent(CustomerPurchasesActivity.this, BikesActivity.class);
        // startActivity(intent);
        // finish();
    }

    private void navigateToEmiCalculator() {
        Toast.makeText(this, "Navigate to EMI Calculator", Toast.LENGTH_SHORT).show();
        // TODO: Implement EMI calculator navigation
        // Intent intent = new Intent(CustomerPurchasesActivity.this, EmiCalculatorActivity.class);
        // startActivity(intent);
        // finish();
    }

    private void navigateToProfile() {
        Intent intent = new Intent(CustomerPurchasesActivity.this, CustomerProfileScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void setActiveTab(LinearLayout activeTab) {
        // Reset all tabs
        resetAllTabs();

        // Set active tab
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
            // Just change color, keep same icon
            ivOrders.setColorFilter(ContextCompat.getColor(this, R.color.primary_color));
            tvOrders.setTextColor(ContextCompat.getColor(this, R.color.primary_color));
            tvOrders.setTypeface(tvOrders.getTypeface(), android.graphics.Typeface.BOLD);
        } else if (activeTab == tabProfile) {
            ivProfile.setImageResource(R.drawable.ic_person_filled);
            ivProfile.setColorFilter(ContextCompat.getColor(this, R.color.primary_color));
            tvProfile.setTextColor(ContextCompat.getColor(this, R.color.primary_color));
            tvProfile.setTypeface(tvProfile.getTypeface(), android.graphics.Typeface.BOLD);
        }
    }

    private void resetAllTabs() {
        // Reset Home tab
        ivHome.setImageResource(R.drawable.ic_home_filled);
        ivHome.setColorFilter(ContextCompat.getColor(this, R.color.gray_400));
        tvHome.setTextColor(ContextCompat.getColor(this, R.color.gray_400));
        tvHome.setTypeface(null, android.graphics.Typeface.NORMAL);

        // Reset Bikes tab
        ivBikes.setColorFilter(ContextCompat.getColor(this, R.color.gray_400));
        tvBikes.setTextColor(ContextCompat.getColor(this, R.color.gray_400));
        tvBikes.setTypeface(null, android.graphics.Typeface.NORMAL);

        // Reset EMI Calculator tab
        ivEmiCalculator.setColorFilter(ContextCompat.getColor(this, R.color.gray_400));
        tvEmiCalculator.setTextColor(ContextCompat.getColor(this, R.color.gray_400));
        tvEmiCalculator.setTypeface(null, android.graphics.Typeface.NORMAL);

        // Reset Orders tab
        ivOrders.setImageResource(R.drawable.ic_receipt_long);
        ivOrders.setColorFilter(ContextCompat.getColor(this, R.color.gray_400));
        tvOrders.setTextColor(ContextCompat.getColor(this, R.color.gray_400));
        tvOrders.setTypeface(null, android.graphics.Typeface.NORMAL);

        // Reset Profile tab
        ivProfile.setImageResource(R.drawable.ic_person);
        ivProfile.setColorFilter(ContextCompat.getColor(this, R.color.gray_400));
        tvProfile.setTextColor(ContextCompat.getColor(this, R.color.gray_400));
        tvProfile.setTypeface(null, android.graphics.Typeface.NORMAL);
    }

    @Override
    public void onBackPressed() {
        // Navigate back to profile screen
        navigateToProfile();
    }
}