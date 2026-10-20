# Padrões Spring Boot

## Estrutura de componentes

- `@RestController` → camada web, fina, sem regra de negócio.
- `@Service` → regra de negócio.
- `@Repository` → acesso a dados (interfaces Spring Data JPA).
- `@Configuration` → definição de beans.

## Injeção de dependências

- **Sempre injeção por construtor** (nunca `@Autowired` em campo).
- Campos injetados são `final`.
- Um único construtor → o `@Autowired` é implícito (não precisa anotar).
- Evite dependências circulares; se aparecerem, repense o design.

## Design de API

- Siga REST: substantivos nos recursos, verbos HTTP corretos (GET/POST/PUT/DELETE).
- Retorne status HTTP apropriados (200, 201, 400, 404, 409, 500).
- Use **DTOs (records)** em request/response — nunca exponha entidades JPA diretamente.
- Valide entrada com Jakarta Bean Validation (`@Valid`, `@Size`, `@NotBlank`...).
- Respostas de erro no formato consistente de `web/ApiError`.

## Configuração

- Configuração em `application.yml`; segredos **sempre** via variável de ambiente.
- Um profile por ambiente (`application-<profile>.yml`); `prod` nunca usa H2.
- `open-in-view: false` (evita lazy loading fora da transação).
- `spring.jpa.hibernate.ddl-auto: validate` — Flyway é dono do schema, nunca `update`/`create`.

## Performance

- Virtual threads ligadas (`spring.threads.virtual.enabled=true`) — ver `tech.md`.
- Connection pool (HikariCP) dimensionado por ambiente via env vars.
- Cache (`@Cacheable`) para dados lidos com frequência quando fizer sentido.
- Processe tarefas longas de forma assíncrona (`@Async`, já em virtual thread).

## Observabilidade

- Exponha apenas os endpoints Actuator necessários (menor privilégio):
  `health,info` em prod. `show-details: never` em `/health` público.
- Logs estruturados em INFO para a aplicação, WARN para frameworks em prod.

## O que NÃO fazer

- ❌ Lógica de negócio no controller.
- ❌ Entidade JPA como response body.
- ❌ `ddl-auto: update` em qualquer ambiente.
- ❌ Segredo hardcoded ou commitado.
- ❌ Expor todos os endpoints do Actuator (`include: "*"`).
- ❌ Criar/alterar endpoint sem atualizar os testes de API ScanAPI (ver `api-tests.md`).

## Testes (dois níveis, ambos obrigatórios)

- **JUnit** para unidade/slice (`@WebMvcTest`, service puro) — ver `java-conventions.md`.
- **ScanAPI** para o contrato HTTP de ponta a ponta — ver `api-tests.md`.
  Todo endpoint novo ou alterado exige atualização de `api-tests/scanapi.yaml`.
