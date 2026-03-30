package com.example.motovista_deep;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.motovista_deep.api.ApiService;
import com.example.motovista_deep.api.RetrofitClient;
import com.example.motovista_deep.models.RegistrationLedgerItem;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationRecordActivity extends AppCompatActivity {

    private int ledgerId;
    private TextView tvCustomerName, tvBikeName, tvCustomerPhone, tvEngineNumber, tvOrderId, tvCompletedCount;
    private ImageView btnBack;

    // Step Views
    private View layoutStep1, layoutStep2, layoutStep3, layoutStep4;
    private Button btnStep1, btnStep2, btnStep3, btnStep4;
    private TextView tvStatus1, tvStatus2, tvStatus3, tvStatus4;
    private ImageView iconStep1, iconStep2, iconStep3, iconStep4;
    private TextView titleStep1, titleStep2, titleStep3, titleStep4;
    private View active1, active2, active3, active4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_record);

        ledgerId = getIntent().getIntExtra("ledger_id", -1);
        String custName = getIntent().getStringExtra("customer_name");
        String bikeName = getIntent().getStringExtra("bike_name");
        String phone = getIntent().getStringExtra("phone");
        String engine = getIntent().getStringExtra("engine_number");

        if (ledgerId == -1) {
            Toast.makeText(this, "Invalid Record ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViews();
        tvCustomerName.setText(custName != null ? custName : "N/A");
        tvBikeName.setText(bikeName != null ? bikeName : "N/A");
        tvCustomerPhone.setText(phone != null ? phone : "N/A");
        tvEngineNumber.setText(engine != null ? engine : "N/A");
        tvOrderId.setText("ORD-" + ledgerId); // Placeholder for ORD ID

        setupStepTitles();

        btnBack.setOnClickListener(v -> finish());
        
        // Setup Button Listeners
        btnStep1.setOnClickListener(v -> showConfirmationDialog(1, "Insurance Process"));
        btnStep2.setOnClickListener(v -> showConfirmationDialog(2, "Document Verification"));
        btnStep3.setOnClickListener(v -> showConfirmationDialog(3, "Tax Payment"));
        btnStep4.setOnClickListener(v -> showConfirmationDialog(4, "Registration & RC"));

        fetchRecordDetails();
    }

    private void initializeViews() {
        tvCustomerName = findViewById(R.id.tvCustomerName);
        tvBikeName = findViewById(R.id.tvBikeName);
        tvCustomerPhone = findViewById(R.id.tvCustomerPhone);
        tvEngineNumber = findViewById(R.id.tvEngineNumber);
        tvOrderId = findViewById(R.id.tvOrderId);
        tvCompletedCount = findViewById(R.id.tvCompletedCount);
        btnBack = findViewById(R.id.btnBack);

        layoutStep1 = findViewById(R.id.layoutStep1);
        layoutStep2 = findViewById(R.id.layoutStep2);
        layoutStep3 = findViewById(R.id.layoutStep3);
        layoutStep4 = findViewById(R.id.layoutStep4);

        // Map internal views
        btnStep1 = layoutStep1.findViewById(R.id.btnMarkCompleted);
        tvStatus1 = layoutStep1.findViewById(R.id.tvStepStatus);
        iconStep1 = layoutStep1.findViewById(R.id.ivStepIcon);
        titleStep1 = layoutStep1.findViewById(R.id.tvStepTitle);
        active1 = layoutStep1.findViewById(R.id.activeIndicator);

        btnStep2 = layoutStep2.findViewById(R.id.btnMarkCompleted);
        tvStatus2 = layoutStep2.findViewById(R.id.tvStepStatus);
        iconStep2 = layoutStep2.findViewById(R.id.ivStepIcon);
        titleStep2 = layoutStep2.findViewById(R.id.tvStepTitle);
        active2 = layoutStep2.findViewById(R.id.activeIndicator);

        btnStep3 = layoutStep3.findViewById(R.id.btnMarkCompleted);
        tvStatus3 = layoutStep3.findViewById(R.id.tvStepStatus);
        iconStep3 = layoutStep3.findViewById(R.id.ivStepIcon);
        titleStep3 = layoutStep3.findViewById(R.id.tvStepTitle);
        active3 = layoutStep3.findViewById(R.id.activeIndicator);

        btnStep4 = layoutStep4.findViewById(R.id.btnMarkCompleted);
        tvStatus4 = layoutStep4.findViewById(R.id.tvStepStatus);
        iconStep4 = layoutStep4.findViewById(R.id.ivStepIcon);
        titleStep4 = layoutStep4.findViewById(R.id.tvStepTitle);
        active4 = layoutStep4.findViewById(R.id.activeIndicator);
    }

    private void setupStepTitles() {
        titleStep1.setText("Insurance Process");
        titleStep2.setText("Document Verification");
        titleStep3.setText("Tax Payment");
        titleStep4.setText("Registration & RC");
    }

    private void fetchRecordDetails() {
        ApiService apiService = RetrofitClient.getApiService();
        Call<com.example.motovista_deep.models.GetRegistrationLedgerResponse> call = apiService.getRegistrationLedger();

        call.enqueue(new Callback<com.example.motovista_deep.models.GetRegistrationLedgerResponse>() {
            @Override
            public void onResponse(Call<com.example.motovista_deep.models.GetRegistrationLedgerResponse> call, Response<com.example.motovista_deep.models.GetRegistrationLedgerResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        for (RegistrationLedgerItem item : response.body().getData()) {
                            if (item.getId() == ledgerId) {
                                updateUI(item);
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<com.example.motovista_deep.models.GetRegistrationLedgerResponse> call, Throwable t) {
                Toast.makeText(RegistrationRecordActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(RegistrationLedgerItem data) {
        int completedCount = 0;
        if ("completed".equalsIgnoreCase(data.getStep1Status())) completedCount++;
        if ("completed".equalsIgnoreCase(data.getStep2Status())) completedCount++;
        if ("completed".equalsIgnoreCase(data.getStep3Status())) completedCount++;
        if ("completed".equalsIgnoreCase(data.getStep4Status())) completedCount++;
        tvCompletedCount.setText(completedCount + "/4 Completed");

        configureStep(data.getStep1Status(), btnStep1, tvStatus1, iconStep1, active1);
        configureStep(data.getStep2Status(), btnStep2, tvStatus2, iconStep2, active2);
        configureStep(data.getStep3Status(), btnStep3, tvStatus3, iconStep3, active3);
        configureStep(data.getStep4Status(), btnStep4, tvStatus4, iconStep4, active4);
    }

    private void configureStep(String status, Button btn, TextView tvStatus, ImageView icon, View active) {
        if ("completed".equalsIgnoreCase(status)) {
            btn.setVisibility(View.GONE);
            tvStatus.setText("COMPLETED");
            tvStatus.setBackgroundResource(R.drawable.pill_inactive);
            tvStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#f0fdf4")));
            tvStatus.setTextColor(android.graphics.Color.parseColor("#16a34a"));
            icon.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#f0fdf4")));
            icon.setImageTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#16a34a")));
            active.setVisibility(View.GONE);
        } else if ("locked".equalsIgnoreCase(status)) {
            btn.setVisibility(View.GONE);
            tvStatus.setText("LOCKED");
            tvStatus.setBackgroundResource(R.drawable.pill_inactive);
            tvStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#f3f4f6")));
            tvStatus.setTextColor(android.graphics.Color.parseColor("#9ca3af"));
            icon.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#f3f4f6")));
            icon.setImageTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#9ca3af")));
            active.setVisibility(View.GONE);
        } else { // pending (active step)
            btn.setVisibility(View.VISIBLE);
            tvStatus.setText("PENDING");
            tvStatus.setBackgroundResource(R.drawable.pill_inactive);
            tvStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#fff7ed")));
            tvStatus.setTextColor(android.graphics.Color.parseColor("#ea580c"));
            icon.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#eff6ff")));
            icon.setImageTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#137fec")));
            active.setVisibility(View.VISIBLE);
        }
    }

    private void showConfirmationDialog(int step, String stepName) {
        android.app.Dialog dialog = new android.app.Dialog(this);
        dialog.setContentView(R.layout.dialog_confirm_completion);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView tvMsg = dialog.findViewById(R.id.tvConfirmationMessage);
        tvMsg.setText("Are you sure you want to mark " + stepName + " as Completed?");

        TextView tvDate = dialog.findViewById(R.id.tvDateDisplay);
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
        tvDate.setText(sdf.format(new java.util.Date()));

        dialog.findViewById(R.id.btnCancel).setOnClickListener(v -> dialog.dismiss());
        dialog.findViewById(R.id.btnConfirm).setOnClickListener(v -> {
            updateStep(step);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void updateStep(int step) {
        ApiService apiService = RetrofitClient.getApiService();
        com.example.motovista_deep.models.UpdateRegistrationStepRequest request = 
            new com.example.motovista_deep.models.UpdateRegistrationStepRequest(ledgerId, step);

        Call<com.example.motovista_deep.models.GenericResponse> call = apiService.updateRegistrationStep(request);
        
        call.enqueue(new retrofit2.Callback<com.example.motovista_deep.models.GenericResponse>() {
            @Override
            public void onResponse(Call<com.example.motovista_deep.models.GenericResponse> call, retrofit2.Response<com.example.motovista_deep.models.GenericResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(RegistrationRecordActivity.this, "Step Updated!", Toast.LENGTH_SHORT).show();
                    fetchRecordDetails(); 
                } else {
                    Toast.makeText(RegistrationRecordActivity.this, "Failed to update", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<com.example.motovista_deep.models.GenericResponse> call, Throwable t) {
                Toast.makeText(RegistrationRecordActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
