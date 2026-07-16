-- ENUM типы для формата мероприятий
CREATE TYPE event_format AS ENUM ('online', 'offline', 'hybrid');

-- ENUM типы для статуса заявок на мероприятия
CREATE TYPE application_status AS ENUM ('submitted', 'approved', 'rejected', 'waitlisted');

-- ENUM типы для статуса мероприятий
CREATE TYPE event_status AS ENUM ('draft', 'published', 'registration_open', 'in_progress', 'completed', 'cancelled');

-- Мероприятия
-- Хранит информацию обо всех мероприятиях (хакатонах, конференциях, акселераторах)
-- Используется для отображения в общем списке, детальной страницы мероприятия и управления
CREATE TABLE events (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL, -- Название мероприятия
    description TEXT, -- Подробное описание мероприятия
    start_date TIMESTAMPTZ NOT NULL, -- Дата и время начала
    end_date TIMESTAMPTZ NOT NULL, -- Дата и время окончания
    format event_format NOT NULL, -- Формат проведения
    status event_status NOT NULL DEFAULT 'draft', -- Текущий статус мероприятия
    rules TEXT, -- Правила участия
    budget NUMERIC(15,2), -- Бюджет мероприятия
    evaluation_criteria TEXT, -- Критерии оценки участников
    created_by UUID NOT NULL, -- ID создателя мероприятия
    created_at TIMESTAMPTZ DEFAULT NOW(), -- Время создания записи
    updated_at TIMESTAMPTZ DEFAULT NOW(), -- Время последнего обновления
    location VARCHAR(512), -- Место проведения (для offline и hybrid)
    max_participants INTEGER, -- Максимальное количество участников
    registration_deadline TIMESTAMPTZ -- Дедлайн регистрации
);

COMMENT ON TABLE events IS 'Основная таблица мероприятий. Хранит информацию о хакатонах, конференциях и других событиях';
COMMENT ON COLUMN events.format IS 'Формат проведения мероприятия: online (онлайн), offline (офлайн), hybrid (гибридный)';
COMMENT ON COLUMN events.status IS 'Текущий статус мероприятия: draft (черновик), published (опубликовано), registration_open (открыта регистрация), in_progress (в процессе), completed (завершено), cancelled (отменено)';
COMMENT ON COLUMN events.location IS 'Адрес проведения мероприятия для офлайн и гибридных форматов. Для онлайн-мероприятий может содержать ссылку на видеоконференцию';

-- Заявки на участие в мероприятиях
-- Хранит информацию о заявках пользователей на участие в мероприятиях
-- Используется для управления процессом отбора участников
CREATE TABLE event_applications (
    id UUID PRIMARY KEY,
    event_id UUID NOT NULL REFERENCES events(id) ON DELETE CASCADE,
    user_id UUID NOT NULL, -- ID пользователя
    team_id UUID, -- ID команды (NULL для индивидуальных заявок)
    status application_status NOT NULL DEFAULT 'submitted', -- Статус заявки
    created_at TIMESTAMPTZ DEFAULT NOW(), -- Время подачи заявки
    motivation TEXT, -- Мотивационное письмо/комментарий
    skills TEXT, -- Навыки, которые пользователь привнесет в мероприятие
    updated_at TIMESTAMPTZ DEFAULT NOW() -- Время последнего обновления статуса
);

COMMENT ON TABLE event_applications IS 'Таблица заявок на участие в мероприятиях. Хранит информацию о поданных заявках и их статусе';
COMMENT ON COLUMN event_applications.status IS 'Текущий статус заявки: submitted (подана), approved (одобрена), rejected (отклонена), waitlisted (в резерве)';
COMMENT ON COLUMN event_applications.motivation IS 'Текст мотивационного письма или комментария, который пользователь предоставил при подаче заявки';

