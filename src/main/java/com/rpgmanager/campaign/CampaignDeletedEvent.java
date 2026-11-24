package com.rpgmanager.campaign;

/** Publicado quando uma campanha é removida. Outros módulos (ex.: fichas no MongoDB) reagem a ele. */
public record CampaignDeletedEvent(Long campaignId) {
}
