package io.github.dkuechler.longjavaty.insights.model;

import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record HealthDataSnapshot(
    UUID userId,
    int analysisWindowDays,
    Trend restingHeartRateTrend,
    Double restingHeartRateAvg,
    Double restingHeartRateLatest,
    Trend vo2MaxTrend,
    Double vo2MaxAvg,
    Double vo2MaxLatest,
    Double averageDailySteps,
    List<WorkoutSummary> workoutSummaries,
    int totalWorkoutsInPeriod,
    UserProfile userProfile,
    OffsetDateTime generatedAt
) {

    public enum Trend {
        INCREASING,
        DECREASING,
        STABLE,
        INSUFFICIENT_DATA
    }

    public record WorkoutSummary(
        String workoutType,
        int count,
        double avgDurationSeconds,
        Double avgHeartRate
    ) {}

    public record UserProfile(
        Integer age,
        String sex,
        Double heightCm,
        Double weightKg
    ) {
        public boolean hasCompleteProfile() {
            return age != null && sex != null && !"UNKNOWN".equals(sex)
                && heightCm != null && weightKg != null;
        }
    }
}
