CREATE TABLE modal_categories (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    usage_note TEXT,
    order_index INT NOT NULL UNIQUE
);

CREATE TABLE modal_entries (
    id BIGSERIAL PRIMARY KEY,
    category_id BIGINT NOT NULL REFERENCES modal_categories(id) ON DELETE CASCADE,
    order_index INT NOT NULL,
    english_term VARCHAR(255) NOT NULL,
    turkish_meaning VARCHAR(255) NOT NULL,
    example_sentence TEXT
);

CREATE INDEX idx_modal_entries_category_id ON modal_entries(category_id);
