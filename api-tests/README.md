# Testes de API (ScanAPI)

Testes de **integração de API** de caixa-preta com [ScanAPI](https://scanapi.dev):
exercitam a aplicação **em execução** via HTTP real e geram **documentação viva**
(relatório HTML) a cada execução.

Esses testes são **complementares** (não substitutos) aos testes JUnit do módulo
Java. JUnit valida unidades e slices internos; o ScanAPI valida o **contrato HTTP
observável** de ponta a ponta.

## Pré-requisitos

- **Python 3.9+** e ScanAPI instalado (CLI isolada, recomendado via pipx):
  ```bash
  pipx install scanapi      # ou: pip install scanapi
  scanapi --version
  ```

## Como rodar

O ScanAPI testa a API **no ar**, então a aplicação precisa estar rodando.

```bash
# 1) Suba a aplicação (em um terminal), na raiz do projeto:
./mvnw spring-boot:run           # expõe http://localhost:8080

# 2) Em outro terminal, rode a suíte (BASE_URL default: http://localhost:8080):
BASE_URL=http://localhost:8080 scanapi run api-tests/scanapi.yaml -o api-tests/scanapi-report.html

# 3) Abra o relatório (documentação viva):
open api-tests/scanapi-report.html      # macOS
```

Alternativa: de dentro de `api-tests/` basta `scanapi run` (usa `scanapi.conf`,
que já define `BASE_URL`). Rodando da raiz do repositório, exporte `BASE_URL`
(como acima), pois o ScanAPI procura o `scanapi.conf` no diretório atual.

A URL base vem da variável `BASE_URL` (default `http://localhost:8080`). Para
apontar para outro ambiente:

```bash
BASE_URL=https://staging.exemplo.com scanapi run api-tests/scanapi.yaml
```

## O que é testado

| Requisição                        | Verifica |
|-----------------------------------|----------|
| `GET /actuator/health`            | status 200 e `status == "UP"` |
| `GET /api/hello?name=Ada`         | 200, `Content-Type` JSON, `message`/`recipient`, `timestamp` presente |
| `GET /api/hello` (sem nome)       | fallback para `World` |
| `GET /api/hello?name="  Linus  "` | nome é "trimado" |
| `GET /api/hello?name=<101 chars>` | **400**, corpo de erro com `status`/`messages`, sem vazar stacktrace |

Cobre **caminho feliz, defaults e o contrato de erro** — não apenas 200.

## Convenções da spec (importante)

- **Query params vão na chave `params:`**, nunca embutidos no `path`
  (o ScanAPI descarta querystring no `path`).
- Asserts são expressões Python dentro de `${{ ... }}`; `response` é um
  objeto `requests.Response` (use `response.status_code`, `response.json()`,
  `response.headers`).
- Variáveis de ambiente: `${BASE_URL}`. Mantenha segredos fora do arquivo —
  injete via ambiente.
- A **spec é versionada** no git; o **relatório `scanapi-report.html` NÃO**
  (está no `.gitignore`) — ele é regenerado a cada execução.

## Ao adicionar/alterar um endpoint

É **obrigatório** atualizar esta spec (ver `.kiro/steering/api-tests.md`):

1. Adicione a `request` correspondente em `scanapi.yaml`.
2. Inclua testes de caminho feliz **e** de erro/validação.
3. Rode a suíte com a app no ar e garanta 100% verde.
4. Faça commit da spec atualizada (sem o relatório).

## CI (sugestão)

No pipeline: subir a app (ou um container), aguardar o `/actuator/health`,
exportar `BASE_URL` e rodar `scanapi run`. O processo retorna código de saída
não-zero se qualquer asserção falhar, quebrando o build.
