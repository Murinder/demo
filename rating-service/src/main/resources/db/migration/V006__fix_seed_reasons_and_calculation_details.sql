-- ============================================================
-- FIX: Update seed rating_history reasons to use machine keys
-- that match RatingConstants.REASON_TO_CATEGORY mapping.
-- Also update student_ratings.calculation_details to use the
-- nested format produced by rebuildCalculationDetails().
-- ============================================================

-- Fix reason strings in rating_history for the demo student
UPDATE rating_history
SET reason = 'task_completed'
WHERE id = '92000000-0000-0000-0000-000000000001'
  AND reason = 'Средний балл за семестр (4.2/5.0)';

UPDATE rating_history
SET reason = 'event_participation'
WHERE id = '92000000-0000-0000-0000-000000000002'
  AND reason = 'Участие в хакатоне AI Solutions';

UPDATE rating_history
SET reason = 'project_completed'
WHERE id = '92000000-0000-0000-0000-000000000003'
  AND reason = 'Завершение проекта "Анализ успеваемости"';

-- Rebuild calculation_details for the demo student to match
-- the nested format that RatingCalculationService produces.
-- Categories: task_completed(35) -> academic, event_participation(25) -> activity,
--             project_completed(18.5) -> academic
-- academic = 35 + 18.5 = 53.5, activity = 25, communication = 0
UPDATE student_ratings
SET calculation_details = '{
  "academic": {"score": 53.5},
  "activity": {"score": 25},
  "communication": {"score": 0},
  "totalRaw": 78.5,
  "totalNormalized": 39.25
}'::jsonb
WHERE user_id = '00000000-0000-0000-0000-000000000001';
