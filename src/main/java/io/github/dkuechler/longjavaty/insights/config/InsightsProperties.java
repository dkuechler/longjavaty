package io.github.dkuechler.longjavaty.insights.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.insights")
public record InsightsProperties(
    int analysisWindowDays,
    int rateLimitDays
) {
    public InsightsProperties {
        if (analysisWindowDays <= 0) {
            analysisWindowDays = 30;
        }
        if (rateLimitDays <= 0) {
            rateLimitDays = 7;
        }
    }
}
