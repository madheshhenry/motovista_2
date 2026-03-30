package com.example.motovista_deep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.motovista_deep.adapter.InsuranceAdapter;
import com.example.motovista_deep.api.ApiService;
import com.example.motovista_deep.api.RetrofitClient;
import com.example.motovista_deep.models.InsuranceModel;
import com.example.motovista_deep.models.InsuranceResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InsuranceLedgerActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView tvTitle;
    private CardView chipAll, chipActive, chipExpiringSoon, chipExpired;
    private TextView tvTotalPolicies, tvActionNeeded;
    
    private RecyclerView rvInsurance;
    private InsuranceAdapter adapter;
    private List<InsuranceModel> fullList = new ArrayList<>();
    private List<InsuranceModel> filteredList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insurance_ledger);

        initializeViews();
        setupRecyclerView();
        setupClickListeners();
        
        fetchInsuranceData();
        setActiveFilter(chipAll);
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        tvTitle = findViewById(R.id.tvTitle);
        chipAll = findViewById(R.id.chipAll);
        chipActive = findViewById(R.id.chipActive);
        chipExpiringSoon = findViewById(R.id.chipExpiringSoon);
        chipExpired = findViewById(R.id.chipExpired);
        tvTotalPolicies = findViewById(R.id.tvTotalPolicies);
        tvActionNeeded = findViewById(R.id.tvActionNeeded);
        rvInsurance = findViewById(R.id.rvInsurance);
    }

    private void setupRecyclerView() {
        rvInsurance.setLayoutManager(new LinearLayoutManager(this));
        adapter = new InsuranceAdapter(filteredList, item -> {
            Intent intent = new Intent(InsuranceLedgerActivity.this, InsuranceDetailsActivity.class);
            intent.putExtra("customer_name", item.getCustomerName());
            intent.putExtra("order_id", item.getOrderId());
            intent.putExtra("policy_number", item.getPolicyNumber());
            intent.putExtra("status", item.getStatus());
            intent.putExtra("end_date", item.getFullInsuranceExpiry());
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
        rvInsurance.setAdapter(adapter);
    }

    private void fetchInsuranceData() {
        ApiService apiService = RetrofitClient.getApiService();
        apiService.getInsuranceLedger().enqueue(new Callback<InsuranceResponse>() {
            @Override
            public void onResponse(Call<InsuranceResponse> call, Response<InsuranceResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    fullList.clear();
                    fullList.addAll(response.body().getData());
                    tvTotalPolicies.setText(String.valueOf(fullList.size()));
                    
                    // Count action needed (Expired or Expiring Soon)
                    long actionNeeded = 0;
                    for (InsuranceModel m : fullList) {
                        if (!"Active".equalsIgnoreCase(m.getStatus())) actionNeeded++;
                    }
                    tvActionNeeded.setText(String.valueOf(actionNeeded));
                    
                    applyFilter("All");
                } else {
                    Toast.makeText(InsuranceLedgerActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<InsuranceResponse> call, Throwable t) {
                Toast.makeText(InsuranceLedgerActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void applyFilter(String filter) {
        filteredList.clear();
        if ("All".equals(filter)) {
            filteredList.addAll(fullList);
        } else {
            for (InsuranceModel item : fullList) {
                if (filter.equalsIgnoreCase(item.getStatus())) {
                    filteredList.add(item);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> onBackPressed());

        chipAll.setOnClickListener(v -> {
            setActiveFilter(chipAll);
            applyFilter("All");
        });

        chipActive.setOnClickListener(v -> {
            setActiveFilter(chipActive);
            applyFilter("Active");
        });

        chipExpiringSoon.setOnClickListener(v -> {
            setActiveFilter(chipExpiringSoon);
            applyFilter("Expiring Soon");
        });

        chipExpired.setOnClickListener(v -> {
            setActiveFilter(chipExpired);
            applyFilter("Expired");
        });
    }

    private void setActiveFilter(CardView activeChip) {
        chipAll.setCardBackgroundColor(ContextCompat.getColor(this, R.color.gray_200));
        chipActive.setCardBackgroundColor(ContextCompat.getColor(this, R.color.gray_200));
        chipExpiringSoon.setCardBackgroundColor(ContextCompat.getColor(this, R.color.gray_200));
        chipExpired.setCardBackgroundColor(ContextCompat.getColor(this, R.color.gray_200));

        chipAll.setCardElevation(0);
        chipActive.setCardElevation(0);
        chipExpiringSoon.setCardElevation(0);
        chipExpired.setCardElevation(0);

        activeChip.setCardBackgroundColor(ContextCompat.getColor(this, R.color.primary_color));
        activeChip.setCardElevation(2);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}