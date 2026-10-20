# Stack Técnica

## Versões (decisões conscientes)

| Item         | Versão        | Racional |
|--------------|---------------|----------|
| Java         | **21 (LTS)**  | Requisito do projeto. LTS com suporte estendido; traz virtual threads (JEP 444), records, pattern matching e sealed types estáveis. |
| Spring Boot  | **3.5.6**     | Última linha OSS com baseline Java 21 e a maior cobertura de documentação. Spring Boot 4.x também roda em Java 21, mas introduz quebras (Jackson 3, Spring Security 7). Escolhemos 3.5.x para um template estável. |
| Build        | **Maven**     | Ampla adoção; `pom.xml` declarativo. Maven Wrapper (`mvnw`) incluído para builds reprodutíveis. |
| Banco (dev)  | **H2** em memória | Permite rodar sem infra externa. |
| Banco (prod) | **PostgreSQL**| Ativado pelo profile `prod` via variáveis de ambiente. |
| Migrações    | **Flyway**    | Schema versionado e determinístico. Fonte única da verdade do schema. |

> **Nota sobre Spring Boot 4.x:** quando migrar, planeje para Jackson 3,
> Spring Security 7 (defaults mais restritos) e modularização dos starters.
> Em Java 21, o `synchronized` ainda pode *pinar* virtual threads (corrigido no
> Java 24, JEP 491) — ver regras de concorrência abaixo.

## Virtual Threads (Java 21)

- Habilitadas por configuração, não por código: `spring.threads.virtual.enabled=true`.
- Movem requests do Tomcat, `@Async` e `@Scheduled` para virtual threads.
- **Ganho**: cargas I/O-bound atendem muito mais requisições concorrentes sem pool.
- **Não ajuda**: trabalho CPU-bound (a CPU continua sendo o limite).
- **Regra crítica em Java 21**: evite `synchronized` em volta de I/O — ele *pina*
  a virtual thread à carrier thread. Use `ReentrantLock` ou `Semaphore`.
- **Limite de concorrência a serviços lentos**: use `Semaphore`, não pool de threads.
- `ThreadLocal` continua funcionando (Spring usa para security/transações), mas
  **não** o use como cache por-thread — cada tarefa tem uma thread nova.

## Comandos essenciais

```bash
# Build + testes (requer JDK 21)
./mvnw clean verify          # ou: mvn clean verify

# Rodar local (profile default, H2)
./mvnw spring-boot:run

# Rodar em produção (PostgreSQL)
SPRING_PROFILES_ACTIVE=prod \
  DB_URL=jdbc:postgresql://host:5432/db \
  DB_USERNAME=app DB_PASSWORD=*** \
  java -jar target/hello-world-0.0.1-SNAPSHOT.jar

# Testar o endpoint
curl "http://localhost:8080/api/hello?name=Ada"
```

## Testes de API (ScanAPI) — obrigatório

Além do JUnit, todo endpoint tem testes de integração com ScanAPI em `api-tests/`
(ver `api-tests.md`). A app precisa estar no ar:

```bash
pipx install scanapi                                  # uma vez (CLI isolada)
./mvnw spring-boot:run                                # terminal 1
BASE_URL=http://localhost:8080 scanapi run \
  api-tests/scanapi.yaml -o api-tests/scanapi-report.html   # terminal 2
```

## Ferramentas de qualidade (recomendadas)

Formatação e estilo devem ser automatizados, não enfiados em steering:
Spotless/Google Java Format, Checkstyle, SpotBugs. Steering foca em arquitetura
e decisões, não em formatação.
