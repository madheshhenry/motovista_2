package com.example.motovista_deep;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import java.text.DecimalFormat;
import java.util.Locale;

public class EmiCalculatorActivity extends AppCompatActivity {

    // Header views
    private ImageView btnBack;

    // SeekBars
    private SeekBar seekBarLoanAmount, seekBarInterestRate, seekBarLoanTenure;

    // TextViews
    private TextView tvLoanAmount, tvInterestRate, tvLoanTenure;
    private TextView tvMonthlyEMI, tvPrincipalAmount, tvTotalInterest, tvTotalAmount;

    // Donut Chart
    private DonutChartExactView donutChartView;
    private FrameLayout donutChartContainer;

    // Current values
    private double loanAmount = 105000; // ₹1,05,000
    private double interestRate = 12.0; // 12%
    private int tenureMonths = 24; // 2 years

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
    }

    private void initializeViews() {
        // Header
        btnBack = findViewById(R.id.btnBack);

        // SeekBars
        seekBarLoanAmount = findViewById(R.id.seekBarLoanAmount);
        seekBarInterestRate = findViewById(R.id.seekBarInterestRate);
        seekBarLoanTenure = findViewById(R.id.seekBarLoanTenure);

        // TextViews
        tvLoanAmount = findViewById(R.id.tvLoanAmount);
        tvInterestRate = findViewById(R.id.tvInterestRate);
        tvLoanTenure = findViewById(R.id.tvLoanTenure);
        tvMonthlyEMI = findViewById(R.id.tvMonthlyEMI);
        tvPrincipalAmount = findViewById(R.id.tvPrincipalAmount);
        tvTotalInterest = findViewById(R.id.tvTotalInterest);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);

        // Donut chart container
        donutChartContainer = findViewById(R.id.donutChartContainer);
    }

    private void setupClickListeners() {
        // Back button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setupSeekBarListeners() {
        // Loan Amount SeekBar (₹50,000 to ₹20,00,000)
        seekBarLoanAmount.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Convert progress to loan amount (50,000 + progress * 100)
                loanAmount = 50000 + (progress * 100);
                updateLoanAmountDisplay();
                calculateEMI();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
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

    private void updateLoanAmountDisplay() {
        String formattedAmount = formatIndianCurrency(loanAmount);
        tvLoanAmount.setText("₹" + formattedAmount);
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
        // Update text views with Indian currency format
        tvMonthlyEMI.setText("₹" + formatIndianCurrency(Math.round(emi)));
        tvPrincipalAmount.setText("₹" + formatIndianCurrency(Math.round(loanAmount)));
        tvTotalInterest.setText("₹" + formatIndianCurrency(Math.round(totalInterest)));
        tvTotalAmount.setText("₹" + formatIndianCurrency(Math.round(totalAmount)));

        // Update donut chart
        donutChartView.setPrincipalPercentage((float) principalPercentage);
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
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}