package com.example.motovista_deep.models;

public class UpdateBikeRequest {
    @com.google.gson.annotations.SerializedName("id")
    private int bike_id;
    private String brand;
    private String model;
    private String variant;
    private String year;
    private String engine_cc;
    private String fuel_type;
    private String transmission;
    private String braking_type;
    @com.google.gson.annotations.SerializedName("on_road_price")
    private String on_road_price;
    @com.google.gson.annotations.SerializedName("ex_showroom_price")
    private String ex_showroom_price;
    @com.google.gson.annotations.SerializedName("insurance_price")
    private String insurance;
    @com.google.gson.annotations.SerializedName("registration_price")
    private String registration_charge;
    @com.google.gson.annotations.SerializedName("ltrt_price")
    private String ltrt;
    private String mileage;
    private String fuel_tank_capacity;
    private String kerb_weight;
    private String seat_height;
    private String ground_clearance;
    private String warranty_period;
    private String free_services_count;
    private String registration_proof;
    private String price_disclaimer;
    private String type;
    private String features;
    private String image_paths;
    private String date;
    private String engine_number;
    private String chassis_number;

    private java.util.List<String> colors;
    private java.util.List<CustomFitting> custom_fittings;
    private java.util.List<CustomFitting> mandatory_fittings;
    private java.util.List<CustomFitting> additional_fittings;

    // Constructor with all fields
    public UpdateBikeRequest(int bike_id,
                             String brand, String model, String variant, String year,
                             String engine_cc, String fuel_type, String transmission,
                             String braking_type, String on_road_price, String ex_showroom_price, String insurance,
                             String registration_charge, String ltrt, String mileage,
                             String fuel_tank_capacity, String kerb_weight, String seat_height,
                             String ground_clearance, String warranty_period,
                             String free_services_count, String registration_proof,
                             String price_disclaimer, String type, String features,
                             String image_paths, String date, String engine_number, String chassis_number,
                             java.util.List<String> colors, java.util.List<CustomFitting> custom_fittings,
                             java.util.List<CustomFitting> mandatory_fittings, java.util.List<CustomFitting> additional_fittings) {
        this.bike_id = bike_id;
        this.brand = brand;
        this.model = model;
        this.variant = variant;
        this.year = year;
        this.engine_cc = engine_cc;
        this.fuel_type = fuel_type;
        this.transmission = transmission;
        this.braking_type = braking_type;
        this.on_road_price = on_road_price;
        this.ex_showroom_price = ex_showroom_price;
        this.insurance = insurance;
        this.registration_charge = registration_charge;
        this.ltrt = ltrt;
        this.mileage = mileage;
        this.fuel_tank_capacity = fuel_tank_capacity;
        this.kerb_weight = kerb_weight;
        this.seat_height = seat_height;
        this.ground_clearance = ground_clearance;
        this.warranty_period = warranty_period;
        this.free_services_count = free_services_count;
        this.registration_proof = registration_proof;
        this.price_disclaimer = price_disclaimer;
        this.type = type;
        this.features = features;
        this.image_paths = image_paths;
        this.date = date;
        this.engine_number = engine_number;
        this.chassis_number = chassis_number;
        this.colors = colors;
        this.custom_fittings = custom_fittings;
        this.mandatory_fittings = mandatory_fittings;
        this.additional_fittings = additional_fittings;
    }

    // Getters and setters for all fields
    public int getBike_id() { return bike_id; }
    public void setBike_id(int bike_id) { this.bike_id = bike_id; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getVariant() { return variant; }
    public void setVariant(String variant) { this.variant = variant; }

    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }

    public String getEngine_cc() { return engine_cc; }
    public void setEngine_cc(String engine_cc) { this.engine_cc = engine_cc; }

    public String getFuel_type() { return fuel_type; }
    public void setFuel_type(String fuel_type) { this.fuel_type = fuel_type; }

    public String getTransmission() { return transmission; }
    public void setTransmission(String transmission) { this.transmission = transmission; }

    public String getBraking_type() { return braking_type; }
    public void setBraking_type(String braking_type) { this.braking_type = braking_type; }

    public String getOn_road_price() { return on_road_price; }
    public void setOn_road_price(String on_road_price) { this.on_road_price = on_road_price; }

    public String getInsurance() { return insurance; }
    public void setInsurance(String insurance) { this.insurance = insurance; }

    public String getRegistration_charge() { return registration_charge; }
    public void setRegistration_charge(String registration_charge) { this.registration_charge = registration_charge; }

    public String getLtrt() { return ltrt; }
    public void setLtrt(String ltrt) { this.ltrt = ltrt; }

    public String getMileage() { return mileage; }
    public void setMileage(String mileage) { this.mileage = mileage; }

    public String getFuel_tank_capacity() { return fuel_tank_capacity; }
    public void setFuel_tank_capacity(String fuel_tank_capacity) { this.fuel_tank_capacity = fuel_tank_capacity; }

    public String getKerb_weight() { return kerb_weight; }
    public void setKerb_weight(String kerb_weight) { this.kerb_weight = kerb_weight; }

    public String getSeat_height() { return seat_height; }
    public void setSeat_height(String seat_height) { this.seat_height = seat_height; }

    public String getGround_clearance() { return ground_clearance; }
    public void setGround_clearance(String ground_clearance) { this.ground_clearance = ground_clearance; }

    public String getWarranty_period() { return warranty_period; }
    public void setWarranty_period(String warranty_period) { this.warranty_period = warranty_period; }

    public String getFree_services_count() { return free_services_count; }
    public void setFree_services_count(String free_services_count) { this.free_services_count = free_services_count; }

    public String getRegistration_proof() { return registration_proof; }
    public void setRegistration_proof(String registration_proof) { this.registration_proof = registration_proof; }

    public String getPrice_disclaimer() { return price_disclaimer; }
    public void setPrice_disclaimer(String price_disclaimer) { this.price_disclaimer = price_disclaimer; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getFeatures() { return features; }
    public void setFeatures(String features) { this.features = features; }

    public String getImage_paths() { return image_paths; }
    public void setImage_paths(String image_paths) { this.image_paths = image_paths; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getEngine_number() { return engine_number; }
    public void setEngine_number(String engine_number) { this.engine_number = engine_number; }

    public String getChassis_number() { return chassis_number; }
    public void setChassis_number(String chassis_number) { this.chassis_number = chassis_number; }
}