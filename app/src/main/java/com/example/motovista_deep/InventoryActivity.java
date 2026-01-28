package com.example.motovista_deep;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.motovista_deep.adapter.InventoryBrandAdapter;
import com.example.motovista_deep.api.ApiService;
import com.example.motovista_deep.api.RetrofitClient;
import com.example.motovista_deep.helpers.SharedPrefManager;
import com.example.motovista_deep.models.GenericResponse;
import com.example.motovista_deep.models.InventoryBrand;
import com.example.motovista_deep.models.InventoryResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InventoryActivity extends AppCompatActivity {

    private RecyclerView rvInventory;
    private ProgressBar progressBar;
    private ImageView btnBack;
    private InventoryBrandAdapter adapter;
    private List<InventoryBrand> brandList = new ArrayList<>();

    // Bottom Navigation
    private android.widget.LinearLayout tabDashboard, tabInventory, tabBikes, tabCustomers, tabSettings;
    private ImageView ivDashboard, ivInventory, ivBikes, ivCustomers, ivSettings;
    private TextView tvDashboard, tvInventory, tvBikes, tvCustomers, tvSettings;

    // Image Upload
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private Uri selectedImageUri;
    private ImageView ivDialogPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        rvInventory = findViewById(R.id.rvInventory);
        progressBar = findViewById(R.id.progressBar);
        btnBack = findViewById(R.id.btnBack);

        // Initialize Image Picker
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        if (ivDialogPreview != null) {
                            ivDialogPreview.setImageURI(selectedImageUri);
                        }
                    }
                }
        );

        rvInventory.setLayoutManager(new GridLayoutManager(this, 2));
        
        adapter = new InventoryBrandAdapter(this, brandList, this::showAddBrandDialog);
        rvInventory.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());

        setupBottomNavigation();
        loadInventory();
    }

    private void setupBottomNavigation() {
        tabDashboard = findViewById(R.id.tabDashboard);
        tabInventory = findViewById(R.id.tabInventory);
        tabBikes = findViewById(R.id.tabBikes);
        tabCustomers = findViewById(R.id.tabCustomers);
        tabSettings = findViewById(R.id.tabSettings);

        ivDashboard = (ImageView) tabDashboard.getChildAt(0);
        ivInventory = (ImageView) tabInventory.getChildAt(0);
        ivBikes = (ImageView) tabBikes.getChildAt(0);
        ivCustomers = (ImageView) tabCustomers.getChildAt(0);
        ivSettings = (ImageView) tabSettings.getChildAt(0);

        tvDashboard = (TextView) tabDashboard.getChildAt(1);
        tvInventory = (TextView) tabInventory.getChildAt(1);
        tvBikes = (TextView) tabBikes.getChildAt(1);
        tvCustomers = (TextView) tabCustomers.getChildAt(1);
        tvSettings = (TextView) tabSettings.getChildAt(1);

        setActiveTab(tabInventory);

        tabDashboard.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminDashboardActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        tabInventory.setOnClickListener(v -> {
            // Already here, maybe refresh
            loadInventory();
        });

        tabBikes.setOnClickListener(v -> {
            Intent intent = new Intent(this, BikeInventoryActivity.class);
            startActivity(intent);
            finish();
        });

        tabCustomers.setOnClickListener(v -> {
            Intent intent = new Intent(this, CustomersActivity.class);
            startActivity(intent);
            finish();
        });

        tabSettings.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminSettingsActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void setActiveTab(android.widget.LinearLayout activeTab) {
        resetAllTabs();
        int primaryColor = androidx.core.content.ContextCompat.getColor(this, R.color.primary_color);

        if (activeTab == tabDashboard) {
            ivDashboard.setColorFilter(primaryColor);
            tvDashboard.setTextColor(primaryColor);
            tvDashboard.setTypeface(tvDashboard.getTypeface(), android.graphics.Typeface.BOLD);
        } else if (activeTab == tabInventory) {
            ivInventory.setColorFilter(primaryColor);
            tvInventory.setTextColor(primaryColor);
            tvInventory.setTypeface(tvInventory.getTypeface(), android.graphics.Typeface.BOLD);
        } else if (activeTab == tabBikes) {
            ivBikes.setColorFilter(primaryColor);
            tvBikes.setTextColor(primaryColor);
            tvBikes.setTypeface(tvBikes.getTypeface(), android.graphics.Typeface.BOLD);
        } else if (activeTab == tabCustomers) {
            ivCustomers.setColorFilter(primaryColor);
            tvCustomers.setTextColor(primaryColor);
            tvCustomers.setTypeface(tvCustomers.getTypeface(), android.graphics.Typeface.BOLD);
        } else if (activeTab == tabSettings) {
            ivSettings.setColorFilter(primaryColor);
            tvSettings.setTextColor(primaryColor);
            tvSettings.setTypeface(tvSettings.getTypeface(), android.graphics.Typeface.BOLD);
        }
    }

    private void resetAllTabs() {
        int grayColor = androidx.core.content.ContextCompat.getColor(this, R.color.gray_400);

        ivDashboard.setColorFilter(grayColor);
        tvDashboard.setTextColor(grayColor);
        tvDashboard.setTypeface(null, android.graphics.Typeface.NORMAL);

        ivInventory.setColorFilter(grayColor);
        tvInventory.setTextColor(grayColor);
        tvInventory.setTypeface(null, android.graphics.Typeface.NORMAL);

        ivBikes.setColorFilter(grayColor);
        tvBikes.setTextColor(grayColor);
        tvBikes.setTypeface(null, android.graphics.Typeface.NORMAL);

        ivCustomers.setColorFilter(grayColor);
        tvCustomers.setTextColor(grayColor);
        tvCustomers.setTypeface(null, android.graphics.Typeface.NORMAL);

        ivSettings.setColorFilter(grayColor);
        tvSettings.setTextColor(grayColor);
        tvSettings.setTypeface(null, android.graphics.Typeface.NORMAL);
    }

    private void showAddBrandDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Brand");

        View view = getLayoutInflater().inflate(R.layout.dialog_add_brand, null);
        final EditText etBrandName = view.findViewById(R.id.etBrandName);
        ivDialogPreview = view.findViewById(R.id.ivBrandLogo);
        Button btnSelectImage = view.findViewById(R.id.btnSelectImage);
        btnSelectImage.setVisibility(View.VISIBLE);

        selectedImageUri = null; // Reset selection

        btnSelectImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        builder.setView(view);

        builder.setPositiveButton("Add", (dialog, which) -> {
            // Prevent auto close? No, standard wrapper.
            // We just handle click.
        });
        
        // We override positive button listener later to prevent close on error, but standard is fine for now.
        // Actually, let's just use standard for simplicity.

        AlertDialog dialog = builder.create();
        dialog.show();
        
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String name = etBrandName.getText().toString().trim();
            if (!name.isEmpty()) {
                if (selectedImageUri != null) {
                    dialog.dismiss();
                    uploadNewBrand(name, selectedImageUri);
                } else {
                    Toast.makeText(InventoryActivity.this, "Please select an image", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(InventoryActivity.this, "Brand Name Required", Toast.LENGTH_SHORT).show();
            }
        });
        
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(v -> dialog.dismiss());
    }

    private void uploadNewBrand(String name, Uri imageUri) {
        progressBar.setVisibility(View.VISIBLE);
        String token = SharedPrefManager.getInstance(this).getToken();
        ApiService apiService = RetrofitClient.getApiService();

        File file = getFileFromUri(imageUri);
        if (file == null) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Error processing image", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(imageUri)), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("brand_logo", file.getName(), requestFile);
        RequestBody brandName = RequestBody.create(MultipartBody.FORM, name);

        apiService.addBrand("Bearer " + token, brandName, body).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(InventoryActivity.this, "Brand Added Successfully!", Toast.LENGTH_SHORT).show();
                    loadInventory();
                } else {
                    Toast.makeText(InventoryActivity.this, "Failed: " + (response.body() != null ? response.body().getMessage() : "Unknown"), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(InventoryActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private File getFileFromUri(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            File tempFile = new File(getCacheDir(), "brand_logo_" + System.currentTimeMillis() + ".jpg");
            OutputStream outputStream = new FileOutputStream(tempFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
            inputStream.close();
            return tempFile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void loadInventory() {
        progressBar.setVisibility(View.VISIBLE);
        String token = SharedPrefManager.getInstance(this).getToken();
        ApiService apiService = RetrofitClient.getApiService();

        apiService.getBrands("Bearer " + token).enqueue(new Callback<InventoryResponse>() {
            @Override
            public void onResponse(Call<InventoryResponse> call, Response<InventoryResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    List<InventoryBrand> data = response.body().getData();
                    if (data != null) {
                        brandList.clear();
                        brandList.addAll(data);
                        
                        if (adapter == null) {
                            adapter = new InventoryBrandAdapter(InventoryActivity.this, brandList, InventoryActivity.this::showAddBrandDialog);
                            rvInventory.setAdapter(adapter);
                        } else {
                            // Efficiently update existing adapter
                            adapter.notifyDataSetChanged();
                        }
                    }
                } else {
                    Toast.makeText(InventoryActivity.this, "Failed to load brands", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<InventoryResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(InventoryActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}