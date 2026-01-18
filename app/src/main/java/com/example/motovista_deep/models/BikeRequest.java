package com.example.motovista_deep.models;

import com.google.gson.annotations.SerializedName;

public class BikeRequest {
    @SerializedName("id")
    private int id;

    @SerializedName("brand")
    private String brand;

    @SerializedName("model")
    private String model;

    @SerializedName("features")
    private String features;

    @SerializedName("full_name")
    private String fullName;

    @SerializedName("mobile_number")
    private String mobileNumber;

    @SerializedName("email")
    private String email;

    @SerializedName("status")
    private String status;
    
    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("user_id")
    private Integer userId;

    // Helper for POST request
    public BikeRequest(String brand, String model, String features, String fullName, String mobileNumber, String email, Integer userId) {
        this.brand = brand;
        this.model = model;
        this.features = features;
        this.fullName = fullName;
        this.mobileNumber = mobileNumber;
        this.email = email;
        this.userId = userId;
    }

    // Getters
    public int getId() { return id; }
    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public String getFeatures() { return features; }
    public String getFullName() { return fullName; }
    public String getMobileNumber() { return mobileNumber; }
    public String getEmail() { return email; }
    public String getStatus() { return status; }
    public String getCreatedAt() { return createdAt; }
}
