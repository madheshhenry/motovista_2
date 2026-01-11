package com.example.motovista_deep;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.motovista_deep.api.ApiService;
import com.example.motovista_deep.api.RetrofitClient;
import com.example.motovista_deep.helpers.SharedPrefManager;
import com.example.motovista_deep.models.GenericResponse;
import com.example.motovista_deep.models.InventoryBrand;
import com.example.motovista_deep.models.InventoryResponse;
import com.example.motovista_deep.views.ColorPickerView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddStockBikeActivity extends AppCompatActivity {

    private ImageView btnBack;
    
    // Session State
    private LinearLayout containerSavedGroups;
    private TextView tvSessionTotal;
    private Button btnFinishSession;
    private LinearLayout layoutAddAnother; // Visual cue
    private int sessionTotalBikes = 0;

    // Group Details (Active Form)
    private Spinner spinnerBrand;
    private EditText etModel, etVariant;
    private LinearLayout btnSelectColor;
    private TextView tvSelectedColorName;
    private View viewSelectedColorCircle;
    private TextView tvInwardDate;
    private TextView tvGroupHeader;
    
    // Quantity Section
    private ImageView btnDecreaseQty, btnIncreaseQty;
    private TextView tvQuantity;
    private TextView tvEntriesRequired;
    
    // Dynamic List
    private RecyclerView rvIdentification;
    private IdentificationAdapter adapter;
    private List<StockEntry> stockEntries = new ArrayList<>();
    
    // Summary & Actions
    private TextView tvBatchSummary;
    private Button btnSaveGroup;
    
    // Data
    private Calendar calendar;
    private List<String> brandList = new ArrayList<>();
    private String selectedColorName = "Select Color";
    private String selectedColorHex = "#CCCCCC";
    private boolean isColorSelected = false;
    private int quantity = 1;
    
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_stock_bike);

        initializeViews();
        setupRecyclerView();
        setupClickListeners();
        setDefaultDate();
        fetchBrands();
        updateBatchSummary();
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        
        containerSavedGroups = findViewById(R.id.containerSavedGroups);
        tvSessionTotal = findViewById(R.id.tvSessionTotal);
        btnFinishSession = findViewById(R.id.btnFinishSession);
        layoutAddAnother = findViewById(R.id.layoutAddAnother);
        
        tvGroupHeader = findViewById(R.id.tvGroupHeader);
        spinnerBrand = findViewById(R.id.spinnerBrand);
        etModel = findViewById(R.id.etModel);
        etVariant = findViewById(R.id.etVariant);
        
        btnSelectColor = findViewById(R.id.btnSelectColor);
        tvSelectedColorName = findViewById(R.id.tvSelectedColorName);
        viewSelectedColorCircle = findViewById(R.id.viewSelectedColorCircle);
        
        tvInwardDate = findViewById(R.id.tvInwardDate);
        
        btnDecreaseQty = findViewById(R.id.btnDecreaseQty);
        btnIncreaseQty = findViewById(R.id.btnIncreaseQty);
        tvQuantity = findViewById(R.id.tvQuantity);
        tvEntriesRequired = findViewById(R.id.tvEntriesRequired);
        
        rvIdentification = findViewById(R.id.rvIdentification);
        
        tvBatchSummary = findViewById(R.id.tvBatchSummary);
        btnSaveGroup = findViewById(R.id.btnSaveGroup); // Was btnSaveStock
        
        calendar = Calendar.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
    }
    
    private void setupRecyclerView() {
        stockEntries.clear();
        stockEntries.add(new StockEntry());
        
        adapter = new IdentificationAdapter(stockEntries);
        rvIdentification.setLayoutManager(new LinearLayoutManager(this));
        rvIdentification.setAdapter(adapter);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> onBackPressed());

        tvInwardDate.setOnClickListener(v -> showDatePicker());

        btnSelectColor.setOnClickListener(v -> showAddColorDialog());
        
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

        btnSaveGroup.setOnClickListener(v -> {
            if (validateInputs()) {
                startBulkSave();
            }
        });
        
        btnFinishSession.setOnClickListener(v -> {
            if (sessionTotalBikes == 0) {
                Toast.makeText(this, "Session is empty. Add at least one group or go back.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Session Completed! " + sessionTotalBikes + " bikes added.", Toast.LENGTH_LONG).show();
                finish();
            }
        });
        
        layoutAddAnother.setOnClickListener(v -> {
            // Just scrolls to the current form logic
             findViewById(R.id.mainScrollView).scrollTo(0, 0); // Simplified scroll
        });
        
        // Listeners for summary updates
        etModel.addTextChangedListener(summaryWatcher);
        etVariant.addTextChangedListener(summaryWatcher);
    }
    
    private final TextWatcher summaryWatcher = new TextWatcher() {
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) { updateBatchSummary(); }
        @Override public void afterTextChanged(Editable s) {}
    };
    
    private void updateQuantityUI() {
        tvQuantity.setText(String.format("%02d", quantity));
        tvEntriesRequired.setText(quantity + " Entries Required");
        
        int currentSize = stockEntries.size();
        if (quantity > currentSize) {
            for (int i = currentSize; i < quantity; i++) {
                stockEntries.add(new StockEntry());
            }
            adapter.notifyItemRangeInserted(currentSize, quantity - currentSize);
        } else if (quantity < currentSize) {
            int removeCount = currentSize - quantity;
            for (int i = 0; i < removeCount; i++) {
                stockEntries.remove(stockEntries.size() - 1);
            }
            adapter.notifyItemRangeRemoved(quantity, removeCount);
        }
        
        updateBatchSummary();
    }
    
    private void updateBatchSummary() {
        String model = etModel.getText().toString().trim();
        if (model.isEmpty()) model = "Unknown Model";
        
        String date = tvInwardDate.getText().toString();
        
        String summary = "Adding " + quantity + " units of " + model + 
                         (isColorSelected ? " (" + selectedColorName + ")" : "") +
                         "\nReceived on " + date;
        tvBatchSummary.setText(summary);
    }

    private void fetchBrands() {
        progressDialog.setMessage("Fetching Brands...");
        progressDialog.show();

        String token = SharedPrefManager.getInstance(this).getToken();
        ApiService apiService = RetrofitClient.getApiService();

        apiService.getBrands("Bearer " + token).enqueue(new Callback<InventoryResponse>() {
            @Override
            public void onResponse(Call<InventoryResponse> call, Response<InventoryResponse> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && response.body() != null && response.body().isStatus()) {
                    List<InventoryBrand> brands = response.body().getData();
                    brandList.clear();
                    brandList.add("Select Brand");
                    for (InventoryBrand b : brands) {
                        if (b.getBrand() != null) {
                            brandList.add(b.getBrand());
                        }
                    }
                    setupBrandSpinner();
                } else {
                    Toast.makeText(AddStockBikeActivity.this, "Failed to fetch brands", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<InventoryResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(AddStockBikeActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupBrandSpinner() {
        ArrayAdapter<String> brandAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, brandList);
        brandAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBrand.setAdapter(brandAdapter);
    }

    private void showDatePicker() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, month1, dayOfMonth) -> {
                    String date = dayOfMonth + "-" + (month1 + 1) + "-" + year1;
                    tvInwardDate.setText(date);
                    updateBatchSummary();
                }, year, month, day);
        datePickerDialog.show();
    }

    private void setDefaultDate() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        tvInwardDate.setText(day + "-" + month + "-" + year);
    }

    private void showAddColorDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_color, null);
        builder.setView(view);

        EditText etColorName = view.findViewById(R.id.etColorName);
        EditText etColorHex = view.findViewById(R.id.etColorHex);
        View viewColorPreview = view.findViewById(R.id.viewColorPreview);
        Button btnAdd = view.findViewById(R.id.btnAdd);
        
        TextView tvTitle = view.findViewById(R.id.tvTitle); 
        if(tvTitle != null) tvTitle.setText("Select Color for Group");

        ColorPickerView colorPickerView = view.findViewById(R.id.colorPickerView);

        android.app.AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        colorPickerView.setOnColorChangedListener((color, hex) -> {
            etColorHex.setText(hex);
            viewColorPreview.setBackgroundTintList(android.content.res.ColorStateList.valueOf(color));
        });

        etColorHex.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    String hex = s.toString();
                    if (!hex.startsWith("#")) hex = "#" + hex;
                    if (hex.length() >= 7) {
                        int color = android.graphics.Color.parseColor(hex);
                        viewColorPreview.setBackgroundTintList(android.content.res.ColorStateList.valueOf(color));
                    }
                } catch (Exception e) {}
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        btnAdd.setText("Apply Color");
        btnAdd.setOnClickListener(v -> {
            String name = etColorName.getText().toString().trim();
            String hex = etColorHex.getText().toString().trim();

            if (name.isEmpty() || hex.isEmpty()) {
                Toast.makeText(this, "Please enter name and hex code", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!hex.startsWith("#")) hex = "#" + hex;
            try {
                android.graphics.Color.parseColor(hex);
                
                selectedColorName = name;
                selectedColorHex = hex;
                isColorSelected = true;
                
                tvSelectedColorName.setText(name);
                viewSelectedColorCircle.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor(hex)));
                updateBatchSummary();
                
                dialog.dismiss();
            } catch (IllegalArgumentException e) {
                etColorHex.setError("Invalid Hex Code");
            }
        });
        
        etColorHex.setText("#000000");
        dialog.show();
    }

    private boolean validateInputs() {
        if (spinnerBrand.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select a brand", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etModel.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter model", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!isColorSelected) {
            Toast.makeText(this, "Please select a color", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        for (int i = 0; i < stockEntries.size(); i++) {
            StockEntry entry = stockEntries.get(i);
            if (entry.chassis.isEmpty() || entry.engine.isEmpty()) {
                Toast.makeText(this, "Please fill all details for Item " + (i+1), Toast.LENGTH_SHORT).show();
                rvIdentification.smoothScrollToPosition(i);
                return false;
            }
        }
        return true;
    }
    
    // --- Batch Save Logic ---
    
    private void startBulkSave() {
        progressDialog.setMessage("Initializing Batch Save...");
        progressDialog.show();
        saveNextItem(0);
    }
    
    private void saveNextItem(int index) {
        if (index >= stockEntries.size()) {
            onGroupSaved();
            return;
        }
        
        progressDialog.setMessage("Saving Bike " + (index+1) + " of " + stockEntries.size() + "...");
        
        StockEntry entry = stockEntries.get(index);
        
        String brand = spinnerBrand.getSelectedItem().toString();
        String model = etModel.getText().toString().trim();
        String variant = etVariant.getText().toString().trim(); 
        String date = tvInwardDate.getText().toString();
        
        List<String> singleColorList = new ArrayList<>();
        singleColorList.add(selectedColorName + "|" + selectedColorHex);
        String colorsJson = new Gson().toJson(singleColorList);
        
        String token = SharedPrefManager.getInstance(this).getToken();
        ApiService apiService = RetrofitClient.getApiService();
        
        // Note: Using chassis and engine from current entry
        apiService.addStockBike("Bearer " + token, brand, model, variant, colorsJson, entry.engine, entry.chassis, date)
            .enqueue(new Callback<GenericResponse>() {
                @Override
                public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        saveNextItem(index + 1);
                    } else {
                        progressDialog.dismiss();
                        String msg = (response.body() != null) ? response.body().getMessage() : "Unknown Error";
                        Toast.makeText(AddStockBikeActivity.this, "Error at Item " + (index+1) + ": " + msg, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<GenericResponse> call, Throwable t) {
                    progressDialog.dismiss();
                    Toast.makeText(AddStockBikeActivity.this, "Network Error at Item " + (index+1), Toast.LENGTH_LONG).show();
                }
            });
    }

    private void onGroupSaved() {
        progressDialog.dismiss();
        
        // 1. Capture details for summary
        String brand = spinnerBrand.getSelectedItem().toString();
        String model = etModel.getText().toString().trim();
        int count = stockEntries.size();
        
        // 2. Add Summary Card
        addSavedGroupCard(brand, model, count);
        
        // 3. Update Session Total
        sessionTotalBikes += count;
        tvSessionTotal.setText("Total: " + sessionTotalBikes + " Bikes");
        
        // 4. Reset Form
        resetForm();
        
        Toast.makeText(this, "Group Saved! Add another or Finish Session.", Toast.LENGTH_SHORT).show();
        
        // 5. Scroll to top to show saved group or new form
        findViewById(R.id.mainScrollView).scrollTo(0, 0);
    }
    
    private void addSavedGroupCard(String brand, String model, int count) {
        View cardView = LayoutInflater.from(this).inflate(R.layout.item_stock_group_summary, containerSavedGroups, false);
        
        TextView tvGroupTitle = cardView.findViewById(R.id.tvGroupTitle);
        TextView tvGroupCount = cardView.findViewById(R.id.tvGroupCount);
        
        // e.g. "Royal Enfield (Classic 350)"
        tvGroupTitle.setText(brand + " " + model);
        tvGroupCount.setText(count + " Bikes Added");
        
        containerSavedGroups.addView(cardView);
    }
    
    private void resetForm() {
        // Reset Inputs
        etModel.setText("");
        etVariant.setText("");
        
        // Reset Color
        selectedColorName = "Select Color";
        selectedColorHex = "#CCCCCC";
        isColorSelected = false;
        tvSelectedColorName.setText(selectedColorName);
        viewSelectedColorCircle.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor(selectedColorHex)));
        
        // Reset Quantity and List
        quantity = 1;
        stockEntries.clear();
        stockEntries.add(new StockEntry());
        adapter.notifyDataSetChanged();
        updateQuantityUI();
        
        // Reset Date (Optional, but keeping same inward date is usually better for a session. Skipping reset.)
        // setDefaultDate(); 
        
        spinnerBrand.setSelection(0);
    }

    // --- Inner Classes ---

    private static class StockEntry {
        String chassis = "";
        String engine = "";
    }

    private class IdentificationAdapter extends RecyclerView.Adapter<IdentificationAdapter.ViewHolder> {
        
        private final List<StockEntry> entries;
        
        public IdentificationAdapter(List<StockEntry> entries) {
            this.entries = entries;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_identification_input, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            StockEntry entry = entries.get(position);
            
            holder.tvRowNumber.setText(String.format("%02d", position + 1));
            
            holder.etChassis.removeTextChangedListener(holder.chassisWatcher);
            holder.etEngine.removeTextChangedListener(holder.engineWatcher);
            
            holder.etChassis.setText(entry.chassis);
            holder.etEngine.setText(entry.engine);
            
            holder.chassisWatcher = new SimpleTextWatcher(s -> entry.chassis = s.toString());
            holder.etChassis.addTextChangedListener(holder.chassisWatcher);
            
            holder.engineWatcher = new SimpleTextWatcher(s -> entry.engine = s.toString());
            holder.etEngine.addTextChangedListener(holder.engineWatcher);
            
            holder.btnScanChassis.setOnClickListener(v -> Toast.makeText(AddStockBikeActivity.this, "Scan Chassis #" + (position+1), Toast.LENGTH_SHORT).show());
            holder.btnScanEngine.setOnClickListener(v -> Toast.makeText(AddStockBikeActivity.this, "Scan Engine #" + (position+1), Toast.LENGTH_SHORT).show());
        }

        @Override
        public int getItemCount() {
            return entries.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvRowNumber;
            EditText etChassis, etEngine;
            ImageView btnScanChassis, btnScanEngine;
            TextWatcher chassisWatcher, engineWatcher;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvRowNumber = itemView.findViewById(R.id.tvRowNumber);
                etChassis = itemView.findViewById(R.id.etChassisInput);
                etEngine = itemView.findViewById(R.id.etEngineInput);
                btnScanChassis = itemView.findViewById(R.id.btnScanChassisRow);
                btnScanEngine = itemView.findViewById(R.id.btnScanEngineRow);
            }
        }
    }
    
    private interface TextListener {
        void onTextChanged(CharSequence s);
    }
    
    private static class SimpleTextWatcher implements TextWatcher {
        private final TextListener listener;
        public SimpleTextWatcher(TextListener listener) { this.listener = listener; }
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) { listener.onTextChanged(s); }
        @Override public void afterTextChanged(Editable s) {}
    }
}
