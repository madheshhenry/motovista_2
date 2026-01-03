package com.example.motovista_deep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.example.motovista_deep.utils.PdfGenerator;
import com.example.motovista_deep.utils.PermissionUtils;

import java.io.File;
import java.util.HashMap;

public class AdminDocumentsActivity extends AppCompatActivity {

    // UI Components
    private CardView btnBack, btnFinishOrder;
    private CardView btnPreviewBill, btnDownloadBill;
    private CardView btnPreviewDelivery, btnDownloadDelivery;
    private CardView cardBillReceipt, cardDeliveryNote;

    // Data
    private HashMap<String, String> orderData = new HashMap<>();
    private PdfGenerator pdfGenerator;
    private ProgressDialog progressDialog;

    // Animation Handler
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_documents);

        // Initialize PDF generator
        pdfGenerator = new PdfGenerator(this);

        // Initialize views
        initializeViews();

        // Get data from intent
        handleIntentData();

        // Setup click listeners
        setupClickListeners();

        // Animate cards entrance
        animateCardEntrance();

        // Check permissions
        checkAndRequestPermissions();
    }

    private void initializeViews() {
        // Header
        btnBack = findViewById(R.id.btnBack);

        // Cards
        cardBillReceipt = findViewById(R.id.cardBillReceipt);
        cardDeliveryNote = findViewById(R.id.cardDeliveryNote);

        // Bill Receipt Buttons
        btnPreviewBill = findViewById(R.id.btnPreviewBill);
        btnDownloadBill = findViewById(R.id.btnDownloadBill);

        // Delivery Note Buttons
        btnPreviewDelivery = findViewById(R.id.btnPreviewDelivery);
        btnDownloadDelivery = findViewById(R.id.btnDownloadDelivery);

        // Footer
        btnFinishOrder = findViewById(R.id.btnFinishOrder);

        // Set initial alpha for animations
        cardBillReceipt.setAlpha(0f);
        cardDeliveryNote.setAlpha(0f);
        btnFinishOrder.setAlpha(0f);

        // Initialize progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Generating PDF...");
        progressDialog.setCancelable(false);
    }

    private void handleIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            // Get data from PaymentConfirmedActivity
            orderData.put("customer_name", intent.getStringExtra("customer_name"));
            orderData.put("vehicle_model", intent.getStringExtra("vehicle_model"));
            orderData.put("transaction_id", intent.getStringExtra("transaction_id"));
            orderData.put("payment_mode", intent.getStringExtra("payment_mode"));
            orderData.put("amount_paid", String.valueOf(intent.getDoubleExtra("amount_paid", 25000)));

            // Add sample data for other fields (you can get these from your database)
            orderData.put("contact_number", "+91 9876543210");
            orderData.put("email", "customer@example.com");
            orderData.put("vehicle_price", "200000");
            orderData.put("gst_amount", "36000");
            orderData.put("insurance_amount", "15000");
            orderData.put("registration_fee", "5000");
            orderData.put("discount", "10000");
            orderData.put("chassis_number", "CH123456789");
            orderData.put("engine_number", "EN987654321");
            orderData.put("vehicle_color", "Racing Blue");
            orderData.put("emi_details", "12 months EMI @ ₹18,333 per month");
        }
    }

    private void checkAndRequestPermissions() {
        if (!PermissionUtils.checkStoragePermissions(this)) {
            PermissionUtils.requestStoragePermissions(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PermissionUtils.REQUEST_MANAGE_STORAGE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Storage permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Storage permission is required for PDF generation", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionUtils.REQUEST_STORAGE_PERMISSION) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            if (allGranted) {
                Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Some permissions were denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void setupClickListeners() {
        // Back button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        // Bill Receipt Preview
        btnPreviewBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButtonClick(btnPreviewBill);
                generateAndPreviewDocument("bill_receipt");
            }
        });

        // Bill Receipt Download
        btnDownloadBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButtonClick(btnDownloadBill);
                generateAndDownloadDocument("bill_receipt");
            }
        });

        // Delivery Note Preview
        btnPreviewDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButtonClick(btnPreviewDelivery);
                generateAndPreviewDocument("delivery_note");
            }
        });

        // Delivery Note Download
        btnDownloadDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButtonClick(btnDownloadDelivery);
                generateAndDownloadDocument("delivery_note");
            }
        });

        // Finish Order Button
        btnFinishOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFinishOrderButton();

                // Show completion message
                Toast.makeText(AdminDocumentsActivity.this,
                        "Order Completed Successfully!\nDocuments have been generated.",
                        Toast.LENGTH_LONG).show();

                // Navigate back to admin dashboard
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(AdminDocumentsActivity.this, AdminDashboardActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                }, 1000);
            }
        });
    }

    private void generateAndPreviewDocument(String documentType) {
        if (!PermissionUtils.checkStoragePermissions(this)) {
            Toast.makeText(this, "Please grant storage permissions first", Toast.LENGTH_SHORT).show();
            PermissionUtils.requestStoragePermissions(this);
            return;
        }

        progressDialog.setMessage("Generating " + (documentType.equals("bill_receipt") ? "Bill Receipt" : "Delivery Note") + "...");
        progressDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File pdfFile;
                    if (documentType.equals("bill_receipt")) {
                        pdfFile = pdfGenerator.generateBillReceipt(orderData);
                    } else {
                        pdfFile = pdfGenerator.generateDeliveryNote(orderData);
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            if (pdfFile != null && pdfFile.exists()) {
                                // Preview the PDF
                                Intent intent = new Intent(AdminDocumentsActivity.this, PdfPreviewActivity.class);
                                intent.putExtra("pdf_path", pdfFile.getAbsolutePath());
                                intent.putExtra("file_name", pdfFile.getName());
                                startActivity(intent);
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            } else {
                                Toast.makeText(AdminDocumentsActivity.this,
                                        "Failed to generate PDF", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            Toast.makeText(AdminDocumentsActivity.this,
                                    "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    private void generateAndDownloadDocument(String documentType) {
        if (!PermissionUtils.checkStoragePermissions(this)) {
            Toast.makeText(this, "Please grant storage permissions first", Toast.LENGTH_SHORT).show();
            PermissionUtils.requestStoragePermissions(this);
            return;
        }

        progressDialog.setMessage("Generating and downloading " +
                (documentType.equals("bill_receipt") ? "Bill Receipt" : "Delivery Note") + "...");
        progressDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File pdfFile;
                    if (documentType.equals("bill_receipt")) {
                        pdfFile = pdfGenerator.generateBillReceipt(orderData);
                    } else {
                        pdfFile = pdfGenerator.generateDeliveryNote(orderData);
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            if (pdfFile != null && pdfFile.exists()) {
                                // Show download complete message
                                Toast.makeText(AdminDocumentsActivity.this,
                                        "PDF downloaded successfully!\nLocation: " + pdfFile.getAbsolutePath(),
                                        Toast.LENGTH_LONG).show();

                                // Optionally open the file
                                PermissionUtils.openPdfFile(AdminDocumentsActivity.this, pdfFile);
                            } else {
                                Toast.makeText(AdminDocumentsActivity.this,
                                        "Failed to generate PDF", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            Toast.makeText(AdminDocumentsActivity.this,
                                    "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    private void animateCardEntrance() {
        // Animate first card
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                cardBillReceipt.animate()
                        .alpha(1f)
                        .translationY(0)
                        .setDuration(400)
                        .start();
            }
        }, 100);

        // Animate second card
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                cardDeliveryNote.animate()
                        .alpha(1f)
                        .translationY(0)
                        .setDuration(400)
                        .start();
            }
        }, 300);

        // Animate finish button
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                btnFinishOrder.animate()
                        .alpha(1f)
                        .setDuration(400)
                        .start();
            }
        }, 500);
    }

    private void animateButtonClick(CardView button) {
        button.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(100)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        button.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(100)
                                .start();
                    }
                })
                .start();
    }

    private void animateFinishOrderButton() {
        // Scale animation
        btnFinishOrder.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(150)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        btnFinishOrder.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(150)
                                .start();
                    }
                })
                .start();

        // Success pulse animation
        ValueAnimator animator = ValueAnimator.ofFloat(1.0f, 1.1f, 1.0f);
        animator.setDuration(500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                btnFinishOrder.setScaleX(value);
                btnFinishOrder.setScaleY(value);
            }
        });
        animator.start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}