-- Dados de exemplo, carregados só no profile "dev" (ver application-dev.yml).
-- Migration repetível e idempotente: pode rodar várias vezes sem duplicar.

INSERT INTO campaigns (name, game_system, status, game_master, description, created_at, updated_at)
SELECT 'A Maldição de Valdren', 'DND_5E', 'ACTIVE', 'Mestre',
       'Uma entidade antiga desperta sob as montanhas e o grupo precisa reunir os fragmentos do selo.',
       now(), now()
WHERE NOT EXISTS (SELECT 1 FROM campaigns WHERE name = 'A Maldição de Valdren');

INSERT INTO campaigns (name, game_system, status, game_master, description, created_at, updated_at)
SELECT 'Protocolo Silêncio', 'ORDEM_PARANORMAL', 'PLANNING', 'Mestre',
       'Civis comuns esbarram numa conspiração ligada ao Elemento Conhecimento.',
       now(), now()
WHERE NOT EXISTS (SELECT 1 FROM campaigns WHERE name = 'Protocolo Silêncio');

INSERT INTO player_characters (campaign_id, name, player_name, character_class, level, status, backstory, created_at, updated_at)
SELECT c.id, 'Aldric Pedraforte', 'Jogador 1', 'Paladino', 7, 'ALIVE',
       'Anão herdeiro de uma linhagem de guardiões.', now(), now()
FROM campaigns c
WHERE c.name = 'A Maldição de Valdren'
  AND NOT EXISTS (SELECT 1 FROM player_characters p WHERE p.campaign_id = c.id AND p.name = 'Aldric Pedraforte');

INSERT INTO player_characters (campaign_id, name, player_name, character_class, level, status, backstory, created_at, updated_at)
SELECT c.id, 'Sera Vento-Cinza', 'Jogador 2', 'Ladina', 7, 'ALIVE',
       'Ex-membro de uma guilda de ladrões tomada por um traidor.', now(), now()
FROM campaigns c
WHERE c.name = 'A Maldição de Valdren'
  AND NOT EXISTS (SELECT 1 FROM player_characters p WHERE p.campaign_id = c.id AND p.name = 'Sera Vento-Cinza');

INSERT INTO npcs (campaign_id, name, role, location, attitude, alive, description, secret_notes, created_at, updated_at)
SELECT c.id, 'Morvath, o Sem-Rosto', 'Vilão', 'Templo em ruínas', 'HOSTILE', TRUE,
       'Cavaleiro de armadura negra montado num cavalo de névoa.',
       'Obcecado pela feiticeira que o manipula.', now(), now()
FROM campaigns c
WHERE c.name = 'A Maldição de Valdren'
  AND NOT EXISTS (SELECT 1 FROM npcs n WHERE n.campaign_id = c.id AND n.name = 'Morvath, o Sem-Rosto');

INSERT INTO game_sessions (campaign_id, session_number, title, scheduled_at, status, summary, created_at, updated_at)
SELECT c.id, 1, 'O despertar', now() - INTERVAL '14 days', 'COMPLETED',
       'O grupo encontrou o primeiro fragmento do selo.', now(), now()
FROM campaigns c
WHERE c.name = 'A Maldição de Valdren'
  AND NOT EXISTS (SELECT 1 FROM game_sessions s WHERE s.campaign_id = c.id AND s.session_number = 1);

INSERT INTO game_sessions (campaign_id, session_number, title, scheduled_at, status, summary, created_at, updated_at)
SELECT c.id, 2, 'O duelo no templo', now() + INTERVAL '7 days', 'SCHEDULED', NULL, now(), now()
FROM campaigns c
WHERE c.name = 'A Maldição de Valdren'
  AND NOT EXISTS (SELECT 1 FROM game_sessions s WHERE s.campaign_id = c.id AND s.session_number = 2);
