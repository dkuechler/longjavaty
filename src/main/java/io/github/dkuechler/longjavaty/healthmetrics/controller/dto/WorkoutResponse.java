package io.github.dkuechler.longjavaty.healthmetrics.controller.dto;

import io.github.dkuechler.longjavaty.healthmetrics.model.Workout;

import java.time.OffsetDateTime;
import java.util.UUID;

public record WorkoutResponse(
    Long id,
    UUID userId,
    String workoutType,
    OffsetDateTime startTime,
    Integer durationSeconds,
    Double caloriesBurned,
    Double distanceMeters,
    String sourceId
) {
    public static WorkoutResponse from(Workout workout) {
        return new WorkoutResponse(
            workout.getId(),
            workout.getUser().getId(),
            workout.getWorkoutType(),
            workout.getStartTime(),
            workout.getDurationSeconds(),
            workout.getCaloriesBurned(),
            workout.getDistanceMeters(),
            workout.getSourceId()
        );
    }
}
