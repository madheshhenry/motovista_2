package com.example.motovista_deep.models;

import com.google.gson.annotations.SerializedName;

public class NotifyPaymentRequest {
    @SerializedName("ledger_id")
    private int ledgerId;

    @SerializedName("amount")
    private double amount;

    public NotifyPaymentRequest(int ledgerId, double amount) {
        this.ledgerId = ledgerId;
        this.amount = amount;
    }

    public int getLedgerId() { return ledgerId; }
    public double getAmount() { return amount; }
}
