package com.example.motovista_deep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ProgressDialog;
import android.util.Log;

import com.example.motovista_deep.adapter.ImageSliderAdapter;
import com.example.motovista_deep.helpers.SharedPrefManager;
import com.example.motovista_deep.models.BikeModel;
import com.example.motovista_deep.api.ApiService;
import com.example.motovista_deep.api.RetrofitClient;
import com.example.motovista_deep.models.GenericResponse;
import com.example.motovista_deep.models.DeleteBikeRequest;
import com.example.motovista_deep.models.GetBikeByIdResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;

public class BikeDetailsActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView tvTitle;
    private ViewPager2 imageViewPager;
    private LinearLayout dotsIndicator;
    private LinearLayout detailsContainer;
    private Button btnEdit, btnDelete;

    private BikeModel bike;
    private String bikeType;
    private int bikeId;
    private boolean isDataLoaded = false;

    private ImageSliderAdapter imageSliderAdapter;
    private Handler sliderHandler = new Handler();
    private ArrayList<String> imageUrls = new ArrayList<>();
    private Runnable sliderRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_details);

        // Get data from intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("BIKE_MODEL")) {
            bike = intent.getParcelableExtra("BIKE_MODEL");
            bikeType = intent.getStringExtra("BIKE_TYPE");
            bikeId = bike.getId();

            // Debug: Check what data we have from intent
            logBikeDetails("From Intent");
        }

        initializeViews();
        setupListeners();
        setupImageSlider();
        // Changed from loadBikeData() to fetchFreshBikeData()
        fetchFreshBikeData();
    }

    private void logBikeDetails(String source) {
        Log.d("BIKE_DETAILS_DEBUG", "=== " + source + " ===");
        if (bike != null) {
            Log.d("BIKE_DETAILS_DEBUG", "Bike ID: " + bike.getId());
            Log.d("BIKE_DETAILS_DEBUG", "Date: " + bike.getDate());
            Log.d("BIKE_DETAILS_DEBUG", "Engine Number: " + bike.getEngine_number());
            Log.d("BIKE_DETAILS_DEBUG", "Chassis Number: " + bike.getChassis_number());
            Log.d("BIKE_DETAILS_DEBUG", "Variant: " + bike.getVariant());
            Log.d("BIKE_DETAILS_DEBUG", "Year: " + bike.getYear());
        } else {
            Log.d("BIKE_DETAILS_DEBUG", "Bike object is null!");
        }
    }

    private String getCleanImageUrl(String url) {
        if (url == null || url.isEmpty()) {
            return "";
        }

        // Remove all quotes and backslashes
        url = url.replace("\\", "").replace("\"", "");

        // If it's already a full URL, return it
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return url;
        }

        // If it's a relative path, add base URL
        String baseUrl = RetrofitClient.BASE_URL;
        if (!baseUrl.endsWith("/")) {
            baseUrl += "/";
        }

        // Remove leading slash if present
        if (url.startsWith("/")) {
            url = url.substring(1);
        }

        return baseUrl + url;
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        tvTitle = findViewById(R.id.tvTitle);
        imageViewPager = findViewById(R.id.imageViewPager);
        dotsIndicator = findViewById(R.id.dotsIndicator);
        detailsContainer = findViewById(R.id.detailsContainer);
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);

        // Set title
        if (bike != null) {
            tvTitle.setText(bike.getBrand() + " " + bike.getModel());
        } else {
            tvTitle.setText("Bike Details");
        }
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnEdit.setOnClickListener(v -> {
            if ("NEW".equalsIgnoreCase(bikeType)) {
                editNewBike();
            } else {
                editSecondHandBike();
            }
        });

        btnDelete.setOnClickListener(v -> showDeleteConfirmationDialog());
    }

    private void setupImageSlider() {
        imageUrls.clear();

        if (bike != null) {
            ArrayList<String> allImages = bike.getAllImages();
            if (allImages != null && !allImages.isEmpty()) {
                for (String img : allImages) {
                    String cleanedUrl = getCleanImageUrl(img);
                    imageUrls.add(cleanedUrl);
                    Log.d("BIKE_DETAILS", "Added image from getAllImages: " + cleanedUrl);
                }
            } else if (bike.getImageUrls() != null && !bike.getImageUrls().isEmpty()) {
                for (String img : bike.getImageUrls()) {
                    String cleanedUrl = getCleanImageUrl(img);
                    imageUrls.add(cleanedUrl);
                    Log.d("BIKE_DETAILS", "Added image from getImageUrls: " + cleanedUrl);
                }
            } else {
                String singleImage = bike.getImageUrl();
                if (singleImage != null && !singleImage.isEmpty()) {
                    String cleanedUrl = getCleanImageUrl(singleImage);
                    imageUrls.add(cleanedUrl);
                    Log.d("BIKE_DETAILS", "Added single image: " + cleanedUrl);
                }
            }
        }

        if (imageUrls.isEmpty()) {
            // Use a placeholder
            imageUrls.add("https://via.placeholder.com/600x400?text=No+Image");
            Log.d("BIKE_DETAILS", "No images found, using placeholder");
        }

        Log.d("BIKE_DETAILS", "Total images for slider: " + imageUrls.size());

        imageSliderAdapter = new ImageSliderAdapter(this, imageUrls);
        imageViewPager.setAdapter(imageSliderAdapter);

        // Setup dots
        setupDotsIndicator(imageUrls.size());

        imageViewPager.registerOnPageChangeCallback(
                new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {
                        updateDotsIndicator(position);
                        resetAutoSlider();
                    }
                }
        );

        if (imageUrls.size() > 1) {
            startAutoSlide();
        }
    }

    private void setupDotsIndicator(int count) {
        dotsIndicator.removeAllViews();

        for (int i = 0; i < count; i++) {
            View dot = new View(this);
            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(12, 12);
            params.setMargins(6, 0, 6, 0);
            dot.setLayoutParams(params);
            dot.setBackgroundResource(
                    i == 0 ? R.drawable.shape_dot_active : R.drawable.shape_dot_inactive
            );
            dotsIndicator.addView(dot);
        }
    }

    private void updateDotsIndicator(int position) {
        for (int i = 0; i < dotsIndicator.getChildCount(); i++) {
            dotsIndicator.getChildAt(i).setBackgroundResource(
                    i == position
                            ? R.drawable.shape_dot_active
                            : R.drawable.shape_dot_inactive
            );
        }
    }

    private void startAutoSlide() {
        sliderRunnable = () -> {
            int next = (imageViewPager.getCurrentItem() + 1) % imageUrls.size();
            imageViewPager.setCurrentItem(next, true);
            sliderHandler.postDelayed(sliderRunnable, 3000);
        };
        sliderHandler.postDelayed(sliderRunnable, 3000);
    }

    private void resetAutoSlider() {
        if (sliderRunnable != null) {
            sliderHandler.removeCallbacks(sliderRunnable);
            sliderHandler.postDelayed(sliderRunnable, 3000);
        }
    }

    private void fetchFreshBikeData() {
        // Only fetch if it's a new bike (since new bikes have the date, engine number, chassis number fields)
        if ("NEW".equalsIgnoreCase(bikeType)) {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Loading bike details...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            String token = SharedPrefManager.getInstance(this).getToken();
            if (token == null || token.isEmpty()) {
                progressDialog.dismiss();
                Toast.makeText(this, "Please login again", Toast.LENGTH_SHORT).show();
                loadBikeDataWithExistingData();
                return;
            }

            ApiService apiService = RetrofitClient.getApiService();

            // Debug: Log the bike ID
            Log.d("BIKE_DETAILS_API", "Fetching bike details for ID: " + bikeId);
            Log.d("BIKE_DETAILS_API", "Token present: " + (token != null));

            Call<GetBikeByIdResponse> call = apiService.getBikeById("Bearer " + token, bikeId);

            call.enqueue(new Callback<GetBikeByIdResponse>() {
                @Override
                public void onResponse(Call<GetBikeByIdResponse> call, Response<GetBikeByIdResponse> response) {
                    progressDialog.dismiss();

                    Log.d("BIKE_DETAILS_API", "Response code: " + response.code());
                    Log.d("BIKE_DETAILS_API", "Response is successful: " + response.isSuccessful());

                    if (response.isSuccessful() && response.body() != null) {
                        GetBikeByIdResponse apiResponse = response.body();
                        Log.d("BIKE_DETAILS_API", "Response status: " + apiResponse.getStatus());
                        Log.d("BIKE_DETAILS_API", "Response message: " + apiResponse.getMessage());

                        if ("success".equals(apiResponse.getStatus())) {
                            GetBikeByIdResponse.BikeData bikeData = apiResponse.getData();
                            if (bikeData != null) {
                                Log.d("BIKE_DETAILS_API", "Bike data received:");
                                Log.d("BIKE_DETAILS_API", "Date: " + bikeData.getDate());
                                Log.d("BIKE_DETAILS_API", "Engine Number: " + bikeData.getEngine_number());
                                Log.d("BIKE_DETAILS_API", "Chassis Number: " + bikeData.getChassis_number());

                                // Update bike model with fresh data from API
                                updateBikeModelWithApiData(bikeData);
                                logBikeDetails("After API Fetch");
                                loadBikeData();
                                isDataLoaded = true;
                            } else {
                                Log.e("BIKE_DETAILS_API", "BikeData is null in response");
                                Toast.makeText(BikeDetailsActivity.this,
                                        "No bike data found", Toast.LENGTH_SHORT).show();
                                loadBikeDataWithExistingData();
                            }
                        } else {
                            Log.e("BIKE_DETAILS_API", "API returned error: " + apiResponse.getMessage());
                            Toast.makeText(BikeDetailsActivity.this,
                                    apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            loadBikeDataWithExistingData();
                        }
                    } else {
                        String errorBody = "Unknown error";
                        try {
                            if (response.errorBody() != null) {
                                errorBody = response.errorBody().string();
                            }
                        } catch (Exception e) {
                            errorBody = e.getMessage();
                        }
                        Log.e("BIKE_DETAILS_API", "Response not successful. Error: " + errorBody);
                        Toast.makeText(BikeDetailsActivity.this,
                                "Failed to fetch bike details. Code: " + response.code(), Toast.LENGTH_SHORT).show();
                        loadBikeDataWithExistingData();
                    }
                }

                @Override
                public void onFailure(Call<GetBikeByIdResponse> call, Throwable t) {
                    progressDialog.dismiss();
                    Log.e("BIKE_DETAILS_API", "API call failed: " + t.getMessage(), t);
                    Toast.makeText(BikeDetailsActivity.this,
                            "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    loadBikeDataWithExistingData();
                }
            });
        } else {
            // For second hand bikes, just use existing data
            loadBikeDataWithExistingData();
        }
    }

    private void updateBikeModelWithApiData(GetBikeByIdResponse.BikeData bikeData) {
        if (bikeData != null && bike != null) {
            // Update the bike object with fresh data from API
            bike.setDate(bikeData.getDate());
            bike.setEngine_number(bikeData.getEngine_number());
            bike.setChassis_number(bikeData.getChassis_number());

            // Also update other fields that might be missing
            bike.setVariant(bikeData.getVariant());
            bike.setYear(bikeData.getYear());
            bike.setEngineCC(bikeData.getEngine_cc());
            bike.setFuelType(bikeData.getFuel_type());
            bike.setTransmission(bikeData.getTransmission());
            bike.setBrakingType(bikeData.getBraking_type());
            bike.setOnRoadPrice(bikeData.getOn_road_price());
            bike.setMileage(bikeData.getMileage());
            bike.setFuelTankCapacity(bikeData.getFuel_tank_capacity());
            bike.setKerbWeight(bikeData.getKerb_weight());
            bike.setSeatHeight(bikeData.getSeat_height());
            bike.setGroundClearance(bikeData.getGround_clearance());
            bike.setWarrantyPeriod(bikeData.getWarranty_period());
            bike.setFreeServicesCount(bikeData.getFree_services_count());
            bike.setRegistrationProof(bikeData.getRegistration_proof());
            bike.setPriceDisclaimer(bikeData.getPrice_disclaimer());

            // Set the price for display
            if (bikeData.getOn_road_price() != null && !bikeData.getOn_road_price().isEmpty()) {
                try {
                    double price = Double.parseDouble(bikeData.getOn_road_price());
                    bike.setPrice(String.valueOf(price));
                } catch (NumberFormatException e) {
                    // Keep existing price
                }
            }
        }
    }

    private void loadBikeData() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View detailsView;

        if ("NEW".equalsIgnoreCase(bikeType)) {
            detailsView = inflater.inflate(
                    R.layout.layout_new_bike_details, detailsContainer, false
            );
            setupNewBikeDetails(detailsView);
        } else {
            detailsView = inflater.inflate(
                    R.layout.layout_sh_bike_details, detailsContainer, false
            );
            setupSHBikeDetails(detailsView);
        }

        detailsContainer.removeAllViews();
        detailsContainer.addView(detailsView);
    }

    private void loadBikeDataWithExistingData() {
        // Use existing data without API call
        loadBikeData();
    }

    private void setupNewBikeDetails(View view) {
        if (bike == null) return;

        // Basic Details
        TextView tvBrand = view.findViewById(R.id.tvBrand);
        TextView tvModel = view.findViewById(R.id.tvModel);
        TextView tvVariant = view.findViewById(R.id.tvVariant);
        TextView tvYear = view.findViewById(R.id.tvYear);
        TextView tvEngineCC = view.findViewById(R.id.tvEngineCC);
        TextView tvFuelType = view.findViewById(R.id.tvFuelType);
        TextView tvTransmission = view.findViewById(R.id.tvTransmission);
        TextView tvBrakingType = view.findViewById(R.id.tvBrakingType);

        // NEW FIELDS: Add TextViews for date, engine number, chassis number
        TextView tvDate = view.findViewById(R.id.tvDate);
        TextView tvEngineNumber = view.findViewById(R.id.tvEngineNumber);
        TextView tvChassisNumber = view.findViewById(R.id.tvChassisNumber);

        // Set values with proper null checks
        tvBrand.setText(bike.getBrand() != null ? bike.getBrand() : "N/A");
        tvModel.setText(bike.getModel() != null ? bike.getModel() : "N/A");
        tvVariant.setText(bike.getVariant() != null && !bike.getVariant().isEmpty() ?
                bike.getVariant() : "Standard");
        tvYear.setText(bike.getYear() != null && !bike.getYear().isEmpty() ?
                bike.getYear() : "2024");
        tvEngineCC.setText(bike.getEngineCC() != null && !bike.getEngineCC().isEmpty() ?
                bike.getEngineCC() : "N/A");
        tvFuelType.setText(bike.getFuel_type() != null && !bike.getFuel_type().isEmpty() ?
                bike.getFuel_type() : "Petrol");
        tvTransmission.setText(bike.getTransmission() != null && !bike.getTransmission().isEmpty() ?
                bike.getTransmission() : "Manual");
        tvBrakingType.setText(bike.getBrakingType() != null && !bike.getBrakingType().isEmpty() ?
                bike.getBrakingType() : "N/A");

        // SET NEW FIELDS - These should now have data from API
        String date = bike.getDate();
        String engineNumber = bike.getEngine_number();
        String chassisNumber = bike.getChassis_number();

        Log.d("BIKE_DETAILS_UI", "Date: " + date);
        Log.d("BIKE_DETAILS_UI", "Engine Number: " + engineNumber);
        Log.d("BIKE_DETAILS_UI", "Chassis Number: " + chassisNumber);

        tvDate.setText(date != null && !date.isEmpty() ? date : "Not specified");
        tvEngineNumber.setText(engineNumber != null && !engineNumber.isEmpty() ?
                engineNumber : "Not specified");
        tvChassisNumber.setText(chassisNumber != null && !chassisNumber.isEmpty() ?
                chassisNumber : "Not specified");

        // Price Configuration
        TextView tvExShowroom = view.findViewById(R.id.tvExShowroom);
        TextView tvInsurance = view.findViewById(R.id.tvInsurance);
        TextView tvRegistration = view.findViewById(R.id.tvRegistration);
        TextView tvLTRT = view.findViewById(R.id.tvLTRT);
        TextView tvTotalPrice = view.findViewById(R.id.tvTotalPrice);

        String price = bike.getPrice();
        if (price != null && !price.isEmpty()) {
            price = price.replace("₹", "").trim();
            double total = 0;
            try {
                total = Double.parseDouble(price.replace(",", ""));
            } catch (NumberFormatException e) {
                // Try to use on_road_price if price parsing fails
                String onRoadPrice = bike.getOnRoadPrice();
                if (onRoadPrice != null && !onRoadPrice.isEmpty()) {
                    try {
                        total = Double.parseDouble(onRoadPrice.replace(",", ""));
                    } catch (NumberFormatException ex) {
                        total = 210350; // Default fallback
                    }
                } else {
                    total = 210350; // Default fallback
                }
            }

            tvExShowroom.setText("₹ " + formatPrice(total * 0.87));
            tvInsurance.setText("₹ " + formatPrice(total * 0.06));
            tvRegistration.setText("₹ " + formatPrice(total * 0.05));
            tvLTRT.setText("₹ " + formatPrice(total * 0.02));
            tvTotalPrice.setText("₹ " + formatPrice(total));
        } else {
            // Try to use on_road_price if price is empty
            String onRoadPrice = bike.getOnRoadPrice();
            if (onRoadPrice != null && !onRoadPrice.isEmpty()) {
                try {
                    double total = Double.parseDouble(onRoadPrice.replace(",", ""));
                    tvExShowroom.setText("₹ " + formatPrice(total * 0.87));
                    tvInsurance.setText("₹ " + formatPrice(total * 0.06));
                    tvRegistration.setText("₹ " + formatPrice(total * 0.05));
                    tvLTRT.setText("₹ " + formatPrice(total * 0.02));
                    tvTotalPrice.setText("₹ " + formatPrice(total));
                } catch (NumberFormatException e) {
                    setDefaultPriceValues(tvExShowroom, tvInsurance, tvRegistration, tvLTRT, tvTotalPrice);
                }
            } else {
                setDefaultPriceValues(tvExShowroom, tvInsurance, tvRegistration, tvLTRT, tvTotalPrice);
            }
        }

        // Specifications
        TextView tvMileage = view.findViewById(R.id.tvMileage);
        TextView tvFuelTank = view.findViewById(R.id.tvFuelTank);
        TextView tvKerbWeight = view.findViewById(R.id.tvKerbWeight);
        TextView tvSeatHeight = view.findViewById(R.id.tvSeatHeight);
        TextView tvGroundClearance = view.findViewById(R.id.tvGroundClearance);

        tvMileage.setText(bike.getMileage() != null && !bike.getMileage().isEmpty() ?
                bike.getMileage() : "N/A");
        tvFuelTank.setText(bike.getFuel_tank() != null && !bike.getFuel_tank().isEmpty() ?
                bike.getFuel_tank() : "11 L");
        tvKerbWeight.setText(bike.getKerb_weight() != null && !bike.getKerb_weight().isEmpty() ?
                bike.getKerb_weight() : "142 kg");
        tvSeatHeight.setText(bike.getSeat_height() != null && !bike.getSeat_height().isEmpty() ?
                bike.getSeat_height() : "815 mm");
        tvGroundClearance.setText(bike.getGround_clearance() != null && !bike.getGround_clearance().isEmpty() ?
                bike.getGround_clearance() : "170 mm");

        // Warranty
        TextView tvWarrantyPeriod = view.findViewById(R.id.tvWarrantyPeriod);
        TextView tvFreeServices = view.findViewById(R.id.tvFreeServices);
        tvWarrantyPeriod.setText(bike.getWarranty() != null && !bike.getWarranty().isEmpty() ?
                bike.getWarranty() : "5 Years");
        tvFreeServices.setText(bike.getFree_services() != null && !bike.getFree_services().isEmpty() ?
                bike.getFree_services() : "4 Services");

        // Legal Notes
        TextView tvRegistrationProof = view.findViewById(R.id.tvRegistrationProof);
        TextView tvPriceDisclaimer = view.findViewById(R.id.tvPriceDisclaimer);
        tvRegistrationProof.setText(bike.getRegistration_proof() != null && !bike.getRegistration_proof().isEmpty() ?
                bike.getRegistration_proof() : "For registration purpose any one of the following is required in ORIGINAL & XEROX for Address Proof: Aadhar Card, LIC Policy, Passport, GST, Voter ID, PAN Card");
        tvPriceDisclaimer.setText(bike.getPrice_disclaimer() != null && !bike.getPrice_disclaimer().isEmpty() ?
                bike.getPrice_disclaimer() : "Price & taxes ruling at the time of delivery will be applicable.");

        // Bank Details
        TextView tvAccountName = view.findViewById(R.id.tvAccountName);
        TextView tvAccountNumber = view.findViewById(R.id.tvAccountNumber);
        TextView tvIFSC = view.findViewById(R.id.tvIFSC);
        tvAccountName.setText("SANTHOSH BIKES");
        tvAccountNumber.setText("75010200000585");
        tvIFSC.setText("BARBOVJAVAD");

        // Setup fittings
        setupFittings(view);
    }

    private void setDefaultPriceValues(TextView tvExShowroom, TextView tvInsurance,
                                       TextView tvRegistration, TextView tvLTRT, TextView tvTotalPrice) {
        tvExShowroom.setText("₹ 1,82,000");
        tvInsurance.setText("₹ 12,500");
        tvRegistration.setText("₹ 14,350");
        tvLTRT.setText("₹ 1,500");
        tvTotalPrice.setText("₹ 2,10,350");
    }

    private void setupSHBikeDetails(View view) {
        if (bike == null) return;

        TextView tvBrand = view.findViewById(R.id.tvBrand);
        TextView tvModel = view.findViewById(R.id.tvModel);
        TextView tvExpectedPrice = view.findViewById(R.id.tvExpectedPrice);
        TextView tvYear = view.findViewById(R.id.tvYear);
        TextView tvOdometer = view.findViewById(R.id.tvOdometer);

        tvBrand.setText(bike.getBrand() != null ? bike.getBrand() : "N/A");
        tvModel.setText(bike.getModel() != null ? bike.getModel() : "N/A");

        String price = bike.getPrice();
        if (price != null && !price.isEmpty()) {
            price = price.replace("₹", "").trim();
            tvExpectedPrice.setText("₹" + price);
        } else {
            tvExpectedPrice.setText("Price on request");
        }

        tvYear.setText(bike.getYear() != null ? bike.getYear() : "N/A");
        tvOdometer.setText(bike.getOdometer() != null && !bike.getOdometer().isEmpty() ?
                bike.getOdometer() : "N/A");
    }

    private String formatPrice(double price) {
        if (price >= 100000) {
            return String.format("%,.0f", price);
        } else {
            return String.format("%.0f", price);
        }
    }

    private void setupFittings(View view) {
        LinearLayout mandatoryContainer = view.findViewById(R.id.fittingsMandatoryContainer);
        LinearLayout additionalContainer = view.findViewById(R.id.fittingsAdditionalContainer);

        if (mandatoryContainer != null) {
            addFittingItem(mandatoryContainer, "Crash Bar", "₹ 850");
            addFittingItem(mandatoryContainer, "Saree Guard", "₹ 450");
            addFittingItem(mandatoryContainer, "Front & Rear Number Plate", "₹ 300");
            addFittingItem(mandatoryContainer, "Side Stand", "₹ 250");
        }

        if (additionalContainer != null) {
            addFittingItem(additionalContainer, "Helmet", "FREE");
            addFittingItem(additionalContainer, "Seat Cover", "₹ 350");
        }
    }

    private void addFittingItem(LinearLayout container, String name, String price) {
        View itemView = LayoutInflater.from(this).inflate(R.layout.item_fitting_detail, container, false);

        TextView tvName = itemView.findViewById(R.id.tvFittingName);
        TextView tvPrice = itemView.findViewById(R.id.tvFittingPrice);

        tvName.setText(name);
        tvPrice.setText(price);

        container.addView(itemView);
    }

    private void editNewBike() {
        Intent intent = new Intent(this, AddBikeActivity.class);
        intent.putExtra("EDIT_MODE", true);
        intent.putExtra("BIKE_ID", bikeId);
        // Pass the updated bike data
        intent.putExtra("BIKE_MODEL", bike);
        startActivityForResult(intent, 1);
    }

    private void editSecondHandBike() {
        Intent intent = new Intent(this, AddSecondHandBikeActivity.class);
        intent.putExtra("EDIT_MODE", true);
        intent.putExtra("BIKE_ID", bikeId);
        intent.putExtra("BIKE_MODEL", bike);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Refresh the data when coming back from edit
            isDataLoaded = false;
            fetchFreshBikeData();
        }
    }

    private void showDeleteConfirmationDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete Bike")
                .setMessage("Are you sure you want to delete this bike? This action cannot be undone.")
                .setPositiveButton("Delete", (d, w) -> deleteBike())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteBike() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Deleting bike...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String token = SharedPrefManager.getInstance(this).getToken();
        if (token == null || token.isEmpty()) {
            progressDialog.dismiss();
            Toast.makeText(this, "Please login again", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = RetrofitClient.getApiService();

        if ("NEW".equalsIgnoreCase(bikeType)) {
            DeleteBikeRequest request = new DeleteBikeRequest(bikeId);
            apiService.deleteBike("Bearer " + token, request)
                    .enqueue(new Callback<GenericResponse>() {
                        @Override
                        public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                            progressDialog.dismiss();
                            if (response.isSuccessful() && response.body() != null) {
                                String status = response.body().getStatus();
                                if ("success".equals(status)) {
                                    Toast.makeText(BikeDetailsActivity.this,
                                            "Bike deleted successfully", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(BikeDetailsActivity.this,
                                            response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(BikeDetailsActivity.this,
                                        "Server error: " + response.code(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<GenericResponse> call, Throwable t) {
                            progressDialog.dismiss();
                            Toast.makeText(BikeDetailsActivity.this,
                                    "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            DeleteBikeRequest request = new DeleteBikeRequest(bikeId);
            apiService.deleteSecondHandBike("Bearer " + token, request)
                    .enqueue(new Callback<GenericResponse>() {
                        @Override
                        public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                            progressDialog.dismiss();
                            if (response.isSuccessful() && response.body() != null) {
                                String status = response.body().getStatus();
                                if ("success".equals(status)) {
                                    Toast.makeText(BikeDetailsActivity.this,
                                            "Bike deleted successfully", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(BikeDetailsActivity.this,
                                            response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(BikeDetailsActivity.this,
                                        "Server error: " + response.code(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<GenericResponse> call, Throwable t) {
                            progressDialog.dismiss();
                            Toast.makeText(BikeDetailsActivity.this,
                                    "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sliderRunnable != null) {
            sliderHandler.removeCallbacks(sliderRunnable);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sliderRunnable != null) {
            sliderHandler.removeCallbacks(sliderRunnable);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (imageUrls.size() > 1 && sliderRunnable != null) {
            sliderHandler.postDelayed(sliderRunnable, 3000);
        }
    }
}