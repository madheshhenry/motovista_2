package com.example.motovista_deep.models;

public class BikeStock {
    private int id;
    private String model;
    private String engineNo;
    private String chassisNo;
    private String stockDate;
    private String customer;
    private String brandName;

    public BikeStock() {}

    public BikeStock(int id, String model, String engineNo, String chassisNo, String stockDate, String customer, String brandName) {
        this.id = id;
        this.model = model;
        this.engineNo = engineNo;
        this.chassisNo = chassisNo;
        this.stockDate = stockDate;
        this.customer = customer;
        this.brandName = brandName;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getEngineNo() { return engineNo; }
    public void setEngineNo(String engineNo) { this.engineNo = engineNo; }

    public String getChassisNo() { return chassisNo; }
    public void setChassisNo(String chassisNo) { this.chassisNo = chassisNo; }

    public String getStockDate() { return stockDate; }
    public void setStockDate(String stockDate) { this.stockDate = stockDate; }

    public String getCustomer() { return customer; }
    public void setCustomer(String customer) { this.customer = customer; }

    public String getBrandName() { return brandName; }
    public void setBrandName(String brandName) { this.brandName = brandName; }
}