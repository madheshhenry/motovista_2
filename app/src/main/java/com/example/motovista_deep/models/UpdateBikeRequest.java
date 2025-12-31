package com.example.motovista_deep.models;

public class UpdateBikeRequest extends AddBikeRequest {
    private int bike_id;

    // Constructor for new bike fields (matching AddBikeRequest)
    public UpdateBikeRequest(int bike_id, String brand, String model, String on_road_price,
                             String engine_cc, String mileage, String top_speed,
                             String braking_type, String type, String features,
                             String image_path) {
        super(brand, model, on_road_price, engine_cc, mileage, top_speed,
                braking_type, type, features, image_path);
        this.bike_id = bike_id;
    }

    // Constructor for updated bike fields with all form fields
    public UpdateBikeRequest(int bike_id, String brand, String model, String variant, String year,
                             String engine_cc, String fuel_type, String transmission,
                             String braking_type, String on_road_price, String insurance,
                             String registration_charge, String ltrt, String mileage,
                             String fuel_tank_capacity, String kerb_weight, String seat_height,
                             String ground_clearance, String warranty_period,
                             String free_services_count, String registration_proof,
                             String price_disclaimer, String type, String features,
                             String image_path) {
        // Call the parent constructor
        super(brand, model, variant, year, engine_cc, fuel_type, transmission,
                braking_type, on_road_price, insurance, registration_charge, ltrt,
                mileage, fuel_tank_capacity, kerb_weight, seat_height, ground_clearance,
                warranty_period, free_services_count, registration_proof, price_disclaimer,
                type, features, image_path);
        this.bike_id = bike_id;
    }

    public int getBike_id() { return bike_id; }
    public void setBike_id(int bike_id) { this.bike_id = bike_id; }
}