package com.rpgmanager.sheet.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.rpgmanager.campaign.GameSystem;
import com.rpgmanager.sheet.model.dnd.Dnd5eSheet;
import com.rpgmanager.sheet.model.ordem.OrdemParanormalSheet;
import com.rpgmanager.sheet.model.tresdt.TresDTAlphaSheet;

import java.util.Map;

/**
 * Conteúdo de uma ficha de personagem. Cada sistema de RPG tem seu próprio formato;
 * o campo {@code "system"} do JSON escolhe qual implementação usar.
 *
 * <p>Para suportar um novo sistema: criar o record implementando esta interface, registrá-lo em
 * {@link JsonSubTypes} e incluir o valor em {@link GameSystem} (com migration para a constraint do banco).</p>
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "system")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Dnd5eSheet.class, name = "DND_5E"),
        @JsonSubTypes.Type(value = OrdemParanormalSheet.class, name = "ORDEM_PARANORMAL"),
        @JsonSubTypes.Type(value = TresDTAlphaSheet.class, name = "TRES_D_T_ALPHA"),
        @JsonSubTypes.Type(value = FreeformSheet.class, name = "OTHER")
})
public interface CharacterSheetData {

    /** Sistema ao qual a ficha pertence. Deve coincidir com o sistema da campanha. */
    GameSystem system();

    /** Valores calculados a partir da ficha (modificadores, bônus...). Não são persistidos. */
    Map<String, Object> computeDerived();
}
