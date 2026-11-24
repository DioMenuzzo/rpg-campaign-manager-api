# 🎲 RPG Campaign Manager API

🇺🇸 [Read in English](README.md)

Backend para gerenciar campanhas de RPG de mesa (D&D 5e, Ordem Paranormal, 3D&T Alpha e outros): campanhas, personagens, NPCs e histórico/agenda de sessões.

Projeto com dois objetivos: ser uma ferramenta real de apoio ao mestre e servir de portfólio cobrindo a stack de vagas backend/cloud.

**Stack:** Java 21 · Spring Boot 3.5 · Spring Data JPA · PostgreSQL 17 · Flyway · Spring Data MongoDB · MongoDB 8 · Bean Validation · springdoc-openapi · JUnit 5 + Mockito + Testcontainers · Docker · GitHub Actions

---

## Roadmap

| Etapa | Entrega | Status |
|---|---|---|
| 1 | API REST (CRUD de Campanha, Personagem, NPC e Sessão) com Spring Boot + PostgreSQL via Docker | ✅ |
| 2 | Fichas de personagem flexíveis em **MongoDB** (cada sistema tem estrutura diferente) | ✅ |
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

O compose sobe PostgreSQL, MongoDB e a API com o profile `dev`, que carrega dados de exemplo (duas campanhas, personagens, um NPC e sessões).
Para subir sem os dados de exemplo: `SPRING_PROFILES_ACTIVE=default docker compose up --build`.

### Opção 2 — banco no Docker, API na IDE (Eclipse/IntelliJ)

```bash
docker compose up -d postgres mongo
```

Depois importe como **Existing Maven Project** e rode `RpgManagerApplication` (adicione `-Dspring.profiles.active=dev` nos VM arguments para ter dados de exemplo), ou pela linha de comando:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Testes

```bash
mvn verify
```

Os testes de integração usam **Testcontainers**: sobem PostgreSQL e MongoDB reais em containers, rodam as migrations do Flyway e exercitam a API via MockMvc. Precisa do Docker rodando.

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
| GET / PUT / DELETE | `/api/campaigns/{id}/characters/{characterId}/sheet` | Ficha do personagem (MongoDB), com valores calculados |
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

### Fichas de personagem

Cada campanha tem um sistema de RPG, e a ficha do personagem precisa ser do mesmo sistema. O campo `system` define o formato:

| `system` | Formato |
|---|---|
| `DND_5E` | Espécie, classes (com multiclasse), atributos, PV, CA, salvaguardas, perícias (proficiência/especialização), espaços de magia, magias, inventário, características |
| `ORDEM_PARANORMAL` | Classe, origem, trilha, NEX (aceita 0 para civis), atributos, PV/PE/Sanidade, perícias com grau de treinamento, rituais por elemento e círculo, poderes, inventário |
| `TRES_D_T_ALPHA` | 3D&T Alpha: as cinco Características (F, H, R, A, PdF), PV/PM, vantagens e desvantagens com custo em pontos, perícias, magias, kit, inventário |
| `OTHER` | Objeto `fields` livre, para sistemas sem modelo próprio |

Exemplo:

```bash
curl -X PUT localhost:8080/api/campaigns/1/characters/1/sheet -H "Content-Type: application/json" -d '{
  "system": "ORDEM_PARANORMAL",
  "characterClass": "OCULTISTA",
  "nex": 10,
  "attributes": {"agility": 1, "strength": 1, "intellect": 3, "presence": 2, "vigor": 1},
  "hitPoints": {"max": 13, "current": 13},
  "effortPoints": {"max": 6, "current": 6},
  "sanity": {"max": 24, "current": 18},
  "skills": [{"skill": "OCULTISMO", "training": "TREINADO"}],
  "rituals": [{"name": "Decadência", "element": "MORTE", "circle": 1}]
}'
```

A resposta devolve a ficha e um bloco `derived` com valores calculados (modificadores, bônus de proficiência, bônus de perícias, percepção passiva, porcentagem de sanidade, FA/FD do 3D&T antes da rolagem de 1d, PV/PM base, pontos gastos e restantes...), para que nenhum cliente precise reimplementar as regras.

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

`404` para recurso inexistente (ou de outra campanha), `409` para conflitos (ex.: número de sessão repetido ou edição simultânea da ficha), `422` para regra de negócio violada (ex.: ficha de D&D numa campanha de Ordem Paranormal).

---

## Arquitetura e decisões

```
src/main/java/com/rpgmanager
├── campaign/     Campaign, CampaignService, CampaignController, dto/
├── character/    PlayerCharacter (PC), ...
├── npc/          Npc, ...
├── session/      GameSession (agenda + histórico), ...
├── sheet/        Fichas no MongoDB: model/dnd, model/ordem, FreeformSheet
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
- **Persistência poliglota:** dados relacionais e fortemente ligados (campanhas, personagens, NPCs, sessões) ficam no PostgreSQL; fichas, cujo formato muda por sistema, ficam no MongoDB. Cada banco é usado onde seu modelo faz sentido.
- **Um único contrato para API e armazenamento:** as fichas são records Java com Bean Validation, desserializados de forma polimórfica pelo Jackson a partir do campo `system`. O mesmo mapeamento converte para o documento do Mongo, então o banco não guarda nomes de classes Java e adicionar um campo à ficha não exige migration.
- **Consistência entre bancos sem transação distribuída:** remover um personagem ou campanha publica um evento de domínio; um `@TransactionalEventListener(AFTER_COMMIT)` apaga as fichas no MongoDB só depois do commit no PostgreSQL. Se a limpeza falhar, sobra uma ficha órfã inofensiva (fichas sempre são acessadas por um personagem existente): é uma troca explícita por consistência eventual.
- **Lock otimista** (`@Version`) nas fichas: duas edições simultâneas resultam em `409`, em vez de uma sobrescrever a outra em silêncio.
- **Valores derivados são calculados, nunca salvos** (modificadores, bônus de proficiência, bônus de perícias), então nunca ficam desatualizados em relação à ficha.
- **Sessões com `scheduledAt` em UTC** e índice `(status, scheduled_at)`: base para o job da etapa 4 que busca sessões próximas e publica na fila.

### Ganchos para as próximas etapas

- **Novos sistemas (ex.: 3DeT Victory):** um novo record implementando `CharacterSheetData`, registrado em `@JsonSubTypes`, e um valor novo em `GameSystem` (com migration do Flyway para a constraint).
- **Etapa 4 (fila):** `GameSessionService.create/update` publica um evento `SessionScheduled` quando a sessão está `SCHEDULED`; um consumidor envia a notificação.
