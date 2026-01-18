package com.example.motovista_deep.models;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;

public class BikeModel implements Parcelable {

    @com.google.gson.annotations.SerializedName("id")
    private int id;

    @com.google.gson.annotations.SerializedName("brand")
    private String brand;

    @com.google.gson.annotations.SerializedName("model")
    private String model;

    @com.google.gson.annotations.SerializedName("price") // Note: Database uses on_road_price mostly, but keeping this
    private String price;

    @com.google.gson.annotations.SerializedName("condition_type")
    private String condition;

    @com.google.gson.annotations.SerializedName(value="image_url", alternate={"thumbnail", "image_path", "image"})
    private String image_url;

    private ArrayList<String> imageUrls = new ArrayList<>();

    @com.google.gson.annotations.SerializedName("type")
    private String type;

    private int is_featured;

    @com.google.gson.annotations.SerializedName("variant")
    private String variant;

    @com.google.gson.annotations.SerializedName("engine_cc")
    private String engine_cc;

    @com.google.gson.annotations.SerializedName("year")
    private String year;

    @com.google.gson.annotations.SerializedName("fuel_type")
    private String fuel_type;

    @com.google.gson.annotations.SerializedName("transmission")
    private String transmission;

    @com.google.gson.annotations.SerializedName("braking_type")
    private String braking_type;

    @com.google.gson.annotations.SerializedName("on_road_price")
    private String on_road_price;

    @com.google.gson.annotations.SerializedName("ex_showroom_price")
    private String ex_showroom_price;

    @com.google.gson.annotations.SerializedName("insurance_price")
    private String insurance;

    @com.google.gson.annotations.SerializedName("registration_price")
    private String registration_charge;

    @com.google.gson.annotations.SerializedName("ltrt_price")
    private String ltrt;

    @com.google.gson.annotations.SerializedName("mileage")
    private String mileage;

    @com.google.gson.annotations.SerializedName("fuel_tank_capacity")
    private String fuel_tank_capacity;

    @com.google.gson.annotations.SerializedName("kerb_weight")
    private String kerb_weight;

    @com.google.gson.annotations.SerializedName("seat_height")
    private String seat_height;

    @com.google.gson.annotations.SerializedName("ground_clearance")
    private String ground_clearance;

    @com.google.gson.annotations.SerializedName("top_speed")
    private String top_speed;

    @com.google.gson.annotations.SerializedName("warranty")
    private String warranty_period;

    @com.google.gson.annotations.SerializedName("free_services")
    private String free_services_count;

    @com.google.gson.annotations.SerializedName("features")
    private String features;

    @com.google.gson.annotations.SerializedName("price_disclaimer")
    private String price_disclaimer;

    @com.google.gson.annotations.SerializedName("registration_proof")
    private String registration_proof;

    // NEW FIELDS - ADDED HERE
    @com.google.gson.annotations.SerializedName("date")
    private String date;

    @com.google.gson.annotations.SerializedName("engine_number")
    private String engine_number;

    @com.google.gson.annotations.SerializedName("chassis_number")
    private String chassis_number;

    @com.google.gson.annotations.SerializedName("max_torque")
    private String max_torque;

    @com.google.gson.annotations.SerializedName("max_power")
    private String max_power;

    @com.google.gson.annotations.SerializedName("odometer")
    private String odometer;

    @com.google.gson.annotations.SerializedName("color_name")
    private String color_name;

    @com.google.gson.annotations.SerializedName("color_hex")
    private String color_hex;

// ... (existing code)


// ... (existing code)


    @com.google.gson.annotations.SerializedName("owner_details")
    private String owner_details;
    
    @com.google.gson.annotations.SerializedName("ownership")
    private String ownership;

    @com.google.gson.annotations.SerializedName("condition_details")
    private String condition_details;

    @com.google.gson.annotations.SerializedName("colors")
    private java.util.List<String> colors;

    @com.google.gson.annotations.SerializedName("color_images")
    private Object colorImagesRaw; // Can be String or Map inside JSON

    private java.util.Map<String, java.util.List<String>> colorImagesMap = new java.util.HashMap<>();

    @com.google.gson.annotations.SerializedName("custom_fittings")
    private java.util.List<CustomFitting> custom_fittings;

// ... (skipping to methods)

// In Constructor


    /* ========== GETTERS AND SETTERS ========== */

