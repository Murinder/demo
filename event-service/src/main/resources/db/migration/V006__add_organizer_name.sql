-- Add organizer_name column to events
ALTER TABLE events ADD COLUMN IF NOT EXISTS organizer_name VARCHAR(255);

-- Set organizer names for demo events
UPDATE events SET organizer_name = 'Кафедра программной инженерии'
WHERE id = '80000000-0000-0000-0000-000000000001';

UPDATE events SET organizer_name = 'Научный совет факультета ИТ'
WHERE id = '80000000-0000-0000-0000-000000000002';

UPDATE events SET organizer_name = 'Центр развития предпринимательства'
WHERE id = '80000000-0000-0000-0000-000000000003';
