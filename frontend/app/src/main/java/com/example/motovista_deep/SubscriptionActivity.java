package com.example.motovista_deep;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.motovista_deep.helpers.BillingManager;

public class SubscriptionActivity extends AppCompatActivity {

    private ImageView btnBack;
    private Button btnSubscribe;
    private BillingManager billingManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);

        billingManager = new BillingManager(this);

        // If already subscribed, why are we here? strict toggle.
        if (billingManager.isSubscribed()) {
            Toast.makeText(this, "You are already a Prime Member!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnBack = findViewById(R.id.btnBack);
        btnSubscribe = findViewById(R.id.btnSubscribe);

        btnBack.setOnClickListener(v -> finish());

        btnSubscribe.setOnClickListener(v -> {
            billingManager.simulatePurchase(this, () -> {
                // On Success
                setResult(RESULT_OK);
                finish(); // Close subscription screen and go back to where we came from
            });
        });
    }
}
