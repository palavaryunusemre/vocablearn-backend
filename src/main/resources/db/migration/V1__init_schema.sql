CREATE TABLE passages (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    level VARCHAR(20) NOT NULL,
    order_index INT NOT NULL UNIQUE
);

CREATE TABLE passage_paragraphs (
    id BIGSERIAL PRIMARY KEY,
    passage_id BIGINT NOT NULL REFERENCES passages(id) ON DELETE CASCADE,
    order_index INT NOT NULL
);

CREATE INDEX idx_passage_paragraphs_passage_id ON passage_paragraphs(passage_id);

CREATE TABLE passage_sentences (
    id BIGSERIAL PRIMARY KEY,
    paragraph_id BIGINT NOT NULL REFERENCES passage_paragraphs(id) ON DELETE CASCADE,
    order_index INT NOT NULL,
    english_text TEXT NOT NULL,
    turkish_text TEXT NOT NULL
);

CREATE INDEX idx_passage_sentences_paragraph_id ON passage_sentences(paragraph_id);

CREATE TABLE vocabulary_words (
    id BIGSERIAL PRIMARY KEY,
    passage_id BIGINT NOT NULL REFERENCES passages(id) ON DELETE CASCADE,
    order_index INT NOT NULL,
    english_term VARCHAR(255) NOT NULL,
    turkish_meaning VARCHAR(255) NOT NULL,
    example_sentence TEXT
);

CREATE INDEX idx_vocabulary_words_passage_id ON vocabulary_words(passage_id);

CREATE TABLE comprehension_questions (
    id BIGSERIAL PRIMARY KEY,
    passage_id BIGINT NOT NULL REFERENCES passages(id) ON DELETE CASCADE,
    order_index INT NOT NULL,
    question_type VARCHAR(30) NOT NULL,
    question_text TEXT NOT NULL,
    correct_option_index INT NOT NULL
);

CREATE INDEX idx_comprehension_questions_passage_id ON comprehension_questions(passage_id);

CREATE TABLE comprehension_question_options (
    question_id BIGINT NOT NULL REFERENCES comprehension_questions(id) ON DELETE CASCADE,
    option_index INT NOT NULL,
    option_text TEXT,
    PRIMARY KEY (question_id, option_index)
);

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    display_name VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE exercise_attempts (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    passage_id BIGINT NOT NULL REFERENCES passages(id) ON DELETE CASCADE,
    exercise_type VARCHAR(30) NOT NULL,
    total_items INT NOT NULL,
    correct_items INT NOT NULL,
    answered_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_exercise_attempts_user_id ON exercise_attempts(user_id);

CREATE TABLE question_attempts (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    question_id BIGINT NOT NULL REFERENCES comprehension_questions(id) ON DELETE CASCADE,
    selected_option_index INT NOT NULL,
    correct BOOLEAN NOT NULL,
    answered_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_question_attempts_user_id ON question_attempts(user_id);

CREATE TABLE word_mastery (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    vocabulary_word_id BIGINT NOT NULL REFERENCES vocabulary_words(id) ON DELETE CASCADE,
    correct_count INT NOT NULL DEFAULT 0,
    incorrect_count INT NOT NULL DEFAULT 0,
    last_practiced_at TIMESTAMP,
    UNIQUE (user_id, vocabulary_word_id)
);

CREATE INDEX idx_word_mastery_user_id ON word_mastery(user_id);
