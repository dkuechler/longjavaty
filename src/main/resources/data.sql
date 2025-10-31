DELETE FROM measurement;
DELETE FROM app_user;

INSERT INTO app_user (id, email, created_at) VALUES
('550e8400-e29b-41d4-a716-446655440000', 'john.doe@example.com', CURRENT_TIMESTAMP - INTERVAL '30 days'),
('550e8400-e29b-41d4-a716-446655440001', 'jane.smith@example.com', CURRENT_TIMESTAMP - INTERVAL '15 days');

INSERT INTO measurement (user_id, measurement_type, value_numeric, recorded_at, external_source_id) VALUES
('550e8400-e29b-41d4-a716-446655440000', 'HEART_RATE', 72, CURRENT_TIMESTAMP - INTERVAL '1 day', 'apple_hr_1'),
('550e8400-e29b-41d4-a716-446655440000', 'HEART_RATE', 68, CURRENT_TIMESTAMP - INTERVAL '2 days', 'apple_hr_2'),
('550e8400-e29b-41d4-a716-446655440000', 'STEPS', 8543, CURRENT_TIMESTAMP - INTERVAL '1 day', 'apple_steps_1'),
('550e8400-e29b-41d4-a716-446655440000', 'STEPS', 12450, CURRENT_TIMESTAMP - INTERVAL '2 days', 'apple_steps_2'),
('550e8400-e29b-41d4-a716-446655440001', 'HEART_RATE', 65, CURRENT_TIMESTAMP - INTERVAL '30 minutes', 'fitbit_hr_1'),
('550e8400-e29b-41d4-a716-446655440001', 'STEPS', 15234, CURRENT_TIMESTAMP - INTERVAL '30 minutes', 'fitbit_steps_1');