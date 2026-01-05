package io.github.dkuechler.longjavaty.insights.service;

import io.github.dkuechler.longjavaty.insights.model.HealthDataSnapshot;
import io.github.dkuechler.longjavaty.insights.model.HealthDataSnapshot.Trend;
import io.github.dkuechler.longjavaty.insights.model.HealthDataSnapshot.UserProfile;
import io.github.dkuechler.longjavaty.insights.model.HealthDataSnapshot.WorkoutSummary;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.insights.enabled", havingValue = "true")
public class PromptBuilder {

    public String buildAnalysisPrompt(HealthDataSnapshot data) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("Please analyze the following health and fitness data and provide personalized workout recommendations.\n\n");

        prompt.append("## User Profile\n");
        appendUserProfile(prompt, data.userProfile());

        prompt.append("\n## Health Metrics (Last ").append(data.analysisWindowDays()).append(" Days)\n");
        appendHealthMetrics(prompt, data);

        prompt.append("\n## Workout Activity\n");
        appendWorkoutSummary(prompt, data);

        prompt.append("\n\nBased on this data, please provide:\n");
        prompt.append("""
            1. Assessment of current cardiovascular fitness level
            2. Key observations about the health metrics trends
            3. Specific workout routine recommendations (types, frequency, intensity, duration)
            4. Areas requiring attention or improvement
            5. One actionable goal for the next 30 days

            Keep recommendations evidence-based and achievable. If data is insufficient,
            acknowledge limitations and provide general guidance.
            """);

        return prompt.toString();
    }

    private void appendUserProfile(StringBuilder prompt, UserProfile profile) {
        if (profile.hasCompleteProfile()) {
            prompt.append(String.format("- Age: %d years%n", profile.age()));
            prompt.append(String.format("- Sex: %s%n", profile.sex()));
            prompt.append(String.format("- Height: %.1f cm%n", profile.heightCm()));
            prompt.append(String.format("- Weight: %.1f kg%n", profile.weightKg()));
        } else {
            prompt.append("- Profile data incomplete (using general population assumptions)\n");
            if (profile.age() != null) {
                prompt.append(String.format("- Age: %d years%n", profile.age()));
            }
        }
    }

    private void appendHealthMetrics(StringBuilder prompt, HealthDataSnapshot data) {
        prompt.append("### Resting Heart Rate\n");
        if (data.restingHeartRateLatest() != null) {
            prompt.append(String.format("- Latest: %.0f bpm%n", data.restingHeartRateLatest()));
            prompt.append(String.format("- Average: %.1f bpm%n", data.restingHeartRateAvg()));
            prompt.append(String.format("- Trend: %s%n", formatTrend(data.restingHeartRateTrend(), true)));
        } else {
            prompt.append("- No data available\n");
        }

        prompt.append("\n### VO2 Max\n");
        if (data.vo2MaxLatest() != null) {
            prompt.append(String.format("- Latest: %.1f ml/kg/min%n", data.vo2MaxLatest()));
            prompt.append(String.format("- Average: %.1f ml/kg/min%n", data.vo2MaxAvg()));
            prompt.append(String.format("- Trend: %s%n", formatTrend(data.vo2MaxTrend(), false)));
        } else {
            prompt.append("- No data available\n");
        }

        prompt.append("\n### Daily Steps\n");
        if (data.averageDailySteps() != null) {
            prompt.append(String.format("- Average: %.0f steps/day%n", data.averageDailySteps()));
        } else {
            prompt.append("- No data available\n");
        }
    }

    private void appendWorkoutSummary(StringBuilder prompt, HealthDataSnapshot data) {
        prompt.append(String.format("- Total workouts in period: %d%n", data.totalWorkoutsInPeriod()));

        if (!data.workoutSummaries().isEmpty()) {
            prompt.append("- Breakdown by type:\n");
            for (WorkoutSummary summary : data.workoutSummaries()) {
                if (summary.avgHeartRate() != null) {
                    prompt.append(String.format("  - %s: %d sessions, avg %.0f min, avg HR %.0f bpm%n",
                        summary.workoutType(),
                        summary.count(),
                        summary.avgDurationSeconds() / 60,
                        summary.avgHeartRate()));
                } else {
                    prompt.append(String.format("  - %s: %d sessions, avg %.0f min%n",
                        summary.workoutType(),
                        summary.count(),
                        summary.avgDurationSeconds() / 60));
                }
            }
        } else {
            prompt.append("- No workout data recorded\n");
        }
    }

    private String formatTrend(Trend trend, boolean lowerIsBetter) {
        return switch (trend) {
            case INCREASING -> lowerIsBetter
                ? "Increasing (may need attention)"
                : "Increasing (improving)";
            case DECREASING -> lowerIsBetter
                ? "Decreasing (improving)"
                : "Decreasing (may need attention)";
            case STABLE -> "Stable";
            case INSUFFICIENT_DATA -> "Insufficient data for trend analysis";
        };
    }
}
