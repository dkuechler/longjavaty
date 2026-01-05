package io.github.dkuechler.longjavaty.insights.controller.dto;

import java.time.OffsetDateTime;

public record RateLimitStatusResponse(
    boolean canRequest,
    OffsetDateTime nextAvailableAt,
    OffsetDateTime lastRequestAt
) {}
