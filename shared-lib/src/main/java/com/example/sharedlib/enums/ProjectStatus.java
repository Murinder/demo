package com.example.sharedlib.enums;

import lombok.Getter;

@Getter
public enum ProjectStatus {
    ACTIVE("active"),
    COMPLETED("completed"),
    FROZEN("frozen"),
    CANCELLED("cancelled");

    private final String value;

    ProjectStatus(String value) {
        this.value = value;
    }

}