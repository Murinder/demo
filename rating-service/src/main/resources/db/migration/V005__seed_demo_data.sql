-- ============================================================
-- SEED DEMO DATA: rating-service
-- ============================================================

-- User UUIDs (from core-service)
-- STUDENT:  00000000-0000-0000-0000-000000000001
-- TEACHER:  00000000-0000-0000-0000-000000000002
-- HEAD:     00000000-0000-0000-0000-000000000003
-- DEPT KPI: 20000000-0000-0000-0000-000000000001
-- FACULTY FIT: 10000000-0000-0000-0000-000000000001

-- ============================================================
-- 1. RATING CRITERIA
-- ============================================================
INSERT INTO rating_criteria (id, name, description, weight, is_active, criteria_type, base_points, max_points) VALUES
    ('90000000-0000-0000-0000-000000000001',
     'Средний балл', 'Средний балл успеваемости за семестр', 3.0, true,
     'STUDENT'::rating_criteria_type, 0, 100),

    ('90000000-0000-0000-0000-000000000002',
     'Проектная активность', 'Участие в проектах и выполнение задач', 2.0, true,
     'STUDENT'::rating_criteria_type, 0, 50),

    ('90000000-0000-0000-0000-000000000003',
     'Участие в мероприятиях', 'Участие в хакатонах, конференциях и других мероприятиях', 1.5, true,
     'STUDENT'::rating_criteria_type, 0, 30),

    ('90000000-0000-0000-0000-000000000004',
     'Публикации', 'Количество и качество научных публикаций', 2.5, true,
     'LECTURER'::rating_criteria_type, 0, 100),

    ('90000000-0000-0000-0000-000000000005',
     'Научное руководство', 'Руководство студенческими проектами и ВКР', 2.0, true,
     'LECTURER'::rating_criteria_type, 0, 50),

    ('90000000-0000-0000-0000-000000000006',
     'Общая эффективность', 'Комплексная оценка эффективности кафедры', 1.0, true,
     'DEPARTMENT'::rating_criteria_type, 0, 100)
ON CONFLICT (name) DO NOTHING;

-- ============================================================
-- 2. RATING SETTINGS
-- ============================================================
INSERT INTO rating_settings (id, semester, calculation_algorithm, is_active, parameters) VALUES
    ('91000000-0000-0000-0000-000000000001', 2, 'WEIGHTED_SUM', true,
     '{"weights":{"academic":0.4,"activity":0.3,"communication":0.3},"normalization":"MIN_MAX"}'::jsonb)
ON CONFLICT (semester) DO NOTHING;

-- ============================================================
-- 3. STUDENT RATINGS
-- ============================================================
INSERT INTO student_ratings (user_id, total_score, calculation_details, semester, verification_status, verified_by, verified_at) VALUES
    ('00000000-0000-0000-0000-000000000001', 78.50,
     '{"academicScore":85,"activityScore":72,"communicationScore":68,"projectsCompleted":2,"eventsAttended":3,"monthGrowth":5.2}'::jsonb,
     2, 'VERIFIED'::verification_status,
     '00000000-0000-0000-0000-000000000003', NOW())
ON CONFLICT (user_id) DO NOTHING;

-- ============================================================
-- 4. LECTURER RATINGS
-- ============================================================
INSERT INTO lecturer_ratings (user_id, total_score, calculation_details, semester, verification_status, verified_by, verified_at) VALUES
    ('00000000-0000-0000-0000-000000000002', 91.20,
     '{"publications":12,"grants":3,"supervisedProjects":5,"studentSatisfaction":4.7,"monthGrowth":2.1}'::jsonb,
     2, 'VERIFIED'::verification_status,
     '00000000-0000-0000-0000-000000000003', NOW())
ON CONFLICT (user_id) DO NOTHING;

-- ============================================================
-- 5. DEPARTMENT RATINGS
-- ============================================================
INSERT INTO department_ratings (department_id, total_score, semester, calculation_details, faculty_id) VALUES
    ('20000000-0000-0000-0000-000000000001', 85.00, 2,
     '{"avgStudentRating":78.5,"avgLecturerRating":91.2,"projectsCount":3,"eventsCount":2,"graduationRate":95.5}'::jsonb,
     '10000000-0000-0000-0000-000000000001')
ON CONFLICT (department_id) DO NOTHING;

-- ============================================================
-- 6. RATING HISTORY
-- ============================================================
INSERT INTO rating_history (id, user_id, department_id, score, reason, criteria_id, related_entity_id, semester) VALUES
    ('92000000-0000-0000-0000-000000000001',
     '00000000-0000-0000-0000-000000000001', NULL, 35.00,
     'Средний балл за семестр (4.2/5.0)',
     '90000000-0000-0000-0000-000000000001', NULL, 2),

    ('92000000-0000-0000-0000-000000000002',
     '00000000-0000-0000-0000-000000000001', NULL, 25.00,
     'Участие в хакатоне AI Solutions',
     '90000000-0000-0000-0000-000000000003',
     '80000000-0000-0000-0000-000000000001', 2),

    ('92000000-0000-0000-0000-000000000003',
     '00000000-0000-0000-0000-000000000001', NULL, 18.50,
     'Завершение проекта "Анализ успеваемости"',
     '90000000-0000-0000-0000-000000000002',
     '60000000-0000-0000-0000-000000000003', 2),

    ('92000000-0000-0000-0000-000000000004',
     '00000000-0000-0000-0000-000000000002', NULL, 40.00,
     'Публикация статьи в журнале ВАК',
     '90000000-0000-0000-0000-000000000004', NULL, 2)
ON CONFLICT (id) DO NOTHING;
