CREATE TABLE quiz_sets (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    order_index INT NOT NULL UNIQUE
);

CREATE TABLE quiz_questions (
    id BIGSERIAL PRIMARY KEY,
    quiz_set_id BIGINT NOT NULL REFERENCES quiz_sets(id) ON DELETE CASCADE,
    order_index INT NOT NULL,
    exam_label VARCHAR(100),
    prompt_text TEXT NOT NULL,
    turkish_translation TEXT,
    option_a TEXT NOT NULL,
    option_b TEXT NOT NULL,
    option_c TEXT NOT NULL,
    option_d TEXT NOT NULL,
    option_e TEXT NOT NULL,
    correct_option VARCHAR(1) NOT NULL
);

CREATE INDEX idx_quiz_questions_quiz_set_id ON quiz_questions(quiz_set_id);