    // ...

    public java.util.Map<String, java.util.List<String>> getColorImages() {
        if ((colorImagesMap == null || colorImagesMap.isEmpty()) && colorImagesRaw != null) {
            colorImagesMap = new java.util.HashMap<>();
            // Manually parse JSON string or Map
            String jsonStr = "";
            if (colorImagesRaw instanceof String) {
                jsonStr = (String) colorImagesRaw;
            } else if (colorImagesRaw instanceof com.google.gson.internal.LinkedTreeMap) {
                try {
                    com.google.gson.Gson gson = new com.google.gson.Gson();
                    String temp = gson.toJson(colorImagesRaw);
                    jsonStr = temp;
                } catch(Exception e){}
            }
            
            if (!jsonStr.isEmpty()) {
                // Basic JSON parser
                try {
                     org.json.JSONObject jsonObj = new org.json.JSONObject(jsonStr);
                     java.util.Iterator<String> keys = jsonObj.keys();
                     while(keys.hasNext()) {
                         String key = keys.next();
                         org.json.JSONArray arr = jsonObj.optJSONArray(key);
                         if (arr != null) {
                             java.util.List<String> list = new java.util.ArrayList<>();
                             for(int i=0; i<arr.length(); i++) {
                                 list.add(arr.optString(i));
                             }
                             colorImagesMap.put(key, list);
                         }
                     }
                } catch(Exception e) {
                     // Retry logic
                     if (jsonStr.startsWith("\"") && jsonStr.endsWith("\"")) {
                        jsonStr = jsonStr.substring(1, jsonStr.length()-1).replace("\\\"", "\"");
                        try {
                             org.json.JSONObject jsonObj = new org.json.JSONObject(jsonStr);
                             java.util.Iterator<String> keys = jsonObj.keys();
                             while(keys.hasNext()) {
                                 String key = keys.next();
                                 org.json.JSONArray arr = jsonObj.optJSONArray(key);
                                 if (arr != null) {
                                     java.util.List<String> list = new java.util.ArrayList<>();
                                     for(int i=0; i<arr.length(); i++) {
                                         list.add(arr.optString(i));
                                     }
                                     colorImagesMap.put(key, list);
                                 }
                             }
                        } catch(Exception ex) {}
                    }
                }
            }
        }
        return colorImagesMap; 
    }
    
    public void setColorImages(java.util.Map<String, java.util.List<String>> color_images) { this.colorImagesMap = color_images; }



    @com.google.gson.annotations.SerializedName("mandatory_fittings")
    private java.util.List<CustomFitting> mandatory_fittings;

    @com.google.gson.annotations.SerializedName("additional_fittings")
    private java.util.List<CustomFitting> additional_fittings;
    
    @com.google.gson.annotations.SerializedName("image_paths") // Mapped from API
    private Object imagePathsRaw; // Can be String or List inside JSON

    private ArrayList<String> all_images = new ArrayList<>();

    // Base URL - will be set from adapter
    private String baseUrl;

    public BikeModel() {}

    /* ========== IMAGE METHODS ========== */

    // Set base URL
    public void setBaseUrl(String baseUrl) {
        if (baseUrl != null) {
            this.baseUrl = baseUrl;
            if (!this.baseUrl.endsWith("/")) {
                this.baseUrl += "/";
            }
        }
    }

    public String getExShowroomPrice() { return ex_showroom_price; }
    public void setExShowroomPrice(String ex_showroom_price) { this.ex_showroom_price = ex_showroom_price; }

    public String getBaseUrl() {
        return baseUrl;
    }

    // Get full image URL
    public String getFullImageUrl(String path) {
        if (path == null || path.isEmpty()) {
            return "";
        }

        // Clean the path
        path = path.trim();
        path = path.replace("\"", "").replace("\\", "");

        // If already a full URL, return as is
        if (path.startsWith("http://") || path.startsWith("https://")) {
            return path;
        }

        // Add uploads/ if missing
        if (!path.contains("uploads/")) {
            if (path.startsWith("bikes/") || path.startsWith("second_hand_bikes/")) {
                path = "uploads/" + path;
            } else if (!path.startsWith("uploads/")) {
                path = "uploads/bikes/" + path;
            }
        }

        // Add base URL
        if (baseUrl != null && !baseUrl.isEmpty()) {
            String base = baseUrl;
            if (!base.endsWith("/")) {
                base += "/";
            }
            String fullUrl = base + path;
            return fullUrl;
        }

        return path;
    }

