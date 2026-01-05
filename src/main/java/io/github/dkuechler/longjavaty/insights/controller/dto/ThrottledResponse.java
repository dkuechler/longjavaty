package io.github.dkuechler.longjavaty.insights.controller.dto;

import java.time.OffsetDateTime;

public record ThrottledResponse(
    String message,
    OffsetDateTime retryAfter
) {}
