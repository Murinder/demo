CREATE INDEX idx_rating_history_calculated_at
    ON rating_history(calculated_at);
CREATE INDEX idx_rating_history_user_calculated
    ON rating_history(user_id, calculated_at);
