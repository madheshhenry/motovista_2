package com.example.motovista.models;

public class SignupRequest {
    private String name;
    private String email;
    private String phone;
    private String password;

    public SignupRequest(String name, String email, String phone, String password) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }
}
