package io.github.dkuechler.longjavaty.healthmetrics.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.OffsetDateTime;
import java.util.UUID;

public record WorkoutRequest(
    @NotNull UUID userId,
    @NotBlank String workoutType,
    @JsonProperty("workoutId") @NotBlank String externalId,
    OffsetDateTime startTime,
    OffsetDateTime endTime,
    @PositiveOrZero Integer durationSeconds,
    @PositiveOrZero Integer activeDurationSeconds,
    @PositiveOrZero Double caloriesBurned,
    @PositiveOrZero Double distanceMeters,
    @PositiveOrZero Integer avgHeartRate,
    @PositiveOrZero Integer maxHeartRate,
    @PositiveOrZero Integer minHeartRate,
    @NotNull Boolean routeAvailable,
    @NotBlank String sourceId
) { }
