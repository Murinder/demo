package com.example.coreservice.model.enums;

public enum SessionStatus {
    ACTIVE("active"),
    EXPIRED("expired"),
    REVOKED("revoked");

    private final String value;

    SessionStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}