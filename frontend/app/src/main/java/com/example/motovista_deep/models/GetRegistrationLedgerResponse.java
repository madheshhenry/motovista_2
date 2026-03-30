package com.example.motovista_deep.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class GetRegistrationLedgerResponse {
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("data")
    private List<RegistrationLedgerItem> data;
    
    @SerializedName("message") // Optional, for error handling
    private String message;

    public boolean isSuccess() { return success; }
    public List<RegistrationLedgerItem> getData() { return data; }
    public String getMessage() { return message; }
}
