package com.example.motovista_deep;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.app.AlertDialog;
import android.graphics.Color;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.motovista_deep.api.ApiService;
import com.example.motovista_deep.api.RetrofitClient;
import com.example.motovista_deep.helpers.PathCallback;
import com.example.motovista_deep.helpers.RealPathUtil;
import com.example.motovista_deep.helpers.SharedPrefManager;
import com.example.motovista_deep.models.AddBikeRequestV2;
import com.example.motovista_deep.models.BikeParentModel;
import com.example.motovista_deep.models.BikeVariantModel;
import com.example.motovista_deep.models.GenericResponse;
import com.example.motovista_deep.models.UploadBikeImageResponse;

import com.example.motovista_deep.models.CustomFitting;
import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddBikeActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 100;

    // Parent Model UI
    private EditText etBrand, etModelName, etYear, etEngineCC, etMileage, etFuelTank, etKerbWeight, etSeatHeight, etGroundClearance, etMaxTorque, etMaxPower;
    private Spinner spFuelType, spTransmission;
    private EditText etWarranty, etFreeServices;
    private TextView etRegProof, etPriceDisclaimer;
    private LinearLayout variantsContainer;
    private MaterialButton btnAddVariant;
    private View btnSaveBike, btnCancel;
    private ImageButton btnBack;

    private BikeParentModel bikeParentModel = new BikeParentModel();
    private List<BikeVariantModel> bikeVariants = new ArrayList<>();
    private List<VariantViewHolder> variantViewHolders = new ArrayList<>();

    // Variables for Image Upload state
    private BikeVariantModel.VariantColor currentColorUploading = null;
    private VariantViewHolder currentVariantHolder = null;
    
    // Fittings UI Elements
    private CheckBox cbCrashBar, cbSareeGuard, cbMirror, cbNumberPlate, cbSideStand, cbFootRest;
    private CheckBox cbSideBox, cbPetrolTankBag, cbGripCover, cbBagHook, cbHelmet, cbBodyCover, cbIndicatorBuzzer, cbSeatCover, cbLadiesHandle, cbEngineGuard, cbBumperSS;
    private EditText etSideBoxPrice, etPetrolTankBagPrice, etGripCoverPrice, etBagHookPrice, etBodyCoverPrice, etIndicatorBuzzerPrice, etSeatCoverPrice, etLadiesHandlePrice, etEngineGuardPrice, etBumperSSPrice;

    private LinearLayout llAdditionalFittings; // Only for dynamic custom ones
    private MaterialButton btnAddAdditional;
    private List<View> additionalItemViews = new ArrayList<>();

    // Edit Mode Flags
    private boolean isEditMode = false;
    private int bikeId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bike_redesign);

        initializeViews();
        setupListeners();

        initializeViews();
        setupListeners();

        // Check for Edit Mode
        if (getIntent().hasExtra("bike_data") || getIntent().hasExtra("BIKE_MODEL")) {
            com.example.motovista_deep.models.BikeModel bike = null;
            if (getIntent().hasExtra("bike_data")) {
                bike = getIntent().getParcelableExtra("bike_data");
            } else {
                bike = getIntent().getParcelableExtra("BIKE_MODEL");
            }
            
            if (bike != null) {
                setupEditMode(bike);
            }
        } else {
            // Add one empty variant by default for new bikes
            addVariant();
        }
    }

    private void setupEditMode(com.example.motovista_deep.models.BikeModel bike) {
        isEditMode = true;
        bikeId = bike.getId();
        TextView title = findViewById(R.id.tvTitle); // Assuming toolbar title exists
        if (title != null) title.setText("Edit Bike");
        ((Button)btnSaveBike).setText("Update Bike");

        // Populate Parent Fields
        etBrand.setText(bike.getBrand());
        etModelName.setText(bike.getModel());
        etYear.setText(bike.getYear());
        etEngineCC.setText(bike.getEngineCC());
        
        // Spinners
        setSpinnerSelection(spFuelType, bike.getFuel_type());
        setSpinnerSelection(spTransmission, bike.getTransmission());
        
        etMileage.setText(bike.getMileage());
        etFuelTank.setText(bike.getFuelTankCapacity());
        etKerbWeight.setText(bike.getKerbWeight());
        etSeatHeight.setText(bike.getSeatHeight());
        etGroundClearance.setText(bike.getGroundClearance());
        etMaxTorque.setText(bike.getMaxTorque());
        etMaxPower.setText(bike.getMaxPower());
        
        etWarranty.setText(bike.getWarrantyPeriod());
        etFreeServices.setText(bike.getFreeServicesCount());
        
        // Legal Notes (if available)
        if (bike.getRegistrationProof() != null) etRegProof.setText(bike.getRegistrationProof());
        if (bike.getPriceDisclaimer() != null) etPriceDisclaimer.setText(bike.getPriceDisclaimer());

        // Pre-fill Fittings
        prefillFittings(bike);

        // Pre-fill Variants
        if (bike.getVariants() != null && !bike.getVariants().isEmpty()) {
            bikeVariants.clear();
            variantsContainer.removeAllViews();
            variantViewHolders.clear();
            
            for (BikeVariantModel v : bike.getVariants()) {
                addVariant(v); 
            }
        } else {
             addVariant(); // Fallback
        }
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        if (value == null) return;
        ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).toString().equalsIgnoreCase(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private void prefillFittings(com.example.motovista_deep.models.BikeModel bike) {
        // Mandatory
        if (bike.getMandatoryFittings() != null) {
            for (CustomFitting f : bike.getMandatoryFittings()) {
                if (f == null) continue;
                checkMandatoryBox(f.getName());
            }
        }
        
        // Additional
        if (bike.getAdditionalFittings() != null) {
            for (CustomFitting f : bike.getAdditionalFittings()) {
                if (f == null) continue;
                if (!checkAdditionalBox(f.getName(), f.getPrice())) {
                    // If not one of the checkboxes, add as dynamic row
                    addFittingRow(llAdditionalFittings, additionalItemViews, f.getName(), f.getPrice());
                }
            }
        }
    }

    private void checkMandatoryBox(String name) {
        if (name == null) return;
        if (name.contains("Crash Bar")) cbCrashBar.setChecked(true);
        if (name.contains("Saree Guard")) cbSareeGuard.setChecked(true);
        if (name.contains("Mirror")) cbMirror.setChecked(true);
        if (name.contains("Number Plate")) cbNumberPlate.setChecked(true);
        if (name.contains("Side Stand")) cbSideStand.setChecked(true);
        if (name.contains("Foot Rest")) cbFootRest.setChecked(true);
    }

    private boolean checkAdditionalBox(String name, String price) {
        if (name == null) return false;
        if (name.contains("Side Box")) { cbSideBox.setChecked(true); etSideBoxPrice.setText(price); return true; }
        if (name.contains("Petrol Tank Bag")) { cbPetrolTankBag.setChecked(true); etPetrolTankBagPrice.setText(price); return true; }
        if (name.contains("Grip Cover")) { cbGripCover.setChecked(true); etGripCoverPrice.setText(price); return true; }
        if (name.contains("Bag Hook")) { cbBagHook.setChecked(true); etBagHookPrice.setText(price); return true; }
        if (name.contains("Helmet")) { cbHelmet.setChecked(true); return true; }
        if (name.contains("Body Cover")) { cbBodyCover.setChecked(true); etBodyCoverPrice.setText(price); return true; }
        if (name.contains("Indicator Buzzer")) { cbIndicatorBuzzer.setChecked(true); etIndicatorBuzzerPrice.setText(price); return true; }
        if (name.contains("Seat Cover")) { cbSeatCover.setChecked(true); etSeatCoverPrice.setText(price); return true; }
        if (name.contains("Ladies Handle")) { cbLadiesHandle.setChecked(true); etLadiesHandlePrice.setText(price); return true; }
        if (name.contains("Engine Guard")) { cbEngineGuard.setChecked(true); etEngineGuardPrice.setText(price); return true; }
        if (name.contains("Bumper SS")) { cbBumperSS.setChecked(true); etBumperSSPrice.setText(price); return true; }
        return false;
    }

    private void addFittingRow(LinearLayout container, List<View> trackingList, String name, String price) {
        View row = LayoutInflater.from(this).inflate(R.layout.item_dynamic_field, container, false);
        container.addView(row);
        trackingList.add(row);

        EditText etName = row.findViewById(R.id.etFieldKey);
        EditText etPrice = row.findViewById(R.id.etFieldValue);
        
        etName.setText(name);
        etPrice.setText(price);
        
        etName.setHint("Fitting Name");
        etPrice.setHint("Price");
        etPrice.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);

        row.setOnLongClickListener(v -> {
             container.removeView(row);
             trackingList.remove(row);
             return true;
        });
    }

    private void initializeViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayShowTitleEnabled(false);

        btnBack = findViewById(R.id.btnBack);
        
        // Parent fields
        etBrand = findViewById(R.id.etBrand);
        etModelName = findViewById(R.id.etModelName);
        etYear = findViewById(R.id.etYear);
        etEngineCC = findViewById(R.id.etEngineCC);
        spFuelType = findViewById(R.id.spFuelType);
        spTransmission = findViewById(R.id.spTransmission);
        etMileage = findViewById(R.id.etMileage);
        etFuelTank = findViewById(R.id.etFuelTank);
        etKerbWeight = findViewById(R.id.etKerbWeight);
        etSeatHeight = findViewById(R.id.etSeatHeight);
        etGroundClearance = findViewById(R.id.etGroundClearance);
        etMaxTorque = findViewById(R.id.etMaxTorque);
        etMaxPower = findViewById(R.id.etMaxPower);
        
        etWarranty = findViewById(R.id.etWarranty);
        etFreeServices = findViewById(R.id.etFreeServices);
        etRegProof = findViewById(R.id.etRegProof);
        etPriceDisclaimer = findViewById(R.id.etPriceDisclaimer);
        
        // Default values are set in XML as Read-Only text.
        
        variantsContainer = findViewById(R.id.variantsContainer);
        btnAddVariant = findViewById(R.id.btnAddVariant);
        btnSaveBike = findViewById(R.id.btnSaveBike);
        btnCancel = findViewById(R.id.btnCancel);

        // Initialize Fittings Sections (Checkboxes)
        cbCrashBar = findViewById(R.id.cbCrashBar);
        cbSareeGuard = findViewById(R.id.cbSareeGuard);
        cbMirror = findViewById(R.id.cbMirror);
        cbNumberPlate = findViewById(R.id.cbNumberPlate);
        cbSideStand = findViewById(R.id.cbSideStand);
        cbFootRest = findViewById(R.id.cbFootRest);
        
        cbSideBox = findViewById(R.id.cbSideBox); etSideBoxPrice = findViewById(R.id.etSideBoxPrice);
        cbPetrolTankBag = findViewById(R.id.cbPetrolTankBag); etPetrolTankBagPrice = findViewById(R.id.etPetrolTankBagPrice);
        cbGripCover = findViewById(R.id.cbGripCover); etGripCoverPrice = findViewById(R.id.etGripCoverPrice);
        cbBagHook = findViewById(R.id.cbBagHook); etBagHookPrice = findViewById(R.id.etBagHookPrice);
        cbHelmet = findViewById(R.id.cbHelmet);
        cbBodyCover = findViewById(R.id.cbBodyCover); etBodyCoverPrice = findViewById(R.id.etBodyCoverPrice);
        cbIndicatorBuzzer = findViewById(R.id.cbIndicatorBuzzer); etIndicatorBuzzerPrice = findViewById(R.id.etIndicatorBuzzerPrice);
        cbSeatCover = findViewById(R.id.cbSeatCover); etSeatCoverPrice = findViewById(R.id.etSeatCoverPrice);
        cbLadiesHandle = findViewById(R.id.cbLadiesHandle); etLadiesHandlePrice = findViewById(R.id.etLadiesHandlePrice);
        cbEngineGuard = findViewById(R.id.cbEngineGuard); etEngineGuardPrice = findViewById(R.id.etEngineGuardPrice);
        cbBumperSS = findViewById(R.id.cbBumperSS); etBumperSSPrice = findViewById(R.id.etBumperSSPrice);

        // Dynamic Additional Fittings
        llAdditionalFittings = findViewById(R.id.llAdditionalFittings);
        btnAddAdditional = findViewById(R.id.btnAddAdditionalFitting);
        btnAddAdditional.setOnClickListener(v -> addFittingRow(llAdditionalFittings, additionalItemViews, "", ""));

        // Spinners
        setupSpinner(spFuelType, new String[]{"Petrol", "Electric", "Hybrid"});
        setupSpinner(spTransmission, new String[]{"Manual", "Automatic", "CVT"});
    }

    private void setupSpinner(Spinner spinner, String[] items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnCancel.setOnClickListener(v -> finish());
        
        btnAddVariant.setOnClickListener(v -> addVariant());
        
        btnSaveBike.setOnClickListener(v -> startSaveProcess());
    }

    private void addVariant() {
        addVariant(new BikeVariantModel());
    }

    private void addVariant(BikeVariantModel existingVariant) {
        BikeVariantModel newVariant = existingVariant; // Use existing or new
        bikeVariants.add(newVariant);

        View variantView = LayoutInflater.from(this).inflate(R.layout.item_variant_builder, variantsContainer, false);
        variantsContainer.addView(variantView);

        VariantViewHolder holder = new VariantViewHolder(variantView, newVariant, this);
        variantViewHolders.add(holder);
    }

    private void startSaveProcess() {
        // 1. Sync UI to Models
        syncUiToModels();

        // 2. Validate
        if (!validateData()) return;

        // 3. Upload Images (Recursive chain)
        uploadImagesAndSubmit();
    }

    private void syncUiToModels() {
        // Parent
        bikeParentModel.setBrand(etBrand.getText().toString().trim());
        bikeParentModel.setModelName(etModelName.getText().toString().trim());
        bikeParentModel.setModelYear(etYear.getText().toString().trim());
        bikeParentModel.setEngineCC(etEngineCC.getText().toString().trim());
        bikeParentModel.setFuelType(spFuelType.getSelectedItem().toString());
        bikeParentModel.setTransmission(spTransmission.getSelectedItem().toString());
        bikeParentModel.setMileage(etMileage.getText().toString().trim());
        bikeParentModel.setFuelTankCapacity(etFuelTank.getText().toString().trim());
        bikeParentModel.setKerbWeight(etKerbWeight.getText().toString().trim());
        bikeParentModel.setSeatHeight(etSeatHeight.getText().toString().trim());
        bikeParentModel.setGroundClearance(etGroundClearance.getText().toString().trim());
        bikeParentModel.setMaxTorque(etMaxTorque.getText().toString().trim());
        bikeParentModel.setMaxPower(etMaxPower.getText().toString().trim());
        
        bikeParentModel.setWarrantyPeriod(etWarranty.getText().toString().trim());
        bikeParentModel.setFreeServices(etFreeServices.getText().toString().trim());
        
        // Fittings
        bikeParentModel.setMandatoryFittings(gatherMandatoryFittings());
        bikeParentModel.setAdditionalFittings(gatherAdditionalFittings());
        
        BikeParentModel.LegalNotes notes = new BikeParentModel.LegalNotes();
        // Since these fields are now just TextViews (static legal notes), we don't really need to read them from UI if they are static.
        // But if the user wants them in the DB, we can send default strings or read from textviews.
        // Previous step made them static textviews, but I kept IDs etRegProof/etPriceDisclaimer on them possibly?
        // Wait, I updated layout to use TextViews but kept the IDs.
        // Let's grab the text from them just in case.
        TextView tvRegProof = findViewById(R.id.etRegProof); // ID reused? Yes if I kept it
        TextView tvPriceDisclaimer = findViewById(R.id.etPriceDisclaimer);
        
        if (tvRegProof != null) notes.regProof = tvRegProof.getText().toString();
        if (tvPriceDisclaimer != null) notes.priceDisclaimer = tvPriceDisclaimer.getText().toString();
        
        bikeParentModel.setLegalNotes(notes);
        
        // Variants
        for (VariantViewHolder holder : variantViewHolders) {
            holder.syncData();
        }
    }

    // --- Fittings UI Logic ---
    // Mandatory are now just checkboxes, no dynamic list vars needed for them.

    // Helper to add dynamic fittings (only for custom added ones)
    // Combined into overloaded method above


    private List<CustomFitting> extractFittings(List<View> views) {
        List<CustomFitting> list = new ArrayList<>();
        for (View v : views) {
             EditText etName = v.findViewById(R.id.etFieldKey);
             EditText etPrice = v.findViewById(R.id.etFieldValue);
             String name = etName.getText().toString().trim();
             String price = etPrice.getText().toString().trim();
             if (!name.isEmpty()) {
                 list.add(new CustomFitting(name, price));
             }
        }
        return list;
    }
    
    private List<CustomFitting> gatherMandatoryFittings() {
        List<CustomFitting> list = new ArrayList<>();
        if (cbCrashBar.isChecked()) list.add(new CustomFitting("Crash Bar", "Included"));
        if (cbSareeGuard.isChecked()) list.add(new CustomFitting("Saree Guard", "Included"));
        if (cbMirror.isChecked()) list.add(new CustomFitting("Mirror Set", "Included"));
        if (cbNumberPlate.isChecked()) list.add(new CustomFitting("Front & Rear Number Plate", "Included"));
        if (cbSideStand.isChecked()) list.add(new CustomFitting("Side Stand", "Included"));
        if (cbFootRest.isChecked()) list.add(new CustomFitting("Foot Rest", "Included"));
        return list;
    }
    
    private List<CustomFitting> gatherAdditionalFittings() {
        List<CustomFitting> list = new ArrayList<>();
        
        // Checklist Items
        addIfChecked(list, cbSideBox, "Side Box (Fibre)", etSideBoxPrice);
        addIfChecked(list, cbPetrolTankBag, "Petrol Tank Bag", etPetrolTankBagPrice);
        addIfChecked(list, cbGripCover, "Grip Cover", etGripCoverPrice);
        addIfChecked(list, cbBagHook, "Bag Hook", etBagHookPrice);
        if(cbHelmet.isChecked()){
             list.add(new CustomFitting("Helmet", "FREE"));
        }
        addIfChecked(list, cbBodyCover, "Body Cover (Full)", etBodyCoverPrice);
        addIfChecked(list, cbIndicatorBuzzer, "Indicator Buzzer", etIndicatorBuzzerPrice);
        addIfChecked(list, cbSeatCover, "Seat Cover", etSeatCoverPrice);
        addIfChecked(list, cbLadiesHandle, "Ladies Handle", etLadiesHandlePrice);
        addIfChecked(list, cbEngineGuard, "Engine Guard", etEngineGuardPrice);
        addIfChecked(list, cbBumperSS, "Bumper SS", etBumperSSPrice);
        
        // Add Dynamic Custom Items
        list.addAll(extractFittings(additionalItemViews));
        
        return list;
    }
    
    private void addIfChecked(List<CustomFitting> list, CheckBox cb, String name, EditText priceInput) {
        if(cb.isChecked()) {
             String p = priceInput.getText().toString().trim();
             if(p.isEmpty()) p = "0";
             list.add(new CustomFitting(name, p));
        }
    }


    
    // --- ViewHolder Logic ---
    // --- End of Image Upload Logic ---
    // (Consolidated VariantViewHolder logic is already defined above)
        
    private boolean validateData() {
        if (bikeVariants.isEmpty()) {
            Toast.makeText(this, "Add at least one variant", Toast.LENGTH_SHORT).show();
            return false;
        }

        for (BikeVariantModel v : bikeVariants) {
            if (v.variantName == null || v.variantName.isEmpty()) {
                Toast.makeText(this, "Variant Name Required", Toast.LENGTH_SHORT).show();
                return false;
            }
            // Check colors
            if (v.colors.isEmpty()) {
                Toast.makeText(this, "Variant " + v.variantName + " needs at least one color", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    // --- Image Upload Logic ---
    private List<ImageUploadTask> uploadQueue = new ArrayList<>();
    private ProgressDialog progressDialog;

    private void uploadImagesAndSubmit() {
        uploadQueue.clear();
        
        for (BikeVariantModel variant : bikeVariants) {
            for (BikeVariantModel.VariantColor color : variant.colors) {
                if (color.tempImageUris != null) {
                    for (String uri : color.tempImageUris) {
                        uploadQueue.add(new ImageUploadTask(variant, color, uri));
                    }
                }
            }
        }

        if (uploadQueue.isEmpty()) {
            submitFinalData();
        } else {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Uploading Images (0/" + uploadQueue.size() + ")...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            processNextUpload(0);
        }
    }

    private void processNextUpload(int index) {
        if (index >= uploadQueue.size()) {
            progressDialog.dismiss();
            submitFinalData();
            return;
        }

        progressDialog.setMessage("Uploading Images (" + (index + 1) + "/" + uploadQueue.size() + ")...");
        ImageUploadTask task = uploadQueue.get(index);
        
        uploadImageToServer(task.uri, new PathCallback() {
            @Override
            public void onPathReceived(String serverPath) {
                // Add to the color's imagePaths list
                task.color.imagePaths.add(serverPath);
                processNextUpload(index + 1);
            }

            @Override
            public void onError(String error) {
                progressDialog.dismiss();
                Toast.makeText(AddBikeActivity.this, "Upload Failed: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void uploadImageToServer(String fileUri, PathCallback callback) {
        try {
            Uri uri = Uri.parse(fileUri);
            File file = new File(RealPathUtil.getRealPath(this, uri));
            
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("bike_images", file.getName(), requestFile);
            
            String token = SharedPrefManager.getInstance(this).getToken();
            
            RetrofitClient.getApiService().uploadBikeImage("Bearer " + token, body)
                    .enqueue(new Callback<UploadBikeImageResponse>() {
                        @Override
                        public void onResponse(Call<UploadBikeImageResponse> call, Response<UploadBikeImageResponse> response) {
                            if (response.isSuccessful() && response.body() != null && "success".equals(response.body().getStatus())) {
                                String relativePath = response.body().getFirstImagePath(); // e.g. "uploads/bikes/..."
                                callback.onPathReceived(relativePath);
                            } else {
                                callback.onError("Server Error");
                            }
                        }

                        @Override
                        public void onFailure(Call<UploadBikeImageResponse> call, Throwable t) {
                            callback.onError(t.getMessage());
                        }
                    });

        } catch (Exception e) {
            callback.onError(e.getMessage());
        }
    }

    private void submitFinalData() {
        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Saving Bike Data...");
        pd.show();

        String token = SharedPrefManager.getInstance(this).getToken();

        if (isEditMode) {
            com.example.motovista_deep.models.UpdateBikeRequestV2 request = 
                new com.example.motovista_deep.models.UpdateBikeRequestV2(bikeId, bikeParentModel, bikeVariants);
            
            RetrofitClient.getApiService().updateBikeV2("Bearer " + token, request)
                .enqueue(new Callback<GenericResponse>() {
                    @Override
                    public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                        pd.dismiss();
                        if (response.isSuccessful() && response.body() != null && "success".equalsIgnoreCase(response.body().getStatus())) {
                            Toast.makeText(AddBikeActivity.this, "Bike Updated Successfully!", Toast.LENGTH_LONG).show();
                            setResult(RESULT_OK); // Notify caller
                            finish();
                        } else {
                            Toast.makeText(AddBikeActivity.this, "Error: " + (response.body()!=null?response.body().getMessage():"Unknown"), Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<GenericResponse> call, Throwable t) {
                        pd.dismiss();
                        Toast.makeText(AddBikeActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        } else {
            AddBikeRequestV2 request = new AddBikeRequestV2(bikeParentModel, bikeVariants);

            RetrofitClient.getApiService().addBikeV2("Bearer " + token, request)
                    .enqueue(new Callback<GenericResponse>() {
                        @Override
                        public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                            pd.dismiss();
                            if (response.isSuccessful() && response.body() != null) {
                                if (response.body().getStatus().equalsIgnoreCase("success")) {
                                    Toast.makeText(AddBikeActivity.this, "Bike Added Successfully!", Toast.LENGTH_LONG).show();
                                    finish();
                                } else {
                                    Toast.makeText(AddBikeActivity.this, "Error: " + response.body().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(AddBikeActivity.this, "Server Error: " + response.code(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<GenericResponse> call, Throwable t) {
                            pd.dismiss();
                            Toast.makeText(AddBikeActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    private void showColorPickerDialog(BikeVariantModel.VariantColor colorModel, View vChip, TextView tvName) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_color, null);
        builder.setView(view);

        EditText etColorName = view.findViewById(R.id.etColorName);
        EditText etColorHex = view.findViewById(R.id.etColorHex);
        View viewColorPreview = view.findViewById(R.id.viewColorPreview);
        Button btnAdd = view.findViewById(R.id.btnAdd); // "Apply Color"
        com.example.motovista_deep.views.ColorPickerView colorPickerView = view.findViewById(R.id.colorPickerView);

        // Pre-fill existing data
        etColorName.setText(colorModel.colorName);
        etColorHex.setText(colorModel.colorHex);
        try {
            int color = Color.parseColor(colorModel.colorHex);
            viewColorPreview.setBackgroundTintList(android.content.res.ColorStateList.valueOf(color));
        } catch (Exception e) {}

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
                        int color = Color.parseColor(hex);
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
                int colorCode = Color.parseColor(hex);
                
                // Update Model
                colorModel.colorName = name;
                colorModel.colorHex = hex;
                
                // Update UI
                tvName.setText(name);
                vChip.setBackgroundTintList(android.content.res.ColorStateList.valueOf(colorCode));
                
                dialog.dismiss();
            } catch (IllegalArgumentException e) {
                etColorHex.setError("Invalid Hex Code");
            }
        });
        
        dialog.show();
    }

    private static class ImageUploadTask {
        BikeVariantModel variant;
        BikeVariantModel.VariantColor color;
        String uri;

        public ImageUploadTask(BikeVariantModel v, BikeVariantModel.VariantColor c, String u) {
            this.variant = v;
            this.color = c;
            this.uri = u;
        }
    }

    // --- ViewHolder Logic ---
    class VariantViewHolder {
        View itemView;
        BikeVariantModel model;
        AddBikeActivity activity;
        
        EditText etVariantName;
        EditText etEx, etIns, etReg, etLtrt;
        TextView tvTotal;
        Spinner spFront, spRear, spSys, spWheel;
        LinearLayout llColors, llDynamic;
        ImageButton btnDelete;
        TextView btnAddColor;
        MaterialButton btnAddSection;

        public VariantViewHolder(View view, BikeVariantModel model, AddBikeActivity activity) {
            this.itemView = view;
            this.model = model;
            this.activity = activity;
            bindViews();
            setupInternalListeners();
        }

        private void bindViews() {
            etVariantName = itemView.findViewById(R.id.etVariantName);
            btnDelete = itemView.findViewById(R.id.btnDeleteVariant);
            
            etEx = itemView.findViewById(R.id.etExShowroom);
            etIns = itemView.findViewById(R.id.etInsurance);
            etReg = itemView.findViewById(R.id.etRegistration);
            etLtrt = itemView.findViewById(R.id.etLTRT);
            tvTotal = itemView.findViewById(R.id.tvTotalOnRoad);
            
            spFront = itemView.findViewById(R.id.spFrontBrake);
            spRear = itemView.findViewById(R.id.spRearBrake);
            spSys = itemView.findViewById(R.id.spBrakingSystem);
            spWheel = itemView.findViewById(R.id.spWheelType);
            
            llColors = itemView.findViewById(R.id.llColorsContainer);
            btnAddColor = itemView.findViewById(R.id.btnAddColor);
            
            llDynamic = itemView.findViewById(R.id.llDynamicSectionsContainer);
            btnAddSection = itemView.findViewById(R.id.btnAddSection);
            
            // Setup Spinners
            activity.setupSpinner(spFront, new String[]{"Disc", "Drum"});
            activity.setupSpinner(spRear, new String[]{"Disc", "Drum"});
            activity.setupSpinner(spSys, new String[]{"Dual Channel ABS", "Single Channel ABS", "CBS", "IBS", "Standard"});
            activity.setupSpinner(spWheel, new String[]{"Alloy", "Spoke"});
            
            // PRE-FILL DATA IF EDITING
            if (model.variantName != null) etVariantName.setText(model.variantName);
            if (model.priceDetails != null) {
                etEx.setText(model.priceDetails.exShowroom);
                etIns.setText(model.priceDetails.insurance);
                etReg.setText(model.priceDetails.registration);
                etLtrt.setText(model.priceDetails.ltrt);
                calculateTotal();
            }
            if (model.brakesWheels != null) {
                activity.setSpinnerSelection(spFront, model.brakesWheels.frontBrake);
                activity.setSpinnerSelection(spRear, model.brakesWheels.rearBrake);
                activity.setSpinnerSelection(spSys, model.brakesWheels.brakingSystem);
                activity.setSpinnerSelection(spWheel, model.brakesWheels.wheelType);
            }
            
            // Colors and Custom Sections
            if (model.colors != null) {
                for (BikeVariantModel.VariantColor c : model.colors) {
                    restoreColorSlot(c);
                }
            }
            if (model.customSections != null) {
                for (BikeVariantModel.CustomSection s : model.customSections) {
                    restoreCustomSection(s);
                }
            }
        }

        private void setupInternalListeners() {
            btnDelete.setOnClickListener(v -> {
                activity.variantsContainer.removeView(itemView);
                activity.bikeVariants.remove(model);
                activity.variantViewHolders.remove(this);
            });
            
            TextWatcher priceWatcher = new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) { calculateTotal(); }
                @Override public void afterTextChanged(Editable s) {}
            };
            etEx.addTextChangedListener(priceWatcher);
            etIns.addTextChangedListener(priceWatcher);
            etReg.addTextChangedListener(priceWatcher);
            etLtrt.addTextChangedListener(priceWatcher);

            btnAddColor.setOnClickListener(v -> addColorSlot());
            btnAddSection.setOnClickListener(v -> showAddSectionDialog());
        }

        private void calculateTotal() {
            double ex = parseDouble(etEx.getText().toString());
            double ins = parseDouble(etIns.getText().toString());
            double reg = parseDouble(etReg.getText().toString());
            double ltrt = parseDouble(etLtrt.getText().toString());
            double total = ex + ins + reg + ltrt;
            tvTotal.setText("₹ " + String.format("%.0f", total));
            model.priceDetails.totalOnRoad = String.valueOf(total);
        }

        private double parseDouble(String s) {
            try { return Double.parseDouble(s); } catch (Exception e) { return 0; }
        }

        private void restoreColorSlot(BikeVariantModel.VariantColor color) {
             View colorView = LayoutInflater.from(activity).inflate(R.layout.item_variant_color, llColors, false);
             llColors.addView(colorView);
             setupColorViewEvents(colorView, color);
        }

        private void addColorSlot() {
            BikeVariantModel.VariantColor color = new BikeVariantModel.VariantColor();
            color.colorName = "Select Color";
            color.colorHex = "#CCCCCC";
            model.colors.add(color);
            
            View colorView = LayoutInflater.from(activity).inflate(R.layout.item_variant_color, llColors, false);
            llColors.addView(colorView);
            setupColorViewEvents(colorView, color);
        }

        private void setupColorViewEvents(View colorView, BikeVariantModel.VariantColor color) {
             LinearLayout btnSelectColor = colorView.findViewById(R.id.btnSelectColor);
            TextView tvColorName = colorView.findViewById(R.id.tvColorName);
            View vColorChip = colorView.findViewById(R.id.vColorChip);
            ImageButton btnRemove = colorView.findViewById(R.id.btnRemoveColor);
            LinearLayout btnAddImage = colorView.findViewById(R.id.btnAddImage);
            
            tvColorName.setText(color.colorName);
            try {
                vColorChip.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor(color.colorHex)));
            } catch (Exception e) {}
            
            btnRemove.setOnClickListener(v -> {
                llColors.removeView(colorView);
                model.colors.remove(color);
            });
            
            btnSelectColor.setOnClickListener(v -> activity.showColorPickerDialog(color, vColorChip, tvColorName));
            
            btnAddImage.setTag(color); 
            btnAddImage.setOnClickListener(v -> {
                activity.currentColorUploading = color;
                activity.currentVariantHolder = this;
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                activity.startActivityForResult(Intent.createChooser(intent, "Select Pictures"), PICK_IMAGE_REQUEST);
            });
            
            colorView.setTag(color); 
            updateImageStatus(color, 0); // Refresh images
        }

        private void restoreCustomSection(BikeVariantModel.CustomSection section) {
            View sectionView = LayoutInflater.from(activity).inflate(R.layout.item_dynamic_section, llDynamic, false);
            llDynamic.addView(sectionView);
            
            TextView tvTitle = sectionView.findViewById(R.id.tvSectionTitle);
            tvTitle.setText(section.sectionName);
            
            ImageButton btnDel = sectionView.findViewById(R.id.btnDeleteSection);
            LinearLayout localFields = sectionView.findViewById(R.id.llFieldsContainer);
            TextView btnAddField = sectionView.findViewById(R.id.btnAddField);
            
            btnDel.setOnClickListener(v -> {
                llDynamic.removeView(sectionView);
                model.customSections.remove(section);
            });
            
            btnAddField.setOnClickListener(v -> {
               addDynamicField(section, localFields, new BikeVariantModel.CustomField("", ""));
            });

            if (section.fields != null) {
                for(BikeVariantModel.CustomField f : section.fields) {
                    addDynamicField(section, localFields, f);
                }
            }
        }

        private void addDynamicField(BikeVariantModel.CustomSection section, LinearLayout localFields, BikeVariantModel.CustomField field) {
            if (!section.fields.contains(field)) section.fields.add(field);
            
             View fieldView = LayoutInflater.from(activity).inflate(R.layout.item_dynamic_field, localFields, false);
                localFields.addView(fieldView);
                
                EditText etKey = fieldView.findViewById(R.id.etFieldKey);
                EditText etVal = fieldView.findViewById(R.id.etFieldValue);
                etKey.setText(field.key);
                etVal.setText(field.value);
                
                etKey.addTextChangedListener(new TextWatcher() {
                    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                    @Override public void onTextChanged(CharSequence s, int start, int before, int count) { field.key = s.toString(); }
                    @Override public void afterTextChanged(Editable s) {}
                });
                
                etVal.addTextChangedListener(new TextWatcher() {
                    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                    @Override public void onTextChanged(CharSequence s, int start, int before, int count) { field.value = s.toString(); }
                    @Override public void afterTextChanged(Editable s) {}
                });
        }

        
        // This is called by Activity onActivityResult
        public void updateImageStatus(BikeVariantModel.VariantColor color, int count) {
             // Find the specific view for this color
             for(int i=0; i<llColors.getChildCount(); i++) {
                 View v = llColors.getChildAt(i);
                 if (v.getTag() == color) {
                     TextView tv = v.findViewById(R.id.tvImageStatus);
                     int current = (color.imagePaths != null ? color.imagePaths.size() : 0) + (color.tempImageUris != null ? color.tempImageUris.size() : 0);
                     tv.setText(current + " Images Selected");
                     
                     // Also add thumbnails
                     LinearLayout llImages = v.findViewById(R.id.llImagesContainer);
                     // Clear existing thumbnails (except add button at index 0)
                     while(llImages.getChildCount() > 1) {
                         llImages.removeViewAt(1);
                     }
                     
                     // 1. Existing Server Images
                     if (color.imagePaths != null) {
                         for(String serverPath : color.imagePaths) {
                             addThumbnail(llImages, serverPath, true);
                         }
                     }

                     // 2. New Local Images
                     if (color.tempImageUris != null) {
                         for(String uriStr : color.tempImageUris) {
                             addThumbnail(llImages, uriStr, false);
                         }
                     }
                     break;
                 }
             }
        }
        
        private void addThumbnail(LinearLayout container, String path, boolean isServer) {
             android.widget.ImageView iv = new android.widget.ImageView(activity);
             LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(150, 150);
             lp.setMargins(0, 0, 16, 0);
             iv.setLayoutParams(lp);
             iv.setScaleType(android.widget.ImageView.ScaleType.CENTER_CROP);
             iv.setBackgroundResource(R.drawable.bg_rounded_gray_50);
             
             if (isServer) {
                 String url = path;
                 if (!url.startsWith("http")) {
                     url = com.example.motovista_deep.api.RetrofitClient.BASE_URL + path;
                 }
                 com.bumptech.glide.Glide.with(activity).load(url).into(iv);
             } else {
                 iv.setImageURI(Uri.parse(path));
             }
             
             container.addView(iv);
        }

        private void showAddSectionDialog() {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(activity);
            builder.setTitle("Add Custom Section");
            
            final EditText input = new EditText(activity);
            input.setHint("Section Name (e.g. Electronics)");
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            input.setLayoutParams(lp);
            builder.setView(input);

            builder.setPositiveButton("Add", (dialog, which) -> {
                String sectionName = input.getText().toString().trim();
                if (!sectionName.isEmpty()) {
                    addDynamicSection(sectionName);
                }
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

            builder.show();
        }

        private void addDynamicSection(String title) {
            BikeVariantModel.CustomSection section = new BikeVariantModel.CustomSection(title);
            model.customSections.add(section);
            
            View sectionView = LayoutInflater.from(activity).inflate(R.layout.item_dynamic_section, llDynamic, false);
            llDynamic.addView(sectionView);
            
            TextView tvTitle = sectionView.findViewById(R.id.tvSectionTitle);
            tvTitle.setText(title);
            
            ImageButton btnDel = sectionView.findViewById(R.id.btnDeleteSection);
            LinearLayout localFields = sectionView.findViewById(R.id.llFieldsContainer);
            TextView btnAddField = sectionView.findViewById(R.id.btnAddField);
            
            btnDel.setOnClickListener(v -> {
                llDynamic.removeView(sectionView);
                model.customSections.remove(section);
            });
            
            btnAddField.setOnClickListener(v -> {
                 addDynamicField(section, localFields, new BikeVariantModel.CustomField("", ""));
            });
        }

        public void syncData() {
            model.variantName = etVariantName.getText().toString().trim().toUpperCase();
            
            model.priceDetails.exShowroom = etEx.getText().toString();
            model.priceDetails.insurance = etIns.getText().toString();
            model.priceDetails.registration = etReg.getText().toString();
            model.priceDetails.ltrt = etLtrt.getText().toString();
            
            model.brakesWheels.frontBrake = spFront.getSelectedItem().toString();
            model.brakesWheels.rearBrake = spRear.getSelectedItem().toString();
            model.brakesWheels.brakingSystem = spSys.getSelectedItem().toString();
            model.brakesWheels.wheelType = spWheel.getSelectedItem().toString();
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            int count = 0;
            if (data.getClipData() != null) {
                count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    if (currentColorUploading != null) currentColorUploading.tempImageUris.add(imageUri.toString());
                }
            } else if (data.getData() != null) {
                Uri imageUri = data.getData();
                if (currentColorUploading != null) currentColorUploading.tempImageUris.add(imageUri.toString());
                count = 1;
            }
            if (currentVariantHolder != null) currentVariantHolder.updateImageStatus(currentColorUploading, count);
        }
    }
}