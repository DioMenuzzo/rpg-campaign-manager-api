package com.rpgmanager.npc.dto;

import com.rpgmanager.npc.NpcAttitude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NpcRequest(
        @NotBlank @Size(max = 120) String name,
        @Size(max = 120) String role,
        @Size(max = 120) String location,
        NpcAttitude attitude,
        Boolean alive,
        @Size(max = 10000) String description,
        @Size(max = 10000) String secretNotes
) {
}
