package dev.kiro.community.helloworld;

import static org.assertj.core.api.Assertions.assertThat;

import dev.kiro.community.helloworld.greeting.GreetingResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;

/**
 * Prova, com o contexto completo (Tomcat real em porta aleatoria), que a
 * propriedade {@code spring.threads.virtual.enabled=true} coloca as requisicoes
 * em virtual threads (Java 21, JEP 444).
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(VirtualThreadsIntegrationTest.ProbeController.class)
class VirtualThreadsIntegrationTest {

    @Autowired
    private TestRestTemplate rest;

    @Test
    void requestRunsOnVirtualThread() {
        Boolean isVirtual = rest.getForObject("/internal/thread-is-virtual", Boolean.class);
        assertThat(isVirtual).as("request deve rodar em virtual thread").isTrue();
    }

    @Test
    void helloEndpointStillWorksEndToEnd() {
        GreetingResponse response = rest.getForObject("/api/hello?name=Grace", GreetingResponse.class);
        assertThat(response.message()).isEqualTo("Hello, Grace!");
    }

    /** Controller de teste que reporta se a thread atual e virtual. */
    @TestConfiguration
    @RestController
    static class ProbeController {
        @GetMapping("/internal/thread-is-virtual")
        boolean isVirtual() {
            return Thread.currentThread().isVirtual();
        }
    }
}
