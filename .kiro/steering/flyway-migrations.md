# Migrações de Banco com Flyway

Flyway é a **fonte única da verdade** do schema. Hibernate roda com
`ddl-auto: validate` — ele valida, não cria nem altera tabelas.

## Convenção de nomes

```
V<versao>__<descricao>.sql     # migração versionada (ex.: V2__add_user_table.sql)
R__<descricao>.sql             # migração repetível (views, procs) — roda quando muda
```

- Dois underscores (`__`) separam versão e descrição.
- Versão crescente e única: `V1`, `V2`, `V3`... (ou `V1_1`, `V2_0_1`).
- Descrição em `snake_case`, curta e clara.
- Local: `src/main/resources/db/migration/`.

## Regra de ouro (inviolável)

> **Nunca edite uma migração que já foi aplicada em qualquer ambiente.**

O Flyway grava um checksum de cada script em `flyway_schema_history`. Editar um
script já aplicado quebra a validação de checksum e falha o startup em todos os
ambientes onde ele já rodou. Para corrigir/alterar, **crie uma nova migração**.

## Boas práticas

- Uma migração = uma mudança lógica e coesa.
- Migrações são **imutáveis e versionadas em git** junto com o código que as usa.
- Prefira mudanças aditivas e compatíveis (adicionar coluna nullable, nova tabela)
  para permitir deploy sem downtime; remoções vêm em passo posterior.
- Para dados grandes/índices, pense em impacto de lock e tempo de execução.
- Teste migrações contra um banco igual ao de produção (Testcontainers PostgreSQL).
- Mantenha dialeto compatível entre dev (H2 em `MODE=PostgreSQL`) e prod (PostgreSQL),
  ou use migrações específicas por vendor se necessário.
- `baseline-on-migrate: true` só em dev/para bancos pré-existentes; em prod,
  decida conscientemente a baseline.

## Fluxo para uma nova mudança de schema

1. Crie `V{proximo}__<descricao>.sql` em `db/migration/`.
2. Escreva o DDL (e DML, se necessário) idempotente quando fizer sentido.
3. Atualize/crie a entidade JPA correspondente (o `validate` vai conferir).
4. Rode `./mvnw verify` — o smoke test sobe o contexto e aplica a migração.
5. Nunca volte e edite migrações anteriores.