    // Set all images from API
    public void setAllImages(ArrayList<String> images) {
        if (images != null) {
            this.all_images = new ArrayList<>();
            this.imageUrls = new ArrayList<>();

            for (String img : images) {
                if (img != null && !img.trim().isEmpty()) {
                    String cleanedImg = img.trim()
                            .replace("\"", "")
                            .replace("\\", "")
                            .replace("[", "")
                            .replace("]", "");

                    if (!cleanedImg.isEmpty()) {
                        String fullUrl = getFullImageUrl(cleanedImg);
                        this.all_images.add(fullUrl);
                        this.imageUrls.add(fullUrl);
                    }
                }
            }

            // Set first image as image_url
            if (!all_images.isEmpty()) {
                this.image_url = all_images.get(0);
            }
        }
    }

    // Get all images for slider
    public ArrayList<String> getAllImages() {
        // 1. Lazy Parse imagePathsRaw
        if ((all_images == null || all_images.isEmpty()) && imagePathsRaw != null) {
             all_images = new ArrayList<>();
             if (imagePathsRaw instanceof java.util.List) {
                 java.util.List<?> list = (java.util.List<?>) imagePathsRaw;
                 for (Object item : list) {
                     if (item != null) all_images.add(item.toString());
                 }
             } else if (imagePathsRaw instanceof String) {
                 String raw = (String) imagePathsRaw;
                 setImagePath(raw); // helper
             }
        }

        // 2. Fallback to color_images if empty
        if ((all_images == null || all_images.isEmpty())) {
             java.util.Map<String, java.util.List<String>> map = getColorImages();
             if (map != null && !map.isEmpty()) {
                 if (all_images == null) all_images = new ArrayList<>();
                 for (java.util.List<String> paths : map.values()) {
                     if (paths != null) {
                         for (String p : paths) {
                             String full = getFullImageUrl(p);
                             if (!all_images.contains(full)) all_images.add(full);
                         }
                     }
                 }
             }
        }

        if (all_images != null && !all_images.isEmpty()) {
            return all_images;
        }

        if (imageUrls != null && !imageUrls.isEmpty()) {
            return imageUrls;
        }

        ArrayList<String> result = new ArrayList<>();
        if (image_url != null && !image_url.isEmpty()) {
            result.add(image_url);
        }

        return result;
    }

    // Set image path from database
    public void setImagePath(String imagePath) {
        imageUrls.clear();
        all_images.clear();

        if (imagePath == null || imagePath.trim().isEmpty()) {
            image_url = "";
            return;
        }

        String cleaned = imagePath.trim();

        // Clean JSON brackets if present
        if (cleaned.startsWith("[") && cleaned.endsWith("]")) {
            cleaned = cleaned.substring(1, cleaned.length() - 1);
        }

        // Remove quotes and backslashes
        cleaned = cleaned.replace("\"", "").replace("\\", "");

        // Split by comma
        String[] paths = cleaned.split(",");

        for (String path : paths) {
            path = path.trim();
            if (!path.isEmpty()) {
                String fullUrl = getFullImageUrl(path);
                if (!fullUrl.isEmpty()) {
                    imageUrls.add(fullUrl);
                    all_images.add(fullUrl);
                }
            }
        }

        // Set first image
        if (!imageUrls.isEmpty()) {
            image_url = imageUrls.get(0);
        }
    }

    // Get first image for list view
    public String getImageUrl() {
        return getAllImages().isEmpty() ? "" : getAllImages().get(0);
    }

    // Set raw image URL
    public void setImageUrl(String image_url) {
        if (image_url == null || image_url.isEmpty()) {
            this.image_url = "";
            return;
        }

        String cleaned = image_url.trim()
                .replace("\"", "")
                .replace("\\", "")
                .replace("[", "")
                .replace("]", "");

        String fullUrl = getFullImageUrl(cleaned);
        this.image_url = fullUrl;

        // Also add to imageUrls if not already there
        if (!fullUrl.isEmpty() && !imageUrls.contains(fullUrl)) {
            imageUrls.add(fullUrl);
            all_images.add(fullUrl);
        }
    }

