# CampusGigs

API REST da plataforma de freelas entre alunos. Um aluno se cadastra e publica um serviço; outro aluno autenticado contrata esse serviço.

Este commit cobre o **Checkpoint 3**: login devolve um JWT e as rotas protegidas exigem `Authorization: Bearer`.

## Requisitos

- Java 21+
- Docker Desktop (o Postgres sobe sozinho com a aplicação)
- Maven 3.9+ (ou use o wrapper `mvnw.cmd`)

## Como executar

Com o Docker Desktop ligado:

```powershell
.\mvnw.cmd spring-boot:run
```

O Spring Boot detecta o `docker-compose.yml`, sobe o PostgreSQL e aplica a migration `V1__create_schema_inicial.sql`. A API fica em [http://localhost:8080](http://localhost:8080).

Health check: `GET /`

Se preferir subir só o banco antes:

```powershell
docker compose up -d
.\mvnw.cmd spring-boot:run
```

## Endpoints (CP3)

Rotas públicas: `GET /`, `POST /usuarios`, `POST /auth/login`. O restante precisa do token.

### Cadastro

`POST /usuarios`

```json
{
  "nome": "Ana Souza",
  "email": "ana@fiap.com.br",
  "senha": "senha1234",
  "cep": "01310100"
}
```

Resposta `201` com o usuário criado. A senha **não** volta no JSON. O papel é sempre `USER`.

### Login

`POST /auth/login`

```json
{
  "email": "ana@fiap.com.br",
  "senha": "senha1234"
}
```

Resposta `200`:

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tipo": "Bearer",
  "usuario": {
    "id": 1,
    "nome": "Ana Souza",
    "email": "ana@fiap.com.br",
    "papel": "USER"
  }
}
```

Credencial errada devolve `401`.

### Consulta

`GET /usuarios/{id}` — envie o header `Authorization: Bearer <token>`. `200`, `401` ou `404`.

```powershell
curl -i http://localhost:8080/usuarios/1 -H "Authorization: Bearer COLAR_TOKEN_AQUI"
```

## Banco de dados

| | |
| --- | --- |
| Host | `localhost:5432` |
| Database | `campusgigs` |
| Usuário | `campusgigs` |
| Senha | `campusgigs` |

O schema **não** é gerado pelo Hibernate (`ddl-auto=none`). Qualquer mudança de tabela entra como uma nova migration em `src/main/resources/db/migration`.

## Testes

Os testes usam H2 em memória (sem Docker):

```powershell
.\mvnw.cmd test
```

## Estrutura

```
br.com.fiap.campusgigs
├── config
├── controller
├── dto
├── exception
├── model
├── repository
├── security
└── service
```
