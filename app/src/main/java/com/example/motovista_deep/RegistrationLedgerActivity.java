package com.example.motovista_deep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class RegistrationLedgerActivity extends AppCompatActivity {

    private ImageView btnBack;
    private EditText etSearch;
    private CardView chipAll, chipPending, chipCompleted, chipVerification;
    private CardView card1, card2, card3, card4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_ledger);

        // Initialize views
        initializeViews();

        // Setup click listeners
        setupClickListeners();

        // Setup search functionality
        setupSearch();

        // Set initial active chip
        setActiveChip(chipAll);
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        etSearch = findViewById(R.id.etSearch);

        // Chips
        chipAll = findViewById(R.id.chipAll);
        chipPending = findViewById(R.id.chipPending);
        chipCompleted = findViewById(R.id.chipCompleted);
        chipVerification = findViewById(R.id.chipVerification);

        // Cards
        card1 = findViewById(R.id.card1);
        card2 = findViewById(R.id.card2);
        card3 = findViewById(R.id.card3);
        card4 = findViewById(R.id.card4);
    }

    private void setupClickListeners() {
        // Back button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Chip clicks
        chipAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveChip(chipAll);
                filterRegistrations("all");
            }
        });

        chipPending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveChip(chipPending);
                filterRegistrations("pending");
            }
        });

        chipCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveChip(chipCompleted);
                filterRegistrations("completed");
            }
        });

        chipVerification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveChip(chipVerification);
                filterRegistrations("verification");
            }
        });

        // Card clicks
        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRegistrationDetails(1);
            }
        });

        card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRegistrationDetails(2);
            }
        });

        card3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRegistrationDetails(3);
            }
        });

        card4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRegistrationDetails(4);
            }
        });
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Filter cards based on search text
                filterBySearch(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setActiveChip(CardView activeChip) {
        // Reset all chips
        resetChips();

        // Set active chip
        if (activeChip == chipAll) {
            chipAll.setCardBackgroundColor(ContextCompat.getColor(this, R.color.primary_color));
            ((TextView) chipAll.getChildAt(0)).setTextColor(ContextCompat.getColor(this, R.color.white));
        } else if (activeChip == chipPending) {
            chipPending.setCardBackgroundColor(ContextCompat.getColor(this, R.color.primary_color));
            ((TextView) chipPending.getChildAt(0)).setTextColor(ContextCompat.getColor(this, R.color.white));
        } else if (activeChip == chipCompleted) {
            chipCompleted.setCardBackgroundColor(ContextCompat.getColor(this, R.color.primary_color));
            ((TextView) chipCompleted.getChildAt(0)).setTextColor(ContextCompat.getColor(this, R.color.white));
        } else if (activeChip == chipVerification) {
            chipVerification.setCardBackgroundColor(ContextCompat.getColor(this, R.color.primary_color));
            ((TextView) chipVerification.getChildAt(0)).setTextColor(ContextCompat.getColor(this, R.color.white));
        }
    }

    private void resetChips() {
        chipAll.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white));
        chipPending.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white));
        chipCompleted.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white));
        chipVerification.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white));

        ((TextView) chipAll.getChildAt(0)).setTextColor(ContextCompat.getColor(this, R.color.text_secondary_light));
        ((TextView) chipPending.getChildAt(0)).setTextColor(ContextCompat.getColor(this, R.color.text_secondary_light));
        ((TextView) chipCompleted.getChildAt(0)).setTextColor(ContextCompat.getColor(this, R.color.text_secondary_light));
        ((TextView) chipVerification.getChildAt(0)).setTextColor(ContextCompat.getColor(this, R.color.text_secondary_light));
    }

    private void filterRegistrations(String filterType) {
        // This will be implemented later with backend
        Toast.makeText(this, "Filtering by: " + filterType, Toast.LENGTH_SHORT).show();
    }

    private void filterBySearch(String query) {
        // This will be implemented later with backend
        if (!query.isEmpty()) {
            Toast.makeText(this, "Searching: " + query, Toast.LENGTH_SHORT).show();
        }
    }

    private void openRegistrationDetails(int registrationId) {
        // Navigate to registration details screen
        Intent intent = new Intent(RegistrationLedgerActivity.this, RegistrationRecordActivity.class);
        intent.putExtra("registration_id", registrationId);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}