    // Get image URLs for adapter
    public ArrayList<String> getImageUrls() {
        return getAllImages();
    }

    // Set image URLs directly (Used for variant updates)
    public void setImageUrls(java.util.List<String> newImages) {
        if (newImages != null) {
            this.imageUrls = new java.util.ArrayList<>(newImages);
            this.all_images = new java.util.ArrayList<>(newImages);
            if (!newImages.isEmpty()) {
                this.image_url = newImages.get(0);
            }
        } else {
             this.imageUrls = new java.util.ArrayList<>();
             this.all_images = new java.util.ArrayList<>();
        }
    }

    // Check if image paths exist
    public boolean hasImages() {
        return !getAllImages().isEmpty();
    }

    /* ========== GETTERS AND SETTERS ========== */
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getBrand() { return brand != null ? brand : ""; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model != null ? model : ""; }
    public void setModel(String model) { this.model = model; }

    public String getPrice() { return price != null ? price : ""; }
    public void setPrice(String price) { this.price = price; }

    public String getCondition() { return condition != null ? condition : ""; }
    public void setCondition(String condition) { this.condition = condition; }

    public String getType() { return type != null ? type : ""; }
    public void setType(String type) { this.type = type; }

    public int getIsFeatured() { return is_featured; }
    public void setIsFeatured(int is_featured) { this.is_featured = is_featured; }

    public String getVariant() { return variant != null ? variant : ""; }
    public void setVariant(String variant) { this.variant = variant; }

    public String getEngineCC() { return engine_cc != null ? engine_cc : ""; }
    public void setEngineCC(String engine_cc) { this.engine_cc = engine_cc; }

    public String getYear() { return year != null ? year : ""; }
    public void setYear(String year) { this.year = year; }

    public String getFuelType() { return fuel_type != null ? fuel_type : ""; }
    public void setFuelType(String fuel_type) { this.fuel_type = fuel_type; }

    public String getTransmission() { return transmission != null ? transmission : ""; }
    public void setTransmission(String transmission) { this.transmission = transmission; }

    public String getBrakingType() { return braking_type != null ? braking_type : ""; }
    public void setBrakingType(String braking_type) { this.braking_type = braking_type; }

    public String getOnRoadPrice() { return on_road_price != null ? on_road_price : ""; }
    public void setOnRoadPrice(String on_road_price) { this.on_road_price = on_road_price; }

    public String getInsurance() { return insurance != null ? insurance : ""; }
    public void setInsurance(String insurance) { this.insurance = insurance; }

    public String getRegistrationCharge() { return registration_charge != null ? registration_charge : ""; }
    public void setRegistrationCharge(String registration_charge) { this.registration_charge = registration_charge; }

    public String getLtrt() { return ltrt != null ? ltrt : ""; }
    public void setLtrt(String ltrt) { this.ltrt = ltrt; }

    public String getMileage() { return mileage != null ? mileage : ""; }
    public void setMileage(String mileage) { this.mileage = mileage; }

    public String getFuelTankCapacity() { return fuel_tank_capacity != null ? fuel_tank_capacity : ""; }
    public void setFuelTankCapacity(String fuel_tank_capacity) { this.fuel_tank_capacity = fuel_tank_capacity; }

    public String getKerbWeight() { return kerb_weight != null ? kerb_weight : ""; }
    public void setKerbWeight(String kerb_weight) { this.kerb_weight = kerb_weight; }

    public String getSeatHeight() { return seat_height != null ? seat_height : ""; }
    public void setSeatHeight(String seat_height) { this.seat_height = seat_height; }

    public String getGroundClearance() { return ground_clearance != null ? ground_clearance : ""; }
    public void setGroundClearance(String ground_clearance) { this.ground_clearance = ground_clearance; }

    public String getTopSpeed() { return top_speed != null ? top_speed : ""; }
    public void setTopSpeed(String top_speed) { this.top_speed = top_speed; }

    public String getWarrantyPeriod() { return warranty_period != null ? warranty_period : ""; }
    public void setWarrantyPeriod(String warranty_period) { this.warranty_period = warranty_period; }

    public String getFreeServicesCount() { return free_services_count != null ? free_services_count : ""; }
    public void setFreeServicesCount(String free_services_count) { this.free_services_count = free_services_count; }

    public String getFeatures() { return features != null ? features : ""; }
    public void setFeatures(String features) { this.features = features; }

