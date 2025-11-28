package io.github.dkuechler.longjavaty.healthmetrics.controller.dto;

import io.github.dkuechler.longjavaty.healthmetrics.model.WorkoutHeartRateSample;

import java.time.OffsetDateTime;

public record WorkoutHeartRateSampleResponse(
    Long id,
    OffsetDateTime timestamp,
    Integer bpm,
    String sourceId
) {
    public static WorkoutHeartRateSampleResponse from(WorkoutHeartRateSample sample) {
        return new WorkoutHeartRateSampleResponse(
            sample.getId(),
            sample.getTimestamp(),
            sample.getBpm(),
            sample.getSourceId()
        );
    }
}
