-- ============================================================
-- Обновить event_type у существующих seed-мероприятий (были NULL)
-- ============================================================
UPDATE events SET event_type = 'Хакатон'     WHERE id = '80000000-0000-0000-0000-000000000001';
UPDATE events SET event_type = 'Конференция' WHERE id = '80000000-0000-0000-0000-000000000002';
UPDATE events SET event_type = 'Акселератор' WHERE id = '80000000-0000-0000-0000-000000000003';

-- ============================================================
-- Дополнительные мероприятия от преподавателя для демо
-- Teacher: 00000000-0000-0000-0000-000000000002
-- ============================================================

-- Карьерное мероприятие
INSERT INTO events (id, title, description, start_date, end_date, format, status,
                    location, max_participants, registration_deadline,
                    created_by, organizer_name, event_type)
VALUES (
    '80000000-0000-0000-0000-000000000004',
    'День карьеры IT',
    'Ежегодная ярмарка вакансий IT-компаний. Презентации работодателей, экспресс-собеседования, мастер-классы по составлению резюме.',
    '2026-04-25 10:00:00+03', '2026-04-25 18:00:00+03',
    'OFFLINE'::event_format, 'REGISTRATION_OPEN'::event_status,
    'Главный корпус, холл 1-го этажа', 300, '2026-04-23 23:59:00+03',
    '00000000-0000-0000-0000-000000000002',
    'Центр карьеры университета',
    'Карьера'
);

-- Конференция по фронтенду
INSERT INTO events (id, title, description, start_date, end_date, format, status,
                    location, max_participants, registration_deadline,
                    created_by, organizer_name, event_type)
VALUES (
    '80000000-0000-0000-0000-000000000005',
    'Frontend Meetup: React & Beyond',
    'Встреча разработчиков, посвящённая современному фронтенду. Доклады по React 19, Server Components, новым подходам к стейт-менеджменту.',
    '2026-05-08 17:00:00+03', '2026-05-08 20:00:00+03',
    'HYBRID'::event_format, 'REGISTRATION_OPEN'::event_status,
    'Корпус 3, ауд. 210 / Zoom', 80, '2026-05-06 23:59:00+03',
    '00000000-0000-0000-0000-000000000002',
    'Кафедра программной инженерии',
    'Конференция'
);

-- Хакатон по кибербезопасности
INSERT INTO events (id, title, description, start_date, end_date, format, status,
                    location, max_participants, registration_deadline,
                    created_by, organizer_name, event_type)
VALUES (
    '80000000-0000-0000-0000-000000000006',
    'CyberSec Hackathon 2026',
    'CTF-хакатон по кибербезопасности. Участники решают задачи по криптографии, реверс-инжинирингу, веб-уязвимостям и форензике.',
    '2026-06-20 10:00:00+03', '2026-06-21 22:00:00+03',
    'OFFLINE'::event_format, 'REGISTRATION_OPEN'::event_status,
    'Корпус 1, лаборатория 115', 60, '2026-06-15 23:59:00+03',
    '00000000-0000-0000-0000-000000000002',
    'Кафедра информационной безопасности',
    'Хакатон'
);

-- Консультация
INSERT INTO events (id, title, description, start_date, end_date, format, status,
                    location, max_participants, registration_deadline,
                    created_by, organizer_name, event_type)
VALUES (
    '80000000-0000-0000-0000-000000000007',
    'Консультация по дипломным проектам',
    'Групповая консультация для студентов, готовящих дипломные работы. Обсуждение требований, структуры и сроков.',
    '2026-04-18 14:00:00+03', '2026-04-18 16:00:00+03',
    'OFFLINE'::event_format, 'REGISTRATION_OPEN'::event_status,
    'Кабинет 401', 20, '2026-04-17 23:59:00+03',
    '00000000-0000-0000-0000-000000000002',
    'Кафедра программной инженерии',
    'Консультация'
);
