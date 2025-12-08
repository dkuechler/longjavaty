DELETE FROM workout_heart_rate_sample;
DELETE FROM workout;
DELETE FROM measurement;
DELETE FROM app_user;

-- Use fixed timestamps that work on both H2 and PostgreSQL
INSERT INTO app_user (id, email, created_at) VALUES
('550e8400-e29b-41d4-a716-446655440000', 'john.doe@example.com', '2024-10-06 10:00:00'),
('550e8400-e29b-41d4-a716-446655440001', 'jane.smith@example.com', '2024-10-21 10:00:00'),
('f0f434e8-1a6c-4ea9-9c45-2a23780dc932', 'user@example.com', '2024-11-01 09:00:00');

INSERT INTO measurement (user_id, measurement_type, value_numeric, recorded_at, external_source_id) VALUES
('550e8400-e29b-41d4-a716-446655440000', 'HEART_RATE', 72, '2024-11-04 10:00:00', 'apple_hr_1'),
('550e8400-e29b-41d4-a716-446655440000', 'HEART_RATE', 68, '2024-11-03 10:00:00', 'apple_hr_2'),
('550e8400-e29b-41d4-a716-446655440000', 'STEPS', 8543, '2024-11-04 10:00:00', 'apple_steps_1'),
('550e8400-e29b-41d4-a716-446655440000', 'STEPS', 12450, '2024-11-03 10:00:00', 'apple_steps_2'),
('550e8400-e29b-41d4-a716-446655440000', 'VO2_MAX', 43.2, '2024-11-02 08:00:00', 'apple_vo2_1'),
('550e8400-e29b-41d4-a716-446655440001', 'HEART_RATE', 65, '2024-11-05 09:30:00', 'fitbit_hr_1'),
('550e8400-e29b-41d4-a716-446655440001', 'STEPS', 15234, '2024-11-05 09:30:00', 'fitbit_steps_1'),
('550e8400-e29b-41d4-a716-446655440001', 'VO2_MAX', 38.5, '2024-11-04 07:45:00', 'fitbit_vo2_1'),
-- Test user measurements (30 days of data)
('f0f434e8-1a6c-4ea9-9c45-2a23780dc932', 'HEART_RATE', 75, '2024-11-01 08:00:00', 'test_hr_1'),
('f0f434e8-1a6c-4ea9-9c45-2a23780dc932', 'HEART_RATE', 73, '2024-11-02 08:00:00', 'test_hr_2'),
('f0f434e8-1a6c-4ea9-9c45-2a23780dc932', 'HEART_RATE', 76, '2024-11-03 08:00:00', 'test_hr_3'),
('f0f434e8-1a6c-4ea9-9c45-2a23780dc932', 'HEART_RATE', 74, '2024-11-04 08:00:00', 'test_hr_4'),
('f0f434e8-1a6c-4ea9-9c45-2a23780dc932', 'HEART_RATE', 72, '2024-11-05 08:00:00', 'test_hr_5'),
('f0f434e8-1a6c-4ea9-9c45-2a23780dc932', 'HEART_RATE', 77, '2024-11-06 08:00:00', 'test_hr_6'),
('f0f434e8-1a6c-4ea9-9c45-2a23780dc932', 'HEART_RATE', 71, '2024-11-07 08:00:00', 'test_hr_7'),
('f0f434e8-1a6c-4ea9-9c45-2a23780dc932', 'RESTING_HEART_RATE', 58, '2024-11-01 06:00:00', 'test_rhr_1'),
('f0f434e8-1a6c-4ea9-9c45-2a23780dc932', 'RESTING_HEART_RATE', 60, '2024-11-03 06:00:00', 'test_rhr_2'),
('f0f434e8-1a6c-4ea9-9c45-2a23780dc932', 'RESTING_HEART_RATE', 57, '2024-11-05 06:00:00', 'test_rhr_3'),
('f0f434e8-1a6c-4ea9-9c45-2a23780dc932', 'RESTING_HEART_RATE', 59, '2024-11-07 06:00:00', 'test_rhr_4'),
('f0f434e8-1a6c-4ea9-9c45-2a23780dc932', 'STEPS', 9845, '2024-11-01 23:59:00', 'test_steps_1'),
('f0f434e8-1a6c-4ea9-9c45-2a23780dc932', 'STEPS', 12340, '2024-11-02 23:59:00', 'test_steps_2'),
('f0f434e8-1a6c-4ea9-9c45-2a23780dc932', 'STEPS', 8120, '2024-11-03 23:59:00', 'test_steps_3'),
('f0f434e8-1a6c-4ea9-9c45-2a23780dc932', 'STEPS', 15670, '2024-11-04 23:59:00', 'test_steps_4'),
('f0f434e8-1a6c-4ea9-9c45-2a23780dc932', 'STEPS', 11250, '2024-11-05 23:59:00', 'test_steps_5'),
('f0f434e8-1a6c-4ea9-9c45-2a23780dc932', 'STEPS', 7890, '2024-11-06 23:59:00', 'test_steps_6'),
('f0f434e8-1a6c-4ea9-9c45-2a23780dc932', 'STEPS', 13450, '2024-11-07 23:59:00', 'test_steps_7'),
('f0f434e8-1a6c-4ea9-9c45-2a23780dc932', 'VO2_MAX', 42.5, '2024-11-01 07:30:00', 'test_vo2_1'),
('f0f434e8-1a6c-4ea9-9c45-2a23780dc932', 'VO2_MAX', 43.1, '2024-11-03 07:30:00', 'test_vo2_2'),
('f0f434e8-1a6c-4ea9-9c45-2a23780dc932', 'VO2_MAX', 42.8, '2024-11-05 07:30:00', 'test_vo2_3'),
('f0f434e8-1a6c-4ea9-9c45-2a23780dc932', 'VO2_MAX', 43.4, '2024-11-07 07:30:00', 'test_vo2_4');

