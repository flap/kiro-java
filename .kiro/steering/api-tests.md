# Testes de API (ScanAPI) — OBRIGATÓRIO

Todo endpoint HTTP exposto por este projeto **deve** ter testes de integração de
API escritos com [ScanAPI](https://scanapi.dev), além dos testes JUnit.

- **JUnit** (`src/test`) → unidades e slices internos (rápido, sem rede).
- **ScanAPI** (`api-tests/`) → contrato HTTP observável, app no ar, ponta a ponta,
  e **documentação viva** (relatório HTML).

Os dois são complementares e ambos são exigidos.

## Regra de obrigatoriedade

Ao **criar ou alterar** qualquer endpoint (novo path, novo método, mudança de
resposta, novo código de status, nova validação), é obrigatório:

1. Adicionar/atualizar a `request` correspondente em `api-tests/scanapi.yaml`.
2. Cobrir **caminho feliz E contrato de erro** (ex.: 400 de validação, 404, 409).
   Não aceite uma feature com apenas o teste de 200.
3. Rodar a suíte com a aplicação no ar e garantir **100% verde** antes de concluir.
4. Versionar a spec atualizada (o relatório `scanapi-report.html` NÃO vai ao git).

Uma mudança de endpoint sem atualização correspondente em `scanapi.yaml` é
considerada **incompleta**.

## Localização

```
api-tests/
├── scanapi.yaml           # spec dos testes (versionada)
├── scanapi.conf           # config (spec_path, output_path, env_vars)
├── scanapi-report.html    # relatório gerado (NÃO versionar — .gitignore)
└── README.md              # como rodar
```

## Como rodar (a app precisa estar no ar)

```bash
./mvnw spring-boot:run                                   # terminal 1
BASE_URL=http://localhost:8080 scanapi run api-tests/scanapi.yaml -o api-tests/scanapi-report.html  # terminal 2
```

A URL base vem de `${BASE_URL}`. Rodando da raiz do repo, exporte-a (como acima);
de dentro de `api-tests/`, o `scanapi.conf` já fornece o default.

## Convenções da spec (evitam erros comuns)

- **Query params SEMPRE na chave `params:`**, nunca inline no `path`. O ScanAPI
  descarta a querystring embutida no `path` — o teste passaria batendo no endpoint
  errado. (Esse erro já ocorreu uma vez neste projeto; não repita.)
- Asserts são Python dentro de `${{ ... }}`. `response` é um `requests.Response`:
  use `response.status_code`, `response.json()["campo"]`, `response.headers[...]`.
- Teste também o que **não** deve aparecer (ex.: `"trace" not in response.json()`)
  para garantir que erros não vazam detalhes internos — alinhado a `security.md`.
- **Nenhum segredo** na spec; injete via variável de ambiente.
- Para cenários encadeados, use a resposta de uma request para montar a próxima
  (recurso nativo do ScanAPI via `vars`).

## Definição de pronto (endpoint)

- [ ] Testes JUnit (unitário/slice) passando.
- [ ] `request` no `scanapi.yaml` com asserts de sucesso e de erro.
- [ ] `scanapi run` 100% verde contra a app no ar.
- [ ] Spec versionada; relatório fora do git.
