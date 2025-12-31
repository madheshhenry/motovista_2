package com.example.motovista_deep.models;

public class GetBikeByIdResponse {
    private String status;
    private String message;
    private BikeData data;

    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public BikeData getData() { return data; }

    public static class BikeData {
        private int id;
        private String brand;
        private String model;
        private String on_road_price;
        private String engine_cc;
        private String mileage;
        private String top_speed;
        private String braking_type;
        private String type;
        private String features;
        private String image_path;

        // Add new fields for bike details
        private String date;
        private String engine_number;
        private String chassis_number;
        private String variant;
        private String year;
        private String fuel_type;
        private String transmission;
        private String insurance;
        private String registration_charge;
        private String ltrt;
        private String fuel_tank_capacity;
        private String kerb_weight;
        private String seat_height;
        private String ground_clearance;
        private String warranty_period;
        private String free_services_count;
        private String registration_proof;
        private String price_disclaimer;

        // Getters for all fields
        public int getId() { return id; }
        public String getBrand() { return brand; }
        public String getModel() { return model; }
        public String getOn_road_price() { return on_road_price; }
        public String getEngine_cc() { return engine_cc; }
        public String getMileage() { return mileage; }
        public String getTop_speed() { return top_speed; }
        public String getBraking_type() { return braking_type; }
        public String getType() { return type; }
        public String getFeatures() { return features; }
        public String getImage_path() { return image_path; }

        // New getters
        public String getDate() { return date != null ? date : ""; }
        public String getEngine_number() { return engine_number != null ? engine_number : ""; }
        public String getChassis_number() { return chassis_number != null ? chassis_number : ""; }
        public String getVariant() { return variant != null ? variant : ""; }
        public String getYear() { return year != null ? year : ""; }
        public String getFuel_type() { return fuel_type != null ? fuel_type : ""; }
        public String getTransmission() { return transmission != null ? transmission : ""; }
        public String getInsurance() { return insurance != null ? insurance : ""; }
        public String getRegistration_charge() { return registration_charge != null ? registration_charge : ""; }
        public String getLtrt() { return ltrt != null ? ltrt : ""; }
        public String getFuel_tank_capacity() { return fuel_tank_capacity != null ? fuel_tank_capacity : ""; }
        public String getKerb_weight() { return kerb_weight != null ? kerb_weight : ""; }
        public String getSeat_height() { return seat_height != null ? seat_height : ""; }
        public String getGround_clearance() { return ground_clearance != null ? ground_clearance : ""; }
        public String getWarranty_period() { return warranty_period != null ? warranty_period : ""; }
        public String getFree_services_count() { return free_services_count != null ? free_services_count : ""; }
        public String getRegistration_proof() { return registration_proof != null ? registration_proof : ""; }
        public String getPrice_disclaimer() { return price_disclaimer != null ? price_disclaimer : ""; }
    }
}