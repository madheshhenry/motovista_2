package com.example.motovista_deep.models;

import com.google.gson.annotations.SerializedName;

public class UpdateRegistrationStepRequest {
    @SerializedName("ledger_id")
    private int ledgerId;
    
    @SerializedName("step_number")
    private int stepNumber;

    public UpdateRegistrationStepRequest(int ledgerId, int stepNumber) {
        this.ledgerId = ledgerId;
        this.stepNumber = stepNumber;
    }
}
