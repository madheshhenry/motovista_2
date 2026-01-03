package com.example.motovista_deep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class EmiLedgerActivity extends AppCompatActivity {

    // Header Views
    private ImageView btnBack, btnSearch;
    private TextView tvTitle;

    // Filter Chips
    private Button chipAll, chipRunning, chipCompleted;

    // Customer Cards
    private CardView cardCustomer1, cardCustomer2, cardCustomer3, cardCustomer4, cardCustomer5;

    // Customer Info Views (for dynamic updates if needed)
    private TextView tvCustomerName1, tvBikeModel1, tvStatus1, tvMonthlyEmi1, tvPendingEmi1;
    private TextView tvCustomerName2, tvBikeModel2, tvStatus2, tvMonthlyEmi2, tvPendingEmi2;
    private TextView tvCustomerName3, tvBikeModel3, tvStatus3, tvMonthlyEmi3, tvPendingEmi3;
    private TextView tvCustomerName4, tvBikeModel4, tvStatus4, tvMonthlyEmi4, tvPendingEmi4;
    private TextView tvCustomerName5, tvBikeModel5, tvStatus5, tvMonthlyEmi5, tvPendingEmi5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emi_ledger);

        // Initialize all views
        initializeViews();

        // Setup click listeners
        setupClickListeners();

        // Set initial active filter (All)
        setActiveFilter(chipAll);

        // Load customer data (static for now, will be dynamic with backend)
        loadCustomerData();
    }

    private void initializeViews() {
        // Header views
        btnBack = findViewById(R.id.btnBack);
        btnSearch = findViewById(R.id.btnSearch);
        tvTitle = findViewById(R.id.tvTitle);

        // Filter chips
        chipAll = findViewById(R.id.chipAll);
        chipRunning = findViewById(R.id.chipRunning);
        chipCompleted = findViewById(R.id.chipCompleted);

        // Customer cards
        cardCustomer1 = findViewById(R.id.cardCustomer1);
        cardCustomer2 = findViewById(R.id.cardCustomer2);
        cardCustomer3 = findViewById(R.id.cardCustomer3);
        cardCustomer4 = findViewById(R.id.cardCustomer4);
        cardCustomer5 = findViewById(R.id.cardCustomer5);

        // Initialize all text views for each customer
        initializeCustomerViews();
    }

    private void initializeCustomerViews() {
        // Customer 1: Rahul Sharma
        tvCustomerName1 = findViewById(R.id.tvCustomerName1);
        tvBikeModel1 = findViewById(R.id.tvBikeModel1);
        tvStatus1 = findViewById(R.id.tvStatus1);
        tvMonthlyEmi1 = findViewById(R.id.tvMonthlyEmi1);
        tvPendingEmi1 = findViewById(R.id.tvPendingEmi1);

        // Customer 2: Anita Desai
        tvCustomerName2 = findViewById(R.id.tvCustomerName2);
        tvBikeModel2 = findViewById(R.id.tvBikeModel2);
        tvStatus2 = findViewById(R.id.tvStatus2);
        tvMonthlyEmi2 = findViewById(R.id.tvMonthlyEmi2);
        tvPendingEmi2 = findViewById(R.id.tvPendingEmi2);

        // Customer 3: Vikram Singh
        tvCustomerName3 = findViewById(R.id.tvCustomerName3);
        tvBikeModel3 = findViewById(R.id.tvBikeModel3);
        tvStatus3 = findViewById(R.id.tvStatus3);
        tvMonthlyEmi3 = findViewById(R.id.tvMonthlyEmi3);
        tvPendingEmi3 = findViewById(R.id.tvPendingEmi3);

        // Customer 4: Arjun Das
        tvCustomerName4 = findViewById(R.id.tvCustomerName4);
        tvBikeModel4 = findViewById(R.id.tvBikeModel4);
        tvStatus4 = findViewById(R.id.tvStatus4);
        tvMonthlyEmi4 = findViewById(R.id.tvMonthlyEmi4);
        tvPendingEmi4 = findViewById(R.id.tvPendingEmi4);

        // Customer 5: Priya Patel
        tvCustomerName5 = findViewById(R.id.tvCustomerName5);
        tvBikeModel5 = findViewById(R.id.tvBikeModel5);
        tvStatus5 = findViewById(R.id.tvStatus5);
        tvMonthlyEmi5 = findViewById(R.id.tvMonthlyEmi5);
        tvPendingEmi5 = findViewById(R.id.tvPendingEmi5);
    }

    private void setupClickListeners() {
        // Back button click
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Search button click
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EmiLedgerActivity.this, "Search functionality coming soon", Toast.LENGTH_SHORT).show();
                // Implement search functionality here
            }
        });

        // Filter chip clicks
        chipAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveFilter(chipAll);
                showAllCustomers();
            }
        });

        chipRunning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveFilter(chipRunning);
                showRunningCustomers();
            }
        });

        chipCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveFilter(chipCompleted);
                showCompletedCustomers();
            }
        });

        // Customer card clicks
        cardCustomer1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCustomerDetails("Rahul Sharma", "Running");
            }
        });

        cardCustomer2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCustomerDetails("Anita Desai", "Completed");
            }
        });

        cardCustomer3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCustomerDetails("Vikram Singh", "Running");
            }
        });

        cardCustomer4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCustomerDetails("Arjun Das", "Running");
            }
        });

        cardCustomer5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCustomerDetails("Priya Patel", "Completed");
            }
        });
    }

    private void setActiveFilter(Button activeChip) {
        // Reset all chips
        chipAll.setBackgroundResource(R.drawable.outline_button);
        chipAll.setTextColor(ContextCompat.getColor(this, R.color.text_primary_light));

        chipRunning.setBackgroundResource(R.drawable.outline_button);
        chipRunning.setTextColor(ContextCompat.getColor(this, R.color.text_primary_light));

        chipCompleted.setBackgroundResource(R.drawable.outline_button);
        chipCompleted.setTextColor(ContextCompat.getColor(this, R.color.text_primary_light));

        // Set active chip
        activeChip.setBackgroundResource(R.drawable.primary_button);
        activeChip.setTextColor(ContextCompat.getColor(this, R.color.white));
    }

    private void showAllCustomers() {
        // Show all customer cards
        cardCustomer1.setVisibility(View.VISIBLE);
        cardCustomer2.setVisibility(View.VISIBLE);
        cardCustomer3.setVisibility(View.VISIBLE);
        cardCustomer4.setVisibility(View.VISIBLE);
        cardCustomer5.setVisibility(View.VISIBLE);
    }

    private void showRunningCustomers() {
        // Show only running customers
        cardCustomer1.setVisibility(View.VISIBLE);  // Running
        cardCustomer2.setVisibility(View.GONE);     // Completed
        cardCustomer3.setVisibility(View.VISIBLE);  // Running
        cardCustomer4.setVisibility(View.VISIBLE);  // Running
        cardCustomer5.setVisibility(View.GONE);     // Completed
    }

    private void showCompletedCustomers() {
        // Show only completed customers
        cardCustomer1.setVisibility(View.GONE);     // Running
        cardCustomer2.setVisibility(View.VISIBLE);  // Completed
        cardCustomer3.setVisibility(View.GONE);     // Running
        cardCustomer4.setVisibility(View.GONE);     // Running
        cardCustomer5.setVisibility(View.VISIBLE);  // Completed
    }

    private void openCustomerDetails(String customerName, String status) {
        // Navigate to EMI details screen
        Intent intent = new Intent(EmiLedgerActivity.this, EmiDetailsActivity.class);
        intent.putExtra("CUSTOMER_NAME", customerName);
        intent.putExtra("STATUS", status);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void loadCustomerData() {
        // This is static data for now
        // In the future, this will come from backend API

        // Customer 1: Rahul Sharma
        tvCustomerName1.setText("Rahul Sharma");
        tvBikeModel1.setText("Royal Enfield Classic 350");
        tvStatus1.setText("Running");
        tvMonthlyEmi1.setText("₹5,200/mo");
        tvPendingEmi1.setText("12 EMIs");

        // Customer 2: Anita Desai
        tvCustomerName2.setText("Anita Desai");
        tvBikeModel2.setText("Honda Activa 6G");
        tvStatus2.setText("Completed");
        tvMonthlyEmi2.setText("₹0/mo");
        tvPendingEmi2.setText("0 EMIs");

        // Customer 3: Vikram Singh
        tvCustomerName3.setText("Vikram Singh");
        tvBikeModel3.setText("Yamaha R15 V4");
        tvStatus3.setText("Running");
        tvMonthlyEmi3.setText("₹4,500/mo");
        tvPendingEmi3.setText("6 EMIs");

        // Customer 4: Arjun Das
        tvCustomerName4.setText("Arjun Das");
        tvBikeModel4.setText("KTM Duke 200");
        tvStatus4.setText("Running");
        tvMonthlyEmi4.setText("₹6,100/mo");
        tvPendingEmi4.setText("18 EMIs");

        // Customer 5: Priya Patel
        tvCustomerName5.setText("Priya Patel");
        tvBikeModel5.setText("TVS Jupiter");
        tvStatus5.setText("Completed");
        tvMonthlyEmi5.setText("₹0/mo");
        tvPendingEmi5.setText("0 EMIs");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}