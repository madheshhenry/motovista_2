package com.example.motovista_deep.models;

public class LoginResponse {
    private boolean success;
    private String message;
    private LoginData data;

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LoginData getData() { return data; }
    public void setData(LoginData data) { this.data = data; }

    public static class LoginData {
        private String token;
        private User customer;
        private boolean requires_verification;

        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }

        public User getCustomer() { return customer; }
        public void setCustomer(User customer) { this.customer = customer; }

        public boolean isRequires_verification() { return requires_verification; }
        public void setRequires_verification(boolean requires_verification) {
            this.requires_verification = requires_verification;
        }
    }
}