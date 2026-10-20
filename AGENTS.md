# AGENTS.md — Guia para Agentes (Kiro e afins)

Este arquivo orienta agentes de IA que trabalham neste repositório. As regras
detalhadas vivem em `.kiro/steering/` e são sempre aplicadas pelo Kiro; este
arquivo é o ponto de entrada rápido.

## O que é este projeto

Template **Hello World** em **Java 21 + Spring Boot 3.5.x** que demonstra
melhores práticas de desenvolvimento seguro e performático. O valor está nos
padrões (steering + código), não na funcionalidade.

## Antes de alterar código, leia os steerings

| Arquivo (`.kiro/steering/`)   | Assunto |
|-------------------------------|---------|
| `product.md`                  | Propósito e escopo |
| `tech.md`                     | Versões, virtual threads, comandos |
| `structure.md`                | Layout e onde colocar código novo |
| `spring-boot-patterns.md`     | Padrões Spring (DI, API, config) |
| `java-conventions.md`         | Convenções de linguagem Java 21 |
| `security.md`                 | Desenvolvimento seguro |
| `flyway-migrations.md`        | Regras de migração de banco |
| `api-tests.md`                | Testes de API com ScanAPI (**obrigatório**) |

## Comandos

```bash
./mvnw clean verify        # compila + roda testes JUnit (requer JDK 21)
./mvnw spring-boot:run     # sobe local (H2 em memória)
curl "http://localhost:8080/api/hello?name=Ada"

# Testes de API (ScanAPI) — app precisa estar no ar
BASE_URL=http://localhost:8080 scanapi run api-tests/scanapi.yaml -o api-tests/scanapi-report.html
```

> O Maven Wrapper (`./mvnw`) está versionado — não precisa instalar Maven, só um
> **JDK 21**. Use `mvn` diretamente se preferir.

## Regras rápidas (não-negociáveis)

- **Java 21** e **Spring Boot 3.5.x**. Não troque a major sem decisão explícita.
- **Injeção por construtor**, campos `final`. Nunca `@Autowired` em campo.
- **Records** para DTOs; nunca exponha entidade JPA na API.
- **Flyway é dono do schema** (`ddl-auto: validate`). Nunca edite uma migração
  já aplicada — crie uma nova `V{n+1}__*.sql`.
- **Nenhum segredo no repositório**; segredos via env var / profile `prod`.
- **Virtual threads ligadas**: evite `synchronized` ao redor de I/O (use
  `ReentrantLock`/`Semaphore`).
- **Erros não vazam** stack trace/detalhe interno ao cliente.
- **Todo endpoint tem teste ScanAPI** em `api-tests/scanapi.yaml` (feliz + erro).
  Criar/alterar endpoint sem atualizar a spec = trabalho incompleto.

## Ao concluir uma mudança

1. Rode `./mvnw clean verify` e garanta build + testes JUnit verdes.
2. Se tocou em endpoint: atualize `api-tests/scanapi.yaml` e rode `scanapi run`
   com a app no ar até 100% verde (ver `api-tests.md`).
3. Para mudança de schema, adicione a migração Flyway e a entidade JPA juntas.
4. Confira o checklist de segurança em `.kiro/steering/security.md`.

## MCP

Config recomendada em `.kiro/mcp.example.json`. Copie para
`.kiro/settings/mcp.json` para habilitar o Maven MCP server e o AWS Docs MCP.
