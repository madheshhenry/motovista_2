package com.example.motovista_deep.models;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.SerializedName;

public class InventoryBrand implements Serializable {
    @SerializedName("brand")
    private String brand;

    @SerializedName("count")
    private int count;

    @SerializedName("bikes")
    private List<InventoryBike> bikes;

    public String getBrand() { return brand; }
    public int getCount() { return count; }
    public List<InventoryBike> getBikes() { return bikes; }
}
