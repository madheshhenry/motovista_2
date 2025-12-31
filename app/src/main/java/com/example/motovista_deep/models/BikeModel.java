package com.example.motovista_deep.models;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;

public class BikeModel implements Parcelable {

    private int id;
    private String brand;
    private String model;
    private String price;
    private String condition;
    private String image_url;
    private ArrayList<String> imageUrls = new ArrayList<>();
    private String type;
    private int is_featured;

    private String variant;
    private String engine_cc;
    private String year;
    private String fuel_type;
    private String transmission;
    private String braking_type;
    private String on_road_price;
    private String insurance;
    private String registration_charge;
    private String ltrt;
    private String mileage;
    private String fuel_tank_capacity;
    private String kerb_weight;
    private String seat_height;
    private String ground_clearance;
    private String top_speed;
    private String warranty_period;
    private String free_services_count;
    private String features;
    private String price_disclaimer;
    private String registration_proof;

    private String odometer;
    private String owner_details;
    private String condition_details;

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
        if (image_url != null && !image_url.isEmpty()) {
            return image_url;
        }

        if (!imageUrls.isEmpty()) {
            return imageUrls.get(0);
        }

        if (!all_images.isEmpty()) {
            return all_images.get(0);
        }

        return "";
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

    // Check if image paths exist
    public boolean hasImages() {
        return (all_images != null && !all_images.isEmpty()) ||
                (imageUrls != null && !imageUrls.isEmpty()) ||
                (image_url != null && !image_url.isEmpty());
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

    public String getOdometer() { return odometer != null ? odometer : ""; }
    public void setOdometer(String odometer) { this.odometer = odometer; }

    public String getOwnerDetails() { return owner_details != null ? owner_details : ""; }
    public void setOwnerDetails(String owner_details) { this.owner_details = owner_details; }

    public String getConditionDetails() { return condition_details != null ? condition_details : ""; }
    public void setConditionDetails(String condition_details) { this.condition_details = condition_details; }

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
        odometer = in.readString();
        owner_details = in.readString();
        condition_details = in.readString();
        all_images = in.createStringArrayList();
        baseUrl = in.readString();
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
        dest.writeString(odometer);
        dest.writeString(owner_details);
        dest.writeString(condition_details);
        dest.writeStringList(all_images);
        dest.writeString(baseUrl);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BikeModel> CREATOR = new Creator<BikeModel>() {
        @Override
        public BikeModel createFromParcel(Parcel in) {
            BikeModel bike = new BikeModel();
            bike.id = in.readInt();
            bike.brand = in.readString();
            bike.model = in.readString();
            bike.price = in.readString();
            bike.condition = in.readString();
            bike.image_url = in.readString();
            bike.imageUrls = in.createStringArrayList();
            bike.type = in.readString();
            bike.is_featured = in.readInt();
            bike.variant = in.readString();
            bike.engine_cc = in.readString();
            bike.year = in.readString();
            bike.fuel_type = in.readString();
            bike.transmission = in.readString();
            bike.braking_type = in.readString();
            bike.on_road_price = in.readString();
            bike.insurance = in.readString();
            bike.registration_charge = in.readString();
            bike.ltrt = in.readString();
            bike.mileage = in.readString();
            bike.fuel_tank_capacity = in.readString();
            bike.kerb_weight = in.readString();
            bike.seat_height = in.readString();
            bike.ground_clearance = in.readString();
            bike.top_speed = in.readString();
            bike.warranty_period = in.readString();
            bike.free_services_count = in.readString();
            bike.features = in.readString();
            bike.price_disclaimer = in.readString();
            bike.registration_proof = in.readString();
            bike.odometer = in.readString();
            bike.owner_details = in.readString();
            bike.condition_details = in.readString();
            bike.all_images = in.createStringArrayList();
            bike.baseUrl = in.readString();
            return bike;
        }

        @Override
        public BikeModel[] newArray(int size) {
            return new BikeModel[size];
        }
    };
}