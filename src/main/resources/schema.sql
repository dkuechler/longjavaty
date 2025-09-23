CREATE TABLE quiz_questions (
    id BIGSERIAL PRIMARY KEY,
    question TEXT NOT NULL,
    options TEXT NOT NULL,
    correct_answer_index INTEGER NOT NULL,
    category VARCHAR(100) NOT NULL
);

INSERT INTO quiz_questions (question, options, correct_answer_index, category) VALUES
('What is the capital of France?', '["London", "New York", "Paris", "Madrid"]', 2, 'Geography'),
('What is 2 + 2?', '["3", "4", "5", "6"]', 1, 'Math'),
('What is the largest planet in our solar system?', '["Earth", "Jupiter", "Saturn", "Mars"]', 1, 'Science');