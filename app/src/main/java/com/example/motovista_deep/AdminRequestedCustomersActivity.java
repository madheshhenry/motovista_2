package com.example.motovista_deep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class AdminRequestedCustomersActivity extends AppCompatActivity {

    private ImageView btnBack;
    private EditText etSearch;

    // Customer 1
    private CardView cardCustomer1;
    private TextView tvCustomerName1, tvCustomerPhone1, tvTime1;
    private Button btnReject1, btnAccept1;
    private LinearLayout statusContainer1, actionContainer1;
    private ImageView ivTick1, ivCross1, ivArrow1;

    // Customer 2
    private CardView cardCustomer2;
    private TextView tvCustomerName2, tvCustomerPhone2, tvTime2;
    private Button btnReject2, btnAccept2;
    private LinearLayout statusContainer2, actionContainer2;
    private ImageView ivTick2, ivCross2, ivArrow2;

    // Customer 3 (Accepted Example)
    private CardView cardCustomer3;
    private TextView tvCustomerName3, tvCustomerPhone3, tvTime3;
    private Button btnReject3, btnAccept3;
    private LinearLayout statusContainer3, actionContainer3;
    private ImageView ivTick3, ivCross3, ivArrow3;

    // Customer 4
    private CardView cardCustomer4;
    private TextView tvCustomerName4, tvCustomerPhone4, tvTime4;
    private Button btnReject4, btnAccept4;
    private LinearLayout statusContainer4, actionContainer4;
    private ImageView ivTick4, ivCross4, ivArrow4;

    // Customer 5
    private CardView cardCustomer5;
    private TextView tvCustomerName5, tvCustomerPhone5, tvTime5;
    private Button btnReject5, btnAccept5;
    private LinearLayout statusContainer5, actionContainer5;
    private ImageView ivTick5, ivCross5, ivArrow5;

    // Track customer status
    private HashMap<Integer, String> customerStatusMap = new HashMap<>();
    private HashMap<Integer, String> customerNameMap = new HashMap<>();
    private HashMap<Integer, String> customerPhoneMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_requested_customers);

        // Initialize all views
        initializeViews();

        // Setup click listeners
        setupClickListeners();

        // Initialize default status for customers
        initializeDefaultStatus();
    }

    private void initializeViews() {
        // Back button and search
        btnBack = findViewById(R.id.btnBack);
        etSearch = findViewById(R.id.etSearch);

        // Customer 1
        cardCustomer1 = findViewById(R.id.cardCustomer1);
        tvCustomerName1 = findViewById(R.id.tvCustomerName1);
        tvCustomerPhone1 = findViewById(R.id.tvCustomerPhone1);
        tvTime1 = findViewById(R.id.tvTime1);
        btnReject1 = findViewById(R.id.btnReject1);
        btnAccept1 = findViewById(R.id.btnAccept1);
        statusContainer1 = findViewById(R.id.statusContainer1);
        actionContainer1 = findViewById(R.id.actionContainer1);
        ivTick1 = findViewById(R.id.ivTick1);
        ivCross1 = findViewById(R.id.ivCross1);
        ivArrow1 = findViewById(R.id.ivArrow1);

        // Customer 2
        cardCustomer2 = findViewById(R.id.cardCustomer2);
        tvCustomerName2 = findViewById(R.id.tvCustomerName2);
        tvCustomerPhone2 = findViewById(R.id.tvCustomerPhone2);
        tvTime2 = findViewById(R.id.tvTime2);
        btnReject2 = findViewById(R.id.btnReject2);
        btnAccept2 = findViewById(R.id.btnAccept2);
        statusContainer2 = findViewById(R.id.statusContainer2);
        actionContainer2 = findViewById(R.id.actionContainer2);
        ivTick2 = findViewById(R.id.ivTick2);
        ivCross2 = findViewById(R.id.ivCross2);
        ivArrow2 = findViewById(R.id.ivArrow2);

        // Customer 3 (Accepted)
        cardCustomer3 = findViewById(R.id.cardCustomer3);
        tvCustomerName3 = findViewById(R.id.tvCustomerName3);
        tvCustomerPhone3 = findViewById(R.id.tvCustomerPhone3);
        tvTime3 = findViewById(R.id.tvTime3);
        btnReject3 = findViewById(R.id.btnReject3);
        btnAccept3 = findViewById(R.id.btnAccept3);
        statusContainer3 = findViewById(R.id.statusContainer3);
        actionContainer3 = findViewById(R.id.actionContainer3);
        ivTick3 = findViewById(R.id.ivTick3);
        ivCross3 = findViewById(R.id.ivCross3);
        ivArrow3 = findViewById(R.id.ivArrow3);

        // Customer 4
        cardCustomer4 = findViewById(R.id.cardCustomer4);
        tvCustomerName4 = findViewById(R.id.tvCustomerName4);
        tvCustomerPhone4 = findViewById(R.id.tvCustomerPhone4);
        tvTime4 = findViewById(R.id.tvTime4);
        btnReject4 = findViewById(R.id.btnReject4);
        btnAccept4 = findViewById(R.id.btnAccept4);
        statusContainer4 = findViewById(R.id.statusContainer4);
        actionContainer4 = findViewById(R.id.actionContainer4);
        ivTick4 = findViewById(R.id.ivTick4);
        ivCross4 = findViewById(R.id.ivCross4);
        ivArrow4 = findViewById(R.id.ivArrow4);

        // Customer 5
        cardCustomer5 = findViewById(R.id.cardCustomer5);
        tvCustomerName5 = findViewById(R.id.tvCustomerName5);
        tvCustomerPhone5 = findViewById(R.id.tvCustomerPhone5);
        tvTime5 = findViewById(R.id.tvTime5);
        btnReject5 = findViewById(R.id.btnReject5);
        btnAccept5 = findViewById(R.id.btnAccept5);
        statusContainer5 = findViewById(R.id.statusContainer5);
        actionContainer5 = findViewById(R.id.actionContainer5);
        ivTick5 = findViewById(R.id.ivTick5);
        ivCross5 = findViewById(R.id.ivCross5);
        ivArrow5 = findViewById(R.id.ivArrow5);
    }

    private void initializeDefaultStatus() {
        // Customer 3 is already accepted by default
        customerStatusMap.put(3, "accepted");
        customerNameMap.put(3, tvCustomerName3.getText().toString());
        customerPhoneMap.put(3, tvCustomerPhone3.getText().toString());

        // Others are pending by default
        customerStatusMap.put(1, "pending");
        customerNameMap.put(1, tvCustomerName1.getText().toString());
        customerPhoneMap.put(1, tvCustomerPhone1.getText().toString());

        customerStatusMap.put(2, "pending");
        customerNameMap.put(2, tvCustomerName2.getText().toString());
        customerPhoneMap.put(2, tvCustomerPhone2.getText().toString());

        customerStatusMap.put(4, "pending");
        customerNameMap.put(4, tvCustomerName4.getText().toString());
        customerPhoneMap.put(4, tvCustomerPhone4.getText().toString());

        customerStatusMap.put(5, "pending");
        customerNameMap.put(5, tvCustomerName5.getText().toString());
        customerPhoneMap.put(5, tvCustomerPhone5.getText().toString());
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

        // Search functionality
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            String searchText = etSearch.getText().toString().trim();
            if (!searchText.isEmpty()) {
                performSearch(searchText);
            }
            return true;
        });

        // Customer 1 actions
        setupCustomerActions(1,
                cardCustomer1,
                btnReject1,
                btnAccept1,
                statusContainer1,
                actionContainer1,
                ivTick1,
                ivCross1,
                ivArrow1,
                tvCustomerName1.getText().toString(),
                tvTime1
        );

        // Customer 2 actions
        setupCustomerActions(2,
                cardCustomer2,
                btnReject2,
                btnAccept2,
                statusContainer2,
                actionContainer2,
                ivTick2,
                ivCross2,
                ivArrow2,
                tvCustomerName2.getText().toString(),
                tvTime2
        );

        // Customer 3 actions (already accepted - view details)
        ivArrow3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToOrderSummary(3, tvCustomerName3.getText().toString(), tvCustomerPhone3.getText().toString());
            }
        });

        // Customer 4 actions
        setupCustomerActions(4,
                cardCustomer4,
                btnReject4,
                btnAccept4,
                statusContainer4,
                actionContainer4,
                ivTick4,
                ivCross4,
                ivArrow4,
                tvCustomerName4.getText().toString(),
                tvTime4
        );

        // Customer 5 actions
        setupCustomerActions(5,
                cardCustomer5,
                btnReject5,
                btnAccept5,
                statusContainer5,
                actionContainer5,
                ivTick5,
                ivCross5,
                ivArrow5,
                tvCustomerName5.getText().toString(),
                tvTime5
        );

        // Make phone numbers clickable
        setupPhoneClickListeners();
    }

    private void setupCustomerActions(
            int customerId,
            CardView card,
            Button rejectBtn,
            Button acceptBtn,
            LinearLayout statusContainer,
            LinearLayout actionContainer,
            ImageView ivTick,
            ImageView ivCross,
            ImageView ivArrow,
            String customerName,
            TextView tvTime) {

        rejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleRejectAction(customerId,
                        card,
                        statusContainer,
                        actionContainer,
                        ivCross,
                        ivTick,
                        ivArrow,
                        customerName,
                        tvTime);
            }
        });

        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleAcceptAction(customerId,
                        card,
                        statusContainer,
                        actionContainer,
                        ivTick,
                        ivCross,
                        ivArrow,
                        customerName,
                        tvTime);
            }
        });

        // Make arrow clickable for viewing details
        ivArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get current status
                String status = customerStatusMap.get(customerId);
                if (status == null) {
                    status = "pending";
                }

                // Get phone number
                String phoneNumber = "";
                switch (customerId) {
                    case 1: phoneNumber = tvCustomerPhone1.getText().toString(); break;
                    case 2: phoneNumber = tvCustomerPhone2.getText().toString(); break;
                    case 4: phoneNumber = tvCustomerPhone4.getText().toString(); break;
                    case 5: phoneNumber = tvCustomerPhone5.getText().toString(); break;
                }

                navigateToOrderSummary(customerId, customerName, phoneNumber);
            }
        });
    }

    private void navigateToOrderSummary(int customerId, String customerName, String phoneNumber) {
        try {
            Intent intent = new Intent(AdminRequestedCustomersActivity.this, OrderSummaryActivity.class);

            // Pass customer data
            intent.putExtra("customer_id", customerId);
            intent.putExtra("customer_name", customerName);
            intent.putExtra("customer_phone", phoneNumber);

            // Get status from map
            String status = customerStatusMap.get(customerId);
            if (status == null || status.equals("pending")) {
                // If pending, show as "approved" in order summary (default)
                status = "approved";
            }
            intent.putExtra("status", status);

            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        } catch (Exception e) {
            Toast.makeText(AdminRequestedCustomersActivity.this,
                    "Error opening order summary: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void setupPhoneClickListeners() {
        // Make phone numbers clickable
        TextView[] phoneTextViews = {
                tvCustomerPhone1, tvCustomerPhone2, tvCustomerPhone3,
                tvCustomerPhone4, tvCustomerPhone5
        };

        for (TextView phoneTextView : phoneTextViews) {
            phoneTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String phoneNumber = phoneTextView.getText().toString();
                    Toast.makeText(AdminRequestedCustomersActivity.this,
                            "Call " + phoneNumber,
                            Toast.LENGTH_SHORT).show();
                    // TODO: Implement phone call intent
                }
            });
        }
    }

    private void handleRejectAction(
            int customerId,
            CardView card,
            LinearLayout statusContainer,
            LinearLayout actionContainer,
            ImageView ivCross,
            ImageView ivTick,
            ImageView ivArrow,
            String customerName,
            TextView tvTime) {

        // Show confirmation
        Toast.makeText(this,
                "Reject " + customerName + "?",
                Toast.LENGTH_LONG).show();

        // Update status in map
        customerStatusMap.put(customerId, "rejected");

        // Update UI immediately
        updateCustomerStatus(customerId, "rejected",
                card, statusContainer, actionContainer,
                ivCross, ivTick, ivArrow, customerName, tvTime);
    }

    private void handleAcceptAction(
            int customerId,
            CardView card,
            LinearLayout statusContainer,
            LinearLayout actionContainer,
            ImageView ivTick,
            ImageView ivCross,
            ImageView ivArrow,
            String customerName,
            TextView tvTime) {

        // Show confirmation
        Toast.makeText(this,
                "Accept " + customerName + " as a customer?",
                Toast.LENGTH_LONG).show();

        // Update status in map
        customerStatusMap.put(customerId, "accepted");

        // Update UI immediately
        updateCustomerStatus(customerId, "accepted",
                card, statusContainer, actionContainer,
                ivCross, ivTick, ivArrow, customerName, tvTime);
    }

    private void updateCustomerStatus(
            int customerId,
            String status,
            CardView card,
            LinearLayout statusContainer,
            LinearLayout actionContainer,
            ImageView ivCross,
            ImageView ivTick,
            ImageView ivArrow,
            String customerName,
            TextView tvTime) {

        // Hide action buttons
        actionContainer.setVisibility(View.GONE);

        // Show status container
        statusContainer.setVisibility(View.VISIBLE);

        if (status.equals("accepted")) {
            // Show tick mark
            ivTick.setVisibility(View.VISIBLE);
            ivCross.setVisibility(View.GONE);

            // Show arrow for details
            ivArrow.setVisibility(View.VISIBLE);

            // Update time text
            tvTime.setText("Accepted");
            tvTime.setTextColor(getResources().getColor(R.color.icon_green));

            // Change card background if needed
            // card.setCardBackgroundColor(getResources().getColor(R.color.icon_bg_green));

            Toast.makeText(this,
                    "Customer accepted successfully!",
                    Toast.LENGTH_SHORT).show();

        } else if (status.equals("rejected")) {
            // Show cross mark
            ivCross.setVisibility(View.VISIBLE);
            ivTick.setVisibility(View.GONE);

            // Show arrow for details
            ivArrow.setVisibility(View.VISIBLE);

            // Update time text
            tvTime.setText("Rejected");
            tvTime.setTextColor(getResources().getColor(R.color.icon_red));

            // Change card background if needed
            // card.setCardBackgroundColor(getResources().getColor(R.color.icon_bg_red));

            Toast.makeText(this,
                    "Customer rejected",
                    Toast.LENGTH_SHORT).show();
        }

        // TODO: Send API request to update status in backend
        // updateCustomerStatusOnServer(customerId, status);
    }

    private void performSearch(String searchText) {
        // Implement search logic here
        Toast.makeText(this,
                "Searching for: " + searchText,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}