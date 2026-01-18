package com.example.motovista_deep.models;

import com.google.gson.annotations.SerializedName;

public class DeleteAnyBikeRequest {
    @SerializedName("id")
    private int id;
    
    @SerializedName("source_table")
    private String sourceTable;

    public DeleteAnyBikeRequest(int id, String sourceTable) {
        this.id = id;
        this.sourceTable = sourceTable;
    }
}
