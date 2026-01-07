package com.example.motovista_deep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class OrderSummaryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_summary);

        // Initialize Views
        CardView btnBack = findViewById(R.id.btnBack);
        CardView btnNext = findViewById(R.id.btnNext);
        TextView tvCustomerName = findViewById(R.id.tvCustomerName);
        TextView tvCustomerPhone = findViewById(R.id.tvCustomerPhone);
        TextView tvStatus = findViewById(R.id.tvStatus);
        View statusDot = findViewById(R.id.statusDot);
        android.widget.LinearLayout statusBadgeLayout = findViewById(R.id.statusBadgeLayout);

        // Get Data from Intent
        String customerName = getIntent().getStringExtra("customer_name");
        String customerPhone = getIntent().getStringExtra("customer_phone");
        String status = getIntent().getStringExtra("status");

        // Set Customer Data
        if (customerName != null) tvCustomerName.setText(customerName);
        if (customerPhone != null) tvCustomerPhone.setText(customerPhone);

        // Handle Status Logic
        if (status != null) {
            String lowerStatus = status.toLowerCase();
            if (lowerStatus.contains("reject")) {
                // Rejected State
                tvStatus.setText("Rejected");
                tvStatus.setTextColor(getResources().getColor(R.color.red_600)); // or red_500
                statusDot.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.red_600)));
                
                // Change badge background tint slightly red if possible, or just keep as is with red text
                // Simply hiding the next button is the main requiremnet
                btnNext.setVisibility(View.GONE);
                
            } else {
                // Approved/Accepted/Pending State (Default to Approved UI)
                tvStatus.setText("Approved");
                tvStatus.setTextColor(getResources().getColor(R.color.icon_green));
                statusDot.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.icon_green)));
                
                btnNext.setVisibility(View.VISIBLE);
            }
        } else {
            // Default if no status passed
            btnNext.setVisibility(View.VISIBLE);
        }

        // Back button
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // NEXT BUTTON
        if (btnNext != null) {
            btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(OrderSummaryActivity.this, PaymentTypeActivity.class);
                        intent.putExtra("customer_name", customerName);
                        intent.putExtra("customer_phone", customerPhone);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    } catch (Exception e) {
                        Toast.makeText(OrderSummaryActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}