package com.example.motovista_deep.models;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class InventoryResponse {
    @SerializedName("status")
    private boolean status;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private List<InventoryBrand> data;

    public boolean isStatus() { return status; }
    public String getMessage() { return message; }
    public List<InventoryBrand> getData() { return data; }
}
