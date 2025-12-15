package com.example.motovista_deep.models;

public class SecondHandBikeRequest {
    private String brand;
    private String model;
    private String year;
    private String odometer;
    private String price;
    private String condition;
    private String owner_details;
    private String features;
    private String image_paths;

    public SecondHandBikeRequest(String brand, String model, String year, String odometer,
                                 String price, String condition, String owner_details,
                                 String features, String image_paths) {
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.odometer = odometer;
        this.price = price;
        this.condition = condition;
        this.owner_details = owner_details;
        this.features = features;
        this.image_paths = image_paths;
    }

    // Getters
    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public String getYear() { return year; }
    public String getOdometer() { return odometer; }
    public String getPrice() { return price; }
    public String getCondition() { return condition; }
    public String getOwner_details() { return owner_details; }
    public String getFeatures() { return features; }
    public String getImage_paths() { return image_paths; }
}