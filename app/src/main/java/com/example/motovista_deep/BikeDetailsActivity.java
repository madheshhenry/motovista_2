package com.example.motovista_deep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import androidx.annotation.NonNull;
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

import com.example.motovista_deep.adapter.ImageSliderAdapter;
import com.example.motovista_deep.models.BikeModel;

import java.util.ArrayList;
import java.util.List;

public class BikeDetailsActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView tvTitle;
    private ViewPager2 imageViewPager;
    private LinearLayout dotsIndicator;
    private LinearLayout detailsContainer;
    private Button btnEdit, btnDelete;

    private BikeModel bike;
    private String bikeType;

    private ImageSliderAdapter imageSliderAdapter;
    private Handler sliderHandler = new Handler();
    private List<String> imageUrls = new ArrayList<>();
    private Runnable sliderRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_details);

        // ---------- GET DATA ----------
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("BIKE_MODEL")) {
            bike = intent.getParcelableExtra("BIKE_MODEL");
            bikeType = intent.getStringExtra("BIKE_TYPE");

            // Debug log
            Toast.makeText(this,
                    "Bike loaded: " + bike.getBrand() + " " + bike.getModel() +
                            "\nImages: " + (bike.getImageUrls() != null ? bike.getImageUrls().size() : 0),
                    Toast.LENGTH_SHORT).show();

        } else {
            // 🔹 SAFE FALLBACK
            bike = new BikeModel();
            bike.setId(1);
            bike.setBrand("Royal Enfield");
            bike.setModel("Classic 350");
            bike.setPrice("2,10,000");
            bike.setCondition("Excellent");
            bike.setType("NEW");
            bike.setIsFeatured(1);

            ArrayList<String> demoImages = new ArrayList<>();
            demoImages.add("https://via.placeholder.com/600x400?text=Bike+1");
            demoImages.add("https://via.placeholder.com/600x400?text=Bike+2");
            demoImages.add("https://via.placeholder.com/600x400?text=Bike+3");

            bike.setAllImages(demoImages);

            bikeType = "NEW";
        }

        initializeViews();
        setupListeners();
        setupImageSlider();
        loadBikeData();
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        tvTitle = findViewById(R.id.tvTitle);
        imageViewPager = findViewById(R.id.imageViewPager);
        dotsIndicator = findViewById(R.id.dotsIndicator);
        detailsContainer = findViewById(R.id.detailsContainer);
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);

        // Set title with bike info
        if (bike != null && bike.getBrand() != null && bike.getModel() != null) {
            tvTitle.setText(bike.getBrand() + " " + bike.getModel());
        } else {
            tvTitle.setText("Bike Details");
        }
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnEdit.setOnClickListener(v ->
                Toast.makeText(this, "Edit Bike Details", Toast.LENGTH_SHORT).show()
        );

        btnDelete.setOnClickListener(v -> showDeleteConfirmationDialog());
    }

    // ---------- IMAGE SLIDER ----------
    // ---------- IMAGE SLIDER ----------
    private void setupImageSlider() {
        imageUrls.clear();

        if (bike != null) {
            // Try to get all images first
            ArrayList<String> allImages = bike.getAllImages();

            if (allImages != null && !allImages.isEmpty()) {
                imageUrls.addAll(allImages);
                Toast.makeText(this, "Found " + allImages.size() + " images", Toast.LENGTH_SHORT).show();
            }
            // Fallback to imageUrls
            else if (bike.getImageUrls() != null && !bike.getImageUrls().isEmpty()) {
                imageUrls.addAll(bike.getImageUrls());
            }
            // Last fallback: single image
            else {
                String singleImage = bike.getImageUrl();
                if (singleImage != null && !singleImage.isEmpty()) {
                    imageUrls.add(singleImage);
                }
            }
        }

        // If still no images, show placeholder
        if (imageUrls.isEmpty()) {
            imageUrls.add("https://via.placeholder.com/600x400?text=No+Image");
            Toast.makeText(this, "No images found", Toast.LENGTH_SHORT).show();
        }

        // Setup ViewPager - ADD THESE LINES FOR FULL IMAGE VIEW
        imageSliderAdapter = new ImageSliderAdapter(this, imageUrls);
        imageViewPager.setAdapter(imageSliderAdapter);

        // 🔥 ADD THESE LINES FOR FULL IMAGE VIEW
        imageViewPager.setClipToPadding(false);
        imageViewPager.setClipChildren(false);
        imageViewPager.setOffscreenPageLimit(3);

        // 🔥 Add page transformer for better visual effect
        imageViewPager.setPageTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float absPosition = Math.abs(position);
                page.setScaleY(0.85f + 0.15f * (1 - absPosition));
            }
        });

        // Setup dots indicator
        setupDotsIndicator(imageUrls.size());

        // Setup page change listener
        imageViewPager.registerOnPageChangeCallback(
                new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {
                        updateDotsIndicator(position);
                        resetAutoSlider();
                    }
                }
        );

        // Auto slide if multiple images
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

    // ---------- DETAILS ----------
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

        ((TextView) view.findViewById(R.id.tvBrand)).setText(bike.getBrand());
        ((TextView) view.findViewById(R.id.tvModel)).setText(bike.getModel());

        // Set price - FIX FOR DOUBLE RUPEES
        String price = bike.getPrice();
        if (price != null && !price.isEmpty()) {
            // Remove any existing rupee symbol and add fresh one
            price = price.replace("₹", "").trim();
            ((TextView) view.findViewById(R.id.tvOnRoadPrice)).setText("₹" + price);
        }

        // Set additional details if available
        if (bike.getOnRoadPrice() != null && !bike.getOnRoadPrice().isEmpty()) {
            String onRoadPrice = bike.getOnRoadPrice();
            onRoadPrice = onRoadPrice.replace("₹", "").trim();
            ((TextView) view.findViewById(R.id.tvOnRoadPrice)).setText("₹" + onRoadPrice);
        }

        if (bike.getEngineCC() != null && !bike.getEngineCC().isEmpty()) {
            ((TextView) view.findViewById(R.id.tvEngine)).setText(bike.getEngineCC());
        }

        if (bike.getMileage() != null && !bike.getMileage().isEmpty()) {
            ((TextView) view.findViewById(R.id.tvMileage)).setText(bike.getMileage());
        }
    }

    private void setupSHBikeDetails(View view) {
        if (bike == null) return;

        ((TextView) view.findViewById(R.id.tvBrand)).setText(bike.getBrand());
        ((TextView) view.findViewById(R.id.tvModel)).setText(bike.getModel());

        // Set price - FIX FOR DOUBLE RUPEES
        String price = bike.getPrice();
        if (price != null && !price.isEmpty()) {
            // Remove any existing rupee symbol and add fresh one
            price = price.replace("₹", "").trim();
            ((TextView) view.findViewById(R.id.tvExpectedPrice)).setText("₹" + price);
        }

        // Set additional details if available
        if (bike.getYear() != null && !bike.getYear().isEmpty()) {
            ((TextView) view.findViewById(R.id.tvYear)).setText(bike.getYear());
        }

        if (bike.getOdometer() != null && !bike.getOdometer().isEmpty()) {
            ((TextView) view.findViewById(R.id.tvOdometer)).setText(bike.getOdometer());
        }
    }

    // ---------- DELETE ----------
    private void showDeleteConfirmationDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Remove Bike")
                .setMessage("Are you sure you want to remove this bike?")
                .setPositiveButton("Remove", (d, w) -> deleteBike())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteBike() {
        Toast.makeText(this, "Bike removed", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, BikeInventoryActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        finish();
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