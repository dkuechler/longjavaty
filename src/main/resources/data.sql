DELETE FROM measurement;
DELETE FROM app_user;

-- Use fixed timestamps that work on both H2 and PostgreSQL
INSERT INTO app_user (id, email, created_at) VALUES
('550e8400-e29b-41d4-a716-446655440000', 'john.doe@example.com', '2024-10-06 10:00:00'),
('550e8400-e29b-41d4-a716-446655440001', 'jane.smith@example.com', '2024-10-21 10:00:00');

INSERT INTO measurement (user_id, measurement_type, value_numeric, recorded_at, external_source_id) VALUES
('550e8400-e29b-41d4-a716-446655440000', 'HEART_RATE', 72, '2024-11-04 10:00:00', 'apple_hr_1'),
('550e8400-e29b-41d4-a716-446655440000', 'HEART_RATE', 68, '2024-11-03 10:00:00', 'apple_hr_2'),
('550e8400-e29b-41d4-a716-446655440000', 'STEPS', 8543, '2024-11-04 10:00:00', 'apple_steps_1'),
('550e8400-e29b-41d4-a716-446655440000', 'STEPS', 12450, '2024-11-03 10:00:00', 'apple_steps_2'),
('550e8400-e29b-41d4-a716-446655440001', 'HEART_RATE', 65, '2024-11-05 09:30:00', 'fitbit_hr_1'),
('550e8400-e29b-41d4-a716-446655440001', 'STEPS', 15234, '2024-11-05 09:30:00', 'fitbit_steps_1');