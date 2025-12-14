package com.example.motovista_deep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.motovista_deep.models.BikeModel;

public class BikeDetailsActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView tvTitle;
    private ImageView ivBikeImage;
    private LinearLayout detailsContainer;
    private Button btnEdit, btnDelete;

    private BikeModel bike;
    private String bikeType; // "NEW" or "SECOND_HAND"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_details);

        // Get bike data from intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("BIKE_MODEL")) {
            bike = (BikeModel) intent.getSerializableExtra("BIKE_MODEL");
            bikeType = intent.getStringExtra("BIKE_TYPE");
        } else {
            // Fallback to sample data
            bike = new BikeModel(1, "Royal Enfield", "Classic 350", "2,10,000",
                    "Excellent", "https://lh3.googleusercontent.com/aida-public/AB6AXuCL-fxH0U0ZsXcpL2B4Re94_JC4GXZVmn7nTucmNIusCpWa-eUfJmPxpZoMXMiDNMg4D7X4bqoKKF9EhiHOym_7_-G-INLRP7U1X21XELIKMVNFX_x7L1tfS88iObaDJ6CoArtzdG5pyM1CAPJTBpMpn106gODuryrbXIa0P5aumti6UI-AT49gyDD2xQnEDlMXT6MDcujkEhn0N_C2Kg6imTq9MpPr2RQMQbJKJZ96xfvfJW6VimvjKO7EwnScxS8x1lOt7G0CRtRO",
                    "NEW", true);
            bikeType = "NEW";
        }

        initializeViews();
        setupListeners();
        loadBikeData();
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        tvTitle = findViewById(R.id.tvTitle);
        ivBikeImage = findViewById(R.id.ivBikeImage);
        detailsContainer = findViewById(R.id.detailsContainer);
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);

        // Set title based on bike type
        if ("NEW".equals(bikeType)) {
            tvTitle.setText("New Bike Details");
        } else {
            tvTitle.setText("SH Bike Details");
        }
    }

    private void setupListeners() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to edit screen
                Toast.makeText(BikeDetailsActivity.this, "Edit Bike Details", Toast.LENGTH_SHORT).show();
                // Intent editIntent = new Intent(BikeDetailsActivity.this, EditBikeActivity.class);
                // editIntent.putExtra("BIKE_MODEL", bike);
                // startActivity(editIntent);
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show confirmation dialog
                showDeleteConfirmationDialog();
            }
        });
    }

    private void loadBikeData() {
        // Load bike image
        if (bike.getImageUrl() != null && !bike.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(bike.getImageUrl())
                    .placeholder(R.drawable.ic_bike_placeholder)
                    .error(R.drawable.ic_bike_placeholder)
                    .into(ivBikeImage);
        }

        // Inflate appropriate layout based on bike type
        LayoutInflater inflater = LayoutInflater.from(this);
        View detailsView;

        if ("NEW".equals(bikeType)) {
            detailsView = inflater.inflate(R.layout.layout_new_bike_details, detailsContainer, false);
            setupNewBikeDetails(detailsView);
        } else {
            detailsView = inflater.inflate(R.layout.layout_sh_bike_details, detailsContainer, false);
            setupSHBikeDetails(detailsView);
        }

        // Clear container and add new view
        detailsContainer.removeAllViews();
        detailsContainer.addView(detailsView);
    }

    private void setupNewBikeDetails(View view) {
        // Find views
        TextView tvBrand = view.findViewById(R.id.tvBrand);
        TextView tvModel = view.findViewById(R.id.tvModel);
        TextView tvExShowroomPrice = view.findViewById(R.id.tvExShowroomPrice);
        TextView tvRTO = view.findViewById(R.id.tvRTO);
        TextView tvInsurance = view.findViewById(R.id.tvInsurance);
        TextView tvOnRoadPrice = view.findViewById(R.id.tvOnRoadPrice);
        TextView tvEngine = view.findViewById(R.id.tvEngine);
        TextView tvMileage = view.findViewById(R.id.tvMileage);
        TextView tvType = view.findViewById(R.id.tvType);
        TextView tvTopSpeed = view.findViewById(R.id.tvTopSpeed);
        TextView tvBraking = view.findViewById(R.id.tvBraking);

        // Set data (you would get this from API)
        tvBrand.setText(bike.getBrand());
        tvModel.setText(bike.getModel());
        tvOnRoadPrice.setText("₹" + bike.getPrice());

        // Set sample data for other fields (replace with actual API data)
        tvExShowroomPrice.setText("₹1,90,000");
        tvRTO.setText("₹12,000");
        tvInsurance.setText("₹8,000");
        tvEngine.setText("349 cc");
        tvMileage.setText("37 kmpl");
        tvType.setText("Cruiser");
        tvTopSpeed.setText("114 kmph");
        tvBraking.setText("Dual Channel ABS");
    }

    private void setupSHBikeDetails(View view) {
        // Find views
        TextView tvBrand = view.findViewById(R.id.tvBrand);
        TextView tvModel = view.findViewById(R.id.tvModel);
        TextView tvExpectedPrice = view.findViewById(R.id.tvExpectedPrice);
        TextView tvYear = view.findViewById(R.id.tvYear);
        TextView tvOdometer = view.findViewById(R.id.tvOdometer);
        TextView tvOwnership = view.findViewById(R.id.tvOwnership);
        TextView tvConditionDetails = view.findViewById(R.id.tvConditionDetails);
        TextView tvEngine = view.findViewById(R.id.tvEngine);
        TextView tvMileage = view.findViewById(R.id.tvMileage);
        TextView tvBrakes = view.findViewById(R.id.tvBrakes);
        TextView tvABS = view.findViewById(R.id.tvABS);

        // Set data
        tvBrand.setText(bike.getBrand());
        tvModel.setText(bike.getModel());
        tvExpectedPrice.setText("₹" + bike.getPrice());

        // Set sample data for other fields (replace with actual API data)
        tvYear.setText("2018");
        tvOdometer.setText("15,000 km");
        tvOwnership.setText("1st Owner");
        tvConditionDetails.setText("The bike is in excellent running condition with minor scratches on the tank. Recently serviced with new tires. All documents are clear and available.");
        tvEngine.setText("346 cc");
        tvMileage.setText("40 kmpl");
        tvBrakes.setText("Front Disc, Rear Drum");
        tvABS.setText("Single Channel");
    }

    private void showDeleteConfirmationDialog() {
        // Create a confirmation dialog
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Remove Bike");
        builder.setMessage("Are you sure you want to remove this bike from inventory?");

        builder.setPositiveButton("Remove", (dialog, which) -> {
            // Call API to delete bike
            deleteBike();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
        });

        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteBike() {
        // Here you would call your API to delete the bike
        Toast.makeText(this, "Bike removed successfully", Toast.LENGTH_SHORT).show();

        // Navigate back to inventory
        Intent intent = new Intent(this, BikeInventoryActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}