package com.rpgmanager.session;

import com.rpgmanager.campaign.Campaign;
import com.rpgmanager.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * Sessão de jogo: serve tanto como agenda (SCHEDULED) quanto como histórico (COMPLETED + resumo).
 * Na etapa 4, criar/remarcar uma sessão SCHEDULED publica um evento na fila de notificações.
 */
@Entity
@Table(name = "game_sessions")
public class GameSession extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "campaign_id", nullable = false)
    private Campaign campaign;

    @Column(name = "session_number", nullable = false)
    private Integer sessionNumber;

    @Column(length = 150)
    private String title;

    @Column(name = "scheduled_at")
    private Instant scheduledAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SessionStatus status;

    @Column(columnDefinition = "text")
    private String summary;

    protected GameSession() {
        // exigido pelo JPA
    }

    public GameSession(Campaign campaign, Integer sessionNumber, String title, Instant scheduledAt,
                       SessionStatus status, String summary) {
        this.campaign = campaign;
        this.sessionNumber = sessionNumber;
        this.title = title;
        this.scheduledAt = scheduledAt;
        this.status = status != null ? status : SessionStatus.SCHEDULED;
        this.summary = summary;
    }

    public void update(Integer sessionNumber, String title, Instant scheduledAt,
                       SessionStatus status, String summary) {
        this.sessionNumber = sessionNumber;
        this.title = title;
        this.scheduledAt = scheduledAt;
        if (status != null) {
            this.status = status;
        }
        this.summary = summary;
    }

    public Campaign getCampaign() {
        return campaign;
    }

    public Integer getSessionNumber() {
        return sessionNumber;
    }

    public String getTitle() {
        return title;
    }

    public Instant getScheduledAt() {
        return scheduledAt;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public String getSummary() {
        return summary;
    }
}
