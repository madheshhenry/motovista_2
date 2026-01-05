    package com.example.motovista_deep.models;

    public class GetProfileResponse {
        // This MUST be named 'status' to match your PHP and the error line
        public boolean status;
        public String message;
        public Data data;

        public class Data {
            public int id;
            public String full_name;
            public String email;
            public String phone;
            public String profile_image;
        }
    }