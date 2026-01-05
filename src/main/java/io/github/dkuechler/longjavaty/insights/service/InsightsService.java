package io.github.dkuechler.longjavaty.insights.service;

import io.github.dkuechler.longjavaty.insights.config.AiConfig;
import io.github.dkuechler.longjavaty.insights.controller.dto.HealthInsightResponse;
import io.github.dkuechler.longjavaty.insights.controller.dto.PromptExportResponse;
import io.github.dkuechler.longjavaty.insights.controller.dto.RateLimitStatusResponse;
import io.github.dkuechler.longjavaty.insights.model.AiInsightRequest;
import io.github.dkuechler.longjavaty.insights.model.HealthDataSnapshot;
import io.github.dkuechler.longjavaty.insights.repository.AiInsightRequestRepository;
import io.github.dkuechler.longjavaty.users.model.AppUser;
import io.github.dkuechler.longjavaty.users.repository.AppUserRepository;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@ConditionalOnProperty(name = "app.insights.enabled", havingValue = "true")
public class InsightsService {

    private final ChatClient chatClient;
    private final HealthDataAggregator healthDataAggregator;
    private final PromptBuilder promptBuilder;
    private final AiInsightRequestRepository requestRepository;
    private final AppUserRepository appUserRepository;
    private final AiConfig aiConfig;
    private final MeterRegistry meterRegistry;

    public InsightsService(ChatClient healthInsightsChatClient,
                           HealthDataAggregator healthDataAggregator,
                           PromptBuilder promptBuilder,
                           AiInsightRequestRepository requestRepository,
                           AppUserRepository appUserRepository,
                           AiConfig aiConfig,
                           MeterRegistry meterRegistry) {
        this.chatClient = healthInsightsChatClient;
        this.healthDataAggregator = healthDataAggregator;
        this.promptBuilder = promptBuilder;
        this.requestRepository = requestRepository;
        this.appUserRepository = appUserRepository;
        this.aiConfig = aiConfig;
        this.meterRegistry = meterRegistry;
    }

    @Transactional
    public HealthInsightResponse generateInsight(UUID userId) {
        AppUser user = findUser(userId);
        checkRateLimit(userId);

        AiInsightRequest request = new AiInsightRequest(user, OffsetDateTime.now());
        requestRepository.save(request);

        HealthDataSnapshot snapshot = healthDataAggregator.aggregateHealthData(userId);
        String userPrompt = promptBuilder.buildAnalysisPrompt(snapshot);

        try {
            ChatResponse response = chatClient.prompt()
                .user(userPrompt)
                .call()
                .chatResponse();

            String content = response.getResult().getOutput().getText();
            Integer tokensUsed = extractTokenUsage(response);
            String modelUsed = extractModel(response);

            request.markSuccessful(tokensUsed, modelUsed);
            requestRepository.save(request);

            meterRegistry.counter("insights.generated", "status", "success").increment();
            log.info("Generated AI insight for user: {}, tokens: {}", userId, tokensUsed);

            return new HealthInsightResponse(
                content,
                snapshot.generatedAt(),
                snapshot.analysisWindowDays(),
                modelUsed
            );
        } catch (Exception e) {
            meterRegistry.counter("insights.generated", "status", "error").increment();
            log.error("AI analysis failed for user: {}", userId, e);
            throw new AiServiceUnavailableException("AI service is temporarily unavailable", e);
        }
    }

    @Transactional(readOnly = true)
    public PromptExportResponse exportPrompt(UUID userId) {
        findUser(userId);

        HealthDataSnapshot snapshot = healthDataAggregator.aggregateHealthData(userId);
        String userPrompt = promptBuilder.buildAnalysisPrompt(snapshot);

        meterRegistry.counter("insights.prompt_exported").increment();
        log.debug("Exported prompt for user: {}", userId);

        return new PromptExportResponse(
            aiConfig.getSystemPrompt(),
            userPrompt,
            snapshot.generatedAt(),
            snapshot.analysisWindowDays()
        );
    }

    @Transactional(readOnly = true)
    public RateLimitStatusResponse getRateLimitStatus(UUID userId) {
        OffsetDateTime rateLimitWindow = OffsetDateTime.now().minusDays(aiConfig.getRateLimitDays());

        Optional<AiInsightRequest> lastRequest = requestRepository
            .findFirstByUser_IdAndSuccessTrueAndRequestedAtAfterOrderByRequestedAtDesc(userId, rateLimitWindow);

        if (lastRequest.isEmpty()) {
            return new RateLimitStatusResponse(true, null, null);
        }

        OffsetDateTime lastRequestAt = lastRequest.get().getRequestedAt();
        OffsetDateTime nextAvailable = lastRequestAt.plusDays(aiConfig.getRateLimitDays());
        boolean canRequest = OffsetDateTime.now().isAfter(nextAvailable);

        return new RateLimitStatusResponse(
            canRequest,
            canRequest ? null : nextAvailable,
            lastRequestAt
        );
    }

    private void checkRateLimit(UUID userId) {
        OffsetDateTime rateLimitWindow = OffsetDateTime.now().minusDays(aiConfig.getRateLimitDays());

        Optional<AiInsightRequest> lastRequest = requestRepository
            .findFirstByUser_IdAndSuccessTrueAndRequestedAtAfterOrderByRequestedAtDesc(userId, rateLimitWindow);

        if (lastRequest.isPresent()) {
            OffsetDateTime nextAvailable = lastRequest.get().getRequestedAt()
                .plusDays(aiConfig.getRateLimitDays());
            throw new RateLimitExceededException(nextAvailable);
        }
    }

    private AppUser findUser(UUID userId) {
        return appUserRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));
    }

    private Integer extractTokenUsage(ChatResponse response) {
        if (response.getMetadata() != null && response.getMetadata().getUsage() != null) {
            return (int) response.getMetadata().getUsage().getTotalTokens();
        }
        return null;
    }

    private String extractModel(ChatResponse response) {
        if (response.getMetadata() != null && response.getMetadata().getModel() != null) {
            return response.getMetadata().getModel();
        }
        return "unknown";
    }
}
