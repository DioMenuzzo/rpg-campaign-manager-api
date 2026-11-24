package com.rpgmanager.character.dto;

import com.rpgmanager.character.CharacterStatus;
import com.rpgmanager.character.PlayerCharacter;

import java.time.Instant;

public record CharacterResponse(
        Long id,
        Long campaignId,
        String name,
        String playerName,
        String characterClass,
        Integer level,
        CharacterStatus status,
        String backstory,
        Instant createdAt,
        Instant updatedAt
) {
    public static CharacterResponse from(PlayerCharacter pc) {
        return new CharacterResponse(
                pc.getId(),
                pc.getCampaign().getId(),
                pc.getName(),
                pc.getPlayerName(),
                pc.getCharacterClass(),
                pc.getLevel(),
                pc.getStatus(),
                pc.getBackstory(),
                pc.getCreatedAt(),
                pc.getUpdatedAt());
    }
}
