package com.example.motovista.models;
import java.util.List;

public class CustomersResponse {
    private boolean success;
    private List<CustomerModel> data;
    private String message;

    public boolean isSuccess() { return success; }
    public List<CustomerModel> getData() { return data; }
    public String getMessage() { return message; }
}
