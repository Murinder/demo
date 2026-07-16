CREATE TABLE user_awards (
    id       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id  UUID NOT NULL REFERENCES users(id),
    title    VARCHAR(500) NOT NULL,
    year     VARCHAR(10) NOT NULL
);

CREATE INDEX idx_user_awards_user_id ON user_awards(user_id);

-- Seed demo awards for the department head
INSERT INTO user_awards (id, user_id, title, year) VALUES
    ('a0000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000003',
     'Заслуженный деятель науки РФ', '2020'),
    ('a0000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000003',
     'Премия Правительства РФ в области образования', '2018'),
    ('a0000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000003',
     'Лучший преподаватель университета', '2015');
