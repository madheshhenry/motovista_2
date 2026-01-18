package com.example.motovista_deep.models;

public class RegistrationLedgerItem {
    private int id;
    private int order_id;
    private int customer_id;
    private String customer_name;
    private String bike_name;
    private String step_1_status; // pending, completed
    private String step_2_status; // locked, pending, completed
    private String step_3_status; // locked, pending, completed
    private String step_4_status; // locked, pending, completed
    private String created_at;
    private String phone;
    private String engine_number;
    private String variant;
    private String bike_color;
    public RegistrationLedgerItem() {}

    public RegistrationLedgerItem(int id, int order_id, int customer_id, String customer_name, String bike_name, String step_1_status, String step_2_status, String step_3_status, String step_4_status, String created_at, String phone, String engine_number, String variant, String bike_color) {
        this.id = id;
        this.order_id = order_id;
        this.customer_id = customer_id;
        this.customer_name = customer_name;
        this.bike_name = bike_name;
        this.step_1_status = step_1_status;
        this.step_2_status = step_2_status;
        this.step_3_status = step_3_status;
        this.step_4_status = step_4_status;
        this.created_at = created_at;
        this.phone = phone;
        this.engine_number = engine_number;
        this.variant = variant;
        this.bike_color = bike_color;
    }

    // Getters
    public int getId() { return id; }
    public int getOrderId() { return order_id; }
    public int getCustomerId() { return customer_id; }
    public String getCustomerName() { return customer_name; }
    public String getBikeName() { return bike_name; }
    public String getStep1Status() { return step_1_status; }
    public String getStep2Status() { return step_2_status; }
    public String getStep3Status() { return step_3_status; }
    public String getStep4Status() { return step_4_status; }
    public String getCreatedAt() { return created_at; }
    public String getPhone() { return phone; }
    public String getEngineNumber() { return engine_number; }
    public String getVariant() { return variant; }
    public String getBikeColor() { return bike_color; }

    // Helper to calculate progress percentage (0%, 25%, 50%, 75%, 100%)
    public int getProgressPercentage() {
        int count = 0;
        if ("completed".equalsIgnoreCase(step_1_status)) count++;
        if ("completed".equalsIgnoreCase(step_2_status)) count++;
        if ("completed".equalsIgnoreCase(step_3_status)) count++;
        if ("completed".equalsIgnoreCase(step_4_status)) count++;
        return count * 25;
    }

    // Helper to get current active step name
    public String getCurrentIncompleteStep() {
        if (!"completed".equalsIgnoreCase(step_1_status)) return "Insurance Process";
        if (!"completed".equalsIgnoreCase(step_2_status)) return "Doc Verification";
        if (!"completed".equalsIgnoreCase(step_3_status)) return "Tax Payment";
        if (!"completed".equalsIgnoreCase(step_4_status)) return "Registration & RC";
        return "Completed";
    }
}
