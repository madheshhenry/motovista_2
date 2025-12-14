package com.example.motovista_deep.models;
public class GetProfileResponse {
    public boolean status;
    public Data data;

    public static class Data {
        public String full_name;
        public String email;
        public String phone;
        public String profile_image;
    }
}
