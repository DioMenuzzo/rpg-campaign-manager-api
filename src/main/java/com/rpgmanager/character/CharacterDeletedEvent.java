package com.rpgmanager.character;

/** Publicado quando um personagem é removido. */
public record CharacterDeletedEvent(Long campaignId, Long characterId) {
}
