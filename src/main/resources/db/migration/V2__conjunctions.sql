CREATE TABLE conjunction_categories (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    usage_note TEXT,
    order_index INT NOT NULL UNIQUE
);

CREATE TABLE conjunction_entries (
    id BIGSERIAL PRIMARY KEY,
    category_id BIGINT NOT NULL REFERENCES conjunction_categories(id) ON DELETE CASCADE,
    order_index INT NOT NULL,
    english_term VARCHAR(255) NOT NULL,
    turkish_meaning VARCHAR(255) NOT NULL,
    example_sentence TEXT
);

CREATE INDEX idx_conjunction_entries_category_id ON conjunction_entries(category_id);

CREATE TABLE conjunction_exercise_attempts (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    category_id BIGINT NOT NULL REFERENCES conjunction_categories(id) ON DELETE CASCADE,
    exercise_type VARCHAR(30) NOT NULL,
    total_items INT NOT NULL,
    correct_items INT NOT NULL,
    answered_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_conjunction_exercise_attempts_user_id ON conjunction_exercise_attempts(user_id);

CREATE TABLE conjunction_mastery (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    conjunction_entry_id BIGINT NOT NULL REFERENCES conjunction_entries(id) ON DELETE CASCADE,
    correct_count INT NOT NULL DEFAULT 0,
    incorrect_count INT NOT NULL DEFAULT 0,
    last_practiced_at TIMESTAMP,
    UNIQUE (user_id, conjunction_entry_id)
);

CREATE INDEX idx_conjunction_mastery_user_id ON conjunction_mastery(user_id);
