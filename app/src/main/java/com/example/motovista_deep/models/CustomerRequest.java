package com.example.motovista_deep.models;

import com.google.gson.annotations.SerializedName;

public class CustomerRequest {
    @SerializedName("id")
    private int id; // Request/Order ID

    @SerializedName("customer_id")
    private int customer_id;
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("customer_name")
    private String customer_name;
    
    @SerializedName("customer_phone")
    private String customer_phone;
    
    @SerializedName("customer_profile")
    private String customer_profile;
    
    @SerializedName("bike_id")
    private int bike_id;
    
    @SerializedName("bike_name")
    private String bike_name;
    
    @SerializedName("bike_variant")
    private String bike_variant;
    
    @SerializedName("bike_color")
    private String bike_color;
    
    @SerializedName("bike_price")
    private String bike_price;

    @SerializedName("created_at")
    private String created_at; // Timestamp from DB

    public CustomerRequest(int customer_id, String customer_name, String customer_phone, String customer_profile, int bike_id, String bike_name, String bike_variant, String bike_color, String bike_price) {
        this.customer_id = customer_id;
        this.customer_name = customer_name;
        this.customer_phone = customer_phone;
        this.customer_profile = customer_profile;
        this.bike_id = bike_id;
        this.bike_name = bike_name;
        this.bike_variant = bike_variant;
        this.bike_color = bike_color;
        this.bike_price = bike_price;
    }
    
    // Getters needed for Admin UI
    public int getId() { return id; }
    public String getCustomer_name() { return customer_name; }
    public String getCustomer_phone() { return customer_phone; }
    public String getBike_name() { return bike_name; }
    public String getBike_variant() { return bike_variant; }
    public String getBike_color() { return bike_color; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getBike_price() { return bike_price; }
    public String getCreated_at() { return created_at; }
}
