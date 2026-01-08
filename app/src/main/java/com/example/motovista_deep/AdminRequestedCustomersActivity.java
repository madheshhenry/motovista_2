package com.example.motovista_deep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import com.example.motovista_deep.api.ApiService;
import com.example.motovista_deep.api.RetrofitClient;
import com.example.motovista_deep.models.CustomerRequest;
import com.example.motovista_deep.models.GetCustomerRequestsResponse;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminRequestedCustomersActivity extends AppCompatActivity {

    private ImageView btnBack;
    private EditText etSearch;
    
    // Filter Views
    private TextView tvFilterAll, tvFilterPending, tvFilterApproved, tvFilterRejected;
    private String currentStatusFilter = "all";
    
    // Data list
    private List<CustomerRequest> allRequests = null;
    private List<CustomerRequest> filteredRequests = null;

    // Customer 1
    private CardView cardCustomer1;
    private TextView tvCustomerName1, tvCustomerPhone1, tvTime1;
    private TextView tvBikeName1, tvBikeColor1; // New
    private LinearLayout layoutBikeColor1; // New
    private Button btnReject1, btnAccept1;
    private LinearLayout statusContainer1, actionContainer1;
    private ImageView ivTick1, ivCross1, ivArrow1;

    // Customer 2
    private CardView cardCustomer2;
    private TextView tvCustomerName2, tvCustomerPhone2, tvTime2;
    private TextView tvBikeName2, tvBikeColor2;
    private LinearLayout layoutBikeColor2;
    private Button btnReject2, btnAccept2;
    private LinearLayout statusContainer2, actionContainer2;
    private ImageView ivTick2, ivCross2, ivArrow2;

    // Customer 3
    private CardView cardCustomer3;
    private TextView tvCustomerName3, tvCustomerPhone3, tvTime3;
    private TextView tvBikeName3, tvBikeColor3;
    private LinearLayout layoutBikeColor3;
    private Button btnReject3, btnAccept3;
    private LinearLayout statusContainer3, actionContainer3;
    private ImageView ivTick3, ivCross3, ivArrow3;

    // Customer 4
    private CardView cardCustomer4;
    private TextView tvCustomerName4, tvCustomerPhone4, tvTime4;
    private TextView tvBikeName4, tvBikeColor4;
    private LinearLayout layoutBikeColor4;
    private Button btnReject4, btnAccept4;
    private LinearLayout statusContainer4, actionContainer4;
    private ImageView ivTick4, ivCross4, ivArrow4;

    // Customer 5
    private CardView cardCustomer5;
    private TextView tvCustomerName5, tvCustomerPhone5, tvTime5;
    private TextView tvBikeName5, tvBikeColor5;
    private LinearLayout layoutBikeColor5;
    private Button btnReject5, btnAccept5;
    private LinearLayout statusContainer5, actionContainer5;
    private ImageView ivTick5, ivCross5, ivArrow5;

    // Track customer status
    private HashMap<Integer, String> customerStatusMap = new HashMap<>();
    private HashMap<Integer, String> customerNameMap = new HashMap<>();
    private HashMap<Integer, String> customerPhoneMap = new HashMap<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_requested_customers);

        // Initialize all views
        initializeViews();

        // Setup click listeners
        setupClickListeners();
        
        // Setup Search and Filter
        setupSearchAndFilter();

        // Fetch data from API
        fetchCustomerRequests();
    }
    
    private void setupSearchAndFilter() {
        // Search Listener
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
        
        // Filter Listeners
        View.OnClickListener filterListener = v -> {
            // Update UI
            updateFilterUI((TextView) v);
            
            // Set Filter
            if (v.getId() == R.id.tvFilterAll) currentStatusFilter = "all";
            else if (v.getId() == R.id.tvFilterPending) currentStatusFilter = "pending";
            else if (v.getId() == R.id.tvFilterApproved) currentStatusFilter = "approved";
            else if (v.getId() == R.id.tvFilterRejected) currentStatusFilter = "rejected";
            
            // Apply Filter
            filterList(etSearch.getText().toString());
        };
        
        tvFilterAll.setOnClickListener(filterListener);
        tvFilterPending.setOnClickListener(filterListener);
        tvFilterApproved.setOnClickListener(filterListener);
        tvFilterRejected.setOnClickListener(filterListener);
    }
    
    private void updateFilterUI(TextView selected) {
        TextView[] filters = {tvFilterAll, tvFilterPending, tvFilterApproved, tvFilterRejected};
        for (TextView tv : filters) {
            if (tv == selected) {
                tv.setBackgroundResource(R.drawable.pill_black);
                tv.setTextColor(android.graphics.Color.WHITE);
            } else {
                tv.setBackgroundResource(R.drawable.pill_gray);
                tv.setTextColor(0xFF111418); // Default Text Color
            }
        }
    }
    
    private void filterList(String query) {
        if (allRequests == null) return;
        
        filteredRequests = new java.util.ArrayList<>();
        String lowerQuery = query.toLowerCase().trim();
        
        for (CustomerRequest req : allRequests) {
            boolean matchesSearch = req.getCustomer_name().toLowerCase().contains(lowerQuery) ||
                                    (req.getBike_name() != null && req.getBike_name().toLowerCase().contains(lowerQuery));
            
            boolean matchesFilter = currentStatusFilter.equals("all") || 
                                    (req.getStatus() != null && req.getStatus().toLowerCase().equals(currentStatusFilter));
            
            if (matchesSearch && matchesFilter) {
                filteredRequests.add(req);
            }
        }
        
        populateRequests(filteredRequests);
    }
    
    private void fetchCustomerRequests() {
        ApiService apiService = RetrofitClient.getApiService();
        Call<GetCustomerRequestsResponse> call = apiService.getCustomerRequests();
        
        call.enqueue(new Callback<GetCustomerRequestsResponse>() {
            @Override
            public void onResponse(Call<GetCustomerRequestsResponse> call, Response<GetCustomerRequestsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GetCustomerRequestsResponse res = response.body();
                    if (res.isSuccess() && res.getData() != null) {
                        allRequests = res.getData();
                        filterList(""); // Populate initial list
                    } else {
                        Toast.makeText(AdminRequestedCustomersActivity.this, "No requests found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AdminRequestedCustomersActivity.this, "Failed to load requests", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GetCustomerRequestsResponse> call, Throwable t) {
                Toast.makeText(AdminRequestedCustomersActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void updateRequestStatus(int requestId, String status) {
        ApiService apiService = RetrofitClient.getApiService();
        Call<com.example.motovista_deep.models.GenericResponse> call = apiService.updateRequestStatus(
            new com.example.motovista_deep.models.UpdateRequestStatusRequest(requestId, status)
        );
        
        call.enqueue(new Callback<com.example.motovista_deep.models.GenericResponse>() {
            @Override
            public void onResponse(Call<com.example.motovista_deep.models.GenericResponse> call, Response<com.example.motovista_deep.models.GenericResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminRequestedCustomersActivity.this, "Status updated", Toast.LENGTH_SHORT).show();
                    
                    // Update Local Data (Optimistic)
                    if (allRequests != null) {
                        for (CustomerRequest req : allRequests) {
                            if (req.getId() == requestId) {
                                req.setStatus(status);
                                break;
                            }
                        }
                    }
                    
                    // Re-apply filter to update UI immediately
                    filterList(etSearch.getText().toString());
                    
                    // Optional: fetch fresh data silently
                    // fetchCustomerRequests(); 
                } else {
                    Toast.makeText(AdminRequestedCustomersActivity.this, "Update failed", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<com.example.motovista_deep.models.GenericResponse> call, Throwable t) {
                Toast.makeText(AdminRequestedCustomersActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void populateRequests(List<CustomerRequest> requests) {
        CardView[] cards = {cardCustomer1, cardCustomer2, cardCustomer3, cardCustomer4, cardCustomer5};
        TextView[] names = {tvCustomerName1, tvCustomerName2, tvCustomerName3, tvCustomerName4, tvCustomerName5};
        TextView[] phones = {tvCustomerPhone1, tvCustomerPhone2, tvCustomerPhone3, tvCustomerPhone4, tvCustomerPhone5};
        TextView[] times = {tvTime1, tvTime2, tvTime3, tvTime4, tvTime5};
        TextView[] bikeNames = {tvBikeName1, tvBikeName2, tvBikeName3, tvBikeName4, tvBikeName5};
        TextView[] bikeColors = {tvBikeColor1, tvBikeColor2, tvBikeColor3, tvBikeColor4, tvBikeColor5};
        
        Button[] rejectBtns = {btnReject1, btnReject2, btnReject3, btnReject4, btnReject5};
        Button[] acceptBtns = {btnAccept1, btnAccept2, btnAccept3, btnAccept4, btnAccept5};
        ImageView[] ivArrows = {ivArrow1, ivArrow2, ivArrow3, ivArrow4, ivArrow5};
        
        // Loop through data and fill cards
        for (int i = 0; i < 5; i++) {
            if (i < requests.size()) {
                CustomerRequest req = requests.get(i);
                final int requestId = req.getId(); 
                final String customerName = req.getCustomer_name();
                String status = req.getStatus() != null ? req.getStatus().toLowerCase() : "pending";

                cards[i].setVisibility(View.VISIBLE);
                names[i].setText(req.getCustomer_name());
                
                // Bike Details
                if (bikeNames[i] != null) bikeNames[i].setText(req.getBike_name());
                
                // Parse Color "Name|Hex"
                String colorStr = req.getBike_color();
                if (bikeColors[i] != null && colorStr != null) {
                    try {
                        String[] parts = colorStr.split("\\|");
                        if (parts.length >= 2) {
                             bikeColors[i].setText(parts[0]);
                             
                             // Parse Color
                             int color = android.graphics.Color.parseColor(parts[1]);
                             
                             // Find Views
                             LinearLayout layout = null;
                             switch(i) {
                                 case 0: layout = layoutBikeColor1; break;
                                 case 1: layout = layoutBikeColor2; break;
                                 case 2: layout = layoutBikeColor3; break;
                                 case 3: layout = layoutBikeColor4; break;
                                 case 4: layout = layoutBikeColor5; break;
                             }
                             
                             if (layout != null) {
                                 // Create dynamic background (Light Tint + Stroke)
                                 android.graphics.drawable.GradientDrawable bg = new android.graphics.drawable.GradientDrawable();
                                 bg.setShape(android.graphics.drawable.GradientDrawable.RECTANGLE);
                                 bg.setCornerRadius(100f); // Pill shape
                                 bg.setStroke(3, color); // 1dp stroke with actual color (3px approx)
                                 
                                 // Light tint for background (alpha 0x20 = 32/255 approx 12%)
                                 int lightColor = androidx.core.graphics.ColorUtils.setAlphaComponent(color, 40);
                                 bg.setColor(lightColor);
                                 
                                 layout.setBackground(bg);
                                 
                                 // Update Text Color
                                 bikeColors[i].setTextColor(color);
                                 
                                 // Update Dot Tint
                                 if (layout.getChildCount() > 0) {
                                     View dot = layout.getChildAt(0);
                                     dot.setBackgroundTintList(android.content.res.ColorStateList.valueOf(color));
                                 }
                             }
                        } else {
                            bikeColors[i].setText(colorStr);
                        }
                    } catch (Exception e) {
                        bikeColors[i].setText(colorStr);
                        e.printStackTrace();
                    }
                }
                
                // Phone
                phones[i].setText(req.getCustomer_phone());
                phones[i].setVisibility(View.VISIBLE);
                
                times[i].setText("Order #" + requestId); 
                
                updateCardUI(i + 1, status); 
                
                // Action Buttons
                acceptBtns[i].setOnClickListener(v -> updateRequestStatus(requestId, "approved"));
                rejectBtns[i].setOnClickListener(v -> updateRequestStatus(requestId, "rejected"));
                
                // Navigate to Order Summary (Clicking Card)
                cards[i].setOnClickListener(v -> {
                     Intent intent = new Intent(AdminRequestedCustomersActivity.this, OrderSummaryActivity.class);
                     intent.putExtra("request_id", requestId);
                     intent.putExtra("customer_name", req.getCustomer_name());
                     intent.putExtra("customer_phone", req.getCustomer_phone());
                     intent.putExtra("status", req.getStatus()); // Pass actual status
                     startActivity(intent);
                     overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                });
                
                // Delete (Long Press)
                cards[i].setOnLongClickListener(v -> {
                    new androidx.appcompat.app.AlertDialog.Builder(AdminRequestedCustomersActivity.this)
                        .setTitle("Delete Request")
                        .setMessage("Are you sure you want to delete order #" + requestId + " for " + customerName + "?")
                        .setPositiveButton("Delete", (dialog, which) -> deleteRequest(requestId))
                        .setNegativeButton("Cancel", null)
                        .show();
                    return true;
                });

            } else {
                 cards[i].setVisibility(View.GONE);
            }
        }
    }
    
    private void deleteRequest(int requestId) {
         ApiService apiService = RetrofitClient.getApiService();
         Call<com.example.motovista_deep.models.GenericResponse> call = apiService.deleteCustomerRequest(
             new com.example.motovista_deep.models.DeleteRequestRequest(requestId)
         );
         
         call.enqueue(new Callback<com.example.motovista_deep.models.GenericResponse>() {
             @Override
             public void onResponse(Call<com.example.motovista_deep.models.GenericResponse> call, Response<com.example.motovista_deep.models.GenericResponse> response) {
                 if (response.isSuccessful()) {
                      Toast.makeText(AdminRequestedCustomersActivity.this, "Request Deleted", Toast.LENGTH_SHORT).show();
                      // Optimistic Update
                      if (allRequests != null) {
                        for (int i = 0; i < allRequests.size(); i++) {
                            if (allRequests.get(i).getId() == requestId) {
                                allRequests.remove(i);
                                break;
                            }
                        }
                      }
                      filterList(etSearch.getText().toString());
                 } else {
                      Toast.makeText(AdminRequestedCustomersActivity.this, "Delete Failed", Toast.LENGTH_SHORT).show();
                 }
             }

             @Override
             public void onFailure(Call<com.example.motovista_deep.models.GenericResponse> call, Throwable t) {
                  Toast.makeText(AdminRequestedCustomersActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
             }
         });
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        fetchCustomerRequests();
    }

    // updateCardUI helper
    private void updateCardUI(int customerIndex, String status) {
        // Reuse existing logic but ensuring we have access to variables
        // This relies on the fields being class-level members which they are.
        
        LinearLayout statusContainer = null;
        LinearLayout actionContainer = null;
        ImageView ivTick = null;
        ImageView ivCross = null;
        
        switch (customerIndex) {
            case 1: statusContainer = statusContainer1; actionContainer = actionContainer1; ivTick = ivTick1; ivCross = ivCross1; break;
            case 2: statusContainer = statusContainer2; actionContainer = actionContainer2; ivTick = ivTick2; ivCross = ivCross2; break;
            case 3: statusContainer = statusContainer3; actionContainer = actionContainer3; ivTick = ivTick3; ivCross = ivCross3; break;
            case 4: statusContainer = statusContainer4; actionContainer = actionContainer4; ivTick = ivTick4; ivCross = ivCross4; break;
            case 5: statusContainer = statusContainer5; actionContainer = actionContainer5; ivTick = ivTick5; ivCross = ivCross5; break;
        }
        
        if (statusContainer == null) return;
        
        String lowerStatus = status.toLowerCase();
        
        if ("approved".equals(lowerStatus) || "completed".equals(lowerStatus) || "accepted".equals(lowerStatus)) {
            actionContainer.setVisibility(View.GONE);
            statusContainer.setVisibility(View.VISIBLE);
            ivTick.setVisibility(View.VISIBLE);
            ivCross.setVisibility(View.GONE);
        } else if ("rejected".equals(lowerStatus)) {
            actionContainer.setVisibility(View.GONE);
            statusContainer.setVisibility(View.VISIBLE);
            ivTick.setVisibility(View.GONE);
            ivCross.setVisibility(View.VISIBLE);
        } else {
             // Pending
            actionContainer.setVisibility(View.VISIBLE);
            statusContainer.setVisibility(View.GONE);
        }
    }

    private void initializeViews() {
        // Back button and search
        btnBack = findViewById(R.id.btnBack);
        etSearch = findViewById(R.id.etSearch);
        
        // Filters
        tvFilterAll = findViewById(R.id.tvFilterAll);
        tvFilterPending = findViewById(R.id.tvFilterPending);
        tvFilterApproved = findViewById(R.id.tvFilterApproved);
        tvFilterRejected = findViewById(R.id.tvFilterRejected);

        // Customer 1
        cardCustomer1 = findViewById(R.id.cardCustomer1);
        tvCustomerName1 = findViewById(R.id.tvCustomerName1);
        tvCustomerPhone1 = findViewById(R.id.tvCustomerPhone1);
        tvTime1 = findViewById(R.id.tvTime1);
        tvBikeName1 = findViewById(R.id.tvBikeName1);
        tvBikeColor1 = findViewById(R.id.tvBikeColor1);
        layoutBikeColor1 = findViewById(R.id.layoutBikeColor1);
        btnReject1 = findViewById(R.id.btnReject1);
        btnAccept1 = findViewById(R.id.btnAccept1);
        statusContainer1 = findViewById(R.id.statusContainer1);
        actionContainer1 = findViewById(R.id.actionContainer1);
        ivTick1 = findViewById(R.id.ivTick1);
        ivCross1 = findViewById(R.id.ivCross1);
        ivArrow1 = findViewById(R.id.ivArrow1);

        // Customer 2
        cardCustomer2 = findViewById(R.id.cardCustomer2);
        tvCustomerName2 = findViewById(R.id.tvCustomerName2);
        tvCustomerPhone2 = findViewById(R.id.tvCustomerPhone2);
        tvTime2 = findViewById(R.id.tvTime2);
        tvBikeName2 = findViewById(R.id.tvBikeName2);
        tvBikeColor2 = findViewById(R.id.tvBikeColor2);
        layoutBikeColor2 = findViewById(R.id.layoutBikeColor2);
        btnReject2 = findViewById(R.id.btnReject2);
        btnAccept2 = findViewById(R.id.btnAccept2);
        statusContainer2 = findViewById(R.id.statusContainer2);
        actionContainer2 = findViewById(R.id.actionContainer2);
        ivTick2 = findViewById(R.id.ivTick2);
        ivCross2 = findViewById(R.id.ivCross2);
        ivArrow2 = findViewById(R.id.ivArrow2);

        // Customer 3
        cardCustomer3 = findViewById(R.id.cardCustomer3);
        tvCustomerName3 = findViewById(R.id.tvCustomerName3);
        tvCustomerPhone3 = findViewById(R.id.tvCustomerPhone3);
        tvTime3 = findViewById(R.id.tvTime3);
        tvBikeName3 = findViewById(R.id.tvBikeName3);
        tvBikeColor3 = findViewById(R.id.tvBikeColor3);
        layoutBikeColor3 = findViewById(R.id.layoutBikeColor3);
        btnReject3 = findViewById(R.id.btnReject3);
        btnAccept3 = findViewById(R.id.btnAccept3);
        statusContainer3 = findViewById(R.id.statusContainer3);
        actionContainer3 = findViewById(R.id.actionContainer3);
        ivTick3 = findViewById(R.id.ivTick3);
        ivCross3 = findViewById(R.id.ivCross3);
        ivArrow3 = findViewById(R.id.ivArrow3);

        // Customer 4
        cardCustomer4 = findViewById(R.id.cardCustomer4);
        tvCustomerName4 = findViewById(R.id.tvCustomerName4);
        tvCustomerPhone4 = findViewById(R.id.tvCustomerPhone4);
        tvTime4 = findViewById(R.id.tvTime4);
        tvBikeName4 = findViewById(R.id.tvBikeName4);
        tvBikeColor4 = findViewById(R.id.tvBikeColor4);
        layoutBikeColor4 = findViewById(R.id.layoutBikeColor4);
        btnReject4 = findViewById(R.id.btnReject4);
        btnAccept4 = findViewById(R.id.btnAccept4);
        statusContainer4 = findViewById(R.id.statusContainer4);
        actionContainer4 = findViewById(R.id.actionContainer4);
        ivTick4 = findViewById(R.id.ivTick4);
        ivCross4 = findViewById(R.id.ivCross4);
        ivArrow4 = findViewById(R.id.ivArrow4);

        // Customer 5
        cardCustomer5 = findViewById(R.id.cardCustomer5);
        tvCustomerName5 = findViewById(R.id.tvCustomerName5);
        tvCustomerPhone5 = findViewById(R.id.tvCustomerPhone5);
        tvTime5 = findViewById(R.id.tvTime5);
        tvBikeName5 = findViewById(R.id.tvBikeName5);
        tvBikeColor5 = findViewById(R.id.tvBikeColor5);
        layoutBikeColor5 = findViewById(R.id.layoutBikeColor5);
        btnReject5 = findViewById(R.id.btnReject5);
        btnAccept5 = findViewById(R.id.btnAccept5);
        statusContainer5 = findViewById(R.id.statusContainer5);
        actionContainer5 = findViewById(R.id.actionContainer5);
        ivTick5 = findViewById(R.id.ivTick5);
        ivCross5 = findViewById(R.id.ivCross5);
        ivArrow5 = findViewById(R.id.ivArrow5);
    }

    private void initializeDefaultStatus() {
        // Customer 3 is already accepted by default
        customerStatusMap.put(3, "accepted");
        customerNameMap.put(3, tvCustomerName3.getText().toString());
        customerPhoneMap.put(3, tvCustomerPhone3.getText().toString());

        // Others are pending by default
        customerStatusMap.put(1, "pending");
        customerNameMap.put(1, tvCustomerName1.getText().toString());
        customerPhoneMap.put(1, tvCustomerPhone1.getText().toString());

        customerStatusMap.put(2, "pending");
        customerNameMap.put(2, tvCustomerName2.getText().toString());
        customerPhoneMap.put(2, tvCustomerPhone2.getText().toString());

        customerStatusMap.put(4, "pending");
        customerNameMap.put(4, tvCustomerName4.getText().toString());
        customerPhoneMap.put(4, tvCustomerPhone4.getText().toString());

        customerStatusMap.put(5, "pending");
        customerNameMap.put(5, tvCustomerName5.getText().toString());
        customerPhoneMap.put(5, tvCustomerPhone5.getText().toString());
    }

    private void setupClickListeners() {
        // Back button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        // Search functionality
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            String searchText = etSearch.getText().toString().trim();
            if (!searchText.isEmpty()) {
                performSearch(searchText);
            }
            return true;
        });

        // Customer 1 actions
        setupCustomerActions(1,
                cardCustomer1,
                btnReject1,
                btnAccept1,
                statusContainer1,
                actionContainer1,
                ivTick1,
                ivCross1,
                ivArrow1,
                tvCustomerName1.getText().toString(),
                tvTime1
        );

        // Customer 2 actions
        setupCustomerActions(2,
                cardCustomer2,
                btnReject2,
                btnAccept2,
                statusContainer2,
                actionContainer2,
                ivTick2,
                ivCross2,
                ivArrow2,
                tvCustomerName2.getText().toString(),
                tvTime2
        );

        // Customer 3 actions (already accepted - view details)
        ivArrow3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToOrderSummary(3, tvCustomerName3.getText().toString(), tvCustomerPhone3.getText().toString());
            }
        });

        // Customer 4 actions
        setupCustomerActions(4,
                cardCustomer4,
                btnReject4,
                btnAccept4,
                statusContainer4,
                actionContainer4,
                ivTick4,
                ivCross4,
                ivArrow4,
                tvCustomerName4.getText().toString(),
                tvTime4
        );

        // Customer 5 actions
        setupCustomerActions(5,
                cardCustomer5,
                btnReject5,
                btnAccept5,
                statusContainer5,
                actionContainer5,
                ivTick5,
                ivCross5,
                ivArrow5,
                tvCustomerName5.getText().toString(),
                tvTime5
        );

        // Make phone numbers clickable
        setupPhoneClickListeners();
    }

    private void setupCustomerActions(
            int customerId,
            CardView card,
            Button rejectBtn,
            Button acceptBtn,
            LinearLayout statusContainer,
            LinearLayout actionContainer,
            ImageView ivTick,
            ImageView ivCross,
            ImageView ivArrow,
            String customerName,
            TextView tvTime) {

        rejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleRejectAction(customerId,
                        card,
                        statusContainer,
                        actionContainer,
                        ivCross,
                        ivTick,
                        ivArrow,
                        customerName,
                        tvTime);
            }
        });

        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleAcceptAction(customerId,
                        card,
                        statusContainer,
                        actionContainer,
                        ivTick,
                        ivCross,
                        ivArrow,
                        customerName,
                        tvTime);
            }
        });

        // Make arrow clickable for viewing details
        ivArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get current status
                String status = customerStatusMap.get(customerId);
                if (status == null) {
                    status = "pending";
                }

                // Get phone number
                String phoneNumber = "";
                switch (customerId) {
                    case 1: phoneNumber = tvCustomerPhone1.getText().toString(); break;
                    case 2: phoneNumber = tvCustomerPhone2.getText().toString(); break;
                    case 4: phoneNumber = tvCustomerPhone4.getText().toString(); break;
                    case 5: phoneNumber = tvCustomerPhone5.getText().toString(); break;
                }

                navigateToOrderSummary(customerId, customerName, phoneNumber);
            }
        });
    }

    private void navigateToOrderSummary(int customerId, String customerName, String phoneNumber) {
        try {
            Intent intent = new Intent(AdminRequestedCustomersActivity.this, OrderSummaryActivity.class);

            // Pass customer data
            intent.putExtra("request_id", customerId); // Using customerId as requestId based on logic
            intent.putExtra("customer_name", customerName);
            intent.putExtra("customer_phone", phoneNumber);

            // Get status from map
            String status = customerStatusMap.get(customerId);
            if (status == null || status.equals("pending")) {
                // If pending, show as "approved" in order summary (default)
                status = "approved";
            }
            intent.putExtra("status", status);

            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        } catch (Exception e) {
            Toast.makeText(AdminRequestedCustomersActivity.this,
                    "Error opening order summary: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void setupPhoneClickListeners() {
        // Make phone numbers clickable
        TextView[] phoneTextViews = {
                tvCustomerPhone1, tvCustomerPhone2, tvCustomerPhone3,
                tvCustomerPhone4, tvCustomerPhone5
        };

        for (TextView phoneTextView : phoneTextViews) {
            phoneTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String phoneNumber = phoneTextView.getText().toString();
                    Toast.makeText(AdminRequestedCustomersActivity.this,
                            "Call " + phoneNumber,
                            Toast.LENGTH_SHORT).show();
                    // TODO: Implement phone call intent
                }
            });
        }
    }

    private void handleRejectAction(
            int customerId,
            CardView card,
            LinearLayout statusContainer,
            LinearLayout actionContainer,
            ImageView ivCross,
            ImageView ivTick,
            ImageView ivArrow,
            String customerName,
            TextView tvTime) {

        // Show confirmation
        Toast.makeText(this,
                "Reject " + customerName + "?",
                Toast.LENGTH_LONG).show();

        // Update status in map
        customerStatusMap.put(customerId, "rejected");

        // Update UI immediately
        updateCustomerStatus(customerId, "rejected",
                card, statusContainer, actionContainer,
                ivCross, ivTick, ivArrow, customerName, tvTime);
    }

    private void handleAcceptAction(
            int customerId,
            CardView card,
            LinearLayout statusContainer,
            LinearLayout actionContainer,
            ImageView ivTick,
            ImageView ivCross,
            ImageView ivArrow,
            String customerName,
            TextView tvTime) {

        // Show confirmation
        Toast.makeText(this,
                "Accept " + customerName + " as a customer?",
                Toast.LENGTH_LONG).show();

        // Update status in map
        customerStatusMap.put(customerId, "accepted");

        // Update UI immediately
        updateCustomerStatus(customerId, "accepted",
                card, statusContainer, actionContainer,
                ivCross, ivTick, ivArrow, customerName, tvTime);
    }

    private void updateCustomerStatus(
            int customerId,
            String status,
            CardView card,
            LinearLayout statusContainer,
            LinearLayout actionContainer,
            ImageView ivCross,
            ImageView ivTick,
            ImageView ivArrow,
            String customerName,
            TextView tvTime) {

        // Hide action buttons
        actionContainer.setVisibility(View.GONE);

        // Show status container
        statusContainer.setVisibility(View.VISIBLE);

        if (status.equals("accepted")) {
            // Show tick mark
            ivTick.setVisibility(View.VISIBLE);
            ivCross.setVisibility(View.GONE);

            // Show arrow for details
            ivArrow.setVisibility(View.VISIBLE);

            // Update time text
            tvTime.setText("Accepted");
            tvTime.setTextColor(getResources().getColor(R.color.icon_green));

            // Change card background if needed
            // card.setCardBackgroundColor(getResources().getColor(R.color.icon_bg_green));

            Toast.makeText(this,
                    "Customer accepted successfully!",
                    Toast.LENGTH_SHORT).show();

        } else if (status.equals("rejected")) {
            // Show cross mark
            ivCross.setVisibility(View.VISIBLE);
            ivTick.setVisibility(View.GONE);

            // Show arrow for details
            ivArrow.setVisibility(View.VISIBLE);

            // Update time text
            tvTime.setText("Rejected");
            tvTime.setTextColor(getResources().getColor(R.color.icon_red));

            // Change card background if needed
            // card.setCardBackgroundColor(getResources().getColor(R.color.icon_bg_red));

            Toast.makeText(this,
                    "Customer rejected",
                    Toast.LENGTH_SHORT).show();
        }

        // TODO: Send API request to update status in backend
        // updateCustomerStatusOnServer(customerId, status);
    }

    private void performSearch(String searchText) {
        // Implement search logic here
        Toast.makeText(this,
                "Searching for: " + searchText,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}