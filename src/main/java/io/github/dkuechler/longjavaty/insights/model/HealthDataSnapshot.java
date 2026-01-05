package io.github.dkuechler.longjavaty.insights.model;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

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
        double avgHeartRate
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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID userId;
        private int analysisWindowDays;
        private Trend restingHeartRateTrend;
        private Double restingHeartRateAvg;
        private Double restingHeartRateLatest;
        private Trend vo2MaxTrend;
        private Double vo2MaxAvg;
        private Double vo2MaxLatest;
        private Double averageDailySteps;
        private List<WorkoutSummary> workoutSummaries = List.of();
        private int totalWorkoutsInPeriod;
        private UserProfile userProfile;
        private OffsetDateTime generatedAt;

        public Builder userId(UUID userId) {
            this.userId = userId;
            return this;
        }

        public Builder analysisWindowDays(int analysisWindowDays) {
            this.analysisWindowDays = analysisWindowDays;
            return this;
        }

        public Builder restingHeartRateTrend(Trend trend) {
            this.restingHeartRateTrend = trend;
            return this;
        }

        public Builder restingHeartRateAvg(Double avg) {
            this.restingHeartRateAvg = avg;
            return this;
        }

        public Builder restingHeartRateLatest(Double latest) {
            this.restingHeartRateLatest = latest;
            return this;
        }

        public Builder vo2MaxTrend(Trend trend) {
            this.vo2MaxTrend = trend;
            return this;
        }

        public Builder vo2MaxAvg(Double avg) {
            this.vo2MaxAvg = avg;
            return this;
        }

        public Builder vo2MaxLatest(Double latest) {
            this.vo2MaxLatest = latest;
            return this;
        }

        public Builder averageDailySteps(Double steps) {
            this.averageDailySteps = steps;
            return this;
        }

        public Builder workoutSummaries(List<WorkoutSummary> summaries) {
            this.workoutSummaries = summaries;
            return this;
        }

        public Builder totalWorkoutsInPeriod(int total) {
            this.totalWorkoutsInPeriod = total;
            return this;
        }

        public Builder userProfile(UserProfile profile) {
            this.userProfile = profile;
            return this;
        }

        public Builder generatedAt(OffsetDateTime time) {
            this.generatedAt = time;
            return this;
        }

        public HealthDataSnapshot build() {
            return new HealthDataSnapshot(
                userId,
                analysisWindowDays,
                restingHeartRateTrend,
                restingHeartRateAvg,
                restingHeartRateLatest,
                vo2MaxTrend,
                vo2MaxAvg,
                vo2MaxLatest,
                averageDailySteps,
                workoutSummaries,
                totalWorkoutsInPeriod,
                userProfile,
                generatedAt
            );
        }
    }
}
