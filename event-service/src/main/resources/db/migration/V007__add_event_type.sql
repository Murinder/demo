-- Добавляет колонку event_type для хранения типа мероприятия вместо вывода из заголовка
ALTER TABLE events ADD COLUMN event_type VARCHAR(50);

COMMENT ON COLUMN events.event_type IS 'Тип мероприятия: Консультация, Лекция, Семинар, Защита, Хакатон, Конференция и т.д.';
