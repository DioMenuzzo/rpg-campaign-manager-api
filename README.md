# 🎲 RPG Campaign Manager API

🇧🇷 [Leia em português](README.pt-BR.md)

Backend for managing tabletop RPG campaigns (D&D 5e, Ordem Paranormal and others): campaigns, player characters, NPCs and session scheduling/history.

A personal project with two goals: to be a real tool I use as a game master, and to serve as a portfolio piece covering the stack commonly required for backend/cloud developer roles.

**Stack:** Java 21 · Spring Boot 3.5 · Spring Data JPA · PostgreSQL 17 · Flyway · Bean Validation · springdoc-openapi · JUnit 5 + Mockito + Testcontainers · Docker · GitHub Actions

---

## Roadmap

| Stage | Deliverable | Status |
|---|---|---|
| 1 | REST API (CRUD for Campaign, Character, NPC and Session) with Spring Boot + PostgreSQL on Docker | ✅ |
| 2 | Flexible character sheets in **MongoDB** (each RPG system has a different structure) | ⏳ |
| 3 | **AWS** deployment (free tier): Docker + EC2/ECS + RDS | ⏳ |
| 4 | Asynchronous notifications for scheduled sessions via a message queue (**RabbitMQ** or **SQS**) | ⏳ |
| 5 | Extras: Prometheus + Grafana, Nginx load balancer, ELK, local Kubernetes (minikube) | ⏳ |

---

## Running locally

### Option 1 — everything in Docker (only Docker required)

```bash
docker compose up --build
```

- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- Health: http://localhost:8080/actuator/health

Compose starts with the `dev` profile, which loads sample data (two campaigns, characters, an NPC and sessions).
To start without sample data: `SPRING_PROFILES_ACTIVE=default docker compose up --build`.

### Option 2 — database in Docker, API in your IDE

```bash
docker compose up -d postgres
```

Then import it as an existing Maven project and run `RpgManagerApplication` (add `-Dspring.profiles.active=dev` to the VM arguments for sample data), or from the command line:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Tests

```bash
mvn verify
```

Integration tests use **Testcontainers**: they start a real PostgreSQL container, run the Flyway migrations and exercise the API through MockMvc. Docker must be running.

---

## Endpoints

Child resources are nested under their campaign, so a character, NPC or session can only be reached through the campaign it belongs to.

| Method | Route | Description |
|---|---|---|
| GET | `/api/campaigns?gameSystem=&status=&page=&size=&sort=` | List campaigns (optional filters) |
| POST | `/api/campaigns` | Create a campaign |
| GET / PUT / DELETE | `/api/campaigns/{id}` | Get / update / delete (cascading delete) |
| GET / POST | `/api/campaigns/{id}/characters` | List / create characters |
| GET / PUT / DELETE | `/api/campaigns/{id}/characters/{characterId}` | |
| GET / POST | `/api/campaigns/{id}/npcs?attitude=` | List (filter by attitude) / create NPCs |
| GET / PUT / DELETE | `/api/campaigns/{id}/npcs/{npcId}` | |
| GET / POST | `/api/campaigns/{id}/sessions?status=` | List (filter by status) / schedule sessions |
| GET / PUT / DELETE | `/api/campaigns/{id}/sessions/{sessionId}` | Reschedule, complete with a summary, cancel |

### Examples

```bash
# Create a campaign
curl -X POST localhost:8080/api/campaigns -H "Content-Type: application/json" \
  -d '{"name":"Protocolo Silêncio","gameSystem":"ORDEM_PARANORMAL","gameMaster":"GM"}'

# Add an NPC
curl -X POST localhost:8080/api/campaigns/1/npcs -H "Content-Type: application/json" \
  -d '{"name":"Dr. Halvorsen","role":"Informant","attitude":"NEUTRAL","secretNotes":"Works for the cult"}'

# Schedule a session
curl -X POST localhost:8080/api/campaigns/1/sessions -H "Content-Type: application/json" \
  -d '{"sessionNumber":3,"title":"The lost file","scheduledAt":"2026-12-05T20:00:00-03:00"}'
```

### Errors (RFC 9457 — Problem Details)

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

`404` for missing resources (or resources from another campaign), `409` for conflicts (e.g. a duplicate session number). Error messages are currently in Portuguese, the language of the tables I run.

---

## Architecture and design decisions

```
src/main/java/com/rpgmanager
├── campaign/     Campaign, CampaignService, CampaignController, dto/
├── character/    PlayerCharacter (PC), ...
├── npc/          Npc, ...
├── session/      GameSession (schedule + history), ...
├── common/       BaseEntity, exceptions and global handler
└── config/       OpenAPI
```

- **Package by feature** (not by layer): each module keeps its entity, repository, service, controller and DTOs together, which makes it easier to extract a module into its own service later.
- **DTOs as `record`s**: the API never exposes JPA entities; validation lives on the input DTOs.
- **Flyway owns the schema** (`ddl-auto: validate`): database changes are versioned and reviewable. Sample data lives in a repeatable migration loaded only by the `dev` profile.
- **Cascading deletes in the database** (`ON DELETE CASCADE`) instead of JPA collections on the campaign, so deleting a campaign doesn't load entire child lists into memory.
- **`PlayerCharacter`** instead of `Character`, to avoid clashing with `java.lang.Character`.
- **Configuration via environment variables** (`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `PORT`), ready for the stage 3 deployment (RDS + ECS).
- **Actuator health probes** (`/actuator/health/liveness` and `/readiness`), used by the Docker `HEALTHCHECK` and later by ECS/Kubernetes.
- **Sessions store `scheduledAt` in UTC** with a `(status, scheduled_at)` index, the basis for the stage 4 job that finds upcoming sessions and publishes them to the queue.

### Hooks for upcoming stages

- **Stage 2 (MongoDB):** `PlayerCharacter` gets a `sheetId` reference to its sheet document; the document format depends on `Campaign.gameSystem`.
- **Stage 4 (queue):** `GameSessionService.create/update` publishes a `SessionScheduled` event when a session is `SCHEDULED`; a consumer sends the notification.
