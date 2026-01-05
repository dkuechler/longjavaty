package io.github.dkuechler.longjavaty.insights.controller.dto;

import java.time.OffsetDateTime;

public record PromptExportResponse(
    String systemPrompt,
    String userPrompt,
    OffsetDateTime generatedAt,
    int dataWindowDays
) {}
