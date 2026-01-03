package com.example.motovista_deep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class UpdateInsurancePolicyActivity extends AppCompatActivity {

    // Header views
    private ImageButton btnBack;
    private TextView tvTitle;

    // Current Policy Section
    private CardView cardComprehensiveCurrent, cardThirdPartyCurrent;
    private TextView tvComprehensiveTitle, tvComprehensiveProvider, tvComprehensiveStatus;
    private TextView tvComprehensivePolicyNumber, tvComprehensiveValidTill;
    private TextView tvThirdPartyTitle, tvThirdPartyProvider, tvThirdPartyStatus;
    private TextView tvThirdPartyPolicyNumber, tvThirdPartyValidTill;

    // Update Form Section
    private CardView cardUpdateForm;
    private LinearLayout spinnerContainer, btnStartDatePicker, btnEndDatePicker;
    private TextView tvSelectedInsuranceType, tvStartDate, tvEndDate;
    private EditText etProviderName, etPolicyNumber, etRemarks; // Changed from TextInputEditText to EditText
    private CardView btnSavePolicy;

    // Policy History Section
    private CardView cardHistory1, cardHistory2;
    private TextView tvHistory1Title, tvHistory1Expiry, tvHistory1Status;
    private TextView tvHistory2Title, tvHistory2Expiry, tvHistory2Status;

    // Date picker
    private Calendar calendar;
    private SimpleDateFormat dateFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_insurance_policy);

        // Initialize date formatter
        calendar = Calendar.getInstance();
        dateFormatter = new SimpleDateFormat("MMM dd, yyyy", Locale.US);

        // Initialize all views
        initializeAllViews();

        // Set all data
        setAllData();

        // Setup click listeners
        setupClickListeners();
    }

    private void initializeAllViews() {
        // Header views
        btnBack = findViewById(R.id.btnBack);
        tvTitle = findViewById(R.id.tvTitle);

        // Current Policy Section
        cardComprehensiveCurrent = findViewById(R.id.cardComprehensiveCurrent);
        cardThirdPartyCurrent = findViewById(R.id.cardThirdPartyCurrent);

        tvComprehensiveTitle = findViewById(R.id.tvComprehensiveTitle);
        tvComprehensiveProvider = findViewById(R.id.tvComprehensiveProvider);
        tvComprehensiveStatus = findViewById(R.id.tvComprehensiveStatus);
        tvComprehensivePolicyNumber = findViewById(R.id.tvComprehensivePolicyNumber);
        tvComprehensiveValidTill = findViewById(R.id.tvComprehensiveValidTill);

        tvThirdPartyTitle = findViewById(R.id.tvThirdPartyTitle);
        tvThirdPartyProvider = findViewById(R.id.tvThirdPartyProvider);
        tvThirdPartyStatus = findViewById(R.id.tvThirdPartyStatus);
        tvThirdPartyPolicyNumber = findViewById(R.id.tvThirdPartyPolicyNumber);
        tvThirdPartyValidTill = findViewById(R.id.tvThirdPartyValidTill);

        // Update Form Section
        cardUpdateForm = findViewById(R.id.cardUpdateForm);
        spinnerContainer = findViewById(R.id.spinnerContainer);
        btnStartDatePicker = findViewById(R.id.btnStartDatePicker);
        btnEndDatePicker = findViewById(R.id.btnEndDatePicker);

        tvSelectedInsuranceType = findViewById(R.id.tvSelectedInsuranceType);
        tvStartDate = findViewById(R.id.tvStartDate);
        tvEndDate = findViewById(R.id.tvEndDate);

        // Changed from findViewById<TextInputEditText> to findViewById<EditText>
        etProviderName = findViewById(R.id.etProviderName);
        etPolicyNumber = findViewById(R.id.etPolicyNumber);
        etRemarks = findViewById(R.id.etRemarks);

        btnSavePolicy = findViewById(R.id.btnSavePolicy);

        // Policy History Section
        cardHistory1 = findViewById(R.id.cardHistory1);
        cardHistory2 = findViewById(R.id.cardHistory2);

        tvHistory1Title = findViewById(R.id.tvHistory1Title);
        tvHistory1Expiry = findViewById(R.id.tvHistory1Expiry);
        tvHistory1Status = findViewById(R.id.tvHistory1Status);

        tvHistory2Title = findViewById(R.id.tvHistory2Title);
        tvHistory2Expiry = findViewById(R.id.tvHistory2Expiry);
        tvHistory2Status = findViewById(R.id.tvHistory2Status);
    }

    private void setAllData() {
        // Set title
        tvTitle.setText("Update Insurance Policy");

        // Current Policy Data
        tvComprehensiveTitle.setText("Comprehensive");
        tvComprehensiveProvider.setText("HDFC Ergo");
        tvComprehensiveStatus.setText("Active");
        tvComprehensivePolicyNumber.setText("#987654321");
        tvComprehensiveValidTill.setText("Jan 1, 2024");

        tvThirdPartyTitle.setText("Third Party");
        tvThirdPartyProvider.setText("ICICI Lombard");
        tvThirdPartyStatus.setText("Active");
        tvThirdPartyPolicyNumber.setText("#123456789");
        tvThirdPartyValidTill.setText("Jan 1, 2026");

        // Update Form - Default values
        tvSelectedInsuranceType.setText("Comprehensive Insurance");

        // Policy History Data
        tvHistory1Title.setText("Third Party - Bajaj Allianz");
        tvHistory1Expiry.setText("Expired: Dec 31, 2022");
        tvHistory1Status.setText("Expired");

        tvHistory2Title.setText("Comprehensive - SBI Gen");
        tvHistory2Expiry.setText("Expired: Dec 31, 2022");
        tvHistory2Status.setText("Expired");
    }

    private void setupClickListeners() {
        // Back button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Insurance Type Spinner
        spinnerContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInsuranceTypeDialog();
            }
        });

        // Start Date Picker
        btnStartDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(true);
            }
        });

        // End Date Picker
        btnEndDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(false);
            }
        });

        // Save Policy Button
        btnSavePolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePolicyUpdate();
            }
        });

        // History Card Clicks
        cardHistory1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(UpdateInsurancePolicyActivity.this,
                        "Third Party - Bajaj Allianz (Expired)",
                        Toast.LENGTH_SHORT).show();
            }
        });

        cardHistory2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(UpdateInsurancePolicyActivity.this,
                        "Comprehensive - SBI Gen (Expired)",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Current Policy Card Clicks
        cardComprehensiveCurrent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(UpdateInsurancePolicyActivity.this,
                        "Current Comprehensive Policy - HDFC Ergo",
                        Toast.LENGTH_SHORT).show();
            }
        });

        cardThirdPartyCurrent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(UpdateInsurancePolicyActivity.this,
                        "Current Third Party Policy - ICICI Lombard",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showInsuranceTypeDialog() {
        // In a real app, you would show a dialog with options
        // For now, just toggle between options
        String current = tvSelectedInsuranceType.getText().toString();
        if (current.equals("Comprehensive Insurance")) {
            tvSelectedInsuranceType.setText("Third Party Insurance");
        } else if (current.equals("Third Party Insurance")) {
            tvSelectedInsuranceType.setText("Zero Depreciation");
        } else if (current.equals("Zero Depreciation")) {
            tvSelectedInsuranceType.setText("Own Damage Only");
        } else {
            tvSelectedInsuranceType.setText("Comprehensive Insurance");
        }

        Toast.makeText(this, "Insurance type selected: " +
                        tvSelectedInsuranceType.getText().toString(),
                Toast.LENGTH_SHORT).show();
    }

    private void showDatePickerDialog(final boolean isStartDate) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    calendar.set(selectedYear, selectedMonth, selectedDay);
                    String formattedDate = dateFormatter.format(calendar.getTime());

                    if (isStartDate) {
                        tvStartDate.setText(formattedDate);
                        tvStartDate.setTextColor(getResources().getColor(R.color.text_dark));
                    } else {
                        tvEndDate.setText(formattedDate);
                        tvEndDate.setTextColor(getResources().getColor(R.color.text_dark));
                    }
                },
                year, month, day
        );

        datePickerDialog.show();
    }

    private void savePolicyUpdate() {
        // Get form data
        String insuranceType = tvSelectedInsuranceType.getText().toString();
        String providerName = etProviderName.getText().toString().trim();
        String policyNumber = etPolicyNumber.getText().toString().trim();
        String startDate = tvStartDate.getText().toString();
        String endDate = tvEndDate.getText().toString();
        String remarks = etRemarks.getText().toString().trim();

        // Validate required fields
        if (providerName.isEmpty()) {
            Toast.makeText(this, "Please enter provider name", Toast.LENGTH_SHORT).show();
            etProviderName.requestFocus();
            return;
        }

        if (policyNumber.isEmpty()) {
            Toast.makeText(this, "Please enter policy number", Toast.LENGTH_SHORT).show();
            etPolicyNumber.requestFocus();
            return;
        }

        if (startDate.equals("Select date")) {
            Toast.makeText(this, "Please select start date", Toast.LENGTH_SHORT).show();
            return;
        }

        if (endDate.equals("Select date")) {
            Toast.makeText(this, "Please select end date", Toast.LENGTH_SHORT).show();
            return;
        }

        // In a real app, you would save to database here
        String message = String.format(
                "Policy Update Saved:\nType: %s\nProvider: %s\nPolicy: %s\nFrom: %s\nTo: %s",
                insuranceType, providerName, policyNumber, startDate, endDate
        );

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

        // Clear form after saving (optional)
        clearForm();
    }

    private void clearForm() {
        etProviderName.setText("");
        etPolicyNumber.setText("");
        tvStartDate.setText("Select date");
        tvStartDate.setTextColor(getResources().getColor(R.color.text_light_gray));
        tvEndDate.setText("Select date");
        tvEndDate.setTextColor(getResources().getColor(R.color.text_light_gray));
        etRemarks.setText("");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}