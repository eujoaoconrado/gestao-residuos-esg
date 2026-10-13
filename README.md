# Gestao de Residuos e Reciclagem (ESG)

Microsservico RESTful desenvolvido em Spring Boot 3 para a atividade da **FIAP - Fase 3 - Frameworks Java - Cap 8 (Microsservicos com Spring)**.

## Tema ESG

**Gestao de Residuos e Reciclagem** (pilar Environmental).

O sistema gerencia pontos de coleta seletiva, registra residuos depositados, agenda e realiza coletas, alem de gerar alertas automaticos quando um ponto de coleta atinge 80% da capacidade. Tambem fornece indicadores agregados de reciclagem para apoiar tomada de decisao.

## Integrante

- **Joao Paulo Conrado Silva**
- **Grupo:** 118
- **RM:** 562593

## Stack utilizada

| Camada | Tecnologia |
|--------|------------|
| Linguagem | Java 17 |
| Framework | Spring Boot 3.2 |
| Web | Spring Web (REST) |
| Persistencia | Spring Data JPA + Hibernate |
| Banco de dados | Oracle XE 21 (driver ojdbc11) |
| Migracoes | Flyway |
| Seguranca | Spring Security + JWT (jjwt) |
| Validacao | Bean Validation (Hibernate Validator) |
| Documentacao | SpringDoc OpenAPI / Swagger UI |
| Build | Maven |
| Utilitarios | Lombok |
| Container | Docker + docker-compose |

## Estrutura de pastas

```
gestao-residuos-esg/
├── src/
│   └── main/
│       ├── java/br/com/fiap/gestaoresiduos/
│       │   ├── GestaoResiduosApplication.java
│       │   ├── config/          (Security, OpenAPI, DataSeeder)
│       │   ├── controller/      (REST controllers)
│       │   ├── dto/             (DTOs de request e response)
│       │   ├── exception/       (RuntimeException + handler global)
│       │   ├── mapper/          (Conversores entity <-> DTO manuais)
│       │   ├── model/           (Entidades JPA + enums)
│       │   ├── repository/      (Spring Data Repositories)
│       │   ├── security/        (JwtService + filtro)
│       │   └── service/         (Regras de negocio)
│       └── resources/
│           ├── application.yml
│           └── db/migration/
│               ├── V1__create_tables.sql
│               └── V2__insert_seed_data.sql
├── Dockerfile
├── docker-compose.yml
├── .dockerignore
├── pom.xml
├── README.md
└── insomnia_collection.json
```

## Como executar com Docker

Pre-requisitos: Docker Desktop instalado e rodando.

```bash
docker-compose up --build
```

O comando faz:
1. Sobe o Oracle XE 21 e aguarda o healthcheck ficar saudavel (pode levar de 1 a 3 minutos na primeira vez).
2. Builda a imagem da aplicacao (multi-stage Maven + JRE).
3. Sobe o microsservico na porta `8080`.

Apos subir:
- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Oracle: `localhost:1521` (service `XEPDB1`, user `system`, senha `oracle`)

Para parar:
```bash
docker-compose down
```

Para resetar tudo (inclui dados do banco):
```bash
docker-compose down -v
```

## Como executar localmente (sem Docker)

Pre-requisitos: Java 17, Maven 3.9+, instancia Oracle XE acessivel.

```bash
# Defina as variaveis de ambiente apontando pro seu Oracle
export ORACLE_URL="jdbc:oracle:thin:@//localhost:1521/XEPDB1"
export ORACLE_USER="system"
export ORACLE_PASSWORD="oracle"

mvn spring-boot:run
```

## Usuarios padroes (criados no startup)

| Email | Senha | Role |
|-------|-------|------|
| `admin@fiap.com` | `admin123` | ADMIN |
| `user@fiap.com` | `user123` | USER |

Voce tambem pode registrar novos usuarios em `POST /auth/register`.

## Tabela de endpoints

### Autenticacao (publico)

| Metodo | Endpoint | Descricao | Role |
|--------|----------|-----------|------|
| POST | `/auth/register` | Registra novo usuario | publico |
| POST | `/auth/login` | Autentica e retorna JWT | publico |

### Empresas

| Metodo | Endpoint | Descricao | Role |
|--------|----------|-----------|------|
| GET | `/empresas` | Lista empresas (paginado) | USER/ADMIN |
| GET | `/empresas/{id}` | Busca empresa por id | USER/ADMIN |
| POST | `/empresas` | Cadastra empresa | ADMIN |
| PUT | `/empresas/{id}` | Atualiza empresa | ADMIN |
| DELETE | `/empresas/{id}` | Remove empresa | ADMIN |

### Pontos de coleta

