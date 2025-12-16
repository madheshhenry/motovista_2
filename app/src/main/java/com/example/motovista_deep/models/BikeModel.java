package com.example.motovista_deep.models;

import android.os.Parcel;
import android.os.Parcelable;

public class BikeModel implements Parcelable {
    private int id;
    private String brand;
    private String model;
    private String price;
    private String condition;
    private String image_url;
    private String type;
    private int is_featured; // Changed to int to fix boolean error
    private String on_road_price;
    private String engine_cc;
    private String mileage;
    private String top_speed;
    private String braking_type;
    private String features;
    private String year;
    private String odometer;
    private String owner_details;

    // Constructor
    public BikeModel(int id, String brand, String model, String price, String condition,
                     String image_url, String type, int is_featured) { // Changed to int
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.price = price;
        this.condition = condition;
        this.image_url = image_url;
        this.type = type;
        this.is_featured = is_featured;
    }

    // Empty constructor
    public BikeModel() {
    }

    // Parcelable constructor
    protected BikeModel(Parcel in) {
        id = in.readInt();
        brand = in.readString();
        model = in.readString();
        price = in.readString();
        condition = in.readString();
        image_url = in.readString();
        type = in.readString();
        is_featured = in.readInt(); // Changed to readInt
        on_road_price = in.readString();
        engine_cc = in.readString();
        mileage = in.readString();
        top_speed = in.readString();
        braking_type = in.readString();
        features = in.readString();
        year = in.readString();
        odometer = in.readString();
        owner_details = in.readString();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(brand);
        dest.writeString(model);
        dest.writeString(price);
        dest.writeString(condition);
        dest.writeString(image_url);
        dest.writeString(type);
        dest.writeInt(is_featured); // Changed to writeInt
        dest.writeString(on_road_price);
        dest.writeString(engine_cc);
        dest.writeString(mileage);
        dest.writeString(top_speed);
        dest.writeString(braking_type);
        dest.writeString(features);
        dest.writeString(year);
        dest.writeString(odometer);
        dest.writeString(owner_details);
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
    public String getImageUrl() { return image_url; }
    public void setImageUrl(String image_url) { this.image_url = image_url; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    // Changed to int to fix boolean error
    public int getIsFeatured() { return is_featured; }
    public void setIsFeatured(int is_featured) { this.is_featured = is_featured; }

    // Helper method for boolean check
    public boolean isFeatured() { return is_featured == 1; }

    public String getOnRoadPrice() { return on_road_price; }
    public void setOnRoadPrice(String on_road_price) { this.on_road_price = on_road_price; }
    public String getEngineCC() { return engine_cc; }
    public void setEngineCC(String engine_cc) { this.engine_cc = engine_cc; }
    public String getMileage() { return mileage; }
    public void setMileage(String mileage) { this.mileage = mileage; }
    public String getTopSpeed() { return top_speed; }
    public void setTopSpeed(String top_speed) { this.top_speed = top_speed; }
    public String getBrakingType() { return braking_type; }
    public void setBrakingType(String braking_type) { this.braking_type = braking_type; }
    public String getFeatures() { return features; }
    public void setFeatures(String features) { this.features = features; }
    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }
    public String getOdometer() { return odometer; }
    public void setOdometer(String odometer) { this.odometer = odometer; }
    public String getOwnerDetails() { return owner_details; }
    public void setOwnerDetails(String owner_details) { this.owner_details = owner_details; }
}