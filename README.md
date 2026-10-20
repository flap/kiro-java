# Hello World — Java 21 + Spring Boot (Template de Melhores Práticas)

Projeto **Hello World** mínimo e executável que serve como **template de
referência** para desenvolvimento Java seguro e performático com Spring Boot e
Flyway. Toda a "inteligência" do time está codificada em **steering files** e
**MCP** do Kiro, para que outro desenvolvedor baixe o projeto e continue — ou
aplique os mesmos padrões a um projeto existente — sem reaprender as regras.

As decisões seguem o [guia Java do Kiro](https://kiro.dev/docs/guides/languages-and-frameworks/java-guide/)
(estrutura de projeto, steering `product`/`tech`/`structure`, steering específicos
de Spring Boot e Java, e MCP servers).

## Pré-requisitos

- **JDK 21** (LTS). Recomendado [Amazon Corretto 21](https://aws.amazon.com/corretto/).
- **Maven 3.9+** — ou use o Maven Wrapper (`./mvnw`) se presente.
- (Opcional) **`uv`/`uvx`** para os MCP servers: https://docs.astral.sh/uv/getting-started/installation/

Verifique: `java -version` deve mostrar `21`.

## Como rodar

```bash
# Compila e roda os testes
mvn clean verify            # ou: ./mvnw clean verify

# Sobe a aplicação (profile default: H2 em memória, sem infra externa)
mvn spring-boot:run         # ou: ./mvnw spring-boot:run

# Testa o endpoint
curl "http://localhost:8080/api/hello?name=Ada"
# {"message":"Hello, Ada!","recipient":"Ada","timestamp":"..."}

# Health check
curl http://localhost:8080/actuator/health
```

### Testes de API (ScanAPI)

Testes de integração de API de ponta a ponta + documentação viva. A app precisa
estar no ar. Detalhes em [`api-tests/README.md`](api-tests/README.md) e na regra
`.kiro/steering/api-tests.md` (obrigatório para todo endpoint).

```bash
pipx install scanapi                                  # uma vez
./mvnw spring-boot:run                                # terminal 1
BASE_URL=http://localhost:8080 scanapi run \
  api-tests/scanapi.yaml -o api-tests/scanapi-report.html   # terminal 2
open api-tests/scanapi-report.html                    # relatório (doc viva)
```

### Produção (PostgreSQL)

```bash
SPRING_PROFILES_ACTIVE=prod \
  DB_URL=jdbc:postgresql://localhost:5432/hellodb \
  DB_USERNAME=app \
  DB_PASSWORD=*** \
  java -jar target/hello-world-0.0.1-SNAPSHOT.jar
```

Nenhum segredo fica no código — tudo vem de variáveis de ambiente.

## O que este template demonstra

| Área | Prática |
|------|---------|
| **Java 21** | Records para DTOs, `Clock` injetável, código testável |
| **Virtual threads** | `spring.threads.virtual.enabled=true`; sem `synchronized` em I/O |
| **Spring Boot** | Injeção por construtor, controller fino + service, DTO (nunca entidade na API) |
| **Validação** | Jakarta Bean Validation na borda (`@Size`) |
| **Erros** | `@RestControllerAdvice` com formato `ApiError` consistente, sem vazar internals |
| **Flyway** | Schema versionado (`V1__*.sql`), `ddl-auto: validate` |
| **Segurança** | Segredos via env, Actuator com menor privilégio, sem stack trace ao cliente |
| **Observabilidade** | Actuator `health`/`info` |
| **Testes** | Unitário puro + `@WebMvcTest` + smoke `@SpringBootTest` (AAA) |
| **Testes de API** | ScanAPI (`api-tests/`) — contrato HTTP de ponta a ponta + doc viva |

## Estrutura

```
src/main/java/dev/kiro/community/helloworld/
├── HelloWorldApplication.java   # entrypoint
├── config/ClockConfig.java      # Clock bean (tempo testável)
├── greeting/                    # feature (vertical slice)
│   ├── GreetingController.java  # web (fino)
│   ├── GreetingService.java     # regra de negócio
│   └── GreetingResponse.java    # DTO (record)
└── web/                         # cross-cutting
    ├── ApiError.java
    └── GlobalExceptionHandler.java

src/main/resources/
├── application.yml              # default (H2, virtual threads)
├── application-prod.yml         # profile prod (PostgreSQL via env)
└── db/migration/V1__create_greeting_log.sql
```

Detalhes em `.kiro/steering/structure.md`.

## Kiro: steering e MCP

As regras que o Kiro aplica automaticamente estão em **`.kiro/steering/`**:

- `product.md` — propósito e escopo
- `tech.md` — stack, versões e virtual threads
- `structure.md` — layout e onde colocar código novo
- `spring-boot-patterns.md` — padrões Spring Boot
- `java-conventions.md` — convenções de Java 21
- `security.md` — desenvolvimento seguro
- `flyway-migrations.md` — regras de migração

**`AGENTS.md`** é o guia rápido para agentes.

### Habilitar os MCP servers

Copie o conteúdo de `.kiro/mcp.example.json` para um dos arquivos de config MCP:

- Workspace: `.kiro/settings/mcp.json`
- Usuário (global): `~/.kiro/settings/mcp.json`

Isso habilita o **Maven MCP server** (rodar Maven e gerenciar dependências pelo
Kiro) e o **AWS Documentation MCP server**. Requer `uvx` instalado.

## Aplicar estes padrões a um projeto existente

1. Copie o diretório `.kiro/` (steering + `mcp.example.json`) e o `AGENTS.md`
   para a raiz do outro projeto.
2. Ajuste `tech.md`/`structure.md` para refletir a stack/estrutura reais.
3. Abra o projeto no Kiro — os steerings passam a valer em todas as interações.

## Próximos passos sugeridos (fora do escopo deste template)

- Adicionar `spring-boot-starter-security` (negar por padrão; OAuth2/JWT).
- Testes de integração com **Testcontainers** (PostgreSQL real).
- Pipeline de CI (build, testes, OWASP Dependency-Check/Snyk, Spotless/Checkstyle).
- Empacotamento em container e deploy.

## Verificação (executada)

O projeto foi compilado e validado com **JDK 21 (Amazon Corretto/OpenJDK 21.0.12)**:

- `mvn clean verify` → **BUILD SUCCESS**, **9 testes** (0 falhas/erros).
- App iniciada: `/actuator/health` → `UP`; `/api/hello?name=Ada` → JSON correto;
  validação de `name` acima de 100 chars → **HTTP 400**.
- **Flyway** aplicou `V1__create_greeting_log.sql` (schema `v1`).
- **Virtual threads** confirmadas por teste de integração
  (`VirtualThreadsIntegrationTest` assegura que a request roda em virtual thread).
- Bytecode gerado em **major version 65 (Java 21)**.

O **Maven Wrapper** (`./mvnw`) está versionado — não é preciso instalar Maven,
apenas um **JDK 21**.
