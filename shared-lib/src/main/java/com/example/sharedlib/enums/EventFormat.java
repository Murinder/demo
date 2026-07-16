package com.example.sharedlib.enums;

public enum EventFormat {
    ONLINE("online"),
    OFFLINE("offline"),
    HYBRID("hybrid");

    private final String value;

    EventFormat(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}