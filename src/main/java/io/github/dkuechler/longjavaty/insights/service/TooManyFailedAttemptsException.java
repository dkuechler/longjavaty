package io.github.dkuechler.longjavaty.insights.service;

import java.time.OffsetDateTime;

public class TooManyFailedAttemptsException extends RuntimeException {

    private final OffsetDateTime retryAfter;

    public TooManyFailedAttemptsException(OffsetDateTime retryAfter) {
        super("Too many failed attempts");
        this.retryAfter = retryAfter;
    }

    public OffsetDateTime getRetryAfter() {
        return retryAfter;
    }
}
