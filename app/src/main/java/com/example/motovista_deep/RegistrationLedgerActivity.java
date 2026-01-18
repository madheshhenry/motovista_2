package com.example.motovista_deep;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.motovista_deep.adapter.RegistrationLedgerAdapter;
import com.example.motovista_deep.models.RegistrationLedgerItem;
import com.example.motovista_deep.api.ApiService;
import com.example.motovista_deep.api.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

public class RegistrationLedgerActivity extends AppCompatActivity {

    private RecyclerView rvLedger;
    private RegistrationLedgerAdapter adapter;
    private List<RegistrationLedgerItem> ledgerItems;
    private ImageView btnBack;
    private android.widget.EditText etSearch;
    private android.widget.TextView filterAll, filterPending, filterCompleted, filterVerification;
    private String currentStatusFilter = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_ledger);

        rvLedger = findViewById(R.id.rvLedger);
        btnBack = findViewById(R.id.btnBack);
        etSearch = findViewById(R.id.etSearch);
        filterAll = findViewById(R.id.filterAll);
        filterPending = findViewById(R.id.filterPending);
        filterCompleted = findViewById(R.id.filterCompleted);
        filterVerification = findViewById(R.id.filterVerification);
        
        rvLedger.setLayoutManager(new LinearLayoutManager(this));
        ledgerItems = new ArrayList<>();
        adapter = new RegistrationLedgerAdapter(this, ledgerItems);
        rvLedger.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());

        setupSearchAndFilters();
        fetchLedgerData();
    }

    private void setupSearchAndFilters() {
        etSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(android.text.Editable s) {
                adapter.filter(s.toString(), currentStatusFilter);
            }
        });

        View.OnClickListener filterClick = v -> {
            updateFilterUI(v.getId());
            adapter.filter(etSearch.getText().toString(), currentStatusFilter);
        };

        filterAll.setOnClickListener(filterClick);
        filterPending.setOnClickListener(filterClick);
        filterCompleted.setOnClickListener(filterClick);
        filterVerification.setOnClickListener(filterClick);
    }

    private void updateFilterUI(int selectedId) {
        resetFilterChip(filterAll);
        resetFilterChip(filterPending);
        resetFilterChip(filterCompleted);
        resetFilterChip(filterVerification);

        android.widget.TextView selected = findViewById(selectedId);
        selected.setBackgroundResource(R.drawable.pill_blue);
        selected.setTextColor(android.graphics.Color.WHITE);
        currentStatusFilter = selected.getText().toString();
    }

    private void resetFilterChip(android.widget.TextView textView) {
        textView.setBackgroundResource(R.drawable.pill_inactive);
        textView.setTextColor(android.graphics.Color.parseColor("#4b5563"));
    }

    private void fetchLedgerData() {
        ApiService apiService = RetrofitClient.getApiService();
        retrofit2.Call<com.example.motovista_deep.models.GetRegistrationLedgerResponse> call = apiService.getRegistrationLedger();

        call.enqueue(new retrofit2.Callback<com.example.motovista_deep.models.GetRegistrationLedgerResponse>() {
            @Override
            public void onResponse(retrofit2.Call<com.example.motovista_deep.models.GetRegistrationLedgerResponse> call, retrofit2.Response<com.example.motovista_deep.models.GetRegistrationLedgerResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        if (response.body().getData() != null && !response.body().getData().isEmpty()) {
                            ledgerItems.clear();
                            ledgerItems.addAll(response.body().getData());
                            adapter.updateList(ledgerItems);
                        } else {
                            Toast.makeText(RegistrationLedgerActivity.this, "No records found", Toast.LENGTH_SHORT).show();
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        String msg = response.body().getMessage() != null ? response.body().getMessage() : "No records found";
                        Toast.makeText(RegistrationLedgerActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RegistrationLedgerActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.example.motovista_deep.models.GetRegistrationLedgerResponse> call, Throwable t) {
                Toast.makeText(RegistrationLedgerActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Refresh when coming back from detail view
        fetchLedgerData(); 
    }
}
