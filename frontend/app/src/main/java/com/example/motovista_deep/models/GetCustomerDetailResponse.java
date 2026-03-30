package com.example.motovista_deep.models;

import com.google.gson.annotations.SerializedName;

public class GetCustomerDetailResponse {
    public boolean status;
    public String message;
    public Data data;

    public static class Data {
        public int id;

        @SerializedName("full_name")
        public String full_name;

        public String email;
        public String phone;

        @SerializedName("created_at")
        public String created_at;

        public String dob;
        public String pan;
        public String address;

        @SerializedName("house_no")
        public String house_no;

        public String street;
        public String city;
        public String state;
        public String pincode;

        // Image fields
        @SerializedName("profile_image")
        public String profile_image;

        @SerializedName("aadhar_front")
        public String aadhar_front;

        @SerializedName("aadhar_back")
        public String aadhar_back;
    }
}