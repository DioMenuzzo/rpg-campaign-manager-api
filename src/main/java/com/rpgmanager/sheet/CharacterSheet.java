package com.rpgmanager.sheet;

import com.rpgmanager.campaign.GameSystem;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

/**
 * Documento MongoDB com a ficha de um personagem.
 *
 * <p>O conteúdo fica em {@code data} como documento genérico. A conversão de/para os tipos de cada
 * sistema ({@code Dnd5eSheet}, {@code OrdemParanormalSheet}...) é feita pelo Jackson no serviço, usando o
 * mesmo contrato da API. Assim o banco não depende de nomes de classes Java, e evoluir o formato de uma
 * ficha não exige migration.</p>
 */
@Document("character_sheets")
public class CharacterSheet {

    @Id
    private String id;

    /** Referência ao personagem no PostgreSQL. Uma ficha por personagem. */
    @Indexed(unique = true)
    private Long characterId;

    @Indexed
    private Long campaignId;

    private GameSystem system;

    private Map<String, Object> data;

    /** Controle de concorrência otimista: duas edições simultâneas não se sobrescrevem em silêncio. */
    @Version
    private Long version;

    private Instant updatedAt;

    protected CharacterSheet() {
        // usado pelo Spring Data
    }

    public CharacterSheet(Long characterId, Long campaignId) {
        this.characterId = characterId;
        this.campaignId = campaignId;
    }

    public void replaceContent(GameSystem system, Map<String, Object> data) {
        this.system = system;
        this.data = data;
        this.updatedAt = Instant.now();
    }

    public String getId() {
        return id;
    }

    public Long getCharacterId() {
        return characterId;
    }

    public Long getCampaignId() {
        return campaignId;
    }

    public GameSystem getSystem() {
        return system;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Long getVersion() {
        return version;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
