package dev.kiro.community.helloworld.greeting;

import jakarta.validation.constraints.Size;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;

/**
 * Endpoint REST da saudacao.
 *
 * <p>Controller fino: delega a regra de negocio ao {@link GreetingService} e
 * retorna um DTO. A entrada e validada com Jakarta Bean Validation.</p>
 */
@RestController
@Validated
public class GreetingController {

    private final GreetingService greetingService;

    public GreetingController(GreetingService greetingService) {
        this.greetingService = greetingService;
    }

    /**
     * GET /api/hello?name=Ada
     *
     * @param name nome opcional (max 100 caracteres)
     * @return saudacao em JSON
     */
    @GetMapping("/api/hello")
    public GreetingResponse hello(
            @RequestParam(name = "name", required = false)
            @Size(max = 100, message = "name deve ter no maximo 100 caracteres")
            String name) {
        return greetingService.greet(name);
    }
}