| Metodo | Endpoint | Descricao | Role |
|--------|----------|-----------|------|
| GET | `/pontos-coleta` | Lista pontos (paginado) | USER/ADMIN |
| GET | `/pontos-coleta/{id}` | Busca por id | USER/ADMIN |
| GET | `/pontos-coleta/alertas` | Pontos com 80%+ da capacidade | USER/ADMIN |
| POST | `/pontos-coleta` | Cria ponto | ADMIN |
| PUT | `/pontos-coleta/{id}` | Atualiza ponto | ADMIN |
| DELETE | `/pontos-coleta/{id}` | Remove ponto | ADMIN |

### Residuos

| Metodo | Endpoint | Descricao | Role |
|--------|----------|-----------|------|
| GET | `/residuos` | Lista residuos (paginado) | USER/ADMIN |
| GET | `/residuos/{id}` | Busca por id | USER/ADMIN |
| GET | `/residuos/por-tipo/{tipo}` | Agrupa por tipo (PLASTICO, PAPEL, VIDRO, METAL, ORGANICO, ELETRONICO) | USER/ADMIN |
| POST | `/residuos` | Registra residuo | ADMIN |
| PUT | `/residuos/{id}` | Atualiza residuo | ADMIN |
| DELETE | `/residuos/{id}` | Remove residuo | ADMIN |

### Coletas

| Metodo | Endpoint | Descricao | Role |
|--------|----------|-----------|------|
| GET | `/coletas` | Lista coletas (paginado) | USER/ADMIN |
| GET | `/coletas/{id}` | Busca por id | USER/ADMIN |
| GET | `/coletas/historico/{empresaId}` | Historico por empresa | USER/ADMIN |
| POST | `/coletas` | Cria coleta avulsa | ADMIN |
| POST | `/coletas/agendar` | Agenda nova coleta | ADMIN |
| PUT | `/coletas/{id}` | Atualiza coleta | ADMIN |
| PUT | `/coletas/{id}/realizar` | Marca como realizada e zera o ponto | ADMIN |
| DELETE | `/coletas/{id}` | Remove coleta | ADMIN |

### Indicadores

| Metodo | Endpoint | Descricao | Role |
|--------|----------|-----------|------|
| GET | `/indicadores/reciclagem` | Indicadores gerais agregados | USER/ADMIN |

## Exemplo de uso

### 1. Registrar usuario (opcional, ja existe o admin)

```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Joao Paulo",
    "email": "joao@fiap.com",
    "senha": "senha123",
    "role": "USER"
  }'
```

### 2. Fazer login e obter token

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@fiap.com",
    "senha": "admin123"
  }'
```

Resposta:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tipo": "Bearer",
  "email": "admin@fiap.com",
  "role": "ADMIN",
  "expiracaoMs": 86400000
}
```

### 3. Requisicao autenticada (criar ponto de coleta)

```bash
TOKEN="eyJhbGciOiJIUzI1NiJ9..."

curl -X POST http://localhost:8080/pontos-coleta \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Ecoponto Oeste",
    "endereco": "Av. Paulista, 1500",
    "tipoResiduo": "PLASTICO",
    "capacidadeMaximaKg": 500,
    "nivelAtualKg": 0,
    "empresaId": 1
  }'
```

### 4. Verificar alertas de pontos com 80%+ da capacidade

```bash
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/pontos-coleta/alertas
```

### 5. Indicadores agregados

```bash
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/indicadores/reciclagem
```

## Tratamento de erros

Todas as respostas de erro seguem o formato:

```json
{
  "timestamp": "2026-05-25T20:15:30",
  "status": 400,
  "error": "Bad Request",
  "message": "Dados invalidos na requisicao",
  "path": "/empresas",
  "detalhes": ["cnpj: CNPJ deve estar no formato 00.000.000/0000-00"]
}
```

Codigos tratados pelo `GlobalExceptionHandler`:
- `400` - Validacao de campos (Bean Validation)
- `401` - Credenciais invalidas no login
- `403` - Sem permissao (role insuficiente)
- `404` - Recurso nao encontrado
- `409` - Violacao de integridade (ex: CNPJ duplicado)
- `500` - Erro generico nao tratado

## Collection do Insomnia

O arquivo `insomnia_collection.json` esta na raiz do projeto. Para importar:
1. Abra o Insomnia
2. `Application` -> `Preferences` -> `Data` -> `Import Data` -> `From File`
3. Selecione `insomnia_collection.json`
4. As variaveis `baseUrl` (default `http://localhost:8080`) e `token` ja vem configuradas no environment "Local"

A collection esta organizada em pastas: Auth, Empresas, Pontos de Coleta, Residuos, Coletas e Indicadores.

## Documentacao da API (Swagger)

Apos subir a aplicacao:
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

Para testar endpoints autenticados pelo Swagger, clique em **Authorize** e cole o token JWT obtido no login (sem o prefixo `Bearer`).
