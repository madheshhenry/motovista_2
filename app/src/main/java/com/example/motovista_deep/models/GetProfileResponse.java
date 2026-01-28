    package com.example.motovista_deep.models;

    public class GetProfileResponse {
        public boolean success; // Preferred
        public boolean status;  // Keep for backward compatibility
        public String message;
        public Data data;

        public boolean isSuccess() {
            return success || status;
        }

        public class Data {
            public int id;
            public String full_name;
            public String email;
            public String phone;
            public String profile_image;
        }
    }