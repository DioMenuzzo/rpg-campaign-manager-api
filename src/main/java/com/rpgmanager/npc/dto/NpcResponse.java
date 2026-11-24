package com.rpgmanager.npc.dto;

import com.rpgmanager.npc.Npc;
import com.rpgmanager.npc.NpcAttitude;

import java.time.Instant;

public record NpcResponse(
        Long id,
        Long campaignId,
        String name,
        String role,
        String location,
        NpcAttitude attitude,
        boolean alive,
        String description,
        String secretNotes,
        Instant createdAt,
        Instant updatedAt
) {
    public static NpcResponse from(Npc npc) {
        return new NpcResponse(
                npc.getId(),
                npc.getCampaign().getId(),
                npc.getName(),
                npc.getRole(),
                npc.getLocation(),
                npc.getAttitude(),
                npc.isAlive(),
                npc.getDescription(),
                npc.getSecretNotes(),
                npc.getCreatedAt(),
                npc.getUpdatedAt());
    }
}
