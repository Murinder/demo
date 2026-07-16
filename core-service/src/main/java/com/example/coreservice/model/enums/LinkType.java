package com.example.coreservice.model.enums;

public enum LinkType {
    GITHUB("github"),
    LINKEDIN("linkedin"),
    PORTFOLIO("portfolio"),
    WEBSITE("website"),
    CV("cv"),
    OTHER("other");

    private final String value;

    LinkType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}