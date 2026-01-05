package com.example.motovista_deep;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.motovista_deep.api.RetrofitClient;
import com.example.motovista_deep.helpers.SharedPrefManager;
import com.example.motovista_deep.models.GetCustomerDetailResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerDetailsActivity extends AppCompatActivity {

    // UI Elements
    private ImageView btnBack;
    private ImageView ivProfile;
    private TextView tvName, tvEmail, tvPhone, tvAddress, tvDob, tvPan;
    private CardView cardAadharFront, cardAadharBack;
    private LinearLayout btnDownloadInvoice, btnPurchases;

    // For Aadhar images
    private ImageView ivAadharFrontPlaceholder, ivAadharBackPlaceholder;
    private TextView tvAadharFrontText, tvAadharBackText;

    private static final String TAG = "CustomerDetails";

    // Image URLs from API response
    private String profileImageUrl = "";
    private String aadharFrontUrl = "";
    private String aadharBackUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_details);

        // Initialize views
        initializeViews();

        // Setup click listeners
        setupClickListeners();

        // Get customer_id from intent
        int customerId = getIntent().getIntExtra("customer_id", 0);

        Log.d(TAG, "Received Customer ID: " + customerId);

        if (customerId > 0) {
            loadCustomerDetails(customerId);
        } else {
            Toast.makeText(this, "Invalid customer ID", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initializeViews() {
        // Back button
        btnBack = findViewById(R.id.btnBack);

        // Profile section
        ivProfile = findViewById(R.id.ivProfile);
        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);

        // Personal info section
        tvAddress = findViewById(R.id.tvAddress);
        tvDob = findViewById(R.id.tvDob);
        tvPan = findViewById(R.id.tvPan);

        // Aadhar cards
        cardAadharFront = findViewById(R.id.cardAadharFront);
        cardAadharBack = findViewById(R.id.cardAadharBack);

        // Aadhar card placeholders and text
        ivAadharFrontPlaceholder = cardAadharFront.findViewById(R.id.ivAadharFrontPlaceholder);
        ivAadharBackPlaceholder = cardAadharBack.findViewById(R.id.ivAadharBackPlaceholder);
        tvAadharFrontText = cardAadharFront.findViewById(R.id.tvAadharFrontText);
        tvAadharBackText = cardAadharBack.findViewById(R.id.tvAadharBackText);

        // Action buttons
        btnDownloadInvoice = findViewById(R.id.btnDownloadInvoice);
        btnPurchases = findViewById(R.id.btnPurchases);
    }

    private void setupClickListeners() {
        // Back button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Profile image click
        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (profileImageUrl != null && !profileImageUrl.isEmpty() && !profileImageUrl.equals("N/A")) {
                    openFullScreenImage(profileImageUrl, "Profile Image");
                } else {
                    Toast.makeText(CustomerDetailsActivity.this,
                            "No profile image available", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Aadhar Front card
        cardAadharFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (aadharFrontUrl != null && !aadharFrontUrl.isEmpty() && !aadharFrontUrl.equals("N/A")) {
                    openFullScreenImage(aadharFrontUrl, "Aadhar Front");
                } else {
                    Toast.makeText(CustomerDetailsActivity.this,
                            "No Aadhar front image available", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Aadhar Back card
        cardAadharBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (aadharBackUrl != null && !aadharBackUrl.isEmpty() && !aadharBackUrl.equals("N/A")) {
                    openFullScreenImage(aadharBackUrl, "Aadhar Back");
                } else {
                    Toast.makeText(CustomerDetailsActivity.this,
                            "No Aadhar back image available", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Download Invoice button
        btnDownloadInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CustomerDetailsActivity.this,
                        "Download Invoice", Toast.LENGTH_SHORT).show();
            }
        });

        // Purchases button
        btnPurchases.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CustomerDetailsActivity.this,
                        "View Purchases", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openFullScreenImage(String imageUrl, String title) {
        Intent intent = new Intent(this, FullScreenImageActivity.class);
        intent.putExtra("image_url", imageUrl);
        intent.putExtra("title", title);
        startActivity(intent);
    }

    private void loadCustomerDetails(int customerId) {
        // Get admin token from SharedPreferences
        String token = SharedPrefManager.getInstance(this).getToken();

        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Admin not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d(TAG, "Loading customer details for ID: " + customerId);

        // Make API call to get customer details
        RetrofitClient.getApiService()
                .getCustomerDetail("Bearer " + token, customerId)
                .enqueue(new Callback<GetCustomerDetailResponse>() {
                    @Override
                    public void onResponse(Call<GetCustomerDetailResponse> call,
                                           Response<GetCustomerDetailResponse> response) {

                        Log.d(TAG, "Response Code: " + response.code());

                        if (response.isSuccessful() && response.body() != null) {
                            GetCustomerDetailResponse apiResponse = response.body();

                            if (apiResponse.status && apiResponse.data != null) {
                                // Update UI with customer data
                                updateCustomerUI(apiResponse.data);
                            } else {
                                String errorMsg = apiResponse.message != null ?
                                        apiResponse.message : "Failed to load customer details";
                                Toast.makeText(CustomerDetailsActivity.this,
                                        errorMsg, Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "API Error: " + errorMsg);
                            }
                        } else {
                            String errorMsg = "Server error: " + response.code();
                            Toast.makeText(CustomerDetailsActivity.this,
                                    errorMsg, Toast.LENGTH_SHORT).show();
                            Log.e(TAG, errorMsg);
                        }
                    }

                    @Override
                    public void onFailure(Call<GetCustomerDetailResponse> call, Throwable t) {
                        String errorMsg = "Network error: " + t.getMessage();
                        Toast.makeText(CustomerDetailsActivity.this,
                                errorMsg, Toast.LENGTH_SHORT).show();
                        Log.e(TAG, errorMsg, t);
                    }
                });
    }

    private void updateCustomerUI(GetCustomerDetailResponse.Data customer) {
        Log.d(TAG, "Updating UI with customer: " + customer.full_name);

        // Debug log for image URLs
        Log.d(TAG, "Profile image: " + customer.profile_image);
        Log.d(TAG, "Aadhar front: " + customer.aadhar_front);
        Log.d(TAG, "Aadhar back: " + customer.aadhar_back);

        // Set basic customer info
        if (tvName != null) {
            tvName.setText(customer.full_name != null ? customer.full_name : "N/A");
        }

        if (tvPhone != null) {
            tvPhone.setText(customer.phone != null ? customer.phone : "N/A");
        }

        if (tvEmail != null) {
            tvEmail.setText(customer.email != null ? customer.email : "N/A");
        }

        // Set address
        if (tvAddress != null) {
            if (customer.address != null && !customer.address.isEmpty() &&
                    !customer.address.equals("N/A")) {
                tvAddress.setText(customer.address);
            } else {
                tvAddress.setText("Address not available");
            }
        }

        // Set date of birth
        if (tvDob != null) {
            tvDob.setText(customer.dob != null && !customer.dob.equals("N/A") ?
                    customer.dob : "Not available");
        }

        // Set PAN
        if (tvPan != null) {
            tvPan.setText(customer.pan != null && !customer.pan.equals("N/A") ?
                    customer.pan : "Not available");
        }

        // Load profile image
        if (customer.profile_image != null && !customer.profile_image.equals("N/A")) {
            profileImageUrl = RetrofitClient.BASE_URL + "uploads/" + customer.profile_image;
            Log.d(TAG, "Loading profile image from: " + profileImageUrl);

            Glide.with(this)
                    .load(profileImageUrl)
                    .placeholder(R.drawable.ic_person)
                    .error(R.drawable.ic_person)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivProfile);

            // Make profile image clickable
            ivProfile.setClickable(true);
        } else {
            Log.d(TAG, "No profile image available");
            ivProfile.setClickable(false);
        }

        // Load Aadhar front image
        if (customer.aadhar_front != null && !customer.aadhar_front.equals("N/A")) {
            aadharFrontUrl = RetrofitClient.BASE_URL + "uploads/" + customer.aadhar_front;
            Log.d(TAG, "Loading Aadhar front from: " + aadharFrontUrl);

            // Create ImageView for Aadhar front
            ImageView ivAadharFront = new ImageView(this);
            ivAadharFront.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openFullScreenImage(aadharFrontUrl, "Aadhar Front");
                }
            });

            ivAadharFront.setId(View.generateViewId());
            ivAadharFront.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT));
            ivAadharFront.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ivAadharFront.setBackgroundResource(R.color.gray_200);

            // Add to card
            LinearLayout frontContainer = (LinearLayout) cardAadharFront.getChildAt(0);
            frontContainer.removeAllViews();
            frontContainer.addView(ivAadharFront);

            // Load image
            Glide.with(this)
                    .load(aadharFrontUrl)
                    .placeholder(R.drawable.ic_image)
                    .error(R.drawable.ic_image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivAadharFront);
            ivAadharFrontPlaceholder.setVisibility(View.GONE);
            tvAadharFrontText.setVisibility(View.GONE);


            cardAadharFront.setClickable(true);
        } else {
            Log.d(TAG, "No Aadhar front image available");
            cardAadharFront.setClickable(false);
            // Make sure placeholder is visible
            ivAadharFrontPlaceholder.setVisibility(View.VISIBLE);
            tvAadharFrontText.setVisibility(View.VISIBLE);
        }

        // Load Aadhar back image
        if (customer.aadhar_back != null && !customer.aadhar_back.equals("N/A")) {
            aadharBackUrl = RetrofitClient.BASE_URL + "uploads/" + customer.aadhar_back;
            Log.d(TAG, "Loading Aadhar back from: " + aadharBackUrl);

            // Create ImageView for Aadhar back
            ImageView ivAadharBack = new ImageView(this);
            ivAadharBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openFullScreenImage(aadharBackUrl, "Aadhar Back");
                }
            });

            ivAadharBack.setId(View.generateViewId());
            ivAadharBack.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT));
            ivAadharBack.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ivAadharBack.setBackgroundResource(R.color.gray_200);

            // Add to card
            LinearLayout backContainer = (LinearLayout) cardAadharBack.getChildAt(0);
            backContainer.removeAllViews();
            backContainer.addView(ivAadharBack);

            // Load image
            Glide.with(this)
                    .load(aadharBackUrl)
                    .placeholder(R.drawable.ic_image)
                    .error(R.drawable.ic_image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivAadharBack);
            ivAadharBackPlaceholder.setVisibility(View.GONE);
            tvAadharBackText.setVisibility(View.GONE);

            cardAadharBack.setClickable(true);
        } else {
            Log.d(TAG, "No Aadhar back image available");
            cardAadharBack.setClickable(false);
            // Make sure placeholder is visible
            ivAadharBackPlaceholder.setVisibility(View.VISIBLE);
            tvAadharBackText.setVisibility(View.VISIBLE);
        }

        // Show joined date in a Toast
        if (customer.created_at != null && !customer.created_at.equals("N/A")) {
            Toast.makeText(this, "Joined: " + customer.created_at,
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}