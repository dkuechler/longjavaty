package io.github.dkuechler.longjavaty.insights.controller.dto;

import java.time.OffsetDateTime;

public record HealthInsightResponse(
    String analysis,
    OffsetDateTime generatedAt,
    int dataWindowDays,
    String modelUsed
) {}
