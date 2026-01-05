package io.github.dkuechler.longjavaty.insights.controller;

import io.github.dkuechler.longjavaty.insights.controller.dto.HealthInsightResponse;
import io.github.dkuechler.longjavaty.insights.controller.dto.PromptExportResponse;
import io.github.dkuechler.longjavaty.insights.controller.dto.RateLimitExceededResponse;
import io.github.dkuechler.longjavaty.insights.controller.dto.RateLimitStatusResponse;
import io.github.dkuechler.longjavaty.insights.service.AiServiceUnavailableException;
import io.github.dkuechler.longjavaty.insights.service.InsightsService;
import io.github.dkuechler.longjavaty.insights.service.RateLimitExceededException;
import io.github.dkuechler.longjavaty.insights.service.TooManyFailedAttemptsException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/api/insights")
@Slf4j
@Tag(name = "AI Insights", description = "AI-powered health analysis and workout recommendations")
@ConditionalOnProperty(name = "app.insights.enabled", havingValue = "true")
public class InsightsController {

    private final InsightsService insightsService;

    public InsightsController(InsightsService insightsService) {
        this.insightsService = insightsService;
    }

    @PostMapping("/analyze")
    @Operation(
        summary = "Generate AI health analysis",
        description = "Analyzes health metrics and workout data to provide personalized recommendations. Limited to 1 request per week."
    )
    @ApiResponse(responseCode = "200", description = "Analysis generated successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "404", description = "User not found")
    @ApiResponse(responseCode = "429", description = "Rate limit exceeded")
    @ApiResponse(responseCode = "503", description = "AI service unavailable")
    public ResponseEntity<Object> analyzeHealth(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        log.info("AI analysis requested for user: {}", userId);

        try {
            HealthInsightResponse response = insightsService.generateInsight(userId);
            return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .body(response);
        } catch (RateLimitExceededException e) {
            log.info("Rate limit exceeded for user: {}, next available: {}", userId, e.getNextAvailableAt());
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(new RateLimitExceededResponse(
                    "Rate limit exceeded. You can request 1 AI analysis per week.",
                    e.getNextAvailableAt()
                ));
        } catch (TooManyFailedAttemptsException e) {
            log.info("Too many failed attempts for user: {}, retry after: {}", userId, e.getRetryAfter());
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(new RateLimitExceededResponse(
                    "Too many failed attempts. Please try again later.",
                    e.getRetryAfter()
                ));
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AiServiceUnavailableException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage());
        }
    }

    @GetMapping("/prompt")
    @Operation(
        summary = "Export analysis prompt",
        description = "Returns the pre-constructed prompt with user's health data for use with external LLMs. Does not count against rate limit."
    )
    @ApiResponse(responseCode = "200", description = "Prompt exported successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<PromptExportResponse> exportPrompt(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        log.debug("Prompt export requested for user: {}", userId);

        try {
            PromptExportResponse response = insightsService.exportPrompt(userId);
            return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .body(response);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/status")
    @Operation(
        summary = "Check rate limit status",
        description = "Returns whether user can make an AI analysis request and when the next request is available."
    )
    @ApiResponse(responseCode = "200", description = "Status retrieved")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    public ResponseEntity<RateLimitStatusResponse> getRateLimitStatus(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        return ResponseEntity.ok(insightsService.getRateLimitStatus(userId));
    }
}
