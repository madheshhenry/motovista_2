package com.example.motovista_deep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class BikeDetailsScreenActivity extends AppCompatActivity {

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
    private String bikeName, bikePrice, bikeImage;
    private String bikeVariant, bikeYear, bikeBrand, bikeModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_details_screen);

        // Get data from intent
        getIntentData();

        // Initialize all views
        initializeViews();

        // Set bike data to views
        populateBikeData();

        // Setup click listeners
        setupClickListeners();
    }

    private void getIntentData() {
        Intent intent = getIntent();

        // Get bike details from intent
        bikeName = intent.getStringExtra("BIKE_NAME");
        bikePrice = intent.getStringExtra("BIKE_PRICE");
        bikeVariant = intent.getStringExtra("BIKE_VARIANT");
        bikeYear = intent.getStringExtra("BIKE_YEAR");
        bikeBrand = intent.getStringExtra("BIKE_BRAND");
        bikeModel = intent.getStringExtra("BIKE_MODEL");
        bikeImage = intent.getStringExtra("BIKE_IMAGE");

        // Set default values if not provided
        if (bikeName == null) bikeName = "Yamaha YZF R15 V4";
        if (bikePrice == null) bikePrice = "₹2,450";
        if (bikeVariant == null) bikeVariant = "Racing Blue";
        if (bikeYear == null) bikeYear = "2024";
        if (bikeBrand == null) bikeBrand = "Yamaha";
        if (bikeModel == null) bikeModel = "YZF R15 V4";
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

    private void populateBikeData() {
        // Set main bike details
        tvBikeTitle.setText(bikeName);
        tvBikePrice.setText(bikePrice);
        tvBikeVariant.setText(bikeVariant + " Variant");
        tvModelYear.setText(bikeYear + " Model");

        // Set basic details
        tvBrandName.setText(bikeBrand);
        tvModelName.setText(bikeModel);
        tvDetailVariant.setText(bikeVariant);
        tvDetailYear.setText(bikeYear);

        // Set specification values (these would come from API/database)
        // For now, using defaults
        tvEngineValue.setText("155 cc");
        tvMileageValue.setText("45 km/l");
        tvFuelValue.setText("Petrol");
        tvGearValue.setText("6-Speed");

        tvDetailEngine.setText("155 cc");
        tvDetailFuel.setText("Petrol");
        tvTransmission.setText("6-Speed Manual");
        tvBraking.setText("Dual Channel ABS");

        tvSpecMileage.setText("45 km/l");
        tvFuelCapacity.setText("11 Litres");
        tvKerbWeight.setText("142 kg");
        tvSeatHeight.setText("815 mm");
        tvGroundClearance.setText("170 mm");

        // Set image if available (you would use Glide/Picasso for network images)
        // For now, using drawable
        // ivBikeImage.setImageResource(R.drawable.bike_raptor);
    }

    private void setupClickListeners() {
        // Back button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // View Invoice Sample button
        btnViewInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewInvoiceSample();
            }
        });

        // Order Now button
        btnOrderBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeOrder();
            }
        });
    }

    private void viewInvoiceSample() {
        // View invoice logic here
        Toast.makeText(this, "Viewing invoice sample for " + bikeName, Toast.LENGTH_SHORT).show();

        // Navigate to invoice sample screen
        // Intent invoiceIntent = new Intent(this, InvoiceSampleActivity.class);
        // invoiceIntent.putExtra("BIKE_NAME", bikeName);
        // invoiceIntent.putExtra("BIKE_PRICE", bikePrice);
        // startActivity(invoiceIntent);
    }

    private void placeOrder() {
        // Navigate to Request Sent screen
        Intent requestSentIntent = new Intent(BikeDetailsScreenActivity.this, RequestSentActivity.class);

        // Pass bike details
        requestSentIntent.putExtra("BIKE_NAME", bikeName);
        requestSentIntent.putExtra("BIKE_PRICE", bikePrice);
        requestSentIntent.putExtra("BIKE_VARIANT", bikeVariant);
        requestSentIntent.putExtra("BIKE_BRAND", bikeBrand);
        requestSentIntent.putExtra("BIKE_MODEL", bikeModel);
        requestSentIntent.putExtra("BIKE_YEAR", bikeYear);

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