package com.rpgmanager.character.dto;

import com.rpgmanager.character.CharacterStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CharacterRequest(
        @NotBlank @Size(max = 120) String name,
        @Size(max = 80) String playerName,
        @Size(max = 60) String characterClass,
        @Min(0) @Max(100) Integer level,
        CharacterStatus status,
        @Size(max = 10000) String backstory
) {
}
