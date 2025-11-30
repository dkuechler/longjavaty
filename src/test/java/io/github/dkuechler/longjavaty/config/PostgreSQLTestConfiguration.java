package io.github.dkuechler.longjavaty.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Test configuration that provides PostgreSQL via Testcontainers when Docker is
 * available.
 * 
 * To use this configuration:
 * 1. Make sure Docker is running
 * 2. Set spring.profiles.active=testcontainers in your test properties
 * 3. If Docker is not available, tests will fall back to H2
 */
@TestConfiguration
@Profile("testcontainers")
public class PostgreSQLTestConfiguration {

    private static PostgreSQLContainer<?> postgres;

    static {
        @SuppressWarnings("resource")
        PostgreSQLContainer<?> container = new PostgreSQLContainer<>(DockerImageName.parse("postgres:15-alpine"))
                .withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test")
                .withReuse(true);
        postgres = container;
    }

    @Bean
    public PostgreSQLContainer<?> postgresContainer() {
        if (!postgres.isRunning()) {
            postgres.start();
        }
        return postgres;
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        if (postgres != null && postgres.isRunning()) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl);
            registry.add("spring.datasource.username", postgres::getUsername);
            registry.add("spring.datasource.password", postgres::getPassword);
            registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.PostgreSQLDialect");
        }
    }
}