# Estrutura do Projeto

## Layout

```
kiro-java/
├── pom.xml                     # Build Maven, Spring Boot 3.5.x, Java 21
├── mvnw / mvnw.cmd             # Maven Wrapper (versionado) — build sem instalar Maven
├── .mvn/wrapper/               # Configuração do wrapper
├── AGENTS.md                   # Guia para agentes (sempre incluído pelo Kiro)
├── README.md                   # Guia humano de onboarding
├── .kiro/
│   ├── steering/               # Regras persistentes (este diretório)
│   │   ├── product.md
│   │   ├── tech.md
│   │   ├── structure.md
│   │   ├── java-conventions.md
│   │   ├── spring-boot-patterns.md
│   │   ├── security.md
│   │   ├── flyway-migrations.md
│   │   └── api-tests.md        # testes de API com ScanAPI (OBRIGATÓRIO)
│   └── settings/
│       └── mcp.json            # MCP servers recomendados
├── api-tests/                  # Testes de integração de API (ScanAPI)
│   ├── scanapi.yaml            # spec versionada
│   ├── scanapi.conf            # config
│   └── README.md               # como rodar
└── src/
    ├── main/
    │   ├── java/dev/kiro/community/helloworld/
    │   │   ├── HelloWorldApplication.java      # @SpringBootApplication
    │   │   ├── config/                         # Beans de configuração (Clock, etc.)
    │   │   ├── greeting/                        # Feature "greeting" (vertical slice)
    │   │   │   ├── GreetingController.java      # camada web
    │   │   │   ├── GreetingService.java         # regra de negócio
    │   │   │   └── GreetingResponse.java         # DTO (record)
    │   │   └── web/                             # cross-cutting web (erros)
    │   │       ├── ApiError.java
    │   │       └── GlobalExceptionHandler.java
    │   └── resources/
    │       ├── application.yml                  # config default (H2)
    │       ├── application-prod.yml             # profile prod (PostgreSQL)
    │       └── db/migration/                    # migrações Flyway
    │           └── V1__create_greeting_log.sql
    └── test/java/...                            # testes espelham a estrutura main
```

## Princípios de organização

- **Pacote base**: `dev.kiro.community.helloworld`. Tudo abaixo dele para o
  component scan funcionar sem configuração extra.
- **Organização por feature (vertical slice)**: agrupe por domínio (`greeting/`),
  não por camada técnica global (`controllers/`, `services/`). Facilita evolução
  e remoção de features inteiras.
- **Cross-cutting** (tratamento de erros, config) em pacotes próprios (`web/`, `config/`).
- **Testes espelham `main`**: mesmo pacote, sufixo `Test`.
- **Sem lógica de negócio no controller**: controller é fino, delega ao service.

## Onde colocar coisas novas

| Preciso de...                        | Vai em...                                   |
|--------------------------------------|---------------------------------------------|
| Novo endpoint de uma feature         | `<feature>/XxxController.java`              |
| Regra de negócio                     | `<feature>/XxxService.java`                 |
| Objeto de request/response           | `<feature>/XxxRequest/Response.java` (record)|
| Entidade JPA + repositório           | `<feature>/` (ex.: `GreetingLog`, `GreetingLogRepository`) |
| Mudança de schema                    | nova migração `db/migration/V{n}__*.sql`    |
| Bean de configuração                 | `config/`                                   |
| Novo profile/ambiente                | `application-<profile>.yml`                 |
| **Teste de endpoint (obrigatório)**  | **`api-tests/scanapi.yaml`** (ver `api-tests.md`) |
```
