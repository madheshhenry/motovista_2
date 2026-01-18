package com.example.motovista_deep.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class GetBikeByIdResponseV2 {
    @SerializedName("status")
    public String status;

    @SerializedName("data")
    public Data data;

    public static class Data {
        @SerializedName("model")
        public BikeParentModel model;

        @SerializedName("variants")
        public List<BikeVariantModel> variants;
    }
}
