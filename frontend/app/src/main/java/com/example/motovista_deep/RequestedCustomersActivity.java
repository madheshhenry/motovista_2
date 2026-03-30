package com.example.motovista_deep;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.motovista_deep.adapters.BikeRequestAdapter;
import com.example.motovista_deep.api.ApiService;
import com.example.motovista_deep.api.RetrofitClient;
import com.example.motovista_deep.models.BikeRequest;
import com.example.motovista_deep.models.GenericResponse;
import com.example.motovista_deep.models.GetBikeRequestsResponse;
import com.example.motovista_deep.models.UpdateBikeRequestStatusRequest;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestedCustomersActivity extends AppCompatActivity {

    private ImageView btnBack;
    private RecyclerView rvRequests;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private BikeRequestAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requested_customers);

        initializeViews();
        setupRecyclerView();
        
        btnBack.setOnClickListener(v -> finish());
        
        fetchRequests();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        fetchRequests();
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        rvRequests = findViewById(R.id.rvRequests);
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tvEmpty);
    }

    private void setupRecyclerView() {
        rvRequests.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BikeRequestAdapter(this, new ArrayList<>(), new BikeRequestAdapter.OnActionListener() {
            @Override
            public void onAccept(BikeRequest request) {
                updateStatus(request.getId(), "Approved");
            }

            @Override
            public void onReject(BikeRequest request) {
                updateStatus(request.getId(), "Rejected");
            }
        }, new BikeRequestAdapter.OnLongClickListener() {
            @Override
            public void onLongClick(BikeRequest request) {
                showDeleteConfirmationDialog(request);
            }
        });
        rvRequests.setAdapter(adapter);
    }

    private void fetchRequests() {
        progressBar.setVisibility(View.VISIBLE);
        ApiService apiService = RetrofitClient.getApiService();
        apiService.getBikeRequests().enqueue(new Callback<GetBikeRequestsResponse>() {
            @Override
            public void onResponse(Call<GetBikeRequestsResponse> call, Response<GetBikeRequestsResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<BikeRequest> requests = response.body().getData();
                    if (requests == null || requests.isEmpty()) {
                        rvRequests.setVisibility(View.GONE);
                        tvEmpty.setVisibility(View.VISIBLE);
                    } else {
                        rvRequests.setVisibility(View.VISIBLE);
                        tvEmpty.setVisibility(View.GONE);
                        adapter.updateList(requests);
                    }
                } else {
                    Toast.makeText(RequestedCustomersActivity.this, "Failed to load requests", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GetBikeRequestsResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(RequestedCustomersActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateStatus(int requestId, String status) {
        progressBar.setVisibility(View.VISIBLE);
        
        UpdateBikeRequestStatusRequest request = new UpdateBikeRequestStatusRequest(requestId, status);
        
        ApiService apiService = RetrofitClient.getApiService();
        apiService.updateBikeRequestStatus(request).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(RequestedCustomersActivity.this, "Status Updated: " + status, Toast.LENGTH_SHORT).show();
                    fetchRequests(); // Refresh list
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(RequestedCustomersActivity.this, "Failed to update status", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                 progressBar.setVisibility(View.GONE);
                 Toast.makeText(RequestedCustomersActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDeleteConfirmationDialog(BikeRequest request) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete Request")
                .setMessage("Are you sure you want to delete this bike request from " + request.getFullName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> deleteRequest(request))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteRequest(BikeRequest request) {
        progressBar.setVisibility(View.VISIBLE);
        com.example.motovista_deep.models.DeleteRequestRequest delReq = new com.example.motovista_deep.models.DeleteRequestRequest(request.getId());
        
        RetrofitClient.getApiService().deleteBikeRequest(delReq).enqueue(new Callback<com.example.motovista_deep.models.GenericResponse>() {
            @Override
            public void onResponse(Call<com.example.motovista_deep.models.GenericResponse> call, Response<com.example.motovista_deep.models.GenericResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(RequestedCustomersActivity.this, "Request Deleted", Toast.LENGTH_SHORT).show();
                    fetchRequests();
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(RequestedCustomersActivity.this, "Failed to delete", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<com.example.motovista_deep.models.GenericResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(RequestedCustomersActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
