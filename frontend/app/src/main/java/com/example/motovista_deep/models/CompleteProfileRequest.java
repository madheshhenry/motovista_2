package com.example.motovista_deep.models;

public class CompleteProfileRequest {
    private String full_name;
    private String phone;
    private String dob;
    private String house_no;
    private String street;
    private String city;
    private String state;
    private String pincode;
    private String pan;

    public CompleteProfileRequest(String full_name, String phone, String dob,
                                  String house_no, String street, String city,
                                  String state, String pincode, String pan) {
        this.full_name = full_name;
        this.phone = phone;
        this.dob = dob;
        this.house_no = house_no;
        this.street = street;
        this.city = city;
        this.state = state;
        this.pincode = pincode;
        this.pan = pan;
    }

    // Getters
    public String getFull_name() { return full_name; }
    public String getPhone() { return phone; }
    public String getDob() { return dob; }
    public String getHouse_no() { return house_no; }
    public String getStreet() { return street; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getPincode() { return pincode; }
    public String getPan() { return pan; }
}