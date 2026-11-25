# 🎲 RPG Campaign Manager API

🇧🇷 [Leia em português](README.pt-BR.md)

Backend for managing tabletop RPG campaigns (D&D 5e, Ordem Paranormal, 3D&T Alpha and others): campaigns, player characters, NPCs and session scheduling/history.

A personal project with two goals: to be a real tool I use as a game master, and to serve as a portfolio piece covering the stack commonly required for backend/cloud developer roles.

**Stack:** Java 21 · Spring Boot 3.5 · Spring Data JPA · PostgreSQL 17 · Flyway · Spring Data MongoDB · MongoDB 8 · Bean Validation · springdoc-openapi · JUnit 5 + Mockito + Testcontainers · Docker · GitHub Actions

---

## Roadmap

| Stage | Deliverable | Status |
|---|---|---|
| 1 | REST API (CRUD for Campaign, Character, NPC and Session) with Spring Boot + PostgreSQL on Docker | ✅ |
| 2 | Flexible character sheets in **MongoDB** (each RPG system has a different structure) | ✅ |
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

Compose starts PostgreSQL, MongoDB and the API with the `dev` profile, which loads sample data (two campaigns, characters, an NPC and sessions).
To start without sample data: `SPRING_PROFILES_ACTIVE=default docker compose up --build`.

### Option 2 — database in Docker, API in your IDE

```bash
docker compose up -d postgres mongo
```

Then import it as an existing Maven project and run `RpgManagerApplication` (add `-Dspring.profiles.active=dev` to the VM arguments for sample data), or from the command line:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Tests

```bash
mvn verify
```

Integration tests use **Testcontainers**: they start real PostgreSQL and MongoDB containers, run the Flyway migrations and exercise the API through MockMvc. Docker must be running.

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
| GET / PUT / DELETE | `/api/campaigns/{id}/characters/{characterId}/sheet` | Character sheet (MongoDB), with derived values |
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

### Character sheets

Each campaign has a game system, and a character's sheet must match it. The `system` field selects the format:

| `system` | Format |
|---|---|
| `DND_5E` | Species, classes (multiclass supported), ability scores, HP, AC, saving throws, skills (proficiency/expertise), spell slots, spells, inventory, features |
| `ORDEM_PARANORMAL` | Class, origin, trail, NEX (0 allowed for civilians), attributes, PV/PE/Sanity, skills with training degree, rituals by element and circle, powers, inventory |
| `TRES_D_T_ALPHA` | 3D&T Alpha: the five characteristics (F, H, R, A, PdF), PV/PM, advantages and disadvantages with point costs, skills, spells, kit, inventory |
| `OTHER` | Free-form `fields` object, for systems without a dedicated model |

Ordem Paranormal enum values keep the game's official Portuguese terms (`OCULTISTA`, `TREINADO`, `CONHECIMENTO`...). For 3D&T Alpha, derived values include melee/ranged attack (FA) and defense (FD) before the 1d6 roll, base PV/PM (R×5) and points spent/remaining.

```bash
curl -X PUT localhost:8080/api/campaigns/1/characters/1/sheet -H "Content-Type: application/json" -d '{
  "system": "DND_5E",
  "species": "Halfling",
  "classes": [{"name": "Rogue", "subclass": "Thief", "level": 5}],
  "abilities": {"strength": 8, "dexterity": 16, "constitution": 14, "intelligence": 10, "wisdom": 13, "charisma": 12},
  "hitPoints": {"max": 33, "current": 33},
  "armorClass": 15,
  "savingThrowProficiencies": ["DEX", "INT"],
  "skills": [{"skill": "STEALTH", "level": "EXPERTISE"}, {"skill": "PERCEPTION"}]
}'
```

The response echoes the sheet and adds computed values, so clients don't need to reimplement the rules:

```json
"derived": {
  "totalLevel": 5,
  "proficiencyBonus": 3,
  "abilityModifiers": { "STR": -1, "DEX": 3, "CON": 2, "INT": 0, "WIS": 1, "CHA": 1 },
  "skills": { "STEALTH": 9, "PERCEPTION": 4, "...": "..." },
  "initiative": 3,
  "passivePerception": 14
}
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

`404` for missing resources (or resources from another campaign), `409` for conflicts (e.g. a duplicate session number or a concurrent sheet edit), `422` for business rule violations (e.g. a D&D sheet in an Ordem Paranormal campaign). Error messages are currently in Portuguese, the language of the tables I run.

---

## Architecture and design decisions

```
src/main/java/com/rpgmanager
├── campaign/     Campaign, CampaignService, CampaignController, dto/
├── character/    PlayerCharacter (PC), ...
├── npc/          Npc, ...
├── session/      GameSession (schedule + history), ...
├── sheet/        Character sheets in MongoDB: model/dnd, model/ordem, FreeformSheet
├── common/       BaseEntity, exceptions and global handler
└── config/       OpenAPI
```

- **Package by feature** (not by layer): each module keeps its entity, repository, service, controller and DTOs together, which makes it easier to extract a module into its own service later.
- **DTOs as `record`s**: the API never exposes JPA entities; validation lives on the input DTOs.
- **Flyway owns the schema** (`ddl-auto: validate`): database changes are versioned and reviewable. Sample data lives in a repeatable migration loaded only by the `dev` profile.
- **Cascading deletes in the database** (`ON DELETE CASCADE`) instead of JPA collections on the campaign, so deleting a campaign doesn't load entire child lists into memory.
- **`PlayerCharacter`** instead of `Character`, to avoid clashing with `java.lang.Character`.
- **Configuration via environment variables** (`RPG_DB_URL`, `RPG_DB_USERNAME`, `RPG_DB_PASSWORD`, `RPG_MONGO_URI`, `RPG_PORT`), prefixed with `RPG_` so they never collide with variables already set on the host, ready for the stage 3 deployment (RDS + ECS).
- **Actuator health probes** (`/actuator/health/liveness` and `/readiness`), used by the Docker `HEALTHCHECK` and later by ECS/Kubernetes.
- **Polyglot persistence:** relational, strongly linked data (campaigns, characters, NPCs, sessions) lives in PostgreSQL; character sheets, whose shape changes per game system, live in MongoDB. Each store is used where its model fits.
- **One contract for API and storage:** sheets are typed Java records with Bean Validation, deserialized polymorphically by Jackson from the `system` field. The same mapping converts them to and from the Mongo document, so the database never stores Java class names and adding a sheet field needs no migration.
- **Cross-store consistency without distributed transactions:** deleting a character or campaign publishes a domain event; a `@TransactionalEventListener(AFTER_COMMIT)` removes the matching sheets from MongoDB only after PostgreSQL commits. A failed cleanup leaves a harmless orphan (sheets are always accessed through an existing character), an explicit eventual-consistency trade-off.
- **Optimistic locking** (`@Version`) on sheets: two concurrent edits return `409` instead of silently overwriting each other.
- **Derived values are computed, never stored** (modifiers, proficiency bonus, skill bonuses), so they can't drift from the sheet.
- **Sessions store `scheduledAt` in UTC** with a `(status, scheduled_at)` index, the basis for the stage 4 job that finds upcoming sessions and publishes them to the queue.

### Hooks for upcoming stages

- **New game systems (e.g. 3DeT Victory):** a new record implementing `CharacterSheetData`, registered in `@JsonSubTypes`, plus a value in `GameSystem` (with a Flyway migration for the check constraint).
- **Stage 4 (queue):** `GameSessionService.create/update` publishes a `SessionScheduled` event when a session is `SCHEDULED`; a consumer sends the notification.
