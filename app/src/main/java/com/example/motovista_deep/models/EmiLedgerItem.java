package com.example.motovista_deep.models;

import com.google.gson.annotations.SerializedName;

public class EmiLedgerItem {
    @SerializedName("id")
    private int id;

    @SerializedName("request_id")
    private int requestId;

    @SerializedName("customer_name")
    private String customerName;

    @SerializedName("vehicle_name")
    private String vehicleName;

    @SerializedName("total_amount")
    private String totalAmount;

    @SerializedName("paid_amount")
    private String paidAmount;

    @SerializedName("remaining_amount")
    private String remainingAmount;

    @SerializedName("emi_monthly_amount")
    private String emiMonthlyAmount;

    @SerializedName("duration_months")
    private int durationMonths;

    @SerializedName("interest_rate")
    private String interestRate;

    @SerializedName("status")
    private String status;

    @SerializedName("next_due_date")
    private String nextDueDate;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("customer_phone")
    private String customerPhone;

    @SerializedName("customer_profile")
    private String customerProfile;

    @SerializedName("bike_images")
    private String bikeImages; // JSON string or comma separated

    @SerializedName("bike_model")
    private String bikeModel;

    @SerializedName("bike_brand")
    private String bikeBrand;

    @SerializedName("bike_color")
    private String bikeColor;

    @SerializedName("bike_variant")
    private String bikeVariant;
    
    @SerializedName("on_road_price")
    private String onRoadPrice;

    @SerializedName("engine_number")
    private String engineNumber;

    @SerializedName("chassis_number")
    private String chassisNumber;

    // Getters
    public int getId() { return id; }
    public String getCustomerName() { return customerName; }
    public String getVehicleName() { return vehicleName; }
    public String getEmiMonthlyAmount() { return emiMonthlyAmount; }
    public String getStatus() { return status; }
    public int getDurationMonths() { return durationMonths; }
    public String getNextDueDate() { return nextDueDate; }
    public String getRemainingAmount() { return remainingAmount; }
    public String getTotalAmount() { return totalAmount; }
    public String getPaidAmount() { return paidAmount; }
    public String getCreatedAt() { return createdAt; }

    public String getCustomerPhone() { return customerPhone; }
    public String getCustomerProfile() { return customerProfile; }
    public String getBikeImages() { return bikeImages; }
    public String getBikeModel() { return bikeModel; }
    public String getBikeBrand() { return bikeBrand; }
    
    public String getBikeColor() { return bikeColor; }
    public String getBikeVariant() { return bikeVariant; }
    public String getOnRoadPrice() { return onRoadPrice; }
    public String getEngineNumber() { return engineNumber; }
    public String getChassisNumber() { return chassisNumber; }
}
