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
import com.example.motovista_deep.utils.ImageUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
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
        return ImageUtils.getFullImageUrl(url);
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
            // imageUrls.add("https://via.placeholder.com/600x400?text=No+Image");
            imageUrls.add("android.resource://" + getPackageName() + "/" + R.drawable.placeholder_bike);
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

            Call<com.example.motovista_deep.models.GetBikeByIdResponseV2> call = apiService.getBikeByIdV2("Bearer " + token, bikeId);

            call.enqueue(new Callback<com.example.motovista_deep.models.GetBikeByIdResponseV2>() {
                @Override
                public void onResponse(Call<com.example.motovista_deep.models.GetBikeByIdResponseV2> call, Response<com.example.motovista_deep.models.GetBikeByIdResponseV2> response) {
                    progressDialog.dismiss();

                    if (response.isSuccessful() && response.body() != null) {
                        com.example.motovista_deep.models.GetBikeByIdResponseV2 apiResponse = response.body();

                        if ("success".equalsIgnoreCase(apiResponse.status) || "true".equalsIgnoreCase(apiResponse.status)) {
                                if (apiResponse.data != null && apiResponse.data.model != null) {
                                    
                                    // MAP V2 TO V1
                                    com.example.motovista_deep.models.BikeParentModel v2Model = apiResponse.data.model;
                                    List<com.example.motovista_deep.models.BikeVariantModel> v2Variants = apiResponse.data.variants;
                                    
                                    com.example.motovista_deep.models.BikeModel bikeData = new com.example.motovista_deep.models.BikeModel();
                                    
                                    // Basic Map
                                    bikeData.setBrand(v2Model.getBrand());
                                    // bikeData.setModel(v2Model.getModelName()); // existing code uses setModel
                                    // Warning: BikeModel.model is "model" field. BikeParentModel has "modelName".
                                    bikeData.setModel(v2Model.getModelName());
                                    
                                    bikeData.setYear(v2Model.getModelYear());
                                    bikeData.setEngineCC(v2Model.getEngineCC());
                                    bikeData.setFuelType(v2Model.getFuelType());
                                    bikeData.setTransmission(v2Model.getTransmission());
                                    bikeData.setMileage(v2Model.getMileage());
                                    bikeData.setFuelTankCapacity(v2Model.getFuelTankCapacity() != null ? v2Model.getFuelTankCapacity() : ""); // Fix getter name check
                                    bikeData.setKerbWeight(v2Model.getKerbWeight() != null ? v2Model.getKerbWeight() : "");
                                    bikeData.setSeatHeight(v2Model.getSeatHeight() != null ? v2Model.getSeatHeight() : "");
                                    bikeData.setGroundClearance(v2Model.getGroundClearance() != null ? v2Model.getGroundClearance() : "");
                                    bikeData.setMaxTorque(v2Model.getMaxTorque());
                                    bikeData.setWarrantyPeriod(v2Model.getWarrantyPeriod()); // check method
                                    bikeData.setFreeServicesCount(v2Model.getFreeServices() != null ? v2Model.getFreeServices() : ""); // check method
                                    
                                    if (v2Model.getMandatoryFittings() != null) bikeData.setMandatoryFittings(v2Model.getMandatoryFittings());
                                    if (v2Model.getAdditionalFittings() != null) bikeData.setAdditionalFittings(v2Model.getAdditionalFittings());
                                    
                                    // Set Variants
                                    if (v2Variants != null) {
                                        bikeData.setVariants(v2Variants);
                                    }

                                    // Variant Map (Use first variant)
                                    if (v2Variants != null && !v2Variants.isEmpty()) {
                                        com.example.motovista_deep.models.BikeVariantModel v2Variant = v2Variants.get(0);
                                        bikeData.setVariant(v2Variant.variantName);
                                        
                                        if (v2Variant.brakesWheels != null) {
                                            bikeData.setBrakingType(v2Variant.brakesWheels.brakingSystem);
                                            bikeData.setFrontBrake(v2Variant.brakesWheels.frontBrake);
                                            bikeData.setRearBrake(v2Variant.brakesWheels.rearBrake);
                                            bikeData.setWheelType(v2Variant.brakesWheels.wheelType);
                                        }

                                        // Map Prices
                                        if (v2Variant.priceDetails != null) {
                                            bikeData.setExShowroomPrice(v2Variant.priceDetails.exShowroom);
                                            bikeData.setInsurance(v2Variant.priceDetails.insurance);
                                            bikeData.setRegistrationCharge(v2Variant.priceDetails.registration);
                                            bikeData.setLtrt(v2Variant.priceDetails.ltrt);
                                            bikeData.setOnRoadPrice(v2Variant.priceDetails.totalOnRoad);
                                        }
                                        
                                        // Map Images
                                        ArrayList<String> images = new ArrayList<>();
                                        ArrayList<String> colorNames = new ArrayList<>();
                                        if (v2Variant.colors != null) {
                                            for(com.example.motovista_deep.models.BikeVariantModel.VariantColor c : v2Variant.colors) {
                                                String colorEntry = c.colorName;
                                                if (c.colorHex != null && !c.colorHex.isEmpty()) {
                                                    colorEntry += "|" + c.colorHex;
                                                }
                                                if(colorEntry != null) colorNames.add(colorEntry);
                                                
                                                if (c.imagePaths != null) images.addAll(c.imagePaths);
                                            }
                                        }
                                        bikeData.setColors(colorNames);
                                        bikeData.setAllImages(images); 
                                    }
                                    
                                    updateBikeModelWithApiData(bikeData);
                                    loadBikeData();
                                    isDataLoaded = true;
                                } else {
                                Log.e("BIKE_DETAILS_API", "BikeData is null in response");
                                Toast.makeText(BikeDetailsActivity.this,
                                        "No bike data found", Toast.LENGTH_SHORT).show();
                                loadBikeDataWithExistingData();
                            }
                        } else {
                            String msg = apiResponse.status != null ? apiResponse.status : "Unknown error";
                            Log.e("BIKE_DETAILS_API", "API returned error: " + msg);
                            Toast.makeText(BikeDetailsActivity.this,
                                    msg, Toast.LENGTH_SHORT).show();
                            loadBikeDataWithExistingData();
                        }
                    } else {
                        Log.e("BIKE_DETAILS_API", "Response not successful. Code: " + response.code());
                        Toast.makeText(BikeDetailsActivity.this,
                                "Failed to fetch bike details", Toast.LENGTH_SHORT).show();
                        loadBikeDataWithExistingData();
                    }
                }

                @Override
                public void onFailure(Call<com.example.motovista_deep.models.GetBikeByIdResponseV2> call, Throwable t) {
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

    private void updateBikeModelWithApiData(com.example.motovista_deep.models.BikeModel bikeData) {
        if (bikeData != null && bike != null) {
            // Update the bike object with fresh data from API
            bike.setDate(bikeData.getDate());
            // bike.setEngine_number(bikeData.getEngine_number());
            // bike.setChassis_number(bikeData.getChassis_number());
            bike.setMaxTorque(bikeData.getMaxTorque());

            // Also update other fields that might be missing
            bike.setVariant(bikeData.getVariant());
            bike.setYear(bikeData.getYear());
            bike.setEngineCC(bikeData.getEngineCC());
            bike.setFuelType(bikeData.getFuelType());
            bike.setTransmission(bikeData.getTransmission());
            bike.setBrakingType(bikeData.getBrakingType());
            bike.setOnRoadPrice(bikeData.getOnRoadPrice());
            bike.setExShowroomPrice(bikeData.getExShowroomPrice()); // Added
            bike.setInsurance(bikeData.getInsurance()); // Added
            bike.setRegistrationCharge(bikeData.getRegistrationCharge()); // Added
            bike.setLtrt(bikeData.getLtrt()); // Added
            bike.setMileage(bikeData.getMileage());
            bike.setFuelTankCapacity(bikeData.getFuelTankCapacity());
            bike.setKerbWeight(bikeData.getKerbWeight());
            bike.setSeatHeight(bikeData.getSeatHeight());
            bike.setGroundClearance(bikeData.getGroundClearance());
            bike.setWarrantyPeriod(bikeData.getWarrantyPeriod());
            bike.setFreeServicesCount(bikeData.getFreeServicesCount());
            bike.setRegistrationProof(bikeData.getRegistrationProof());
            bike.setPriceDisclaimer(bikeData.getPriceDisclaimer());

            bike.setPriceDisclaimer(bikeData.getPriceDisclaimer());

            // Update Collections with logging
            Log.d("BIKE_DETAILS_API", "Colors size: " + (bikeData.getColors() != null ? bikeData.getColors().size() : "null"));
            Log.d("BIKE_DETAILS_API", "Custom Fittings size: " + (bikeData.getCustomFittings() != null ? bikeData.getCustomFittings().size() : "null"));
            Log.d("BIKE_DETAILS_API", "Mandatory Fittings size: " + (bikeData.getMandatoryFittings() != null ? bikeData.getMandatoryFittings().size() : "null"));
            Log.d("BIKE_DETAILS_API", "Additional Fittings size: " + (bikeData.getAdditionalFittings() != null ? bikeData.getAdditionalFittings().size() : "null"));
            Log.d("BIKE_DETAILS_API", "Images: " + (bikeData.getAllImages() != null ? bikeData.getAllImages().size() : "null"));

            bike.setColors(bikeData.getColors());
            bike.setCustomFittings(bikeData.getCustomFittings());
            bike.setMandatoryFittings(bikeData.getMandatoryFittings());
            bike.setAdditionalFittings(bikeData.getAdditionalFittings());
            bike.setVariants(bikeData.getVariants()); // Added
            bike.setAllImages(bikeData.getAllImages()); // Added copy images

            // Set the price for display
            if (bikeData.getOnRoadPrice() != null && !bikeData.getOnRoadPrice().isEmpty()) {
                try {
                    double price = Double.parseDouble(bikeData.getOnRoadPrice());
                    bike.setPrice(String.valueOf(price));
                } catch (NumberFormatException e) {
                    // Keep existing price
                }
            }
            
            // Refresh Image Slider
            setupImageSlider();
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
        // TextView tvVariant = view.findViewById(R.id.tvVariant); // Removed
        TextView tvYear = view.findViewById(R.id.tvYear);
        TextView tvEngineCC = view.findViewById(R.id.tvEngineCC);
        TextView tvFuelType = view.findViewById(R.id.tvFuelType);
        TextView tvTransmission = view.findViewById(R.id.tvTransmission);

        // Setup Variant RecyclerView
        androidx.recyclerview.widget.RecyclerView rvVariants = view.findViewById(R.id.rvVariants);
        rvVariants.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false));

        if (bike.getVariants() != null && !bike.getVariants().isEmpty()) {
            com.example.motovista_deep.adapter.VariantAdapter adapter = new com.example.motovista_deep.adapter.VariantAdapter(this, bike.getVariants(), (variant, position) -> {
                updateBikeDetailsForVariant(view, variant);
            });
            rvVariants.setAdapter(adapter);

            // Set initial selection
            if (adapter.getItemCount() > 0) {
                 // Trigger update for first item
                 updateBikeDetailsForVariant(view, bike.getVariants().get(0));
            }
        }
        // Braking Type removed from layout
        // TextView tvBrakingType = view.findViewById(R.id.tvBrakingType);

        // Colors
        LinearLayout llColorsSection = view.findViewById(R.id.llColorsSection);
        LinearLayout colorsContainer = view.findViewById(R.id.colorsContainer);
        if (bike.getColors() != null && !bike.getColors().isEmpty()) {
            llColorsSection.setVisibility(View.VISIBLE);
            if (colorsContainer != null) {
                colorsContainer.removeAllViews();
                for (String color : bike.getColors()) {
                    addColorItem(colorsContainer, color);
                }
            }
        } else {
            if (llColorsSection != null) llColorsSection.setVisibility(View.GONE);
        }

        // NEW FIELDS: Add TextViews for date and max torque
        TextView tvDate = view.findViewById(R.id.tvDate);
        TextView tvMaxTorque = view.findViewById(R.id.tvMaxTorque);
        // Removed Engine/Chassis bindings

        // Set values with proper null checks
        tvBrand.setText(bike.getBrand() != null ? bike.getBrand() : "N/A");
        tvModel.setText(bike.getModel() != null ? bike.getModel() : "N/A");
        // tvVariant removed
        tvYear.setText(bike.getYear() != null && !bike.getYear().isEmpty() ?
                bike.getYear() : "2024");
        tvEngineCC.setText(bike.getEngineCC() != null && !bike.getEngineCC().isEmpty() ?
                bike.getEngineCC() : "N/A");
        tvFuelType.setText(bike.getFuel_type() != null && !bike.getFuel_type().isEmpty() ?
                bike.getFuel_type() : "Petrol");
        tvTransmission.setText(bike.getTransmission() != null && !bike.getTransmission().isEmpty() ?
                bike.getTransmission() : "Manual");
        // tvBrakingType.setText(bike.getBrakingType() != null && !bike.getBrakingType().isEmpty() ?
        //        bike.getBrakingType() : "N/A");

        // SET NEW FIELDS
        String date = bike.getDate();
        String maxTorque = bike.getMaxTorque();

        Log.d("BIKE_DETAILS_UI", "Date: " + date);
        Log.d("BIKE_DETAILS_UI", "Max Torque: " + maxTorque);

        tvDate.setText(date != null && !date.isEmpty() ? date : "Not specified");
        tvMaxTorque.setText(maxTorque != null && !maxTorque.isEmpty() ? maxTorque : "N/A");

        // Price Configuration
        TextView tvExShowroom = view.findViewById(R.id.tvExShowroom);
        TextView tvInsurance = view.findViewById(R.id.tvInsurance);
        TextView tvRegistration = view.findViewById(R.id.tvRegistration);
        TextView tvLTRT = view.findViewById(R.id.tvLTRT);
        TextView tvTotalPrice = view.findViewById(R.id.tvTotalPrice);

        // Display stored values if available, otherwise fallback to calculation
        if (bike.getExShowroomPrice() != null && !bike.getExShowroomPrice().isEmpty()) {
            tvExShowroom.setText("₹ " + bike.getExShowroomPrice());
        } else {
            // Fallback logic
            tvExShowroom.setText("₹ " + formatPrice(getSafeOnRoadPrice(bike) * 0.87));
        }

        if (bike.getInsurance() != null && !bike.getInsurance().isEmpty()) {
            tvInsurance.setText("₹ " + bike.getInsurance());
        } else {
             tvInsurance.setText("₹ " + formatPrice(getSafeOnRoadPrice(bike) * 0.06));
        }

        if (bike.getRegistrationCharge() != null && !bike.getRegistrationCharge().isEmpty()) {
            tvRegistration.setText("₹ " + bike.getRegistrationCharge());
        } else {
             tvRegistration.setText("₹ " + formatPrice(getSafeOnRoadPrice(bike) * 0.05));
        }

        if (bike.getLtrt() != null && !bike.getLtrt().isEmpty()) {
            tvLTRT.setText("₹ " + bike.getLtrt());
        } else {
             tvLTRT.setText("₹ " + formatPrice(getSafeOnRoadPrice(bike) * 0.02));
        }
        
        // Total Price
        String onRoad = bike.getOnRoadPrice();
        if (onRoad != null && !onRoad.isEmpty()) {
             tvTotalPrice.setText("₹ " + onRoad);
        } else {
             tvTotalPrice.setText(bike.getPrice() != null ? bike.getPrice() : "N/A");
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
        // Using layout values which match AddBikeActivity
        // tvAccountName.setText("SANTHOSH BIKES");
        // tvAccountNumber.setText("75010200000585");
        // tvIFSC.setText("BARBOVJAVAD");

        // Setup fittings
        setupFittings(view);
    }

    private void updateBikeDetailsForVariant(View view, com.example.motovista_deep.models.BikeVariantModel variant) {
         // Update Colors
        LinearLayout llColorsSection = view.findViewById(R.id.llColorsSection);
        LinearLayout colorsContainer = view.findViewById(R.id.colorsContainer);
        
        java.util.List<String> colorNames = new java.util.ArrayList<>();
         if (variant.colors != null) {
            for(com.example.motovista_deep.models.BikeVariantModel.VariantColor c : variant.colors) {
                String colorEntry = c.colorName;
                if (c.colorHex != null && !c.colorHex.isEmpty()) {
                    colorEntry += "|" + c.colorHex;
                }
                if(colorEntry != null) colorNames.add(colorEntry);
            }
        }
        
        if (!colorNames.isEmpty()) {
            llColorsSection.setVisibility(View.VISIBLE);
            if (colorsContainer != null) {
                colorsContainer.removeAllViews();
                for (String color : colorNames) {
                    addColorItem(colorsContainer, color);
                }
            }
        } else {
             if (llColorsSection != null) llColorsSection.setVisibility(View.GONE);
        }
        
        // Update Price
        if (variant.priceDetails != null) {
             updatePriceDisplay(view, 
                variant.priceDetails.exShowroom, 
                variant.priceDetails.insurance, 
                variant.priceDetails.registration, 
                variant.priceDetails.ltrt, 
                variant.priceDetails.totalOnRoad);
        }

        // Update Images
        java.util.ArrayList<String> newImages = new java.util.ArrayList<>();
        if (variant.colors != null) {
            for(com.example.motovista_deep.models.BikeVariantModel.VariantColor c : variant.colors) {
                if (c.imagePaths != null) newImages.addAll(c.imagePaths);
            }
        }
        if (!newImages.isEmpty()) {
            bike.setAllImages(newImages);
            bike.setImageUrls(newImages); // Ensure compatibility
            setupImageSlider();
        }

        // Update Brakes & Wheels
        TextView tvFrontBrake = view.findViewById(R.id.tvFrontBrake);
        TextView tvRearBrake = view.findViewById(R.id.tvRearBrake);
        TextView tvBrakingSystem = view.findViewById(R.id.tvBrakingSystem);
        TextView tvWheelType = view.findViewById(R.id.tvWheelType);

        if (variant.brakesWheels != null) {
            tvFrontBrake.setText(variant.brakesWheels.frontBrake != null ? variant.brakesWheels.frontBrake : "N/A");
            tvRearBrake.setText(variant.brakesWheels.rearBrake != null ? variant.brakesWheels.rearBrake : "N/A");
            tvBrakingSystem.setText(variant.brakesWheels.brakingSystem != null ? variant.brakesWheels.brakingSystem : "N/A");
            tvWheelType.setText(variant.brakesWheels.wheelType != null ? variant.brakesWheels.wheelType : "N/A");
        }

        // Update Custom Sections
        LinearLayout llCustomSections = view.findViewById(R.id.llCustomSectionsContainer);
        if (llCustomSections != null) {
            llCustomSections.removeAllViews();
            if (variant.customSections != null) {
                for (com.example.motovista_deep.models.BikeVariantModel.CustomSection section : variant.customSections) {
                    addCustomSectionToView(llCustomSections, section);
                }
            }
        }
    }

    private void addCustomSectionToView(LinearLayout container, com.example.motovista_deep.models.BikeVariantModel.CustomSection section) {
        // Section Container
        LinearLayout sectionLayout = new LinearLayout(this);
        sectionLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = 24; // dp conversion ideally needed
        sectionLayout.setLayoutParams(lp);

        // Title
        TextView tvTitle = new TextView(this);
        tvTitle.setText(section.sectionName);
        tvTitle.setTextSize(12); // sp
        tvTitle.setTextColor(getResources().getColor(R.color.gray_500));
        tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        titleParams.bottomMargin = 16;
        tvTitle.setLayoutParams(titleParams);
        sectionLayout.addView(tvTitle);

        // Grid (Using GridLayout for 2 columns)
        android.widget.GridLayout grid = new android.widget.GridLayout(this);
        grid.setColumnCount(2);
        grid.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        
        if (section.fields != null) {
            for (com.example.motovista_deep.models.BikeVariantModel.CustomField field : section.fields) {
                // Key View (Left Column)
                TextView tvKey = new TextView(this);
                tvKey.setText(field.key);
                tvKey.setTextSize(14);
                tvKey.setTextColor(android.graphics.Color.parseColor("#111318"));
                tvKey.setTypeface(null, android.graphics.Typeface.BOLD);
                tvKey.setBackgroundResource(R.drawable.bg_input_new);
                tvKey.setPadding(24, 24, 24, 24); // px
                
                android.widget.GridLayout.LayoutParams keyParams = new android.widget.GridLayout.LayoutParams();
                keyParams.width = 0;
                keyParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                keyParams.columnSpec = android.widget.GridLayout.spec(android.widget.GridLayout.UNDEFINED, 1f);
                keyParams.bottomMargin = 24;
                keyParams.rightMargin = 12; // Gap
                tvKey.setLayoutParams(keyParams);
                
                grid.addView(tvKey);

                // Value View (Right Column)
                TextView tvValue = new TextView(this);
                tvValue.setText(field.value);
                tvValue.setTextSize(14);
                tvValue.setTextColor(android.graphics.Color.parseColor("#111318"));
                tvValue.setTypeface(null, android.graphics.Typeface.BOLD);
                tvValue.setBackgroundResource(R.drawable.bg_input_new);
                tvValue.setPadding(24, 24, 24, 24); // px

                android.widget.GridLayout.LayoutParams valueParams = new android.widget.GridLayout.LayoutParams();
                valueParams.width = 0;
                valueParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                valueParams.columnSpec = android.widget.GridLayout.spec(android.widget.GridLayout.UNDEFINED, 1f);
                valueParams.bottomMargin = 24;
                valueParams.leftMargin = 12; // Gap
                tvValue.setLayoutParams(valueParams);

                grid.addView(tvValue);
            }
        }

        sectionLayout.addView(grid);
        container.addView(sectionLayout);
    }
    
    private void updatePriceDisplay(View view, String ex, String ins, String reg, String ltrt, String total) {
        TextView tvExShowroom = view.findViewById(R.id.tvExShowroom);
        TextView tvInsurance = view.findViewById(R.id.tvInsurance);
        TextView tvRegistration = view.findViewById(R.id.tvRegistration);
        TextView tvLTRT = view.findViewById(R.id.tvLTRT);
        TextView tvTotalPrice = view.findViewById(R.id.tvTotalPrice);
        
        tvExShowroom.setText("₹ " + (ex != null ? ex : "0.00"));
        tvInsurance.setText("₹ " + (ins != null ? ins : "0.00"));
        tvRegistration.setText("₹ " + (reg != null ? reg : "0.00"));
        tvLTRT.setText("₹ " + (ltrt != null ? ltrt : "0.00"));
        tvTotalPrice.setText("₹ " + (total != null ? total : "0.00"));
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
        TextView tvOwnership = view.findViewById(R.id.tvOwnership);
        TextView tvCondition = view.findViewById(R.id.tvCondition);
        TextView tvConditionDetails = view.findViewById(R.id.tvConditionDetails);
        TextView tvEngine = view.findViewById(R.id.tvEngine);
        TextView tvBrakes = view.findViewById(R.id.tvBrakes);
        TextView tvAdditionalFeatures = view.findViewById(R.id.tvAdditionalFeatures);
        TextView tvOwnerDetails = view.findViewById(R.id.tvOwnerDetails);

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
        
        tvOwnership.setText(bike.getOwnership() != null && !bike.getOwnership().isEmpty() ?
                bike.getOwnership() : "N/A");
        
        // Show condition (Excellent/Good)
        String condition = bike.getCondition();
        if (condition == null || condition.isEmpty()) {
            condition = "Good"; 
        }
        tvCondition.setText(condition);

        tvConditionDetails.setText(bike.getConditionDetails() != null && !bike.getConditionDetails().isEmpty() ?
                bike.getConditionDetails() : "No details provided.");

        tvEngine.setText(bike.getEngineCC() != null && !bike.getEngineCC().isEmpty() ?
                bike.getEngineCC() : "N/A");

        tvBrakes.setText(bike.getBrakingType() != null && !bike.getBrakingType().isEmpty() ?
                bike.getBrakingType() : "N/A");

        tvAdditionalFeatures.setText(bike.getFeatures() != null && !bike.getFeatures().isEmpty() ?
                bike.getFeatures() : "No additional features specified.");

        tvOwnerDetails.setText(bike.getOwnerDetails() != null && !bike.getOwnerDetails().isEmpty() ?
                bike.getOwnerDetails() : "Contact Admin for details.");
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

        if (bike == null) return;

        if (mandatoryContainer != null) {
            mandatoryContainer.removeAllViews();
            if (bike.getMandatoryFittings() != null) {
                for (com.example.motovista_deep.models.CustomFitting fitting : bike.getMandatoryFittings()) {
                    addFittingCheckboxItem(mandatoryContainer, fitting.getName(), false);
                }
            }
        }

        if (additionalContainer != null) {
            additionalContainer.removeAllViews();
            
            boolean helmetFound = false;

            // Combine standard additional and custom fittings
            List<com.example.motovista_deep.models.CustomFitting> allAdditional = new ArrayList<>();
            if (bike.getAdditionalFittings() != null) allAdditional.addAll(bike.getAdditionalFittings());
            if (bike.getCustomFittings() != null) allAdditional.addAll(bike.getCustomFittings());

            for (com.example.motovista_deep.models.CustomFitting fitting : allAdditional) {
                if (fitting.getName().equalsIgnoreCase("Helmet")) {
                    helmetFound = true;
                    addFittingCheckboxItem(additionalContainer, fitting.getName(), true);
                } else {
                    addFittingCheckboxItem(additionalContainer, fitting.getName(), false);
                }
            }

            // Force "Helmet" display if not present (per user request "always helmet varanum")
            if (!helmetFound) {
                 addFittingCheckboxItem(additionalContainer, "Helmet", true);
            }
        }
    }

    private void addColorItem(LinearLayout container, String colorData) {
        // Expected format: "Name|#HexCode" (e.g. "Red|#FF0000") or just "Name"
        String colorName = colorData;
        String colorHex = "#CCCCCC"; // Default gray

        if (colorData.contains("|")) {
            String[] parts = colorData.split("\\|");
            if (parts.length > 0) colorName = parts[0];
            if (parts.length > 1) colorHex = parts[1];
        }

        LinearLayout itemLayout = new LinearLayout(this);
        itemLayout.setOrientation(LinearLayout.VERTICAL);
        itemLayout.setGravity(android.view.Gravity.CENTER);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(16, 0, 16, 0);
        itemLayout.setLayoutParams(layoutParams);

        // Color Circle
        View colorCircle = new View(this);
        int size = (int) (40 * getResources().getDisplayMetrics().density); // 40dp
        LinearLayout.LayoutParams circleParams = new LinearLayout.LayoutParams(size, size);
        circleParams.bottomMargin = (int) (4 * getResources().getDisplayMetrics().density);
        colorCircle.setLayoutParams(circleParams);

        // Create rounded background dynamically
        android.graphics.drawable.GradientDrawable drawable = new android.graphics.drawable.GradientDrawable();
        drawable.setShape(android.graphics.drawable.GradientDrawable.OVAL);
        try {
            drawable.setColor(android.graphics.Color.parseColor(colorHex));
        } catch (IllegalArgumentException e) {
            drawable.setColor(android.graphics.Color.LTGRAY);
        }
        // Add border
        drawable.setStroke(2, android.graphics.Color.LTGRAY);
        
        colorCircle.setBackground(drawable);

        // Color Name
        TextView tvName = new TextView(this);
        tvName.setText(colorName);
        tvName.setTextColor(getResources().getColor(R.color.text_dark));
        tvName.setTextSize(12);
        tvName.setTypeface(null, android.graphics.Typeface.BOLD);
        tvName.setGravity(android.view.Gravity.CENTER);

        itemLayout.addView(colorCircle);
        itemLayout.addView(tvName);

        container.addView(itemLayout);
    }

    private void addFittingCheckboxItem(LinearLayout container, String name, boolean isFree) {
        View itemView = LayoutInflater.from(this).inflate(R.layout.item_fitting_checkbox, container, false);

        TextView tvName = itemView.findViewById(R.id.tvFittingName);
        TextView tvBadge = itemView.findViewById(R.id.tvFittingBadge);
        
        tvName.setText(name);
        
        if (isFree || name.equalsIgnoreCase("Helmet")) {
            tvBadge.setVisibility(View.VISIBLE);
            tvBadge.setText("FREE");
        } else {
            tvBadge.setVisibility(View.GONE);
        }

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
        fetchFreshBikeData();
        if (imageUrls.size() > 1 && sliderRunnable != null) {
            sliderHandler.postDelayed(sliderRunnable, 3000);
        }
    }
    private double getSafeOnRoadPrice(com.example.motovista_deep.models.BikeModel bike) {
        if (bike.getOnRoadPrice() == null || bike.getOnRoadPrice().isEmpty()) {
            return 0.0;
        }
        try {
            return Double.parseDouble(bike.getOnRoadPrice().replace(",", ""));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}