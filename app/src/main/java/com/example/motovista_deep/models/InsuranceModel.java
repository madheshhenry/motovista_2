package com.example.motovista_deep.models;

import com.google.gson.annotations.SerializedName;

public class InsuranceModel {
    private int id;
    
    @SerializedName("order_id")
    private int orderId;
    
    @SerializedName("customer_id")
    private int customerId;
    
    @SerializedName("customer_name")
    private String customerName;
    
    @SerializedName("bike_name")
    private String bikeName;
    
    @SerializedName("policy_number")
    private String policyNumber;
    
    @SerializedName("full_insurance_expiry")
    private String fullInsuranceExpiry;
    
    @SerializedName("third_party_expiry")
    private String thirdPartyExpiry;
    
    private String status;
    private String phone;

    public InsuranceModel() {}

    public int getId() { return id; }
    public int getOrderId() { return orderId; }
    public int getCustomerId() { return customerId; }
    public String getCustomerName() { return customerName; }
    public String getBikeName() { return bikeName; }
    public String getPolicyNumber() { return policyNumber; }
    public String getFullInsuranceExpiry() { return fullInsuranceExpiry; }
    public String getThirdPartyExpiry() { return thirdPartyExpiry; }
    public String getStatus() { return status; }
    public String getPhone() { return phone; }
    
    public void setPolicyNumber(String policyNumber) { this.policyNumber = policyNumber; }
    public void setStatus(String status) { this.status = status; }
}
