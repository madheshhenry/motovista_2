package com.example.motovista_deep;

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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.card.MaterialCardView;

public class HelpSupportActivity extends AppCompatActivity {

    // Header
    private ImageView btnBack;

    // Contact Us Section
    private MaterialCardView cardCallSupport, cardEmailSupport, cardChatSupport;

    // FAQ Section
    private EditText etSearchFAQ;
    private ImageView clearSearch;
    private LinearLayout faqItem1, faqItem2, faqItem3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Support modern edge-to-edge experience (Notch support)
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_help_support);

        // Initialize views
        initializeViews();

        // Setup window insets to handle notch area dynamically
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.header), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), systemBars.top, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        // Setup click listeners
        setupClickListeners();

        // Setup search functionality
        setupSearchFunctionality();
    }

    private void initializeViews() {
        // Header
        btnBack = findViewById(R.id.btnBack);

        // Contact Us Section (Updated to MaterialCardView)
        cardCallSupport = findViewById(R.id.cardCallSupport);
        cardEmailSupport = findViewById(R.id.cardEmailSupport);
        cardChatSupport = findViewById(R.id.cardChatSupport);

        // FAQ Section
        etSearchFAQ = findViewById(R.id.etSearchFAQ);
        clearSearch = findViewById(R.id.clear_search);
        faqItem1 = findViewById(R.id.faqItem1);
        faqItem2 = findViewById(R.id.faqItem2);
        faqItem3 = findViewById(R.id.faqItem3);
    }

    private void setupClickListeners() {
        // Back Button
        btnBack.setOnClickListener(v -> finish());

        // Contact Us Section
        cardCallSupport.setOnClickListener(v -> makePhoneCall());
        cardEmailSupport.setOnClickListener(v -> sendEmail());
        cardChatSupport.setOnClickListener(v -> startChatSupport());

        // FAQ Items
        faqItem1.setOnClickListener(v -> showFAQDetail("How to book a bike?",
                "1. Explore our bike catalog from the 'Bikes' tab.\n" +
                        "2. Select your preferred bike model and color.\n" +
                        "3. Review the specifications and pricing.\n" +
                        "4. Click on 'Book Now' to proceed with your booking.\n" +
                        "5. Fill in your details and complete the initial payment.\n" +
                        "6. You'll receive a confirmation with your order ID."));

        faqItem2.setOnClickListener(v -> showFAQDetail("What are the payment options?",
                "We accept multiple payment methods for bookings and services:\n\n" +
                        "• Credit/Debit Cards (Visa, MasterCard, RuPay)\n" +
                        "• UPI (Google Pay, PhonePe, Paytm)\n" +
                        "• Net Banking\n" +
                        "• EMI options available (3-12 months)\n\n" +
                        "Note: Down payment amounts vary by bike model."));

        faqItem3.setOnClickListener(v -> showFAQDetail("How can I track my ordered bike?",
                "Once your booking is confirmed, you can track it easily:\n\n" +
                        "1. Go to the 'Orders' or 'My Bookings' section.\n" +
                        "2. Select your recent bike order.\n" +
                        "3. View real-time status updates including:\n" +
                        "   - Payment Confirmed\n" +
                        "   - Processing at Showroom\n" +
                        "   - Ready for PDI (Pre-Delivery Inspection)\n" +
                        "   - Out for Delivery / Ready for Pickup\n" +
                        "4. You'll receive push notifications for major milestones."));
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
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        clearSearch.setOnClickListener(v -> etSearchFAQ.setText(""));
    }

    private void makePhoneCall() {
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:18001234567")); // Replace with your support number
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
    }

    private void showFAQDetail(String title, String content) {
        // Show a standard Material dialog for FAQ details
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                .setTitle(title)
                .setMessage(content)
                .setPositiveButton("Close", null)
                .show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}