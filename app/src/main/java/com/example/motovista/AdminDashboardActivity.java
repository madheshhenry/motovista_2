package com.example.motovista;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import android.widget.RelativeLayout;
import android.widget.TextView;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminDashboardActivity extends AppCompatActivity {

    LinearLayout cardEmi, cardInsurance, cardRegistration, cardApplication, cardSales, cardRequested;
    RelativeLayout btnAddBike;      // FIXED TYPE
    TextView btnAddSecondHand;      // FIXED TYPE
    BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_dashboard_activity);

        // Cards
        cardEmi = findViewById(R.id.cardEmi);
        cardInsurance = findViewById(R.id.cardInsurance);
        cardRegistration = findViewById(R.id.cardRegistration);
        cardApplication = findViewById(R.id.cardApplication);
        cardSales = findViewById(R.id.cardSales);
        cardRequested = findViewById(R.id.cardRequested);

        // FIXED BUTTONS
        RelativeLayout btnAddBike = findViewById(R.id.btnAddBike);
        RelativeLayout btnAddSecondHand = findViewById(R.id.btnAddSecondHand);


        // Bottom nav
        bottomNav = findViewById(R.id.bottomNav);

        setupDashboardCards();

        // Card clicks
        cardEmi.setOnClickListener(v -> startActivity(new Intent(this, EmiLedgerActivity.class)));
        cardInsurance.setOnClickListener(v -> startActivity(new Intent(this, InsuranceLedgerActivity.class)));
        cardRegistration.setOnClickListener(v -> startActivity(new Intent(this, RegistrationLedgerActivity.class)));
        cardApplication.setOnClickListener(v -> startActivity(new Intent(this, ApplicationsActivity.class)));
        cardSales.setOnClickListener(v -> startActivity(new Intent(this, SalesActivity.class)));
        cardRequested.setOnClickListener(v -> startActivity(new Intent(this, RequestedCustomerActivity.class)));

        // Button clicks
        btnAddBike.setOnClickListener(v -> startActivity(new Intent(this, AddBikeActivity.class)));
        btnAddSecondHand.setOnClickListener(v -> startActivity(new Intent(this, AddSecondHandBikeActivity.class)));

        // Bottom navigation
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_dashboard) return true;
            if (id == R.id.nav_inventory) {
                startActivity(new Intent(this, InventoryActivity.class));
                return true;
            }
            if (id == R.id.nav_bikes) {
                startActivity(new Intent(this, BikesActivity.class));
                return true;
            }
            if (id == R.id.nav_customers) {
                startActivity(new Intent(this, CustomersActivity.class));
                return true;
            }
            if (id == R.id.nav_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            }

            return false;
        });
    }

    private void setupDashboardCards() {
        setCard(cardEmi, "EMI Ledger", R.drawable.emi_icon, R.drawable.bg_red);
        setCard(cardInsurance, "Insurance Ledger", R.drawable.insurance_icon, R.drawable.bg_yellow);
        setCard(cardRegistration, "Registration Ledger", R.drawable.registration_icon, R.drawable.bg_blue);
        setCard(cardApplication, "Application", R.drawable.application_icon, R.drawable.bg_green);
        setCard(cardSales, "Sales", R.drawable.sales_icon, R.drawable.bg_purple);
        setCard(cardRequested, "Requested Customer", R.drawable.request_icon, R.drawable.bg_lightblue);
    }

    private void setCard(View card, String title, int icon, int bgColor) {
        TextView titleText = card.findViewById(R.id.titleText);
        ImageView iconImage = card.findViewById(R.id.iconImage);
        View iconBg = card.findViewById(R.id.iconBg);

        titleText.setText(title);
        iconImage.setImageResource(icon);
        iconBg.setBackgroundResource(bgColor);
    }
}
