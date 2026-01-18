package com.example.motovista_deep.models;

import com.google.gson.annotations.SerializedName;

public class UpdateWorkflowRequest {
    @SerializedName("workflow_stage")
    private String workflowStage;

    @SerializedName("active_order_id")
    private Integer activeOrderId;

    public UpdateWorkflowRequest(String workflowStage, Integer activeOrderId) {
        this.workflowStage = workflowStage;
        this.activeOrderId = activeOrderId;
    }
}
