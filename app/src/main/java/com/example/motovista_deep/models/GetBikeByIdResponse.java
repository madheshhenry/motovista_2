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

        // Getters
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
    }
}