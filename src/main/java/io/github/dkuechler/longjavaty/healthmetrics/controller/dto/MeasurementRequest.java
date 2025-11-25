package io.github.dkuechler.longjavaty.healthmetrics.controller.dto;

import io.github.dkuechler.longjavaty.healthmetrics.model.MeasurementType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.OffsetDateTime;
import java.util.UUID;

public record MeasurementRequest(
    @NotNull UUID userId,
    @NotNull MeasurementType measurementType,
    @NotNull @Positive Double value,
    OffsetDateTime recordedAt,
    @NotBlank String sourceId
) { }
