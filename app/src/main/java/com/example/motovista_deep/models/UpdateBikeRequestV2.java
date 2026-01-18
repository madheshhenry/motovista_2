package com.example.motovista_deep.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class UpdateBikeRequestV2 implements Serializable {
    @SerializedName("id")
    private int id;

    @SerializedName("model")
    private BikeParentModel model;

    @SerializedName("variants")
    private List<BikeVariantModel> variants;

    public UpdateBikeRequestV2(int id, BikeParentModel model, List<BikeVariantModel> variants) {
        this.id = id;
        this.model = model;
        this.variants = variants;
    }

    public int getId() {
        return id;
    }

    public BikeParentModel getModel() {
        return model;
    }

    public List<BikeVariantModel> getVariants() {
        return variants;
    }
}
