# Segurança

Regras de desenvolvimento seguro para este projeto. Valem para todo código
gerado ou alterado por humanos e por agentes Kiro.

## Segredos e configuração

- **Nunca** commite segredos (senhas, tokens, chaves). `.gitignore` já bloqueia
  `.env`, `*.pem`, `*.key`, `application-local.*`.
- Em produção, segredos vêm de variáveis de ambiente (ver `application-prod.yml`)
  ou de um gerenciador de segredos (AWS Secrets Manager, Vault).
- Não logue segredos nem PII. Mascare valores sensíveis em logs.

## Validação de entrada

- Valide toda entrada externa na borda (Jakarta Bean Validation nos controllers).
- Trate parâmetros de usuário como não confiáveis: limite tamanho, formato e faixa.
- Prefira DTOs (records) a `Map`/entidades cruas no corpo da request.

## Banco de dados

- **Sempre** use queries parametrizadas (Spring Data / JPA já fazem; nunca concatene
  SQL com entrada do usuário).
- `ddl-auto: validate` — o schema é controlado só por Flyway.
- Pool de conexões com limite por ambiente (evita exaustão de recursos).

## Exposição e respostas

- Respostas de erro **não** vazam stack trace, mensagem interna ou detalhe de schema
  (`server.error.include-stacktrace=never`, `include-message=never`).
- Actuator expõe o mínimo (`health,info`); `health` sem detalhes para público.
- Desabilite o console H2 em qualquer ambiente acessível externamente.

## Autenticação/Autorização (quando adicionar)

- Adicione `spring-boot-starter-security`; negue por padrão e libere rotas explicitamente.
- Para APIs, prefira tokens (OAuth2/JWT) a sessão; valide assinatura e expiração.
- Guarde o contexto de segurança no `SecurityContextHolder` (funciona com virtual threads).
- **Spring Security 7 (Boot 4.x)** muda defaults — revise ao migrar.

## Dependências

- Fixe versões (o `spring-boot-starter-parent` já gerencia as transitivas).
- Rode verificação de vulnerabilidades (ex.: OWASP Dependency-Check, Snyk) no CI.
- Desconfie de pacotes com nome suspeito (typosquatting) antes de adicionar.

## Concorrência segura

- Evite `synchronized` em volta de I/O (pinning de virtual thread em Java 21);
  use `ReentrantLock`/`Semaphore`. Ver `java-conventions.md` e `tech.md`.

## Checklist antes de abrir PR

- [ ] Nenhum segredo no diff.
- [ ] Entrada externa validada.
- [ ] Sem SQL concatenado com entrada do usuário.
- [ ] Erros não vazam detalhes internos.
- [ ] Dependências novas justificadas e com versão fixada.
