# Projeto - Cidades ESGInteligentes

## Gestão de Resíduos e Reciclagem (ESG)

**Integrante:** João Paulo Conrado Silva - RM 562593

**FIAP - Fase 6 - DevOps**

Microsserviço REST em Spring Boot que gerencia pontos de coleta seletiva, resíduos e coletas, com alerta quando um ponto chega a 80% da capacidade e indicadores de reciclagem. O projeto veio da Fase 3 e nesta fase foi adaptado para rodar em containers e ter uma pipeline de CI/CD com deploy em staging e produção.

Uma mudança importante: o projeto original usava **Oracle XE**. Troquei para **PostgreSQL** porque a imagem do Oracle XE ocupa uns 2 GB por container e deixaria o pipeline pesado e lento, já que staging e produção sobem dentro do runner gratuito do GitHub. As migrations do Flyway foram reescritas para o dialeto do Postgres e o resto do código (controllers, services, repositories) continuou igual.

## Como executar localmente com Docker

Pré-requisito: Docker Desktop instalado e rodando.

O projeto tem três arquivos de compose, um para cada ambiente:

| Arquivo | Ambiente | Porta da API | Arquivo de variáveis |
|---------|----------|--------------|----------------------|
| `docker-compose.yml` | Local (desenvolvimento) | 8080 | `.env` |
| `docker-compose.staging.yml` | Staging | 8081 | `.env.staging` |
| `docker-compose.prod.yml` | Produção | 8080 | `.env.prod` |

Cada arquivo de variáveis tem um `.example` versionado. Os arquivos de verdade ficam no `.gitignore`.

### Ambiente local

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

### Ambiente de staging na própria máquina

Dá para subir o mesmo ambiente que o pipeline usa em staging:

```bash
cp .env.staging.example .env.staging
docker compose -f docker-compose.staging.yml up --build
```

A API fica em `http://localhost:8081` (health em `http://localhost:8081/actuator/health`). Como os containers, o volume e a rede têm nomes próprios, ele pode rodar junto com o ambiente local. Para derrubar: `docker compose -f docker-compose.staging.yml down -v`.

### Testes

Para rodar só os testes, sem Docker (precisa de Java 17 e Maven):

```bash
mvn verify
```

Os testes usam o perfil `test`, com banco H2 em memória.

## Pipeline CI/CD

A pipeline fica em `.github/workflows/ci-cd.yml` e roda no **GitHub Actions**. Ela dispara em todo push e pull request para a branch `main`. Os ambientes de staging e produção são criados pelo próprio pipeline, dentro do runner do GitHub, usando Docker Compose.

Uma limitação que precisa ficar clara: os ambientes de staging e produção são efêmeros. Eles sobem no runner do GitHub Actions com Docker Compose, são testados (health check, login e um endpoint com token) e são derrubados no fim do job. Não existe servidor público nem URL para acessar depois. Cada ambiente constrói a imagem a partir do mesmo commit. Escolhi esse formato para não depender de servidor pago.

O fluxo tem três jobs, um depois do outro:

1. **build-e-testes**: faz o checkout, configura o Java 17, roda `mvn -B verify` (compila e executa os testes automatizados), guarda o `.jar` como artefato e faz o `docker build` da imagem para garantir que o Dockerfile está funcionando. Em pull request a pipeline para aqui.
2. **deploy-staging** (automático): só roda em push na `main` e depois que os testes passaram. Cria o `.env.staging` a partir do exemplo, sobe o ambiente com `docker compose -f docker-compose.staging.yml up -d --build`, espera a API responder em `/actuator/health`, testa o login do admin, chama `/empresas` com o token para conferir que os dados do Postgres foram lidos, mostra os containers e os logs da aplicação e no final derruba tudo.
3. **deploy-producao** (com aprovação manual): roda depois do staging e usa o environment `production` do GitHub, que tem *required reviewers*. O job fica parado até alguém aprovar. Depois faz os mesmos passos com o `docker-compose.prod.yml`, mas o `.env.prod` é montado com as senhas guardadas nos secrets do repositório, e não com valores fixos.

Cada ambiente sobe a aplicação e um Postgres isolados, com containers, volume, rede e variáveis próprios:

