package com.example.motovista_deep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.motovista_deep.adapters.ApplicationsAdapter;
import com.example.motovista_deep.api.ApiService;
import com.example.motovista_deep.api.RetrofitClient;
import com.example.motovista_deep.models.CustomerRequest;
import com.example.motovista_deep.models.GetCustomerRequestsResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminRequestedCustomersActivity extends AppCompatActivity {

    // Views
    private ImageView btnBack;
    private EditText etSearch;
    private RecyclerView rvRequests;
    private TextView tvEmpty;
    private ProgressBar progressBar;

    // Filter Views
    private TextView tvFilterAll, tvFilterPending, tvFilterApproved, tvFilterRejected;
    private LinearLayout layoutFilterNew;
    private String currentStatusFilter = "all";

    // Data
    private List<CustomerRequest> allRequests = new ArrayList<>();
    private ApplicationsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_requested_customers);

        initializeViews();
        setupRecyclerView();
        setupClickListeners();
        setupSearchAndFilter();
        
        // Initial Fetch
        fetchCustomerRequests();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Refresh when coming back from details
        fetchCustomerRequests();
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        etSearch = findViewById(R.id.etSearch);
        rvRequests = findViewById(R.id.rvRequests);
        tvEmpty = findViewById(R.id.tvEmpty);
        progressBar = findViewById(R.id.progressBar);

        tvFilterAll = findViewById(R.id.tvFilterAll);
        layoutFilterNew = findViewById(R.id.layoutFilterNew);
        tvFilterPending = findViewById(R.id.tvFilterPending);
        tvFilterApproved = findViewById(R.id.tvFilterApproved);
        tvFilterRejected = findViewById(R.id.tvFilterRejected);
    }

    private void setupRecyclerView() {
        rvRequests.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ApplicationsAdapter(this, new ArrayList<>(), new ApplicationsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(CustomerRequest request) {
                 navigateToDetails(request);
            }
        });
        rvRequests.setAdapter(adapter);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
    }
    
    private void setupSearchAndFilter() {
        // Search
        etSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterList(s.toString());
            }
            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });
        
        // Filter Click Listener
        View.OnClickListener filterListener = v -> {
            // Identify clicked view type for UI update
            View clickedView = v;
            if(v.getId() == R.id.layoutFilterNew) {
                // Handle the LinearLayout click
                 updateFilterUI(layoutFilterNew); 
                 currentStatusFilter = "pending";
            } else {
                 updateFilterUI(v);
                 int id = v.getId();
                 if (id == R.id.tvFilterAll) currentStatusFilter = "all";
                 else if (id == R.id.tvFilterApproved) currentStatusFilter = "approved";
                 else if (id == R.id.tvFilterRejected) currentStatusFilter = "rejected";
            }
            
            filterList(etSearch.getText().toString());
        };
        
        tvFilterAll.setOnClickListener(filterListener);
        layoutFilterNew.setOnClickListener(filterListener);
        tvFilterApproved.setOnClickListener(filterListener);
        tvFilterRejected.setOnClickListener(filterListener);
        
        // Set Default Selection
        updateFilterUI(tvFilterAll);
    }
    
    private void updateFilterUI(View selected) {
        // Reset All
        resetPill(tvFilterAll);
        resetPill(layoutFilterNew);
        resetPill(tvFilterApproved);
        resetPill(tvFilterRejected);
        
        // Activate Selected
        if (selected instanceof LinearLayout) {
             // For the "New" layout
             selected.setBackgroundResource(R.drawable.pill_black);
             tvFilterPending.setTextColor(android.graphics.Color.WHITE);
        } else if (selected instanceof TextView) {
             selected.setBackgroundResource(R.drawable.pill_black);
             ((TextView) selected).setTextColor(android.graphics.Color.WHITE);
        }
    }
    
    private void resetPill(View v) {
        v.setBackgroundResource(R.drawable.pill_gray);
        if (v instanceof LinearLayout) {
            tvFilterPending.setTextColor(0xFF111718);
        } else if (v instanceof TextView) {
            ((TextView) v).setTextColor(0xFF111718);
        }
    }

    private void filterList(String query) {
        if (allRequests == null) return;
        
        List<CustomerRequest> filtered = new ArrayList<>();
        String lowerQuery = query.toLowerCase().trim();
        
        for (CustomerRequest req : allRequests) {
            // Search Match
            boolean matchesSearch = req.getCustomer_name().toLowerCase().contains(lowerQuery) ||
                                    (req.getBike_name() != null && req.getBike_name().toLowerCase().contains(lowerQuery));
            
            // Status Match
            boolean matchesFilter = false;
            String status = req.getStatus() != null ? req.getStatus().toLowerCase() : "pending";
            
            if (currentStatusFilter.equals("all")) {
                matchesFilter = true;
            } else if (currentStatusFilter.equals("approved")) {
                matchesFilter = status.equals("approved") || status.equals("accepted") || status.equals("completed");
            } else {
                matchesFilter = status.equals(currentStatusFilter);
            }
            
            if (matchesSearch && matchesFilter) {
                filtered.add(req);
            }
        }
        
        adapter.updateList(filtered);
        
        // Empty State
        if (filtered.isEmpty()) {
            rvRequests.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            rvRequests.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
        }
    }

    private void fetchCustomerRequests() {
        progressBar.setVisibility(View.VISIBLE);
        ApiService apiService = RetrofitClient.getApiService();
        apiService.getCustomerRequests().enqueue(new Callback<GetCustomerRequestsResponse>() {
            @Override
            public void onResponse(Call<GetCustomerRequestsResponse> call, Response<GetCustomerRequestsResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    allRequests = response.body().getData();
                    filterList(etSearch.getText().toString()); // Refresh Adapter
                } else {
                    Toast.makeText(AdminRequestedCustomersActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GetCustomerRequestsResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AdminRequestedCustomersActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToDetails(CustomerRequest req) {
        Intent intent = new Intent(this, OrderSummaryActivity.class);
        intent.putExtra("request_id", req.getId());
        intent.putExtra("customer_name", req.getCustomer_name());
        intent.putExtra("customer_phone", req.getCustomer_phone());
        intent.putExtra("status", req.getStatus());
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}