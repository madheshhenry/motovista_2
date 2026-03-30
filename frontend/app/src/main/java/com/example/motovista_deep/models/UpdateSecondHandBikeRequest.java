package com.example.motovista_deep.models;

import com.example.motovista_deep.models.SecondHandBikeRequest;

public class UpdateSecondHandBikeRequest extends SecondHandBikeRequest {
    public UpdateSecondHandBikeRequest(int bike_id, String brand, String model, String year,
                                       String odometer, String price, String condition,
                                       String ownership, String engine_cc, String braking_type,
                                       String owner_details, String condition_details,
                                       String features, String image_paths) {
        super(brand, model, year, odometer, price, condition, ownership,
                engine_cc, braking_type, owner_details, condition_details, features, image_paths);
        // Set the bike_id using setter if it exists in parent class
        try {
            java.lang.reflect.Method setBikeIdMethod = SecondHandBikeRequest.class.getMethod("setBike_id", int.class);
            setBikeIdMethod.invoke(this, bike_id);
        } catch (Exception e) {
            // If setBike_id doesn't exist, we'll add it to parent class
            e.printStackTrace();
        }
    }
}