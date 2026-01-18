package com.example.motovista_deep.models;

import com.google.gson.annotations.SerializedName;

public class PayEmiRequest {
    @SerializedName("ledger_id")
    private int ledgerId;

    @SerializedName("amount")
    private double amount;

    @SerializedName("payment_date")
    private String paymentDate;

    @SerializedName("payment_mode")
    private String paymentMode;

    @SerializedName("fine_paid")
    private double finePaid;

    @SerializedName("remarks")
    private String remarks;

    @SerializedName("payment_id")
    private Integer paymentId;

    public PayEmiRequest(int ledgerId, double amount, double finePaid, String paymentDate, String paymentMode, String remarks, Integer paymentId) {
        this.ledgerId = ledgerId;
        this.amount = amount;
        this.finePaid = finePaid;
        this.paymentDate = paymentDate;
        this.paymentMode = paymentMode;
        this.remarks = remarks;
        this.paymentId = paymentId;
    }
}
