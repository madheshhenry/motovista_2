package com.example.motovista_deep.models;

import com.google.gson.annotations.SerializedName;

public class CreateEmiOrderRequest {
    @SerializedName("request_id")
    private int requestId;

    @SerializedName("customer_name")
    private String customerName;

    @SerializedName("vehicle_name")
    private String vehicleName;

    @SerializedName("total_amount")
    private double totalAmount;

    @SerializedName("paid_amount")
    private double paidAmount;

    @SerializedName("emi_monthly_amount")
    private double emiMonthlyAmount;

    @SerializedName("duration_months")
    private int durationMonths;

    @SerializedName("interest_rate")
    private double interestRate;

    public CreateEmiOrderRequest(int requestId, String customerName, String vehicleName, 
                                 double totalAmount, double paidAmount, double emiMonthlyAmount, 
                                 int durationMonths, double interestRate) {
        this.requestId = requestId;
        this.customerName = customerName;
        this.vehicleName = vehicleName;
        this.totalAmount = totalAmount;
        this.paidAmount = paidAmount;
        this.emiMonthlyAmount = emiMonthlyAmount;
        this.durationMonths = durationMonths;
        this.interestRate = interestRate;
    }
}
