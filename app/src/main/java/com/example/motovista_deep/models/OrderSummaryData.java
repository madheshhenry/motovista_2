package com.example.motovista_deep.models;

import com.google.gson.annotations.SerializedName;

public class OrderSummaryData {
    @SerializedName("request_id") private int requestId;
    @SerializedName("customer_name") private String customerName;
    @SerializedName("customer_phone") private String customerPhone;
    @SerializedName("customer_profile") private String customerProfile; // New
    @SerializedName("status") private String status;
    @SerializedName("bike_id") private int bikeId;
    @SerializedName("brand") private String brand;
    @SerializedName("bike_name") private String bikeName;
    @SerializedName("bike_variant") private String bikeVariant;
    @SerializedName("engine_cc") private String engineCc;
    @SerializedName("fuel_type") private String fuelType;
    @SerializedName("on_road_price") private String onRoadPrice;
    @SerializedName("mileage") private String mileage;
    @SerializedName("fuel_tank_capacity") private String fuelTankCapacity;
    @SerializedName("kerb_weight") private String kerbWeight;
    @SerializedName("seat_height") private String seatHeight;
    @SerializedName("ground_clearance") private String groundClearance;
    @SerializedName("engine_number") private String engineNumber;
    @SerializedName("chassis_number") private String chassisNumber;
    @SerializedName("image_paths") private String imagePaths; // New
    @SerializedName("bike_color") private String bikeColor;
    @SerializedName("colors") private String colors;

    // Getters
    public int getRequestId() { return requestId; }
    public String getCustomerName() { return customerName; }
    public String getCustomerPhone() { return customerPhone; }
    public String getCustomerProfile() { return customerProfile; } // New
    public String getStatus() { return status; }
    public int getBikeId() { return bikeId; }
    public String getBrand() { return brand; }
    public String getBikeName() { return bikeName; }
    public String getBikeVariant() { return bikeVariant; }
    public String getEngineCc() { return engineCc; }
    public String getFuelType() { return fuelType; }
    public String getOnRoadPrice() { return onRoadPrice; }
    public String getMileage() { return mileage; }
    public String getFuelTankCapacity() { return fuelTankCapacity; }
    public String getKerbWeight() { return kerbWeight; }
    public String getSeatHeight() { return seatHeight; }
    public String getGroundClearance() { return groundClearance; }
    public String getEngineNumber() { return engineNumber; }
    public String getChassisNumber() { return chassisNumber; }
    public String getImagePaths() { return imagePaths; }
    public String getBikeColor() { return bikeColor; }
    public String getColors() { return colors; }
}
