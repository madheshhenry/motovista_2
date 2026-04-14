package com.example.motovista_deep;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.motovista_deep.adapters.BikeRequestAdapter;
import com.example.motovista_deep.api.ApiService;
import com.example.motovista_deep.api.RetrofitClient;
import com.example.motovista_deep.helpers.SharedPrefManager;
import com.example.motovista_deep.models.BikeRequest;
import com.example.motovista_deep.models.DeleteRequestRequest;
import com.example.motovista_deep.models.GenericResponse;
import com.example.motovista_deep.models.GetBikeRequestsResponse;
import com.example.motovista_deep.models.GetMyRequestsRequest;
import com.example.motovista_deep.models.User;

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
    private View emptyStateContainer;
    private BikeRequestAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Use modern EdgeToEdge to match Profile screen's status bar/notch behavior
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_my_requested_bikes);

        initializeViews();
        setupRecyclerView();
        
        // Properly handle system bars insets for the header (notch area)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.header), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), systemBars.top, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        btnBack.setOnClickListener(v -> finish());
        
        fetchMyRequests();
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        rvRequests = findViewById(R.id.rvRequests);
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tvEmpty);
        emptyStateContainer = findViewById(R.id.emptyStateContainer);
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
            if (emptyStateContainer != null) {
                emptyStateContainer.setVisibility(View.VISIBLE);
                tvEmpty.setText("Profile incomplete. Please add phone number.");
            }
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        if (emptyStateContainer != null) emptyStateContainer.setVisibility(View.GONE);
        
        User user = SharedPrefManager.getInstance(this).getUser();
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
                        if (emptyStateContainer != null) emptyStateContainer.setVisibility(View.VISIBLE);
                    } else {
                        rvRequests.setVisibility(View.VISIBLE);
                        if (emptyStateContainer != null) emptyStateContainer.setVisibility(View.GONE);
                        adapter.updateList(requests);
                    }
                } else {
                    Toast.makeText(MyRequestedBikesActivity.this, "Failed to load requests", Toast.LENGTH_SHORT).show();
                    if (emptyStateContainer != null) {
                        emptyStateContainer.setVisibility(View.VISIBLE);
                        tvEmpty.setText("Failed to load requests. Please try again.");
                    }
                }
            }

            @Override
            public void onFailure(Call<GetBikeRequestsResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MyRequestedBikesActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                if (emptyStateContainer != null) emptyStateContainer.setVisibility(View.VISIBLE);
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
        DeleteRequestRequest delReq = new DeleteRequestRequest(request.getId());
        
        RetrofitClient.getApiService().deleteBikeRequest(delReq).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(MyRequestedBikesActivity.this, "Request Deleted", Toast.LENGTH_SHORT).show();
                    fetchMyRequests();
                } else {
                    Toast.makeText(MyRequestedBikesActivity.this, "Failed to delete", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MyRequestedBikesActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
