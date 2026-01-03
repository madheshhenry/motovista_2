package com.example.motovista_deep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Typeface; // Add this import

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EmiDetailsActivity extends AppCompatActivity {

    // Header Views
    private ImageView btnBack;
    private TextView tvTitle;

    // Progress Card Views
    private TextView tvPaidEmis, tvProgressPercentage, tvTotalLoanValue, tvPendingDuration;
    private View progressFill;

    // Quick Stats Views
    private TextView tvNextDue, tvMonthlyEmi, tvPendingAmount;

    // Current EMI Views
    private TextView tvCurrentEmiTitle, tvCurrentEmiDue, tvCurrentEmiAmount;
    private Button btnMarkAsPaid;

    // Upcoming EMI Views
    private TextView tvUpcomingEmiTitle, tvUpcomingEmiDue, tvUpcomingEmiAmount;

    // Paid EMI Container
    private LinearLayout paidEmiContainer;

    // Data Models
    private List<EmiItem> paidEmiList = new ArrayList<>();
    private EmiItem currentEmi;
    private EmiItem upcomingEmi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emi_details);

        // Get customer data from intent
        String customerName = getIntent().getStringExtra("CUSTOMER_NAME");
        String status = getIntent().getStringExtra("STATUS");

        // Initialize all views
        initializeViews();

        // Setup click listeners
        setupClickListeners();

        // Load sample data
        loadSampleData(customerName);

        // Setup paid EMI items
        setupPaidEmiItems();
    }

    private void initializeViews() {
        // Header views
        btnBack = findViewById(R.id.btnBack);
        tvTitle = findViewById(R.id.tvTitle);

        // Progress Card Views
        tvPaidEmis = findViewById(R.id.tvPaidEmis);
        tvProgressPercentage = findViewById(R.id.tvProgressPercentage);
        tvTotalLoanValue = findViewById(R.id.tvTotalLoanValue);
        tvPendingDuration = findViewById(R.id.tvPendingDuration);
        progressFill = findViewById(R.id.progressFill);

        // Quick Stats Views
        tvNextDue = findViewById(R.id.tvNextDue);
        tvMonthlyEmi = findViewById(R.id.tvMonthlyEmi);
        tvPendingAmount = findViewById(R.id.tvPendingAmount);

        // Current EMI Views
        tvCurrentEmiTitle = findViewById(R.id.tvCurrentEmiTitle);
        tvCurrentEmiDue = findViewById(R.id.tvCurrentEmiDue);
        tvCurrentEmiAmount = findViewById(R.id.tvCurrentEmiAmount);
        btnMarkAsPaid = findViewById(R.id.btnMarkAsPaid);

        // Upcoming EMI Views
        tvUpcomingEmiTitle = findViewById(R.id.tvUpcomingEmiTitle);
        tvUpcomingEmiDue = findViewById(R.id.tvUpcomingEmiDue);
        tvUpcomingEmiAmount = findViewById(R.id.tvUpcomingEmiAmount);

        // Paid EMI Container
        paidEmiContainer = findViewById(R.id.paidEmiContainer);
    }

    private void setupClickListeners() {
        // Back button click
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Mark as Paid button click
        btnMarkAsPaid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPaymentConfirmationDialog();
            }
        });
    }

    private void loadSampleData(String customerName) {
        // Update title with customer name
        if (customerName != null && !customerName.isEmpty()) {
            tvTitle.setText(customerName + " - EMI Details");
        }

        // Set progress data
        tvPaidEmis.setText("Paid: 7 / 12 EMIs");
        tvProgressPercentage.setText("58%");
        tvTotalLoanValue.setText("$14,400");
        tvPendingDuration.setText("5 Months");

        // Update progress bar weight (58% = weight 58)
        LinearLayout progressLayout = (LinearLayout) progressFill.getParent();
        View progressFill = this.progressFill;
        View progressEmpty = progressLayout.getChildAt(1);

        LinearLayout.LayoutParams fillParams = (LinearLayout.LayoutParams) progressFill.getLayoutParams();
        fillParams.weight = 58;

        LinearLayout.LayoutParams emptyParams = (LinearLayout.LayoutParams) progressEmpty.getLayoutParams();
        emptyParams.weight = 42;

        progressFill.setLayoutParams(fillParams);
        progressEmpty.setLayoutParams(emptyParams);

        // Rest of the code remains same...
        tvNextDue.setText("Oct 15");
        tvMonthlyEmi.setText("$1,200");
        tvPendingAmount.setText("$6,000");

        // Set current EMI
        currentEmi = new EmiItem("October EMI", "Due: Oct 15, 2023", "$1,200", "08", true);
        tvCurrentEmiTitle.setText(currentEmi.getTitle());
        tvCurrentEmiDue.setText(currentEmi.getDueDate());
        tvCurrentEmiAmount.setText(currentEmi.getAmount());

        // Set upcoming EMI
        upcomingEmi = new EmiItem("November EMI", "Due: Nov 15, 2023", "$1,200", "09", false);
        tvUpcomingEmiTitle.setText(upcomingEmi.getTitle());
        tvUpcomingEmiDue.setText(upcomingEmi.getDueDate());
        tvUpcomingEmiAmount.setText(upcomingEmi.getAmount());
    }

    private void setupPaidEmiItems() {
        // Clear existing views
        paidEmiContainer.removeAllViews();

        // Create sample paid EMI items
        paidEmiList.add(new EmiItem("September EMI", "Paid on Sep 14", "$1,200", "", true));
        paidEmiList.add(new EmiItem("August EMI", "Paid on Aug 15", "$1,200", "", true));
        paidEmiList.add(new EmiItem("July EMI", "Paid on Jul 12", "$1,200", "", true));

        // Add paid EMI items to container
        for (int i = 0; i < paidEmiList.size(); i++) {
            EmiItem emi = paidEmiList.get(i);
            addPaidEmiItem(emi, i);
        }
    }

    private void addPaidEmiItem(EmiItem emi, int position) {
        // Create card layout
        CardView cardView = new CardView(this);
        cardView.setCardBackgroundColor(getResources().getColor(R.color.card_background));
        cardView.setCardElevation(0);
        cardView.setRadius(dpToPx(12));
        cardView.setUseCompatPadding(true);

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 0, 0, dpToPx(12));
        cardView.setLayoutParams(cardParams);

        // Create main layout
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.HORIZONTAL);
        mainLayout.setGravity(Gravity.CENTER_VERTICAL);
        mainLayout.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));

        // Create month badge
        RelativeLayout badgeLayout = new RelativeLayout(this);
        badgeLayout.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(40), dpToPx(40)));
        badgeLayout.setBackgroundResource(R.drawable.green_circle_check);

        ImageView checkIcon = new ImageView(this);
        checkIcon.setImageResource(R.drawable.ic_check);
        checkIcon.setColorFilter(getResources().getColor(R.color.icon_green));
        RelativeLayout.LayoutParams iconParams = new RelativeLayout.LayoutParams(dpToPx(20), dpToPx(20));
        iconParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        checkIcon.setLayoutParams(iconParams);

        badgeLayout.addView(checkIcon);

        // Create EMI details layout
        LinearLayout detailsLayout = new LinearLayout(this);
        detailsLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams detailsParams = new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1
        );
        detailsParams.setMargins(dpToPx(12), 0, 0, 0);
        detailsLayout.setLayoutParams(detailsParams);

        // EMI title
        TextView titleTextView = new TextView(this);
        titleTextView.setText(emi.getTitle());
        titleTextView.setTextColor(getResources().getColor(R.color.text_primary_light));
        titleTextView.setTextSize(14);
        titleTextView.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        // Paid date and undo button layout
        LinearLayout dateLayout = new LinearLayout(this);
        dateLayout.setOrientation(LinearLayout.HORIZONTAL);
        dateLayout.setGravity(Gravity.CENTER_VERTICAL);

        // Paid date
        TextView dateTextView = new TextView(this);
        dateTextView.setText(emi.getDueDate());
        dateTextView.setTextColor(getResources().getColor(R.color.icon_green));
        dateTextView.setTextSize(12);
        titleTextView.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        // Undo button
        ImageView undoButton = new ImageView(this);
        undoButton.setImageResource(R.drawable.ic_undo);
        undoButton.setColorFilter(getResources().getColor(R.color.text_light_gray));
        undoButton.setPadding(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8));
        undoButton.setContentDescription("Undo Payment");

        LinearLayout.LayoutParams undoParams = new LinearLayout.LayoutParams(dpToPx(24), dpToPx(24));
        undoParams.setMargins(dpToPx(8), 0, 0, 0);
        undoButton.setLayoutParams(undoParams);

        undoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUndoPaymentDialog(emi, position);
            }
        });

        dateLayout.addView(dateTextView);
        dateLayout.addView(undoButton);

        // Add to details layout
        detailsLayout.addView(titleTextView);
        detailsLayout.addView(dateLayout);

        // Create amount and status layout
        LinearLayout amountLayout = new LinearLayout(this);
        amountLayout.setOrientation(LinearLayout.VERTICAL);
        amountLayout.setGravity(Gravity.END);

        // Amount
        TextView amountTextView = new TextView(this);
        amountTextView.setText(emi.getAmount());
        amountTextView.setTextColor(getResources().getColor(R.color.text_primary_light));
        amountTextView.setTextSize(14);
        amountTextView.setTypeface(amountTextView.getTypeface(), android.graphics.Typeface.BOLD);
        amountTextView.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        // Paid status
        LinearLayout statusLayout = new LinearLayout(this);
        statusLayout.setOrientation(LinearLayout.HORIZONTAL);
        statusLayout.setGravity(Gravity.CENTER);
        statusLayout.setBackgroundResource(R.drawable.paid_status_bg);
        statusLayout.setPadding(dpToPx(8), dpToPx(4), dpToPx(8), dpToPx(4));

        TextView statusTextView = new TextView(this);
        statusTextView.setText("PAID");
        statusTextView.setTextColor(getResources().getColor(R.color.icon_green));
        statusTextView.setTextSize(10);
        statusTextView.setTypeface(statusTextView.getTypeface(), android.graphics.Typeface.BOLD);
        dateTextView.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
        statusLayout.addView(statusTextView);

        // Add to amount layout
        amountLayout.addView(amountTextView);
        amountLayout.addView(statusLayout);

        // Add all views to main layout
        mainLayout.addView(badgeLayout);
        mainLayout.addView(detailsLayout);
        mainLayout.addView(amountLayout);

        // Add main layout to card
        cardView.addView(mainLayout);

        // Add card to container
        paidEmiContainer.addView(cardView);
    }

    private void showPaymentConfirmationDialog() {
        final Dialog dialog = new Dialog(this, R.style.BottomSheetDialogTheme);
        dialog.setContentView(R.layout.dialog_payment_confirmation);

        // Set dialog properties
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.BOTTOM);
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }

        // Initialize dialog views
        Button btnConfirmPayment = dialog.findViewById(R.id.btnConfirmPayment);
        ImageView btnCloseDialog = dialog.findViewById(R.id.btnCloseDialog);
        Spinner spinnerPaymentMode = dialog.findViewById(R.id.spinnerPaymentMode);
        EditText etPaymentDate = dialog.findViewById(R.id.etPaymentDate);
        EditText etRemarks = dialog.findViewById(R.id.etRemarks);

        // Set up spinner with payment modes
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.payment_modes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPaymentMode.setAdapter(adapter);

        // Set default selection to first item
        spinnerPaymentMode.setSelection(0);

        // Set today's date as default
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = sdf.format(new Date());
        etPaymentDate.setText(currentDate);

        btnCloseDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnConfirmPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get selected payment mode
                String paymentMode = spinnerPaymentMode.getSelectedItem().toString();

                // Validate selection
                if (paymentMode.equals("Select Payment Mode")) {
                    Toast.makeText(EmiDetailsActivity.this, "Please select a payment mode", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Handle payment confirmation
                Toast.makeText(EmiDetailsActivity.this, "Payment marked as paid via " + paymentMode, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                // Update UI to show as paid
                updateCurrentEmiAsPaid();
            }
        });

        dialog.show();
    }

    private void showUndoPaymentDialog(EmiItem emi, int position) {
        final Dialog dialog = new Dialog(this, R.style.BottomSheetDialogTheme);
        dialog.setContentView(R.layout.dialog_undo_payment);

        // Set dialog properties
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.BOTTOM);
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }

        // Initialize dialog views
        TextView tvEmiTitle = dialog.findViewById(R.id.tvEmiTitle);
        TextView tvEmiAmount = dialog.findViewById(R.id.tvEmiAmount);
        Button btnConfirmUndo = dialog.findViewById(R.id.btnConfirmUndo);
        ImageView btnCloseDialog = dialog.findViewById(R.id.btnCloseDialog);
        EditText etReason = dialog.findViewById(R.id.etReason);

        // Set data
        tvEmiTitle.setText(emi.getTitle());
        tvEmiAmount.setText(emi.getAmount());

        btnCloseDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnConfirmUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reason = etReason.getText().toString().trim();
                // Handle undo payment
                String message = "Payment reversed";
                if (!reason.isEmpty()) {
                    message += " - Reason: " + reason;
                }
                Toast.makeText(EmiDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                // Remove from paid list and add to pending
                paidEmiList.remove(position);
                paidEmiContainer.removeViewAt(position);
            }
        });

        dialog.show();
    }

    private void updateCurrentEmiAsPaid() {
        // Move current EMI to paid list
        paidEmiList.add(0, currentEmi);
        paidEmiContainer.removeAllViews();
        setupPaidEmiItems();

        // Update current EMI to upcoming EMI
        currentEmi = upcomingEmi;
        tvCurrentEmiTitle.setText(currentEmi.getTitle());
        tvCurrentEmiDue.setText(currentEmi.getDueDate());
        tvCurrentEmiAmount.setText(currentEmi.getAmount());

        // Update upcoming EMI (for demo, just update month)
        String nextMonth = getNextMonth(currentEmi.getTitle());
        upcomingEmi = new EmiItem(nextMonth + " EMI", "Due: " + getNextMonthDueDate(), "$1,200", "", false);
        tvUpcomingEmiTitle.setText(upcomingEmi.getTitle());
        tvUpcomingEmiDue.setText(upcomingEmi.getDueDate());
        tvUpcomingEmiAmount.setText(upcomingEmi.getAmount());

        // Update progress
        updateProgress();
    }

    private void updateProgress() {
        // Update paid count and progress
        int paidCount = paidEmiList.size() + 1; // +1 for current if marked as paid
        int totalCount = 12; // Assuming 12 months total

        tvPaidEmis.setText("Paid: " + paidCount + " / " + totalCount + " EMIs");

        int percentage = (paidCount * 100) / totalCount;
        tvProgressPercentage.setText(percentage + "%");

        // Update progress bar - remove old code
        // ViewGroup.LayoutParams params = progressFill.getLayoutParams();
        // params.width = (int) (getResources().getDisplayMetrics().density * percentage * 3);
        // progressFill.setLayoutParams(params);

        // Instead, we'll update the LinearLayout weight
        LinearLayout progressLayout = (LinearLayout) progressFill.getParent();
        View progressFill = this.progressFill;
        View progressEmpty = progressLayout.getChildAt(1);

        LinearLayout.LayoutParams fillParams = (LinearLayout.LayoutParams) progressFill.getLayoutParams();
        fillParams.weight = percentage;

        LinearLayout.LayoutParams emptyParams = (LinearLayout.LayoutParams) progressEmpty.getLayoutParams();
        emptyParams.weight = 100 - percentage;

        progressFill.setLayoutParams(fillParams);
        progressEmpty.setLayoutParams(emptyParams);

        // Update pending duration
        int pendingMonths = totalCount - paidCount;
        tvPendingDuration.setText(pendingMonths + " Months");

        // Update pending amount
        int pendingAmount = pendingMonths * 1200; // $1,200 per month
        tvPendingAmount.setText("$" + String.format("%,d", pendingAmount));
    }

    private String getNextMonth(String currentMonth) {
        // Simple month rotation for demo
        String[] months = {"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};

        for (int i = 0; i < months.length; i++) {
            if (currentMonth.contains(months[i])) {
                return months[(i + 1) % months.length];
            }
        }
        return "December";
    }

    private String getNextMonthDueDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
        return sdf.format(calendar.getTime());
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    // EMI Item Model Class
    private class EmiItem {
        private String title;
        private String dueDate;
        private String amount;
        private String monthNumber;
        private boolean isPaid;

        public EmiItem(String title, String dueDate, String amount, String monthNumber, boolean isPaid) {
            this.title = title;
            this.dueDate = dueDate;
            this.amount = amount;
            this.monthNumber = monthNumber;
            this.isPaid = isPaid;
        }

        public String getTitle() { return title; }
        public String getDueDate() { return dueDate; }
        public String getAmount() { return amount; }
        public String getMonthNumber() { return monthNumber; }
        public boolean isPaid() { return isPaid; }
    }
}