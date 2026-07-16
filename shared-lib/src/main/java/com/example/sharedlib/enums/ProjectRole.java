package com.example.sharedlib.enums;

public enum ProjectRole {
    LEADER("leader"),
    MEMBER("member"),
    MENTOR("mentor"),
    OBSERVER("observer");

    private final String value;

    ProjectRole(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}