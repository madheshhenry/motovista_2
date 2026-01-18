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
import com.example.motovista_deep.helpers.SharedPrefManager;
import com.example.motovista_deep.models.BikeRequest;
import com.example.motovista_deep.models.GetBikeRequestsResponse;
import com.example.motovista_deep.models.GetMyRequestsRequest;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyRequestedBikesActivity extends AppCompatActivity {

    private ImageView btnBack;
    private RecyclerView rvRequests;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private BikeRequestAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_requested_bikes);

        initializeViews();
        setupRecyclerView();
        
        btnBack.setOnClickListener(v -> finish());
        
        fetchMyRequests();
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        rvRequests = findViewById(R.id.rvRequests);
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tvEmpty);
    }

    private void setupRecyclerView() {
        rvRequests.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BikeRequestAdapter(this, new ArrayList<>(), new BikeRequestAdapter.OnLongClickListener() {
            @Override
            public void onLongClick(BikeRequest request) {
                showDeleteConfirmationDialog(request);
            }
        });
        rvRequests.setAdapter(adapter);
    }

    private void fetchMyRequests() {
        String mobileNumber = SharedPrefManager.getInstance(this).getUserPhone();
        
        if (mobileNumber == null || mobileNumber.isEmpty()) {
            Toast.makeText(this, "User phone number not found.", Toast.LENGTH_SHORT).show();
            tvEmpty.setText("Profile incomplete. Please add phone number.");
            tvEmpty.setVisibility(View.VISIBLE);
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        
        com.example.motovista_deep.models.User user = SharedPrefManager.getInstance(this).getUser();
        Integer userId = (user != null) ? user.getId() : null;
        
        GetMyRequestsRequest request = new GetMyRequestsRequest(mobileNumber, userId);
        
        ApiService apiService = RetrofitClient.getApiService();
        apiService.getMyBikeRequests(request).enqueue(new Callback<GetBikeRequestsResponse>() {
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
                    Toast.makeText(MyRequestedBikesActivity.this, "Failed to load requests", Toast.LENGTH_SHORT).show();
                    tvEmpty.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<GetBikeRequestsResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MyRequestedBikesActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                tvEmpty.setVisibility(View.VISIBLE);
            }
        });
    }

    private void showDeleteConfirmationDialog(BikeRequest request) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete request")
                .setMessage("Are you sure you want to delete this bike request?")
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
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(MyRequestedBikesActivity.this, "Request Deleted", Toast.LENGTH_SHORT).show();
                    fetchMyRequests();
                } else {
                    Toast.makeText(MyRequestedBikesActivity.this, "Failed to delete", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<com.example.motovista_deep.models.GenericResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MyRequestedBikesActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
