package com.rpgmanager.campaign.dto;

import com.rpgmanager.campaign.CampaignStatus;
import com.rpgmanager.campaign.GameSystem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Corpo de criação/atualização de campanha. {@code status} é opcional:
 * na criação assume PLANNING; na atualização, se omitido, mantém o atual.
 */
public record CampaignRequest(
        @NotBlank @Size(max = 120) String name,
        @NotNull GameSystem gameSystem,
        CampaignStatus status,
        @Size(max = 80) String gameMaster,
        @Size(max = 5000) String description
) {
}
