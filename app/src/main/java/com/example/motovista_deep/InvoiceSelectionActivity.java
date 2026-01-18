package com.example.motovista_deep;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.motovista_deep.api.ApiService;
import com.example.motovista_deep.api.RetrofitClient;
import com.example.motovista_deep.helpers.SharedPrefManager;
import com.example.motovista_deep.models.BikeModel;
import com.example.motovista_deep.models.CustomFitting;
import com.example.motovista_deep.models.CustomerRequest;
import com.example.motovista_deep.models.RequestResponse;
import com.example.motovista_deep.models.User;
import com.example.motovista_deep.utils.ImageUtils;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InvoiceSelectionActivity extends AppCompatActivity {

    private static final String TAG = "InvoiceSelectionActivity";

    private BikeModel bike;
    private String selectedColor;
    private double currentTotalPrice = 0;
    private List<CustomFitting> selectedAdditionalFittings = new ArrayList<>();
    private List<CheckBox> additionalFittingCheckboxes = new ArrayList<>();
    private CheckBox cbSelectAllAdditional;

    private ImageView ivBike, btnBack;
    private TextView tvBikeName, tvVariantColor, tvBasePrice, tvTotalPrice;
    private TextView tvWarranty, tvFreeServices, tvLegalNotes;
    private LinearLayout llMandatoryFittings, llAdditionalFittings;
    private MaterialButton btnConfirmOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_selection);

        bike = getIntent().getParcelableExtra("BIKE_DATA");
        selectedColor = getIntent().getStringExtra("SELECTED_COLOR");

        if (bike == null) {
            Toast.makeText(this, "Error loading data", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViews();
        setupData();
        calculateTotal();
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        ivBike = findViewById(R.id.ivBike);
        tvBikeName = findViewById(R.id.tvBikeName);
        tvVariantColor = findViewById(R.id.tvVariantColor);
        tvBasePrice = findViewById(R.id.tvBasePrice);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        tvWarranty = findViewById(R.id.tvWarranty);
        tvFreeServices = findViewById(R.id.tvFreeServices);
        tvLegalNotes = findViewById(R.id.tvLegalNotes);
        llMandatoryFittings = findViewById(R.id.llMandatoryFittings);
        llAdditionalFittings = findViewById(R.id.llAdditionalFittings);
        btnConfirmOrder = findViewById(R.id.btnConfirmOrder);
        cbSelectAllAdditional = findViewById(R.id.cbSelectAllAdditional);

        btnBack.setOnClickListener(v -> finish());
        btnConfirmOrder.setOnClickListener(v -> confirmOrder());

        cbSelectAllAdditional.setOnClickListener(v -> {
            boolean isChecked = cbSelectAllAdditional.isChecked();
            toggleAllAdditionalFittings(isChecked);
        });
    }

    private void setupData() {
        tvBikeName.setText(bike.getBrand() + " " + bike.getModel());
        tvVariantColor.setText(bike.getVariant() + " | " + selectedColor);
        
        String priceStr = bike.getOnRoadPrice();
        if (priceStr == null || priceStr.isEmpty()) priceStr = bike.getPrice();
        tvBasePrice.setText("₹ " + priceStr);
        
        try {
            currentTotalPrice = Double.parseDouble(priceStr.replaceAll("[^0-9.]", ""));
        } catch (Exception e) {
            currentTotalPrice = 0;
        }

        tvWarranty.setText(bike.getWarrantyPeriod() != null ? bike.getWarrantyPeriod() : "-");
        tvFreeServices.setText(bike.getFreeServicesCount() != null ? bike.getFreeServicesCount() : "-");
        
        StringBuilder legal = new StringBuilder();
        if (bike.getPriceDisclaimer() != null) legal.append(bike.getPriceDisclaimer());
        if (bike.getRegistrationProof() != null) {
            if (legal.length() > 0) legal.append("\n\n");
            legal.append("Proof Required: ").append(bike.getRegistrationProof());
        }
        if (legal.length() > 0) tvLegalNotes.setText(legal.toString());

        // Image
        String imageUrl = bike.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(ImageUtils.getFullImageUrl(imageUrl))
                    .placeholder(R.drawable.placeholder_bike)
                    .into(ivBike);
        }

        // Mandatory Fittings
        if (bike.getMandatoryFittings() != null) {
            for (CustomFitting fitting : bike.getMandatoryFittings()) {
                addFittingView(llMandatoryFittings, fitting, false);
            }
        }

        // Additional Fittings
        if (bike.getAdditionalFittings() != null) {
            for (CustomFitting fitting : bike.getAdditionalFittings()) {
                addFittingView(llAdditionalFittings, fitting, true);
            }
        }
    }

    private void addFittingView(LinearLayout container, CustomFitting fitting, boolean isInteractive) {
        View row = LayoutInflater.from(this).inflate(R.layout.item_fitting_selection, container, false);
        CheckBox cb = row.findViewById(R.id.cbFitting);
        TextView tvPrice = row.findViewById(R.id.tvFittingPrice);

        cb.setText(fitting.getName());
        
        String price = fitting.getPrice();
        if (price == null || price.isEmpty() || price.equalsIgnoreCase("Included") || price.equalsIgnoreCase("FREE")) {
             tvPrice.setText("Included");
        } else {
             tvPrice.setText("+ ₹ " + price);
        }

        if (isInteractive) {
            additionalFittingCheckboxes.add(cb);
            // Clicking any individual item also toggles everything (bundle behavior)
            cb.setOnClickListener(v -> {
                boolean targetState = cb.isChecked();
                toggleAllAdditionalFittings(targetState);
            });
            // Also allow clicking the row to toggle
            row.setOnClickListener(v -> {
                boolean targetState = !cb.isChecked();
                toggleAllAdditionalFittings(targetState);
            });
        } else {
            cb.setChecked(true);
            cb.setEnabled(false);
            row.setAlpha(0.7f);
        }

        container.addView(row);
    }

    private void toggleAllAdditionalFittings(boolean isChecked) {
        cbSelectAllAdditional.setChecked(isChecked);
        selectedAdditionalFittings.clear();
        
        for (CheckBox cb : additionalFittingCheckboxes) {
            cb.setChecked(isChecked);
        }

        if (isChecked && bike.getAdditionalFittings() != null) {
            selectedAdditionalFittings.addAll(bike.getAdditionalFittings());
        }
        
        calculateTotal();
    }

    private void calculateTotal() {
        double total = currentTotalPrice;
        for (CustomFitting f : selectedAdditionalFittings) {
            try {
                total += Double.parseDouble(f.getPrice().replaceAll("[^0-9.]", ""));
            } catch (Exception e) {}
        }
        tvTotalPrice.setText(String.format("₹ %.2f", total));
    }

    private void confirmOrder() {
        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            Toast.makeText(this, "Please login to place an order", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, CustomerLoginActivity.class));
            return;
        }

        User user = SharedPrefManager.getInstance(this).getCustomer();
        if (user == null) return;

        btnConfirmOrder.setEnabled(false);
        btnConfirmOrder.setText("Processing...");

        // Prepare data
        List<CustomFitting> allFittings = new ArrayList<>();
        if (bike.getMandatoryFittings() != null) allFittings.addAll(bike.getMandatoryFittings());
        allFittings.addAll(selectedAdditionalFittings);

        String fittingsJson = new Gson().toJson(allFittings);

        CustomerRequest request = new CustomerRequest(
            user.getId(),
            user.getFull_name(),
            user.getPhone(),
            user.getProfile_image(),
            bike.getId(),
            bike.getBrand() + " " + bike.getModel(),
            bike.getVariant(),
            selectedColor,
            tvTotalPrice.getText().toString(),
            fittingsJson
        );

        ApiService apiService = RetrofitClient.getApiService();
        Call<RequestResponse> call = apiService.addCustomerRequest(request);

        call.enqueue(new Callback<RequestResponse>() {
            @Override
            public void onResponse(Call<RequestResponse> call, Response<RequestResponse> response) {
                btnConfirmOrder.setEnabled(true);
                btnConfirmOrder.setText("CONFIRM ORDER");

                if (response.isSuccessful() && response.body() != null) {
                    RequestResponse res = response.body();
                    if (res.isSuccess()) {
                        Intent intent = new Intent(InvoiceSelectionActivity.this, RequestSentActivity.class);
                        intent.putExtra("BIKE_NAME", bike.getBrand() + " " + bike.getModel());
                        intent.putExtra("BIKE_PRICE", tvTotalPrice.getText().toString());
                        intent.putExtra("BIKE_VARIANT", bike.getVariant());
                        intent.putExtra("BIKE_Color", selectedColor);
                        if (res.getOrderId() != null) intent.putExtra("ORDER_ID", res.getOrderId());
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(InvoiceSelectionActivity.this, res.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(InvoiceSelectionActivity.this, "Server error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RequestResponse> call, Throwable t) {
                btnConfirmOrder.setEnabled(true);
                btnConfirmOrder.setText("CONFIRM ORDER");
                Toast.makeText(InvoiceSelectionActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
