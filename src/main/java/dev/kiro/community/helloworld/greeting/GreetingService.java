package dev.kiro.community.helloworld.greeting;

import java.time.Clock;
import java.time.Instant;
import org.springframework.stereotype.Service;

/**
 * Regra de negocio da saudacao.
 *
 * <p>A dependencia de {@link Clock} e injetada para tornar o tempo testavel
 * (evita {@code Instant.now()} fixo no codigo de producao).</p>
 */
@Service
public class GreetingService {

    private final Clock clock;

    // Injecao por construtor (preferida): dependencias final, sem reflexao em campo.
    public GreetingService(Clock clock) {
        this.clock = clock;
    }

    /**
     * Monta a saudacao para o destinatario informado.
     *
     * @param name nome do destinatario; se vazio, usa "World"
     * @return resposta de saudacao
     */
    public GreetingResponse greet(String name) {
        String recipient = (name == null || name.isBlank()) ? "World" : name.strip();
        String message = "Hello, %s!".formatted(recipient);
        return new GreetingResponse(message, recipient, Instant.now(clock));
    }
}
