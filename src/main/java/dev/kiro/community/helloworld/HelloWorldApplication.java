package dev.kiro.community.helloworld;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Ponto de entrada da aplicacao Hello World.
 *
 * <p>As virtual threads (Java 21, JEP 444) sao habilitadas via a propriedade
 * {@code spring.threads.virtual.enabled=true} em {@code application.yml}, nao por codigo.</p>
 */
@SpringBootApplication
public class HelloWorldApplication {

    public static void main(String[] args) {
        SpringApplication.run(HelloWorldApplication.class, args);
    }
}
