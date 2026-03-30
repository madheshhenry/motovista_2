package com.example.motovista_deep.models;

import com.google.gson.annotations.SerializedName;

public class PaymentScheduleItem {
    @SerializedName("installment_no")
    private int installmentNo;

    @SerializedName("due_date")
    private String dueDate;

    @SerializedName("amount")
    private String amount;

    @SerializedName("status")
    private String status;

    @SerializedName("payment_date")
    private String paymentDate;

    @SerializedName("amount_paid")
    private String amountPaid;

    @SerializedName("fine")
    private int fine;

    @SerializedName("payment_id")
    private Integer paymentId;

    public int getInstallmentNo() { return installmentNo; }
    public String getDueDate() { return dueDate; }
    public String getAmount() { return amount; }
    public String getStatus() { return status; }
    public String getPaymentDate() { return paymentDate; }
    public String getAmountPaid() { return amountPaid; }
    public int getFine() { return fine; }
    public Integer getPaymentId() { return paymentId; }
}
