-- ============================================================
-- SEED DEMO DATA: portfolio-service
-- ============================================================

-- User UUIDs (from core-service)
-- STUDENT:  00000000-0000-0000-0000-000000000001
-- TEACHER:  00000000-0000-0000-0000-000000000002
-- HEAD:     00000000-0000-0000-0000-000000000003

-- ============================================================
-- 1. PORTFOLIOS
-- ============================================================
INSERT INTO portfolios (user_id, visibility_settings) VALUES
    ('00000000-0000-0000-0000-000000000001',
     '{"showAchievements":true,"showSkills":true,"showReviews":true,"showAcademicInfo":true,"isPublic":true}'::jsonb),
    ('00000000-0000-0000-0000-000000000002',
     '{"showAchievements":true,"showSkills":true,"showReviews":false,"showAcademicInfo":false,"isPublic":false}'::jsonb)
ON CONFLICT (user_id) DO NOTHING;

-- ============================================================
-- 2. ACHIEVEMENTS (for student)
-- ============================================================
INSERT INTO achievements (id, portfolio_id, type, title, description, date, proof_document, is_external, issuer) VALUES
    ('A0000000-0000-0000-0000-000000000001',
     '00000000-0000-0000-0000-000000000001',
     'PROJECT'::achievement_type,
     'Система рекомендаций для учебных курсов',
     'Разработка рекомендательной системы на основе collaborative filtering для персонализации обучения.',
     '2026-01-15', NULL, false, NULL),

    ('A0000000-0000-0000-0000-000000000002',
     '00000000-0000-0000-0000-000000000001',
     'EVENT'::achievement_type,
     '1-е место на хакатоне AI Solutions 2025',
     'Победа в хакатоне с проектом чат-бота для учебных материалов.',
     '2025-11-20', NULL, false, 'Факультет информационных технологий'),

    ('A0000000-0000-0000-0000-000000000003',
     '00000000-0000-0000-0000-000000000001',
     'CERTIFICATE'::achievement_type,
     'AWS Cloud Practitioner',
     'Сертификат базового уровня по облачным технологиям Amazon Web Services.',
     '2025-09-01', NULL, true, 'Amazon Web Services'),

    ('A0000000-0000-0000-0000-000000000004',
     '00000000-0000-0000-0000-000000000001',
     'PUBLICATION'::achievement_type,
     'Анализ методов кластеризации для образовательных данных',
     'Статья опубликована в сборнике трудов студенческой конференции.',
     '2026-02-10', NULL, false, 'Издательство университета')
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- 3. SKILLS (for student portfolio)
-- ============================================================
INSERT INTO skills (id, portfolio_id, name, level, verification_status, verified_by, verified_at) VALUES
    ('A1000000-0000-0000-0000-000000000001',
     '00000000-0000-0000-0000-000000000001',
     'Java', 4, 'VERIFIED'::verification_status,
     '00000000-0000-0000-0000-000000000002', NOW()),

    ('A1000000-0000-0000-0000-000000000002',
     '00000000-0000-0000-0000-000000000001',
     'Python', 3, 'VERIFIED'::verification_status,
     '00000000-0000-0000-0000-000000000002', NOW()),

    ('A1000000-0000-0000-0000-000000000003',
     '00000000-0000-0000-0000-000000000001',
     'Machine Learning', 2, 'PENDING'::verification_status,
     NULL, NULL),

    ('A1000000-0000-0000-0000-000000000004',
     '00000000-0000-0000-0000-000000000001',
     'SQL', 3, 'PENDING'::verification_status,
     NULL, NULL)
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- 4. REVIEWS
-- ============================================================
INSERT INTO reviews (id, portfolio_id, from_user_id, content, rating, type, related_entity_id) VALUES
    ('A2000000-0000-0000-0000-000000000001',
     '00000000-0000-0000-0000-000000000001',
     '00000000-0000-0000-0000-000000000002',
     'Отличная работа над проектом рекомендательной системы. Иван проявил инициативу и глубокое понимание ML-алгоритмов.',
     4, 'PROJECT'::review_type,
     '60000000-0000-0000-0000-000000000001'),

    ('A2000000-0000-0000-0000-000000000002',
     '00000000-0000-0000-0000-000000000001',
     '00000000-0000-0000-0000-000000000003',
     'Перспективный студент с хорошей научной базой. Рекомендую к участию в исследовательских проектах кафедры.',
     5, 'MENTORSHIP'::review_type,
     NULL)
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- 5. ACADEMIC INFO (for student)
-- ============================================================
INSERT INTO academic_info (portfolio_id, faculty, department, study_program, group_name,
                           enrollment_year, current_semester, gpa, credits_earned, progress_percentage) VALUES
    ('00000000-0000-0000-0000-000000000001',
     'Факультет информационных технологий',
     'Кафедра программной инженерии',
     'Программная инженерия', 'ИТ-21',
     2023, 4, 4.20, 120, 67.50)
ON CONFLICT (portfolio_id) DO NOTHING;
