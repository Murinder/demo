-- Persistent storage for earned badges/achievements.
-- Replaces the on-the-fly computation in StudentRatingService.getAchievements().

CREATE TABLE IF NOT EXISTS earned_achievements (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID NOT NULL,
    badge_key       VARCHAR(100) NOT NULL,
    title           VARCHAR(200) NOT NULL,
    description     TEXT,
    score           NUMERIC(10, 2) NOT NULL DEFAULT 0,
    category        VARCHAR(50) NOT NULL,
    earned_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    related_entity_id UUID,
    UNIQUE (user_id, badge_key)
);

CREATE INDEX idx_earned_achievements_user ON earned_achievements (user_id);
