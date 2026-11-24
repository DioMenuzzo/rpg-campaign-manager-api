# 🎲 RPG Campaign Manager API

🇺🇸 [Read in English](README.md)

Backend para gerenciar campanhas de RPG de mesa (D&D 5e, Ordem Paranormal e outros): campanhas, personagens, NPCs e histórico/agenda de sessões.

Projeto com dois objetivos: ser uma ferramenta real de apoio ao mestre e servir de portfólio cobrindo a stack de vagas backend/cloud.

**Stack:** Java 21 · Spring Boot 3.5 · Spring Data JPA · PostgreSQL 17 · Flyway · Bean Validation · springdoc-openapi · JUnit 5 + Mockito + Testcontainers · Docker · GitHub Actions

---

## Roadmap

| Etapa | Entrega | Status |
|---|---|---|
| 1 | API REST (CRUD de Campanha, Personagem, NPC e Sessão) com Spring Boot + PostgreSQL via Docker | ✅ |
| 2 | Fichas de personagem flexíveis em **MongoDB** (cada sistema tem estrutura diferente) | ⏳ |
| 3 | Deploy na **AWS** (free tier): Docker + EC2/ECS + RDS | ⏳ |
| 4 | Notificação assíncrona de sessão agendada via fila (**RabbitMQ** ou **SQS**) | ⏳ |
| 5 | Extras: Prometheus + Grafana, Nginx (load balancer), ELK, Kubernetes local (minikube) | ⏳ |

---

## Como rodar

### Opção 1 — tudo em Docker (só precisa do Docker instalado)

```bash
docker compose up --build
```

- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- Health: http://localhost:8080/actuator/health

O compose sobe com o profile `dev`, que carrega dados de exemplo (duas campanhas, personagens, um NPC e sessões).
Para subir sem os dados de exemplo: `SPRING_PROFILES_ACTIVE=default docker compose up --build`.

### Opção 2 — banco no Docker, API na IDE (Eclipse/IntelliJ)

```bash
docker compose up -d postgres
```

Depois importe como **Existing Maven Project** e rode `RpgManagerApplication` (adicione `-Dspring.profiles.active=dev` nos VM arguments para ter dados de exemplo), ou pela linha de comando:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Testes

```bash
mvn verify
```

Os testes de integração usam **Testcontainers**: sobem um PostgreSQL real em container, rodam as migrations do Flyway e exercitam a API via MockMvc. Precisa do Docker rodando.

---

## Endpoints

Todos os recursos filhos ficam aninhados na campanha, o que garante que um personagem/NPC/sessão só é acessível pela campanha a que pertence.

| Método | Rota | Descrição |
|---|---|---|
| GET | `/api/campaigns?gameSystem=&status=&page=&size=&sort=` | Lista campanhas (filtros opcionais) |
| POST | `/api/campaigns` | Cria campanha |
| GET / PUT / DELETE | `/api/campaigns/{id}` | Busca / atualiza / remove (remoção em cascata) |
| GET / POST | `/api/campaigns/{id}/characters` | Lista / cria personagens |
| GET / PUT / DELETE | `/api/campaigns/{id}/characters/{characterId}` | |
| GET / POST | `/api/campaigns/{id}/npcs?attitude=` | Lista (filtro por atitude) / cria NPCs |
| GET / PUT / DELETE | `/api/campaigns/{id}/npcs/{npcId}` | |
| GET / POST | `/api/campaigns/{id}/sessions?status=` | Lista (filtro por status) / agenda sessões |
| GET / PUT / DELETE | `/api/campaigns/{id}/sessions/{sessionId}` | Remarcar, concluir com resumo, cancelar |

### Exemplos

```bash
# Criar campanha
curl -X POST localhost:8080/api/campaigns -H "Content-Type: application/json" \
  -d '{"name":"Protocolo Silêncio","gameSystem":"ORDEM_PARANORMAL","gameMaster":"Mestre"}'

# Adicionar NPC
curl -X POST localhost:8080/api/campaigns/1/npcs -H "Content-Type: application/json" \
  -d '{"name":"Dr. Halvorsen","role":"Informante","attitude":"NEUTRAL","secretNotes":"Trabalha para a seita"}'

# Agendar sessão
curl -X POST localhost:8080/api/campaigns/1/sessions -H "Content-Type: application/json" \
  -d '{"sessionNumber":3,"title":"O arquivo perdido","scheduledAt":"2026-12-05T20:00:00-03:00"}'
```

### Erros (RFC 9457 — Problem Details)

```json
{
  "type": "about:blank",
  "title": "Dados inválidos",
  "status": 400,
  "detail": "Um ou mais campos não passaram na validação.",
  "instance": "/api/campaigns",
  "errors": { "name": "must not be blank", "gameSystem": "must not be null" }
}
```

`404` para recurso inexistente (ou de outra campanha), `409` para conflitos (ex.: número de sessão repetido).

---

## Arquitetura e decisões

```
src/main/java/com/rpgmanager
├── campaign/     Campaign, CampaignService, CampaignController, dto/
├── character/    PlayerCharacter (PC), ...
├── npc/          Npc, ...
├── session/      GameSession (agenda + histórico), ...
├── common/       BaseEntity, exceções e handler global
└── config/       OpenAPI
```

- **Pacotes por funcionalidade** (não por camada): cada módulo tem entidade, repositório, serviço, controller e DTOs juntos. Facilita extrair um módulo para outro serviço no futuro.
- **DTOs como `record`**: a API nunca expõe entidades JPA; validação fica nos DTOs de entrada.
- **Flyway como dono do schema** (`ddl-auto: validate`): mudanças no banco são versionadas e revisáveis. Dados de exemplo ficam numa migration repetível carregada só no profile `dev`.
- **Cascata no banco** (`ON DELETE CASCADE`) em vez de coleções JPA na campanha: evita carregar listas inteiras na memória para remover uma campanha.
- **`PlayerCharacter`** em vez de `Character`, para não colidir com `java.lang.Character`.
- **Configuração por variáveis de ambiente** (`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `PORT`), já pensando no deploy da etapa 3 (RDS + ECS).
- **Health probes do Actuator** (`/actuator/health/liveness` e `/readiness`) usados pelo `HEALTHCHECK` do Docker e, depois, pelo ECS/Kubernetes.
- **Sessões com `scheduledAt` em UTC** e índice `(status, scheduled_at)`: base para o job da etapa 4 que busca sessões próximas e publica na fila.

### Ganchos para as próximas etapas

- **Etapa 2 (MongoDB):** `PlayerCharacter` ganha uma referência `sheetId` para o documento da ficha; o formato do documento depende de `Campaign.gameSystem`.
- **Etapa 4 (fila):** `GameSessionService.create/update` publica um evento `SessionScheduled` quando a sessão está `SCHEDULED`; um consumidor envia a notificação.
