package com.example.motovista_deep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class EMISetupActivity extends AppCompatActivity {

    // UI Components
    private CardView btnBack, btnConfirmEMI;
    private EditText etVehiclePrice, etDownPayment, etInterestRate, etMonthlyEMI;
    private Spinner spinnerDuration;
    private TextView tvMinDownPayment, tvPrincipalAmount, tvTotalInterest, tvTotalPayable;

    // EMI Calculation variables
    private double vehiclePrice = 140000.00;
    private double downPayment = 30000.00;
    private double interestRate = 8.5;
    private int durationMonths = 12;
    private double minDownPayment = 0.0;

    // Formatting
    private DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

    // Payment data
    private int requestId = -1;
    private String customerName = "Customer";
    private String vehicleName = "Vehicle";
    private String vehicleDetails = "Details";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emi_setup);

        // Initialize views
        initializeViews();

        // Get data from intent
        handleIntentData();

        // Setup spinners
        setupSpinner();

        // Setup text change listeners for auto-calculation
        setupTextChangeListeners();

        // Setup click listeners
        setupClickListeners();

        // Initial calculation
        calculateEMI();
        
        // Fetch data if request ID is valid
        if (requestId != -1) {
            fetchVehicleDetails(requestId);
        } else {
            Toast.makeText(this, "Error: Invalid Request ID", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void initializeViews() {
        // Header
        btnBack = findViewById(R.id.btnBack);

        // Input fields
        etVehiclePrice = findViewById(R.id.etVehiclePrice);
        etDownPayment = findViewById(R.id.etDownPayment);
        etInterestRate = findViewById(R.id.etInterestRate);
        etMonthlyEMI = findViewById(R.id.etMonthlyEMI);

        // Spinner
        spinnerDuration = findViewById(R.id.spinnerDuration);

        // Text views
        tvMinDownPayment = findViewById(R.id.tvMinDownPayment);
        tvPrincipalAmount = findViewById(R.id.tvPrincipalAmount);
        tvTotalInterest = findViewById(R.id.tvTotalInterest);
        tvTotalPayable = findViewById(R.id.tvTotalPayable);

        // Footer button
        btnConfirmEMI = findViewById(R.id.btnConfirmEMI);
    }

    private void handleIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            requestId = intent.getIntExtra("request_id", -1);
            // Fallback price if passed directly, though API is preferred source now
            double price = intent.getDoubleExtra("vehicle_price", -1);
            if (price > 0) {
                vehiclePrice = price;
                etVehiclePrice.setText(formatIndianCurrencyNoSymbol(vehiclePrice));
                // Recalculate down payment based on passed price
                minDownPayment = vehiclePrice * 0.20;
                downPayment = minDownPayment; // Set default down payment to minimum
                updateMinDownPaymentText();
                etDownPayment.setText(formatIndianCurrencyNoSymbol(downPayment));
            }
        }
    }

    private void fetchVehicleDetails(int id) {
        com.example.motovista_deep.api.ApiService apiService = com.example.motovista_deep.api.RetrofitClient.getApiService();
        retrofit2.Call<com.example.motovista_deep.models.GetOrderSummaryResponse> call = apiService.getOrderSummary(id);

        call.enqueue(new retrofit2.Callback<com.example.motovista_deep.models.GetOrderSummaryResponse>() {
            @Override
            public void onResponse(retrofit2.Call<com.example.motovista_deep.models.GetOrderSummaryResponse> call, retrofit2.Response<com.example.motovista_deep.models.GetOrderSummaryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess() && response.body().getData() != null) {
                        com.example.motovista_deep.models.OrderSummaryData data = response.body().getData();
                        
                        // Update Data
                        customerName = data.getCustomerName();
                        String brand = data.getBrand() != null ? data.getBrand() : "";
                        String model = data.getBikeName() != null ? data.getBikeName() : "";
                        vehicleName = brand + " " + model;
                        vehicleDetails = data.getBikeVariant(); // Use variant as details

                        // Parse Price
                        String priceStr = data.getOnRoadPrice();
                        if (priceStr != null) {
                            priceStr = priceStr.replaceAll("[^\\d.]", "");
                            try {
                                double price = Double.parseDouble(priceStr);
                                if (price > 0) {
                                    vehiclePrice = price;
                                    etVehiclePrice.setText(formatIndianCurrencyNoSymbol(vehiclePrice));
                                    
                                    // Update derived values
                                    minDownPayment = vehiclePrice * 0.20;
                                    downPayment = minDownPayment;
                                    updateMinDownPaymentText();
                                    etDownPayment.setText(formatIndianCurrencyNoSymbol(downPayment));
                                    
                                    // Recalculate EMI with new values
                                    calculateEMI();
                                }
                            } catch (NumberFormatException e) {
                                // Keep default if parse fails
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.example.motovista_deep.models.GetOrderSummaryResponse> call, Throwable t) {
                Toast.makeText(EMISetupActivity.this, "Failed to load vehicle details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSpinner() {
        // Create adapter for spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.emi_durations,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDuration.setAdapter(adapter);

        // Set default selection
        spinnerDuration.setSelection(1); // 12 Months

        // Set spinner listener
        spinnerDuration.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                // Extract months from string like "12 Months"
                try {
                    durationMonths = Integer.parseInt(selected.split(" ")[0]);
                    calculateEMI();
                } catch (Exception e) {
                    durationMonths = 12;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                durationMonths = 12;
            }
        });
    }

    private void setupTextChangeListeners() {
        // Down payment text change listener
        etDownPayment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    String text = s.toString().replace(",", "").replace("₹", "").trim();
                    if (!text.isEmpty()) {
                        downPayment = Double.parseDouble(text);
                        if (downPayment > vehiclePrice) {
                            downPayment = vehiclePrice;
                            etDownPayment.setText(formatIndianCurrencyNoSymbol(downPayment));
                            etDownPayment.setSelection(etDownPayment.getText().length());
                        }
                    } else {
                        downPayment = 0;
                    }
                    calculateEMI();
                } catch (Exception e) {
                    downPayment = 0;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Interest rate text change listener
        etInterestRate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    String text = s.toString();
                    if (!text.isEmpty()) {
                        interestRate = Double.parseDouble(text);
                        if (interestRate > 100) {
                            interestRate = 100;
                            etInterestRate.setText(String.valueOf(interestRate));
                            etInterestRate.setSelection(etInterestRate.getText().length());
                        }
                    } else {
                        interestRate = 0;
                    }
                    calculateEMI();
                } catch (Exception e) {
                    interestRate = 0;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void calculateEMI() {
        try {
            // Calculate principal amount
            double principalAmount = vehiclePrice - downPayment;
            if (principalAmount < 0) principalAmount = 0;

            // Convert annual interest rate to monthly
            double monthlyInterestRate = interestRate / 12 / 100;

            // Calculate EMI using formula: EMI = P × r × (1+r)^n / ((1+r)^n - 1)
            double monthlyEMI = 0;
            if (monthlyInterestRate > 0 && durationMonths > 0) {
                double power = Math.pow(1 + monthlyInterestRate, durationMonths);
                monthlyEMI = principalAmount * monthlyInterestRate * power / (power - 1);
            } else if (durationMonths > 0) {
                monthlyEMI = principalAmount / durationMonths;
            }

            // Calculate total interest
            double totalInterest = (monthlyEMI * durationMonths) - principalAmount;
            if (totalInterest < 0) totalInterest = 0;

            // Calculate total payable
            double totalPayable = principalAmount + totalInterest;

            // Update UI
            updateCalculatedValues(principalAmount, monthlyEMI, totalInterest, totalPayable);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error in calculation", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateCalculatedValues(double principalAmount, double monthlyEMI,
                                        double totalInterest, double totalPayable) {
        // Format and set values
        etMonthlyEMI.setText(formatIndianCurrencyNoSymbol(monthlyEMI));
        tvPrincipalAmount.setText(formatIndianCurrency(principalAmount));
        tvTotalInterest.setText("+ " + formatIndianCurrency(totalInterest));
        tvTotalPayable.setText(formatIndianCurrency(totalPayable));

        // Update interest rate in interest text
        String interestText = "Total Interest (" + String.format("%.1f", interestRate) + "%)";
        // Create a new TextView reference to update the text before "Total Interest"
        // We need to update the TextView that shows "Total Interest (8.5%)"
        // Let me update the code to handle this properly

        // For now, let's update the label text
        TextView interestLabel = findViewById(R.id.tvTotalInterest);
        // Actually we need to update the label before the amount
        // Let's just update the amount for now
        tvTotalInterest.setText("+ " + formatIndianCurrency(totalInterest));
    }

    private void updateMinDownPaymentText() {
        String text = "Minimum 20% required (" + formatIndianCurrency(minDownPayment) + ")";
        tvMinDownPayment.setText(text);
    }

    private String formatIndianCurrency(double amount) {
        try {
            String formatted = currencyFormat.format(amount);
            return formatted.replace(".00", "");
        } catch (Exception e) {
            return "₹0";
        }
    }
    
    private String formatIndianCurrencyNoSymbol(double amount) {
         try {
            // Use DecimalFormat for no symbol
            return decimalFormat.format(amount);
        } catch (Exception e) {
            return "0.00";
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

        // Confirm EMI button
        // Replace the btnConfirmEMI click listener with this:
        // In EMISetupActivity.java, modify the btnConfirmEMI click listener:

        // Replace the existing btnConfirmEMI click listener with this:

        btnConfirmEMI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Validate down payment
                if (downPayment < minDownPayment) {
                    Toast.makeText(EMISetupActivity.this,
                            "Down payment must be at least " + formatIndianCurrency(minDownPayment),
                            Toast.LENGTH_LONG).show();
                    return;
                }

                // Navigate to Initial Payment screen instead of Payment Confirmed
                Intent intent = new Intent(EMISetupActivity.this, InitialPaymentActivity.class);

                // Pass all necessary data for initial payment
                intent.putExtra("vehicle_price", vehiclePrice);
                intent.putExtra("down_payment", downPayment);
                intent.putExtra("request_id", requestId); // Pass ID forward

                // Calculate and pass EMI details
                double principalAmount = vehiclePrice - downPayment;
                double monthlyInterestRate = interestRate / 12 / 100;
                double power = Math.pow(1 + monthlyInterestRate, durationMonths);
                double monthlyEMI = 0;
                 if (monthlyInterestRate > 0 && durationMonths > 0) {
                     monthlyEMI = principalAmount * monthlyInterestRate * power / (power - 1);
                } else if (durationMonths > 0) {
                     monthlyEMI = principalAmount / durationMonths;
                }
                double totalPayable = monthlyEMI * durationMonths;

                // Pass EMI calculation details
                intent.putExtra("monthly_emi", monthlyEMI);
                intent.putExtra("duration_months", durationMonths);
                intent.putExtra("interest_rate", interestRate);
                intent.putExtra("total_payable", totalPayable);

                // Pass customer and vehicle info (dynamically fetched)
                intent.putExtra("customer_name", customerName);
                intent.putExtra("vehicle_name", vehicleName);
                intent.putExtra("vehicle_details", vehicleDetails != null ? vehicleDetails : "");

                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}