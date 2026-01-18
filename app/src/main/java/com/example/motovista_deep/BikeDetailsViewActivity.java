package com.example.motovista_deep;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.bumptech.glide.Glide;
import com.example.motovista_deep.api.RetrofitClient;
import com.example.motovista_deep.utils.ImageUtils;

public class BikeDetailsViewActivity extends AppCompatActivity {

    // Views
    private CardView btnBack, btnShare;
    private LinearLayout layoutEngineNumber, layoutChassisNumber;
    private ImageView ivCopyEngine, ivCopyChassis;
    private ImageView ivBikeImage;
    private View viewColorIndicator;

    // Text Views
    private TextView tvBikeName, tvPurchaseDate;
    private TextView tvEngineNumber, tvChassisNumber, tvColor, tvVariant;
    private TextView tvPolicyNumber, tvInsuranceEnd, tvExpiryDaysCountdown;
    
    // EMI Views
    private CardView cardEmi;
    private TextView tvEmiStatus, tvRemainingBalance, tvMonthlyEmi, tvPayProgressText, tvTotalLoanAmount;
    private android.widget.ProgressBar emiProgressBar;
    private android.widget.Button btnViewEmiDetails;

    // Handler for copy animation
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_details_view);

        // Initialize views
        initializeViews();

        // Setup click listeners
        setupClickListeners();

        // Load bike data from intent
        loadBikeData();
    }

    private void initializeViews() {
        // Header
        btnBack = findViewById(R.id.btnBack);
        btnShare = findViewById(R.id.btnShare);

        // Bike info
        ivBikeImage = findViewById(R.id.ivBikeImage);
        tvBikeName = findViewById(R.id.tvBikeName);
        tvPurchaseDate = findViewById(R.id.tvPurchaseDate);
        viewColorIndicator = findViewById(R.id.viewColorIndicator);

        // Vehicle details
        layoutEngineNumber = findViewById(R.id.layoutEngineNumber);
        layoutChassisNumber = findViewById(R.id.layoutChassisNumber);
        ivCopyEngine = findViewById(R.id.ivCopyEngine);
        ivCopyChassis = findViewById(R.id.ivCopyChassis);
        tvEngineNumber = findViewById(R.id.tvEngineNumber);
        tvChassisNumber = findViewById(R.id.tvChassisNumber);
        tvColor = findViewById(R.id.tvColor);
        tvVariant = findViewById(R.id.tvVariant);

        // Insurance
        tvPolicyNumber = findViewById(R.id.tvPolicyNumber);
        tvInsuranceEnd = findViewById(R.id.tvInsuranceEnd);
        tvExpiryDaysCountdown = findViewById(R.id.tvExpiryDaysCountdown);

        // EMI
        cardEmi = findViewById(R.id.cardEmi);
        tvEmiStatus = findViewById(R.id.tvEmiStatus);
        tvRemainingBalance = findViewById(R.id.tvRemainingBalance);
        tvMonthlyEmi = findViewById(R.id.tvMonthlyEmi);
        tvPayProgressText = findViewById(R.id.tvPayProgressText);
        tvTotalLoanAmount = findViewById(R.id.tvTotalLoanAmount);
        emiProgressBar = findViewById(R.id.emiProgressBar);
        btnViewEmiDetails = findViewById(R.id.btnViewEmiDetails);

        // Setup hover effects for copy icons
        setupHoverEffects();
    }

    private void setupHoverEffects() {
        // Show copy icon on hover for engine number
        layoutEngineNumber.setOnHoverListener((v, event) -> {
            switch (event.getAction()) {
                case android.view.MotionEvent.ACTION_HOVER_ENTER:
                    ivCopyEngine.animate().alpha(1f).setDuration(200).start();
                    break;
                case android.view.MotionEvent.ACTION_HOVER_EXIT:
                    ivCopyEngine.animate().alpha(0f).setDuration(200).start();
                    break;
            }
            return false;
        });

        // Show copy icon on hover for chassis number
        layoutChassisNumber.setOnHoverListener((v, event) -> {
            switch (event.getAction()) {
                case android.view.MotionEvent.ACTION_HOVER_ENTER:
                    ivCopyChassis.animate().alpha(1f).setDuration(200).start();
                    break;
                case android.view.MotionEvent.ACTION_HOVER_EXIT:
                    ivCopyChassis.animate().alpha(0f).setDuration(200).start();
                    break;
            }
            return false;
        });
    }

    private void setupClickListeners() {
        // Back button
        btnBack.setOnClickListener(v -> {
            finish();
        });

        // Share button
        btnShare.setOnClickListener(v -> {
            shareBikeDetails();
        });

        // Copy engine number
        layoutEngineNumber.setOnClickListener(v -> {
            copyToClipboard(tvEngineNumber.getText().toString(), "Engine Number");
            animateCopyIcon(ivCopyEngine);
        });

        ivCopyEngine.setOnClickListener(v -> {
            copyToClipboard(tvEngineNumber.getText().toString(), "Engine Number");
            animateCopyIcon(ivCopyEngine);
        });

        // Copy chassis number
        layoutChassisNumber.setOnClickListener(v -> {
            copyToClipboard(tvChassisNumber.getText().toString(), "Chassis Number");
            animateCopyIcon(ivCopyChassis);
        });

        ivCopyChassis.setOnClickListener(v -> {
            copyToClipboard(tvChassisNumber.getText().toString(), "Chassis Number");
            animateCopyIcon(ivCopyChassis);
        });

        // EMI Details Button
        btnViewEmiDetails.setOnClickListener(v -> {
            Intent emiIntent = new Intent(this, EmiDetailsActivity.class);
            emiIntent.putExtra("LEDGER_ID", getIntent().getIntExtra("LEDGER_ID", -1));
            emiIntent.putExtra("IS_CUSTOMER_VIEW", true);
            startActivity(emiIntent);
        });
    }

    private void loadBikeData() {
        // Get data from intent
        Intent intent = getIntent();
        if (intent != null) {
            // Bike info
            String bikeName = intent.getStringExtra("BIKE_NAME");
            String purchaseDate = intent.getStringExtra("PURCHASE_DATE");
            String color = intent.getStringExtra("COLOR");
            String variant = intent.getStringExtra("VARIANT");
            String engineNumber = intent.getStringExtra("ENGINE_NUMBER");
            String chassisNumber = intent.getStringExtra("CHASSIS_NUMBER");

            // Insurance info
            String policyNumber = intent.getStringExtra("POLICY_NUMBER");
            String insuranceEnd = intent.getStringExtra("INSURANCE_END");

            // Set values if provided, otherwise use defaults
            if (bikeName != null) tvBikeName.setText(bikeName);
            if (purchaseDate != null) tvPurchaseDate.setText("Purchase Date: " + purchaseDate);
            if (color != null) tvColor.setText(color);
            if (variant != null) tvVariant.setText(variant);
            if (engineNumber != null) tvEngineNumber.setText(engineNumber);
            if (chassisNumber != null) tvChassisNumber.setText(chassisNumber);
            if (policyNumber != null) tvPolicyNumber.setText(policyNumber);
            if (insuranceEnd != null) tvInsuranceEnd.setText(insuranceEnd);

            // Insurance Countdown
            if (insuranceEnd != null && !insuranceEnd.equals("TBD")) {
                calculateInsuranceCountdown(insuranceEnd);
            } else {
                tvExpiryDaysCountdown.setVisibility(View.GONE);
            }

            // EMI Section
            double emiTotal = intent.getDoubleExtra("EMI_TOTAL", 0);
            if (emiTotal > 0) {
                cardEmi.setVisibility(View.VISIBLE);
                double emiPaid = intent.getDoubleExtra("EMI_PAID", 0);
                double emiMonthly = intent.getDoubleExtra("EMI_MONTHLY", 0);
                int emiDuration = intent.getIntExtra("EMI_DURATION", 0);
                String emiStatus = intent.getStringExtra("EMI_STATUS");
                double emiRemaining = intent.getDoubleExtra("EMI_REMAINING", 0);

                if (emiStatus != null) tvEmiStatus.setText(emiStatus.toUpperCase());
                tvRemainingBalance.setText("₹" + String.format("%,.0f", emiRemaining));
                tvMonthlyEmi.setText("₹" + String.format("%,.0f", emiMonthly));
                tvTotalLoanAmount.setText("Total Loan Amount: ₹" + String.format("%,.0f", emiTotal));

                // Calculation for progress
                int paidMonths = 0;
                if (emiMonthly > 0) {
                    paidMonths = (int) Math.floor(emiPaid / emiMonthly);
                }
                tvPayProgressText.setText(paidMonths + " of " + emiDuration + " Months Paid");
                
                int progress = 0;
                if (emiTotal > 0) {
                    progress = (int) ((emiPaid / emiTotal) * 100);
                }
                emiProgressBar.setProgress(progress);
            } else {
                cardEmi.setVisibility(View.GONE);
            }
 
            // Color indicator
            String colorHex = intent.getStringExtra("COLOR_HEX");
            if (colorHex != null && !colorHex.isEmpty()) {
                try {
                    android.graphics.drawable.Drawable background = viewColorIndicator.getBackground();
                    if (background instanceof android.graphics.drawable.GradientDrawable) {
                        ((android.graphics.drawable.GradientDrawable) background).setColor(android.graphics.Color.parseColor(colorHex));
                    } else {
                        viewColorIndicator.setBackgroundColor(android.graphics.Color.parseColor(colorHex));
                    }
                } catch (Exception e) {
                    viewColorIndicator.setBackgroundColor(android.graphics.Color.GRAY);
                }
            }

            // Load bike image with Glide using centralized ImageUtils
            String bikeImage = intent.getStringExtra("BIKE_IMAGE");
            String imageUrl = ImageUtils.getFullImageUrl(bikeImage);
 
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder_bike)
                    .error(R.drawable.placeholder_bike)
                    .into(ivBikeImage);
        }
    }

    private void copyToClipboard(String text, String label) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, text);
        clipboard.setPrimaryClip(clip);

        Toast.makeText(this, label + " copied to clipboard", Toast.LENGTH_SHORT).show();
    }

    private void animateCopyIcon(ImageView icon) {
        icon.animate()
                .scaleX(1.2f)
                .scaleY(1.2f)
                .setDuration(150)
                .withEndAction(() -> icon.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(150)
                        .start())
                .start();
    }

    private void calculateInsuranceCountdown(String endDateStr) {
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.ENGLISH);
            java.util.Date endDate = sdf.parse(endDateStr);
            java.util.Date today = new java.util.Date();
            
            long diffInMillis = endDate.getTime() - today.getTime();
            long diffInDays = diffInMillis / (24 * 60 * 60 * 1000);
            
            if (diffInDays > 0) {
                tvExpiryDaysCountdown.setText("Expires in " + diffInDays + " days");
                tvExpiryDaysCountdown.setVisibility(View.VISIBLE);
                if (diffInDays < 30) {
                    tvExpiryDaysCountdown.setTextColor(android.graphics.Color.RED);
                }
            } else {
                tvExpiryDaysCountdown.setText("Policy Expired");
                tvExpiryDaysCountdown.setTextColor(android.graphics.Color.RED);
                tvExpiryDaysCountdown.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            tvExpiryDaysCountdown.setVisibility(View.GONE);
        }
    }

    private void shareBikeDetails() {
        String shareText = "Check out my bike details:\n\n" +
                "Bike: " + tvBikeName.getText() + "\n" +
                "Color: " + tvColor.getText() + "\n" +
                "Variant: " + tvVariant.getText() + "\n" +
                "Policy No: " + tvPolicyNumber.getText();

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My Bike Details");

        startActivity(Intent.createChooser(shareIntent, "Share bike details via"));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}