package io.github.dkuechler.longjavaty.healthmetrics.controller.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record WorkoutHeartRateSamplesRequest(
    @NotNull UUID userId,
    @NotNull String workoutId,
    @NotEmpty List<@Valid HeartRateSampleDto> samples
) {
    public record HeartRateSampleDto(
        @NotNull OffsetDateTime timestamp,
        @PositiveOrZero Integer bpm,
        @NotBlank String sourceId
    ) { }
}
