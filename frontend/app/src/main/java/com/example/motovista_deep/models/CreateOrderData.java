package com.example.motovista_deep.models;

import com.google.gson.annotations.SerializedName;

public class CreateOrderData {
    @SerializedName("ledger_id")
    private int ledgerId;

    @SerializedName("order_reference")
    private String orderReference;

    public int getLedgerId() { return ledgerId; }
    public String getOrderReference() { return orderReference; }
}
