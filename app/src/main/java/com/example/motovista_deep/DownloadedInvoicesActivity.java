package com.example.motovista_deep;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DownloadedInvoicesActivity extends AppCompatActivity {

    // Header
    private ImageView btnBack;

    // Invoice Views
    private TextView tvInvoiceNumber1, tvDownloadDate1, tvInvoiceDescription1;
    private TextView tvInvoiceNumber2, tvDownloadDate2, tvInvoiceDescription2;
    private TextView tvInvoiceNumber3, tvDownloadDate3, tvInvoiceDescription3;
    private TextView tvInvoiceNumber4, tvDownloadDate4, tvInvoiceDescription4;

    private ImageView btnDownload1, btnDownload2, btnDownload3, btnDownload4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloaded_invoices);

        // Initialize views
        initializeViews();

        // Setup click listeners
        setupClickListeners();
    }

    private void initializeViews() {
        // Header
        btnBack = findViewById(R.id.btnBack);

        // Invoice 1
        tvInvoiceNumber1 = findViewById(R.id.tvInvoiceNumber1);
        tvDownloadDate1 = findViewById(R.id.tvDownloadDate1);
        tvInvoiceDescription1 = findViewById(R.id.tvInvoiceDescription1);
        btnDownload1 = findViewById(R.id.btnDownload1);

        // Invoice 2
        tvInvoiceNumber2 = findViewById(R.id.tvInvoiceNumber2);
        tvDownloadDate2 = findViewById(R.id.tvDownloadDate2);
        tvInvoiceDescription2 = findViewById(R.id.tvInvoiceDescription2);
        btnDownload2 = findViewById(R.id.btnDownload2);

        // Invoice 3
        tvInvoiceNumber3 = findViewById(R.id.tvInvoiceNumber3);
        tvDownloadDate3 = findViewById(R.id.tvDownloadDate3);
        tvInvoiceDescription3 = findViewById(R.id.tvInvoiceDescription3);
        btnDownload3 = findViewById(R.id.btnDownload3);

        // Invoice 4
        tvInvoiceNumber4 = findViewById(R.id.tvInvoiceNumber4);
        tvDownloadDate4 = findViewById(R.id.tvDownloadDate4);
        tvInvoiceDescription4 = findViewById(R.id.tvInvoiceDescription4);
        btnDownload4 = findViewById(R.id.btnDownload4);
    }

    private void setupClickListeners() {
        // Back Button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Download Buttons
        btnDownload1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadInvoice("INV-2024-001", "Royal Enfield Classic 350");
            }
        });

        btnDownload2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadInvoice("INV-2024-002", "Premium Bike Service");
            }
        });

        btnDownload3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadInvoice("INV-2023-118", "Helmet & Riding Jacket");
            }
        });

        btnDownload4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadInvoice("INV-2023-085", "Jawa Perak");
            }
        });
    }

    private void downloadInvoice(String invoiceNumber, String description) {
        // TODO: Implement actual PDF download functionality
        Toast.makeText(this, "Downloading " + invoiceNumber + " - " + description, Toast.LENGTH_SHORT).show();

        // This is where you would implement:
        // 1. Check storage permission
        // 2. Download PDF from server
        // 3. Save to Downloads folder
        // 4. Show download completion notification

        // For now, just show a toast
        Toast.makeText(this, "Invoice download started: " + invoiceNumber, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Optional: Add animation
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}