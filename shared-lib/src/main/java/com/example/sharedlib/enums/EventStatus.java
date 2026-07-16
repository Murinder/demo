package com.example.sharedlib.enums;

public enum EventStatus {
    DRAFT("draft"),
    PUBLISHED("published"),
    REGISTRATION_OPEN("registration_open"),
    IN_PROGRESS("in_progress"),
    COMPLETED("completed"),
    CANCELLED("cancelled");

    private final String value;

    EventStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}