package com.example.motovista_deep.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class BikeParentModel implements Serializable {
    // Basic Details
    @SerializedName("brand")
    private String brand;
    @SerializedName("model_name")
    private String modelName;
    @SerializedName("model_year")
    private String modelYear;
    @SerializedName("engine_cc")
    private String engineCC;
    @SerializedName("fuel_type")
    private String fuelType;
    @SerializedName("transmission")
    private String transmission;
    @SerializedName("mileage")
    private String mileage;
    @SerializedName("fuel_tank_capacity")
    private String fuelTankCapacity;
    @SerializedName("kerb_weight")
    private String kerbWeight;
    @SerializedName("seat_height")
    private String seatHeight;
    @SerializedName("ground_clearance")
    private String groundClearance;
    @SerializedName("max_torque")
    private String maxTorque;
    @SerializedName("max_power")
    private String maxPower;
    
    // Warranty & Service
    @SerializedName("warranty_period")
    private String warrantyPeriod;
    @SerializedName("free_services")
    private String freeServices;

    // Common JSON fields
    @SerializedName("invoice_legal_notes")
    private LegalNotes legalNotes;
    
    @SerializedName("mandatory_fittings")
    private List<CustomFitting> mandatoryFittings;

    @SerializedName("additional_fittings")
    private List<CustomFitting> additionalFittings;

    public void setMandatoryFittings(List<CustomFitting> f) { this.mandatoryFittings = f; }
    public void setAdditionalFittings(List<CustomFitting> f) { this.additionalFittings = f; }



    // Getters and Setters (Omitted for brevity, assume Lombok or generate in IDE)
    // Constructor
    public BikeParentModel() {}

    public static class LegalNotes implements Serializable {
        @SerializedName("registration_proof")
        public String regProof;
        @SerializedName("price_disclaimer")
        public String priceDisclaimer;
    }

    // Setters
    public void setBrand(String s) { this.brand = s; }
    public void setModelName(String s) { this.modelName = s; }
    public void setModelYear(String s) { this.modelYear = s; }
    public void setEngineCC(String s) { this.engineCC = s; }
    public void setFuelType(String s) { this.fuelType = s; }
    public void setTransmission(String s) { this.transmission = s; }
    public void setMileage(String s) { this.mileage = s; }
    public void setFuelTankCapacity(String s) { this.fuelTankCapacity = s; }
    public void setKerbWeight(String s) { this.kerbWeight = s; }
    public void setSeatHeight(String s) { this.seatHeight = s; }
    public void setGroundClearance(String s) { this.groundClearance = s; }
    public void setMaxTorque(String s) { this.maxTorque = s; }
    public void setMaxPower(String s) { this.maxPower = s; }
    public void setWarrantyPeriod(String s) { this.warrantyPeriod = s; }
    public void setFreeServices(String s) { this.freeServices = s; }
    public void setLegalNotes(LegalNotes l) { this.legalNotes = l; }

    public String getBrand() { return brand; }
    public String getModelName() { return modelName; }
    public String getModelYear() { return modelYear; }

    
    public String getEngineCC() { return engineCC; }
    public String getFuelType() { return fuelType; }
    public String getTransmission() { return transmission; }
    public String getMileage() { return mileage; }
    public String getFuelTankCapacity() { return fuelTankCapacity; }
    public String getKerbWeight() { return kerbWeight; }
    public String getSeatHeight() { return seatHeight; }
    public String getGroundClearance() { return groundClearance; }
    public String getMaxTorque() { return maxTorque; }
    public String getMaxPower() { return maxPower; }
    public String getWarrantyPeriod() { return warrantyPeriod; }
    public String getFreeServices() { return freeServices; }
    public LegalNotes getLegalNotes() { return legalNotes; }
    public List<CustomFitting> getMandatoryFittings() { return mandatoryFittings; }
    public List<CustomFitting> getAdditionalFittings() { return additionalFittings; }
}
