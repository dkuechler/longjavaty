package io.github.dkuechler.longjavaty.users.controller.dto;

import io.github.dkuechler.longjavaty.healthmetrics.controller.dto.MeasurementResponse;
import io.github.dkuechler.longjavaty.healthmetrics.controller.dto.WorkoutResponse;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * GDPR Article 20 - Right to Data Portability
 * Contains all user data in a structured, machine-readable format.
 */
public record UserDataExport(
        UserInfo user,
        List<WorkoutResponse> workouts,
        List<MeasurementResponse> measurements,
        OffsetDateTime exportedAt
) {
    public record UserInfo(
            UUID id,
            String email,
            OffsetDateTime createdAt
    ) {}
}