| Ambiente | Compose | Containers | Volume | Rede | Variáveis |
|----------|---------|------------|--------|------|-----------|
| Local | `docker-compose.yml` | `gestao-residuos-db` / `gestao-residuos-app` | `postgres_data` | `residuos-net` | `.env` |
| Staging | `docker-compose.staging.yml` | `residuos-staging-db` / `residuos-staging-app` | `postgres_data_staging` | `residuos-staging-net` | `.env.staging` |
| Produção | `docker-compose.prod.yml` | `residuos-prod-db` / `residuos-prod-app` | `postgres_data_prod` | `residuos-prod-net` | `.env.prod` (secrets) |

Configuração feita no GitHub (fora do código):
- Environments `staging` e `production`. No `production` foi marcada a opção *required reviewers* para ter a aprovação manual.
- Secrets `PROD_DB_PASSWORD` (senha do banco de produção) e `PROD_JWT_SECRET` (chave do JWT em Base64, com pelo menos 32 bytes).

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

Runs usados nos prints: #1 (primeira execução, https://github.com/eujoaoconrado/gestao-residuos-esg/actions/runs/38469841023) e #2 (https://github.com/eujoaoconrado/gestao-residuos-esg/actions/runs/38474803117)

### Pipeline rodando no GitHub Actions

![Pipeline completo](docs/prints/06-pipeline-completo.jpg)

Pipeline completo, com os três jobs concluídos.

![Job build-e-testes](docs/prints/07-build-testes.jpg)

Job build-e-testes do run 2: passo Build e testes aberto, mostrando `Tests run: 4, Failures: 0` e BUILD SUCCESS.

![Pipeline aguardando aprovação](docs/prints/01-pipeline-aguardando-aprovacao.jpg)

Pipeline com build e staging concluídos, aguardando a aprovação de produção.

### Aprovação manual do deploy em produção

![Aprovação de produção](docs/prints/02-aprovacao-producao.jpg)

Deploy de produção aprovado manualmente no environment `production`.

### Ambiente de staging (health e login)

![Log do staging](docs/prints/03-staging-log.jpg)

Log do job no GitHub Actions com os passos Esperar a aplicação subir, Testar login na API e Mostrar containers abertos.

Log exportado do job (trecho):

```text
{"status":"UP"}
Aplicacao no ar
$ RESPOSTA=$(curl -fsS -X POST localhost:8081/auth/login \
{"token":"***
$ docker compose -f docker-compose.staging.yml ps
NAME                   IMAGE                  COMMAND                  SERVICE   CREATED          STATUS                    PORTS
residuos-staging-app   residuos-staging-app   "java -jar /app/app.…"   app       16 seconds ago   Up 10 seconds             0.0.0.0:8081->8080/tcp,
residuos-staging-db    postgres:16-alpine     "docker-entrypoint.s…"   db        16 seconds ago   Up 16 seconds (healthy)   5432/tcp
```

### Ambiente de produção (health e login)

![Log da produção](docs/prints/04-producao-log.jpg)

Log do job no GitHub Actions com os passos Esperar a aplicação subir, Testar login na API e Mostrar containers abertos.

Log exportado do job (trecho):

```text
{"status":"UP"}
Aplicacao no ar
$ RESPOSTA=$(curl -fsS -X POST localhost:8080/auth/login \
{"token":"***
$ docker compose -f docker-compose.prod.yml ps
NAME                IMAGE                COMMAND                  SERVICE   CREATED          STATUS                    PORTS
residuos-prod-app   residuos-prod-app    "java -jar /app/app.…"   app       17 seconds ago   Up 11 seconds             0.0.0.0:8080->8080/tcp, [::]:
residuos-prod-db    postgres:16-alpine   "docker-entrypoint.s…"   db        17 seconds ago   Up 16 seconds (healthy)   5432/tcp
```

### Docker Compose rodando localmente

![Swagger local](docs/prints/05-swagger-local.jpg)

Swagger da API rodando localmente com `docker compose up`.

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
| Deploy | Docker Compose no runner do GitHub Actions |

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
| Projeto compactado em .ZIP com estrutura organizada | [x] |
| Dockerfile funcional | [x] |
| docker-compose.yml ou arquivos Kubernetes | [x] |
| Pipeline com etapas de build, teste e deploy | [x] |
| README.md com instruções e prints | [x] |
| Documentação técnica com evidências (PDF ou PPT) | [x] |
| Deploy realizado nos ambientes staging e produção | [x] |