INSERT INTO workout (id, user_id, workout_type, external_id, start_time, end_time, duration_seconds, active_duration_seconds, calories_burned, distance_meters, avg_heart_rate, max_heart_rate, min_heart_rate, route_available, source_id) VALUES
('1', '550e8400-e29b-41d4-a716-446655440000', 'RUN', 'apple_run_1', '2024-11-04 07:00:00', '2024-11-04 08:00:00', 3600, 3500, 650, 10000, 150, 170, 120, TRUE, 'com.apple.Health'),
('2', '550e8400-e29b-41d4-a716-446655440001', 'CYCLE', 'fitbit_cycle_1', '2024-11-05 06:30:00', '2024-11-05 07:15:00', 2700, 2600, 500, 15000, 140, 160, 110, FALSE, 'com.fitbit.app'),
('3', 'f0f434e8-1a6c-4ea9-9c45-2a23780dc932', 'RUN', 'test_run_1', '2024-11-01 07:00:00', '2024-11-01 07:45:00', 2700, 2650, 450, 8000, 145, 165, 125, TRUE, 'test.device'),
('4', 'f0f434e8-1a6c-4ea9-9c45-2a23780dc932', 'CYCLE', 'test_cycle_1', '2024-11-03 06:30:00', '2024-11-03 07:30:00', 3600, 3550, 550, 20000, 135, 155, 115, FALSE, 'test.device'),
('5', 'f0f434e8-1a6c-4ea9-9c45-2a23780dc932', 'RUN', 'test_run_2', '2024-11-05 07:15:00', '2024-11-05 08:00:00', 2700, 2600, 470, 8500, 150, 170, 130, TRUE, 'test.device'),
('6', 'f0f434e8-1a6c-4ea9-9c45-2a23780dc932', 'WALK', 'test_walk_1', '2024-11-06 18:00:00', '2024-11-06 19:00:00', 3600, 3550, 280, 6000, 105, 125, 90, FALSE, 'test.device');

INSERT INTO workout_heart_rate_sample (workout_id, sample_time, bpm, source_id) VALUES
(1, '2024-11-04 07:00:15', 120, 'com.apple.Health'),
(1, '2024-11-04 07:30:15', 150, 'com.apple.Health'),
(1, '2024-11-04 07:59:45', 165, 'com.apple.Health'),
(2, '2024-11-05 06:30:30', 115, 'com.fitbit.app'),
(2, '2024-11-05 06:50:30', 145, 'com.fitbit.app'),
(2, '2024-11-05 07:10:30', 155, 'com.fitbit.app'),
(3, '2024-11-01 07:00:30', 125, 'test.device'),
(3, '2024-11-01 07:15:30', 145, 'test.device'),
(3, '2024-11-01 07:30:30', 155, 'test.device'),
(3, '2024-11-01 07:44:30', 165, 'test.device'),
(4, '2024-11-03 06:30:45', 115, 'test.device'),
(4, '2024-11-03 07:00:45', 135, 'test.device'),
(4, '2024-11-03 07:29:45', 150, 'test.device'),
(5, '2024-11-05 07:15:20', 130, 'test.device'),
(5, '2024-11-05 07:35:20', 150, 'test.device'),
(5, '2024-11-05 07:59:20', 168, 'test.device'),
(6, '2024-11-06 18:00:00', 90, 'test.device'),
(6, '2024-11-06 18:30:00', 105, 'test.device'),
(6, '2024-11-06 19:00:00', 110, 'test.device');
