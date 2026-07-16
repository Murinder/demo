package com.example.sharedlib.enums;

import lombok.Data;
import lombok.Getter;

@Getter
public enum UserRole {
    STUDENT("STUDENT"),
    LECTURER("LECTURER"),
    DEPARTMENT_HEAD("DEPARTMENT_HEAD"),
    PARTNER("PARTNER"),
    ADMIN("ADMIN");

    private final String value;

    UserRole(String value) {
        this.value = value;
    }

}