package com.example.ratingservice;

import java.util.Map;

/**
 * Shared constants for rating calculation and display.
 */
public final class RatingConstants {

    private RatingConstants() {}

    /**
     * Maps history reason strings to scoring categories.
     * Used by both calculation and display logic — keep in sync.
     */
    public static final Map<String, String> REASON_TO_CATEGORY = Map.of(
            "task_completed",         "academic",
            "project_completed",      "academic",
            "event_participation",    "activity",
            "achievement_added",      "achievements"
    );

    public static final Map<String, String> REASON_TITLES = Map.of(
            "task_completed",         "Выполнение задачи",
            "project_completed",      "Завершение проекта",
            "event_participation",    "Участие в мероприятии",
            "achievement_added",      "Новое достижение"
    );

    public static final Map<String, String> REASON_DESCRIPTIONS = Map.of(
            "task_completed",         "Баллы за выполнение задачи в проекте",
            "project_completed",      "Баллы за успешное завершение проекта",
            "event_participation",    "Баллы за участие в мероприятии",
            "achievement_added",      "Баллы за добавление достижения в портфолио"
    );
}
