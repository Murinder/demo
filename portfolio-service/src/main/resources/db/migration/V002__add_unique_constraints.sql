-- =============================================================
-- V002: Add unique constraints to prevent duplicate entities
-- =============================================================

-- 1. A skill name must be unique per portfolio
DELETE FROM skills a
    USING skills b
    WHERE a.portfolio_id = b.portfolio_id
      AND a.name = b.name
      AND a.created_at > b.created_at;

ALTER TABLE skills
    ADD CONSTRAINT uq_portfolio_skill_name UNIQUE (portfolio_id, name);

-- 2. A user can only leave one review per entity per type
DELETE FROM reviews a
    USING reviews b
    WHERE a.portfolio_id = b.portfolio_id
      AND a.from_user_id = b.from_user_id
      AND a.type = b.type
      AND a.related_entity_id IS NOT DISTINCT FROM b.related_entity_id
      AND a.created_at > b.created_at;

ALTER TABLE reviews
    ADD CONSTRAINT uq_review_per_entity UNIQUE (portfolio_id, from_user_id, type, related_entity_id);
