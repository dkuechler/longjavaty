package io.github.dkuechler.longjavaty.insights.service;

import java.time.OffsetDateTime;

public class RateLimitExceededException extends RuntimeException {

    private final OffsetDateTime nextAvailableAt;

    public RateLimitExceededException(OffsetDateTime nextAvailableAt) {
        super("Rate limit exceeded");
        this.nextAvailableAt = nextAvailableAt;
    }

    public OffsetDateTime getNextAvailableAt() {
        return nextAvailableAt;
    }
}
