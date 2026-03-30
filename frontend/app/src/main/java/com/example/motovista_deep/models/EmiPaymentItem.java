package com.example.motovista_deep.models;

import com.google.gson.annotations.SerializedName;

public class EmiPaymentItem {
    @SerializedName("id")
    private int id;

    @SerializedName("ledger_id")
    private int ledgerId;

    @SerializedName("amount_paid")
    private String amountPaid;

    @SerializedName("payment_date")
    private String paymentDate;

    @SerializedName("payment_mode")
    private String paymentMode;

    @SerializedName("remarks")
    private String remarks;

    @SerializedName("created_at")
    private String createdAt;

    // Getters
    public int getId() { return id; }
    public String getAmountPaid() { return amountPaid; }
    public String getPaymentDate() { return paymentDate; }
    public String getPaymentMode() { return paymentMode; }
    public String getRemarks() { return remarks; }
}
