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
import com.example.motovista_deep.models.BikeModel;
import com.example.motovista_deep.api.ApiService;
import com.example.motovista_deep.api.RetrofitClient;
import com.example.motovista_deep.helpers.SharedPrefManager;
import com.example.motovista_deep.models.GenericResponse;
import com.example.motovista_deep.models.DeleteBikeRequest;

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
        }

        initializeViews();
        setupListeners();
        setupImageSlider();
        loadBikeData();
    }

    // ✅ RENAME THIS METHOD to avoid duplicate
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

    private void debugBikeDetails() {
        Log.d("BIKE_DETAILS_DEBUG", "=== BIKE DETAILS DEBUG ===");
        if (bike != null) {
            Log.d("BIKE_DETAILS_DEBUG", "Bike ID: " + bike.getId());
            Log.d("BIKE_DETAILS_DEBUG", "Bike Brand: " + bike.getBrand());
            Log.d("BIKE_DETAILS_DEBUG", "Bike Model: " + bike.getModel());

            // Check image paths
            Log.d("BIKE_DETAILS_DEBUG", "Image URL from bike: " + bike.getImageUrl());
            Log.d("BIKE_DETAILS_DEBUG", "Cleaned Image URL: " + getCleanImageUrl(bike.getImageUrl()));

            ArrayList<String> allImages = bike.getAllImages();
            if (allImages != null && !allImages.isEmpty()) {
                for (int i = 0; i < allImages.size(); i++) {
                    String original = allImages.get(i);
                    Log.d("BIKE_DETAILS_DEBUG", "Image " + i + " Original: " + original);
                    Log.d("BIKE_DETAILS_DEBUG", "Image " + i + " Cleaned: " + getCleanImageUrl(original));
                }
            }

            // Test if image is accessible
            String testUrl = "http://192.168.0.103/motovista_api/uploads/bikes/6954aa6ccbd79_1767156332.jpg";
            Log.d("BIKE_DETAILS_DEBUG", "Test URL: " + testUrl);
        } else {
            Log.d("BIKE_DETAILS_DEBUG", "Bike object is null!");
        }
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
                // ✅ USE RENAMED METHOD getCleanImageUrl
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

        tvBrand.setText(bike.getBrand());
        tvModel.setText(bike.getModel());
        tvVariant.setText(bike.getFeatures() != null && bike.getFeatures().contains("Variant") ?
                bike.getFeatures().split("Variant:")[1].split(",")[0].trim() : "Standard");
        tvYear.setText("2024");
        tvEngineCC.setText(bike.getEngineCC());
        tvFuelType.setText("Petrol");
        tvTransmission.setText(bike.getType().contains("Manual") ? "Manual" : "Automatic");
        tvBrakingType.setText(bike.getBrakingType());

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
                total = 210350;
            }

            tvExShowroom.setText("₹ " + formatPrice(total * 0.87));
            tvInsurance.setText("₹ " + formatPrice(total * 0.06));
            tvRegistration.setText("₹ " + formatPrice(total * 0.05));
            tvLTRT.setText("₹ " + formatPrice(total * 0.02));
            tvTotalPrice.setText("₹ " + formatPrice(total));
        }

        // Specifications
        TextView tvMileage = view.findViewById(R.id.tvMileage);
        TextView tvFuelTank = view.findViewById(R.id.tvFuelTank);
        TextView tvKerbWeight = view.findViewById(R.id.tvKerbWeight);
        TextView tvSeatHeight = view.findViewById(R.id.tvSeatHeight);
        TextView tvGroundClearance = view.findViewById(R.id.tvGroundClearance);

        tvMileage.setText(bike.getMileage());
        tvFuelTank.setText("11 L");
        tvKerbWeight.setText("142 kg");
        tvSeatHeight.setText("815 mm");
        tvGroundClearance.setText("170 mm");

        // Warranty
        TextView tvWarrantyPeriod = view.findViewById(R.id.tvWarrantyPeriod);
        TextView tvFreeServices = view.findViewById(R.id.tvFreeServices);
        tvWarrantyPeriod.setText("5 Years");
        tvFreeServices.setText("4 Services");

        // Legal Notes
        TextView tvRegistrationProof = view.findViewById(R.id.tvRegistrationProof);
        TextView tvPriceDisclaimer = view.findViewById(R.id.tvPriceDisclaimer);
        tvRegistrationProof.setText("For registration purpose any one of the following is required in ORIGINAL & XEROX for Address Proof: Aadhar Card, LIC Policy, Passport, GST, Voter ID, PAN Card");
        tvPriceDisclaimer.setText("Price & taxes ruling at the time of delivery will be applicable.");

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

    private void setupSHBikeDetails(View view) {
        if (bike == null) return;

        TextView tvBrand = view.findViewById(R.id.tvBrand);
        TextView tvModel = view.findViewById(R.id.tvModel);
        TextView tvExpectedPrice = view.findViewById(R.id.tvExpectedPrice);
        TextView tvYear = view.findViewById(R.id.tvYear);
        TextView tvOdometer = view.findViewById(R.id.tvOdometer);

        tvBrand.setText(bike.getBrand());
        tvModel.setText(bike.getModel());

        String price = bike.getPrice();
        if (price != null && !price.isEmpty()) {
            price = price.replace("₹", "").trim();
            tvExpectedPrice.setText("₹" + price);
        }

        tvYear.setText(bike.getYear());
        tvOdometer.setText(bike.getOdometer());
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
        startActivityForResult(intent, 1);
    }

    private void editSecondHandBike() {
        Intent intent = new Intent(this, AddSecondHandBikeActivity.class);
        intent.putExtra("EDIT_MODE", true);
        intent.putExtra("BIKE_ID", bikeId);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            finish();
            startActivity(getIntent());
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