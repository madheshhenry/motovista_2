package com.example.motovista_deep.models;

import java.io.Serializable;

public class BikeModel implements Serializable {  // Add Serializable
    private int id;
    private String brand;
    private String model;
    private String price;
    private String condition;
    private String imageUrl;
    private String type; // "NEW" or "SECOND_HAND"
    private boolean isAvailable;

    // Add serialVersionUID for Serializable
    private static final long serialVersionUID = 1L;

    // Constructor
    public BikeModel(int id, String brand, String model, String price,
                     String condition, String imageUrl, String type, boolean isAvailable) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.price = price;
        this.condition = condition;
        this.imageUrl = imageUrl;
        this.type = type;
        this.isAvailable = isAvailable;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }

    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }
}