# CampusGigs

API REST da plataforma de freelas entre alunos. Um aluno se cadastra e publica um serviço; outro aluno autenticado contrata esse serviço.

Este commit cobre o **Checkpoint 1**: ambiente Docker + primeira migration Flyway com o schema inicial.

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
├── model
├── repository
├── security
└── service
```
