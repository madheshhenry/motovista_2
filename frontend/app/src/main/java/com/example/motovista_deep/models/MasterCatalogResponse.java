package com.example.motovista_deep.models;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class MasterCatalogResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("brands")
    private List<InventoryBrand> brands;

    @SerializedName("catalog")
    private List<BikeModel> catalog;

    public boolean isSuccess() {
        return success || "success".equalsIgnoreCase(status);
    }

    public String getMessage() {
        return message;
    }

    public List<InventoryBrand> getBrands() {
        return brands;
    }

    public List<BikeModel> getCatalog() {
        return catalog;
    }
}
