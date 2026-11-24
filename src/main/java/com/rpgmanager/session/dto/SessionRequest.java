package com.rpgmanager.session.dto;

import com.rpgmanager.session.SessionStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;

/**
 * {@code scheduledAt} aceita ISO-8601 com fuso, ex.: {@code 2026-12-05T20:00:00-03:00}.
 */
public record SessionRequest(
        @NotNull @Positive Integer sessionNumber,
        @Size(max = 150) String title,
        OffsetDateTime scheduledAt,
        SessionStatus status,
        @Size(max = 20000) String summary
) {
}