    public String getPriceDisclaimer() { return price_disclaimer != null ? price_disclaimer : ""; }
    public void setPriceDisclaimer(String price_disclaimer) { this.price_disclaimer = price_disclaimer; }

    public String getRegistrationProof() { return registration_proof != null ? registration_proof : ""; }
    public void setRegistrationProof(String registration_proof) { this.registration_proof = registration_proof; }

    // NEW GETTERS AND SETTERS
    public String getDate() { return date != null ? date : ""; }
    public void setDate(String date) { this.date = date; }

    public String getEngine_number() { return engine_number != null ? engine_number : ""; }
    public void setEngine_number(String engine_number) { this.engine_number = engine_number; }

    public String getChassis_number() { return chassis_number != null ? chassis_number : ""; }
    public void setChassis_number(String chassis_number) { this.chassis_number = chassis_number; }

    public String getMaxTorque() { return max_torque != null ? max_torque : ""; }
    public void setMaxTorque(String max_torque) { this.max_torque = max_torque; }

    public String getMaxPower() { return max_power != null ? max_power : ""; }
    public void setMaxPower(String max_power) { this.max_power = max_power; }



    // Convenience getters for BikeDetailsActivity
    public String getFuel_type() { return fuel_type != null ? fuel_type : ""; }

    public String getFuel_tank() { return fuel_tank_capacity != null ? fuel_tank_capacity : ""; }

    public String getKerb_weight() { return kerb_weight != null ? kerb_weight : ""; }

    public String getSeat_height() { return seat_height != null ? seat_height : ""; }

    public String getGround_clearance() { return ground_clearance != null ? ground_clearance : ""; }

    public String getWarranty() { return warranty_period != null ? warranty_period : ""; }

    public String getFree_services() { return free_services_count != null ? free_services_count : ""; }

    public String getRegistration_proof() { return registration_proof != null ? registration_proof : ""; }

    public String getPrice_disclaimer() { return price_disclaimer != null ? price_disclaimer : ""; }

    public String getOdometer() { return odometer != null ? odometer : ""; }
    public void setOdometer(String odometer) { this.odometer = odometer; }

    public String getOwnerDetails() { return owner_details != null ? owner_details : ""; }
    public void setOwnerDetails(String owner_details) { this.owner_details = owner_details; }

    public String getConditionDetails() { return condition_details != null ? condition_details : ""; }
    public void setConditionDetails(String condition_details) { this.condition_details = condition_details; }

    public String getOwnership() { return ownership != null ? ownership : ""; }
    public void setOwnership(String ownership) { this.ownership = ownership; }

    public String getColor_name() { return color_name != null ? color_name : ""; }
    public void setColor_name(String color_name) { this.color_name = color_name; }

    public String getColor_hex() { return color_hex != null ? color_hex : ""; }
    public void setColor_hex(String color_hex) { this.color_hex = color_hex; }

    public java.util.List<String> getColors() { return colors; }
    public void setColors(java.util.List<String> colors) { this.colors = colors; }



    @com.google.gson.annotations.SerializedName("front_brake")
    private String frontBrake;

    @com.google.gson.annotations.SerializedName("rear_brake")
    private String rearBrake;

    @com.google.gson.annotations.SerializedName("abs_type")
    private String absType;

    @com.google.gson.annotations.SerializedName("wheel_type")
    private String wheelType;



    public String getFrontBrake() { return frontBrake != null ? frontBrake : ""; }
    public void setFrontBrake(String frontBrake) { this.frontBrake = frontBrake; }

    public String getRearBrake() { return rearBrake != null ? rearBrake : ""; }
    public void setRearBrake(String rearBrake) { this.rearBrake = rearBrake; }

    public String getAbsType() { return absType != null ? absType : ""; }
    public void setAbsType(String absType) { this.absType = absType; }

    public String getWheelType() { return wheelType != null ? wheelType : ""; }
    public void setWheelType(String wheelType) { this.wheelType = wheelType; }

    public java.util.List<CustomFitting> getCustomFittings() { return custom_fittings; }
    public void setCustomFittings(java.util.List<CustomFitting> custom_fittings) { this.custom_fittings = custom_fittings; }

    public java.util.List<CustomFitting> getMandatoryFittings() { return mandatory_fittings; }
    public void setMandatoryFittings(java.util.List<CustomFitting> mandatory_fittings) { this.mandatory_fittings = mandatory_fittings; }

