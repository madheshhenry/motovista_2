package com.example.motovista_deep.models;

import com.google.gson.annotations.SerializedName;

public class InsuranceDetailResponse {
    private boolean success;
    private InsuranceDetailModel data;
    private String message;

    public boolean isSuccess() { return success; }
    public InsuranceDetailModel getData() { return data; }
    public String getMessage() { return message; }

    public static class InsuranceDetailModel extends InsuranceModel {
        @SerializedName("registration_date")
        private String registrationDate;

        @SerializedName("profile_image")
        private String profileImage;

        @SerializedName("full_name")
        private String fullName;

        public String getRegistrationDate() { return registrationDate; }
        public String getProfileImage() { return profileImage; }
        public String getFullName() { return fullName; }
    }
}
