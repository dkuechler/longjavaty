package io.github.dkuechler.longjavaty.insights.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
@ConditionalOnProperty(name = "app.insights.enabled", havingValue = "true")
@EnableConfigurationProperties(InsightsProperties.class)
public class AiConfig {

    private static final String SYSTEM_PROMPT = """
        You are a certified fitness coach and health analyst. Your role is to analyze \
        health metrics and workout data to provide evidence-based, actionable workout \
        recommendations. Be encouraging but realistic. Focus on gradual improvements \
        and sustainable habits. Always consider safety and recommend consulting a \
        healthcare provider for significant changes.""";

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
