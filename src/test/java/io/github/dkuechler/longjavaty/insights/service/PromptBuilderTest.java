package io.github.dkuechler.longjavaty.insights.service;

import io.github.dkuechler.longjavaty.insights.model.HealthDataSnapshot;
import io.github.dkuechler.longjavaty.insights.model.HealthDataSnapshot.Trend;
import io.github.dkuechler.longjavaty.insights.model.HealthDataSnapshot.UserProfile;
import io.github.dkuechler.longjavaty.insights.model.HealthDataSnapshot.WorkoutSummary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PromptBuilderTest {

    private PromptBuilder promptBuilder;

    @BeforeEach
    void setUp() {
        promptBuilder = new PromptBuilder();
    }

    @Test
    void buildAnalysisPrompt_withCompleteData_shouldIncludeAllSections() {
        HealthDataSnapshot snapshot = HealthDataSnapshot.builder()
            .userId(UUID.randomUUID())
            .analysisWindowDays(30)
            .restingHeartRateTrend(Trend.DECREASING)
            .restingHeartRateAvg(62.5)
            .restingHeartRateLatest(60.0)
            .vo2MaxTrend(Trend.INCREASING)
            .vo2MaxAvg(45.0)
            .vo2MaxLatest(47.0)
            .averageDailySteps(8500.0)
            .workoutSummaries(List.of(
                new WorkoutSummary("RUN", 5, 1800.0, 155.0),
                new WorkoutSummary("CYCLE", 3, 2700.0, 140.0)
            ))
            .totalWorkoutsInPeriod(8)
            .userProfile(new UserProfile(35, "MALE", 180.0, 75.0))
            .generatedAt(OffsetDateTime.now())
            .build();

        String prompt = promptBuilder.buildAnalysisPrompt(snapshot);

        assertThat(prompt).contains("User Profile");
        assertThat(prompt).contains("Age: 35 years");
        assertThat(prompt).contains("Sex: MALE");
        assertThat(prompt).contains("Height: 180.0 cm");
        assertThat(prompt).contains("Weight: 75.0 kg");
        assertThat(prompt).contains("Health Metrics (Last 30 Days)");
        assertThat(prompt).contains("Resting Heart Rate");
        assertThat(prompt).contains("Latest: 60 bpm");
        assertThat(prompt).contains("Average: 62.5 bpm");
        assertThat(prompt).contains("VO2 Max");
        assertThat(prompt).contains("Latest: 47.0 ml/kg/min");
        assertThat(prompt).contains("Daily Steps");
        assertThat(prompt).contains("8500 steps/day");
        assertThat(prompt).contains("Total workouts in period: 8");
        assertThat(prompt).contains("RUN: 5 sessions");
        assertThat(prompt).contains("CYCLE: 3 sessions");
    }

    @Test
    void buildAnalysisPrompt_withNoData_shouldIndicateMissingData() {
        HealthDataSnapshot snapshot = HealthDataSnapshot.builder()
            .userId(UUID.randomUUID())
            .analysisWindowDays(30)
            .restingHeartRateTrend(null)
            .restingHeartRateAvg(null)
            .restingHeartRateLatest(null)
            .vo2MaxTrend(null)
            .vo2MaxAvg(null)
            .vo2MaxLatest(null)
            .averageDailySteps(null)
            .workoutSummaries(List.of())
            .totalWorkoutsInPeriod(0)
            .userProfile(new UserProfile(null, "UNKNOWN", null, null))
            .generatedAt(OffsetDateTime.now())
            .build();

        String prompt = promptBuilder.buildAnalysisPrompt(snapshot);

        assertThat(prompt).contains("Profile data incomplete");
        assertThat(prompt).contains("No data available");
        assertThat(prompt).contains("Total workouts in period: 0");
        assertThat(prompt).contains("No workout data recorded");
    }

    @Test
    void buildAnalysisPrompt_shouldIncludeRequestSection() {
        HealthDataSnapshot snapshot = HealthDataSnapshot.builder()
            .userId(UUID.randomUUID())
            .analysisWindowDays(30)
            .workoutSummaries(List.of())
            .totalWorkoutsInPeriod(0)
            .userProfile(new UserProfile(null, "UNKNOWN", null, null))
            .generatedAt(OffsetDateTime.now())
            .build();

        String prompt = promptBuilder.buildAnalysisPrompt(snapshot);

        assertThat(prompt).contains("Based on this data, please provide:");
        assertThat(prompt).contains("Assessment of current cardiovascular fitness level");
        assertThat(prompt).contains("Key observations about the health metrics trends");
        assertThat(prompt).contains("Specific workout routine recommendations");
        assertThat(prompt).contains("One actionable goal for the next 30 days");
    }
}
