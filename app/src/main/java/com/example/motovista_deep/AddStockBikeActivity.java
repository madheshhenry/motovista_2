package com.example.motovista_deep;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.FileProvider;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import com.example.motovista_deep.models.OcrResponse;
import com.example.motovista_deep.api.OcrRetrofitClient;
import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.canhub.cropper.CropImageView;

import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.content.ContextCompat;

import com.example.motovista_deep.api.ApiService;
import com.example.motovista_deep.api.RetrofitClient;
import com.example.motovista_deep.helpers.SharedPrefManager;
import com.example.motovista_deep.models.BikeModel;
import com.example.motovista_deep.models.BikeVariantModel;
import com.example.motovista_deep.models.GenericResponse;
import com.example.motovista_deep.models.GetBikesResponse;
import com.example.motovista_deep.models.InventoryBrand;
import com.example.motovista_deep.models.InventoryResponse;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddStockBikeActivity extends AppCompatActivity {

    private ImageView btnBack;

    // Selection Views
    private TextView tvSelectedBrand, tvSelectedModel, tvSelectedVariant, tvSelectedColor;
    private LinearLayout containerBrand, containerModel, containerVariant, containerColor;
    private View viewColorPreview;
    private ImageView iconEndModel, iconEndVariant, iconEndColor;

    // Quantity Section
    private View btnDecreaseQty, btnIncreaseQty;
    private TextView tvQuantity;

    // Lists
    private RecyclerView rvIdentification;
    private IdentificationAdapter adapter;
    private List<StockEntry> stockEntries = new ArrayList<>();
    private TextView tvActiveUnits;

    // Pending Batch List (TOP)
    private RecyclerView rvPendingStock;
    private PendingStockAdapter pendingAdapter;
    private List<StockBatchItem> pendingList = new ArrayList<>();

    // Footer
    private Button btnAddAnother, btnSaveStock;

    // Data
    private Calendar calendar;
    private List<BikeModel> masterBikeList = new ArrayList<>();
    private List<InventoryBrand> masterBrandList = new ArrayList<>();
    
    // Selection State
    private String selectedBrand = "";
    private String selectedModel = "";
    private String selectedVariant = "";
    private String selectedColor = "";
    private String selectedColorHex = "#CCCCCC";
    
    private int quantity = 1;

    private ProgressDialog progressDialog;

    // OCR & Camera State
    private ActivityResultLauncher<Uri> takePictureLauncher;
    private ActivityResultLauncher<CropImageContractOptions> cropImageLauncher;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private Uri currentPhotoUri;
    private int currentScanPosition = -1;
    private boolean currentScanIsEngine = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_stock_bike);

        initializeViews();
        setupRecyclerViews();
        setupClickListeners();
        
        enableSelection(containerModel, iconEndModel, false);
        enableSelection(containerVariant, iconEndVariant, false);
        enableSelection(containerColor, iconEndColor, false);

        setupCameraLauncher();
        fetchMasterData();
    }

    private void setupCameraLauncher() {
        takePictureLauncher = registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
            if (success) {
                if (currentPhotoUri != null) {
                    launchImageCropper(currentPhotoUri);
                } else {
                    Toast.makeText(this, "Photo URI is null", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Camera capture cancelled", Toast.LENGTH_SHORT).show();
            }
        });

        cropImageLauncher = registerForActivityResult(new CropImageContract(), result -> {
            if (result.isSuccessful() && result.getUriContent() != null) {
                performOcr(result.getUriContent());
            } else if (result.getError() != null) {
                Toast.makeText(this, "Cropping failed: " + result.getError().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                // Permission granted, restart the process that triggered it
                if (currentScanPosition != -1) {
                    startScanning(currentScanPosition, currentScanIsEngine);
                }
            } else {
                Toast.makeText(this, "Camera permission is required to scan", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void launchImageCropper(Uri uri) {
        try {
            CropImageOptions cropOptions = new CropImageOptions();
            cropOptions.guidelines = CropImageView.Guidelines.ON;
            cropOptions.activityTitle = "Confirm OCR Crop";
            cropOptions.cropMenuCropButtonTitle = "SCAN NOW";
            
            cropOptions.allowRotation = true;
            cropOptions.allowFlipping = true;
            cropOptions.autoZoomEnabled = true;

            CropImageContractOptions options = new CropImageContractOptions(uri, cropOptions);
            cropImageLauncher.launch(options);
        } catch (Exception e) {
            Toast.makeText(this, "Error launching cropper: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void performOcr(Uri imageUri) {
        progressDialog.setMessage("Scanning with AI...");
        progressDialog.show();

        try {
            File tempFile = new File(getExternalFilesDir(null), "ocr_upload_temp.jpg");
            if (copyUriToFile(imageUri, tempFile)) {
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), tempFile);
                MultipartBody.Part body = MultipartBody.Part.createFormData("image", tempFile.getName(), requestFile);

                OcrRetrofitClient.getOcrApiService().scanEngineChassis(body).enqueue(new Callback<OcrResponse>() {
                    @Override
                    public void onResponse(Call<OcrResponse> call, Response<OcrResponse> response) {
                        progressDialog.dismiss();
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            String recognizedText = response.body().getText();
                            if (currentScanPosition != -1) {
                                StockEntry entry = stockEntries.get(currentScanPosition);
                                if (currentScanIsEngine) {
                                    entry.engine = recognizedText;
                                } else {
                                    entry.chassis = recognizedText;
                                }
                                adapter.notifyItemChanged(currentScanPosition);
                            }
                        } else {
                            Toast.makeText(AddStockBikeActivity.this, "OCR Failed: " + (response.body() != null ? response.body().getMessage() : "Server Error"), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<OcrResponse> call, Throwable t) {
                        progressDialog.dismiss();
                        Toast.makeText(AddStockBikeActivity.this, "OCR Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                progressDialog.dismiss();
                Toast.makeText(this, "Failed to prepare image for upload", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            progressDialog.dismiss();
            Toast.makeText(this, "File Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean copyUriToFile(Uri uri, File destFile) {
        try (java.io.InputStream inputStream = getContentResolver().openInputStream(uri);
             java.io.FileOutputStream outputStream = new java.io.FileOutputStream(destFile)) {
            if (inputStream == null) return false;
            byte[] buffer = new byte[8192];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void startScanning(int position, boolean isEngine) {
        this.currentScanPosition = position;
        this.currentScanIsEngine = isEngine;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
            return;
        }

        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            Toast.makeText(this, "Error creating file", Toast.LENGTH_SHORT).show();
            return;
        }

        if (photoFile != null) {
            currentPhotoUri = FileProvider.getUriForFile(this,
                    "com.example.motovista_deep.provider",
                    photoFile);
            takePictureLauncher.launch(currentPhotoUri);
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "OCR_" + timeStamp + ".jpg";
        File storageDir = getExternalFilesDir(null);
        return new File(storageDir, imageFileName);
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);

        tvSelectedBrand = findViewById(R.id.tvSelectedBrand);
        tvSelectedModel = findViewById(R.id.tvSelectedModel);
        tvSelectedVariant = findViewById(R.id.tvSelectedVariant);
        tvSelectedColor = findViewById(R.id.tvSelectedColor);
        viewColorPreview = findViewById(R.id.viewColorPreview);

        containerBrand = findViewById(R.id.containerBrand);
        containerModel = findViewById(R.id.containerModel);
        containerVariant = findViewById(R.id.containerVariant);
        containerColor = findViewById(R.id.containerColor);
        
        iconEndModel = findViewById(R.id.iconEndModel);
        iconEndVariant = findViewById(R.id.iconEndVariant);
        iconEndColor = findViewById(R.id.iconEndColor);

        btnDecreaseQty = findViewById(R.id.btnDecreaseQty);
        btnIncreaseQty = findViewById(R.id.btnIncreaseQty);
        tvQuantity = findViewById(R.id.tvQuantity);

        rvIdentification = findViewById(R.id.rvIdentification);
        tvActiveUnits = findViewById(R.id.tvActiveUnits);
        
        rvPendingStock = findViewById(R.id.rvPendingStock);

        btnAddAnother = findViewById(R.id.btnAddAnother);
        btnSaveStock = findViewById(R.id.btnSaveStock);

        calendar = Calendar.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
    }
    
    private void enableSelection(LinearLayout container, ImageView icon, boolean enabled) {
        container.setClickable(enabled);
        container.setAlpha(enabled ? 1.0f : 0.5f);
        if (icon != null) {
            icon.setImageResource(enabled ? R.drawable.ic_search : R.drawable.ic_lock);
        }
    }

    private void setupRecyclerViews() {
        stockEntries.clear();
        stockEntries.add(new StockEntry());

        adapter = new IdentificationAdapter(stockEntries);
        rvIdentification.setLayoutManager(new LinearLayoutManager(this));
        rvIdentification.setAdapter(adapter);
        
        // Pending List
        pendingAdapter = new PendingStockAdapter(pendingList, this::editPendingItem);
        rvPendingStock.setLayoutManager(new LinearLayoutManager(this));
        rvPendingStock.setAdapter(pendingAdapter);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnDecreaseQty.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                updateQuantityUI();
            }
        });

        btnIncreaseQty.setOnClickListener(v -> {
            if (quantity < 50) {
                quantity++;
                updateQuantityUI();
            } else {
                Toast.makeText(this, "Max limit reached", Toast.LENGTH_SHORT).show();
            }
        });

        btnAddAnother.setOnClickListener(v -> {
             if (validateInputs()) {
                 addCurrentToPending();
             }
        });

        btnSaveStock.setOnClickListener(v -> {
            // Check if current form has data
            if (!stockEntries.get(0).engine.isEmpty() || !selectedBrand.isEmpty()) {
                if (validateInputs()) {
                    addCurrentToPending(); // Move current to pending queue
                    startBatchUpload();
                }
            } else {
                // Current form empty, just save pending
                if (pendingList.isEmpty()) {
                    Toast.makeText(this, "No stock to save", Toast.LENGTH_SHORT).show();
                } else {
                    startBatchUpload();
                }
            }
        });
        
        setupSelectionListeners();
    }
    
    private void setupSelectionListeners() {
        containerBrand.setOnClickListener(v -> showBrandDialog());
        containerModel.setOnClickListener(v -> showModelDialog());
        containerVariant.setOnClickListener(v -> showVariantDialog());
        containerColor.setOnClickListener(v -> showColorDialog());
    }
    
    // --- Logic: Add to Pending Queue ---
    
    private void addCurrentToPending() {
        StockBatchItem newItem = new StockBatchItem();
        newItem.brand = selectedBrand;
        newItem.model = selectedModel;
        newItem.variant = selectedVariant;
        newItem.color = selectedColor;
        newItem.colorHex = selectedColorHex;
        newItem.entries = new ArrayList<>();
        
        // Deep copy entries
        for (StockEntry e : stockEntries) {
            StockEntry copy = new StockEntry();
            copy.engine = e.engine;
            copy.chassis = e.chassis;
            newItem.entries.add(copy);
        }
        
        pendingList.add(newItem);
        pendingAdapter.notifyItemInserted(pendingList.size() - 1);
        
        resetForm();
        
        // Scroll to top
        findViewById(R.id.mainScrollView).scrollTo(0, 0);
        Toast.makeText(this, "Added to queue. Fill next bike.", Toast.LENGTH_SHORT).show();
    }
    
    private void editPendingItem(int position) {
        if (position < 0 || position >= pendingList.size()) return;
        
        // Restore Data
        StockBatchItem item = pendingList.get(position);
        
        selectedBrand = item.brand;
        selectedModel = item.model;
        selectedVariant = item.variant;
        selectedColor = item.color;
        selectedColorHex = item.colorHex; // Restore Hex check?
        
        tvSelectedBrand.setText(selectedBrand);
        tvSelectedModel.setText(selectedModel);
        tvSelectedVariant.setText(selectedVariant);
        tvSelectedColor.setText(selectedColor);
        
        try {
            String hex = selectedColorHex.trim(); 
            if(!hex.startsWith("#")) hex = "#"+hex;
            viewColorPreview.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor(hex)));
        } catch(Exception e) {
             viewColorPreview.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#CCCCCC")));
        }
        
        enableSelection(containerModel, iconEndModel, true);
        enableSelection(containerVariant, iconEndVariant, true);
        enableSelection(containerColor, iconEndColor, true);

        // Restore Entries
        stockEntries.clear();
        for (StockEntry e : item.entries) {
            StockEntry copy = new StockEntry();
            copy.engine = e.engine;
            copy.chassis = e.chassis;
            stockEntries.add(copy);
        }
        quantity = stockEntries.size();
        
        // Update UI
        adapter.notifyDataSetChanged();
        updateQuantityUI();
        
        // Remove from pending
        pendingList.remove(position);
        pendingAdapter.notifyItemRemoved(position);
    }

    // --- Logic: Batch Upload ---
    
    private void startBatchUpload() {
        if (pendingList.isEmpty()) return;
        
        progressDialog.setMessage("Saving All Stock...");
        progressDialog.show();
        
        uploadNextBatchItem(0);
    }
    
    private void uploadNextBatchItem(int batchIndex) {
        if (batchIndex >= pendingList.size()) {
            progressDialog.dismiss();
            Toast.makeText(this, "Batch Upload Complete!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        
        StockBatchItem batchItem = pendingList.get(batchIndex);
        uploadNextEntryInBatch(batchIndex, batchItem, 0);
    }
    
    private void uploadNextEntryInBatch(int batchIndex, StockBatchItem batchItem, int entryIndex) {
        if (entryIndex >= batchItem.entries.size()) {
            // Done with this batch item, move to next
            uploadNextBatchItem(batchIndex + 1);
            return;
        }
        
        StockEntry entry = batchItem.entries.get(entryIndex);
        
        progressDialog.setMessage("Saving Batch " + (batchIndex+1) + " (Item " + (entryIndex+1) + ")...");

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String date = day + "-" + month + "-" + year;
        
        String colorsJson = new Gson().toJson(Collections.singletonList(batchItem.color));
        String token = SharedPrefManager.getInstance(this).getToken();
        
        RetrofitClient.getApiService().addStockBike(
            "Bearer " + token, 
            batchItem.brand, batchItem.model, batchItem.variant, 
            colorsJson, entry.engine, entry.chassis, date
        ).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    uploadNextEntryInBatch(batchIndex, batchItem, entryIndex + 1);
                } else {
                    progressDialog.dismiss();
                    String msg = (response.body() != null) ? response.body().getMessage() : "Failed";
                    Toast.makeText(AddStockBikeActivity.this, "Error: " + msg, Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(AddStockBikeActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --- Helper Logic (UI, Dialogs, Data Fetch) ---

    private void showBrandDialog() {
        if (masterBrandList == null) masterBrandList = new ArrayList<>();
        if (masterBikeList == null) masterBikeList = new ArrayList<>();
        List<SelectionItem> items = new ArrayList<>();
        for (InventoryBrand b : masterBrandList) {
             if (b.getBrand() != null && !b.getBrand().isEmpty()) items.add(new SelectionItem(b.getBrand()));
        }
        if (items.isEmpty()) {
             Set<String> brands = new HashSet<>();
             for (BikeModel bike : masterBikeList) if (bike.getBrand() != null) brands.add(bike.getBrand());
             for(String b : brands) items.add(new SelectionItem(b));
        }
        Collections.sort(items, (o1, o2) -> o1.name.compareTo(o2.name));
        showSelectionDialog("Select Brand", items, item -> {
            selectedBrand = item.name;
            tvSelectedBrand.setText(selectedBrand);
            resetSelection(tvSelectedModel, "Select Model");
            resetSelection(tvSelectedVariant, "Select Variant");
            resetSelection(tvSelectedColor, "Select Color");
            selectedModel = ""; selectedVariant = ""; selectedColor = "";
            viewColorPreview.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#CBD5E1")));
            enableSelection(containerModel, iconEndModel, true);
            enableSelection(containerVariant, iconEndVariant, false);
            enableSelection(containerColor, iconEndColor, false);
        });
    }

    private void showModelDialog() {
        Set<String> models = new HashSet<>();
        for (BikeModel bike : masterBikeList) {
            if (selectedBrand.equalsIgnoreCase(bike.getBrand()) && bike.getModel() != null) models.add(bike.getModel());
        }
        // if (models.isEmpty()) Toast.makeText(this, "No models found", Toast.LENGTH_SHORT).show();
        List<SelectionItem> items = new ArrayList<>();
        List<String> sortedModels = new ArrayList<>(models);
        Collections.sort(sortedModels);
        for(String m : sortedModels) items.add(new SelectionItem(m));
        
        showSelectionDialog("Select Model", items, item -> {
            selectedModel = item.name;
            tvSelectedModel.setText(selectedModel);
            resetSelection(tvSelectedVariant, "Select Variant");
            resetSelection(tvSelectedColor, "Select Color");
            selectedVariant = ""; selectedColor = "";
            viewColorPreview.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#CBD5E1")));
            enableSelection(containerVariant, iconEndVariant, true);
            enableSelection(containerColor, iconEndColor, false);
        });
    }

    private void showVariantDialog() {
        Set<String> variants = new HashSet<>();
        for (BikeModel bike : masterBikeList) {
            if (selectedBrand.equalsIgnoreCase(bike.getBrand()) && selectedModel.equalsIgnoreCase(bike.getModel())) {
                if (bike.getVariants() != null && !bike.getVariants().isEmpty()) {
                    for (BikeVariantModel v : bike.getVariants()) if (v.variantName != null) variants.add(v.variantName);
                } else if (bike.getVariant() != null) variants.add(bike.getVariant());
            }
        }
        List<SelectionItem> items = new ArrayList<>();
        List<String> sortedVariants = new ArrayList<>(variants);
        Collections.sort(sortedVariants);
        for(String v : sortedVariants) items.add(new SelectionItem(v));
        
        showSelectionDialog("Select Variant", items, item -> {
            selectedVariant = item.name;
            tvSelectedVariant.setText(selectedVariant);
            resetSelection(tvSelectedColor, "Select Color");
            selectedColor = "";
            viewColorPreview.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#CBD5E1")));
            enableSelection(containerColor, iconEndColor, true);
        });
    }
    
    private void showColorDialog() {
        List<SelectionItem> items = new ArrayList<>();
        Set<String> uniqueColors = new HashSet<>();
        for (BikeModel bike : masterBikeList) {
             if (selectedBrand.equalsIgnoreCase(bike.getBrand()) && selectedModel.equalsIgnoreCase(bike.getModel())) {
                 boolean foundInVariants = false;
                 if (bike.getVariants() != null) {
                     for (BikeVariantModel v : bike.getVariants()) {
                         if (selectedVariant.equalsIgnoreCase(v.variantName) && v.colors != null) {
                             for (BikeVariantModel.VariantColor c : v.colors) {
                                 String key = c.colorName + "|" + (c.colorHex!=null?c.colorHex:"#CCCCCC");
                                 if (uniqueColors.add(key)) items.add(new SelectionItem(c.colorName, c.colorHex, key));
                             }
                             foundInVariants = true;
                         }
                     }
                 }
                 if (!foundInVariants && bike.getColors() != null && bike.getVariant().contains(selectedVariant)) { 
                      for (String cStr : bike.getColors()) {
                           if (uniqueColors.add(cStr)) {
                               String name = cStr, hex = "#CCCCCC";
                               if (cStr.contains("|")) { String[] parts=cStr.split("\\|"); name=parts[0]; if(parts.length>1) hex=parts[1]; }
                               items.add(new SelectionItem(name, hex, cStr));
                           }
                      }
                 }
            }
        }
        Collections.sort(items, (o1, o2) -> o1.name.compareTo(o2.name));
        showSelectionDialog("Select Color", items, item -> {
            selectedColor = (item.extraData != null) ? item.extraData : item.name;
            tvSelectedColor.setText(item.name);
            selectedColorHex = (item.description != null) ? item.description : "#CCCCCC";
            try {
                String hex = selectedColorHex.trim(); if (!hex.startsWith("#")) hex="#"+hex;
                viewColorPreview.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor(hex)));
            } catch (Exception e) {
                viewColorPreview.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#CCCCCC")));
            }
        });
    }

    private void resetSelection(TextView view, String placeholder) { view.setText(placeholder); }
    
    private void resetForm() {
        quantity = 1;
        stockEntries.clear();
        stockEntries.add(new StockEntry());
        adapter.notifyDataSetChanged();
        updateQuantityUI();
        
        selectedBrand = "";
        selectedModel = "";
        selectedVariant = "";
        selectedColor = "";
        selectedColorHex = "#CCCCCC";
        tvSelectedBrand.setText("Select Brand");
        resetSelection(tvSelectedModel, "Select Model");
        resetSelection(tvSelectedVariant, "Select Variant");
        resetSelection(tvSelectedColor, "Select Color");
        try { viewColorPreview.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#CBD5E1"))); } catch (Exception e) {}
        enableSelection(containerModel, iconEndModel, false);
        enableSelection(containerVariant, iconEndVariant, false);
        enableSelection(containerColor, iconEndColor, false);
    }

    private boolean validateInputs() {
        if (selectedBrand.isEmpty() || selectedModel.isEmpty() || selectedVariant.isEmpty() || selectedColor.isEmpty()) {
            Toast.makeText(this, "Please select all product details", Toast.LENGTH_SHORT).show(); return false;
        }
        Set<String> engineSet = new HashSet<>();
        Set<String> chassisSet = new HashSet<>();
        for (int i = 0; i < stockEntries.size(); i++) {
            StockEntry entry = stockEntries.get(i);
            if (entry.chassis.trim().isEmpty() || entry.engine.trim().isEmpty()) { Toast.makeText(this, "Fill all details for Item " + (i+1), Toast.LENGTH_SHORT).show(); return false; }
            if (!engineSet.add(entry.engine.trim())) { Toast.makeText(this, "Duplicate Engine No.", Toast.LENGTH_SHORT).show(); return false; }
            if (!chassisSet.add(entry.chassis.trim())) { Toast.makeText(this, "Duplicate Chassis No.", Toast.LENGTH_SHORT).show(); return false; }
        }
        return true;
    }

    private void updateQuantityUI() {
        tvQuantity.setText(String.format("%02d", quantity));
        tvActiveUnits.setText(quantity + " Units Active");
        int currentSize = stockEntries.size();
        if (quantity > currentSize) {
            for (int i = currentSize; i < quantity; i++) stockEntries.add(new StockEntry());
            adapter.notifyItemRangeInserted(currentSize, quantity - currentSize);
        } else if (quantity < currentSize) {
            int removeCount = currentSize - quantity;
            for (int i = 0; i < removeCount; i++) stockEntries.remove(stockEntries.size() - 1);
            adapter.notifyItemRangeRemoved(quantity, removeCount);
        }
    }
    
    private void showSelectionDialog(String title, List<SelectionItem> items, SelectionListener listener) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); dialog.setContentView(R.layout.dialog_searchable_selection);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView tvTitle = dialog.findViewById(R.id.tvDialogTitle); tvTitle.setText(title);
        EditText etSearch = dialog.findViewById(R.id.etSearch);
        RecyclerView rvList = dialog.findViewById(R.id.rvSelectionList);
        SelectionAdapter selectionAdapter = new SelectionAdapter(items, listener, dialog);
        rvList.setLayoutManager(new LinearLayoutManager(this)); rvList.setAdapter(selectionAdapter);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { selectionAdapter.filter(s.toString()); }
            @Override public void afterTextChanged(Editable s) {}
        });
        dialog.findViewById(R.id.btnCloseDialog).setOnClickListener(v->dialog.dismiss());
        dialog.show();
    }
    
    private void fetchMasterData() {
        progressDialog.setMessage("Loading Catalog...");
        progressDialog.show();
        String token = SharedPrefManager.getInstance(this).getToken();
        ApiService apiService = RetrofitClient.getApiService();
        apiService.getBrands("Bearer " + token).enqueue(new Callback<InventoryResponse>() {
            @Override public void onResponse(Call<InventoryResponse> call, Response<InventoryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    masterBrandList = response.body().getData();
                    if (masterBrandList == null) masterBrandList = new ArrayList<>();
                } else {
                    masterBrandList = new ArrayList<>();
                }
                fetchModels(apiService, token);
            }
            @Override public void onFailure(Call<InventoryResponse> call, Throwable t) { masterBrandList = new ArrayList<>(); fetchModels(apiService, token); }
        });
    }
    
    private void fetchModels(ApiService apiService, String token) {
        apiService.getNewBikes("Bearer " + token).enqueue(new Callback<GetBikesResponse>() {
            @Override public void onResponse(Call<GetBikesResponse> call, Response<GetBikesResponse> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    masterBikeList = response.body().getData();
                    if (masterBikeList == null) masterBikeList = new ArrayList<>();
                } else {
                    masterBikeList = new ArrayList<>();
                }
            }
            @Override public void onFailure(Call<GetBikesResponse> call, Throwable t) { progressDialog.dismiss(); masterBikeList = new ArrayList<>(); }
        });
    }
    
    // --- Inner Classes ---

    private static class StockEntry { String chassis = ""; String engine = ""; }
    
    private static class StockBatchItem {
        String brand, model, variant, color, colorHex;
        List<StockEntry> entries;
    }

    interface PendingItemListener { void onItemClick(int position); }

    private class PendingStockAdapter extends RecyclerView.Adapter<PendingStockAdapter.ViewHolder> {
        List<StockBatchItem> list;
        PendingItemListener listener;
        PendingStockAdapter(List<StockBatchItem> list, PendingItemListener listener) { this.list=list; this.listener=listener; }
        @NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pending_stock, parent, false));
        }
        @Override public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            StockBatchItem item = list.get(position);
            holder.tvTitle.setText(item.brand + " " + item.model);
            holder.tvSub.setText(item.variant + " • " + item.color);
            holder.tvCount.setText(item.entries.size() + " Unit(s)");
            holder.itemView.setOnClickListener(v -> listener.onItemClick(position));
        }
        @Override public int getItemCount() { return list.size(); }
        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle, tvSub, tvCount;
            ViewHolder(View v) { super(v); tvTitle=v.findViewById(R.id.tvPendBrandModel); tvSub=v.findViewById(R.id.tvPendVariantColor); tvCount=v.findViewById(R.id.tvPendChassisCount); }
        }
    }

    private static class SelectionItem { String name, description, extraData; SelectionItem(String name) { this.name=name; } SelectionItem(String name, String desc, String extra) { this.name=name; this.description=desc; this.extraData=extra; } }
    interface SelectionListener { void onItemSelected(SelectionItem item); }
    private class SelectionAdapter extends RecyclerView.Adapter<SelectionAdapter.ViewHolder> {
        List<SelectionItem> o, f; SelectionListener l; Dialog d;
        SelectionAdapter(List<SelectionItem> list, SelectionListener l, Dialog d) { this.o=list; this.f=new ArrayList<>(list); this.l=l; this.d=d; }
        void filter(String q) { f.clear(); if(q.isEmpty()) f.addAll(o); else for(SelectionItem i:o) if(i.name.toLowerCase().contains(q.toLowerCase())) f.add(i); notifyDataSetChanged(); }
        @NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup p, int t) { return new ViewHolder(LayoutInflater.from(p.getContext()).inflate(R.layout.item_selection_row, p, false)); }
        @Override public void onBindViewHolder(@NonNull ViewHolder h, int p) {
            SelectionItem i = f.get(p); h.tv.setText(i.name);
            if(i.description!=null) { h.chip.setVisibility(View.VISIBLE); try{h.chip.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor(i.description)));}catch(Exception e){h.chip.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.LTGRAY));} } else h.chip.setVisibility(View.GONE);
            h.itemView.setOnClickListener(v->{l.onItemSelected(i); d.dismiss();});
        }
        @Override public int getItemCount() { return f.size(); }
        class ViewHolder extends RecyclerView.ViewHolder { TextView tv; View chip; ViewHolder(View v){super(v); tv=v.findViewById(R.id.tvItemName); chip=v.findViewById(R.id.viewColorChip);} }
    }
    
    private class IdentificationAdapter extends RecyclerView.Adapter<IdentificationAdapter.ViewHolder> {
        private final List<StockEntry> entries;
        public IdentificationAdapter(List<StockEntry> entries) { this.entries = entries; }
        @NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_identification_input, parent, false));
        }
        @Override public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            StockEntry entry = entries.get(position);
            holder.tvRowNumber.setText("Bike #" + (position + 1));
            holder.etChassis.removeTextChangedListener(holder.chassisWatcher);
            holder.etEngine.removeTextChangedListener(holder.engineWatcher);
            holder.etChassis.setText(entry.chassis);
            holder.etEngine.setText(entry.engine);
            holder.chassisWatcher = new SimpleTextWatcher(s -> entry.chassis = s.toString());
            holder.etChassis.addTextChangedListener(holder.chassisWatcher);
            holder.engineWatcher = new SimpleTextWatcher(s -> entry.engine = s.toString());
            holder.etEngine.addTextChangedListener(holder.engineWatcher);

            // OCR Scan Listeners
            holder.btnScanChassis.setOnClickListener(v -> startScanning(position, false));
            holder.btnScanEngine.setOnClickListener(v -> startScanning(position, true));
        }
        @Override public int getItemCount() { return entries.size(); }
        class ViewHolder extends RecyclerView.ViewHolder { 
            TextView tvRowNumber; 
            EditText etChassis, etEngine; 
            View btnScanChassis, btnScanEngine;
            TextWatcher chassisWatcher, engineWatcher; 
            ViewHolder(View v) { 
                super(v); 
                tvRowNumber=v.findViewById(R.id.tvRowNumber); 
                etChassis=v.findViewById(R.id.etChassisInput); 
                etEngine=v.findViewById(R.id.etEngineInput);
                btnScanChassis=v.findViewById(R.id.btnScanChassisRow);
                btnScanEngine=v.findViewById(R.id.btnScanEngineRow);
            } 
        }
    }
    private interface TextListener { void onTextChanged(CharSequence s); }
    private static class SimpleTextWatcher implements TextWatcher {
        private final TextListener l; SimpleTextWatcher(TextListener l) { this.l=l; }
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) { l.onTextChanged(s); }
        @Override public void afterTextChanged(Editable s) {}
    }
}
