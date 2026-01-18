package com.example.motovista_deep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.motovista_deep.adapter.PaymentScheduleAdapter;
import com.example.motovista_deep.api.ApiService;
import com.example.motovista_deep.api.RetrofitClient;
import com.example.motovista_deep.utils.ImageUtils;
import com.example.motovista_deep.models.EmiDetailsData;
import com.example.motovista_deep.models.EmiLedgerItem;
import com.example.motovista_deep.models.GenericResponse;
import com.example.motovista_deep.models.GetEmiDetailsResponse;
import com.example.motovista_deep.models.NotifyPaymentRequest;
import com.example.motovista_deep.models.PayEmiRequest;
import com.example.motovista_deep.models.PaymentScheduleItem;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmiDetailsActivity extends AppCompatActivity implements PaymentScheduleAdapter.OnPaymentClickListener {

    // Header Views
    private ImageView btnBack;
    private TextView tvTitle;

    // Summary Card
    private ImageView ivBikeImage;
    private TextView tvBikeName, tvStatusBadge;
    private TextView tvTotalLoanValue, tvMonthlyEmi, tvRemainingBalance;
    private TextView tvPaidProgressText, tvProgressDesc;
    private ProgressBar emiProgressBar;

    // Payment Schedule
    private RecyclerView rvPaymentSchedule;
    private PaymentScheduleAdapter scheduleAdapter;

    // Data
    private int ledgerId;
    private EmiLedgerItem currentLedger;
    private boolean isCustomerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emi_details);

        // Get data from intent
        ledgerId = getIntent().getIntExtra("LEDGER_ID", -1);
        isCustomerView = getIntent().getBooleanExtra("IS_CUSTOMER_VIEW", false);

        if (ledgerId == -1) {
            Toast.makeText(this, "Invalid Ledger ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViews();
        setupClickListeners();
        fetchEmiDetails();
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        tvTitle = findViewById(R.id.tvTitle);

        ivBikeImage = findViewById(R.id.ivBikeImage);
        tvBikeName = findViewById(R.id.tvBikeName);
        tvStatusBadge = findViewById(R.id.tvStatusBadge);
        
        tvTotalLoanValue = findViewById(R.id.tvTotalLoanValue);
        tvMonthlyEmi = findViewById(R.id.tvMonthlyEmi);
        tvRemainingBalance = findViewById(R.id.tvRemainingBalance);
        
        tvPaidProgressText = findViewById(R.id.tvPaidProgressText);
        tvProgressDesc = findViewById(R.id.tvProgressDesc);
        emiProgressBar = findViewById(R.id.emiProgressBar);

        rvPaymentSchedule = findViewById(R.id.rvPaymentSchedule);
        rvPaymentSchedule.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> onBackPressed());
    }

    private void fetchEmiDetails() {
        ApiService apiService = RetrofitClient.getApiService();
        apiService.getEmiDetails(ledgerId).enqueue(new Callback<GetEmiDetailsResponse>() {
            @Override
            public void onResponse(Call<GetEmiDetailsResponse> call, Response<GetEmiDetailsResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    updateUI(response.body().getData());
                } else {
                    Toast.makeText(EmiDetailsActivity.this, "Failed to fetch details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GetEmiDetailsResponse> call, Throwable t) {
                Toast.makeText(EmiDetailsActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(EmiDetailsData data) {
        currentLedger = data.getLedger();
        List<PaymentScheduleItem> schedule = data.getPaymentSchedule();

        if (currentLedger != null) {
            tvBikeName.setText(currentLedger.getBikeModel() != null ? currentLedger.getBikeModel() : currentLedger.getVehicleName());
            tvStatusBadge.setText(currentLedger.getStatus().toUpperCase());
            
            tvTotalLoanValue.setText("₹" + currentLedger.getTotalAmount());
            tvMonthlyEmi.setText("₹" + currentLedger.getEmiMonthlyAmount());
            tvRemainingBalance.setText("₹" + currentLedger.getRemainingAmount());

            // Progress Logic
            double totalAmount = parseDoubleSafe(currentLedger.getTotalAmount());
            double paidAmount = parseDoubleSafe(currentLedger.getPaidAmount());
            int progressPercent = 0;
            if (totalAmount > 0) {
                progressPercent = (int) ((paidAmount / totalAmount) * 100);
            }
            
            int paidCount = 0;
            if (schedule != null) {
                for (PaymentScheduleItem item : schedule) {
                    if (item.getStatus().equalsIgnoreCase("paid")) paidCount++;
                }
            }
            
            tvPaidProgressText.setText(paidCount + " of " + currentLedger.getDurationMonths());
            emiProgressBar.setProgress(progressPercent);
            tvProgressDesc.setText(progressPercent + "% of your loan is completed");

            // Load Bike Image
            String bikeImagesJson = currentLedger.getBikeImages();
            String firstImage = extractFirstImage(bikeImagesJson);
            Glide.with(this)
                 .load(getFullImageUrl(firstImage))
                 .placeholder(R.drawable.bike_placeholder)
                 .error(R.drawable.bike_placeholder)
                 .into(ivBikeImage);
        }

        if (schedule != null) {
            boolean isCompleted = currentLedger.getStatus().equalsIgnoreCase("completed");
            scheduleAdapter = new PaymentScheduleAdapter(schedule, !isCustomerView, isCompleted, this);
            rvPaymentSchedule.setAdapter(scheduleAdapter);
        }
    }

    private String extractFirstImage(String json) {
        if (json == null || json.isEmpty()) return null;
        try {
            if (json.startsWith("[")) {
                JSONArray array = new JSONArray(json);
                if (array.length() > 0) return array.getString(0);
            } else {
                return json.split(",")[0].replace("\"", "").replace("[", "").replace("]", "").trim();
            }
        } catch (Exception e) {}
        return json;
    }

    private double parseDoubleSafe(String value) {
        if (value == null || value.isEmpty()) return 0.0;
        try {
            return Double.parseDouble(value.replace(",", "").trim());
        } catch (Exception e) {
            return 0.0;
        }
    }

    private String getFullImageUrl(String path) {
        return ImageUtils.getFullImageUrl(path);
    }

    @Override
    public void onNotifyPaid(PaymentScheduleItem item) {
        // Customer side notification
        ApiService apiService = RetrofitClient.getApiService();
        NotifyPaymentRequest request = new NotifyPaymentRequest(ledgerId, parseDoubleSafe(item.getAmount()));
        
        apiService.notifyEmiPayment(request).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(EmiDetailsActivity.this, "Admin notified. Awaiting verification.", Toast.LENGTH_LONG).show();
                    fetchEmiDetails();
                } else {
                    Toast.makeText(EmiDetailsActivity.this, "Notification failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                Toast.makeText(EmiDetailsActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onAdminMarkAsPaid(PaymentScheduleItem item) {
        showAdminPaymentDialog(item);
    }

    private void showAdminPaymentDialog(PaymentScheduleItem item) {
        final Dialog dialog = new Dialog(this, R.style.BottomSheetDialogTheme);
        dialog.setContentView(R.layout.dialog_payment_confirmation);

        // Init Views
        EditText etAmount = dialog.findViewById(R.id.etAmount);
        EditText etFine = dialog.findViewById(R.id.etFine);
        EditText etPaymentDate = dialog.findViewById(R.id.etPaymentDate);
        EditText etRemarks = dialog.findViewById(R.id.etRemarks);
        Spinner spinnerPaymentMode = dialog.findViewById(R.id.spinnerPaymentMode);
        Button btnConfirmPayment = dialog.findViewById(R.id.btnConfirmPayment);
        ImageView btnCloseDialog = dialog.findViewById(R.id.btnCloseDialog);

        // Pre-fill
        etAmount.setText(item.getAmount());
        etFine.setText(String.valueOf(item.getFine()));
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        etPaymentDate.setText(sdf.format(new Date()));

        // Setup Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.payment_modes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPaymentMode.setAdapter(adapter);

        btnCloseDialog.setOnClickListener(v -> dialog.dismiss());

        btnConfirmPayment.setOnClickListener(v -> {
            String mode = spinnerPaymentMode.getSelectedItem().toString();
            String date = etPaymentDate.getText().toString();
            String remarks = etRemarks.getText().toString();
            double amount = parseDoubleSafe(etAmount.getText().toString());
            double fine = parseDoubleSafe(etFine.getText().toString());

            if (mode.equals("Select Payment Mode")) {
                Toast.makeText(this, "Please select a payment mode", Toast.LENGTH_SHORT).show();
                return;
            }

            processAdminPayment(amount, fine, date, mode, remarks, item.getPaymentId(), dialog);
        });

        // Set Dialog Params
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.BOTTOM);
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }
        dialog.show();
    }

    private void processAdminPayment(double amount, double fine, String date, String mode, String remarks, Integer paymentId, Dialog dialog) {
        ApiService apiService = RetrofitClient.getApiService();
        PayEmiRequest request = new PayEmiRequest(ledgerId, amount, fine, date, mode, remarks, paymentId);

        apiService.payEmiInstallment(request).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(EmiDetailsActivity.this, "Payment Recorded", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    fetchEmiDetails();
                } else {
                    Toast.makeText(EmiDetailsActivity.this, "Payment Failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                Toast.makeText(EmiDetailsActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}