package com.example.motovista_deep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.bumptech.glide.Glide;
import com.example.motovista_deep.api.RetrofitClient;
import com.example.motovista_deep.models.GetOrderSummaryResponse;
import com.example.motovista_deep.models.OrderSummaryData;

import java.text.SimpleDateFormat;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderStatusActivity extends AppCompatActivity {

    // Header
    private ImageView btnBack;
    private TextView tvTitle;

    // Status Views (State 1)
    private LinearLayout layoutStatus;
    private View outerRing;
    private CardView cardStatusIcon;
    private ImageView ivStatusIcon;
    private TextView tvStatusTitle;
    private TextView tvStatusMessage;
    
    // Tracking Views (State 2)
    private LinearLayout layoutTracking;
    private TextView tvTrackBikeName;
    private TextView tvTrackOrderId;
    private TextView tvTrackOrderDate;
    private ImageView ivTrackBikeImage;
    private LinearLayout btnSupport;


    // Order Details Views (Inside Status Views)
    private TextView tvOrderId;
    private TextView tvBikeName;
    private TextView tvBikeVariant;
    private TextView tvBikePrice;
    private TextView tvBikeColor;
    private View viewBikeColorDot;
    private TextView tvOrderDate;
    
    // Bottom Navigation
    private LinearLayout tabHome, tabBikes, tabEmiCalculator, tabOrders, tabProfile;
    private ImageView ivHome, ivBikes, ivEmiCalculator, ivOrders, ivProfile;
    private TextView tvHome, tvBikes, tvEmiCalculator, tvOrders, tvProfile;
    
    // Data
    private String orderIdString;
    private int orderIdInt = -1;

    // Polling
    private Handler pollingHandler = new Handler();
    private Runnable pollingRunnable;
    private static final int POLLING_INTERVAL = 10000; // 10 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        // Get Data
        if (getIntent().hasExtra("ORDER_ID")) {
            orderIdString = getIntent().getStringExtra("ORDER_ID");
            parseOrderId();
        }

        initializeViews();
        setupClickListeners();
        setActiveTab(tabOrders);

        // Start Animation
        startStatusAnimation();

        // Initialize Polling Runnable
        pollingRunnable = new Runnable() {
            @Override
            public void run() {
                if (orderIdInt != -1) {
                    fetchOrderDetails(orderIdInt);
                }
                pollingHandler.postDelayed(this, POLLING_INTERVAL);
            }
        };

        // Load Data
        if (orderIdInt != -1) {
            fetchOrderDetails(orderIdInt);
        } else {
            // If no ID passed (e.g. from Tab), fetch latest order for current user
            com.example.motovista_deep.models.User user = com.example.motovista_deep.helpers.SharedPrefManager.getInstance(this).getUser();
            if (user != null) {
                fetchLatestOrder(user.getId());
            } else {
                tvStatusTitle.setText("Login Required");
                tvStatusMessage.setText("Please login to view your orders.");
            }
        }
    }
    
    private void parseOrderId() {
        if (orderIdString != null) {
            try {
                // If it looks like "#ORD123", strip non-digits
                String cleanId = orderIdString.replaceAll("\\D+", "");
                if (!cleanId.isEmpty()) {
                    orderIdInt = Integer.parseInt(cleanId);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    private void initializeViews() {
        // Header
        btnBack = findViewById(R.id.btnBack);
        tvTitle = findViewById(R.id.tvTitle);

        // Status Container (State 1)
        layoutStatus = findViewById(R.id.layoutStatus);
        outerRing = findViewById(R.id.outerRing);
        cardStatusIcon = findViewById(R.id.cardStatusIcon);
        ivStatusIcon = findViewById(R.id.ivStatusIcon);
        tvStatusTitle = findViewById(R.id.tvStatusTitle);
        tvStatusMessage = findViewById(R.id.tvStatusMessage);
        
        // Tracking Container (State 2)
        layoutTracking = findViewById(R.id.layoutTracking);
        tvTrackBikeName = findViewById(R.id.tvTrackBikeName);
        tvTrackOrderId = findViewById(R.id.tvTrackOrderId);
        tvTrackOrderDate = findViewById(R.id.tvTrackOrderDate);
        ivTrackBikeImage = findViewById(R.id.ivTrackBikeImage);

        // Order Details (Inside State 1)
        tvOrderId = findViewById(R.id.tvOrderId);
        tvBikeName = findViewById(R.id.tvBikeName);
        tvBikeVariant = findViewById(R.id.tvBikeVariant);
        tvBikePrice = findViewById(R.id.tvBikePrice);
        tvBikeColor = findViewById(R.id.tvBikeColor);
        viewBikeColorDot = findViewById(R.id.viewBikeColorDot);
        tvOrderDate = findViewById(R.id.tvOrderDate);

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

        // Support
        btnSupport = findViewById(R.id.btnSupport);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        // Bottom Navigation
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
             startActivity(new Intent(OrderStatusActivity.this, CustomerOrdersActivity.class));
             finish();
        });

        tabProfile.setOnClickListener(v -> {
            Intent intent = new Intent(OrderStatusActivity.this, CustomerProfileScreenActivity.class);
            startActivity(intent);
            finish();
        });

        if (btnSupport != null) {
            btnSupport.setOnClickListener(v -> {
                Intent intent = new Intent(OrderStatusActivity.this, HelpSupportActivity.class);
                startActivity(intent);
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startPolling();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopPolling();
    }

    private void startPolling() {
        stopPolling(); // Avoid multiple instances
        pollingHandler.postDelayed(pollingRunnable, POLLING_INTERVAL);
    }

    private void stopPolling() {
        pollingHandler.removeCallbacks(pollingRunnable);
    }

    private void fetchLatestOrder(int userId) {
        RetrofitClient.getApiService().getCustomerOrders(userId).enqueue(new Callback<com.example.motovista_deep.models.GetCustomerRequestsResponse>() {
            @Override
            public void onResponse(Call<com.example.motovista_deep.models.GetCustomerRequestsResponse> call, Response<com.example.motovista_deep.models.GetCustomerRequestsResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    if (response.body().getData() != null && !response.body().getData().isEmpty()) {
                        // Get latest order (first one since API sorts DESC)
                        orderIdInt = response.body().getData().get(0).getId();
                        fetchOrderDetails(orderIdInt);
                    } else {
                        tvStatusTitle.setText("No Orders Found");
                        tvStatusMessage.setText("You haven't placed any orders yet.");
                    }
                } else {
                    tvStatusTitle.setText("Error");
                    tvStatusMessage.setText("Failed to fetch orders.");
                }
            }

            @Override
            public void onFailure(Call<com.example.motovista_deep.models.GetCustomerRequestsResponse> call, Throwable t) {
                tvStatusTitle.setText("Network Error");
                tvStatusMessage.setText("Check your internet connection.");
            }
        });
    }

    private void fetchOrderDetails(int id) {
        RetrofitClient.getApiService().getOrderSummary(id).enqueue(new Callback<GetOrderSummaryResponse>() {
            @Override
            public void onResponse(Call<GetOrderSummaryResponse> call, Response<GetOrderSummaryResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    OrderSummaryData data = response.body().getData();
                    updateUI(data);
                } else {
                    Toast.makeText(OrderStatusActivity.this, "Failed to load order details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GetOrderSummaryResponse> call, Throwable t) {
                Toast.makeText(OrderStatusActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(OrderSummaryData data) {
        // --- Populate Details Card (State 1) ---
        tvOrderId.setText("#ORD" + data.getRequestId());
        tvBikeName.setText(data.getBikeName());
        tvBikeVariant.setText(data.getBikeVariant());
        
        String price = data.getOnRoadPrice();
        if (price != null && !price.startsWith("₹")) {
             price = "₹" + price;
        }
        tvBikePrice.setText(price);
        
        // Color
        String parts[] = data.getBikeColor().split("\\|");
        String colorName = parts.length > 0 ? parts[0] : data.getBikeColor();
        String hexCode = parts.length > 1 ? parts[1] : null;
        
        tvBikeColor.setText(colorName);
        if (hexCode != null && viewBikeColorDot != null) {
             try {
                viewBikeColorDot.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor(hexCode)));
             } catch (Exception e) {
                viewBikeColorDot.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.GRAY));
             }
        }
        
        // Date Logic (Reused for both states)
        String dateString = data.getCreatedAt();
        if (dateString != null) {
             try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                java.util.Date date = inputFormat.parse(dateString);
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                dateString = outputFormat.format(date);
             } catch (Exception e) {
                // Keep original
             }
        } else {
            dateString = "...";
        }
        tvOrderDate.setText(dateString);

        // --- Populate Tracking Card (State 2) ---
        tvTrackBikeName.setText(data.getBikeName());
        tvTrackOrderId.setText("Order #" + data.getRequestId());
        tvTrackOrderDate.setText("Placed on " + dateString);
        
        // Status Logic
        updateStatusUI(data.getStatus(), data.getRequestId());
        
        // Progress Logic (Step Updates)
        updateProgressUI(data);

        // Load Bike Image
        loadBikeImage(data.getImagePaths());
    }
    
    private void updateProgressUI(OrderSummaryData data) {
         // Default states (Grey/Pending) handled by XML default or loops below
         // We need to match steps:
         // 1. Order Confirmed (Green if status accepted)
         // 2. Document Verification (step_1_status)
         // 3. Tax Payment (step_2_status)
         // 4. Number Plate (step_3_status) (SWAPPED in Layout vs Logic mapping, checking XML...)
         // XML Item 4 is "Number Plate Request" (was RC) -> I fixed titles in XML to match LEDGER order:
         // 1: Order Confirmed
         // 2: Docs (Ledger 1)
         // 3: Tax (Ledger 2)
         // 4: Number Plate (Ledger 3)
         // 5: RC Issue (Ledger 4)
         
         java.util.Map<String, String> progress = data.getRegistrationProgress();
         
         // --- Step 1: Order Confirmed ---
         boolean isAccepted = data.getStatus() != null && 
                             (data.getStatus().toLowerCase().contains("accept") || 
                              data.getStatus().toLowerCase().contains("approve") || 
                              data.getStatus().toLowerCase().contains("complet"));
                              
         setStepState(1, isAccepted ? "Completed" : "Pending", isAccepted);
         
         // --- Ledger Steps ---
         if (progress != null) {
             setStepState(2, progress.get("step_1_status"), isAccepted);
             setStepState(3, progress.get("step_2_status"), isAccepted);
             setStepState(4, progress.get("step_3_status"), isAccepted);
             setStepState(5, progress.get("step_4_status"), isAccepted);
         }
    }
    
    private void loadBikeImage(String paths) {
        if (paths == null || paths.isEmpty() || ivTrackBikeImage == null) return;

        String firstPath = paths;
        if (paths.contains(",")) {
            firstPath = paths.split(",")[0];
        }

        // Logic from BikeAdapter
        String baseUrl = RetrofitClient.BASE_URL;
        if (baseUrl.endsWith("api/")) {
            baseUrl = baseUrl.replace("api/", "");
        }
        
        String imageUrl = firstPath.replace("\"", "").replace("\\", "").trim();
        if (!imageUrl.startsWith("http")) {
            if (imageUrl.startsWith("bikes/") || imageUrl.startsWith("second_hand_bikes/")) {
                imageUrl = "uploads/" + imageUrl;
            } else if (!imageUrl.startsWith("uploads/")) {
                imageUrl = "uploads/bikes/" + imageUrl;
            }
            imageUrl = baseUrl + imageUrl;
        }

        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_bike)
                .error(R.drawable.placeholder_bike)
                .into(ivTrackBikeImage);
    }

    private void startBlinkingAnimation(View view) {
        if (view == null) return;
        view.clearAnimation();
        AlphaAnimation blink = new AlphaAnimation(1.0f, 0.3f);
        blink.setDuration(800);
        blink.setInterpolator(new AccelerateDecelerateInterpolator());
        blink.setRepeatMode(Animation.REVERSE);
        blink.setRepeatCount(Animation.INFINITE);
        view.startAnimation(blink);
    }

    private void setStepState(int stepNum, String status, boolean parentAccepted) {
        TextView tvTitle = null;
        TextView tvStatus = null;
        ImageView ivIcon = null;
        View viewLine = null;
        
        // Find Views by ID dynamically
        int idTitle = getResources().getIdentifier("tvStep" + stepNum + "Title", "id", getPackageName());
        int idStatus = getResources().getIdentifier("tvStep" + stepNum + "Status", "id", getPackageName());
        int idIcon = getResources().getIdentifier("ivStep" + stepNum + "Icon", "id", getPackageName());
        int idLine = getResources().getIdentifier("viewStep" + stepNum + "Line", "id", getPackageName());
        
        if (idTitle != 0) tvTitle = findViewById(idTitle);
        if (idStatus != 0) tvStatus = findViewById(idStatus);
        if (idIcon != 0) ivIcon = findViewById(idIcon);
        if (idLine != 0) viewLine = findViewById(idLine);
        
        if (status == null) status = "locked";
        
        int colorGreen = ContextCompat.getColor(this, R.color.emi_success_green);
        int colorGray = ContextCompat.getColor(this, R.color.gray_400);
        int colorDark = ContextCompat.getColor(this, R.color.text_dark);
        int colorAmber = 0xFFF59E0B; // Amber/Orange for pending
        
        if ("completed".equalsIgnoreCase(status) || (stepNum == 1 && parentAccepted)) {
            if (tvStatus != null) {
                tvStatus.setText("Completed");
                tvStatus.setTextColor(colorGreen);
            }
            if (ivIcon != null) {
                ivIcon.setImageResource(R.drawable.ic_check_circle);
                ivIcon.setColorFilter(colorGreen);
                ivIcon.clearAnimation();
            }
            if (viewLine != null) {
                viewLine.setBackgroundColor(colorGreen);
            }
            if (tvTitle != null) tvTitle.setTextColor(colorDark);
            
        } else if ("pending".equalsIgnoreCase(status)) {
            if (tvStatus != null) {
                tvStatus.setText("In Progress");
                tvStatus.setTextColor(colorAmber);
            }
            if (ivIcon != null) {
                ivIcon.setImageResource(R.drawable.ic_circle_outline);
                ivIcon.setColorFilter(colorAmber);
                startBlinkingAnimation(ivIcon);
            }
            if (viewLine != null) {
                viewLine.setBackgroundColor(colorGray);
            }
            if (tvTitle != null) tvTitle.setTextColor(colorDark);
            
        } else { // Locked/Default
            if (tvStatus != null) {
                tvStatus.setText(stepNum == 1 ? "Pending Approval" : "Locked");
                tvStatus.setTextColor(colorGray);
            }
            if (ivIcon != null) {
                ivIcon.setImageResource(R.drawable.ic_circle_outline);
                ivIcon.setColorFilter(colorGray);
                ivIcon.clearAnimation();
            }
            if (viewLine != null) {
                viewLine.setBackgroundColor(ContextCompat.getColor(this, R.color.gray_300));
            }
            if (tvTitle != null) tvTitle.setTextColor(ContextCompat.getColor(this, R.color.gray_500));
        }
    }

    private void updateStatusUI(String status, int requestId) {
        if (status == null) status = "Pending";
        status = status.toLowerCase();
        
        boolean isAccepted = status.contains("accept") || status.contains("approve") || status.contains("complet");
        
        // Default: Show Status View
        layoutStatus.setVisibility(View.VISIBLE);
        layoutTracking.setVisibility(View.GONE);

        if (isAccepted) {
            // Check if seen before
            String key = "seen_acceptance_" + requestId;
            boolean seen = getSharedPreferences("order_tracking_prefs", MODE_PRIVATE).getBoolean(key, false);

            if (seen) {
                // Already seen: Show Tracking directly
                layoutStatus.setVisibility(View.GONE);
                layoutTracking.setVisibility(View.VISIBLE);
            } else {
                // First time: Show "Processing" state (Blue) briefly then switch to "Accepted" (Green)
                
                // 1. Start with Processing state (Blue)
                tvStatusTitle.setText("Processing Order");
                tvStatusTitle.setTextColor(ContextCompat.getColor(this, R.color.primary));
                tvStatusMessage.setText("We have received your request and it's under review.");
                
                ivStatusIcon.setImageResource(R.drawable.ic_check_white); 
                cardStatusIcon.setCardBackgroundColor(ContextCompat.getColor(this, R.color.primary));
                outerRing.setBackgroundTintList(android.content.res.ColorStateList.valueOf(ContextCompat.getColor(this, R.color.primary)));
                outerRing.setAlpha(0.3f);

                // 2. Transition to "Accepted" (Green) after 1.5 seconds
                new Handler().postDelayed(() -> {
                    try {
                        tvStatusTitle.setText("Order Accepted");
                        tvStatusTitle.setTextColor(ContextCompat.getColor(this, R.color.emi_success_green));
                        tvStatusMessage.setText("Your order has been accepted! Showroom visit required.");
                        
                        ivStatusIcon.setImageResource(R.drawable.ic_check_white); 
                        cardStatusIcon.setCardBackgroundColor(ContextCompat.getColor(this, R.color.emi_success_green));
                        outerRing.setBackgroundResource(R.drawable.bg_pulsing_ring);
                        outerRing.setBackgroundTintList(android.content.res.ColorStateList.valueOf(ContextCompat.getColor(this, R.color.emi_success_green)));
                        outerRing.setAlpha(0.8f);

                        // 3. Finally reveal Tracking after 2 more seconds
                        new Handler().postDelayed(() -> {
                            try {
                                layoutStatus.setVisibility(View.GONE);
                                layoutTracking.setVisibility(View.VISIBLE);
                                // Mark as seen
                                getSharedPreferences("order_tracking_prefs", MODE_PRIVATE).edit().putBoolean(key, true).apply();
                            } catch (Exception e) { e.printStackTrace(); }
                        }, 2000);
                        
                    } catch (Exception e) { e.printStackTrace(); }
                }, 1500);
            }

        } else if (status.contains("reject")) {
            // Rejected (Red)
            tvStatusTitle.setText("Order Rejected");
            tvStatusTitle.setTextColor(ContextCompat.getColor(this, R.color.red_500));
            tvStatusMessage.setText("We're sorry, your order could not be processed at this time.");
            
            ivStatusIcon.setImageResource(R.drawable.ic_close); 
            cardStatusIcon.setCardBackgroundColor(ContextCompat.getColor(this, R.color.red_500));
            outerRing.setBackgroundTintList(android.content.res.ColorStateList.valueOf(ContextCompat.getColor(this, R.color.red_500)));

        } else {
            // Pending (Default/Primary)
            tvStatusTitle.setText("Processing Order");
            tvStatusTitle.setTextColor(ContextCompat.getColor(this, R.color.text_dark));
            tvStatusMessage.setText("We are reviewing your request. Please wait for admin approval.");
            
            ivStatusIcon.setImageResource(R.drawable.ic_check_white); 
            cardStatusIcon.setCardBackgroundColor(ContextCompat.getColor(this, R.color.primary));
            outerRing.setBackgroundTintList(null); 
        }
    }

    private void startStatusAnimation() {
        ObjectAnimator scaleAnimator = ObjectAnimator.ofFloat(outerRing, "scaleX", 1f, 1.2f, 1f);
        scaleAnimator.setDuration(2000);
        scaleAnimator.setRepeatCount(ValueAnimator.INFINITE);
        scaleAnimator.setRepeatMode(ValueAnimator.RESTART);
        scaleAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator scaleAnimatorY = ObjectAnimator.ofFloat(outerRing, "scaleY", 1f, 1.2f, 1f);
        scaleAnimatorY.setDuration(2000);
        scaleAnimatorY.setRepeatCount(ValueAnimator.INFINITE);
        scaleAnimatorY.setRepeatMode(ValueAnimator.RESTART);
        scaleAnimatorY.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(outerRing, "alpha", 0.3f, 0.8f, 0.3f);
        alphaAnimator.setDuration(2000);
        alphaAnimator.setRepeatCount(ValueAnimator.INFINITE);
        alphaAnimator.setRepeatMode(ValueAnimator.RESTART);

        scaleAnimator.start();
        scaleAnimatorY.start();
        alphaAnimator.start();
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
}