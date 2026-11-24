package com.rpgmanager.sheet.dto;

import com.rpgmanager.campaign.GameSystem;
import com.rpgmanager.sheet.model.CharacterSheetData;

import java.time.Instant;
import java.util.Map;

/**
 * Ficha devolvida pela API: o conteúdo enviado pelo usuário ({@code sheet}) e os valores
 * calculados a partir dele ({@code derived}), como modificadores e bônus.
 */
public record SheetResponse(
        String id,
        Long characterId,
        Long campaignId,
        GameSystem system,
        CharacterSheetData sheet,
        Map<String, Object> derived,
        Long version,
        Instant updatedAt
) {
}
