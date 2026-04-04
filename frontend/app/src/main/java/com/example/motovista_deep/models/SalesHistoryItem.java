package com.example.motovista_deep.models;

import com.google.gson.annotations.SerializedName;

public class SalesHistoryItem {
    @SerializedName("bike_id")
    private int bikeId;
    @SerializedName("brand")
    private String brand;
    @SerializedName("model")
    private String model;
    @SerializedName("variant")
    private String variant;
    @SerializedName("bike_color")
    private String bikeColor;
    @SerializedName("engine_number")
    private String engineNumber;
    @SerializedName("chassis_number")
    private String chassisNumber;
    @SerializedName("order_id")
    private int orderId;
    @SerializedName("customer_name")
    private String customerName;
    @SerializedName("sale_date")
    private String saleDate;
    @SerializedName("customer_id")
    private int customerId;
    @SerializedName("total_value")
    private String totalValue;
    @SerializedName("customer_phone")
    private String customerPhone;
    @SerializedName("customer_address")
    private String customerAddress;
    @SerializedName("payment_type")
    private String paymentType;
    @SerializedName("formatted_date")
    private String formattedDate;
    @SerializedName("bike_color_name")
    private String bikeColorName;
    @SerializedName("bike_image")
    private String bikeImage;
    @SerializedName("bike_color_hex")
    private String bikeColorHex;

    public int getBikeId() { return bikeId; }
    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public String getVariant() { return variant; }
    public String getBikeColor() { return bikeColor; }
    public String getEngineNumber() { return engineNumber; }
    public String getChassisNumber() { return chassisNumber; }
    public int getOrderId() { return orderId; }
    public String getCustomerName() { return customerName; }
    public String getSaleDate() { return saleDate; }
    public int getCustomerId() { return customerId; }
    public String getTotalValue() { return totalValue; }
    public String getCustomerPhone() { return customerPhone; }
    public String getCustomerAddress() { return customerAddress; }
    public String getPaymentType() { return paymentType; }
    public String getFormattedDate() { return formattedDate; }
    public String getBikeColorName() { return bikeColorName; }
    public String getBikeImage() { return bikeImage; }
    public String getBikeColorHex() { return bikeColorHex; }
}
