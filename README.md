# CampusGigs

API REST da plataforma de freelas entre alunos. Um aluno se cadastra e publica um serviço; outro aluno autenticado contrata esse serviço.

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

Na primeira subida a API cria o admin `admin@campusgigs.com` / `admin1234` (se o e-mail ainda não existir).

## Autenticação

Rotas públicas: `GET /`, `POST /usuarios`, `POST /auth/login`. O restante precisa de `Authorization: Bearer <token>`.

Sem token ou com token inválido: `401`. Sem permissão: `403`. Violação de regra de negócio: `409`. Validação: `400`. A resposta de erro é centralizada e não expõe stack trace.

| Endpoint | Quem acessa |
| --- | --- |
| `POST /usuarios` | Público. Papel gravado: `USER`. CEP consulta o ViaCEP |
| `POST /auth/login` | Público |
| `GET /usuarios/{id}` | autenticado |
| `GET /usuarios` | só `ADMIN` |
| `POST /servicos` | autenticado (vira o prestador) |
| `GET /servicos` | autenticado (só serviços `ATIVO`) |
| `GET /servicos/{id}` | autenticado |
| `PATCH /servicos/{id}/encerrar` | prestador do serviço (ou `ADMIN`) |
| `POST /servicos/{id}/contratacoes` | autenticado, que não seja o prestador |

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

### Login

`POST /auth/login`

```json
{
  "email": "ana@fiap.com.br",
  "senha": "senha1234"
}
```

### Publicar serviço

`POST /servicos`

```json
{
  "titulo": "Aulas de Java",
  "descricao": "Revisão para checkpoint",
  "categoria": "Tutoria",
  "preco": 50.00
}
```

Resposta `201`, situação `ATIVO`.

### Listar publicados

`GET /servicos` — só os `ATIVO`.

### Contratar

`POST /servicos/{id}/contratacoes`

Serviço que não está ativo, o próprio anúncio ou contratação duplicada devolvem `409`.

### Encerrar

`PATCH /servicos/{id}/encerrar`

Quem não é o prestador recebe `403`. Depois de encerrado, o serviço some da listagem e não aceita nova contratação.

## Banco de dados

| | |
| --- | --- |
| Host | `localhost:5432` |
| Database | `campusgigs` |
| Usuário | `campusgigs` |
| Senha | `campusgigs` |

O schema **não** é gerado pelo Hibernate (`ddl-auto=none`). Qualquer mudança de tabela entra como uma nova migration em `src/main/resources/db/migration`.

## Testes

Os testes usam H2 em memória (sem Docker). O ViaCEP é mockado:

```powershell
.\mvnw.cmd test
```

## Estrutura

```
br.com.fiap.campusgigs
├── client
├── config
├── controller
├── dto
├── exception
├── model
├── repository
├── security
└── service
```