-- Команды мероприятий
-- Хранит информацию о командах, сформированных для участия в мероприятиях
-- Используется для организации командной работы и оценки результатов
CREATE TABLE teams (
    id UUID PRIMARY KEY,
    event_id UUID NOT NULL REFERENCES events(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL, -- Название команды
    created_by UUID NOT NULL, -- ID создателя команды
    created_at TIMESTAMPTZ DEFAULT NOW(), -- Время создания команды
    idea_description TEXT, -- Описание идеи/проекта команды
    github_repo VARCHAR(512), -- Ссылка на GitHub репозиторий
    presentation_link VARCHAR(512) -- Ссылка на презентацию
);

COMMENT ON TABLE teams IS 'Таблица команд мероприятий. Хранит информацию о командах, участвующих в хакатонах и других командных событиях';
COMMENT ON COLUMN teams.idea_description IS 'Описание идеи или проекта, который команда планирует реализовать в рамках мероприятия';
COMMENT ON COLUMN teams.github_repo IS 'Ссылка на GitHub репозиторий с кодом проекта команды';
COMMENT ON COLUMN teams.presentation_link IS 'Ссылка на презентацию или демо-версию проекта команды';

-- Участники команд
-- Хранит информацию о составе команд и ролях участников
-- Используется для управления составом команд и распределения задач
CREATE TABLE team_members (
    team_id UUID NOT NULL REFERENCES teams(id) ON DELETE CASCADE,
    user_id UUID NOT NULL, -- ID пользователя
    role VARCHAR(50) NOT NULL DEFAULT 'member', -- Роль в команде
    joined_at TIMESTAMPTZ DEFAULT NOW(), -- Время присоединения к команде
    PRIMARY KEY (team_id, user_id)
);

COMMENT ON TABLE team_members IS 'Таблица состава команд. Хранит информацию об участниках команд и их ролях';
COMMENT ON COLUMN team_members.role IS 'Роль пользователя в команде: leader (лидер), member (участник), designer (дизайнер), developer (разработчик), analyst (аналитик)';

-- Менторы мероприятий
-- Хранит информацию о менторах, прикрепленных к мероприятиям
-- Используется для организации взаимодействия между менторами и командами
CREATE TABLE event_mentors (
    event_id UUID NOT NULL REFERENCES events(id) ON DELETE CASCADE,
    user_id UUID NOT NULL, -- ID ментора
    expertise TEXT, -- Область экспертизы ментора
    availability TEXT, -- График доступности
    PRIMARY KEY (event_id, user_id)
);

COMMENT ON TABLE event_mentors IS 'Таблица менторов мероприятий. Хранит информацию о менторах, назначенных для поддержки участников';
COMMENT ON COLUMN event_mentors.expertise IS 'Области экспертизы ментора, например: "фронтенд разработка", "бизнес-аналитика", "ML-модели"';
COMMENT ON COLUMN event_mentors.availability IS 'График доступности ментора во время мероприятия, например: "12:00-15:00, 17:00-19:00"';

-- Задачи мероприятий
-- Хранит информацию о задачах, выдаваемых командам в рамках мероприятий
-- Используется для структурирования работы и оценки результатов
CREATE TABLE event_tasks (
    id UUID PRIMARY KEY,
    event_id UUID NOT NULL REFERENCES events(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL, -- Название задачи
    description TEXT NOT NULL, -- Описание задачи
    difficulty VARCHAR(50) CHECK (difficulty IN ('beginner', 'intermediate', 'advanced')), -- Сложность задачи
    points INTEGER NOT NULL, -- Количество баллов за выполнение
    deadline TIMESTAMPTZ, -- Дедлайн выполнения
    file_path VARCHAR(512) -- Путь к файлам с дополнительными материалами
);

COMMENT ON TABLE event_tasks IS 'Таблица задач мероприятий. Хранит информацию о задачах, выдаваемых командам в хакатонах и других мероприятиях';
COMMENT ON COLUMN event_tasks.difficulty IS 'Уровень сложности задачи: beginner (начальный), intermediate (средний), advanced (продвинутый)';
COMMENT ON COLUMN event_tasks.points IS 'Количество баллов, которые команда получит за успешное выполнение задачи';