package com.example.motovista_deep.models;

public class RegisterResponse {
    private boolean success;
    private String status; // For robustness
    private String message;
    private RegisterData data;

    public boolean isSuccess() { 
        return success || "success".equalsIgnoreCase(status) || "true".equalsIgnoreCase(status); 
    }
    
    public void setSuccess(boolean success) { this.success = success; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public RegisterData getData() { return data; }
    public void setData(RegisterData data) { this.data = data; }

    public static class RegisterData {
        private String token;
        private User customer;
        private boolean requires_verification;
        private boolean email_sent;
        private String note;

        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }

        public User getCustomer() { return customer; }
        public void setCustomer(User customer) { this.customer = customer; }

        public boolean isRequires_verification() { return requires_verification; }
        public void setRequires_verification(boolean requires_verification) {
            this.requires_verification = requires_verification;
        }

        public boolean isEmail_sent() { return email_sent; }
        public void setEmail_sent(boolean email_sent) { this.email_sent = email_sent; }

        public String getNote() { return note; }
        public void setNote(String note) { this.note = note; }
    }
}