package com.example.motovista_deep.models;

public class AddBikeRequest {
    private String brand;
    private String model;
    private String on_road_price;
    private String engine_cc;
    private String mileage;
    private String top_speed;
    private String braking_type;
    private String type;
    private String features;
    private String image_path;

    // Constructor for NEW BIKES (10 parameters)
    public AddBikeRequest(String brand, String model, String on_road_price,
                          String engine_cc, String mileage, String top_speed,
                          String braking_type, String type, String features,
                          String image_path) {
        this.brand = brand;
        this.model = model;
        this.on_road_price = on_road_price;
        this.engine_cc = engine_cc;
        this.mileage = mileage;
        this.top_speed = top_speed;
        this.braking_type = braking_type;
        this.type = type;
        this.features = features;
        this.image_path = image_path;
    }

    // Getters
    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public String getOn_road_price() { return on_road_price; }
    public String getEngine_cc() { return engine_cc; }
    public String getMileage() { return mileage; }
    public String getTop_speed() { return top_speed; }
    public String getBraking_type() { return braking_type; }
    public String getType() { return type; }
    public String getFeatures() { return features; }
    public String getImage_path() { return image_path; }
}