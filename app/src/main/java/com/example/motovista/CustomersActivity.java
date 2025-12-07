package com.example.motovista;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.motovista.adapters.CustomerAdapter;
import com.example.motovista.api.ApiClient;
import com.example.motovista.api.ApiService;
import com.example.motovista.models.CustomerModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomersActivity extends AppCompatActivity {

    RecyclerView rvCustomers;
    EditText etSearch;

    CustomerAdapter adapter;
    List<CustomerModel> customers = new ArrayList<>();

    ApiService apiService;
    BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customers);

        overridePendingTransition(0, 0); // Disable animation

        apiService = ApiClient.getClient().create(ApiService.class);

        rvCustomers = findViewById(R.id.rvCustomers);
        etSearch = findViewById(R.id.etSearch);
        bottomNav = findViewById(R.id.bottomNav);

        // Recycler setup
        rvCustomers.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CustomerAdapter(this, customers);
        rvCustomers.setAdapter(adapter);

        // Load all customers initially
        loadCustomers("");

        // Search
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {}
            @Override public void onTextChanged(CharSequence s, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                loadCustomers(editable.toString().trim());
            }
        });

        // Bottom navigation
        setupBottomNav();
    }

    private void setupBottomNav() {

        bottomNav.setSelectedItemId(R.id.nav_customers);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_dashboard) {
                startActivity(new Intent(this, AdminDashboardActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }

            if (id == R.id.nav_inventory) {
                startActivity(new Intent(this, InventoryActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }

            if (id == R.id.nav_bikes) {
                startActivity(new Intent(this, BikesActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }

            if (id == R.id.nav_customers) {
                return true; // Already here
            }

            if (id == R.id.nav_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }

            return false;
        });
    }

    private void loadCustomers(String q) {
        apiService.getCustomers(q).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (!response.isSuccessful()) {
                    Toast.makeText(CustomersActivity.this, "API Error", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    String json = response.body().string();

                    // OLD GSON COMPATIBLE PARSER
                    com.google.gson.JsonParser parser = new com.google.gson.JsonParser();
                    com.google.gson.JsonObject root = parser.parse(json).getAsJsonObject();

                    String dataJson = root.getAsJsonArray("data").toString();

                    List<CustomerModel> newList =
                            new Gson().fromJson(dataJson, new TypeToken<List<CustomerModel>>(){}.getType());

                    adapter.updateData(newList);

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(CustomersActivity.this, "Parsing Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(CustomersActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
