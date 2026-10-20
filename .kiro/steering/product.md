# Produto

## Visão Geral

Projeto **Hello World** em Java 21 com Spring Boot, servindo como **template de
referência** de melhores práticas para desenvolvimento seguro e performático.
O objetivo não é a funcionalidade em si, mas demonstrar, de forma mínima e
executável, os padrões que devem ser replicados em projetos reais.

## Propósito

- Servir de ponto de partida para novos serviços Java/Spring Boot.
- Codificar, em steering files e código, as convenções do time.
- Permitir que qualquer desenvolvedor usando Kiro baixe o projeto e continue
  (ou aplique os mesmos padrões a um projeto existente) sem reaprender as regras.

## Funcionalidade Demo

- `GET /api/hello?name=<nome>` → retorna uma saudação em JSON.
- `name` é opcional (fallback para "World") e validado (máx. 100 caracteres).
- Endpoints de gestão: `/actuator/health` e `/actuator/info`.

## Fora de Escopo

- Autenticação/autorização completas (o template mostra onde entraria).
- Persistência de dados de negócio (há apenas uma migração de exemplo).
- Pipeline de CI/CD (documentado como próximo passo, não implementado).

## Público-alvo

Desenvolvedores backend Java e agentes Kiro que vão evoluir este projeto ou
adotar seus padrões em outro lugar.
