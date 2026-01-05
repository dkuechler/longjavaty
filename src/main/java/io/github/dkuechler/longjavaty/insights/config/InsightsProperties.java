package io.github.dkuechler.longjavaty.insights.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for AI Insights feature.
 * Defaults are defined in application.properties.
 */
@ConfigurationProperties(prefix = "app.insights")
public record InsightsProperties(
    int analysisWindowDays,
    int rateLimitDays,
    int maxFailedAttemptsPerHour,
    String apiKey
) {}
