package com.example.motovista_deep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class OrderSummaryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_summary);

        Toast.makeText(this, "Order Summary Screen", Toast.LENGTH_SHORT).show();
        Log.d("ORDER", "Activity created");

        // Back button
        CardView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // NEXT BUTTON - FIXED VERSION
        CardView btnNext = findViewById(R.id.btnNext);
        if (btnNext != null) {
            btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(OrderSummaryActivity.this,
                            "Opening Payment Screen...",
                            Toast.LENGTH_LONG).show();
                    Log.d("ORDER", "Next button clicked");

                    try {
                        // METHOD 1: Direct intent
                        Intent intent = new Intent(OrderSummaryActivity.this, PaymentTypeActivity.class);

                        // Add FLAG to clear any issues
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                        // Add some test data
                        intent.putExtra("test", "Hello from OrderSummary");

                        Log.d("ORDER", "Starting PaymentTypeActivity");
                        startActivity(intent);

                        // Add animation
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                    } catch (Exception e) {
                        Toast.makeText(OrderSummaryActivity.this,
                                "ERROR: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                        Log.e("ORDER", "Error: " + e.toString());
                        e.printStackTrace();
                    }
                }
            });
        } else {
            Toast.makeText(this, "Next button not found", Toast.LENGTH_LONG).show();
            Log.e("ORDER", "btnNext is null");
        }
    }
}