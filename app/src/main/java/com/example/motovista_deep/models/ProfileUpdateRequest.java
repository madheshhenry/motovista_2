package com.example.motovista_deep.models;

public class ProfileUpdateRequest {
    private String full_name;
    private String email;
    private String phone;
    private String dob;
    private String house_no; // Changed from 'address'
    private String street;   // Added
    private String city;
    private String state;
    private String pincode;

    public ProfileUpdateRequest(String full_name, String email, String phone, String dob,
                                String house_no, String street, String city, String state, String pincode) {
        this.full_name = full_name;
        this.email = email;
        this.phone = phone;
        this.dob = dob;
        this.house_no = house_no;
        this.street = street;
        this.city = city;
        this.state = state;
        this.pincode = pincode;
    }
}