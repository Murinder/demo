-- Add rating criteria that map to actual event reasons used in scoring.
-- These replace the previously hardcoded point values in RatingCalculationService.

INSERT INTO rating_criteria (id, name, description, weight, is_active, criteria_type, base_points, max_points) VALUES
    ('90000000-0000-0000-0000-000000000010',
     'task_completed', 'Баллы за выполнение задачи в проекте', 1.0, true,
     'STUDENT'::rating_criteria_type, 2, NULL),

    ('90000000-0000-0000-0000-000000000011',
     'project_completed', 'Баллы за успешное завершение проекта', 1.0, true,
     'STUDENT'::rating_criteria_type, 10, NULL),

    ('90000000-0000-0000-0000-000000000012',
     'event_participation', 'Баллы за участие в мероприятии', 1.0, true,
     'STUDENT'::rating_criteria_type, 5, NULL),

    ('90000000-0000-0000-0000-000000000013',
     'achievement_added', 'Баллы за добавление достижения в портфолио', 1.0, true,
     'STUDENT'::rating_criteria_type, 3, NULL)
ON CONFLICT (name) DO NOTHING;
