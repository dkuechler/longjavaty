DELETE FROM quiz_questions;
INSERT INTO quiz_questions (question, options, correct_answer_index, category) VALUES
('What is the capital of France?', '["London", "New York", "Paris", "Madrid"]', 2, 'Geography'),
('What is 2 + 2?', '["3", "4", "5", "6"]', 1, 'Math'),
('What is the largest planet in our solar system?', '["Earth", "Jupiter", "Saturn", "Mars"]', 1, 'Science');