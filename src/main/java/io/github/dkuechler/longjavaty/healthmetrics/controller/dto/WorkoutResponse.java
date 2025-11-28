package io.github.dkuechler.longjavaty.healthmetrics.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.dkuechler.longjavaty.healthmetrics.model.Workout;

import java.time.OffsetDateTime;
import java.util.UUID;

public record WorkoutResponse(
    Long id,
    UUID userId,
    String workoutType,
    @JsonProperty("workoutId") String externalId,
    OffsetDateTime startTime,
    OffsetDateTime endTime,
    Integer durationSeconds,
    Integer activeDurationSeconds,
    Double caloriesBurned,
    Double distanceMeters,
    Integer avgHeartRate,
    Integer maxHeartRate,
    Integer minHeartRate,
    Boolean routeAvailable,
    String sourceId
) {
    public static WorkoutResponse from(Workout workout) {
        return new WorkoutResponse(
            workout.getId(),
            workout.getUser().getId(),
            workout.getWorkoutType(),
            workout.getExternalId(),
            workout.getStartTime(),
            workout.getEndTime(),
            workout.getDurationSeconds(),
            workout.getActiveDurationSeconds(),
            workout.getCaloriesBurned(),
            workout.getDistanceMeters(),
            workout.getAvgHeartRate(),
            workout.getMaxHeartRate(),
            workout.getMinHeartRate(),
            workout.getRouteAvailable(),
            workout.getSourceId()
        );
    }
}
