package com.example.motovista_deep.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class AddBikeRequestV2 implements Serializable {
    @SerializedName("model")
    private BikeParentModel model;

    @SerializedName("variants")
    private List<BikeVariantModel> variants;

    public AddBikeRequestV2(BikeParentModel model, List<BikeVariantModel> variants) {
        this.model = model;
        this.variants = variants;
    }

    public BikeParentModel getModel() {
        return model;
    }

    public List<BikeVariantModel> getVariants() {
        return variants;
    }
}
