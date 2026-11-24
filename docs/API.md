# Documentação da API — CS Hub

- **Endereço base:** `http://localhost:8080/api`
- **Formato:** JSON (`Content-Type: application/json`)
- **Documentação interativa:** http://localhost:8080/swagger-ui.html
- **Especificação OpenAPI:** [openapi.json](openapi.json) (importável no Postman/Insomnia)

## Sumário

- [Padrão de respostas de erro](#padrão-de-respostas-de-erro)
- [Times](#times) — `/api/times`
- [Jogadores](#jogadores) — `/api/jogadores`
- [Partidas](#partidas) — `/api/partidas`
- [Mapas](#mapas) — `/api/mapas`
- [Armas](#armas) — `/api/armas`

---

## Padrão de respostas de erro

Todos os erros retornam o mesmo formato:

```json
{
  "timestamp": "2026-11-24T11:40:12.315",
  "status": 404,
  "erro": "Not Found",
  "mensagem": "Time com ID 99 não encontrado"
}
```

Em erros de validação (`400`), vem também o objeto `campos`, com a mensagem de cada campo inválido:

```json
{
  "timestamp": "2026-11-24T11:40:12.315",
  "status": 400,
  "erro": "Bad Request",
  "mensagem": "Existem campos inválidos. Verifique os dados enviados.",
  "campos": {
    "funcao": "A função deve ser AWPer, Entry, IGL, Suporte, Lurker ou Rifler",
    "rating": "O rating deve ser no máximo 3.0"
  }
}
```

| Código | Significado |
|---|---|
| `200 OK` | Consulta ou atualização realizada |
| `201 Created` | Registro criado |
| `204 No Content` | Registro excluído |
| `400 Bad Request` | Dados inválidos, JSON mal formatado, ID não numérico ou relacionamento inexistente |
| `404 Not Found` | Registro ou rota inexistente |
| `405 Method Not Allowed` | Método HTTP não suportado na rota |
| `409 Conflict` | Valor duplicado ou exclusão de registro em uso |
| `500 Internal Server Error` | Erro inesperado (detalhes apenas no log do servidor) |

---

## Times

| Campo | Tipo | Regras |
|---|---|---|
| `id` | número | Gerado pelo banco |
| `nome` | texto | Obrigatório, até 60 caracteres, único |
| `pais` | texto | Obrigatório, até 40 caracteres |
| `ranking` | inteiro | Obrigatório, de 1 a 500 |
| `anoFundacao` | inteiro | Obrigatório, de 1999 a 2100 |

| Método | Rota | Descrição | Respostas |
|---|---|---|---|
| `GET` | `/api/times` | Lista os times ordenados pelo ranking | `200` |
| `GET` | `/api/times/{id}` | Consulta um time | `200`, `400`, `404` |
| `POST` | `/api/times` | Cadastra um time | `201`, `400`, `409` |
| `PUT` | `/api/times/{id}` | Atualiza um time | `200`, `400`, `404`, `409` |
| `DELETE` | `/api/times/{id}` | Exclui um time sem jogadores e sem partidas | `204`, `404`, `409` |

**Parâmetro de rota:** `id` (inteiro) — identificador do time.

**Exemplo — cadastrar**

```http
POST /api/times
Content-Type: application/json

{
  "nome": "Imperial",
  "pais": "Brasil",
  "ranking": 30,
  "anoFundacao": 2018
}
```

Resposta `201 Created`:

```json
{
  "id": 5,
  "nome": "Imperial",
  "pais": "Brasil",
  "ranking": 30,
  "anoFundacao": 2018
}
```

**Exemplo — excluir time com jogadores** (`DELETE /api/times/1`) → `409 Conflict`:

```json
{
  "timestamp": "2026-11-24T11:42:00.101",
  "status": 409,
  "erro": "Conflict",
  "mensagem": "Não é possível excluir: o time possui jogadores vinculados. Transfira ou remova os jogadores antes."
}
```

---

## Jogadores

| Campo | Tipo | Regras |
|---|---|---|
| `id` | número | Gerado pelo banco |
| `nickname` | texto | Obrigatório, até 30 caracteres, único |
| `nomeReal` | texto | Obrigatório, até 80 caracteres |
| `nacionalidade` | texto | Obrigatório, até 40 caracteres |
| `funcao` | texto | `AWPer`, `Entry`, `IGL`, `Suporte`, `Lurker` ou `Rifler` |
| `rating` | decimal | Obrigatório, de 0.0 a 3.0 |
| `time` | objeto | Opcional. Enviar `{ "id": 1 }`. `null` = *free agent* |

| Método | Rota | Descrição | Respostas |
|---|---|---|---|
| `GET` | `/api/jogadores` | Lista os jogadores por nickname | `200` |
| `GET` | `/api/jogadores/{id}` | Consulta um jogador | `200`, `400`, `404` |
| `POST` | `/api/jogadores` | Cadastra um jogador | `201`, `400`, `409` |
| `PUT` | `/api/jogadores/{id}` | Atualiza (ou transfere) um jogador | `200`, `400`, `404`, `409` |
| `DELETE` | `/api/jogadores/{id}` | Exclui um jogador | `204`, `404` |

**Exemplo — cadastrar**

```http
POST /api/jogadores
Content-Type: application/json

{
  "nickname": "chelo",
  "nomeReal": "Marcelo Cespedes",
  "nacionalidade": "Brasil",
  "funcao": "Entry",
  "rating": 1.04,
  "time": { "id": 1 }
}
```

Resposta `201 Created` (o time volta completo):

```json
{
  "id": 9,
  "nickname": "chelo",
  "nomeReal": "Marcelo Cespedes",
  "nacionalidade": "Brasil",
  "funcao": "Entry",
  "rating": 1.04,
  "time": { "id": 1, "nome": "FURIA", "pais": "Brasil", "ranking": 12, "anoFundacao": 2017 }
}
```

Time inexistente (`"time": { "id": 999 }`) → `400`: `"O time informado (ID 999) não existe"`.

---

## Partidas

| Campo | Tipo | Regras |
|---|---|---|
| `id` | número | Gerado pelo banco |
| `campeonato` | texto | Obrigatório, até 80 caracteres |
| `data` | data | Obrigatório, formato `AAAA-MM-DD` |
| `timeA` | objeto | Obrigatório, `{ "id": 1 }`, deve existir |
| `timeB` | objeto | Obrigatório, `{ "id": 2 }`, deve existir e ser diferente do time A |
| `mapa` | objeto | Obrigatório, `{ "id": 1 }`, deve existir |
| `placarA` | inteiro | Obrigatório, de 0 a 50 |
| `placarB` | inteiro | Obrigatório, de 0 a 50 |

| Método | Rota | Descrição | Respostas |
|---|---|---|---|
| `GET` | `/api/partidas` | Lista as partidas (mais recentes primeiro) | `200` |
| `GET` | `/api/partidas/{id}` | Consulta uma partida | `200`, `400`, `404` |
| `POST` | `/api/partidas` | Registra uma partida | `201`, `400` |
| `PUT` | `/api/partidas/{id}` | Atualiza uma partida | `200`, `400`, `404` |
| `DELETE` | `/api/partidas/{id}` | Exclui uma partida | `204`, `404` |

**Exemplo — registrar**

```http
POST /api/partidas
Content-Type: application/json

{
  "campeonato": "IEM Rio 2026",
  "data": "2026-10-12",
  "timeA": { "id": 1 },
  "timeB": { "id": 3 },
  "mapa": { "id": 1 },
  "placarA": 13,
  "placarB": 9
}
```

Resposta `201 Created`:

```json
{
  "id": 4,
  "campeonato": "IEM Rio 2026",
  "data": "2026-10-12",
  "timeA": { "id": 1, "nome": "FURIA", "pais": "Brasil", "ranking": 12, "anoFundacao": 2017 },
  "timeB": { "id": 3, "nome": "Natus Vincere", "pais": "Ucrânia", "ranking": 3, "anoFundacao": 2009 },
  "mapa": { "id": 1, "nome": "Mirage", "modo": "Competitivo", "poolAtivo": true, "anoLancamento": 2013 },
  "placarA": 13,
  "placarB": 9
}
```

Mesmo time nos dois lados → `400`: `"O time A e o time B devem ser diferentes"`.

---

## Mapas

| Campo | Tipo | Regras |
|---|---|---|
| `id` | número | Gerado pelo banco |
| `nome` | texto | Obrigatório, até 40 caracteres, único |
| `modo` | texto | `Competitivo`, `Wingman` ou `Casual` |
| `poolAtivo` | booleano | Obrigatório (`true`/`false`) |
| `anoLancamento` | inteiro | Obrigatório, de 1999 a 2100 |

| Método | Rota | Descrição | Respostas |
|---|---|---|---|
| `GET` | `/api/mapas` | Lista os mapas em ordem alfabética | `200` |
| `GET` | `/api/mapas/{id}` | Consulta um mapa | `200`, `400`, `404` |
| `POST` | `/api/mapas` | Cadastra um mapa | `201`, `400`, `409` |
| `PUT` | `/api/mapas/{id}` | Atualiza um mapa | `200`, `400`, `404`, `409` |
| `DELETE` | `/api/mapas/{id}` | Exclui um mapa que não está em partidas | `204`, `404`, `409` |

**Exemplo — cadastrar**

```http
POST /api/mapas
Content-Type: application/json

{ "nome": "Anubis", "modo": "Competitivo", "poolAtivo": true, "anoLancamento": 2020 }
```

Resposta `201 Created`:

```json
{ "id": 8, "nome": "Anubis", "modo": "Competitivo", "poolAtivo": true, "anoLancamento": 2020 }
```

---

## Armas

| Campo | Tipo | Regras |
|---|---|---|
| `id` | número | Gerado pelo banco |
| `nome` | texto | Obrigatório, até 40 caracteres, único |
| `categoria` | texto | `Pistola`, `SMG`, `Rifle`, `Sniper` ou `Pesada` |
| `preco` | inteiro | Obrigatório, de 0 a 10000 |
| `lado` | texto | `TR`, `CT` ou `Ambos` |
| `dano` | inteiro | Obrigatório, de 1 a 500 |

| Método | Rota | Descrição | Respostas |
|---|---|---|---|
| `GET` | `/api/armas` | Lista as armas por categoria e preço | `200` |
| `GET` | `/api/armas/{id}` | Consulta uma arma | `200`, `400`, `404` |
| `POST` | `/api/armas` | Cadastra uma arma | `201`, `400`, `409` |
| `PUT` | `/api/armas/{id}` | Atualiza uma arma | `200`, `400`, `404`, `409` |
| `DELETE` | `/api/armas/{id}` | Exclui uma arma | `204`, `404` |

**Exemplo — cadastrar**

```http
POST /api/armas
Content-Type: application/json

{ "nome": "Galil AR", "categoria": "Rifle", "preco": 1800, "lado": "TR", "dano": 30 }
```

Resposta `201 Created`:

```json
{ "id": 10, "nome": "Galil AR", "categoria": "Rifle", "preco": 1800, "lado": "TR", "dano": 30 }
```

**Exemplo — dados inválidos** → `400`:

```json
{
  "timestamp": "2026-11-24T11:45:31.002",
  "status": 400,
  "erro": "Bad Request",
  "mensagem": "Existem campos inválidos. Verifique os dados enviados.",
  "campos": {
    "preco": "O preço não pode ser negativo",
    "lado": "O lado deve ser TR, CT ou Ambos"
  }
}
```
