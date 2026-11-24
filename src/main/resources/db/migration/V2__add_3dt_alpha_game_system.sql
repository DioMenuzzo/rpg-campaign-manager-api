-- Novo sistema suportado: 3D&T Alpha.
ALTER TABLE campaigns DROP CONSTRAINT ck_campaigns_game_system;
ALTER TABLE campaigns ADD CONSTRAINT ck_campaigns_game_system
    CHECK (game_system IN ('DND_5E', 'ORDEM_PARANORMAL', 'TRES_D_T_ALPHA', 'OTHER'));
