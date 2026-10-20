package dev.kiro.community.helloworld.greeting;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

/**
 * Teste unitario puro (sem contexto Spring) — rapido e deterministico.
 * O {@link Clock} fixo garante timestamp previsivel (padrao AAA).
 */
class GreetingServiceTest {

    private final Clock fixedClock = Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC);
    private final GreetingService service = new GreetingService(fixedClock);

    @Test
    void greetsNamedRecipient() {
        // Act
        GreetingResponse response = service.greet("Ada");

        // Assert
        assertThat(response.message()).isEqualTo("Hello, Ada!");
        assertThat(response.recipient()).isEqualTo("Ada");
        assertThat(response.timestamp()).isEqualTo(Instant.parse("2026-01-01T00:00:00Z"));
    }

    @Test
    void fallsBackToWorldWhenNameIsBlank() {
        assertThat(service.greet("   ").message()).isEqualTo("Hello, World!");
        assertThat(service.greet(null).recipient()).isEqualTo("World");
    }

    @Test
    void trimsRecipientName() {
        assertThat(service.greet("  Linus  ").recipient()).isEqualTo("Linus");
    }
}
