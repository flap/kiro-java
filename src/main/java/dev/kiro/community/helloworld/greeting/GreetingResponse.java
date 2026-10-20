package dev.kiro.community.helloworld.greeting;

import java.time.Instant;

/**
 * DTO de resposta imutavel (Java record) para a saudacao.
 *
 * <p>Use records para DTOs: sao imutaveis, concisos e thread-safe por natureza,
 * o que combina com o modelo de virtual threads.</p>
 *
 * @param message    a mensagem de saudacao
 * @param recipient  para quem a saudacao foi gerada
 * @param timestamp  momento da geracao, em UTC
 */
public record GreetingResponse(String message, String recipient, Instant timestamp) {
}
