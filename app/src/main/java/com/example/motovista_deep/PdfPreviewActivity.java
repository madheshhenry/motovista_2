package com.example.motovista_deep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;

public class PdfPreviewActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextView tvFileName;
    private CardView btnBack, btnShare, btnOpen;

    private File pdfFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_preview);

        initializeViews();
        loadPdfFile();
        setupClickListeners();
    }

    private void initializeViews() {
        progressBar = findViewById(R.id.progressBar);
        tvFileName = findViewById(R.id.tvFileName);

        btnBack = findViewById(R.id.btnBack);
        btnShare = findViewById(R.id.btnShare);
        btnOpen = findViewById(R.id.btnOpen);
    }

    private void loadPdfFile() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("pdf_path")) {
            String filePath = intent.getStringExtra("pdf_path");
            pdfFile = new File(filePath);

            if (pdfFile.exists()) {
                tvFileName.setText(pdfFile.getName());

                // Set file display name and size
                TextView tvFileDisplayName = findViewById(R.id.tvFileDisplayName);
                TextView tvFileSize = findViewById(R.id.tvFileSize);

                tvFileDisplayName.setText(pdfFile.getName());
                long fileSize = pdfFile.length() / 1024; // Convert to KB
                tvFileSize.setText("Size: " + fileSize + " KB");

                progressBar.setVisibility(View.GONE);
            } else {
                Toast.makeText(this, "PDF file not found", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(this, "No PDF file specified", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharePdf();
            }
        });

        btnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPdfWithNativeViewer();
            }
        });
    }

    private void sharePdf() {
        try {
            Uri pdfUri = FileProvider.getUriForFile(this,
                    getPackageName() + ".provider",
                    pdfFile);

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("application/pdf");
            shareIntent.putExtra(Intent.EXTRA_STREAM, pdfUri);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Vehicle Document");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(Intent.createChooser(shareIntent, "Share PDF"));
        } catch (Exception e) {
            Toast.makeText(this, "Error sharing PDF", Toast.LENGTH_SHORT).show();
        }
    }

    private void openPdfWithNativeViewer() {
        try {
            Uri pdfUri = FileProvider.getUriForFile(this,
                    getPackageName() + ".provider",
                    pdfFile);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(pdfUri, "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(this, "No PDF viewer app found", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error opening PDF", Toast.LENGTH_SHORT).show();
        }
    }
}