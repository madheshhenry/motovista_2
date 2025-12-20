package com.example.motovista_deep.models;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BikeModel implements Parcelable {

    private int id;
    private String brand;
    private String model;
    private String price;
    private String condition;
    private String image_url;  // First image URL
    private ArrayList<String> imageUrls = new ArrayList<>(); // All images
    private String type;
    private int is_featured;

    // New bike fields
    private String on_road_price;
    private String engine_cc;
    private String mileage;
    private String top_speed;
    private String braking_type;
    private String features;

    // Second-hand bike fields
    private String year;
    private String odometer;
    private String owner_details;

    // All images array from API
    private ArrayList<String> all_images = new ArrayList<>();

    // Add base URL field (will be set from RetrofitClient)
    private String baseUrl = "";

    public BikeModel() {}

    /* ========== IMAGE METHODS ========== */

    // Set base URL once
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        if (this.baseUrl != null && !this.baseUrl.endsWith("/")) {
            this.baseUrl += "/";
        }
    }

    // Set all images from array (from API)
    public void setAllImages(ArrayList<String> images) {
        if (images != null) {
            this.all_images = new ArrayList<>();
            this.imageUrls = new ArrayList<>();

            for (String img : images) {
                String fullUrl = buildFullUrl(img);
                this.all_images.add(fullUrl);
                this.imageUrls.add(fullUrl);
            }

            // Set first image as image_url
            if (!all_images.isEmpty() && (image_url == null || image_url.isEmpty())) {
                this.image_url = all_images.get(0);
            }
        }
    }

    // Get all images
    public ArrayList<String> getAllImages() {
        if (all_images != null && !all_images.isEmpty()) {
            return all_images;
        }
        return imageUrls;
    }

    // Convert string to full URLs
    public void setImageUrlsFromString(String imagePath) {
        imageUrls.clear();
        all_images.clear();

        if (imagePath == null || imagePath.trim().isEmpty()) return;

        String cleaned = imagePath.trim();

        // Remove JSON brackets if present
        if (cleaned.startsWith("[") && cleaned.endsWith("]")) {
            cleaned = cleaned.substring(1, cleaned.length() - 1);
        }

        // Remove quotes
        cleaned = cleaned.replace("\"", "");

        // Split by comma
        String[] paths = cleaned.split(",");

        for (String path : paths) {
            path = path.trim();
            if (!path.isEmpty()) {
                String fullUrl = buildFullUrl(path);
                imageUrls.add(fullUrl);
                all_images.add(fullUrl);
            }
        }

        // Set first image
        if (!imageUrls.isEmpty() && (image_url == null || image_url.isEmpty())) {
            image_url = imageUrls.get(0);
        }
    }

    // Build full URL from relative path
    private String buildFullUrl(String path) {
        if (path == null || path.isEmpty()) {
            return "";
        }

        // If already a full URL, return as is
        if (path.startsWith("http://") || path.startsWith("https://")) {
            return path;
        }

        // Remove leading slash if present
        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        // Add base URL
        if (baseUrl != null && !baseUrl.isEmpty()) {
            return baseUrl + path;
        }

        // If no base URL, return relative path (will cause error but at least it's consistent)
        return path;
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

    // Set raw image URL (convert to full URL)
    public void setImageUrlRaw(String image_url) {
        this.image_url = buildFullUrl(image_url);

        // Also add to imageUrls if not already there
        if (this.image_url != null && !this.image_url.isEmpty() && !imageUrls.contains(this.image_url)) {
            imageUrls.add(this.image_url);
            all_images.add(this.image_url);
        }
    }

    // Get raw image URL
    public String getImageUrlRaw() {
        return image_url;
    }

    // Get all image URLs for slider
    public ArrayList<String> getImageUrls() {
        if (!all_images.isEmpty()) {
            return all_images;
        }
        if (!imageUrls.isEmpty()) {
            return imageUrls;
        }
        return new ArrayList<>();
    }

    // Other getters and setters remain the same...
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

    public String getOnRoadPrice() { return on_road_price != null ? on_road_price : ""; }
    public void setOnRoadPrice(String on_road_price) { this.on_road_price = on_road_price; }

    public String getEngineCC() { return engine_cc != null ? engine_cc : ""; }
    public void setEngineCC(String engine_cc) { this.engine_cc = engine_cc; }

    public String getMileage() { return mileage != null ? mileage : ""; }
    public void setMileage(String mileage) { this.mileage = mileage; }

    public String getTopSpeed() { return top_speed != null ? top_speed : ""; }
    public void setTopSpeed(String top_speed) { this.top_speed = top_speed; }

    public String getBrakingType() { return braking_type != null ? braking_type : ""; }
    public void setBrakingType(String braking_type) { this.braking_type = braking_type; }

    public String getFeatures() { return features != null ? features : ""; }
    public void setFeatures(String features) { this.features = features; }

    public String getYear() { return year != null ? year : ""; }
    public void setYear(String year) { this.year = year; }

    public String getOdometer() { return odometer != null ? odometer : ""; }
    public void setOdometer(String odometer) { this.odometer = odometer; }

    public String getOwnerDetails() { return owner_details != null ? owner_details : ""; }
    public void setOwnerDetails(String owner_details) { this.owner_details = owner_details; }

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
        on_road_price = in.readString();
        engine_cc = in.readString();
        mileage = in.readString();
        top_speed = in.readString();
        braking_type = in.readString();
        features = in.readString();
        year = in.readString();
        odometer = in.readString();
        owner_details = in.readString();
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
        dest.writeString(on_road_price);
        dest.writeString(engine_cc);
        dest.writeString(mileage);
        dest.writeString(top_speed);
        dest.writeString(braking_type);
        dest.writeString(features);
        dest.writeString(year);
        dest.writeString(odometer);
        dest.writeString(owner_details);
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
            return new BikeModel(in);
        }

        @Override
        public BikeModel[] newArray(int size) {
            return new BikeModel[size];
        }
    };
}