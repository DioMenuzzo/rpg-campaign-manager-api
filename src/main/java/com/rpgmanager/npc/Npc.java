package com.rpgmanager.npc;

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

@Entity
@Table(name = "npcs")
public class Npc extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "campaign_id", nullable = false)
    private Campaign campaign;

    @Column(nullable = false, length = 120)
    private String name;

    /** Papel na história: "taverneiro", "vilão principal", "informante"... */
    @Column(length = 120)
    private String role;

    @Column(length = 120)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NpcAttitude attitude;

    @Column(nullable = false)
    private boolean alive;

    @Column(columnDefinition = "text")
    private String description;

    /** Anotações só do mestre (segredos, motivações reais). */
    @Column(name = "secret_notes", columnDefinition = "text")
    private String secretNotes;

    protected Npc() {
        // exigido pelo JPA
    }

    public Npc(Campaign campaign, String name, String role, String location, NpcAttitude attitude,
               Boolean alive, String description, String secretNotes) {
        this.campaign = campaign;
        this.name = name;
        this.role = role;
        this.location = location;
        this.attitude = attitude != null ? attitude : NpcAttitude.UNKNOWN;
        this.alive = alive == null || alive;
        this.description = description;
        this.secretNotes = secretNotes;
    }

    public void update(String name, String role, String location, NpcAttitude attitude,
                       Boolean alive, String description, String secretNotes) {
        this.name = name;
        this.role = role;
        this.location = location;
        if (attitude != null) {
            this.attitude = attitude;
        }
        if (alive != null) {
            this.alive = alive;
        }
        this.description = description;
        this.secretNotes = secretNotes;
    }

    public Campaign getCampaign() {
        return campaign;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public String getLocation() {
        return location;
    }

    public NpcAttitude getAttitude() {
        return attitude;
    }

    public boolean isAlive() {
        return alive;
    }

    public String getDescription() {
        return description;
    }

    public String getSecretNotes() {
        return secretNotes;
    }
}
