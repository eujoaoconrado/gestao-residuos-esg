# Projeto - Gestão de Resíduos e Reciclagem (ESG)

**Integrante:** João Paulo Conrado Silva - RM 562593

**FIAP - Fase 6 - DevOps**

Microsserviço REST em Spring Boot que gerencia pontos de coleta seletiva, resíduos e coletas, com alerta quando um ponto chega a 80% da capacidade e indicadores de reciclagem. O projeto veio da Fase 3 e nesta fase foi adaptado para rodar em containers e ter uma pipeline de CI/CD com deploy em staging e produção.

Uma mudança importante: o projeto original usava **Oracle XE**. Troquei para **PostgreSQL** porque a imagem do Oracle XE ocupa uns 2 GB por container e não cabe nos ambientes gratuitos de deploy. As migrations do Flyway foram reescritas para o dialeto do Postgres e o resto do código (controllers, services, repositories) continuou igual.

## Como executar localmente com Docker

Pré-requisito: Docker Desktop instalado e rodando.

1. Copie o arquivo de variáveis de ambiente:

```bash
cp .env.example .env
```

2. Suba o banco e a aplicação:

```bash
docker compose up --build
```

O compose sobe dois serviços na rede `residuos-net`:
- `db`: PostgreSQL 16 (alpine), com os dados guardados no volume `postgres_data`. Tem healthcheck com `pg_isready`.
- `app`: a API, que só sobe depois que o banco estiver saudável. Na subida o Flyway cria as tabelas e os dados de exemplo.

3. Teste se está funcionando:

```bash
# health check
curl http://localhost:8080/actuator/health

# login com o usuario admin (criado automaticamente)
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "admin@fiap.com", "senha": "admin123"}'
```

Swagger: `http://localhost:8080/swagger-ui.html`

Usuários criados na subida:

| Email | Senha | Role |
|-------|-------|------|
| `admin@fiap.com` | `admin123` | ADMIN |
| `user@fiap.com` | `user123` | USER |

Para parar: `docker compose down` (ou `docker compose down -v` para apagar também os dados do banco).

Para rodar só os testes, sem Docker (precisa de Java 17 e Maven):

```bash
mvn verify
```

Os testes usam o perfil `test`, com banco H2 em memória.

## Pipeline CI/CD

A pipeline fica em `.github/workflows/ci-cd.yml` e roda no **GitHub Actions**. Ela dispara em todo push e pull request para a branch `main`.

Etapas:

1. **build-e-testes**: faz o checkout, configura o Java 17, roda `mvn -B verify` (compila e executa os testes automatizados), guarda o `.jar` como artefato e faz o `docker build` da imagem para garantir que o Dockerfile está funcionando. Em pull request a pipeline para aqui.
2. **deploy-staging**: só roda em push na `main` e depois que os testes passaram. Ele atualiza a branch `staging` com o commit da main. O Render está configurado para fazer deploy automático dessa branch. Depois o job faz um smoke test chamando `/actuator/health` até 20 vezes, com 30 segundos de intervalo.
3. **deploy-producao**: roda depois do staging. Usa o environment `production` do GitHub, que tem aprovação manual, então o job fica esperando alguém aprovar. Depois de aprovado ele atualiza a branch `release`, que é a branch de produção no Render, e faz o mesmo smoke test.

Ambientes:

| Ambiente | Branch | Onde roda | Banco |
|----------|--------|-----------|-------|
| Local | qualquer | Docker Compose | Postgres em container |
| Staging | `staging` | Render (web service com Docker) | Postgres do Render |
| Produção | `release` | Render (web service com Docker) | Postgres do Render |

Configuração feita fora do código:
- **GitHub**: environments `staging` e `production` (o de produção com *required reviewers*) e as variáveis `STAGING_URL` e `PRODUCTION_URL` com a URL de cada serviço no Render.
- **Render**: um web service para cada ambiente, apontando para a branch certa, com auto-deploy ligado e as variáveis `DB_URL`, `DB_USER`, `DB_PASSWORD`, `JWT_SECRET` e `JWT_EXPIRATION`. O `DB_URL` tem que estar no formato JDBC (`jdbc:postgresql://host:5432/banco`).

A pipeline não usa nenhum segredo além do `GITHUB_TOKEN` padrão, porque o deploy é feito pelo próprio Render quando a branch muda.

## Containerização

Dockerfile usado no projeto:

```dockerfile
# ---------- Build stage ----------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests -B

# ---------- Runtime stage ----------
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
```

O Dockerfile é multi-stage. O primeiro estágio usa a imagem do Maven com JDK 17 para baixar as dependências e gerar o `.jar` (os testes não rodam aqui porque já rodam na pipeline). O segundo estágio usa só o JRE 17 e copia apenas o `.jar` gerado, então a imagem final fica bem menor e não leva o Maven nem o código fonte. Copiar o `pom.xml` antes do `src` faz o Docker reaproveitar a camada das dependências quando só o código muda.

## Prints do funcionamento

### Pipeline rodando no GitHub Actions

(print será adicionado)

### Aprovação manual do deploy em produção

(print será adicionado)

### Staging no Swagger

(print será adicionado)

### Produção no Swagger

(print será adicionado)

### Docker Compose rodando localmente

(print será adicionado)

## Tecnologias utilizadas

| Camada | Tecnologia |
|--------|------------|
| Linguagem | Java 17 |
| Framework | Spring Boot 3.2 |
| Web | Spring Web (REST) |
| Persistência | Spring Data JPA + Hibernate |
| Banco de dados | PostgreSQL 16 |
| Migrações | Flyway |
| Segurança | Spring Security + JWT (jjwt) |
| Validação | Bean Validation |
| Documentação | SpringDoc OpenAPI / Swagger UI |
| Monitoramento | Spring Boot Actuator (health check) |
| Testes | JUnit 5 + MockMvc + H2 |
| Build | Maven |
| Container | Docker + Docker Compose |
| CI/CD | GitHub Actions |
| Deploy | Render |

## Endpoints da API

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

## Checklist de entrega

| Item | Status |
|------|--------|
| Projeto compactado em .ZIP com estrutura organizada | [ ] |
| Dockerfile funcional | [x] |
| docker-compose.yml ou arquivos Kubernetes | [x] |
| Pipeline com etapas de build, teste e deploy | [x] |
| README.md com instruções e prints | [ ] |
| Documentação técnica com evidências (PDF ou PPT) | [ ] |
| Deploy realizado nos ambientes staging e produção | [ ] |
