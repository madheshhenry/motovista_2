package com.example.motovista_deep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AdminSettingsActivity extends AppCompatActivity {

    private ImageView btnBack;
    private LinearLayout cardManageProfile, cardChangePassword, cardSecuritySettings,
            cardUserManagement, cardRoleManagement, cardNotificationPreferences,
            cardSystemConfigurations, cardMaintenanceMode, cardHelpSupport, cardAbout;
    private Button btnLogout;
    private SwitchCompat switchMaintenanceMode;

    // Bottom navigation
    private LinearLayout tabDashboard, tabInventory, tabBikes, tabCustomers, tabSettings;
    private ImageView ivDashboard, ivInventory, ivBikes, ivCustomers, ivSettings;
    private TextView tvDashboard, tvInventory, tvBikes, tvCustomers, tvSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_settings);

        // Initialize views
        initializeViews();

        // Setup click listeners
        setupClickListeners();

        // Set Settings tab as active
        setActiveTab(tabSettings);
    }

    private void initializeViews() {
        // Find all views
        btnBack = findViewById(R.id.btnBack);

        // Card views - These are LinearLayouts inside CardView, not CardView itself
        cardManageProfile = findViewById(R.id.cardManageProfile);
        cardChangePassword = findViewById(R.id.cardChangePassword);
        cardSecuritySettings = findViewById(R.id.cardSecuritySettings);
        cardUserManagement = findViewById(R.id.cardUserManagement);
        cardRoleManagement = findViewById(R.id.cardRoleManagement);
        cardNotificationPreferences = findViewById(R.id.cardNotificationPreferences);
        cardSystemConfigurations = findViewById(R.id.cardSystemConfigurations);
        cardMaintenanceMode = findViewById(R.id.cardMaintenanceMode);
        cardHelpSupport = findViewById(R.id.cardHelpSupport);
        cardAbout = findViewById(R.id.cardAbout);

        // Switch - Find it within the LinearLayout
        switchMaintenanceMode = findViewById(R.id.switchMaintenanceMode);

        // Button
        btnLogout = findViewById(R.id.btnLogout);

        // Bottom navigation tabs
        tabDashboard = findViewById(R.id.tabDashboard);
        tabInventory = findViewById(R.id.tabInventory);
        tabBikes = findViewById(R.id.tabBikes);
        tabCustomers = findViewById(R.id.tabCustomers);
        tabSettings = findViewById(R.id.tabSettings);

        // Initialize bottom navigation ImageViews and TextViews
        if (tabDashboard.getChildCount() > 0 && tabDashboard.getChildAt(0) instanceof ImageView) {
            ivDashboard = (ImageView) tabDashboard.getChildAt(0);
        }
        if (tabDashboard.getChildCount() > 1 && tabDashboard.getChildAt(1) instanceof TextView) {
            tvDashboard = (TextView) tabDashboard.getChildAt(1);
        }

        if (tabInventory.getChildCount() > 0 && tabInventory.getChildAt(0) instanceof ImageView) {
            ivInventory = (ImageView) tabInventory.getChildAt(0);
        }
        if (tabInventory.getChildCount() > 1 && tabInventory.getChildAt(1) instanceof TextView) {
            tvInventory = (TextView) tabInventory.getChildAt(1);
        }

        if (tabBikes.getChildCount() > 0 && tabBikes.getChildAt(0) instanceof ImageView) {
            ivBikes = (ImageView) tabBikes.getChildAt(0);
        }
        if (tabBikes.getChildCount() > 1 && tabBikes.getChildAt(1) instanceof TextView) {
            tvBikes = (TextView) tabBikes.getChildAt(1);
        }

        if (tabCustomers.getChildCount() > 0 && tabCustomers.getChildAt(0) instanceof ImageView) {
            ivCustomers = (ImageView) tabCustomers.getChildAt(0);
        }
        if (tabCustomers.getChildCount() > 1 && tabCustomers.getChildAt(1) instanceof TextView) {
            tvCustomers = (TextView) tabCustomers.getChildAt(1);
        }

        if (tabSettings.getChildCount() > 0 && tabSettings.getChildAt(0) instanceof ImageView) {
            ivSettings = (ImageView) tabSettings.getChildAt(0);
        }
        if (tabSettings.getChildCount() > 1 && tabSettings.getChildAt(1) instanceof TextView) {
            tvSettings = (TextView) tabSettings.getChildAt(1);
        }
    }

    private void setupClickListeners() {
        // Back button click
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Card clicks
        cardManageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AdminSettingsActivity.this, "Manage Profile clicked", Toast.LENGTH_SHORT).show();
                // Navigate to Manage Profile screen
                // startActivity(new Intent(AdminSettingsActivity.this, ManageProfileActivity.class));
            }
        });

        cardChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AdminSettingsActivity.this, "Change Password clicked", Toast.LENGTH_SHORT).show();
                // Navigate to Change Password screen
                // startActivity(new Intent(AdminSettingsActivity.this, ChangePasswordActivity.class));
            }
        });

        cardSecuritySettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AdminSettingsActivity.this, "Security Settings clicked", Toast.LENGTH_SHORT).show();
                // Navigate to Security Settings screen
                // startActivity(new Intent(AdminSettingsActivity.this, SecuritySettingsActivity.class));
            }
        });

        cardUserManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AdminSettingsActivity.this, "User Management clicked", Toast.LENGTH_SHORT).show();
                // Navigate to User Management screen
                // startActivity(new Intent(AdminSettingsActivity.this, UserManagementActivity.class));
            }
        });

        cardRoleManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AdminSettingsActivity.this, "Role Management clicked", Toast.LENGTH_SHORT).show();
                // Navigate to Role Management screen
                // startActivity(new Intent(AdminSettingsActivity.this, RoleManagementActivity.class));
            }
        });

        cardNotificationPreferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AdminSettingsActivity.this, "Notification Preferences clicked", Toast.LENGTH_SHORT).show();
                // Navigate to Notification Preferences screen
                // startActivity(new Intent(AdminSettingsActivity.this, NotificationPreferencesActivity.class));
            }
        });

        cardSystemConfigurations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AdminSettingsActivity.this, "System Configurations clicked", Toast.LENGTH_SHORT).show();
                // Navigate to System Configurations screen
                // startActivity(new Intent(AdminSettingsActivity.this, SystemConfigurationsActivity.class));
            }
        });

        // Maintenance Mode toggle
        cardMaintenanceMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle the switch
                boolean isChecked = switchMaintenanceMode.isChecked();
                switchMaintenanceMode.setChecked(!isChecked);
                Toast.makeText(AdminSettingsActivity.this,
                        "Maintenance Mode " + (!isChecked ? "enabled" : "disabled"),
                        Toast.LENGTH_SHORT).show();
            }
        });

        cardHelpSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AdminSettingsActivity.this, "Help & Support clicked", Toast.LENGTH_SHORT).show();
                // Navigate to Help & Support screen
                // startActivity(new Intent(AdminSettingsActivity.this, HelpSupportActivity.class));
            }
        });

        cardAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AdminSettingsActivity.this, "About clicked", Toast.LENGTH_SHORT).show();
                // Navigate to About screen
                // startActivity(new Intent(AdminSettingsActivity.this, AboutActivity.class));
            }
        });

        // Logout button
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AdminSettingsActivity.this, "Logging out...", Toast.LENGTH_SHORT).show();

                // Clear any admin session data from SharedPreferences
                // Navigate to Role Selection screen
                Intent intent = new Intent(AdminSettingsActivity.this, RoleSelectionActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        // Bottom navigation tabs
        tabDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Dashboard
                Intent intent = new Intent(AdminSettingsActivity.this, AdminDashboardActivity.class);
                startActivity(intent);
                finish();
            }
        });

        tabInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveTab(tabInventory);
                Toast.makeText(AdminSettingsActivity.this, "Inventory selected", Toast.LENGTH_SHORT).show();
                // Navigate to Inventory screen
                // startActivity(new Intent(AdminSettingsActivity.this, InventoryActivity.class));
            }
        });

        tabBikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveTab(tabBikes);
                Toast.makeText(AdminSettingsActivity.this, "Bikes selected", Toast.LENGTH_SHORT).show();
                // Navigate to Bikes screen
                // startActivity(new Intent(AdminSettingsActivity.this, BikeInventoryActivity.class));
            }
        });

        tabCustomers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveTab(tabCustomers);
                Toast.makeText(AdminSettingsActivity.this, "Customers selected", Toast.LENGTH_SHORT).show();
                // Navigate to Customers screen
                // startActivity(new Intent(AdminSettingsActivity.this, CustomersActivity.class));
            }
        });

        tabSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Already on Settings screen
                setActiveTab(tabSettings);
                Toast.makeText(AdminSettingsActivity.this, "Settings selected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setActiveTab(LinearLayout activeTab) {
        // Reset all tabs to inactive state
        resetAllTabs();

        // Set the active tab
        if (activeTab == tabDashboard && ivDashboard != null && tvDashboard != null) {
            ivDashboard.setColorFilter(ContextCompat.getColor(this, R.color.primary_color));
            tvDashboard.setTextColor(ContextCompat.getColor(this, R.color.primary_color));
            tvDashboard.setTypeface(tvDashboard.getTypeface(), android.graphics.Typeface.BOLD);
        }
        else if (activeTab == tabInventory && ivInventory != null && tvInventory != null) {
            ivInventory.setColorFilter(ContextCompat.getColor(this, R.color.primary_color));
            tvInventory.setTextColor(ContextCompat.getColor(this, R.color.primary_color));
            tvInventory.setTypeface(tvInventory.getTypeface(), android.graphics.Typeface.BOLD);
        }
        else if (activeTab == tabBikes && ivBikes != null && tvBikes != null) {
            ivBikes.setColorFilter(ContextCompat.getColor(this, R.color.primary_color));
            tvBikes.setTextColor(ContextCompat.getColor(this, R.color.primary_color));
            tvBikes.setTypeface(tvBikes.getTypeface(), android.graphics.Typeface.BOLD);
        }
        else if (activeTab == tabCustomers && ivCustomers != null && tvCustomers != null) {
            ivCustomers.setColorFilter(ContextCompat.getColor(this, R.color.primary_color));
            tvCustomers.setTextColor(ContextCompat.getColor(this, R.color.primary_color));
            tvCustomers.setTypeface(tvCustomers.getTypeface(), android.graphics.Typeface.BOLD);
        }
        else if (activeTab == tabSettings && ivSettings != null && tvSettings != null) {
            ivSettings.setColorFilter(ContextCompat.getColor(this, R.color.primary_color));
            tvSettings.setTextColor(ContextCompat.getColor(this, R.color.primary_color));
            tvSettings.setTypeface(tvSettings.getTypeface(), android.graphics.Typeface.BOLD);
        }
    }

    private void resetAllTabs() {
        // Reset Dashboard tab
        if (ivDashboard != null && tvDashboard != null) {
            ivDashboard.setColorFilter(ContextCompat.getColor(this, R.color.gray_400));
            tvDashboard.setTextColor(ContextCompat.getColor(this, R.color.gray_400));
            tvDashboard.setTypeface(null, android.graphics.Typeface.NORMAL);
        }

        // Reset Inventory tab
        if (ivInventory != null && tvInventory != null) {
            ivInventory.setColorFilter(ContextCompat.getColor(this, R.color.gray_400));
            tvInventory.setTextColor(ContextCompat.getColor(this, R.color.gray_400));
            tvInventory.setTypeface(null, android.graphics.Typeface.NORMAL);
        }

        // Reset Bikes tab
        if (ivBikes != null && tvBikes != null) {
            ivBikes.setColorFilter(ContextCompat.getColor(this, R.color.gray_400));
            tvBikes.setTextColor(ContextCompat.getColor(this, R.color.gray_400));
            tvBikes.setTypeface(null, android.graphics.Typeface.NORMAL);
        }

        // Reset Customers tab
        if (ivCustomers != null && tvCustomers != null) {
            ivCustomers.setColorFilter(ContextCompat.getColor(this, R.color.gray_400));
            tvCustomers.setTextColor(ContextCompat.getColor(this, R.color.gray_400));
            tvCustomers.setTypeface(null, android.graphics.Typeface.NORMAL);
        }

        // Reset Settings tab - Note: Settings tab stays active on this screen
        // We don't reset Settings tab since this is the Settings screen
    }

    @Override
    public void onBackPressed() {
        // Navigate back to Admin Dashboard
        Intent intent = new Intent(this, AdminDashboardActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}