package io.github.dkuechler.longjavaty.insights.service;

import io.github.dkuechler.longjavaty.healthmetrics.model.Measurement;
import io.github.dkuechler.longjavaty.healthmetrics.model.MeasurementType;
import io.github.dkuechler.longjavaty.healthmetrics.model.Workout;
import io.github.dkuechler.longjavaty.healthmetrics.repository.MeasurementRepository;
import io.github.dkuechler.longjavaty.healthmetrics.repository.WorkoutRepository;
import io.github.dkuechler.longjavaty.insights.config.AiConfig;
import io.github.dkuechler.longjavaty.insights.model.HealthDataSnapshot;
import io.github.dkuechler.longjavaty.insights.model.HealthDataSnapshot.Trend;
import io.github.dkuechler.longjavaty.insights.model.HealthDataSnapshot.UserProfile;
import io.github.dkuechler.longjavaty.insights.model.HealthDataSnapshot.WorkoutSummary;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@ConditionalOnProperty(name = "app.insights.enabled", havingValue = "true", matchIfMissing = true)
public class HealthDataAggregator {

    private final MeasurementRepository measurementRepository;
    private final WorkoutRepository workoutRepository;
    private final AiConfig aiConfig;

    public HealthDataAggregator(MeasurementRepository measurementRepository,
                                WorkoutRepository workoutRepository,
                                AiConfig aiConfig) {
        this.measurementRepository = measurementRepository;
        this.workoutRepository = workoutRepository;
        this.aiConfig = aiConfig;
    }

    @Transactional(readOnly = true)
    public HealthDataSnapshot aggregateHealthData(UUID userId) {
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime windowStart = now.minusDays(aiConfig.getAnalysisWindowDays());

        List<Measurement> restingHR = measurementRepository
            .findByUserAndMeasurementTypeAndTimestampBetween(
                userId, MeasurementType.RESTING_HEART_RATE, windowStart, now);

        List<Measurement> vo2Max = measurementRepository
            .findByUserAndMeasurementTypeAndTimestampBetween(
                userId, MeasurementType.VO2_MAX, windowStart, now);

        List<Measurement> steps = measurementRepository
            .findByUserAndMeasurementTypeAndTimestampBetween(
                userId, MeasurementType.STEPS, windowStart, now);

        List<Workout> workouts = workoutRepository
            .findByUserIdAndStartTimeBetween(userId, windowStart, now);

        log.debug("Aggregated data for user {}: {} RHR, {} VO2max, {} steps, {} workouts",
            userId, restingHR.size(), vo2Max.size(), steps.size(), workouts.size());

        return HealthDataSnapshot.builder()
            .userId(userId)
            .analysisWindowDays(aiConfig.getAnalysisWindowDays())
            .restingHeartRateTrend(calculateTrend(restingHR))
            .restingHeartRateAvg(calculateAverage(restingHR))
            .restingHeartRateLatest(getLatestValue(restingHR))
            .vo2MaxTrend(calculateTrend(vo2Max))
            .vo2MaxAvg(calculateAverage(vo2Max))
            .vo2MaxLatest(getLatestValue(vo2Max))
            .averageDailySteps(calculateDailyStepsAverage(steps))
            .workoutSummaries(summarizeWorkouts(workouts))
            .totalWorkoutsInPeriod(workouts.size())
            .userProfile(buildUserProfile())
            .generatedAt(now)
            .build();
    }

    private Trend calculateTrend(List<Measurement> measurements) {
        if (measurements.size() < 3) {
            return Trend.INSUFFICIENT_DATA;
        }

        List<Measurement> sorted = measurements.stream()
            .sorted(Comparator.comparing(Measurement::getTimestamp))
            .toList();

        int n = sorted.size();
        int midpoint = n / 2;

        double firstHalfAvg = sorted.subList(0, midpoint).stream()
            .mapToDouble(Measurement::getValue)
            .average()
            .orElse(0);

        double secondHalfAvg = sorted.subList(midpoint, n).stream()
            .mapToDouble(Measurement::getValue)
            .average()
            .orElse(0);

        if (firstHalfAvg == 0) {
            return Trend.INSUFFICIENT_DATA;
        }

        double percentChange = ((secondHalfAvg - firstHalfAvg) / firstHalfAvg) * 100;

        if (Math.abs(percentChange) < 3) {
            return Trend.STABLE;
        }
        return percentChange > 0 ? Trend.INCREASING : Trend.DECREASING;
    }

    private Double calculateAverage(List<Measurement> measurements) {
        if (measurements.isEmpty()) {
            return null;
        }
        return measurements.stream()
            .mapToDouble(Measurement::getValue)
            .average()
            .orElse(0);
    }

    private Double getLatestValue(List<Measurement> measurements) {
        if (measurements.isEmpty()) {
            return null;
        }
        return measurements.stream()
            .max(Comparator.comparing(Measurement::getTimestamp))
            .map(Measurement::getValue)
            .orElse(null);
    }

    private Double calculateDailyStepsAverage(List<Measurement> steps) {
        if (steps.isEmpty()) {
            return null;
        }
        Map<String, Double> dailyTotals = steps.stream()
            .collect(Collectors.groupingBy(
                m -> m.getTimestamp().toLocalDate().toString(),
                Collectors.summingDouble(Measurement::getValue)
            ));
        return dailyTotals.values().stream()
            .mapToDouble(Double::doubleValue)
            .average()
            .orElse(0);
    }

    private List<WorkoutSummary> summarizeWorkouts(List<Workout> workouts) {
        return workouts.stream()
            .collect(Collectors.groupingBy(Workout::getWorkoutType))
            .entrySet().stream()
            .map(entry -> new WorkoutSummary(
                entry.getKey(),
                entry.getValue().size(),
                entry.getValue().stream()
                    .mapToInt(w -> w.getDurationSeconds() != null ? w.getDurationSeconds() : 0)
                    .average()
                    .orElse(0),
                entry.getValue().stream()
                    .filter(w -> w.getAvgHeartRate() != null)
                    .mapToInt(Workout::getAvgHeartRate)
                    .average()
                    .orElse(0)
            ))
            .toList();
    }

    private UserProfile buildUserProfile() {
        // Templated values - extension point for when real data is added to AppUser
        return new UserProfile(
            null,
            "UNKNOWN",
            null,
            null
        );
    }
}
