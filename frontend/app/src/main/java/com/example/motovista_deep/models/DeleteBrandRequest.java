package com.example.motovista_deep.models;

import com.google.gson.annotations.SerializedName;

public class DeleteBrandRequest {
    @SerializedName("brand_id")
    private int brandId;

    public DeleteBrandRequest(int brandId) {
        this.brandId = brandId;
    }

    public int getBrandId() {
        return brandId;
    }
}
