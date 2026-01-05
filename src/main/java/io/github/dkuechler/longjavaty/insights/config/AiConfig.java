package io.github.dkuechler.longjavaty.insights.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "app.insights.enabled", havingValue = "true")
public class AiConfig {

    private static final String SYSTEM_PROMPT = """
        You are a certified fitness coach and health analyst. Your role is to analyze \
        health metrics and workout data to provide evidence-based, actionable workout \
        recommendations. Be encouraging but realistic. Focus on gradual improvements \
        and sustainable habits. Always consider safety and recommend consulting a \
        healthcare provider for significant changes.""";

    @Value("${app.insights.analysis-window-days:30}")
    private int analysisWindowDays;

    @Value("${app.insights.rate-limit-days:7}")
    private int rateLimitDays;

    @Bean
    public ChatClient healthInsightsChatClient(ChatClient.Builder builder) {
        return builder
            .defaultSystem(SYSTEM_PROMPT)
            .build();
    }

    public String getSystemPrompt() {
        return SYSTEM_PROMPT;
    }

    public int getAnalysisWindowDays() {
        return analysisWindowDays;
    }

    public int getRateLimitDays() {
        return rateLimitDays;
    }
}
