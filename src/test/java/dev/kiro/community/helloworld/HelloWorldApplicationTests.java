package dev.kiro.community.helloworld;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Smoke test: garante que o contexto Spring sobe (inclui Flyway + JPA validate).
 */
@SpringBootTest
class HelloWorldApplicationTests {

    @Test
    void contextLoads() {
        // Se o contexto carregar, Flyway aplicou V1 e o JPA validou o schema.
    }
}
