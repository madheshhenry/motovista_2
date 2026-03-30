package com.example.motovista_deep.models;

public class AddBikeRequest {
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
    private String type = "NEW";
    private String features;
    private String image_paths;
    private String date;
    private String engine_number;
    private String chassis_number;
    private String max_torque;

    // Constructor for updated bike fields
    public AddBikeRequest(String brand, String model, String on_road_price,
                          String engine_cc, String mileage, String top_speed,
                          String braking_type, String type, String features,
                          String image_paths) {
        this.brand = brand;
        this.model = model;
        this.on_road_price = on_road_price;
        this.engine_cc = engine_cc;
        this.mileage = mileage;
        this.braking_type = braking_type;
        this.type = type;
        this.features = features;
        this.image_paths = image_paths;
    }

    // New constructor with all fields including new ones
    public AddBikeRequest(String brand, String model, String variant, String year, String engine_cc,
                          String fuel_type, String transmission, String braking_type,
                          String on_road_price, String ex_showroom_price, String insurance_price,
                          String registration_price, String ltrt_price,
                          String mileage, String fuel_tank_capacity, String kerb_weight,
                          String seat_height, String ground_clearance, String warranty,
                          String free_services, String registration_proof, String price_disclaimer,
                          String date, String max_torque,
                          java.util.List<String> colors, java.util.List<com.example.motovista_deep.models.CustomFitting> custom_fittings,
                          java.util.Map<String, Double> mandatory_fittings, java.util.Map<String, Double> additional_fittings,
                          java.util.Map<String, java.util.List<String>> color_images,
                          String front_brake, String rear_brake, String abs_type, String wheel_type) {
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
        this.insurance = insurance_price;
        this.registration_charge = registration_price;
        this.ltrt = ltrt_price;
        this.mileage = mileage;
        this.fuel_tank_capacity = fuel_tank_capacity;
        this.kerb_weight = kerb_weight;
        this.seat_height = seat_height;
        this.ground_clearance = ground_clearance;
        this.warranty_period = warranty;
        this.free_services_count = free_services;
        this.registration_proof = registration_proof;
        this.price_disclaimer = price_disclaimer;
        this.date = date;
        this.max_torque = max_torque;
        this.colors = colors;
        this.custom_fittings = custom_fittings;
        
        this.mandatory_fittings = new java.util.ArrayList<>();
        if (mandatory_fittings != null) {
            for (java.util.Map.Entry<String, Double> entry : mandatory_fittings.entrySet()) {
                this.mandatory_fittings.add(new CustomFitting(entry.getKey(), String.valueOf(entry.getValue())));
            }
        }

        this.additional_fittings = new java.util.ArrayList<>();
        if (additional_fittings != null) {
            for (java.util.Map.Entry<String, Double> entry : additional_fittings.entrySet()) {
                this.additional_fittings.add(new CustomFitting(entry.getKey(), String.valueOf(entry.getValue())));
            }
        }

        this.color_images = color_images;
        
        // Populate image_paths from color_images for backward compatibility / thumbnail generation
        if (color_images != null && !color_images.isEmpty()) {
            java.util.List<String> allPaths = new java.util.ArrayList<>();
            for (java.util.List<String> paths : color_images.values()) {
                if (paths != null) allPaths.addAll(paths);
            }
            if (!allPaths.isEmpty()) {
                // Join with commas
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < allPaths.size(); i++) {
                    sb.append(allPaths.get(i));
                    if (i < allPaths.size() - 1) sb.append(",");
                }
                this.image_paths = sb.toString();
            }
        }

        this.front_brake = front_brake;
        this.rear_brake = rear_brake;
        this.abs_type = abs_type;
        this.wheel_type = wheel_type;
    }

    private java.util.List<String> colors;
    private java.util.Map<String, java.util.List<String>> color_images;
    private java.util.List<CustomFitting> custom_fittings;
    private java.util.List<CustomFitting> mandatory_fittings;
    private java.util.List<CustomFitting> additional_fittings;
    
    private String front_brake;
    private String rear_brake;
    private String abs_type;
    private String wheel_type;

    public java.util.List<String> getColors() { return colors; }
    public void setColors(java.util.List<String> colors) { this.colors = colors; }

    public String getMaxTorque() { return max_torque; }
    public void setMaxTorque(String max_torque) { this.max_torque = max_torque; }
    
    public String getFrontBrake() { return front_brake; }
    public void setFrontBrake(String front_brake) { this.front_brake = front_brake; }

    public String getRearBrake() { return rear_brake; }
    public void setRearBrake(String rear_brake) { this.rear_brake = rear_brake; }

    public String getAbsType() { return abs_type; }
    public void setAbsType(String abs_type) { this.abs_type = abs_type; }

    public String getWheelType() { return wheel_type; }
    public void setWheelType(String wheel_type) { this.wheel_type = wheel_type; }

    // Restored Getters and Setters
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

    public String getEx_showroom_price() { return ex_showroom_price; }
    public void setEx_showroom_price(String ex_showroom_price) { this.ex_showroom_price = ex_showroom_price; }

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

    public java.util.Map<String, java.util.List<String>> getColor_images() { return color_images; }
    public void setColor_images(java.util.Map<String, java.util.List<String>> color_images) { this.color_images = color_images; }

    public java.util.List<CustomFitting> getCustom_fittings() { return custom_fittings; }
    public void setCustom_fittings(java.util.List<CustomFitting> custom_fittings) { this.custom_fittings = custom_fittings; }

    public java.util.List<CustomFitting> getMandatory_fittings() { return mandatory_fittings; }
    public void setMandatory_fittings(java.util.List<CustomFitting> mandatory_fittings) { this.mandatory_fittings = mandatory_fittings; }

    public java.util.List<CustomFitting> getAdditional_fittings() { return additional_fittings; }
    public void setAdditional_fittings(java.util.List<CustomFitting> additional_fittings) { this.additional_fittings = additional_fittings; }
}