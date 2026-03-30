package com.example.motovista_deep.models;

public class RegisterRequest {
    private String full_name;
    private String email;
    private String phone;
    private String password;

    public RegisterRequest(String full_name, String email, String phone, String password) {
        this.full_name = full_name;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }
}
