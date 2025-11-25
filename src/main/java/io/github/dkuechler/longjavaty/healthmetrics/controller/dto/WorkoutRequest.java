package io.github.dkuechler.longjavaty.healthmetrics.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.OffsetDateTime;
import java.util.UUID;

public record WorkoutRequest(
    @NotNull UUID userId,
    @NotBlank String workoutType,
    OffsetDateTime startTime,
    @PositiveOrZero Integer durationSeconds,
    @PositiveOrZero Double caloriesBurned,
    @PositiveOrZero Double distanceMeters,
    @NotBlank String sourceId
) { }
