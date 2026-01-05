package io.github.dkuechler.longjavaty.insights.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
@ConditionalOnProperty(name = "app.insights.enabled", havingValue = "true")
@EnableConfigurationProperties(InsightsProperties.class)
@Slf4j
public class AiConfig {

    private static final String SYSTEM_PROMPT = """
        You are a fitness coach analyzing health metrics and workout data
        to provide evidence-based, actionable recommendations. Be encouraging
        but realistic. Focus on gradual improvements and sustainable habits.
        Always consider safety and recommend consulting a healthcare provider
        for significant changes.""";

    private final InsightsProperties properties;

    public AiConfig(InsightsProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void validateConfiguration() {
        if (properties.apiKey() == null || properties.apiKey().isBlank()) {
            throw new IllegalStateException(
                "AI Insights is enabled but spring.ai.openai.api-key is not configured. " +
                "Set OPENAI_API_KEY environment variable or disable the feature with app.insights.enabled=false"
            );
        }
        log.info("AI Insights feature enabled with OpenAI integration");
    }

    @Bean
    public ChatClient healthInsightsChatClient(ChatClient.Builder builder) {
        return builder
            .defaultSystem(SYSTEM_PROMPT)
            .build();
    }

    @Bean
    public Clock insightsClock() {
        return Clock.systemUTC();
    }

    public String getSystemPrompt() {
        return SYSTEM_PROMPT;
    }
}
