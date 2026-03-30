package com.example.motovista_deep.models;

public class SecondHandBikeRequest {
    private int bike_id; // For update operations
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

    // Constructor for adding new bike
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

    // Constructor for update with bike_id
    public SecondHandBikeRequest(int bike_id, String brand, String model, String year,
                                 String odometer, String price, String condition,
                                 String ownership, String engine_cc, String braking_type,
                                 String owner_details, String condition_details,
                                 String features, String image_paths) {
        this.bike_id = bike_id;
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

    // Getters and Setters
    public int getBike_id() { return bike_id; }
    public void setBike_id(int bike_id) { this.bike_id = bike_id; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }

    public String getOdometer() { return odometer; }
    public void setOdometer(String odometer) { this.odometer = odometer; }

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }

    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }

    public String getOwnership() { return ownership; }
    public void setOwnership(String ownership) { this.ownership = ownership; }

    public String getEngine_cc() { return engine_cc; }
    public void setEngine_cc(String engine_cc) { this.engine_cc = engine_cc; }

    public String getBraking_type() { return braking_type; }
    public void setBraking_type(String braking_type) { this.braking_type = braking_type; }

    public String getOwner_details() { return owner_details; }
    public void setOwner_details(String owner_details) { this.owner_details = owner_details; }

    public String getCondition_details() { return condition_details; }
    public void setCondition_details(String condition_details) { this.condition_details = condition_details; }

    public String getFeatures() { return features; }
    public void setFeatures(String features) { this.features = features; }

    public String getImage_paths() { return image_paths; }
    public void setImage_paths(String image_paths) { this.image_paths = image_paths; }

}