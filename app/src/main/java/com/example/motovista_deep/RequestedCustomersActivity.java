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

public class RequestedCustomersActivity extends AppCompatActivity {

    private ImageView btnBack;
    private EditText etSearch;

    // Customer 1
    private Button btnReject1, btnAccept1;
    private TextView tvCustomerName1, tvCustomerPhone1;
    private LinearLayout statusContainer1, actionContainer1;
    private ImageView ivTick1, ivCross1, ivArrow1;
    private CardView cardCustomer1;

    // Customer 2
    private Button btnReject2, btnAccept2;
    private TextView tvCustomerName2, tvCustomerPhone2;
    private LinearLayout statusContainer2, actionContainer2;
    private ImageView ivTick2, ivCross2, ivArrow2;
    private CardView cardCustomer2;

    // Customer 3
    private Button btnReject3, btnAccept3;
    private TextView tvCustomerName3, tvCustomerPhone3;
    private LinearLayout statusContainer3, actionContainer3;
    private ImageView ivTick3, ivCross3, ivArrow3;
    private CardView cardCustomer3;

    // Customer 4
    private Button btnReject4, btnAccept4;
    private TextView tvCustomerName4, tvCustomerPhone4;
    private LinearLayout statusContainer4, actionContainer4;
    private ImageView ivTick4, ivCross4, ivArrow4;
    private CardView cardCustomer4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requested_customers);

        // Initialize all views
        initializeViews();

        // Setup click listeners
        setupClickListeners();
    }

    private void initializeViews() {
        // Back button
        btnBack = findViewById(R.id.btnBack);
        etSearch = findViewById(R.id.etSearch);

        // Customer 1
        cardCustomer1 = findViewById(R.id.cardCustomer1);
        btnReject1 = findViewById(R.id.btnReject1);
        btnAccept1 = findViewById(R.id.btnAccept1);
        tvCustomerName1 = findViewById(R.id.tvCustomerName1);
        tvCustomerPhone1 = findViewById(R.id.tvCustomerPhone1);
        statusContainer1 = findViewById(R.id.statusContainer1);
        actionContainer1 = findViewById(R.id.actionContainer1);
        ivTick1 = findViewById(R.id.ivTick1);
        ivCross1 = findViewById(R.id.ivCross1);
        ivArrow1 = findViewById(R.id.ivArrow1);

        // Customer 2
        cardCustomer2 = findViewById(R.id.cardCustomer2);
        btnReject2 = findViewById(R.id.btnReject2);
        btnAccept2 = findViewById(R.id.btnAccept2);
        tvCustomerName2 = findViewById(R.id.tvCustomerName2);
        tvCustomerPhone2 = findViewById(R.id.tvCustomerPhone2);
        statusContainer2 = findViewById(R.id.statusContainer2);
        actionContainer2 = findViewById(R.id.actionContainer2);
        ivTick2 = findViewById(R.id.ivTick2);
        ivCross2 = findViewById(R.id.ivCross2);
        ivArrow2 = findViewById(R.id.ivArrow2);

        // Customer 3
        cardCustomer3 = findViewById(R.id.cardCustomer3);
        btnReject3 = findViewById(R.id.btnReject3);
        btnAccept3 = findViewById(R.id.btnAccept3);
        tvCustomerName3 = findViewById(R.id.tvCustomerName3);
        tvCustomerPhone3 = findViewById(R.id.tvCustomerPhone3);
        statusContainer3 = findViewById(R.id.statusContainer3);
        actionContainer3 = findViewById(R.id.actionContainer3);
        ivTick3 = findViewById(R.id.ivTick3);
        ivCross3 = findViewById(R.id.ivCross3);
        ivArrow3 = findViewById(R.id.ivArrow3);

        // Customer 4
        cardCustomer4 = findViewById(R.id.cardCustomer4);
        btnReject4 = findViewById(R.id.btnReject4);
        btnAccept4 = findViewById(R.id.btnAccept4);
        tvCustomerName4 = findViewById(R.id.tvCustomerName4);
        tvCustomerPhone4 = findViewById(R.id.tvCustomerPhone4);
        statusContainer4 = findViewById(R.id.statusContainer4);
        actionContainer4 = findViewById(R.id.actionContainer4);
        ivTick4 = findViewById(R.id.ivTick4);
        ivCross4 = findViewById(R.id.ivCross4);
        ivArrow4 = findViewById(R.id.ivArrow4);
    }

    private void setupClickListeners() {
        // Back button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
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
        setupCustomerActions(
                cardCustomer1,
                btnReject1,
                btnAccept1,
                statusContainer1,
                actionContainer1,
                ivTick1,
                ivCross1,
                ivArrow1,
                tvCustomerName1.getText().toString()
        );

        // Customer 2 actions
        setupCustomerActions(
                cardCustomer2,
                btnReject2,
                btnAccept2,
                statusContainer2,
                actionContainer2,
                ivTick2,
                ivCross2,
                ivArrow2,
                tvCustomerName2.getText().toString()
        );

        // Customer 3 actions
        setupCustomerActions(
                cardCustomer3,
                btnReject3,
                btnAccept3,
                statusContainer3,
                actionContainer3,
                ivTick3,
                ivCross3,
                ivArrow3,
                tvCustomerName3.getText().toString()
        );

        // Customer 4 actions
        setupCustomerActions(
                cardCustomer4,
                btnReject4,
                btnAccept4,
                statusContainer4,
                actionContainer4,
                ivTick4,
                ivCross4,
                ivArrow4,
                tvCustomerName4.getText().toString()
        );
    }

    private void setupCustomerActions(
            CardView card,
            Button rejectBtn,
            Button acceptBtn,
            LinearLayout statusContainer,
            LinearLayout actionContainer,
            ImageView ivTick,
            ImageView ivCross,
            ImageView ivArrow,
            String customerName) {

        rejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleRejectAction(
                        card,
                        statusContainer,
                        actionContainer,
                        ivCross,
                        ivTick,
                        ivArrow,
                        customerName
                );
            }
        });

        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleAcceptAction(
                        card,
                        statusContainer,
                        actionContainer,
                        ivTick,
                        ivCross,
                        ivArrow,
                        customerName
                );
            }
        });
    }

    private void handleRejectAction(
            CardView card,
            LinearLayout statusContainer,
            LinearLayout actionContainer,
            ImageView ivCross,
            ImageView ivTick,
            ImageView ivArrow,
            String customerName) {

        // Show confirmation dialog
        showConfirmationDialog("Reject", customerName, new Runnable() {
            @Override
            public void run() {
                // Hide action buttons
                actionContainer.setVisibility(View.GONE);

                // Show status container
                statusContainer.setVisibility(View.VISIBLE);

                // Show cross mark and hide tick mark
                ivCross.setVisibility(View.VISIBLE);
                ivTick.setVisibility(View.GONE);

                // Show right arrow
                ivArrow.setVisibility(View.VISIBLE);

                // Change card background color to indicate rejection
                card.setCardBackgroundColor(getResources().getColor(R.color.icon_bg_red));

                // Show toast
                Toast.makeText(RequestedCustomersActivity.this,
                        "Rejected request from " + customerName,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleAcceptAction(
            CardView card,
            LinearLayout statusContainer,
            LinearLayout actionContainer,
            ImageView ivTick,
            ImageView ivCross,
            ImageView ivArrow,
            String customerName) {

        // Show confirmation dialog
        showConfirmationDialog("Accept", customerName, new Runnable() {
            @Override
            public void run() {
                // Hide action buttons
                actionContainer.setVisibility(View.GONE);

                // Show status container
                statusContainer.setVisibility(View.VISIBLE);

                // Show tick mark and hide cross mark
                ivTick.setVisibility(View.VISIBLE);
                ivCross.setVisibility(View.GONE);

                // Show right arrow
                ivArrow.setVisibility(View.VISIBLE);

                // Change card background color to indicate acceptance
                card.setCardBackgroundColor(getResources().getColor(R.color.icon_bg_green));

                // Show toast
                Toast.makeText(RequestedCustomersActivity.this,
                        "Accepted request from " + customerName,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performSearch(String searchText) {
        // Implement search logic here
        Toast.makeText(this, "Searching for: " + searchText, Toast.LENGTH_SHORT).show();
    }

    private void showConfirmationDialog(String action, String customerName, Runnable onConfirm) {
        // For now, directly execute the action
        onConfirm.run();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}