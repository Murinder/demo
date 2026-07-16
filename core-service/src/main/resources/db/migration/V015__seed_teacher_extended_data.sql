-- ============================================================
-- SEED EXTENDED DATA FOR TEACHER (user 0002)
-- ============================================================

-- Update lecturer_academic_metrics with breakdown fields
UPDATE lecturer_academic_metrics SET
    monographs = 2,
    articles = 24,
    conferences = 15,
    teaching_start_year = 2010,
    supervised_phd = 1,
    supervised_masters = 8,
    supervised_bachelors = 32
WHERE user_id = '00000000-0000-0000-0000-000000000002';

-- Set education history and interests for teacher
UPDATE users SET
    education_history = '[{"degree":"Кандидат технических наук","university":"Московский государственный университет","program":"Системы автоматизации проектирования","year":"2012"},{"degree":"Магистр","university":"Московский государственный университет","program":"Программная инженерия","year":"2008"}]'::jsonb,
    interests = 'Искусственный интеллект, Машинное обучение, Веб-технологии, Облачные вычисления'
WHERE id = '00000000-0000-0000-0000-000000000002';

-- Seed awards for teacher
INSERT INTO user_awards (id, user_id, title, year) VALUES
    ('a0000000-0000-0000-0000-000000000004', '00000000-0000-0000-0000-000000000002',
     'Лучший преподаватель года', '2023'),
    ('a0000000-0000-0000-0000-000000000005', '00000000-0000-0000-0000-000000000002',
     'Грант РФФИ', '2022'),
    ('a0000000-0000-0000-0000-000000000006', '00000000-0000-0000-0000-000000000002',
     'Благодарность ректора', '2021')
ON CONFLICT (id) DO NOTHING;
