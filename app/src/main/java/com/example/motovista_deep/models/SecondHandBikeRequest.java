package com.example.motovista_deep.models;

public class SecondHandBikeRequest {
    private String brand;
    private String model;
    private String year;
    private String odometer;
    private String price;
    private String condition;
    private String ownership;
    private String engine_cc;
    private String braking_type;
    private String owner_details;
    private String condition_details;
    private String features;
    private String image_paths;

    // Constructor for SECOND-HAND BIKES (13 parameters)
    public SecondHandBikeRequest(String brand, String model, String year, String odometer,
                                 String price, String condition, String ownership,
                                 String engine_cc, String braking_type, String owner_details,
                                 String condition_details, String features, String image_paths) {
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.odometer = odometer;
        this.price = price;
        this.condition = condition;
        this.ownership = ownership;
        this.engine_cc = engine_cc;
        this.braking_type = braking_type;
        this.owner_details = owner_details;
        this.condition_details = condition_details;
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
    public String getOwnership() { return ownership; }
    public String getEngine_cc() { return engine_cc; }
    public String getBraking_type() { return braking_type; }
    public String getOwner_details() { return owner_details; }
    public String getCondition_details() { return condition_details; }
    public String getFeatures() { return features; }
    public String getImage_paths() { return image_paths; }
}