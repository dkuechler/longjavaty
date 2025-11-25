package io.github.dkuechler.longjavaty;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LongjavatyApplicationTest {

    @Test
    void contextLoads() {
        // This test verifies that the Spring application context loads successfully
        // Currently using H2 database for testing (fallback when Docker not available)
    }
}