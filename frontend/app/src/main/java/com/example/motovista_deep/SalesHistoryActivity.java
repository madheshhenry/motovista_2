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

public class SalesHistoryActivity extends AppCompatActivity {

    private ImageView btnBack;
    private Button btnFilterAll, btnFilterCash, btnFilterEMI, btnFilterThisMonth;
    private CardView cardSale1, cardSale2, cardSale3, cardSale4, cardSale5;
    private TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_history);

        // Initialize views
        initializeViews();

        // Setup click listeners
        setupClickListeners();

        // Set active filter (All)
        setActiveFilter(btnFilterAll);
    }

    private void initializeViews() {
        // Header
        btnBack = findViewById(R.id.btnBack);
        tvTitle = findViewById(R.id.tvTitle);

        // Filter buttons
        btnFilterAll = findViewById(R.id.btnFilterAll);
        btnFilterCash = findViewById(R.id.btnFilterCash);
        btnFilterEMI = findViewById(R.id.btnFilterEMI);
        btnFilterThisMonth = findViewById(R.id.btnFilterThisMonth);

        // Sale cards
        cardSale1 = findViewById(R.id.cardSale1);
        cardSale2 = findViewById(R.id.cardSale2);
        cardSale3 = findViewById(R.id.cardSale3);
        cardSale4 = findViewById(R.id.cardSale4);
        cardSale5 = findViewById(R.id.cardSale5);
    }

    private void setupClickListeners() {
        // Back button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Filter buttons
        btnFilterAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveFilter(btnFilterAll);
                Toast.makeText(SalesHistoryActivity.this, "Showing all sales", Toast.LENGTH_SHORT).show();
                // TODO: Filter sales data
            }
        });

        btnFilterCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveFilter(btnFilterCash);
                Toast.makeText(SalesHistoryActivity.this, "Showing cash sales", Toast.LENGTH_SHORT).show();
                // TODO: Filter sales data for cash payments
            }
        });

        btnFilterEMI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveFilter(btnFilterEMI);
                Toast.makeText(SalesHistoryActivity.this, "Showing EMI sales", Toast.LENGTH_SHORT).show();
                // TODO: Filter sales data for EMI payments
            }
        });

        btnFilterThisMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveFilter(btnFilterThisMonth);
                Toast.makeText(SalesHistoryActivity.this, "Showing this month's sales", Toast.LENGTH_SHORT).show();
                // TODO: Filter sales data for current month
            }
        });

        // Sale card clicks
        cardSale1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSaleDetails("Yamaha R15 V4", "John Doe", "Oct 24, 2023", "EMI", "$2,400.00");
            }
        });

        cardSale2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSaleDetails("Royal Enfield Classic", "Sarah Smith", "Oct 23, 2023", "Cash", "$4,500.00");
            }
        });

        cardSale3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSaleDetails("Honda CB350", "Mike Ross", "Oct 22, 2023", "EMI", "$3,100.00");
            }
        });

        cardSale4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSaleDetails("KTM Duke 250", "Alex Chen", "Oct 20, 2023", "Cash", "$5,200.00");
            }
        });

        cardSale5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSaleDetails("Bajaj Avenger 220", "Priya Patel", "Oct 18, 2023", "EMI", "$1,800.00");
            }
        });
    }

    private void setActiveFilter(Button activeButton) {
        // Reset all buttons
        resetFilterButtons();

        // Set active button style
        activeButton.setTextColor(ContextCompat.getColor(this, R.color.white));
        activeButton.setBackground(ContextCompat.getDrawable(this, R.drawable.primary_button_rounded));

        // Set inactive buttons style
        if (activeButton != btnFilterAll) {
            btnFilterAll.setTextColor(ContextCompat.getColor(this, R.color.text_primary_light));
            btnFilterAll.setBackground(ContextCompat.getDrawable(this, R.drawable.gray_button_rounded));
        }
        if (activeButton != btnFilterCash) {
            btnFilterCash.setTextColor(ContextCompat.getColor(this, R.color.text_primary_light));
            btnFilterCash.setBackground(ContextCompat.getDrawable(this, R.drawable.gray_button_rounded));
        }
        if (activeButton != btnFilterEMI) {
            btnFilterEMI.setTextColor(ContextCompat.getColor(this, R.color.text_primary_light));
            btnFilterEMI.setBackground(ContextCompat.getDrawable(this, R.drawable.gray_button_rounded));
        }
        if (activeButton != btnFilterThisMonth) {
            btnFilterThisMonth.setTextColor(ContextCompat.getColor(this, R.color.text_primary_light));
            btnFilterThisMonth.setBackground(ContextCompat.getDrawable(this, R.drawable.gray_button_rounded));
        }
    }

    private void resetFilterButtons() {
        btnFilterAll.setTextColor(ContextCompat.getColor(this, R.color.text_primary_light));
        btnFilterAll.setBackground(ContextCompat.getDrawable(this, R.drawable.gray_button_rounded));

        btnFilterCash.setTextColor(ContextCompat.getColor(this, R.color.text_primary_light));
        btnFilterCash.setBackground(ContextCompat.getDrawable(this, R.drawable.gray_button_rounded));

        btnFilterEMI.setTextColor(ContextCompat.getColor(this, R.color.text_primary_light));
        btnFilterEMI.setBackground(ContextCompat.getDrawable(this, R.drawable.gray_button_rounded));

        btnFilterThisMonth.setTextColor(ContextCompat.getColor(this, R.color.text_primary_light));
        btnFilterThisMonth.setBackground(ContextCompat.getDrawable(this, R.drawable.gray_button_rounded));
    }

    private void showSaleDetails(String bikeName, String customer, String date, String paymentType, String price) {
        Intent intent = new Intent(SalesHistoryActivity.this, SaleDetailsActivity.class);

        // Pass data to SaleDetailsActivity
        intent.putExtra("BIKE_NAME", bikeName);
        intent.putExtra("BIKE_COLOR", "Dark Stealth Black");
        intent.putExtra("ENGINE_NUMBER", "RE350-9821X");
        intent.putExtra("CHASSIS_NUMBER", "ME3-7728-Y");

        intent.putExtra("CUSTOMER_NAME", "Rahul Sharma");
        intent.putExtra("CUSTOMER_PHONE", "+91 98765 43210");
        intent.putExtra("CUSTOMER_ADDRESS", "123, MG Road, District A, Bangalore, Karnataka - 560001");

        intent.putExtra("SALE_DATE", "24 Oct, 2023");
        intent.putExtra("PAYMENT_TYPE", "EMI Finance");
        intent.putExtra("DOWN_PAYMENT", "₹50,000");
        intent.putExtra("TOTAL_VALUE", "₹2,10,000");

        intent.putExtra("INVOICE_NUMBER", "INV-2023-001");
        intent.putExtra("DELIVERY_NOTE", "DEL-099-BLR");

        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}