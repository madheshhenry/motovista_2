package com.example.motovista_deep.models;

import java.util.List;

public class InsuranceResponse {
    private boolean success;
    private List<InsuranceModel> data;
    private String message;

    public boolean isSuccess() { return success; }
    public List<InsuranceModel> getData() { return data; }
    public String getMessage() { return message; }
}
