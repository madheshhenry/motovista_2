package com.example.motovista_deep.models;

public class RegisterResponse {
    private String status;
    private String message;
    private Data data;

    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public Data getData() { return data; }

    public static class Data {
        private User customer;
        private String token;

        public User getCustomer() { return customer; }
        public String getToken() { return token; }
    }
}
