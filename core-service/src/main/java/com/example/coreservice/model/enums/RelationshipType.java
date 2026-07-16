package com.example.coreservice.model.enums;

public enum RelationshipType {
    PARENT("parent"),
    SPOUSE("spouse"),
    SIBLING("sibling"),
    FRIEND("friend"),
    COLLEAGUE("colleague"),
    OTHER("other");

    private final String value;

    RelationshipType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}