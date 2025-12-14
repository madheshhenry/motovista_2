package com.example.motovista_deep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class HelpSupportActivity extends AppCompatActivity {

    // Header
    private ImageView btnBack;

    // Contact Us Section
    private CardView cardCallSupport, cardEmailSupport, cardChatSupport;

    // FAQ Section
    private EditText etSearchFAQ;
    private ImageView clearSearch;
    private LinearLayout faqItem1, faqItem2, faqItem3, faqItem4;

    // Common Queries Section
    private LinearLayout queryBilling, queryService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_support);

        // Initialize views
        initializeViews();

        // Setup click listeners
        setupClickListeners();

        // Setup search functionality
        setupSearchFunctionality();
    }

    private void initializeViews() {
        // Header
        btnBack = findViewById(R.id.btnBack);

        // Contact Us Section
        cardCallSupport = findViewById(R.id.cardCallSupport);
        cardEmailSupport = findViewById(R.id.cardEmailSupport);
        cardChatSupport = findViewById(R.id.cardChatSupport);

        // FAQ Section
        etSearchFAQ = findViewById(R.id.etSearchFAQ);
        clearSearch = findViewById(R.id.clear_search);
        faqItem1 = findViewById(R.id.faqItem1);
        faqItem2 = findViewById(R.id.faqItem2);
        faqItem3 = findViewById(R.id.faqItem3);
        faqItem4 = findViewById(R.id.faqItem4);

        // Common Queries Section
        queryBilling = findViewById(R.id.queryBilling);
        queryService = findViewById(R.id.queryService);
    }

    private void setupClickListeners() {
        // Back Button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Contact Us Section
        cardCallSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePhoneCall();
            }
        });

        cardEmailSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
            }
        });

        cardChatSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startChatSupport();
            }
        });

        // FAQ Items
        faqItem1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFAQDetail("How to book a service?",
                        "1. Open the app and go to 'Services' section\n" +
                                "2. Select your bike model\n" +
                                "3. Choose the type of service needed\n" +
                                "4. Select preferred date and time\n" +
                                "5. Confirm booking and make payment\n" +
                                "6. You'll receive a confirmation notification");
            }
        });

        faqItem2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFAQDetail("What are the payment options?",
                        "We accept multiple payment methods:\n\n" +
                                "• Credit/Debit Cards (Visa, MasterCard, RuPay)\n" +
                                "• UPI (Google Pay, PhonePe, Paytm)\n" +
                                "• Net Banking\n" +
                                "• Cash on Delivery (for some services)\n" +
                                "• EMI options available (3-12 months)");
            }
        });

        faqItem3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFAQDetail("How can I track my bike service?",
                        "Track your service in real-time:\n\n" +
                                "1. Go to 'Orders' section in the app\n" +
                                "2. Select your ongoing service\n" +
                                "3. View current status:\n" +
                                "   - Picked up\n" +
                                "   - Under inspection\n" +
                                "   - Service in progress\n" +
                                "   - Quality check\n" +
                                "   - Ready for delivery\n" +
                                "4. You'll receive SMS/notification updates");
            }
        });

        faqItem4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFAQDetail("Can I reschedule my appointment?",
                        "Yes, you can reschedule your appointment:\n\n" +
                                "• Reschedule up to 2 hours before appointment\n" +
                                "• Go to 'My Appointments' section\n" +
                                "• Select the appointment to reschedule\n" +
                                "• Choose new date and time\n" +
                                "• Confirm the changes\n\n" +
                                "Note: Free rescheduling up to 2 times. Additional charges may apply thereafter.");
            }
        });

        // Common Queries
        queryBilling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFAQDetail("Billing & Payments",
                        "Billing Information:\n\n" +
                                "• All bills are generated digitally\n" +
                                "• Download invoice from 'My Orders' section\n" +
                                "• Payment receipts sent to registered email\n" +
                                "• GST invoice available on request\n" +
                                "• For billing issues, contact support@motovista.com\n\n" +
                                "Refund Policy:\n" +
                                "• 100% refund if cancelled before service pickup\n" +
                                "• 50% refund if cancelled after pickup\n" +
                                "• No refund after service completion");
            }
        });

        queryService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFAQDetail("Service & Maintenance",
                        "Our Services:\n\n" +
                                "1. Regular Maintenance:\n" +
                                "   • Oil change\n" +
                                "   • Brake servicing\n" +
                                "   • Chain lubrication\n" +
                                "   • Battery check\n\n" +
                                "2. Major Services:\n" +
                                "   • Engine overhaul\n" +
                                "   • Electrical repairs\n" +
                                "   • Suspension work\n" +
                                "   • Transmission service\n\n" +
                                "3. Emergency Services:\n" +
                                "   • On-road assistance\n" +
                                "   • Towing service\n" +
                                "   • Flat tire repair\n\n" +
                                "Service Warranty: 30 days on all services");
            }
        });
    }

    private void setupSearchFunctionality() {
        etSearchFAQ.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Show/hide clear button
                if (s.length() > 0) {
                    clearSearch.setVisibility(View.VISIBLE);
                } else {
                    clearSearch.setVisibility(View.GONE);
                }

                // TODO: Implement FAQ search functionality
                // You can filter FAQ items based on search text
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        clearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etSearchFAQ.setText("");
            }
        });
    }

    private void makePhoneCall() {
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + "18001234567")); // Replace with your support number
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Unable to make call", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendEmail() {
        try {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:support@motovista.com")); // Replace with your support email
            intent.putExtra(Intent.EXTRA_SUBJECT, "Support Request - MotoVista App");
            startActivity(Intent.createChooser(intent, "Send email via:"));
        } catch (Exception e) {
            Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show();
        }
    }

    private void startChatSupport() {
        Toast.makeText(this, "Chat support coming soon!", Toast.LENGTH_SHORT).show();
        // TODO: Implement chat support integration
    }

    private void showFAQDetail(String title, String content) {
        // For now, show a toast with FAQ title
        // You can create a detailed FAQ dialog or activity later
        Toast.makeText(this, title, Toast.LENGTH_SHORT).show();

        // TODO: Create FAQ detail dialog or activity
        // Example: FAQDetailDialog dialog = new FAQDetailDialog(this, title, content);
        // dialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}