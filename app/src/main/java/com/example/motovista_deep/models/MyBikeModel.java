package com.example.motovista_deep.models;

import com.google.gson.annotations.SerializedName;

public class MyBikeModel {
    @SerializedName("request_id")
    private int requestId;

    @SerializedName("bike_name")
    private String bikeName;

    @SerializedName("bike_variant")
    private String bikeVariant;

    @SerializedName("bike_color_name")
    private String bikeColorName;

    @SerializedName("bike_color_hex")
    private String bikeColorHex;

    @SerializedName("bike_price")
    private double bikePrice;

    @SerializedName("purchase_date_formatted")
    private String purchaseDate;

    @SerializedName("engine_number")
    private String engineNumber;

    @SerializedName("chassis_number")
    private String chassisNumber;

    @SerializedName("bike_image")
    private String bikeImage;

    @SerializedName("policy_number")
    private String policyNumber;

    @SerializedName("full_insurance_expiry")
    private String insuranceExpiry;

    @SerializedName("insurance_status")
    private String insuranceStatus;

    @SerializedName("emi_total_amount")
    private double emiTotalAmount;

    @SerializedName("emi_paid_amount")
    private double emiPaidAmount;

    @SerializedName("emi_monthly_amount")
    private double emiMonthlyAmount;

    @SerializedName("emi_duration_months")
    private int emiDurationMonths;

    @SerializedName("emi_status")
    private String emiStatus;

    @SerializedName("emi_remaining_amount")
    private double emiRemainingAmount;

    @SerializedName("emi_ledger_id")
    private int emiLedgerId;

    // Getters
    public int getRequestId() { return requestId; }
    public String getBikeName() { return bikeName; }
    public String getBikeVariant() { return bikeVariant; }
    public String getBikeColorName() { return bikeColorName; }
    public String getBikeColorHex() { return bikeColorHex; }
    public double getBikePrice() { return bikePrice; }
    public String getPurchaseDate() { return purchaseDate; }
    public String getEngineNumber() { return engineNumber; }
    public String getChassisNumber() { return chassisNumber; }
    public String getBikeImage() { return bikeImage; }
    public String getPolicyNumber() { return policyNumber; }
    public String getInsuranceExpiry() { return insuranceExpiry; }
    public String getInsuranceStatus() { return insuranceStatus; }
    public double getEmiTotalAmount() { return emiTotalAmount; }
    public double getEmiPaidAmount() { return emiPaidAmount; }
    public double getEmiMonthlyAmount() { return emiMonthlyAmount; }
    public int getEmiDurationMonths() { return emiDurationMonths; }
    public String getEmiStatus() { return emiStatus; }
    public double getEmiRemainingAmount() { return emiRemainingAmount; }
    public int getEmiLedgerId() { return emiLedgerId; }
}
