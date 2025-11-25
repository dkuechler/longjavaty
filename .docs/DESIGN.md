# Health Metrics Database Design

## Overview
This database schema is designed for a health metrics tracking application with strong emphasis on data privacy, security, and compliance with healthcare data regulations (like GDPR, HIPAA principles).

## Core Tables

### 1. `app_user`
- **Purpose**: User identity and privacy preferences
- **Privacy Features**:
  - `data_retention_days`: User-controlled data retention period
  - `anonymization_requested_at`: GDPR "right to be forgotten" tracking
  - `last_access`: Identifies inactive users for cleanup
- **Security**: Uses UUID for non-sequential, hard-to-guess IDs

### 2. `measurement`
- **Purpose**: Core health data storage
- **Privacy Features**:
  - `encrypted_notes`: Sensitive notes encrypted at application level
  - `data_quality_score`: Helps identify and filter unreliable data
  - `geo_hash`: Coarse location data (city-level only) when relevant
  - Unique constraint prevents duplicate imports
- **Performance**: Optimized indexes for time-series queries

### 3. `data_source`
- **Purpose**: Track and control data origins
- **Privacy Features**:
  - `data_sharing_consent`: Per-source consent tracking
  - `retention_override_days`: Custom retention per source
  - Active/inactive status control

### 4. `measurement_aggregate`
- **Purpose**: Pre-computed aggregations for privacy and performance
- **Privacy Benefits**:
  - Reduces need to access raw data
  - Provides statistical insights without exposing individual measurements
  - Supports data minimization principles

### 5. `data_export_request`
- **Purpose**: GDPR data portability and deletion requests
- **Features**:
  - Tracks export/deletion request lifecycle
  - Temporary file storage with auto-expiry
  - Selective data export by date range and measurement types

### 6. `audit_log`
- **Purpose**: Security and compliance audit trail
- **Tracks**:
  - Data access patterns
  - Export/deletion activities
  - Manual data entries
  - Sync activities from external sources

## Privacy & Security Features

### Data Minimization
- Only collect necessary health metrics
- Aggregate data to reduce raw data exposure
- Configurable retention periods per user

### Access Control
- All data tied to specific users (no shared data)
- Cascade delete ensures data removal when user is deleted
- Source-level consent tracking

### Audit Trail
- Comprehensive logging of all data access
- IP address and user agent tracking
- Flexible metadata storage for context

### Data Quality
- Quality scoring to identify unreliable data
- Source tracking for data provenance
- Deduplication to prevent data corruption

### Compliance Ready
- GDPR data export functionality
- Right to be forgotten support
- Data retention policies
- Audit trail for regulatory requirements

## Performance Optimizations

### Indexes
- Time-series optimized indexes for recent data
- Composite indexes for common query patterns
- Partial indexes for filtered queries

### Aggregations
- Pre-computed daily/weekly/monthly summaries
- Reduces load on raw measurement data
- Faster dashboard and reporting queries

## Security Considerations (Application Level)

### Encryption
- `encrypted_notes` field should be encrypted at application level
- Consider encryption of sensitive measurement values if required
- Use proper key management (not in application code)

### Authentication (Future)
When you add authentication:
- Use strong password requirements or OAuth
- Implement MFA for health data access
- Session management with proper timeout
- Rate limiting on API endpoints

### Data Transmission
- Always use HTTPS/TLS in production
- Consider additional encryption for highly sensitive data
- Validate all input data for type and range

## Usage Examples

### Common Queries
```sql
-- Get recent measurements for a user
SELECT m.*, ds.source_name 
FROM measurement m
JOIN data_source ds ON m.user_id = ds.user_id 
WHERE m.user_id = ? 
AND m.measurement_type = 'HEART_RATE'
AND m.timestamp > CURRENT_TIMESTAMP - INTERVAL '7 days'
ORDER BY m.timestamp DESC;

-- Get daily averages (privacy-friendly)
SELECT * FROM measurement_aggregate 
WHERE user_id = ? 
AND measurement_type = 'HEART_RATE'
AND aggregation_period = 'DAILY'
AND period_start > CURRENT_DATE - INTERVAL '30 days'
ORDER BY period_start DESC;
```

### Data Export (GDPR)
```sql
-- Create export request
INSERT INTO data_export_request (user_id, request_type, date_range_start, date_range_end)
VALUES (?, 'EXPORT', ?, ?);
```

## Migration from Quiz App
The schema completely replaces the quiz-focused structure with health-centric tables. Key changes:
- Removed `quiz_questions` table
- Added comprehensive health data model
- Implemented privacy and security features
- Added audit and compliance capabilities

## Next Steps
1. Update JPA entities to match new schema
2. Implement data encryption for sensitive fields
3. Add data retention cleanup jobs
4. Implement aggregation calculation jobs
5. Add API endpoints with proper access controls
6. Consider adding more measurement types as needed
7. Implement backup and disaster recovery procedures