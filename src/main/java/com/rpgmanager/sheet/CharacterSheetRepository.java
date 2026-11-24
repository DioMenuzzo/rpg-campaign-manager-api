package com.rpgmanager.sheet;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CharacterSheetRepository extends MongoRepository<CharacterSheet, String> {

    Optional<CharacterSheet> findByCharacterId(Long characterId);

    long deleteByCharacterId(Long characterId);

    long deleteByCampaignId(Long campaignId);
}
