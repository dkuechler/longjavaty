CREATE TABLE IF NOT EXISTS app_user (
    id UUID PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS measurement (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    measurement_type VARCHAR(50) NOT NULL,
    value_numeric DOUBLE PRECISION NOT NULL,
    recorded_at TIMESTAMP NOT NULL,
    external_source_id VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT unique_user_type_source UNIQUE (user_id, measurement_type, external_source_id),
    CONSTRAINT positive_measurement_value CHECK (value_numeric >= 0)
);

CREATE INDEX measurement_user_type_time ON measurement(user_id, measurement_type, recorded_at DESC);