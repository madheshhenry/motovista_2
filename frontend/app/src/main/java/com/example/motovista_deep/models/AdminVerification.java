package com.example.motovista_deep.models;

import com.google.gson.annotations.SerializedName;

public class AdminVerification {
    @SerializedName("id")
    private int id; // Payment record ID

    @SerializedName("ledger_id")
    private int ledgerId;

    @SerializedName("customer_name")
    private String customerName;

    @SerializedName("bike_name")
    private String bikeName;

    @SerializedName("amount_paid")
    private String amountPaid;

    @SerializedName("created_at")
    private String createdAt;
 
    @SerializedName("bike_image")
    private String bikeImage;
 
    public int getId() { return id; }
    public int getLedgerId() { return ledgerId; }
    public String getCustomerName() { return customerName; }
    public String getBikeName() { return bikeName; }
    public String getAmountPaid() { return amountPaid; }
    public String getCreatedAt() { return createdAt; }
    public String getBikeImage() { return bikeImage; }
}
