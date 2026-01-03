package com.example.motovista_deep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class InsuranceLedgerActivity extends AppCompatActivity {

    // Header views
    private ImageButton btnBack;
    private TextView tvTitle;

    // Filter chips
    private CardView chipAll, chipActive, chipExpiringSoon, chipExpired;

    // Summary cards
    private CardView cardTotalPolicies, cardActionNeeded;
    private TextView tvTotalPolicies, tvActionNeeded;

    // Policy cards
    private CardView cardPriyaSingh, cardRahulSharma, cardAmitVerma,
            cardVikramMalhotra, cardSarahJenkins;

    // Status text views
    private TextView tvPriyaStatus, tvRahulStatus, tvAmitStatus,
            tvVikramStatus, tvSarahStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insurance_ledger);

        // Initialize views
        initializeViews();

        // Setup click listeners
        setupClickListeners();

        // Set initial active filter (All)
        setActiveFilter(chipAll);
    }

    private void initializeViews() {
        // Header views
        btnBack = findViewById(R.id.btnBack);
        tvTitle = findViewById(R.id.tvTitle);

        // Filter chips
        chipAll = findViewById(R.id.chipAll);
        chipActive = findViewById(R.id.chipActive);
        chipExpiringSoon = findViewById(R.id.chipExpiringSoon);
        chipExpired = findViewById(R.id.chipExpired);

        // Summary cards
        cardTotalPolicies = findViewById(R.id.cardTotalPolicies);
        cardActionNeeded = findViewById(R.id.cardActionNeeded);
        tvTotalPolicies = findViewById(R.id.tvTotalPolicies);
        tvActionNeeded = findViewById(R.id.tvActionNeeded);

        // Policy cards
        cardPriyaSingh = findViewById(R.id.cardPriyaSingh);
        cardRahulSharma = findViewById(R.id.cardRahulSharma);
        cardAmitVerma = findViewById(R.id.cardAmitVerma);
        cardVikramMalhotra = findViewById(R.id.cardVikramMalhotra);
        cardSarahJenkins = findViewById(R.id.cardSarahJenkins);

        // Status text views
        tvPriyaStatus = findViewById(R.id.tvPriyaStatus);
        tvRahulStatus = findViewById(R.id.tvRahulStatus);
        tvAmitStatus = findViewById(R.id.tvAmitStatus);
        tvVikramStatus = findViewById(R.id.tvVikramStatus);
        tvSarahStatus = findViewById(R.id.tvSarahStatus);
    }

    private void setupClickListeners() {
        // Back button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Filter chips
        chipAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveFilter(chipAll);
                Toast.makeText(InsuranceLedgerActivity.this, "Showing all policies", Toast.LENGTH_SHORT).show();
                // Here you would filter the list to show all policies
            }
        });

        chipActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveFilter(chipActive);
                Toast.makeText(InsuranceLedgerActivity.this, "Showing active policies", Toast.LENGTH_SHORT).show();
                // Here you would filter the list to show only active policies
            }
        });

        chipExpiringSoon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveFilter(chipExpiringSoon);
                Toast.makeText(InsuranceLedgerActivity.this, "Showing expiring soon policies", Toast.LENGTH_SHORT).show();
                // Here you would filter the list to show only expiring soon policies
            }
        });

        chipExpired.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveFilter(chipExpired);
                Toast.makeText(InsuranceLedgerActivity.this, "Showing expired policies", Toast.LENGTH_SHORT).show();
                // Here you would filter the list to show only expired policies
            }
        });

        // Policy card clicks
        cardPriyaSingh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPolicyDetails("Priya Singh", "YM-8832-X", "Expiring Soon", "Oct 24, 2025");
            }
        });

        // In your card click listeners:
        cardRahulSharma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to InsuranceDetailsActivity
                Intent intent = new Intent(InsuranceLedgerActivity.this, InsuranceDetailsActivity.class);
                intent.putExtra("customer_name", "Rahul Sharma");
                intent.putExtra("policy_number", "HC-2910-A");
                intent.putExtra("status", "Active");
                intent.putExtra("end_date", "Dec 12, 2025");
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        cardAmitVerma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPolicyDetails("Amit Verma", "RE-1102-Z", "Expired", "Nov 05, 2023");
            }
        });

        cardVikramMalhotra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPolicyDetails("Vikram Malhotra", "KT-9912-Q", "Active", "Jan 15, 2026");
            }
        });

        cardSarahJenkins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPolicyDetails("Sarah Jenkins", "TS-4412-B", "Expiring Soon", "Nov 10, 2025");
            }
        });

        // Summary cards (optional click listeners)
        cardTotalPolicies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(InsuranceLedgerActivity.this, "Total Policies: 1,248", Toast.LENGTH_SHORT).show();
            }
        });

        cardActionNeeded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(InsuranceLedgerActivity.this, "42 policies need action", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setActiveFilter(CardView activeChip) {
        // Reset all chips to inactive state
        chipAll.setCardBackgroundColor(ContextCompat.getColor(this, R.color.gray_200));
        chipActive.setCardBackgroundColor(ContextCompat.getColor(this, R.color.gray_200));
        chipExpiringSoon.setCardBackgroundColor(ContextCompat.getColor(this, R.color.gray_200));
        chipExpired.setCardBackgroundColor(ContextCompat.getColor(this, R.color.gray_200));

        chipAll.setCardElevation(0);
        chipActive.setCardElevation(0);
        chipExpiringSoon.setCardElevation(0);
        chipExpired.setCardElevation(0);

        // Set active chip
        activeChip.setCardBackgroundColor(ContextCompat.getColor(this, R.color.primary_color));
        activeChip.setCardElevation(2);

        // Update text colors for better visibility
        // Note: You might want to create separate TextView references for chip text
    }

    // In the showPolicyDetails method or card click listeners:
    private void showPolicyDetails(String customerName, String policyNumber, String status, String endDate) {
        // Navigate to InsuranceDetailsActivity
        Intent intent = new Intent(InsuranceLedgerActivity.this, InsuranceDetailsActivity.class);
        intent.putExtra("customer_name", customerName);
        intent.putExtra("policy_number", policyNumber);
        intent.putExtra("status", status);
        intent.putExtra("end_date", endDate);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}