package com.example.motovista_deep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AdminDashboardActivity extends AppCompatActivity {

    private CardView cardEmiLedger, cardInsuranceLedger, cardRegistrationLedger,
            cardApplication, cardSales, cardRequestedCustomer;
    private Button btnAddNewBike, btnAddSecondHandBike;
    private LinearLayout tabDashboard, tabInventory, tabBikes, tabCustomers, tabSettings;
    private LinearLayout iconContainerEmi, iconContainerInsurance, iconContainerRegistration,
            iconContainerApplication, iconContainerSales, iconContainerRequestedCustomer;
    private ImageView btnMenu, btnNotifications;
    private TextView tvWelcome;

    // Add these ImageViews for bottom navigation icons
    private ImageView ivDashboard, ivInventory, ivBikes, ivCustomers, ivSettings;
    private TextView tvDashboard, tvInventory, tvBikes, tvCustomers, tvSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // Initialize views
        initializeViews();

        // Setup icon backgrounds with different colors
        setupIconBackgrounds();

        // Setup click listeners
        setupClickListeners();

        // Set welcome text with admin name
        String adminName = "Admin"; // Change this to get from SharedPreferences
        tvWelcome.setText("Welcome, " + adminName);

        // Set initial active tab (Dashboard)
        setActiveTab(tabDashboard);
    }

    private void initializeViews() {
        // Find all views
        cardEmiLedger = findViewById(R.id.cardEmiLedger);
        cardInsuranceLedger = findViewById(R.id.cardInsuranceLedger);
        cardRegistrationLedger = findViewById(R.id.cardRegistrationLedger);
        cardApplication = findViewById(R.id.cardApplication);
        cardSales = findViewById(R.id.cardSales);
        cardRequestedCustomer = findViewById(R.id.cardRequestedCustomer);

        btnAddNewBike = findViewById(R.id.btnAddNewBike);
        btnAddSecondHandBike = findViewById(R.id.btnAddSecondHandBike);

        tabDashboard = findViewById(R.id.tabDashboard);
        tabInventory = findViewById(R.id.tabInventory);
        tabBikes = findViewById(R.id.tabBikes);
        tabCustomers = findViewById(R.id.tabCustomers);
        tabSettings = findViewById(R.id.tabSettings);

        btnMenu = findViewById(R.id.btnMenu);
        btnNotifications = findViewById(R.id.btnNotifications);
        tvWelcome = findViewById(R.id.tvWelcome);

        // Initialize icon containers
        iconContainerEmi = findViewById(R.id.iconContainerEmi);
        iconContainerInsurance = findViewById(R.id.iconContainerInsurance);
        iconContainerRegistration = findViewById(R.id.iconContainerRegistration);
        iconContainerApplication = findViewById(R.id.iconContainerApplication);
        iconContainerSales = findViewById(R.id.iconContainerSales);
        iconContainerRequestedCustomer = findViewById(R.id.iconContainerRequestedCustomer);

        // Initialize bottom navigation ImageViews and TextViews
        ivDashboard = (ImageView) tabDashboard.getChildAt(0);
        ivInventory = (ImageView) tabInventory.getChildAt(0);
        ivBikes = (ImageView) tabBikes.getChildAt(0);
        ivCustomers = (ImageView) tabCustomers.getChildAt(0);
        ivSettings = (ImageView) tabSettings.getChildAt(0);

        tvDashboard = (TextView) tabDashboard.getChildAt(1);
        tvInventory = (TextView) tabInventory.getChildAt(1);
        tvBikes = (TextView) tabBikes.getChildAt(1);
        tvCustomers = (TextView) tabCustomers.getChildAt(1);
        tvSettings = (TextView) tabSettings.getChildAt(1);
    }

    private void setupIconBackgrounds() {
        // Set different background colors for each icon container
        if (iconContainerEmi != null) {
            iconContainerEmi.setBackgroundColor(ContextCompat.getColor(this, R.color.icon_bg_red));
        }
        if (iconContainerInsurance != null) {
            iconContainerInsurance.setBackgroundColor(ContextCompat.getColor(this, R.color.icon_bg_yellow));
        }
        if (iconContainerRegistration != null) {
            iconContainerRegistration.setBackgroundColor(ContextCompat.getColor(this, R.color.icon_bg_blue));
        }
        if (iconContainerApplication != null) {
            iconContainerApplication.setBackgroundColor(ContextCompat.getColor(this, R.color.icon_bg_green));
        }
        if (iconContainerSales != null) {
            iconContainerSales.setBackgroundColor(ContextCompat.getColor(this, R.color.icon_bg_purple));
        }
        if (iconContainerRequestedCustomer != null) {
            iconContainerRequestedCustomer.setBackgroundColor(ContextCompat.getColor(this, R.color.icon_bg_indigo));
        }
    }

    private void setupClickListeners() {
        // Menu button click
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AdminDashboardActivity.this, "Menu clicked", Toast.LENGTH_SHORT).show();
                // You can add a drawer or menu here
            }
        });

        // Notifications button click
        // Notifications button click
        btnNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to notifications screen
                Intent intent = new Intent(AdminDashboardActivity.this, NotificationsActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        // LEDGER CARD CLICKS - Updated version
        cardEmiLedger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to EMI Ledger screen
                Intent intent = new Intent(AdminDashboardActivity.this, EmiLedgerActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        // In the cardInsuranceLedger onClick listener
        cardInsuranceLedger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Insurance Ledger screen
                Intent intent = new Intent(AdminDashboardActivity.this, InsuranceLedgerActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        // Find this code in your setupClickListeners() method:
        cardRegistrationLedger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AdminDashboardActivity.this, "Registration Ledger clicked", Toast.LENGTH_SHORT).show();
                // Navigate to Registration Ledger screen - ADD THIS:
                Intent intent = new Intent(AdminDashboardActivity.this, RegistrationLedgerActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        cardApplication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Application screen
                Intent intent = new Intent(AdminDashboardActivity.this, AdminRequestedCustomersActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        // Replace the existing cardSales click listener with this:
        cardSales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Sales History screen
                Intent intent = new Intent(AdminDashboardActivity.this, SalesHistoryActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        // In the cardRequestedCustomer onClick listener
        cardRequestedCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Requested Customer screen
                Intent intent = new Intent(AdminDashboardActivity.this, RequestedCustomersActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        // Add bike buttons
        btnAddNewBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AdminDashboardActivity.this, "Add New Bike clicked", Toast.LENGTH_SHORT).show();
                // Navigate to Add New Bike screen
                Intent intent = new Intent(AdminDashboardActivity.this, AddBikeActivity.class);
                startActivity(intent);
            }
        });

        btnAddSecondHandBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Add Second-Hand Bike screen
                Intent intent = new Intent(AdminDashboardActivity.this, AddSecondHandBikeActivity.class);
                startActivity(intent);
            }
        });

        // Bottom navigation tabs - FIXED VERSION
        tabDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Already on dashboard
                setActiveTab(tabDashboard);
                Toast.makeText(AdminDashboardActivity.this, "Dashboard selected", Toast.LENGTH_SHORT).show();
                // Refresh dashboard content if needed
            }
        });

        tabInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveTab(tabInventory);
                // Navigate to Inventory screen
                Intent intent = new Intent(AdminDashboardActivity.this, InventoryActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        tabBikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveTab(tabBikes);
                Toast.makeText(AdminDashboardActivity.this, "Bikes selected", Toast.LENGTH_SHORT).show();
                // Navigate to Bikes screen - UPDATED
                Intent intent = new Intent(AdminDashboardActivity.this, BikeInventoryActivity.class);
                startActivity(intent);
            }
        });

        tabCustomers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveTab(tabCustomers);
                Toast.makeText(AdminDashboardActivity.this, "Customers selected", Toast.LENGTH_SHORT).show();
                // Navigate to Customers screen - ADD THIS LINE
                Intent intent = new Intent(AdminDashboardActivity.this, CustomersActivity.class);
                startActivity(intent);
            }
        });

        tabSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveTab(tabSettings);
                Toast.makeText(AdminDashboardActivity.this, "Settings selected", Toast.LENGTH_SHORT).show();
                // Navigate to Settings screen - ADD THIS LINE
                Intent intent = new Intent(AdminDashboardActivity.this, AdminSettingsActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }

    private void setActiveTab(LinearLayout activeTab) {
        // Reset all tabs to inactive state
        resetAllTabs();

        // Set the active tab
        if (activeTab == tabDashboard) {
            ivDashboard.setColorFilter(ContextCompat.getColor(this, R.color.primary_color));
            tvDashboard.setTextColor(ContextCompat.getColor(this, R.color.primary_color));
            tvDashboard.setTypeface(tvDashboard.getTypeface(), android.graphics.Typeface.BOLD);
        }
        else if (activeTab == tabInventory) {
            ivInventory.setColorFilter(ContextCompat.getColor(this, R.color.primary_color));
            tvInventory.setTextColor(ContextCompat.getColor(this, R.color.primary_color));
            tvInventory.setTypeface(tvInventory.getTypeface(), android.graphics.Typeface.BOLD);
        }
        else if (activeTab == tabBikes) {
            ivBikes.setColorFilter(ContextCompat.getColor(this, R.color.primary_color));
            tvBikes.setTextColor(ContextCompat.getColor(this, R.color.primary_color));
            tvBikes.setTypeface(tvBikes.getTypeface(), android.graphics.Typeface.BOLD);
        }
        else if (activeTab == tabCustomers) {
            ivCustomers.setColorFilter(ContextCompat.getColor(this, R.color.primary_color));
            tvCustomers.setTextColor(ContextCompat.getColor(this, R.color.primary_color));
            tvCustomers.setTypeface(tvCustomers.getTypeface(), android.graphics.Typeface.BOLD);
        }
        else if (activeTab == tabSettings) {
            ivSettings.setColorFilter(ContextCompat.getColor(this, R.color.primary_color));
            tvSettings.setTextColor(ContextCompat.getColor(this, R.color.primary_color));
            tvSettings.setTypeface(tvSettings.getTypeface(), android.graphics.Typeface.BOLD);
        }
    }

    private void resetAllTabs() {
        // Reset Dashboard tab
        ivDashboard.setColorFilter(ContextCompat.getColor(this, R.color.gray_400));
        tvDashboard.setTextColor(ContextCompat.getColor(this, R.color.gray_400));
        tvDashboard.setTypeface(null, android.graphics.Typeface.NORMAL);

        // Reset Inventory tab
        ivInventory.setColorFilter(ContextCompat.getColor(this, R.color.gray_400));
        tvInventory.setTextColor(ContextCompat.getColor(this, R.color.gray_400));
        tvInventory.setTypeface(null, android.graphics.Typeface.NORMAL);

        // Reset Bikes tab
        ivBikes.setColorFilter(ContextCompat.getColor(this, R.color.gray_400));
        tvBikes.setTextColor(ContextCompat.getColor(this, R.color.gray_400));
        tvBikes.setTypeface(null, android.graphics.Typeface.NORMAL);

        // Reset Customers tab
        ivCustomers.setColorFilter(ContextCompat.getColor(this, R.color.gray_400));
        tvCustomers.setTextColor(ContextCompat.getColor(this, R.color.gray_400));
        tvCustomers.setTypeface(null, android.graphics.Typeface.NORMAL);

        // Reset Settings tab
        ivSettings.setColorFilter(ContextCompat.getColor(this, R.color.gray_400));
        tvSettings.setTextColor(ContextCompat.getColor(this, R.color.gray_400));
        tvSettings.setTypeface(null, android.graphics.Typeface.NORMAL);
    }


    @Override
    protected void onResume() {
        super.onResume();
        checkActiveSession();
    }

    private void checkActiveSession() {
        com.example.motovista_deep.helpers.OrderSessionManager sessionManager = 
            new com.example.motovista_deep.helpers.OrderSessionManager(this);
            
        if (sessionManager.isSessionActive()) {
            com.example.motovista_deep.helpers.OrderSessionManager.Step currentStep = sessionManager.getCurrentStep();
            Intent intent = null;
            
            switch (currentStep) {
                case PAYMENT_CONFIRMED:
                    intent = new Intent(this, PaymentConfirmedActivity.class);
                    break;
                case DOCUMENTS:
                    intent = new Intent(this, DocumentsActivity.class);
                    break;
                case COMPLETED:
                    intent = new Intent(this, OrderCompletedActivity.class);
                    break;
            }
            
            if (intent != null) {
                // Restore request ID if possible to pass as extra, though activities handle session restoration themselves now.
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish(); // Close Dashboard so user can't just back into it without clearing session
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Double press to exit
        Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
    }
}