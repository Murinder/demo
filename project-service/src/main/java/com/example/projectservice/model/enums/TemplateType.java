package com.example.projectservice.model.enums;

public enum TemplateType {
    DIPLOMA("diploma"),
    RESEARCH("research"),
    COMMERCIAL_CASE("commercial_case"),
    HACKATHON("hackathon"),
    OTHER("other");

    private final String value;

    TemplateType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}