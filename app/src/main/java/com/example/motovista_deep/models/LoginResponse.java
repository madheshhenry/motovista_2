package com.example.motovista_deep.models;

public class LoginResponse {
    private String status;
    private String message;
    private LoginData data;

    public String getStatus(){ return status; }
    public String getMessage(){ return message; }
    public LoginData getData(){ return data; }

    public static class LoginData {
        private User customer;   // For customer login
        private Admin admin;     // For admin login
        private String token;

        public User getCustomer(){ return customer; }
        public Admin getAdmin(){ return admin; }
        public String getToken(){ return token; }
    }

    public static class Admin {
        private int id;
        private String username;

        public int getId() { return id; }
        public String getUsername() { return username; }
    }
}
