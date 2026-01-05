package io.github.dkuechler.longjavaty.insights.service;

import io.github.dkuechler.longjavaty.healthmetrics.model.Measurement;
import io.github.dkuechler.longjavaty.healthmetrics.model.MeasurementType;
import io.github.dkuechler.longjavaty.healthmetrics.model.Workout;
import io.github.dkuechler.longjavaty.healthmetrics.repository.MeasurementRepository;
import io.github.dkuechler.longjavaty.healthmetrics.repository.WorkoutRepository;
import io.github.dkuechler.longjavaty.insights.config.InsightsProperties;
import io.github.dkuechler.longjavaty.insights.model.HealthDataSnapshot;
import io.github.dkuechler.longjavaty.insights.model.HealthDataSnapshot.Trend;
import io.github.dkuechler.longjavaty.users.model.AppUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HealthDataAggregatorTest {

    @Mock
    private MeasurementRepository measurementRepository;

    @Mock
    private WorkoutRepository workoutRepository;

    private InsightsProperties properties;
    private Clock clock;
    private HealthDataAggregator aggregator;
    private UUID userId;
    private AppUser user;

    @BeforeEach
    void setUp() {
        properties = new InsightsProperties(30, 7);
        clock = Clock.fixed(Instant.parse("2024-06-15T10:00:00Z"), ZoneOffset.UTC);
        aggregator = new HealthDataAggregator(measurementRepository, workoutRepository, properties, clock);
        userId = UUID.randomUUID();
        user = new AppUser("test@example.com");
        user.setId(userId);
    }

    @Test
    void aggregateHealthData_withNoData_shouldReturnEmptySnapshot() {
        when(measurementRepository.findByUserAndMeasurementTypeAndTimestampBetween(
            eq(userId), any(MeasurementType.class), any(), any())).thenReturn(List.of());
        when(workoutRepository.findByUserIdAndStartTimeBetween(eq(userId), any(), any())).thenReturn(List.of());

        HealthDataSnapshot snapshot = aggregator.aggregateHealthData(userId);

        assertThat(snapshot.userId()).isEqualTo(userId);
        assertThat(snapshot.analysisWindowDays()).isEqualTo(30);
        assertThat(snapshot.restingHeartRateLatest()).isNull();
        assertThat(snapshot.vo2MaxLatest()).isNull();
        assertThat(snapshot.totalWorkoutsInPeriod()).isZero();
        assertThat(snapshot.workoutSummaries()).isEmpty();
    }

    @Test
    void aggregateHealthData_withMeasurements_shouldCalculateAveragesAndTrends() {
        List<Measurement> restingHR = createMeasurements(user, MeasurementType.RESTING_HEART_RATE,
            new double[]{70, 68, 66, 64, 62, 60}); // Decreasing trend

        when(measurementRepository.findByUserAndMeasurementTypeAndTimestampBetween(
            eq(userId), eq(MeasurementType.RESTING_HEART_RATE), any(), any())).thenReturn(restingHR);
        when(measurementRepository.findByUserAndMeasurementTypeAndTimestampBetween(
            eq(userId), eq(MeasurementType.VO2_MAX), any(), any())).thenReturn(List.of());
        when(measurementRepository.findByUserAndMeasurementTypeAndTimestampBetween(
            eq(userId), eq(MeasurementType.STEPS), any(), any())).thenReturn(List.of());
        when(workoutRepository.findByUserIdAndStartTimeBetween(eq(userId), any(), any())).thenReturn(List.of());

        HealthDataSnapshot snapshot = aggregator.aggregateHealthData(userId);

        assertThat(snapshot.restingHeartRateLatest()).isEqualTo(60.0);
        assertThat(snapshot.restingHeartRateAvg()).isBetween(64.0, 66.0);
        assertThat(snapshot.restingHeartRateTrend()).isEqualTo(Trend.DECREASING);
    }

    @Test
    void aggregateHealthData_withWorkouts_shouldSummarizeByType() {
        List<Workout> workouts = List.of(
            createWorkout(user, "RUN", 1800, 150),
            createWorkout(user, "RUN", 2400, 155),
            createWorkout(user, "CYCLE", 3600, 140)
        );

        when(measurementRepository.findByUserAndMeasurementTypeAndTimestampBetween(
            eq(userId), any(MeasurementType.class), any(), any())).thenReturn(List.of());
        when(workoutRepository.findByUserIdAndStartTimeBetween(eq(userId), any(), any())).thenReturn(workouts);

        HealthDataSnapshot snapshot = aggregator.aggregateHealthData(userId);

        assertThat(snapshot.totalWorkoutsInPeriod()).isEqualTo(3);
        assertThat(snapshot.workoutSummaries()).hasSize(2);

        var runSummary = snapshot.workoutSummaries().stream()
            .filter(s -> s.workoutType().equals("RUN"))
            .findFirst()
            .orElseThrow();
        assertThat(runSummary.count()).isEqualTo(2);
        assertThat(runSummary.avgDurationSeconds()).isEqualTo(2100.0);
        assertThat(runSummary.avgHeartRate()).isEqualTo(152.5);
    }

    @Test
    void calculateTrend_withInsufficientData_shouldReturnInsufficientData() {
        List<Measurement> twoMeasurements = createMeasurements(user, MeasurementType.RESTING_HEART_RATE,
            new double[]{70, 68});

        when(measurementRepository.findByUserAndMeasurementTypeAndTimestampBetween(
            eq(userId), eq(MeasurementType.RESTING_HEART_RATE), any(), any())).thenReturn(twoMeasurements);
        when(measurementRepository.findByUserAndMeasurementTypeAndTimestampBetween(
            eq(userId), eq(MeasurementType.VO2_MAX), any(), any())).thenReturn(List.of());
        when(measurementRepository.findByUserAndMeasurementTypeAndTimestampBetween(
            eq(userId), eq(MeasurementType.STEPS), any(), any())).thenReturn(List.of());
        when(workoutRepository.findByUserIdAndStartTimeBetween(eq(userId), any(), any())).thenReturn(List.of());

        HealthDataSnapshot snapshot = aggregator.aggregateHealthData(userId);

        assertThat(snapshot.restingHeartRateTrend()).isEqualTo(Trend.INSUFFICIENT_DATA);
    }

    @Test
    void calculateTrend_withStableValues_shouldReturnStable() {
        List<Measurement> stableMeasurements = createMeasurements(user, MeasurementType.RESTING_HEART_RATE,
            new double[]{65, 66, 65, 64, 65, 66}); // Within 3% variation

        when(measurementRepository.findByUserAndMeasurementTypeAndTimestampBetween(
            eq(userId), eq(MeasurementType.RESTING_HEART_RATE), any(), any())).thenReturn(stableMeasurements);
        when(measurementRepository.findByUserAndMeasurementTypeAndTimestampBetween(
            eq(userId), eq(MeasurementType.VO2_MAX), any(), any())).thenReturn(List.of());
        when(measurementRepository.findByUserAndMeasurementTypeAndTimestampBetween(
            eq(userId), eq(MeasurementType.STEPS), any(), any())).thenReturn(List.of());
        when(workoutRepository.findByUserIdAndStartTimeBetween(eq(userId), any(), any())).thenReturn(List.of());

        HealthDataSnapshot snapshot = aggregator.aggregateHealthData(userId);

        assertThat(snapshot.restingHeartRateTrend()).isEqualTo(Trend.STABLE);
    }

    private List<Measurement> createMeasurements(AppUser user, MeasurementType type, double[] values) {
        OffsetDateTime baseTime = OffsetDateTime.now(clock).minusDays(values.length);
        return java.util.stream.IntStream.range(0, values.length)
            .mapToObj(i -> {
                Measurement m = new Measurement(user, type, values[i], baseTime.plusDays(i), "test-source-" + i);
                return m;
            })
            .toList();
    }

    private Workout createWorkout(AppUser user, String type, int durationSeconds, int avgHeartRate) {
        Workout w = new Workout();
        w.setUser(user);
        w.setWorkoutType(type);
        w.setDurationSeconds(durationSeconds);
        w.setAvgHeartRate(avgHeartRate);
        w.setStartTime(OffsetDateTime.now(clock).minusDays(1));
        return w;
    }
}
