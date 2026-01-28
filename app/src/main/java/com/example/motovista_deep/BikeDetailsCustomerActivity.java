package com.example.motovista_deep;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.motovista_deep.adapter.BikeImageAdapter;
import com.example.motovista_deep.api.RetrofitClient;
import com.example.motovista_deep.models.BikeParentModel;
import com.example.motovista_deep.models.BikeVariantModel;
import com.example.motovista_deep.models.GetBikeByIdResponseV2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BikeDetailsCustomerActivity extends AppCompatActivity {

    private int bikeId;
    
    // UI Components
    private ViewPager2 vpBikeImages;
    private TabLayout tlImageIndicator;
    private ImageView btnBack;
    
    private TextView tvHeaderTitle, tvHeaderSubtitle;
    private TextView tvModelName, tvModelDetails, tvPrice;
    
    private RecyclerView rvVariants;
    private LinearLayout llColorsSection, colorsContainer;
    private TextView tvSelectedColorName;
    
    // Specs
    private TextView tvEngineCC, tvMileage, tvBrakingType, tvTransmission;
    private TextView tvFrontBrake, tvRearBrake, tvBrakingSystemDetail, tvWheelType;
    private TextView tvTankCapacity, tvKerbWeight, tvSeatHeight, tvGroundClearance;
    private TextView tvMaxPower, tvMaxTorque, tvWarranty, tvFreeService;
    
    // Price Breakup
    private TextView tvExShowroom, tvInsurance, tvRegistration, tvLTRT, tvTotalBreakdown;
    
    private Button btnViewInvoice, btnOrderNow;
    private LinearLayout llCustomSectionsContainer;

    // Data
    private BikeParentModel parentModel;
    private List<BikeVariantModel> variants = new ArrayList<>();
    private BikeVariantModel selectedVariant;
    private VariantAdapter variantAdapter;
    private BikeImageAdapter imageAdapter;
    private TabLayoutMediator imageMediator;
    private List<String> currentImagePaths = new ArrayList<>();
    private List<BikeVariantModel.VariantColor> currentColorList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_details_customer_v2);

        bikeId = getIntent().getIntExtra("bike_id", -1);
        if (bikeId == -1) {
            Toast.makeText(this, "Invalid Bike ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        fetchBikeDetails();

        btnBack.setOnClickListener(v -> finish());
        
        btnViewInvoice.setOnClickListener(v -> navigateToInvoiceSelection());
        btnOrderNow.setOnClickListener(v -> navigateToInvoiceSelection());
    }

    private void navigateToInvoiceSelection() {
        if (parentModel == null || selectedVariant == null) {
            Toast.makeText(this, "Data is still loading, please wait...", Toast.LENGTH_SHORT).show();
            return;
        }

        com.example.motovista_deep.models.BikeModel bike = new com.example.motovista_deep.models.BikeModel();
        bike.setId(bikeId);
        bike.setBrand(parentModel.getBrand());
        bike.setModel(parentModel.getModelName());
        bike.setVariant(selectedVariant.variantName);
        
        String price = (selectedVariant.priceDetails != null) ? selectedVariant.priceDetails.totalOnRoad : "0";
        bike.setOnRoadPrice(price);
        bike.setPrice(price);
        
        bike.setWarrantyPeriod(parentModel.getWarrantyPeriod());
        bike.setFreeServicesCount(parentModel.getFreeServices());
        
        if (parentModel.getLegalNotes() != null) {
            bike.setPriceDisclaimer(parentModel.getLegalNotes().priceDisclaimer);
            bike.setRegistrationProof(parentModel.getLegalNotes().regProof);
        }

        bike.setMandatoryFittings(parentModel.getMandatoryFittings());
        bike.setAdditionalFittings(parentModel.getAdditionalFittings());

        // Get image for selected color
        String rawColor = tvSelectedColorName.getText().toString();
        String selectedImage = null;
        if (selectedVariant.colors != null) {
            for (com.example.motovista_deep.models.BikeVariantModel.VariantColor c : selectedVariant.colors) {
                if (c.colorName.equalsIgnoreCase(rawColor)) {
                    if (c.imagePaths != null && !c.imagePaths.isEmpty()) {
                        selectedImage = c.imagePaths.get(0);
                    }
                    break;
                }
            }
        }
        bike.setImageUrl(selectedImage);

        android.content.Intent intent = new android.content.Intent(this, InvoiceSelectionActivity.class);
        intent.putExtra("BIKE_DATA", bike);
        intent.putExtra("SELECTED_COLOR", rawColor);
        startActivity(intent);
    }

    private void sendOrderRequest() {
        com.example.motovista_deep.models.User user = com.example.motovista_deep.helpers.SharedPrefManager.getInstance(this).getUser();
        if (user == null) {
            Toast.makeText(this, "Please Login to continue", Toast.LENGTH_SHORT).show();
            return;
        }

        android.app.ProgressDialog pd = new android.app.ProgressDialog(this);
        pd.setMessage("Placing Order...");
        pd.setCancelable(false);
        pd.show();

        String bikeName = parentModel.getModelName();
        String variantName = selectedVariant.variantName;
        String rawColor = tvSelectedColorName.getText().toString();
        String colorName = (rawColor.isEmpty() || llColorsSection.getVisibility() != View.VISIBLE) ? "Standard" : rawColor;
        
        String price = tvTotalBreakdown.getText().toString().replace("₹ ", ""); // Clean price

        // Use CustomerRequest model which triggers add_customer_request.php
        // Note: CustomerRequest constructor params order: 
        // customer_id, customer_name, customer_phone, customer_profile, bike_id, bike_name, bike_variant, bike_color, bike_price
        
        com.example.motovista_deep.models.CustomerRequest request = new com.example.motovista_deep.models.CustomerRequest(
            user.getId(),
            user.getFull_name(),
            user.getPhone(),
            user.getProfile_image(), // Can be null/empty
            bikeId,
            bikeName,
            variantName,
            colorName,
            price,
            null // selected_fittings
        );

        RetrofitClient.getApiService().addCustomerRequest(request).enqueue(new Callback<com.example.motovista_deep.models.RequestResponse>() {
            @Override
            public void onResponse(Call<com.example.motovista_deep.models.RequestResponse> call, Response<com.example.motovista_deep.models.RequestResponse> response) {
                pd.dismiss();
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    String orderId = response.body().getOrderId();
                    navigateToSuccess(bikeName, variantName, colorName, price, orderId);
                } else {
                    String msg = response.body() != null ? response.body().getMessage() : "Failed to place order";
                    Toast.makeText(BikeDetailsCustomerActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<com.example.motovista_deep.models.RequestResponse> call, Throwable t) {
                pd.dismiss();
                Toast.makeText(BikeDetailsCustomerActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToSuccess(String name, String variant, String color, String price, String orderId) {
        android.content.Intent intent = new android.content.Intent(this, RequestSentActivity.class);
        intent.putExtra("BIKE_NAME", name);
        intent.putExtra("BIKE_VARIANT", variant);
        intent.putExtra("BIKE_Color", color);
        intent.putExtra("BIKE_PRICE", "₹" + price);
        intent.putExtra("ORDER_ID", orderId);
        startActivity(intent);
        finish(); // Close details screen so back goes to catalog/home
    }

    private void initViews() {
        vpBikeImages = findViewById(R.id.vpBikeImages);
        tlImageIndicator = findViewById(R.id.tlImageIndicator);
        btnBack = findViewById(R.id.btnBack);
        
        tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        tvHeaderSubtitle = findViewById(R.id.tvHeaderSubtitle);
        
        tvModelName = findViewById(R.id.tvModelName);
        tvModelDetails = findViewById(R.id.tvModelDetails);
        tvPrice = findViewById(R.id.tvPrice);
        
        rvVariants = findViewById(R.id.rvVariants);
        llColorsSection = findViewById(R.id.llColorsSection);
        colorsContainer = findViewById(R.id.colorsContainer);
        tvSelectedColorName = findViewById(R.id.tvSelectedColorName);
        
        // Specs
        tvEngineCC = findViewById(R.id.tvEngineCC);
        tvMileage = findViewById(R.id.tvMileage);
        tvBrakingType = findViewById(R.id.tvBrakingType);
        tvTransmission = findViewById(R.id.tvTransmission);
        
        tvFrontBrake = findViewById(R.id.tvFrontBrake);
        tvRearBrake = findViewById(R.id.tvRearBrake);
        tvBrakingSystemDetail = findViewById(R.id.tvBrakingSystemDetail);
        tvWheelType = findViewById(R.id.tvWheelType);
        
        tvTankCapacity = findViewById(R.id.tvTankCapacity);
        tvKerbWeight = findViewById(R.id.tvKerbWeight);
        tvSeatHeight = findViewById(R.id.tvSeatHeight);
        tvGroundClearance = findViewById(R.id.tvGroundClearance);
        
        // New Fields
        tvMaxPower = findViewById(R.id.tvMaxPower);
        tvMaxTorque = findViewById(R.id.tvMaxTorque);
        tvWarranty = findViewById(R.id.tvWarranty);
        tvFreeService = findViewById(R.id.tvFreeService);
        
        // Prices
        tvExShowroom = findViewById(R.id.tvExShowroom);
        tvInsurance = findViewById(R.id.tvInsurance);
        tvRegistration = findViewById(R.id.tvRegistration);
        tvLTRT = findViewById(R.id.tvLTRT);
        tvTotalBreakdown = findViewById(R.id.tvTotalBreakdown);
        
        llCustomSectionsContainer = findViewById(R.id.llCustomSectionsContainer);
        
        btnViewInvoice = findViewById(R.id.btnViewInvoice);
        btnOrderNow = findViewById(R.id.btnOrderNow);
    }

    private void fetchBikeDetails() {
        String token = com.example.motovista_deep.helpers.SharedPrefManager.getInstance(this).getToken();
        
        RetrofitClient.getApiService().getBikeByIdV2(token != null ? "Bearer " + token : null, bikeId)
            .enqueue(new Callback<GetBikeByIdResponseV2>() {
                @Override
                public void onResponse(Call<GetBikeByIdResponseV2> call, Response<GetBikeByIdResponseV2> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                        parentModel = response.body().data.model;
                        variants = response.body().data.variants != null ? response.body().data.variants : new ArrayList<>();
                        
                        setupParentData();
                        setupVariantSelector();
                        
                        if (!variants.isEmpty()) {
                            selectVariant(variants.get(0));
                        }
                    } else {
                        Toast.makeText(BikeDetailsCustomerActivity.this, "Failed to load details", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<GetBikeByIdResponseV2> call, Throwable t) {
                    Toast.makeText(BikeDetailsCustomerActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void setupParentData() {
        if (parentModel == null) return;
        
        String fullName = parentModel.getBrand() + " " + parentModel.getModelName();
        tvHeaderTitle.setText(fullName);
        tvHeaderSubtitle.setText(parentModel.getBrand() + " · " + parentModel.getModelYear());
        
        // Default Model Details if needed independent of variant
    }

    private void setupVariantSelector() {
        variantAdapter = new VariantAdapter(variants, variant -> selectVariant(variant));
        rvVariants.setAdapter(variantAdapter);
    }

    private void selectVariant(BikeVariantModel variant) {
        selectedVariant = variant;
        variantAdapter.notifyDataSetChanged(); // Refresh selection state
        
        // Update UI
        tvModelName.setText(parentModel.getModelName() + " - " + variant.variantName);
        tvModelDetails.setText(parentModel.getEngineCC() + "cc · " + parentModel.getFuelType() + " · " + parentModel.getTransmission());
        
        // Price
        String onRoad = variant.priceDetails != null ? variant.priceDetails.totalOnRoad : "N/A";
        tvPrice.setText("₹ " + onRoad);
        
        // Key Specs
        tvEngineCC.setText(parentModel.getEngineCC() + " CC");
        tvMileage.setText(parentModel.getMileage() + " KMPL");
        tvTransmission.setText(parentModel.getTransmission() != null ? parentModel.getTransmission().toUpperCase() : "");
        
        if (variant.brakesWheels != null) {
             tvBrakingType.setText(variant.brakesWheels.brakingSystem != null ? variant.brakesWheels.brakingSystem.toUpperCase() : "N/A");
             tvFrontBrake.setText(variant.brakesWheels.frontBrake);
             tvRearBrake.setText(variant.brakesWheels.rearBrake);
             tvBrakingSystemDetail.setText(variant.brakesWheels.brakingSystem);
             tvWheelType.setText(variant.brakesWheels.wheelType);
        }
        
        // Technical Specs (From Parent usually, unless overridden in variant? Assuming parent for now)
        tvTankCapacity.setText(parentModel.getFuelTankCapacity() + " Litres");
        tvKerbWeight.setText(parentModel.getKerbWeight() + " kg");
        tvSeatHeight.setText(parentModel.getSeatHeight() + " mm");
        tvGroundClearance.setText(parentModel.getGroundClearance() + " mm");
        tvMaxPower.setText(parentModel.getMaxPower());
        tvMaxTorque.setText(parentModel.getMaxTorque());

        // Warranty & Service
        tvWarranty.setText(parentModel.getWarrantyPeriod());
        tvFreeService.setText(parentModel.getFreeServices());
        
        // Price Breakup
        if (variant.priceDetails != null) {
            tvExShowroom.setText("₹ " + variant.priceDetails.exShowroom);
            tvInsurance.setText("₹ " + variant.priceDetails.insurance);
            tvRegistration.setText("₹ " + variant.priceDetails.registration); // Mapping to RTO
            tvLTRT.setText("₹ " + variant.priceDetails.ltrt); // Mapping to Handling/Other
            tvTotalBreakdown.setText("₹ " + variant.priceDetails.totalOnRoad);
        }
        
        // Colors
        setupColors(variant);
        
        // Custom Sections
        setupCustomSections(variant);
    }
    
    private void setupColors(BikeVariantModel variant) {
        List<BikeVariantModel.VariantColor> newColors = variant.colors != null ? variant.colors : new ArrayList<>();
        
        // Skip re-creation if colors are exactly the same
        if (currentColorList.equals(newColors)) {
            return;
        }
        currentColorList = new ArrayList<>(newColors);

        colorsContainer.removeAllViews();
        if (newColors.isEmpty()) {
            llColorsSection.setVisibility(View.GONE);
            updateImages(null); // Show default images
            return;
        }
        
        llColorsSection.setVisibility(View.VISIBLE);
        
        // By default select first color
        // But we need to track selected color index or name?
        // Let's just default to 0
        
        for (int i = 0; i < variant.colors.size(); i++) {
            BikeVariantModel.VariantColor color = variant.colors.get(i);
            boolean isSelected = (i == 0); // Logic can be improved to keep selection across variants if name matches
            
            addColorCircle(color, isSelected, i);
            
            if (isSelected) {
                tvSelectedColorName.setText(color.colorName);
                updateImages(color.imagePaths);
            }
        }
    }
    
    private void addColorCircle(BikeVariantModel.VariantColor color, boolean isSelected, int index) {
        int size = (int) (40 * getResources().getDisplayMetrics().density);
        int margin = (int) (8 * getResources().getDisplayMetrics().density);
        
        View circle = new View(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
        params.setMargins(0, 0, margin, 0);
        circle.setLayoutParams(params);
        
        android.graphics.drawable.GradientDrawable drawable = new android.graphics.drawable.GradientDrawable();
        drawable.setShape(android.graphics.drawable.GradientDrawable.OVAL);
        try {
            drawable.setColor(android.graphics.Color.parseColor(color.colorHex));
        } catch (Exception e) {
            drawable.setColor(android.graphics.Color.LTGRAY);
        }
        
        if (isSelected) {
            drawable.setStroke(6, android.graphics.Color.parseColor("#374151")); // Dark Gray border
        } else {
             drawable.setStroke(2, android.graphics.Color.parseColor("#E5E7EB")); // Light Gray
        }
        
        circle.setBackground(drawable);
        circle.setOnClickListener(v -> {
            // Update Selection UI
            updateColorSelection(index);
            tvSelectedColorName.setText(color.colorName);
            updateImages(color.imagePaths);
        });
        
        colorsContainer.addView(circle);
    }
    
    private void updateColorSelection(int selectedIndex) {
        for (int i = 0; i < colorsContainer.getChildCount(); i++) {
            View child = colorsContainer.getChildAt(i);
            android.graphics.drawable.GradientDrawable bg = (android.graphics.drawable.GradientDrawable) child.getBackground();
            if (i == selectedIndex) {
                 bg.setStroke(6, android.graphics.Color.parseColor("#374151"));
            } else {
                 bg.setStroke(2, android.graphics.Color.parseColor("#E5E7EB"));
            }
        }
    }
    
    private void updateImages(List<String> imagePaths) {
        List<String> imagesToShow = new ArrayList<>();
        if (imagePaths != null && !imagePaths.isEmpty()) {
            imagesToShow.addAll(imagePaths);
        }

        // Check for equality to prevent redundant refreshes (and flashes)
        if (currentImagePaths.equals(imagesToShow) && imageAdapter != null) {
            return;
        }
        currentImagePaths = new ArrayList<>(imagesToShow);

        // Hide indicators if only 1 image or empty
        tlImageIndicator.setVisibility(imagesToShow.size() <= 1 ? View.GONE : View.VISIBLE);

        if (imageAdapter == null) {
            imageAdapter = new BikeImageAdapter(this, imagesToShow);
            vpBikeImages.setAdapter(imageAdapter);
            imageMediator = new TabLayoutMediator(tlImageIndicator, vpBikeImages, (tab, position) -> {});
            imageMediator.attach();
        } else {
            imageAdapter.updateImages(imagesToShow);
        }
    }
    
    private void setupCustomSections(BikeVariantModel variant) {
        llCustomSectionsContainer.removeAllViews();
        if (variant.customSections == null) return;
        
        for (BikeVariantModel.CustomSection section : variant.customSections) {
             addCustomSectionView(section);
        }
    }
    
    private void addCustomSectionView(BikeVariantModel.CustomSection section) {
        androidx.cardview.widget.CardView card = new androidx.cardview.widget.CardView(this);
        card.setCardBackgroundColor(getResources().getColor(android.R.color.white));
        card.setRadius(16 * getResources().getDisplayMetrics().density); // 16dp
        card.setCardElevation(4 * getResources().getDisplayMetrics().density); // 4dp
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        int margin16 = (int) (16 * getResources().getDisplayMetrics().density);
        cardParams.setMargins(margin16, margin16, margin16, 0);
        card.setLayoutParams(cardParams);
        
        LinearLayout innerLayout = new LinearLayout(this);
        innerLayout.setOrientation(LinearLayout.VERTICAL);
        int padding16 = (int) (16 * getResources().getDisplayMetrics().density);
        innerLayout.setPadding(padding16, padding16, padding16, padding16);
        
        // Header
        LinearLayout headerLayout = new LinearLayout(this);
        headerLayout.setOrientation(LinearLayout.HORIZONTAL);
        headerLayout.setGravity(android.view.Gravity.CENTER_VERTICAL);
        
        ImageView icon = new ImageView(this);
        icon.setImageResource(R.drawable.ic_check_circle); 
        icon.setImageTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#13A4EC")));
        int size24 = (int) (24 * getResources().getDisplayMetrics().density);
        icon.setLayoutParams(new LinearLayout.LayoutParams(size24, size24));
        headerLayout.addView(icon);
        
        TextView title = new TextView(this);
        title.setText(section.sectionName != null ? section.sectionName.toUpperCase() : "");
        title.setTextSize(16);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        title.setTextColor(android.graphics.Color.parseColor("#111618"));
        title.setPadding(padding16/2, 0, 0, 0); // 8dp padding start
        headerLayout.addView(title);
        
        innerLayout.addView(headerLayout);
        
        // Spacing
        android.widget.Space space = new android.widget.Space(this);
        space.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, padding16));
        innerLayout.addView(space);
        
        // Fields Grid
        android.widget.GridLayout grid = new android.widget.GridLayout(this);
        grid.setColumnCount(2);
        grid.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        
        if (section.fields != null) {
            for (BikeVariantModel.CustomField field : section.fields) {
                // Container for each field (Label + Value stacked)
                LinearLayout fieldContainer = new LinearLayout(this);
                fieldContainer.setOrientation(LinearLayout.VERTICAL);
                
                android.widget.GridLayout.LayoutParams params = new android.widget.GridLayout.LayoutParams();
                params.width = 0;
                params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                params.columnSpec = android.widget.GridLayout.spec(android.widget.GridLayout.UNDEFINED, 1f);
                params.bottomMargin = padding16;
                fieldContainer.setLayoutParams(params);

                // Label
                TextView tvKey = new TextView(this);
                tvKey.setText(field.key.toUpperCase());
                tvKey.setTextSize(10);
                tvKey.setTypeface(null, android.graphics.Typeface.BOLD);
                tvKey.setTextColor(android.graphics.Color.parseColor("#617C89")); // Gray label
                fieldContainer.addView(tvKey);

                // Value
                TextView tvValue = new TextView(this);
                tvValue.setText(field.value);
                tvValue.setTextSize(14);
                tvValue.setTypeface(null, android.graphics.Typeface.BOLD);
                tvValue.setTextColor(android.graphics.Color.parseColor("#111618")); // Black value
                tvValue.setPadding(0, 4 * (int)getResources().getDisplayMetrics().density, 0, 0); // 4dp top margin
                fieldContainer.addView(tvValue);

                grid.addView(fieldContainer);
            }
        }
        
        innerLayout.addView(grid);
        card.addView(innerLayout);
        llCustomSectionsContainer.addView(card);
    }


    // Adapter Class
    private static class VariantAdapter extends RecyclerView.Adapter<VariantAdapter.ViewHolder> {
        private final List<BikeVariantModel> variants;
        private final OnVariantClickListener listener;
        private int selectedPosition = 0;

        public interface OnVariantClickListener {
            void onVariantClick(BikeVariantModel variant);
        }

        public VariantAdapter(List<BikeVariantModel> variants, OnVariantClickListener listener) {
            this.variants = variants;
            this.listener = listener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_variant_chip_customer, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            BikeVariantModel variant = variants.get(position);
            holder.tvVariantChip.setText(variant.variantName);
            holder.tvVariantChip.setSelected(position == selectedPosition);
            
            holder.itemView.setOnClickListener(v -> {
                int previousPos = selectedPosition;
                selectedPosition = holder.getAdapterPosition();
                notifyItemChanged(previousPos);
                notifyItemChanged(selectedPosition);
                listener.onVariantClick(variant);
            });
        }

        @Override
        public int getItemCount() {
            return variants != null ? variants.size() : 0;
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvVariantChip;
            public ViewHolder(View itemView) {
                super(itemView);
                tvVariantChip = itemView.findViewById(R.id.tvVariantChip);
            }
        }
    }
}
