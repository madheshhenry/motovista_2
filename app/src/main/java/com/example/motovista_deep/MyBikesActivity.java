package com.example.motovista_deep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MyBikesActivity extends AppCompatActivity {

    private CardView btnBack;
    private CardView cardBike1, cardBike2, cardBike3;
    private ImageView ivChevron1, ivChevron2, ivChevron3;
    private LinearLayout emptyStateLayout;
    private Button btnBrowseShowroom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bikes);

        // Initialize views
        initializeViews();

        // Setup click listeners
        setupClickListeners();

        // TODO: Load bikes data from API
        loadBikesData();
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);

        cardBike1 = findViewById(R.id.cardBike1);
        cardBike2 = findViewById(R.id.cardBike2);
        cardBike3 = findViewById(R.id.cardBike3);

        ivChevron1 = findViewById(R.id.ivChevron1);
        ivChevron2 = findViewById(R.id.ivChevron2);
        ivChevron3 = findViewById(R.id.ivChevron3);

        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        btnBrowseShowroom = findViewById(R.id.btnBrowseShowroom);
    }

    private void setupClickListeners() {
        // Back button
        btnBack.setOnClickListener(v -> {
            finish();
        });

        // Bike card clicks
        cardBike1.setOnClickListener(v -> {
            showBikeDetails(1);
        });

        cardBike2.setOnClickListener(v -> {
            showBikeDetails(2);
        });

        cardBike3.setOnClickListener(v -> {
            showBikeDetails(3);
        });

        // Chevron clicks
        ivChevron1.setOnClickListener(v -> {
            showBikeDetails(1);
        });

        ivChevron2.setOnClickListener(v -> {
            showBikeDetails(2);
        });

        ivChevron3.setOnClickListener(v -> {
            showBikeDetails(3);
        });

        // Browse showroom button
        btnBrowseShowroom.setOnClickListener(v -> {
            Intent intent = new Intent(MyBikesActivity.this, BikeCatalogActivity.class);
            startActivity(intent);
        });
    }

    private void showBikeDetails(int bikeId) {
        Intent intent = new Intent(MyBikesActivity.this, BikeDetailsViewActivity.class);

        // Add sample data based on bikeId
        switch (bikeId) {
            case 1:
                intent.putExtra("BIKE_NAME", "Yamaha R15 V4");
                intent.putExtra("REG_NUMBER", "KA-01-HQ-1234");
                intent.putExtra("PURCHASE_DATE", "Jan 12, 2023");
                intent.putExtra("COLOR", "Racing Blue");
                intent.putExtra("VARIANT", "ABS Version 4.0");
                intent.putExtra("ENGINE_NUMBER", "YM123456789");
                intent.putExtra("CHASSIS_NUMBER", "CH987654321");
                intent.putExtra("INSURER", "ICICI Lombard");
                intent.putExtra("POLICY_NUMBER", "3001/A/123456");
                intent.putExtra("INSURANCE_TYPE", "Comprehensive");
                intent.putExtra("INSURANCE_START", "12 Jan 2023");
                intent.putExtra("INSURANCE_END", "11 Jan 2024");
                intent.putExtra("REGISTRATION_STATUS", "Registered");
                intent.putExtra("RC_BOOK_STATUS", "Dispatched");
                intent.putExtra("NUMBER_PLATE_STATUS", "Fitted");
                break;

            case 2:
                intent.putExtra("BIKE_NAME", "Classic 350");
                intent.putExtra("REG_NUMBER", "Pending...");
                intent.putExtra("PURCHASE_DATE", "Oct 5, 2023");
                intent.putExtra("COLOR", "Stealth Black");
                intent.putExtra("VARIANT", "Reborn");
                intent.putExtra("ENGINE_NUMBER", "RE350789012");
                intent.putExtra("CHASSIS_NUMBER", "CH350123456");
                intent.putExtra("INSURER", "Bajaj Allianz");
                intent.putExtra("POLICY_NUMBER", "BAJ/2023/789");
                intent.putExtra("INSURANCE_TYPE", "Third Party");
                intent.putExtra("INSURANCE_START", "05 Oct 2023");
                intent.putExtra("INSURANCE_END", "04 Oct 2024");
                intent.putExtra("REGISTRATION_STATUS", "In Progress");
                intent.putExtra("RC_BOOK_STATUS", "Processing");
                intent.putExtra("NUMBER_PLATE_STATUS", "Pending");
                break;

            case 3:
                intent.putExtra("BIKE_NAME", "Ducati Panigale V4");
                intent.putExtra("REG_NUMBER", "MH-12-AB-9999");
                intent.putExtra("PURCHASE_DATE", "Mar 15, 2023");
                intent.putExtra("COLOR", "Ducati Red");
                intent.putExtra("VARIANT", "V4 S");
                intent.putExtra("ENGINE_NUMBER", "DUCATI789123");
                intent.putExtra("CHASSIS_NUMBER", "CHD456789");
                intent.putExtra("INSURER", "HDFC Ergo");
                intent.putExtra("POLICY_NUMBER", "HDFC/789/456");
                intent.putExtra("INSURANCE_TYPE", "Comprehensive");
                intent.putExtra("INSURANCE_START", "15 Mar 2023");
                intent.putExtra("INSURANCE_END", "14 Mar 2024");
                intent.putExtra("REGISTRATION_STATUS", "Registered");
                intent.putExtra("RC_BOOK_STATUS", "Delivered");
                intent.putExtra("NUMBER_PLATE_STATUS", "Fitted");
                break;
        }

        startActivity(intent);
    }

    private void loadBikesData() {
        // TODO: Make API call to get user's bikes
        // For now, we'll use static data

        // If no bikes, show empty state
        // emptyStateLayout.setVisibility(View.VISIBLE);
        // cardBike1.setVisibility(View.GONE);
        // cardBike2.setVisibility(View.GONE);
        // cardBike3.setVisibility(View.GONE);

        // If has bikes, hide empty state (default)
        emptyStateLayout.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}