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

public class BikeDetailsViewActivity extends AppCompatActivity {

    // Views
    private CardView btnBack, btnShare;
    private LinearLayout layoutEngineNumber, layoutChassisNumber;
    private ImageView ivCopyEngine, ivCopyChassis;
    private ImageView ivBikeImage;

    // Text Views
    private TextView tvBikeName, tvRegNumber, tvPurchaseDate;
    private TextView tvEngineNumber, tvChassisNumber, tvColor, tvVariant;
    private TextView tvInsurer, tvPolicyNumber, tvInsuranceType, tvInsuranceStart, tvInsuranceEnd;
    private TextView tvRegistrationStatus, tvRcBookStatus, tvNumberPlateStatus;

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
        tvRegNumber = findViewById(R.id.tvRegNumber);
        tvPurchaseDate = findViewById(R.id.tvPurchaseDate);

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
        tvInsurer = findViewById(R.id.tvInsurer);
        tvPolicyNumber = findViewById(R.id.tvPolicyNumber);
        tvInsuranceType = findViewById(R.id.tvInsuranceType);
        tvInsuranceStart = findViewById(R.id.tvInsuranceStart);
        tvInsuranceEnd = findViewById(R.id.tvInsuranceEnd);

        // Registration
        tvRegistrationStatus = findViewById(R.id.tvRegistrationStatus);
        tvRcBookStatus = findViewById(R.id.tvRcBookStatus);
        tvNumberPlateStatus = findViewById(R.id.tvNumberPlateStatus);

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
    }

    private void loadBikeData() {
        // Get data from intent
        Intent intent = getIntent();
        if (intent != null) {
            // Bike info
            String bikeName = intent.getStringExtra("BIKE_NAME");
            String regNumber = intent.getStringExtra("REG_NUMBER");
            String purchaseDate = intent.getStringExtra("PURCHASE_DATE");
            String color = intent.getStringExtra("COLOR");
            String variant = intent.getStringExtra("VARIANT");
            String engineNumber = intent.getStringExtra("ENGINE_NUMBER");
            String chassisNumber = intent.getStringExtra("CHASSIS_NUMBER");

            // Insurance info
            String insurer = intent.getStringExtra("INSURER");
            String policyNumber = intent.getStringExtra("POLICY_NUMBER");
            String insuranceType = intent.getStringExtra("INSURANCE_TYPE");
            String insuranceStart = intent.getStringExtra("INSURANCE_START");
            String insuranceEnd = intent.getStringExtra("INSURANCE_END");

            // Registration info
            String registrationStatus = intent.getStringExtra("REGISTRATION_STATUS");
            String rcBookStatus = intent.getStringExtra("RC_BOOK_STATUS");
            String numberPlateStatus = intent.getStringExtra("NUMBER_PLATE_STATUS");

            // Set values if provided, otherwise use defaults
            if (bikeName != null) tvBikeName.setText(bikeName);
            if (regNumber != null) tvRegNumber.setText(regNumber);
            if (purchaseDate != null) tvPurchaseDate.setText("Purchased " + purchaseDate);
            if (color != null) tvColor.setText(color);
            if (variant != null) tvVariant.setText(variant);
            if (engineNumber != null) tvEngineNumber.setText(engineNumber);
            if (chassisNumber != null) tvChassisNumber.setText(chassisNumber);
            if (insurer != null) tvInsurer.setText(insurer);
            if (policyNumber != null) tvPolicyNumber.setText(policyNumber);
            if (insuranceType != null) tvInsuranceType.setText(insuranceType);
            if (insuranceStart != null) tvInsuranceStart.setText(insuranceStart);
            if (insuranceEnd != null) tvInsuranceEnd.setText(insuranceEnd);
            if (registrationStatus != null) tvRegistrationStatus.setText(registrationStatus);
            if (rcBookStatus != null) tvRcBookStatus.setText(rcBookStatus);
            if (numberPlateStatus != null) tvNumberPlateStatus.setText(numberPlateStatus);

            // You can also load image from URL if provided
            // String imageUrl = intent.getStringExtra("IMAGE_URL");
            // if (imageUrl != null) {
            //     Glide.with(this).load(imageUrl).into(ivBikeImage);
            // }
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

    private void shareBikeDetails() {
        String shareText = "Check out my bike details:\n\n" +
                "Bike: " + tvBikeName.getText() + "\n" +
                "Registration: " + tvRegNumber.getText() + "\n" +
                "Color: " + tvColor.getText() + "\n" +
                "Variant: " + tvVariant.getText() + "\n" +
                "Insurance: " + tvInsurer.getText();

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