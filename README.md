# URL Shortener API

API REST para encurtamento de URLs com suporte a expiração, redirecionamento, contagem de cliques e consulta paginada dos links cadastrados.

## O que o projeto faz

Este projeto permite:

- Criar uma short URL a partir de uma `originalUrl`.
- Redirecionar para a URL original via short code.
- Consultar detalhes de uma short URL por id (short code).
- Listar todas as URLs encurtadas (paginado).
- Proteger o endpoint de criação com API Key (`X-API-Key`).

## Como funciona a lógica de encurtar a URL

Fluxo de criação:

1. O cliente envia `originalUrl` (obrigatória) e `expirationDate` (opcional) em `POST /api/v1/urls`.
2. A URL é persistida no banco com ID numérico auto gerado.
3. O ID numérico é ofuscado com XOR usando um segredo configurável (`url-shortener.obfuscation-secret`).
4. O valor ofuscado é convertido para Base62 (`0-9A-Za-z`), gerando o `shortCode`.
5. O `shortCode` é salvo e retornado para o cliente junto com a short URL completa (`baseUrl + shortCode`).

Fluxo de redirecionamento:

1. Cliente chama `GET /api/v1/{shortCode}`.
2. A API decodifica Base62 para número.
3. Remove a ofuscação (XOR com o mesmo segredo) para recuperar o ID original.
4. Busca no banco.
5. Se existir e não estiver expirada, incrementa `clickCount` e retorna `302 Found` com `Location` apontando para `originalUrl`.
6. Se não existir, retorna erro de não encontrado.
7. Se estiver expirada, retorna erro `410 Gone`.

## Tecnologias usadas

- Java 21
- Spring Boot 3.5.4
- Spring Web
- Spring Data JPA
- Jakarta Validation
- H2 Database (memória)
- Lombok
- JUnit 5 / Spring Boot Test / MockMvc
- Maven Wrapper (`./mvnw`)

## Requisitos para rodar localmente

- JDK 21 instalado
- (Opcional) Maven instalado — **não obrigatório** se usar `./mvnw`

## Configurações principais

Arquivo: `src/main/resources/application.yaml`

Variáveis importantes:

- `URL_SHORTENER_SECRET` (default: `123456`) — segredo usado na ofuscação do ID.
- `URL_SHORTENER_BASE_PATH` (default: `https://short.lee/`) — basepath/base URL para montar a URL curta de resposta.
- `API_KEY` (default: `token-x-men`) — valor esperado no header `X-API-Key` no POST de criação.

## Como rodar o projeto no computador

```bash
./mvnw spring-boot:run
```

A API sobe por padrão em:

- `http://localhost:8080`

## Como rodar os testes

Todos os testes:

```bash
./mvnw test
```

Somente testes de integração:

```bash
./mvnw -Dtest=UrlShortenerIntegrationTest test
```

## Como acessar o console do H2 no navegador: 

1. Acesse seu navegador e insira: http://localhost:8080/h2-console

2. No campo "JDBC URL" insira: jdbc:h2:mem:urlshortener

3. Clique em "Conect"

## Autenticação (header obrigatório no POST)

Para criar short URL, envie:

- Header: `X-API-Key: token-x-men`

> Observação: endpoints GET não exigem esse header.

## Endpoints

### 1) Criar short URL

`POST /api/v1/urls`

Headers:

- `Content-Type: application/json`
- `X-API-Key: token-x-men`

Body de exemplo:

```json
{
  "originalUrl": "https://www.exemplo.com/produto/123",
  "expirationDate": "2026-12-31T23:59:59"
}
```

`expirationDate` é opcional. Se não for enviada, será `null` e o short code **não expira**.

Exemplo com `curl`:

```bash
curl -X POST 'http://localhost:8080/api/v1/urls' \
  -H 'Content-Type: application/json' \
  -H 'X-API-Key: token-x-men' \
  -d '{
    "originalUrl": "https://www.exemplo.com/produto/123",
    "expirationDate": "2026-12-31T23:59:59"
  }'
```

### 2) Redirecionar pelo short code

`GET /api/v1/{shortCode}`

Exemplo:

```bash
curl -i 'http://localhost:8080/api/v1/W7H'
```

Resposta esperada em sucesso:

- Status `302 Found`
- Header `Location: <originalUrl>`

### 3) Buscar detalhes por id (short code)

`GET /api/v1/urls/{id}`

Exemplo:

```bash
curl 'http://localhost:8080/api/v1/urls/W7H'
```

### 4) Listar todas as short URLs cadastradas

`GET /api/v1/urls`

Exemplo:

```bash
curl 'http://localhost:8080/api/v1/urls?page=0&size=10'
```

## Formato de erro

Estrutura padrão de erro:

```json
{
  "error": "<CÓDIGO>",
  "message": "<mensagem>",
  "status": 400,
  "timestamp": "2026-03-11T07:48:57.8994745",
  "validations": {
    "campo": "mensagem de validação"
  }
}
```

Principais códigos:

- `VALIDATION_ERROR` (400)
- `URL_NOT_FOUND` (404)
- `URL_EXPIRED` (410)
- `UNAUTHORIZED` (401)
- `INTERNAL_SERVER_ERROR` (500)

Exemplo de URL expirada:

```json
{
  "error": "URL_EXPIRED",
  "message": "Short URL expired: W7H",
  "status": 410,
  "timestamp": "2026-03-11T07:48:57.8994745",
  "validations": null
}
```

## Política de expiração

- O usuário pode escolher uma `expirationDate`.
- Se `expirationDate` não for enviada, ela é armazenada como `null`.
- Quando `expirationDate` é `null`, o short code não expira.
- Se a URL estiver expirada no momento do acesso, a API retorna `410 Gone` com erro `URL_EXPIRED`.

## Requisitos funcionais

- `originalUrl` é obrigatória.
- `originalUrl` não pode ser vazia ou nula.
- `originalUrl` deve começar com `http://` ou `https://`.
- `expirationDate` é opcional.
- Ao receber um id válido em `GET /api/v1/{shortCode}`, deve redirecionar para a `originalUrl`.
- Se o id não existir, deve retornar erro apropriado (`URL_NOT_FOUND`, status 404).
- Se a URL estiver expirada, deve retornar erro `URL_EXPIRED` (status 410) no formato definido.

## Observações

- O campo `id` nas respostas representa o próprio short code gerado.
- A URL curta completa retornada é montada com `URL_SHORTENER_BASE_PATH + shortCode`.