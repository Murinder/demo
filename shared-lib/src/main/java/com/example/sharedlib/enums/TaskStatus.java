package com.example.sharedlib.enums;

public enum TaskStatus {
    TO_DO("to_do"),
    IN_PROGRESS("in_progress"),
    REVIEW("review"),
    DONE("done"),
    BLOCKED("blocked");

    private final String value;

    TaskStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}