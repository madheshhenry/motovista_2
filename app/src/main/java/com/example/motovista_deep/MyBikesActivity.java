package com.example.motovista_deep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.motovista_deep.adapter.MyBikesAdapter;
import com.example.motovista_deep.api.ApiService;
import com.example.motovista_deep.api.RetrofitClient;
import com.example.motovista_deep.helpers.SharedPrefManager;
import com.example.motovista_deep.models.MyBikeModel;
import com.example.motovista_deep.models.MyBikesResponse;
import com.example.motovista_deep.models.User;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyBikesActivity extends AppCompatActivity {

    private ImageView btnBack;
    private RecyclerView rvMyBikes;
    private MyBikesAdapter adapter;
    private List<MyBikeModel> bikeList = new ArrayList<>();
    private LinearLayout emptyStateLayout;
    private Button btnBrowseShowroom;
    private ProgressBar progressBar;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bikes);

        apiService = RetrofitClient.getApiService();

        initializeViews();
        setupRecyclerView();
        setupClickListeners();
        loadBikesData();
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        rvMyBikes = findViewById(R.id.rvMyBikes);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        btnBrowseShowroom = findViewById(R.id.btnBrowseShowroom);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupRecyclerView() {
        rvMyBikes.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyBikesAdapter(this, bikeList);
        rvMyBikes.setAdapter(adapter);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnBrowseShowroom.setOnClickListener(v -> {
            Intent intent = new Intent(this, BikeCatalogActivity.class);
            startActivity(intent);
        });
    }

    private void loadBikesData() {
        User user = SharedPrefManager.getInstance(this).getCustomer();
        if (user == null) {
            Toast.makeText(this, "Session expired", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        apiService.getMyBikes(user.getId()).enqueue(new Callback<MyBikesResponse>() {
            @Override
            public void onResponse(Call<MyBikesResponse> call, Response<MyBikesResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    MyBikesResponse bikesResponse = response.body();
                    if (bikesResponse.isSuccess()) {
                        bikeList.clear();
                        if (bikesResponse.getData() != null && !bikesResponse.getData().isEmpty()) {
                            bikeList.addAll(bikesResponse.getData());
                            emptyStateLayout.setVisibility(View.GONE);
                            rvMyBikes.setVisibility(View.VISIBLE);
                        } else {
                            emptyStateLayout.setVisibility(View.VISIBLE);
                            rvMyBikes.setVisibility(View.GONE);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(MyBikesActivity.this, bikesResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<MyBikesResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MyBikesActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}