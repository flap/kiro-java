# Convenções Java (Java 21)

## Recursos de linguagem a usar

- **Records** para DTOs e value objects imutáveis (requests, responses, eventos).
- **`var`** para variáveis locais quando o tipo for óbvio pela expressão.
- **Pattern matching** (`instanceof`, `switch`) em vez de casts manuais.
- **Sealed types** para hierarquias fechadas (ex.: resultados de domínio).
- **Text blocks** (`"""`) para SQL, JSON e strings multilinha.
- **`String.formatted(...)`** / `.strip()` / `.isBlank()` em vez de utilitários antigos.

## Imutabilidade e nulos

- Campos `final` sempre que possível; prefira objetos imutáveis.
- Não retorne `null` de métodos públicos — use `Optional<T>` ou coleções vazias.
- Valide argumentos no limite público (controllers via Bean Validation).

## Concorrência (crítico com virtual threads)

- **Nunca** use `synchronized` ao redor de I/O em Java 21 (pina a virtual thread).
  Use `java.util.concurrent.locks.ReentrantLock` ou `Semaphore`.
- Para limitar chamadas a serviços lentos, use `Semaphore`, não pools de threads.
- Para paralelizar sub-tarefas de uma request, use
  `Executors.newVirtualThreadPerTaskExecutor()` em try-with-resources.
- Prefira objetos thread-safe compartilhados (ex.: `DateTimeFormatter`) a
  `ThreadLocal` como cache.

## Tratamento de erros

- Exceções específicas de domínio (ex.: `GreetingNotFoundException`), não `Exception` genérica.
- Centralize a conversão em HTTP no `@RestControllerAdvice` (ver `web/GlobalExceptionHandler`).
- **Nunca** exponha stack trace, mensagem interna ou SQL ao cliente.
- Logue com contexto (correlation id quando houver); não logue segredos nem PII.

## Testes

- Use JUnit 5 + AssertJ + Mockito (vem no `spring-boot-starter-test`).
- Padrão **AAA** (Arrange, Act, Assert).
- Teste unitário puro para lógica (sem subir contexto Spring) — rápido.
- `@WebMvcTest` para slice de controller; `@SpringBootTest` para smoke/integração.
- Para integração com banco real, prefira **Testcontainers** (PostgreSQL).
- Torne o tempo testável: injete `Clock`, não chame `Instant.now()` direto.

## Estilo

- Formatação é responsabilidade de ferramenta (Spotless/Google Java Format), não humana.
- Métodos curtos e com responsabilidade única.
- Javadoc em APIs públicas e decisões não-óbvias; comentário explica o "porquê".
