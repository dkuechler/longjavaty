package io.github.dkuechler.longjavaty.insights.controller;

import io.github.dkuechler.longjavaty.insights.controller.dto.HealthInsightResponse;
import io.github.dkuechler.longjavaty.insights.controller.dto.PromptExportResponse;
import io.github.dkuechler.longjavaty.insights.controller.dto.RateLimitStatusResponse;
import io.github.dkuechler.longjavaty.insights.service.InsightsService;
import io.github.dkuechler.longjavaty.insights.service.RateLimitExceededException;
import io.github.dkuechler.longjavaty.users.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InsightsController.class)
@TestPropertySource(properties = "app.insights.enabled=true")
class InsightsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InsightsService insightsService;

    @MockitoBean
    private UserService userService;

    @Test
    void analyzeHealth_shouldReturnInsight() throws Exception {
        UUID userId = UUID.randomUUID();
        HealthInsightResponse response = new HealthInsightResponse(
            "Your cardiovascular fitness is improving...",
            OffsetDateTime.now(),
            30,
            "gpt-4o"
        );

        when(insightsService.generateInsight(any(UUID.class))).thenReturn(response);

        mockMvc.perform(post("/api/insights/analyze")
                .with(jwt().jwt(jwt -> jwt.subject(userId.toString()))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.analysis").value("Your cardiovascular fitness is improving..."))
            .andExpect(jsonPath("$.modelUsed").value("gpt-4o"))
            .andExpect(jsonPath("$.dataWindowDays").value(30));
    }

    @Test
    void analyzeHealth_rateLimitExceeded_shouldReturn429() throws Exception {
        UUID userId = UUID.randomUUID();
        OffsetDateTime nextAvailable = OffsetDateTime.now().plusDays(3);

        when(insightsService.generateInsight(any(UUID.class)))
            .thenThrow(new RateLimitExceededException(nextAvailable));

        mockMvc.perform(post("/api/insights/analyze")
                .with(jwt().jwt(jwt -> jwt.subject(userId.toString()))))
            .andExpect(status().isTooManyRequests())
            .andExpect(jsonPath("$.message").exists())
            .andExpect(jsonPath("$.nextAvailableAt").exists());
    }

    @Test
    void analyzeHealth_userNotFound_shouldReturn404() throws Exception {
        UUID userId = UUID.randomUUID();

        when(insightsService.generateInsight(any(UUID.class)))
            .thenThrow(new NoSuchElementException("User not found"));

        mockMvc.perform(post("/api/insights/analyze")
                .with(jwt().jwt(jwt -> jwt.subject(userId.toString()))))
            .andExpect(status().isNotFound());
    }

    @Test
    void exportPrompt_shouldReturnPrompt() throws Exception {
        UUID userId = UUID.randomUUID();
        PromptExportResponse response = new PromptExportResponse(
            "You are a fitness coach...",
            "Please analyze the following...",
            OffsetDateTime.now(),
            30
        );

        when(insightsService.exportPrompt(any(UUID.class))).thenReturn(response);

        mockMvc.perform(get("/api/insights/prompt")
                .with(jwt().jwt(jwt -> jwt.subject(userId.toString()))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.systemPrompt").exists())
            .andExpect(jsonPath("$.userPrompt").exists())
            .andExpect(jsonPath("$.dataWindowDays").value(30));
    }

    @Test
    void exportPrompt_userNotFound_shouldReturn404() throws Exception {
        UUID userId = UUID.randomUUID();

        when(insightsService.exportPrompt(any(UUID.class)))
            .thenThrow(new NoSuchElementException("User not found"));

        mockMvc.perform(get("/api/insights/prompt")
                .with(jwt().jwt(jwt -> jwt.subject(userId.toString()))))
            .andExpect(status().isNotFound());
    }

    @Test
    void getRateLimitStatus_canRequest_shouldReturnTrue() throws Exception {
        UUID userId = UUID.randomUUID();
        RateLimitStatusResponse response = new RateLimitStatusResponse(true, null, null);

        when(insightsService.getRateLimitStatus(any(UUID.class))).thenReturn(response);

        mockMvc.perform(get("/api/insights/status")
                .with(jwt().jwt(jwt -> jwt.subject(userId.toString()))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.canRequest").value(true))
            .andExpect(jsonPath("$.nextAvailableAt").isEmpty());
    }

    @Test
    void getRateLimitStatus_rateLimited_shouldReturnFalseWithNextAvailable() throws Exception {
        UUID userId = UUID.randomUUID();
        OffsetDateTime lastRequest = OffsetDateTime.now().minusDays(3);
        OffsetDateTime nextAvailable = lastRequest.plusDays(7);
        RateLimitStatusResponse response = new RateLimitStatusResponse(false, nextAvailable, lastRequest);

        when(insightsService.getRateLimitStatus(any(UUID.class))).thenReturn(response);

        mockMvc.perform(get("/api/insights/status")
                .with(jwt().jwt(jwt -> jwt.subject(userId.toString()))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.canRequest").value(false))
            .andExpect(jsonPath("$.nextAvailableAt").exists())
            .andExpect(jsonPath("$.lastRequestAt").exists());
    }
}
