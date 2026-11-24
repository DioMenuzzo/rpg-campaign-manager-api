package com.rpgmanager.sheet.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rpgmanager.campaign.GameSystem;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Size;

import java.util.Map;

/**
 * Ficha de formato livre para sistemas sem modelo próprio (campanhas "OTHER").
 * Aceita qualquer estrutura em {@code fields}, sem validação de regras.
 */
public record FreeformSheet(
        @Size(max = 200) Map<String, Object> fields,
        @Size(max = 5000) String notes
) implements CharacterSheetData {

    /** O MongoDB (via Spring Data) não aceita chaves com '.' ou iniciadas por '$'. */
    @JsonIgnore
    @AssertTrue(message = "As chaves de 'fields' não podem conter '.' nem começar com '$'")
    public boolean isFieldKeysValid() {
        return fields == null || fields.keySet().stream()
                .allMatch(key -> key != null && !key.contains(".") && !key.startsWith("$"));
    }

    @Override
    public GameSystem system() {
        return GameSystem.OTHER;
    }

    @Override
    public Map<String, Object> computeDerived() {
        return Map.of();
    }
}
