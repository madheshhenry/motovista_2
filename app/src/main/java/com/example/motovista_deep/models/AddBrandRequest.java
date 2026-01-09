package com.example.motovista_deep.models;

public class AddBrandRequest {
    private String brand_name;
    private String brand_logo;

    public AddBrandRequest(String brand_name, String brand_logo) {
        this.brand_name = brand_name;
        this.brand_logo = brand_logo;
    }

    public String getBrandName() { return brand_name; }
    public String getBrandLogo() { return brand_logo; }
}