    public java.util.List<CustomFitting> getAdditionalFittings() { return additional_fittings; }
    public void setAdditionalFittings(java.util.List<CustomFitting> additional_fittings) { this.additional_fittings = additional_fittings; }

    // Variants List Field
    private java.util.List<BikeVariantModel> variants;
    public java.util.List<BikeVariantModel> getVariants() { return variants; }
    public void setVariants(java.util.List<BikeVariantModel> variants) { this.variants = variants; }

    /* ========== PARCELABLE ========== */
    protected BikeModel(Parcel in) {
        id = in.readInt();
        brand = in.readString();
        model = in.readString();
        price = in.readString();
        condition = in.readString();
        image_url = in.readString();
        imageUrls = in.createStringArrayList();
        type = in.readString();
        is_featured = in.readInt();
        variant = in.readString();
        engine_cc = in.readString();
        year = in.readString();
        fuel_type = in.readString();
        transmission = in.readString();
        braking_type = in.readString();
        on_road_price = in.readString();
        ex_showroom_price = in.readString(); // Fix: Was missing
        insurance = in.readString();
        registration_charge = in.readString();
        ltrt = in.readString();
        mileage = in.readString();
        fuel_tank_capacity = in.readString();
        kerb_weight = in.readString();
        seat_height = in.readString();
        ground_clearance = in.readString();
        top_speed = in.readString();
        warranty_period = in.readString();
        free_services_count = in.readString();
        features = in.readString();
        price_disclaimer = in.readString();
        registration_proof = in.readString();
        // Read new fields
        date = in.readString();
        engine_number = in.readString();
        chassis_number = in.readString();
        odometer = in.readString();
        owner_details = in.readString();
        condition_details = in.readString();
        ownership = in.readString();
        colors = in.createStringArrayList();
        custom_fittings = in.createTypedArrayList(CustomFitting.CREATOR);
        mandatory_fittings = in.createTypedArrayList(CustomFitting.CREATOR);
        additional_fittings = in.createTypedArrayList(CustomFitting.CREATOR);
        variants = in.createTypedArrayList(BikeVariantModel.CREATOR); // Added
        all_images = in.createStringArrayList();
        baseUrl = in.readString();
        max_torque = in.readString();
        max_power = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(brand);
        dest.writeString(model);
        dest.writeString(price);
        dest.writeString(condition);
        dest.writeString(image_url);
        dest.writeStringList(imageUrls);
        dest.writeString(type);
        dest.writeInt(is_featured);
        dest.writeString(variant);
        dest.writeString(engine_cc);
        dest.writeString(year);
        dest.writeString(fuel_type);
        dest.writeString(transmission);
        dest.writeString(braking_type);
        dest.writeString(on_road_price);
        dest.writeString(ex_showroom_price); // Added
        dest.writeString(insurance);
        dest.writeString(registration_charge);
        dest.writeString(ltrt);
        dest.writeString(mileage);
        dest.writeString(fuel_tank_capacity);
        dest.writeString(kerb_weight);
        dest.writeString(seat_height);
        dest.writeString(ground_clearance);
        dest.writeString(top_speed);
        dest.writeString(warranty_period);
        dest.writeString(free_services_count);
        dest.writeString(features);
        dest.writeString(price_disclaimer);
        dest.writeString(registration_proof);
        // Write new fields
        dest.writeString(date);
        dest.writeString(engine_number);
        dest.writeString(chassis_number);
        dest.writeString(odometer);
        dest.writeString(owner_details);
        dest.writeString(condition_details);
        dest.writeString(ownership);
        dest.writeStringList(colors);
        dest.writeTypedList(custom_fittings);
        dest.writeTypedList(mandatory_fittings);
        dest.writeTypedList(additional_fittings);
        dest.writeTypedList(variants); // Added
        dest.writeStringList(all_images);
        dest.writeString(baseUrl);
        dest.writeString(max_torque);
        dest.writeString(max_power);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BikeModel> CREATOR = new Creator<BikeModel>() {
        @Override
        public BikeModel createFromParcel(Parcel in) {
            return new BikeModel(in);
        }

        @Override
        public BikeModel[] newArray(int size) {
            return new BikeModel[size];
        }
    };
}