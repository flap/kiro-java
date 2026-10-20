package dev.kiro.community.helloworld.config;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Expoe um {@link Clock} como bean para que componentes dependam do tempo de
 * forma testavel, em vez de chamar {@code Instant.now()} diretamente.
 */
@Configuration
public class ClockConfig {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}
