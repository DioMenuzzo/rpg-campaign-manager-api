package com.rpgmanager.campaign;

import com.rpgmanager.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "campaigns")
public class Campaign extends BaseEntity {

    @Column(nullable = false, length = 120)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "game_system", nullable = false, length = 30)
    private GameSystem gameSystem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CampaignStatus status;

    @Column(name = "game_master", length = 80)
    private String gameMaster;

    @Column(columnDefinition = "text")
    private String description;

    protected Campaign() {
        // exigido pelo JPA
    }

    public Campaign(String name, GameSystem gameSystem, CampaignStatus status, String gameMaster, String description) {
        this.name = name;
        this.gameSystem = gameSystem;
        this.status = status != null ? status : CampaignStatus.PLANNING;
        this.gameMaster = gameMaster;
        this.description = description;
    }

    public void update(String name, GameSystem gameSystem, CampaignStatus status, String gameMaster, String description) {
        this.name = name;
        this.gameSystem = gameSystem;
        if (status != null) {
            this.status = status;
        }
        this.gameMaster = gameMaster;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public GameSystem getGameSystem() {
        return gameSystem;
    }

    public CampaignStatus getStatus() {
        return status;
    }

    public String getGameMaster() {
        return gameMaster;
    }

    public String getDescription() {
        return description;
    }
}
