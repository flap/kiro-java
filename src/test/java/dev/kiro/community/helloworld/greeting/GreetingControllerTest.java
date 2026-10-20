package dev.kiro.community.helloworld.greeting;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.kiro.community.helloworld.config.ClockConfig;
import dev.kiro.community.helloworld.web.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Teste de slice web: carrega apenas a camada MVC do controller.
 * Importa ClockConfig (dependencia do service/handler) e o handler global.
 */
@WebMvcTest(GreetingController.class)
@Import({GreetingService.class, ClockConfig.class, GlobalExceptionHandler.class})
class GreetingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void returnsGreetingForProvidedName() throws Exception {
        mockMvc.perform(get("/api/hello").param("name", "Grace"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Hello, Grace!"))
                .andExpect(jsonPath("$.recipient").value("Grace"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void returnsDefaultGreetingWithoutName() throws Exception {
        mockMvc.perform(get("/api/hello"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recipient").value("World"));
    }

    @Test
    void rejectsNameLongerThanLimit() throws Exception {
        String tooLong = "x".repeat(101);
        mockMvc.perform(get("/api/hello").param("name", tooLong))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation failed"));
    }
}
