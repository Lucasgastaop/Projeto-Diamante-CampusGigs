# CampusGigs

API REST da plataforma de freelas entre alunos. Um aluno se cadastra e publica um serviço; outro aluno autenticado contrata esse serviço.

Este commit cobre o **Checkpoint 4**: autorização por papéis. Cadastro vira `USER`; só `ADMIN` lista todos os usuários.

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

Na primeira subida a API cria o admin `admin@campusgigs.com` / `admin1234` (se o e-mail ainda não existir).

## Endpoints (CP4)

Rotas públicas: `GET /`, `POST /usuarios`, `POST /auth/login`. O restante precisa do token.

| Endpoint | Quem acessa |
| --- | --- |
| `POST /usuarios` | Público. Papel gravado: `USER` |
| `POST /auth/login` | Público |
| `GET /usuarios/{id}` | `USER` ou `ADMIN` autenticado |
| `GET /usuarios` | só `ADMIN` (`403` para `USER`) |

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

Resposta `201`. A senha **não** volta no JSON. O papel é sempre `USER`.

### Login

`POST /auth/login`

```json
{
  "email": "ana@fiap.com.br",
  "senha": "senha1234"
}
```

Resposta `200` com `token`, `tipo: Bearer` e `usuario` (incluindo `papel`).

### Consulta por id

`GET /usuarios/{id}` — header `Authorization: Bearer <token>`. `200`, `401` ou `404`.

### Listagem (admin)

`GET /usuarios`

```powershell
# login do admin
curl -i -X POST http://localhost:8080/auth/login -H "Content-Type: application/json" -d "{\"email\":\"admin@campusgigs.com\",\"senha\":\"admin1234\"}"

# listar — só passa com o token do ADMIN
curl -i http://localhost:8080/usuarios -H "Authorization: Bearer COLAR_TOKEN_ADMIN"
```

Token de `USER` nesta rota devolve `403`.

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
