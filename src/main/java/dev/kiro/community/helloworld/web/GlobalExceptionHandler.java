package dev.kiro.community.helloworld.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Tratamento global de erros. Centraliza a conversao de excecoes em respostas
 * {@link ApiError} consistentes, sem vazar stack traces ou detalhes internos.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Clock clock;

    public GlobalExceptionHandler(Clock clock) {
        this.clock = clock;
    }

    /** Falhas de validacao de parametros (@Size, @NotBlank, etc.). */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleValidation(
            ConstraintViolationException ex, HttpServletRequest request) {
        List<String> messages = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .toList();
        return build(HttpStatus.BAD_REQUEST, "Validation failed", messages, request);
    }

    /** Fallback: evita expor detalhes internos ao cliente. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception ex, HttpServletRequest request) {
        return build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal server error",
                List.of("Ocorreu um erro inesperado."),
                request);
    }

    private ResponseEntity<ApiError> build(
            HttpStatus status, String error, List<String> messages, HttpServletRequest request) {
        ApiError body = new ApiError(
                Instant.now(clock), status.value(), error, messages, request.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }
}
