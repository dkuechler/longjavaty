package io.github.dkuechler.longjavaty.insights.controller.dto;

import java.time.OffsetDateTime;

public record RateLimitExceededResponse(
    String message,
    OffsetDateTime nextAvailableAt
) {}
