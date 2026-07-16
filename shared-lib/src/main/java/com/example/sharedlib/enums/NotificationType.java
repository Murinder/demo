package com.example.sharedlib.enums;

public enum NotificationType {
    TASK_ASSIGNED("task_assigned"),
    DEADLINE("deadline"),
    PROJECT_INVITE("project_invite"),
    EVENT_UPDATE("event_update"),
    SYSTEM("system"),
    RATING_UPDATE("rating_update");

    private final String value;

    NotificationType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
