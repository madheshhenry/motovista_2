package com.example.motovista_deep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class InsuranceDetailsActivity extends AppCompatActivity {

    // Header views
    private ImageButton btnBack;
    private TextView tvTitle;

    // Customer Context
    private CardView cardCustomerContext;
    private de.hdodenhof.circleimageview.CircleImageView ivProfile;
    private TextView tvCustomerName, tvCustomerBadge, tvBikeModel, tvRegDate;

    // Renewal Summary
    private CardView cardRenewalSummary;
    private TextView tvRenewalLabel, tvPolicyType, tvDaysCount, tvDaysLabel, tvExpiryDate;

    // Coverage Plans
    private TextView tvCoverageHeader;

    // Comprehensive Plan
    private CardView cardComprehensive;
    private TextView tvPlan1Title, tvPlan1Subtitle, tvPlan1Status;
    private TextView tvPlan1StartDate, tvPlan1EndDate, tvPlan1PolicyNumber, tvPlan1ViewDocument;

    // Third Party Plan
    private CardView cardThirdParty;
    private TextView tvPlan2Title, tvPlan2Subtitle, tvPlan2Status;
    private TextView tvPlan2StartDate, tvPlan2EndDate, tvPlan2PolicyNumber, tvPlan2ViewDocument;

    // FAB Area
    private CardView btnUpdatePolicy;
    private TextView tvUpdateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insurance_details);

        // Initialize all views
        initializeAllViews();

        // Set all data
        setAllData();

        // Setup click listeners
        setupClickListeners();
    }

    private void initializeAllViews() {
        // Header views
        btnBack = findViewById(R.id.btnBack);
        tvTitle = findViewById(R.id.tvTitle);

        // Customer Context
        cardCustomerContext = findViewById(R.id.cardCustomerContext);
        ivProfile = findViewById(R.id.ivProfile);
        tvCustomerName = findViewById(R.id.tvCustomerName);
        tvCustomerBadge = findViewById(R.id.tvCustomerBadge);
        tvBikeModel = findViewById(R.id.tvBikeModel);
        tvRegDate = findViewById(R.id.tvRegDate);

        // Renewal Summary
        cardRenewalSummary = findViewById(R.id.cardRenewalSummary);
        tvRenewalLabel = findViewById(R.id.tvRenewalLabel);
        tvPolicyType = findViewById(R.id.tvPolicyType);
        tvDaysCount = findViewById(R.id.tvDaysCount);
        tvDaysLabel = findViewById(R.id.tvDaysLabel);
        tvExpiryDate = findViewById(R.id.tvExpiryDate);

        // Coverage Header
        tvCoverageHeader = findViewById(R.id.tvCoverageHeader);

        // Comprehensive Plan
        cardComprehensive = findViewById(R.id.cardComprehensive);
        tvPlan1Title = findViewById(R.id.tvPlan1Title);
        tvPlan1Subtitle = findViewById(R.id.tvPlan1Subtitle);
        tvPlan1Status = findViewById(R.id.tvPlan1Status);
        tvPlan1StartDate = findViewById(R.id.tvPlan1StartDate);
        tvPlan1EndDate = findViewById(R.id.tvPlan1EndDate);
        tvPlan1PolicyNumber = findViewById(R.id.tvPlan1PolicyNumber);
        tvPlan1ViewDocument = findViewById(R.id.tvPlan1ViewDocument);

        // Third Party Plan
        cardThirdParty = findViewById(R.id.cardThirdParty);
        tvPlan2Title = findViewById(R.id.tvPlan2Title);
        tvPlan2Subtitle = findViewById(R.id.tvPlan2Subtitle);
        tvPlan2Status = findViewById(R.id.tvPlan2Status);
        tvPlan2StartDate = findViewById(R.id.tvPlan2StartDate);
        tvPlan2EndDate = findViewById(R.id.tvPlan2EndDate);
        tvPlan2PolicyNumber = findViewById(R.id.tvPlan2PolicyNumber);
        tvPlan2ViewDocument = findViewById(R.id.tvPlan2ViewDocument);

        // FAB Area
        btnUpdatePolicy = findViewById(R.id.btnUpdatePolicy);
        tvUpdateButton = findViewById(R.id.tvUpdateButton);
    }

    private void setAllData() {
        // DEBUG: Set all text directly to ensure it appears
        tvTitle.setText("Insurance Details");

        // Customer data
        tvCustomerName.setText("Rahul Sharma");
        tvCustomerBadge.setText("Customer");
        tvBikeModel.setText("Royal Enfield Classic 350");
        tvRegDate.setText("Reg Date: Oct 24, 2023");

        // Renewal summary
        tvRenewalLabel.setText("Next Renewal In");
        tvPolicyType.setText("Comprehensive");
        tvDaysCount.setText("245");
        tvDaysLabel.setText("Days");
        tvExpiryDate.setText("Policy expires on Oct 23, 2024");

        // Coverage header
        tvCoverageHeader.setText("Coverage Plans");

        // Comprehensive plan
        tvPlan1Title.setText("Own Damage");
        tvPlan1Subtitle.setText("1 Year Comprehensive");
        tvPlan1Status.setText("Active");
        tvPlan1StartDate.setText("Oct 24, 2023");
        tvPlan1EndDate.setText("Oct 23, 2024");
        tvPlan1PolicyNumber.setText("Policy #987654321");
        tvPlan1ViewDocument.setText("View Document");

        // Third Party plan
        tvPlan2Title.setText("Third Party Liability");
        tvPlan2Subtitle.setText("5 Year TP Cover");
        tvPlan2Status.setText("Active");
        tvPlan2StartDate.setText("Oct 24, 2023");
        tvPlan2EndDate.setText("Oct 23, 2028");
        tvPlan2PolicyNumber.setText("Policy #TP-88442109");
        tvPlan2ViewDocument.setText("View Document");

        // Update button
        tvUpdateButton.setText("Update Policy Details");
    }

    private void setupClickListeners() {
        // Back button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // View Document buttons
        tvPlan1ViewDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(InsuranceDetailsActivity.this,
                        "Viewing Comprehensive Document",
                        Toast.LENGTH_SHORT).show();
            }
        });

        tvPlan2ViewDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(InsuranceDetailsActivity.this,
                        "Viewing Third Party Document",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Update Policy button
        // Update Policy button
        btnUpdatePolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InsuranceDetailsActivity.this,
                        UpdateInsurancePolicyActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        // Card clicks
        cardComprehensive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(InsuranceDetailsActivity.this,
                        "Own Damage - Comprehensive Plan",
                        Toast.LENGTH_SHORT).show();
            }
        });

        cardThirdParty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(InsuranceDetailsActivity.this,
                        "Third Party Liability - TP Cover",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}