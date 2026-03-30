package com.example.motovista_deep;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Typeface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import java.text.DecimalFormat;
import java.util.Locale;

public class EmiCalculatorActivity extends AppCompatActivity {

    // Header views
    private ImageView btnBack;

    // SeekBars
    private SeekBar seekBarLoanAmount, seekBarInterestRate, seekBarLoanTenure;

    // EditTexts & TextViews
    private android.widget.EditText etLoanAmount;
    private TextView tvInterestRate, tvLoanTenure;
    private TextView tvMonthlyEMI, tvPrincipalAmount, tvTotalInterest, tvTotalAmount;

    // Bottom Navigation
    private LinearLayout tabHome, tabBikes, tabEmiCalculator, tabOrders, tabProfile;
    private ImageView ivHome, ivBikes, ivEmiCalculator, ivOrders, ivProfile;
    private TextView tvHome, tvBikes, tvEmiCalculator, tvOrders, tvProfile;

    // Donut Chart
    private DonutChartExactView donutChartView;
    private FrameLayout donutChartContainer;

    // Current values
    private double loanAmount = 105000; // ₹1,05,000
    private double interestRate = 12.0; // 12%
    private int tenureMonths = 24; // 2 years
    private long previousEmi = 0;

    // Formatting
    private DecimalFormat decimalFormat = new DecimalFormat("#.##");
    private DecimalFormat currencyFormat = new DecimalFormat("#,##,###");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emi_calculator);

        // Initialize views
        initializeViews();

        // Setup click listeners
        setupClickListeners();

        // Setup SeekBar listeners
        setupSeekBarListeners();

        // Setup donut chart
        setupDonutChart();

        // Calculate initial EMI
        calculateEMI();
        
        // Initial Loan Amount display sync
        updateLoanAmountDisplay(true);

        // Set active tab
        setActiveTab(tabEmiCalculator);
    }

    private void initializeViews() {
        // Header
        btnBack = findViewById(R.id.btnBack);

        // SeekBars
        seekBarLoanAmount = findViewById(R.id.seekBarLoanAmount);
        seekBarInterestRate = findViewById(R.id.seekBarInterestRate);
        seekBarLoanTenure = findViewById(R.id.seekBarLoanTenure);

        // TextViews
        // EditTexts & TextViews
        etLoanAmount = findViewById(R.id.etLoanAmount);
        tvInterestRate = findViewById(R.id.tvInterestRate);
        tvLoanTenure = findViewById(R.id.tvLoanTenure);
        tvMonthlyEMI = findViewById(R.id.tvMonthlyEMI);
        tvPrincipalAmount = findViewById(R.id.tvPrincipalAmount);
        tvTotalInterest = findViewById(R.id.tvTotalInterest);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);

        // Donut chart container
        donutChartContainer = findViewById(R.id.donutChartContainer);

        // Bottom Navigation
        tabHome = findViewById(R.id.tabHome);
        tabBikes = findViewById(R.id.tabBikes);
        tabEmiCalculator = findViewById(R.id.tabEmiCalculator);
        tabOrders = findViewById(R.id.tabOrders);
        tabProfile = findViewById(R.id.tabProfile);

        ivHome = findViewById(R.id.ivHome);
        ivBikes = findViewById(R.id.ivBikes);
        ivEmiCalculator = findViewById(R.id.ivEmiCalculator);
        ivOrders = findViewById(R.id.ivOrders);
        ivProfile = findViewById(R.id.ivProfile);

        tvHome = findViewById(R.id.tvHome);
        tvBikes = findViewById(R.id.tvBikes);
        tvEmiCalculator = findViewById(R.id.tvEmiCalculator);
        tvOrders = findViewById(R.id.tvOrders);
        tvProfile = findViewById(R.id.tvProfile);
    }

    private void setupClickListeners() {
        // Back button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Bottom Navigation
        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        tabHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, CustomerHomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        });

        tabBikes.setOnClickListener(v -> {
            startActivity(new Intent(this, BikeCatalogActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });

        tabEmiCalculator.setOnClickListener(v -> {
            // Already here
            setActiveTab(tabEmiCalculator);
        });

        tabOrders.setOnClickListener(v -> {
            startActivity(new Intent(this, CustomerOrdersActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });

        tabProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, CustomerProfileScreenActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });
    }

    private void setActiveTab(LinearLayout activeTab) {
        resetAllTabs();

        int activeColor = androidx.core.content.ContextCompat.getColor(this, R.color.primary_color);

        if (activeTab == tabHome) {
            ivHome.setImageResource(R.drawable.ic_home_filled);
            ivHome.setColorFilter(activeColor);
            tvHome.setTextColor(activeColor);
            tvHome.setTypeface(tvHome.getTypeface(), Typeface.BOLD);
        } else if (activeTab == tabBikes) {
            ivBikes.setColorFilter(activeColor);
            tvBikes.setTextColor(activeColor);
            tvBikes.setTypeface(tvBikes.getTypeface(), Typeface.BOLD);
        } else if (activeTab == tabEmiCalculator) {
            ivEmiCalculator.setColorFilter(activeColor);
            tvEmiCalculator.setTextColor(activeColor);
            tvEmiCalculator.setTypeface(tvEmiCalculator.getTypeface(), Typeface.BOLD);
        } else if (activeTab == tabOrders) {
            ivOrders.setImageResource(R.drawable.ic_receipt_long_filled);
            ivOrders.setColorFilter(activeColor);
            tvOrders.setTextColor(activeColor);
            tvOrders.setTypeface(tvOrders.getTypeface(), Typeface.BOLD);
        } else if (activeTab == tabProfile) {
            ivProfile.setImageResource(R.drawable.ic_person_filled);
            ivProfile.setColorFilter(activeColor);
            tvProfile.setTextColor(activeColor);
            tvProfile.setTypeface(tvProfile.getTypeface(), Typeface.BOLD);
        }
    }

    private void resetAllTabs() {
        int inactiveColor = androidx.core.content.ContextCompat.getColor(this, R.color.gray_400);

        // Reset Home
        ivHome.setImageResource(R.drawable.ic_home);
        ivHome.setColorFilter(inactiveColor);
        tvHome.setTextColor(inactiveColor);
        tvHome.setTypeface(null, Typeface.NORMAL);

        // Reset Bikes
        ivBikes.setColorFilter(inactiveColor);
        tvBikes.setTextColor(inactiveColor);
        tvBikes.setTypeface(null, Typeface.NORMAL);

        // Reset EMI Calculator
        ivEmiCalculator.setColorFilter(inactiveColor);
        tvEmiCalculator.setTextColor(inactiveColor);
        tvEmiCalculator.setTypeface(null, Typeface.NORMAL);

        // Reset Orders
        ivOrders.setImageResource(R.drawable.ic_receipt_long);
        ivOrders.setColorFilter(inactiveColor);
        tvOrders.setTextColor(inactiveColor);
        tvOrders.setTypeface(null, Typeface.NORMAL);

        // Reset Profile
        ivProfile.setImageResource(R.drawable.ic_person);
        ivProfile.setColorFilter(inactiveColor);
        tvProfile.setTextColor(inactiveColor);
        tvProfile.setTypeface(null, Typeface.NORMAL);
    }

    private void setupSeekBarListeners() {
        // Loan Amount SeekBar (₹50,000 to ₹20,00,000, Steps of ₹5,000)
        seekBarLoanAmount.setMax(390); // (2,000,000 - 50,000) / 5,000 = 390
        seekBarLoanAmount.setProgress(11); // Initial: 50,000 + 11*5000 = 105,000
        
        seekBarLoanAmount.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    // Convert progress to loan amount (50,000 + progress * 5,000)
                    loanAmount = 50000 + (progress * 5000);
                    updateLoanAmountDisplay(false); // don't update et if fromUser
                    calculateEMI();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // etLoanAmount text watcher
        etLoanAmount.addTextChangedListener(new android.text.TextWatcher() {
            private String current = "";
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(android.text.Editable s) {
                if (!s.toString().equals(current)) {
                    etLoanAmount.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[^\\d]", "");
                    if (cleanString.length() > 0) {
                        double parsed = Double.parseDouble(cleanString);
                        
                        // Clamp value
                        if (parsed > 2000000) parsed = 2000000;
                        loanAmount = parsed;
                        
                        // Format for display
                        String formatted = formatIndianCurrency(loanAmount);
                        current = formatted;
                        etLoanAmount.setText(formatted);
                        etLoanAmount.setSelection(formatted.length());
                        
                        // Update SeekBar progress
                        int progress = (int) ((loanAmount - 50000) / 5000);
                        if (progress < 0) progress = 0;
                        if (progress > 390) progress = 390;
                        seekBarLoanAmount.setProgress(progress);
                        
                        calculateEMI();
                    } else {
                        current = "";
                        etLoanAmount.setText("");
                        loanAmount = 0;
                        calculateEMI();
                    }

                    etLoanAmount.addTextChangedListener(this);
                }
            }
        });

        // Interest Rate SeekBar (6% to 20%, step 0.1%)
        seekBarInterestRate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Convert progress to interest rate (6 + progress * 0.1)
                interestRate = 6.0 + (progress * 0.1);
                updateInterestRateDisplay();
                calculateEMI();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Loan Tenure SeekBar (12 to 72 months = 1 to 6 years)
        seekBarLoanTenure.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Convert progress to months (12 + progress)
                tenureMonths = 12 + progress;
                updateLoanTenureDisplay();
                calculateEMI();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void setupDonutChart() {
        // Create and add donut chart view
        donutChartView = new DonutChartExactView(this);
        donutChartContainer.addView(donutChartView);
    }

    private void updateLoanAmountDisplay(boolean updateEt) {
        String formattedAmount = formatIndianCurrency(loanAmount);
        if (updateEt) {
            etLoanAmount.setText(formattedAmount);
        }
    }

    private void updateInterestRateDisplay() {
        tvInterestRate.setText(decimalFormat.format(interestRate) + "%");
    }

    private void updateLoanTenureDisplay() {
        int years = tenureMonths / 12;
        int months = tenureMonths % 12;

        if (months == 0) {
            tvLoanTenure.setText(years + " Yr");
        } else {
            tvLoanTenure.setText(years + " Yr " + months + " Mo");
        }
    }

    private void calculateEMI() {
        // Calculate monthly interest rate
        double monthlyInterestRate = interestRate / 12 / 100;

        // EMI Formula: EMI = [P x R x (1+R)^N]/[(1+R)^N-1]
        double emi = 0;

        if (monthlyInterestRate > 0) {
            double temp = Math.pow(1 + monthlyInterestRate, tenureMonths);
            emi = (loanAmount * monthlyInterestRate * temp) / (temp - 1);
        } else {
            emi = loanAmount / tenureMonths;
        }

        // Calculate total values
        double totalAmount = emi * tenureMonths;
        double totalInterest = totalAmount - loanAmount;

        // Calculate percentages for donut chart
        double principalPercentage = (loanAmount / totalAmount) * 100;

        // Update UI
        updateEMIDisplay(emi, totalInterest, totalAmount, principalPercentage);
    }

    private void updateEMIDisplay(double emi, double totalInterest, double totalAmount, double principalPercentage) {
        // Update Breakdown Section
        tvPrincipalAmount.setText("₹" + formatIndianCurrency(Math.round(loanAmount)));
        tvTotalInterest.setText("₹" + formatIndianCurrency(Math.round(totalInterest)));
        
        // Update total amount
        tvTotalAmount.setText("₹" + formatIndianCurrency(Math.round(totalAmount)));

        // Update donut chart without animation
        donutChartView.setPrincipalPercentage((float) principalPercentage);
    }

    private boolean isInitialCalculation = true;

    private void animateValue(TextView textView, long start, long end, String prefix) {
        textView.setText(prefix + formatIndianCurrency(end));
    }

    private String formatIndianCurrency(double amount) {
        long amountLong = (long) amount;

        if (amountLong < 1000) {
            return String.valueOf(amountLong);
        }

        // Convert to string
        String amountStr = String.valueOf(amountLong);
        int len = amountStr.length();

        if (len <= 3) {
            return amountStr;
        }

        // Handle Indian numbering system
        StringBuilder result = new StringBuilder();
        int count = 0;

        // Process from right to left
        for (int i = len - 1; i >= 0; i--) {
            result.insert(0, amountStr.charAt(i));
            count++;

            // Add comma after every 2 digits except first 3 digits
            if (count == 3 && i > 0) {
                result.insert(0, ',');
                count = 0;
            } else if (count == 2 && i > 0 && (len - i) > 3) {
                result.insert(0, ',');
                count = 0;
            }
        }

        return result.toString();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, CustomerHomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }
}