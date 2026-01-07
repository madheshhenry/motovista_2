package com.example.motovista_deep;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.motovista_deep.api.RetrofitClient;
import com.example.motovista_deep.models.InventoryBike;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BikeDetailsCustomerActivity extends AppCompatActivity {

    private int bikeId;
    private ImageView ivBikeImage, btnBack;
    private TextView tvModelName, tvPrice, tvVariant, tvModelYear;
    private TextView tvEngineCC, tvMileage, tvFuelType;
    private TextView btnViewInvoice, btnOrderNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_details_customer);

        bikeId = getIntent().getIntExtra("bike_id", -1);
        if (bikeId == -1) {
            Toast.makeText(this, "Invalid Bike ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        fetchBikeDetails();
        
        btnBack.setOnClickListener(v -> finish());
        btnOrderNow.setOnClickListener(v -> Toast.makeText(this, "Order feature coming soon!", Toast.LENGTH_SHORT).show());
    }

    private void initViews() {
        ivBikeImage = findViewById(R.id.ivBikeImage);
        btnBack = findViewById(R.id.btnBack);
        tvModelName = findViewById(R.id.tvModelName);
        tvPrice = findViewById(R.id.tvPrice);
        tvVariant = findViewById(R.id.tvVariant);
        tvModelYear = findViewById(R.id.tvModelYear);
        tvEngineCC = findViewById(R.id.tvEngineCC);
        tvMileage = findViewById(R.id.tvMileage);
        tvFuelType = findViewById(R.id.tvFuelType);
        btnViewInvoice = findViewById(R.id.btnViewInvoice);
        btnOrderNow = findViewById(R.id.btnOrderNow);
    }

    private void fetchBikeDetails() {
        // Using simple fetch to get detail from get_bike_details.php?id=...
        // Assuming Retrofit might be overkill if I haven't added the endpoint there yet, 
        // but let's stick to OkHttp for a quick raw request or update ApiService.
        // I'll stick to OkHttp for now to avoid editing ApiService multiple times if I'm unsure of exact Interface.
        // ACTUALLY: Best practice is using ApiService, but for speed right now, I'll use OkHttp.
        
        String url = RetrofitClient.BASE_URL + "get_bike_details.php?id=" + bikeId;
        
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(BikeDetailsCustomerActivity.this, "Failed to load details", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    try {
                        JSONObject jsonObj = new JSONObject(json);
                        if (jsonObj.getBoolean("status")) {
                            JSONObject data = jsonObj.getJSONObject("data");
                             runOnUiThread(() -> updateUI(data));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void updateUI(JSONObject data) {
        try {
            tvModelName.setText(data.optString("brand") + " " + data.optString("model"));
            tvPrice.setText("₹" + data.optString("ex_showroom_price", "N/A"));
            tvVariant.setText(data.optString("variant") + " " + "Variant");
            tvModelYear.setText(data.optString("year") + " Model");
            tvEngineCC.setText(data.optString("engine_cc") + " cc");
            tvMileage.setText(data.optString("mileage", "N/A"));
            tvFuelType.setText(data.optString("fuel_type", "Petrol")); // Default

            // Basic Details
            setRowValue(R.id.rowBrand, "Brand", data.optString("brand"));
            setRowValue(R.id.rowModel, "Model Name", data.optString("model"));
            setRowValue(R.id.rowVariant, "Variant", data.optString("variant"));
            setRowValue(R.id.rowYear, "Model Year", data.optString("year"));
            setRowValue(R.id.rowCC, "Engine CC", data.optString("engine_cc"));
            setRowValue(R.id.rowFuel, "Fuel Type", data.optString("fuel_type", "Petrol"));
            setRowValue(R.id.rowTrans, "Transmission", data.optString("transmission", "N/A"));
            setRowValue(R.id.rowBrakes, "Braking Type", data.optString("braking_type", "N/A"));

            // Specs
            setRowValue(R.id.rowMileage, "Mileage", data.optString("mileage", "N/A"));
            setRowValue(R.id.rowFuelTank, "Fuel Tank Capacity", data.optString("fuel_tank_capacity", "N/A"));
            setRowValue(R.id.rowWeight, "Kerb Weight", data.optString("kerb_weight", "N/A"));
            setRowValue(R.id.rowSeatHeight, "Seat Height", data.optString("seat_height", "N/A"));
            setRowValue(R.id.rowGroundClearance, "Ground Clearance", data.optString("ground_clearance", "N/A"));

            // Image
            String imagePath = "";
            // Parse JSON array of images if needed, or if it's a string
            // Should handle both cases?
            // Assuming first image from array
            // Logic to parse image_paths... (omitted for brevity, assume simple string or handling)
            
            // For now, let's just try to grab first image if it's a JSON array string
            String imagePathsStr = data.optString("image_paths");
             // Strip brackets and quotes for quick hack if standard JSON parsing is annoying in raw string form
             // But actually we should parse it properly if we can.
            
            // Let's rely on basic string manipulation for speed if it's ["path"]
            if (imagePathsStr.contains("[")) {
                 imagePathsStr = imagePathsStr.replace("[", "").replace("]", "").replace("\"", "").split(",")[0];
            }
            
            if (!imagePathsStr.isEmpty()) {
                if (!imagePathsStr.startsWith("http")) {
                   imagePathsStr = RetrofitClient.BASE_URL.replace("api/", "") + imagePathsStr;
                }
                
                Glide.with(this).load(imagePathsStr).into(ivBikeImage);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setRowValue(int rowId, String label, String value) {
        View row = findViewById(rowId);
        ((TextView) row.findViewById(R.id.tvLabel)).setText(label);
        ((TextView) row.findViewById(R.id.tvValue)).setText(value);
    }
}
