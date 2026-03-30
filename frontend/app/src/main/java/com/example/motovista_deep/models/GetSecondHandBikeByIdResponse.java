package com.example.motovista_deep.models;

public class GetSecondHandBikeByIdResponse {
    private String status;
    private String message;
    private SecondHandBikeData data;

    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public SecondHandBikeData getData() { return data; }

    public static class SecondHandBikeData {
        private int id;
        private String brand;
        private String model;
        private String year;
        private String odometer;
        private String price;
        private String condition;
        private String ownership;
        private String engine_cc;
        private String braking_type;
        private String owner_details;
        private String condition_details;
        private String features;
        private String image_paths;

        // Getters
        public int getId() { return id; }
        public String getBrand() { return brand; }
        public String getModel() { return model; }
        public String getYear() { return year; }
        public String getOdometer() { return odometer; }
        public String getPrice() { return price; }
        public String getCondition() { return condition; }
        public String getOwnership() { return ownership; }
        public String getEngine_cc() { return engine_cc; }
        public String getBraking_type() { return braking_type; }
        public String getOwner_details() { return owner_details; }
        public String getCondition_details() { return condition_details; }
        public String getFeatures() { return features; }
        public String getImage_paths() { return image_paths; }
    }
}