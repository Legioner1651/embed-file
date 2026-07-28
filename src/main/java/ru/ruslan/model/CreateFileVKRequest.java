package ru.ruslan.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public class CreateFileVKRequest {
    private String pathFileVK;
    private String orderId;
    private String exerciseId;
    private String kns;
    private String billingAccount;
    private Instant time;       // формат: 2026-07-05T07:53:03.429Z
    private String epcParams;

    // Getters and Setters
    public String getPathFileVK() {
        return pathFileVK;
    }

    public void setPathFileVK(String pathFileVK) {
        this.pathFileVK = pathFileVK;
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

    public String getBillingAccount() {
        return billingAccount;
    }

    public void setBillingAccount(String billingAccount) {
        this.billingAccount = billingAccount;
    }

    public Instant getTime() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = time;
    }

    public String getEpcParams() {
        return epcParams;
    }

    public void setEpcParams(String epcParams) {
        this.epcParams = epcParams;
    }
}
