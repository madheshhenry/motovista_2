package com.example.motovista_deep.models;

public class UpdateProfileRequest {
    private String full_name;
    private String email;
    private String phone;

    public UpdateProfileRequest(String full_name, String email, String phone) {
        this.full_name = full_name;
        this.email = email;
        this.phone = phone;
    }

    // Getters (Optional, but good practice)
    public String getFull_name() { return full_name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
}