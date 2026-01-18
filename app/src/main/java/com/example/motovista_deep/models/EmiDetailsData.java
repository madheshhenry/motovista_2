package com.example.motovista_deep.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class EmiDetailsData {
    @SerializedName("ledger")
    private EmiLedgerItem ledger;

    @SerializedName("payments")
    private List<EmiPaymentItem> payments;

    @SerializedName("payment_schedule")
    private List<PaymentScheduleItem> paymentSchedule;

    public EmiLedgerItem getLedger() { return ledger; }
    public List<EmiPaymentItem> getPayments() { return payments; }
    public List<PaymentScheduleItem> getPaymentSchedule() { return paymentSchedule; }
}
