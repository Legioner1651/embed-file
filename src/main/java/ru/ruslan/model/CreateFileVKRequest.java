package ru.ruslan.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateFileVKRequest {
    private String pathCreateFileVK;
    private String orderId;
    private String exerciseId;
    private String kns;

    @JsonProperty("KNS")
    private String KNS;

    private String billingAccount;
    private String time;
    private String epcParams;

    // Getters and Setters
    public String getPathCreateFileVK() {
        return pathCreateFileVK;
    }

    public void setPathCreateFileVK(String pathCreateFileVK) {
        this.pathCreateFileVK = pathCreateFileVK;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(String exerciseId) {
        this.exerciseId = exerciseId;
    }

    public String getKns() {
        return kns;
    }

    public void setKns(String kns) {
        this.kns = kns;
    }

    public String getKNS() {
        return KNS;
    }

    public void setKNS(String KNS) {
        this.KNS = KNS;
    }

    public String getBillingAccount() {
        return billingAccount;
    }

    public void setBillingAccount(String billingAccount) {
        this.billingAccount = billingAccount;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getEpcParams() {
        return epcParams;
    }

    public void setEpcParams(String epcParams) {
        this.epcParams = epcParams;
    }
}