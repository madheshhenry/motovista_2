package com.example.motovista_deep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

public class AdminDocumentsActivity extends AppCompatActivity {

    // UI Components
    private CardView btnBack, btnFinishOrder;
    private CardView btnPreviewBill, btnDownloadBill;
    private CardView btnPreviewDelivery, btnDownloadDelivery;
    private CardView cardBillReceipt, cardDeliveryNote;

    // Animation Handler
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_documents);

        // Initialize views
        initializeViews();

        // Get data from intent
        handleIntentData();

        // Setup click listeners
        setupClickListeners();

        // Animate cards entrance
        animateCardEntrance();
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
    }

    private void handleIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            // Get data from PaymentConfirmedActivity
            String customerName = intent.getStringExtra("customer_name");
            String vehicleModel = intent.getStringExtra("vehicle_model");
            String transactionId = intent.getStringExtra("transaction_id");

            // You can use this data later for PDF generation
            // For now, we'll just pass it along if needed
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
                openBillReceiptPreview();
            }
        });

        // Bill Receipt Download
        btnPreviewDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButtonClick(btnPreviewDelivery);
                openDeliveryNotePreview();
            }
        });

        // Delivery Note Preview
        btnPreviewDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButtonClick(btnPreviewDelivery);
                openDeliveryNotePreview();  // This should call the method to open activity
            }
        });

        // Delivery Note Download
        btnDownloadDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButtonClick(btnDownloadDelivery);
                showToast("Downloading Delivery Note PDF...");
                // TODO: Implement PDF download
                // downloadPdf("delivery_note");
            }
        });

        // Finish Order Button
        // Finish Order Button
        btnFinishOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFinishOrderButton();
                showToast("Order Completed Successfully!");

                // Navigate to OrderCompletedActivity
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(AdminDocumentsActivity.this, OrderCompletedActivity.class);

                        // Get data from current intent
                        Intent currentIntent = getIntent();
                        if (currentIntent != null) {
                            // Pass customer data to OrderCompletedActivity
                            String customerName = currentIntent.getStringExtra("customer_name");
                            String vehicleModel = currentIntent.getStringExtra("vehicle_model");

                            if (customerName != null && !customerName.isEmpty()) {
                                intent.putExtra("customer_name", customerName);
                            }

                            if (vehicleModel != null && !vehicleModel.isEmpty()) {
                                intent.putExtra("vehicle_model", vehicleModel);
                            }

                            // You can pass other data if available
                            String paymentType = currentIntent.getStringExtra("payment_type");
                            if (paymentType != null && !paymentType.isEmpty()) {
                                intent.putExtra("payment_type", paymentType);
                            } else {
                                intent.putExtra("payment_type", "Private EMI"); // Default
                            }
                        } else {
                            // Default data if no intent
                            intent.putExtra("customer_name", "Rahul Sharma");
                            intent.putExtra("vehicle_model", "Yamaha R15 V4");
                            intent.putExtra("payment_type", "Private EMI");
                        }

                        startActivity(intent);
                        finish();
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                }, 500);
            }
        });
    }

    private void openBillReceiptPreview() {
        Intent intent = new Intent(AdminDocumentsActivity.this, BillReceiptActivity.class);

        // Get data from the current activity (passed from PaymentConfirmedActivity)
        Intent currentIntent = getIntent();
        if (currentIntent != null) {
            // Pass along customer data if available
            String customerName = currentIntent.getStringExtra("customer_name");
            String vehicleModel = currentIntent.getStringExtra("vehicle_model");
            double amount = currentIntent.getDoubleExtra("amount_paid", 50000.00);
            String transactionId = currentIntent.getStringExtra("transaction_id");

            if (customerName != null) {
                intent.putExtra("customer_name", customerName);
            } else {
                intent.putExtra("customer_name", "Mr. Arun Kumar"); // Default
            }

            if (vehicleModel != null) {
                intent.putExtra("vehicle_model", vehicleModel);
            } else {
                intent.putExtra("vehicle_model", "Royal Enfield Classic 350"); // Default
            }

            intent.putExtra("amount", amount);
            intent.putExtra("payment_mode", "Cash");
            intent.putExtra("purpose", "Initial Payment");

            // Generate receipt number from transaction ID
            if (transactionId != null) {
                String receiptNo = "#REC-" + transactionId.substring(transactionId.length() - 5);
                intent.putExtra("receipt_number", receiptNo);
            } else {
                intent.putExtra("receipt_number", "#REC-23-899");
            }
        } else {
            // Default data if no intent
            intent.putExtra("receipt_number", "#REC-23-899");
            intent.putExtra("customer_name", "Mr. Arun Kumar");
            intent.putExtra("amount", 50000.00);
            intent.putExtra("payment_mode", "Cash");
            intent.putExtra("purpose", "Initial Payment");
            intent.putExtra("vehicle_model", "Royal Enfield Classic 350");
        }

        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void openDeliveryNotePreview() {
        Intent intent = new Intent(AdminDocumentsActivity.this, DeliveryNoteActivity.class);

        // Get data from the current activity (passed from PaymentConfirmedActivity)
        Intent currentIntent = getIntent();
        if (currentIntent != null) {
            // Pass along customer data if available
            String customerName = currentIntent.getStringExtra("customer_name");
            String vehicleModel = currentIntent.getStringExtra("vehicle_model");
            String transactionId = currentIntent.getStringExtra("transaction_id");

            if (customerName != null) {
                // Split customer name for delivery note format
                intent.putExtra("customer_name", customerName);
                intent.putExtra("father_name", customerName.split(" ")[0] + " Kumar"); // Example format
            } else {
                intent.putExtra("customer_name", "Rahul Kumar");
                intent.putExtra("father_name", "Suresh Kumar");
            }

            if (vehicleModel != null) {
                intent.putExtra("vehicle_name", vehicleModel);
            } else {
                intent.putExtra("vehicle_name", "Royal Enfield Classic 350");
            }

            // Generate delivery note number from transaction ID
            if (transactionId != null) {
                String deliveryNoteNo = "AUTO-" + transactionId.substring(transactionId.length() - 5);
                intent.putExtra("delivery_note_no", deliveryNoteNo);
            } else {
                intent.putExtra("delivery_note_no", "AUTO-10293");
            }
        } else {
            // Default data if no intent
            intent.putExtra("delivery_note_no", "AUTO-10293");
            intent.putExtra("customer_name", "Rahul Kumar");
            intent.putExtra("father_name", "Suresh Kumar");
            intent.putExtra("customer_phone", "+91 98400 12345");
            intent.putExtra("customer_address", "No 45, 2nd Main Road,\nAnna Nagar, Chennai,\nPIN: 600040");
            intent.putExtra("vehicle_name", "Royal Enfield Classic 350");
            intent.putExtra("vehicle_color", "Halcyon Black");
            intent.putExtra("engine_no", "U3S5F00881");
            intent.putExtra("chassis_no", "ME3J35F008992");
            intent.putExtra("remarks", "Nil");
        }

        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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

        // Success pulse animation (optional)
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

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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
    }
}