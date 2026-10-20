package dev.kiro.community.helloworld.web;

import java.time.Instant;
import java.util.List;

/**
 * Formato de erro consistente retornado pela API.
 *
 * @param timestamp momento do erro (UTC)
 * @param status    codigo HTTP
 * @param error     descricao curta do erro
 * @param messages  mensagens detalhadas (ex.: falhas de validacao)
 * @param path      caminho da requisicao
 */
public record ApiError(
        Instant timestamp,
        int status,
        String error,
        List<String> messages,
        String path) {
}
