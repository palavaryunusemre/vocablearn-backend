-- "İzah" feature: per-question explanation of why the correct option is
-- correct, shown via an "İzah" button instead of the client-side-only
-- "Konu Tekrarı" panel (grammarPatternHint.ts) used by the original
-- tense/modals/passive/gerund set. Nullable because older quiz sets have
-- no server-authored explanation and keep using the old panel.
ALTER TABLE quiz_questions ADD COLUMN explanation TEXT;
