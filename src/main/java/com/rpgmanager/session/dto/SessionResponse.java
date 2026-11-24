package com.rpgmanager.session.dto;

import com.rpgmanager.session.GameSession;
import com.rpgmanager.session.SessionStatus;

import java.time.Instant;

public record SessionResponse(
        Long id,
        Long campaignId,
        Integer sessionNumber,
        String title,
        Instant scheduledAt,
        SessionStatus status,
        String summary,
        Instant createdAt,
        Instant updatedAt
) {
    public static SessionResponse from(GameSession session) {
        return new SessionResponse(
                session.getId(),
                session.getCampaign().getId(),
                session.getSessionNumber(),
                session.getTitle(),
                session.getScheduledAt(),
                session.getStatus(),
                session.getSummary(),
                session.getCreatedAt(),
                session.getUpdatedAt());
    }
}
