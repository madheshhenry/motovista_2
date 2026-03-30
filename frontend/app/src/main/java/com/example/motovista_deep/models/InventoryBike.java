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

    @SerializedName("image_url")
    private String thumbnail;

    @SerializedName("stock_date")
    private String stockDate;

    @SerializedName("status")
    private String status;

    @SerializedName("customer_name")
    private String customerName;

    @SerializedName("sold_date")
    private String soldDate;

    @SerializedName("colors")
    private String colors; // Can be JSON string

    @SerializedName("source_table")
    private String sourceTable;

    public int getId() { return id; }
    public String getModel() { return model; }
    public String getVariant() { return variant; }
    public String getEngineNumber() { return engineNumber; }
    public String getChassisNumber() { return chassisNumber; }
    public String getThumbnail() { return thumbnail; }
    public String getStockDate() { return stockDate; }
    public String getStatus() { return status; }
    public String getCustomerName() { return customerName; }
    public String getSoldDate() { return soldDate; }
    public String getColors() { return colors; }
    public String getSourceTable() { return sourceTable; }
}
