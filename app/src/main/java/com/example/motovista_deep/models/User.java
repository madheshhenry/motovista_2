package com.example.motovista_deep.models;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("id")
    private int id;

    @SerializedName("full_name")
    private String full_name;

    @SerializedName("email")
    private String email;

    @SerializedName("phone")
    private String phone;

    @SerializedName("dob")
    private String dob;

    @SerializedName("house_no")
    private String house_no;

    @SerializedName("street")
    private String street;

    @SerializedName("city")
    private String city;

    @SerializedName("state")
    private String state;

    @SerializedName("pincode")
    private String pincode;

    @SerializedName("pan")
    private String pan;

    @SerializedName("profile_image")
    private String profile_image;

    @SerializedName("aadhar_front")
    private String aadhar_front;

    @SerializedName("aadhar_back")
    private String aadhar_back;

    @SerializedName("email_verified")
    private boolean email_verified;

    @SerializedName("phone_verified")
    private boolean phone_verified;

    @SerializedName("status")
    private String status;

    @SerializedName("created_at")
    private String created_at;

    @SerializedName("dob_formatted")
    private String dob_formatted;

    @SerializedName("is_profile_completed")
    private boolean is_profile_completed;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFull_name() { return full_name; }
    public void setFull_name(String full_name) { this.full_name = full_name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getDob() { return dob; }
    public void setDob(String dob) { this.dob = dob; }

    public String getHouse_no() { return house_no; }
    public void setHouse_no(String house_no) { this.house_no = house_no; }

    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getPincode() { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }

    public String getPan() { return pan; }
    public void setPan(String pan) { this.pan = pan; }

    public String getProfile_image() { return profile_image; }
    public void setProfile_image(String profile_image) { this.profile_image = profile_image; }

    public String getAadhar_front() { return aadhar_front; }
    public void setAadhar_front(String aadhar_front) { this.aadhar_front = aadhar_front; }

    public String getAadhar_back() { return aadhar_back; }
    public void setAadhar_back(String aadhar_back) { this.aadhar_back = aadhar_back; }

    public boolean isEmail_verified() { return email_verified; }
    public void setEmail_verified(boolean email_verified) { this.email_verified = email_verified; }

    public boolean isPhone_verified() { return phone_verified; }
    public void setPhone_verified(boolean phone_verified) { this.phone_verified = phone_verified; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreated_at() { return created_at; }
    public void setCreated_at(String created_at) { this.created_at = created_at; }

    public String getDob_formatted() { return dob_formatted; }
    public void setDob_formatted(String dob_formatted) { this.dob_formatted = dob_formatted; }

    public boolean isIs_profile_completed() {
        return is_profile_completed;
    }

    public void setIs_profile_completed(boolean is_profile_completed) {
        this.is_profile_completed = is_profile_completed;
    }
}