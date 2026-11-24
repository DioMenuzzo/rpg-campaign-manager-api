package com.rpgmanager.campaign.dto;

import com.rpgmanager.campaign.Campaign;
import com.rpgmanager.campaign.CampaignStatus;
import com.rpgmanager.campaign.GameSystem;

import java.time.Instant;

public record CampaignResponse(
        Long id,
        String name,
        GameSystem gameSystem,
        CampaignStatus status,
        String gameMaster,
        String description,
        Instant createdAt,
        Instant updatedAt
) {
    public static CampaignResponse from(Campaign campaign) {
        return new CampaignResponse(
                campaign.getId(),
                campaign.getName(),
                campaign.getGameSystem(),
                campaign.getStatus(),
                campaign.getGameMaster(),
                campaign.getDescription(),
                campaign.getCreatedAt(),
                campaign.getUpdatedAt());
    }
}
