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

public class BikeCatalogActivity extends AppCompatActivity {

    // Top header
    private ImageView btnBack;
    private TextView tvTitle;

    // Filter chips
    private CardView chipAll, chipPetrol, chipEBikes, chipSecondHand;

    // Bike cards
    private CardView cardRaptor, cardCruiser, cardTrailBlazer;

    // View Details buttons
    private Button btnViewDetailsRaptor, btnViewDetailsCruiser, btnViewDetailsTrailBlazer;

    // Bottom navigation
    private LinearLayout tabHome, tabBikes, tabEmiCalculator, tabOrders, tabProfile;
    private ImageView ivHome, ivBikes, ivEmiCalculator, ivOrders, ivProfile;
    private TextView tvHome, tvBikes, tvEmiCalculator, tvOrders, tvProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_catalog);

        // Initialize views
        initializeViews();

        // Setup click listeners
        setupClickListeners();

        // Set active tab (Bikes is active by default)
        setActiveTab(tabBikes);

        // Set active filter chip (All is active by default)
        setActiveChip(chipAll);
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

        // Bike cards
        cardRaptor = findViewById(R.id.cardRaptor);
        cardCruiser = findViewById(R.id.cardCruiser);
        cardTrailBlazer = findViewById(R.id.cardTrailBlazer);

        // View Details buttons
        btnViewDetailsRaptor = findViewById(R.id.btnViewDetailsRaptor);
        btnViewDetailsCruiser = findViewById(R.id.btnViewDetailsCruiser);
        btnViewDetailsTrailBlazer = findViewById(R.id.btnViewDetailsTrailBlazer);

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
        // Back button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Filter chips
        chipAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveChip(chipAll);
                Toast.makeText(BikeCatalogActivity.this, "Showing all bikes", Toast.LENGTH_SHORT).show();
                // Filter logic for all bikes
            }
        });

        chipPetrol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveChip(chipPetrol);
                Toast.makeText(BikeCatalogActivity.this, "Showing petrol bikes", Toast.LENGTH_SHORT).show();
                // Filter logic for petrol bikes
            }
        });

        chipEBikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveChip(chipEBikes);
                Toast.makeText(BikeCatalogActivity.this, "Showing e-bikes", Toast.LENGTH_SHORT).show();
                // Filter logic for e-bikes
            }
        });

        chipSecondHand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveChip(chipSecondHand);
                Toast.makeText(BikeCatalogActivity.this, "Showing second-hand bikes", Toast.LENGTH_SHORT).show();
                // Filter logic for second-hand bikes
            }
        });

        // Bike cards click listeners
        cardRaptor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(BikeCatalogActivity.this, "SB-Raptor 500", Toast.LENGTH_SHORT).show();
                // Navigate to bike details
                // startActivity(new Intent(BikeCatalogActivity.this, BikeDetailsActivity.class));
            }
        });

        cardCruiser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(BikeCatalogActivity.this, "SB-Cruiser King", Toast.LENGTH_SHORT).show();
                // Navigate to bike details
                // startActivity(new Intent(BikeCatalogActivity.this, BikeDetailsActivity.class));
            }
        });

        cardTrailBlazer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(BikeCatalogActivity.this, "SB-TrailBlazer", Toast.LENGTH_SHORT).show();
                // Navigate to bike details
                // startActivity(new Intent(BikeCatalogActivity.this, BikeDetailsActivity.class));
            }
        });

        // View Details buttons
        btnViewDetailsRaptor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(BikeCatalogActivity.this, "Viewing details for SB-Raptor 500", Toast.LENGTH_SHORT).show();
                // Navigate to bike details page
                // startActivity(new Intent(BikeCatalogActivity.this, BikeDetailsActivity.class));
            }
        });

        btnViewDetailsCruiser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(BikeCatalogActivity.this, "Viewing details for SB-Cruiser King", Toast.LENGTH_SHORT).show();
                // Navigate to bike details page
                // startActivity(new Intent(BikeCatalogActivity.this, BikeDetailsActivity.class));
            }
        });

        btnViewDetailsTrailBlazer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(BikeCatalogActivity.this, "Viewing details for SB-TrailBlazer", Toast.LENGTH_SHORT).show();
                // Navigate to bike details page
                // startActivity(new Intent(BikeCatalogActivity.this, BikeDetailsActivity.class));
            }
        });

        // Bottom navigation tabs
        tabHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveTab(tabHome);
                // Navigate to home screen
                startActivity(new Intent(BikeCatalogActivity.this, CustomerHomeActivity.class));
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        tabBikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Already on bikes screen
                setActiveTab(tabBikes);
            }
        });

        tabEmiCalculator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveTab(tabEmiCalculator);
                // Navigate to EMI calculator
                startActivity(new Intent(BikeCatalogActivity.this, EmiCalculatorActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        tabOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveTab(tabOrders);
                Toast.makeText(BikeCatalogActivity.this, "Orders", Toast.LENGTH_SHORT).show();
                // Navigate to orders
                // startActivity(new Intent(BikeCatalogActivity.this, OrdersActivity.class));
            }
        });

        tabProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveTab(tabProfile);
                // Navigate to profile screen
                startActivity(new Intent(BikeCatalogActivity.this, CustomerProfileScreenActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
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
            ivBikes.setImageResource(R.drawable.ic_two_wheeler);
            ivBikes.setColorFilter(ContextCompat.getColor(this, R.color.primary_color));
            tvBikes.setTextColor(ContextCompat.getColor(this, R.color.primary_color));
            tvBikes.setTypeface(tvBikes.getTypeface(), android.graphics.Typeface.BOLD);
        } else if (activeTab == tabEmiCalculator) {
            ivEmiCalculator.setColorFilter(ContextCompat.getColor(this, R.color.primary_color));
            tvEmiCalculator.setTextColor(ContextCompat.getColor(this, R.color.primary_color));
            tvEmiCalculator.setTypeface(tvEmiCalculator.getTypeface(), android.graphics.Typeface.BOLD);
        } else if (activeTab == tabOrders) {
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
        // Reset Home tab
        ivHome.setImageResource(R.drawable.ic_home_filled);
        ivHome.setColorFilter(ContextCompat.getColor(this, R.color.gray_400));
        tvHome.setTextColor(ContextCompat.getColor(this, R.color.gray_400));
        tvHome.setTypeface(null, android.graphics.Typeface.NORMAL);

        // Reset Bikes tab
        ivBikes.setImageResource(R.drawable.ic_two_wheeler);
        ivBikes.setColorFilter(ContextCompat.getColor(this, R.color.gray_400));
        tvBikes.setTextColor(ContextCompat.getColor(this, R.color.gray_400));
        tvBikes.setTypeface(null, android.graphics.Typeface.NORMAL);

        // Reset EMI Calculator tab
        ivEmiCalculator.setColorFilter(ContextCompat.getColor(this, R.color.gray_400));
        tvEmiCalculator.setTextColor(ContextCompat.getColor(this, R.color.gray_400));
        tvEmiCalculator.setTypeface(null, android.graphics.Typeface.NORMAL);

        // Reset Orders tab
        ivOrders.setColorFilter(ContextCompat.getColor(this, R.color.gray_400));
        tvOrders.setTextColor(ContextCompat.getColor(this, R.color.gray_400));
        tvOrders.setTypeface(null, android.graphics.Typeface.NORMAL);

        // Reset Profile tab
        ivProfile.setColorFilter(ContextCompat.getColor(this, R.color.gray_400));
        tvProfile.setTextColor(ContextCompat.getColor(this, R.color.gray_400));
        tvProfile.setTypeface(null, android.graphics.Typeface.NORMAL);
    }

    private void setActiveChip(CardView activeChip) {
        // Reset all chips
        chipAll.setCardBackgroundColor(ContextCompat.getColor(this, R.color.background_light));
        ((TextView) chipAll.getChildAt(0)).setTextColor(ContextCompat.getColor(this, R.color.text_gray));

        chipPetrol.setCardBackgroundColor(ContextCompat.getColor(this, R.color.background_light));
        ((TextView) chipPetrol.getChildAt(0)).setTextColor(ContextCompat.getColor(this, R.color.text_gray));

        chipEBikes.setCardBackgroundColor(ContextCompat.getColor(this, R.color.background_light));
        ((TextView) chipEBikes.getChildAt(0)).setTextColor(ContextCompat.getColor(this, R.color.text_gray));

        chipSecondHand.setCardBackgroundColor(ContextCompat.getColor(this, R.color.background_light));
        ((TextView) chipSecondHand.getChildAt(0)).setTextColor(ContextCompat.getColor(this, R.color.text_gray));

        // Set active chip
        activeChip.setCardBackgroundColor(ContextCompat.getColor(this, R.color.primary_light));
        ((TextView) activeChip.getChildAt(0)).setTextColor(ContextCompat.getColor(this, R.color.primary));
    }

    @Override
    public void onBackPressed() {
        // Go back to home screen
        startActivity(new Intent(this, CustomerHomeActivity.class));
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}