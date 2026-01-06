package com.example.motovista_deep.models;

import java.io.Serializable;
import com.google.gson.annotations.SerializedName;

public class InventoryBike implements Serializable {
    @SerializedName("id")
    private int id;

    @SerializedName("model")
    private String model;

    @SerializedName("variant")
    private String variant;

    @SerializedName("engine_number")
    private String engineNumber;

    @SerializedName("chassis_number")
    private String chassisNumber;

    @SerializedName("thumbnail")
    private String thumbnail;

    @SerializedName("stock_date")
    private String stockDate;

    public int getId() { return id; }
    public String getModel() { return model; }
    public String getVariant() { return variant; }
    public String getEngineNumber() { return engineNumber; }
    public String getChassisNumber() { return chassisNumber; }
    public String getThumbnail() { return thumbnail; }
    public String getStockDate() { return stockDate; }
}
