-- ============================================================
-- SEED DEMO DATA: document-service
-- ============================================================

-- User UUIDs (from core-service)
-- TEACHER:  00000000-0000-0000-0000-000000000002
-- HEAD:     00000000-0000-0000-0000-000000000003

-- ============================================================
-- 1. DOCUMENT TEMPLATES
-- ============================================================
INSERT INTO document_templates (id, name, description, file_path, document_type, created_by,
                                 status, version, is_public, faculty_id, department_id) VALUES
    ('D0000000-0000-0000-0000-000000000001',
     'Справка об обучении', 'Шаблон справки об обучении студента в университете.',
     '/templates/study-certificate.docx',
     'CERTIFICATE'::document_type, '00000000-0000-0000-0000-000000000003',
     'ACTIVE'::template_status, 1, true,
     '10000000-0000-0000-0000-000000000001', '20000000-0000-0000-0000-000000000001'),

    ('D0000000-0000-0000-0000-000000000002',
     'Рекомендательное письмо', 'Шаблон рекомендательного письма от научного руководителя.',
     '/templates/recommendation-letter.docx',
     'RECOMMENDATION'::document_type, '00000000-0000-0000-0000-000000000003',
     'ACTIVE'::template_status, 1, true,
     '10000000-0000-0000-0000-000000000001', NULL),

    ('D0000000-0000-0000-0000-000000000003',
     'Заявка на грант', 'Шаблон заявки на получение исследовательского гранта.',
     '/templates/grant-application.docx',
     'GRANT_APPLICATION'::document_type, '00000000-0000-0000-0000-000000000002',
     'DRAFT'::template_status, 1, false,
     '10000000-0000-0000-0000-000000000001', '20000000-0000-0000-0000-000000000001')
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- 2. PLACEHOLDERS
-- ============================================================
INSERT INTO placeholders (template_id, placeholder, description, data_type, placeholder_type, example_value, is_required) VALUES
    -- Справка об обучении
    ('D0000000-0000-0000-0000-000000000001', '{{STUDENT_NAME}}', 'ФИО студента', 'STRING', 'USER'::placeholder_type, 'Студентов Иван Петрович', true),
    ('D0000000-0000-0000-0000-000000000001', '{{FACULTY}}', 'Название факультета', 'STRING', 'DEPARTMENT'::placeholder_type, 'Факультет информационных технологий', true),
    ('D0000000-0000-0000-0000-000000000001', '{{DATE}}', 'Дата выдачи справки', 'DATE', 'SYSTEM'::placeholder_type, '2026-04-01', true),

    -- Рекомендательное письмо
    ('D0000000-0000-0000-0000-000000000002', '{{STUDENT_NAME}}', 'ФИО студента', 'STRING', 'USER'::placeholder_type, 'Студентов Иван Петрович', true),
    ('D0000000-0000-0000-0000-000000000002', '{{SUPERVISOR_NAME}}', 'ФИО научного руководителя', 'STRING', 'USER'::placeholder_type, 'Преподавателева Мария Александровна', true),
    ('D0000000-0000-0000-0000-000000000002', '{{PROJECT_TITLE}}', 'Название проекта/ВКР', 'STRING', 'PROJECT'::placeholder_type, 'Система рекомендаций для учебных курсов', false)
ON CONFLICT (template_id, placeholder) DO NOTHING;
