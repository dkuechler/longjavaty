package io.github.dkuechler.longjavaty.healthmetrics.controller.dto;

import io.github.dkuechler.longjavaty.healthmetrics.model.Measurement;
import io.github.dkuechler.longjavaty.healthmetrics.model.MeasurementType;

import java.time.OffsetDateTime;
import java.util.UUID;

public record MeasurementResponse(
    Long id,
    UUID userId,
    MeasurementType measurementType,
    double value,
    String unit,
    OffsetDateTime recordedAt,
    String sourceId
) {
    public static MeasurementResponse from(Measurement measurement) {
        return new MeasurementResponse(
            measurement.getId(),
            measurement.getUser().getId(),
            measurement.getMeasurementType(),
            measurement.getValue(),
            measurement.getUnit(),
            measurement.getTimestamp(),
            measurement.getSourceId()
        );
    }
}
