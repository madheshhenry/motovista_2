package com.example.motovista_deep.models;

public class Bike {
    private String id;
    private String brand;
    private String model;
    private String exShowroomPrice;
    private String rtoCharges;
    private String insurance;
    private String engineCC;
    private String mileage;
    private String topSpeed;
    private String brakingType;
    private String type;
    private String features;
    private String imageUri;
    private String createdAt;

    // Default constructor
    public Bike() {
        // Empty constructor for Firebase or other serialization
    }

    // Parameterized constructor
    public Bike(String brand, String model, String exShowroomPrice, String rtoCharges,
                String insurance, String engineCC, String mileage, String topSpeed,
                String brakingType, String type, String features, String imageUri) {
        this.brand = brand;
        this.model = model;
        this.exShowroomPrice = exShowroomPrice;
        this.rtoCharges = rtoCharges;
        this.insurance = insurance;
        this.engineCC = engineCC;
        this.mileage = mileage;
        this.topSpeed = topSpeed;
        this.brakingType = brakingType;
        this.type = type;
        this.features = features;
        this.imageUri = imageUri;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getExShowroomPrice() {
        return exShowroomPrice;
    }

    public void setExShowroomPrice(String exShowroomPrice) {
        this.exShowroomPrice = exShowroomPrice;
    }

    public String getRtoCharges() {
        return rtoCharges;
    }

    public void setRtoCharges(String rtoCharges) {
        this.rtoCharges = rtoCharges;
    }

    public String getInsurance() {
        return insurance;
    }

    public void setInsurance(String insurance) {
        this.insurance = insurance;
    }

    public String getEngineCC() {
        return engineCC;
    }

    public void setEngineCC(String engineCC) {
        this.engineCC = engineCC;
    }

    public String getMileage() {
        return mileage;
    }

    public void setMileage(String mileage) {
        this.mileage = mileage;
    }

    public String getTopSpeed() {
        return topSpeed;
    }

    public void setTopSpeed(String topSpeed) {
        this.topSpeed = topSpeed;
    }

    public String getBrakingType() {
        return brakingType;
    }

    public void setBrakingType(String brakingType) {
        this.brakingType = brakingType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFeatures() {
        return features;
    }

    public void setFeatures(String features) {
        this.features = features;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Bike{" +
                "brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", type='" + type + '\'' +
                ", engineCC='" + engineCC + '\'' +
                '}';
    }
}