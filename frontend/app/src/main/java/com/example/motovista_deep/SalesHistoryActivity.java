package com.example.motovista_deep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.motovista_deep.adapters.SalesHistoryAdapter;
import com.example.motovista_deep.api.ApiService;
import com.example.motovista_deep.api.RetrofitClient;
import com.example.motovista_deep.models.SalesHistoryItem;
import com.example.motovista_deep.models.SalesHistoryResponse;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SalesHistoryActivity extends AppCompatActivity {

    private ImageView btnBack;
    private Button btnFilterAll, btnFilterCash, btnFilterEMI, btnFilterThisMonth;
    private RecyclerView recyclerSalesHistory;
    private TextView tvEmptyState;
    private SalesHistoryAdapter adapter;
    private List<SalesHistoryItem> allSales = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_history);

        // Initialize views
        initializeViews();

        // Setup click listeners
        setupClickListeners();

        // Fetch Data
        fetchSalesHistory();

        // Apply System UI Insets for Notch/Status Bar
        View headerView = findViewById(R.id.header);
        if (headerView != null) {
            com.example.motovista_deep.utils.SystemUIHelper.setupEdgeToEdgeWithScroll(
                this,
                findViewById(R.id.rootLayout),
                headerView,
                recyclerSalesHistory,
                null
            );
        }
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        btnFilterAll = findViewById(R.id.btnFilterAll);
        btnFilterCash = findViewById(R.id.btnFilterCash);
        btnFilterEMI = findViewById(R.id.btnFilterEMI);
        btnFilterThisMonth = findViewById(R.id.btnFilterThisMonth);
        recyclerSalesHistory = findViewById(R.id.recyclerSalesHistory);
        tvEmptyState = findViewById(R.id.tvEmptyState);

        recyclerSalesHistory.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SalesHistoryAdapter(new ArrayList<>(), item -> showSaleDetails(item));
        recyclerSalesHistory.setAdapter(adapter);
    }

    private void fetchSalesHistory() {
        ApiService apiService = RetrofitClient.getApiService();
        apiService.getSalesHistory().enqueue(new Callback<SalesHistoryResponse>() {
            @Override
            public void onResponse(Call<SalesHistoryResponse> call, Response<SalesHistoryResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    allSales = response.body().getData();
                    updateDisplayList(allSales);
                    setActiveFilter(btnFilterAll);
                } else {
                    tvEmptyState.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<SalesHistoryResponse> call, Throwable t) {
                Toast.makeText(SalesHistoryActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                tvEmptyState.setVisibility(View.VISIBLE);
            }
        });
    }

    private void updateDisplayList(List<SalesHistoryItem> list) {
        if (list == null || list.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            recyclerSalesHistory.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            recyclerSalesHistory.setVisibility(View.VISIBLE);
            adapter.updateList(list);
        }
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> onBackPressed());

        btnFilterAll.setOnClickListener(v -> {
            setActiveFilter(btnFilterAll);
            updateDisplayList(allSales);
        });

        btnFilterCash.setOnClickListener(v -> {
            setActiveFilter(btnFilterCash);
            List<SalesHistoryItem> filtered = new ArrayList<>();
            for (SalesHistoryItem s : allSales) {
                if ("Cash".equalsIgnoreCase(s.getPaymentType())) filtered.add(s);
            }
            updateDisplayList(filtered);
        });

        btnFilterEMI.setOnClickListener(v -> {
            setActiveFilter(btnFilterEMI);
            List<SalesHistoryItem> filtered = new ArrayList<>();
            for (SalesHistoryItem s : allSales) {
                if ("EMI".equalsIgnoreCase(s.getPaymentType())) filtered.add(s);
            }
            updateDisplayList(filtered);
        });

        btnFilterThisMonth.setOnClickListener(v -> {
            setActiveFilter(btnFilterThisMonth);
            List<SalesHistoryItem> filtered = new ArrayList<>();
            Calendar now = Calendar.getInstance();
            int currentMonth = now.get(Calendar.MONTH);
            int currentYear = now.get(Calendar.YEAR);

            // Note: In real app, you'd parse item.getSaleDate() to compare
            // For now, simplify or filter based on a flag from backend
            updateDisplayList(allSales); // Placeholder for complex date filter
        });
    }

    private void setActiveFilter(Button activeButton) {
        resetFilterButtons();
        activeButton.setTextColor(ContextCompat.getColor(this, R.color.white));
        activeButton.setBackground(ContextCompat.getDrawable(this, R.drawable.primary_button_rounded));
    }

    private void resetFilterButtons() {
        Button[] buttons = {btnFilterAll, btnFilterCash, btnFilterEMI, btnFilterThisMonth};
        for (Button b : buttons) {
            b.setTextColor(ContextCompat.getColor(this, R.color.text_primary_light));
            b.setBackground(ContextCompat.getDrawable(this, R.drawable.gray_button_rounded));
        }
    }

    private void showSaleDetails(SalesHistoryItem item) {
        String cleanPrice = item.getTotalValue();
        if (cleanPrice != null) {
            cleanPrice = cleanPrice.replaceAll("[^0-9.]", "").trim();
        } else {
            cleanPrice = "0.00";
        }

        Intent intent = new Intent(this, SaleDetailsActivity.class);
        intent.putExtra("BIKE_NAME", item.getBrand() + " " + item.getModel());
        intent.putExtra("BIKE_COLOR", item.getBikeColorName());
        intent.putExtra("BIKE_COLOR_HEX", item.getBikeColorHex());
        intent.putExtra("BIKE_IMAGE", item.getBikeImage());
        intent.putExtra("ENGINE_NUMBER", item.getEngineNumber());
        intent.putExtra("CHASSIS_NUMBER", item.getChassisNumber());
        intent.putExtra("CUSTOMER_NAME", item.getCustomerName());
        intent.putExtra("CUSTOMER_PHONE", item.getCustomerPhone());
        intent.putExtra("CUSTOMER_ADDRESS", item.getCustomerAddress());
        intent.putExtra("SALE_DATE", item.getFormattedDate());
        intent.putExtra("PAYMENT_TYPE", item.getPaymentType());
        intent.putExtra("TOTAL_VALUE", "₹" + cleanPrice);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}