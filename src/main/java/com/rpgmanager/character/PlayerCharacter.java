package com.rpgmanager.character;

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

/**
 * Personagem de jogador (PC). Chamado PlayerCharacter para não colidir com {@code java.lang.Character}.
 * Os dados "fixos" ficam aqui; a ficha completa (atributos, perícias...) vai para o MongoDB na etapa 2.
 */
@Entity
@Table(name = "player_characters")
public class PlayerCharacter extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "campaign_id", nullable = false)
    private Campaign campaign;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(name = "player_name", length = 80)
    private String playerName;

    /** Classe/trilha livre: "Mago", "Paladino", "Ocultista"... */
    @Column(name = "character_class", length = 60)
    private String characterClass;

    /** Nível (D&D) ou NEX convertido (Ordem Paranormal). */
    @Column(name = "level")
    private Integer level;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CharacterStatus status;

    @Column(columnDefinition = "text")
    private String backstory;

    protected PlayerCharacter() {
        // exigido pelo JPA
    }

    public PlayerCharacter(Campaign campaign, String name, String playerName, String characterClass,
                           Integer level, CharacterStatus status, String backstory) {
        this.campaign = campaign;
        this.name = name;
        this.playerName = playerName;
        this.characterClass = characterClass;
        this.level = level;
        this.status = status != null ? status : CharacterStatus.ALIVE;
        this.backstory = backstory;
    }

    public void update(String name, String playerName, String characterClass,
                       Integer level, CharacterStatus status, String backstory) {
        this.name = name;
        this.playerName = playerName;
        this.characterClass = characterClass;
        this.level = level;
        if (status != null) {
            this.status = status;
        }
        this.backstory = backstory;
    }

    public Campaign getCampaign() {
        return campaign;
    }

    public String getName() {
        return name;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getCharacterClass() {
        return characterClass;
    }

    public Integer getLevel() {
        return level;
    }

    public CharacterStatus getStatus() {
        return status;
    }

    public String getBackstory() {
        return backstory;
    }
}
