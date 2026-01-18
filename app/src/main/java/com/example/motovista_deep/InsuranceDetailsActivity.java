package com.example.motovista_deep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.motovista_deep.api.ApiService;
import com.example.motovista_deep.api.RetrofitClient;
import com.example.motovista_deep.models.InsuranceDetailResponse;
import com.example.motovista_deep.utils.ImageUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    private View progressBarFill;

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

    private int orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insurance_details);

        orderId = getIntent().getIntExtra("order_id", -1);

        // Initialize all views
        initializeAllViews();

        // Setup click listeners
        setupClickListeners();

        if (orderId != -1) {
            fetchInsuranceDetails();
        } else {
            // Set dummy data if no order id (for safety)
            setAllData();
        }
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
        progressBarFill = findViewById(R.id.progressBarFill);

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

    private void fetchInsuranceDetails() {
        ApiService apiService = RetrofitClient.getApiService();
        apiService.getInsuranceDetails(orderId).enqueue(new Callback<InsuranceDetailResponse>() {
            @Override
            public void onResponse(Call<InsuranceDetailResponse> call, Response<InsuranceDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    updateUI(response.body().getData());
                } else {
                    Toast.makeText(InsuranceDetailsActivity.this, "Failed to load details", Toast.LENGTH_SHORT).show();
                    setAllData(); // Fallback to dummy data
                }
            }

            @Override
            public void onFailure(Call<InsuranceDetailResponse> call, Throwable t) {
                Toast.makeText(InsuranceDetailsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                setAllData();
            }
        });
    }

    private void updateUI(InsuranceDetailResponse.InsuranceDetailModel data) {
        tvTitle.setText("Insurance Details");

        // Customer Context
        tvCustomerName.setText(data.getFullName() != null ? data.getFullName() : data.getCustomerName());
        tvBikeModel.setText(data.getBikeName());
        
        if (data.getRegistrationDate() != null) {
            tvRegDate.setText("Reg Date: " + formatDate(data.getRegistrationDate()));
        }

        // Profile Image using centralized ImageUtils
        String profileImageUrl = ImageUtils.getFullImageUrl(data.getProfileImage(), ImageUtils.PATH_PROFILE_PICS);
        if (!profileImageUrl.isEmpty()) {
            Glide.with(this)
                    .load(profileImageUrl)
                    .placeholder(R.drawable.default_profile)
                    .into(ivProfile);
        }

        // Renewal Summary
        tvPolicyType.setText("Comprehensive");
        
        calculateAndSetExpiry(data.getFullInsuranceExpiry());

        // Comprehensive plan (Own Damage)
        tvPlan1Status.setText(data.getStatus());
        tvPlan1StartDate.setText(formatDate(data.getRegistrationDate())); // Approximated
        tvPlan1EndDate.setText(formatDate(data.getFullInsuranceExpiry()));
        tvPlan1PolicyNumber.setText("Policy #" + data.getPolicyNumber());

        // Third Party plan
        tvPlan2EndDate.setText(formatDate(data.getThirdPartyExpiry()));
        tvPlan2PolicyNumber.setText("Policy #TP-" + data.getPolicyNumber()); // Placeholder logic
    }

    private String formatDate(String dateStr) {
        if (dateStr == null) return "";
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            Date date = inputFormat.parse(dateStr);
            return outputFormat.format(date);
        } catch (Exception e) {
            return dateStr;
        }
    }

    private void calculateAndSetExpiry(String expiryDateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date expiryDate = sdf.parse(expiryDateStr);
            Date today = new Date();

            long diffInMillis = expiryDate.getTime() - today.getTime();
            long daysLeft = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);

            if (daysLeft < 0) {
                tvDaysCount.setText("0");
                tvExpiryDate.setText("Policy expired on " + formatDate(expiryDateStr));
                updateProgressBar(0);
            } else {
                tvDaysCount.setText(String.valueOf(daysLeft));
                tvExpiryDate.setText("Policy expires on " + formatDate(expiryDateStr));
                
                // Progress bar logic: assume 1 year (365 days) total
                float progress = (daysLeft / 365f) * 100;
                updateProgressBar((int) progress);
            }
        } catch (Exception e) {
            tvDaysCount.setText("-");
        }
    }

    private void updateProgressBar(int percentage) {
        if (progressBarFill != null) {
            percentage = Math.max(0, Math.min(100, percentage));
            android.widget.LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) progressBarFill.getLayoutParams();
            params.weight = percentage;
            progressBarFill.setLayoutParams(params);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}