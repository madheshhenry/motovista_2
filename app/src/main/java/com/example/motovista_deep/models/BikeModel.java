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

    // 🔴 backend raw string
    private String image_url;

    // 🔵 processed list
    private ArrayList<String> imageUrls = new ArrayList<>();

    private String type;
    private int is_featured;

    private String on_road_price;
    private String engine_cc;
    private String mileage;
    private String top_speed;
    private String braking_type;
    private String features;
    private String year;
    private String odometer;
    private String owner_details;

    public BikeModel() {}

    /* ---------------- IMAGE HELPERS ---------------- */

    public void setImageUrlRaw(String image_url) {
        this.image_url = image_url;
    }

    public String getImageUrlRaw() {
        return image_url;
    }

    // 🔥 MAIN CONVERTER (STRING → LIST)
    public void setImageUrlsFromString(String imagePath, String baseUrl) {
        imageUrls.clear();

        if (imagePath == null || imagePath.trim().isEmpty()) return;

        String cleaned = imagePath.replace("[", "").replace("]", "");
        List<String> paths = Arrays.asList(cleaned.split(","));

        for (String p : paths) {
            p = p.trim();
            if (!p.startsWith("http")) {
                p = baseUrl + p;
            }
            imageUrls.add(p);
        }
    }

    public ArrayList<String> getImageUrls() {
        return imageUrls;
    }

    // 🔹 inventory image (first image)
    public String getImageUrl() {
        return imageUrls != null && !imageUrls.isEmpty() ? imageUrls.get(0) : "";
    }

    /* ---------------- GETTERS / SETTERS ---------------- */

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

    /* ---------------- PARCELABLE ---------------- */

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
    }

    @Override
    public int describeContents() { return 0; }

    public static final Creator<BikeModel> CREATOR = new Creator<BikeModel>() {
        @Override public BikeModel createFromParcel(Parcel in) { return new BikeModel(in); }
        @Override public BikeModel[] newArray(int size) { return new BikeModel[size]; }
    };
}
