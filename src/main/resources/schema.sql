CREATE TABLE IF NOT EXISTS quiz_questions (
    id BIGSERIAL PRIMARY KEY,
    question TEXT NOT NULL,
    options TEXT NOT NULL,
    correct_answer_index INTEGER NOT NULL,
    category VARCHAR(100) NOT NULL
);