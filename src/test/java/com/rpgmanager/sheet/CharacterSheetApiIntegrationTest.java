package com.rpgmanager.sheet;

import com.jayway.jsonpath.JsonPath;
import com.rpgmanager.support.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CharacterSheetApiIntegrationTest extends IntegrationTestBase {

    private static final String DND_SHEET = """
            {
              "system": "DND_5E",
              "species": "Halfling",
              "classes": [{"name": "Rogue", "subclass": "Thief", "level": 5}],
              "abilities": {"strength": 8, "dexterity": 16, "constitution": 14,
                            "intelligence": 10, "wisdom": 13, "charisma": 12},
              "hitPoints": {"max": 33, "current": 20},
              "armorClass": 15,
              "savingThrowProficiencies": ["DEX", "INT"],
              "skills": [{"skill": "STEALTH", "level": "EXPERTISE"}, {"skill": "PERCEPTION"}],
              "inventory": [{"name": "Adaga", "quantity": 2}]
            }
            """;

    private static final String ORDEM_SHEET = """
            {
              "system": "ORDEM_PARANORMAL",
              "characterClass": "OCULTISTA",
              "nex": 10,
              "attributes": {"agility": 1, "strength": 1, "intellect": 3, "presence": 2, "vigor": 1},
              "hitPoints": {"max": 13, "current": 13},
              "effortPoints": {"max": 6, "current": 6},
              "sanity": {"max": 24, "current": 18},
              "skills": [{"skill": "OCULTISMO", "training": "TREINADO"}],
              "rituals": [{"name": "Decadência", "element": "MORTE", "circle": 1}]
            }
            """;

    @Autowired
    private CharacterSheetRepository sheetRepository;

    @Test
    void savesDndSheetAndReturnsDerivedValues() throws Exception {
        long campaignId = createCampaign("DND_5E");
        long characterId = createCharacter(campaignId);

        mockMvc.perform(put(sheetUrl(campaignId, characterId))
                        .contentType(MediaType.APPLICATION_JSON).content(DND_SHEET))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.system").value("DND_5E"))
                .andExpect(jsonPath("$.sheet.system").value("DND_5E"))
                .andExpect(jsonPath("$.sheet.species").value("Halfling"))
                .andExpect(jsonPath("$.derived.proficiencyBonus").value(3))
                .andExpect(jsonPath("$.derived.abilityModifiers.DEX").value(3))
                .andExpect(jsonPath("$.derived.skills.STEALTH").value(9))
                .andExpect(jsonPath("$.derived.passivePerception").value(14))
                .andExpect(jsonPath("$.version").value(0));

        // Ler de volta: o documento salvo no Mongo é convertido de novo no tipo certo
        mockMvc.perform(get(sheetUrl(campaignId, characterId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sheet.hitPoints.current").value(20))
                .andExpect(jsonPath("$.sheet.inventory[0].name").value("Adaga"));

        // Substituir incrementa a versão
        mockMvc.perform(put(sheetUrl(campaignId, characterId))
                        .contentType(MediaType.APPLICATION_JSON).content(DND_SHEET))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.version").value(1));
    }

    @Test
    void savesOrdemParanormalSheet() throws Exception {
        long campaignId = createCampaign("ORDEM_PARANORMAL");
        long characterId = createCharacter(campaignId);

        mockMvc.perform(put(sheetUrl(campaignId, characterId))
                        .contentType(MediaType.APPLICATION_JSON).content(ORDEM_SHEET))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sheet.characterClass").value("OCULTISTA"))
                .andExpect(jsonPath("$.derived.trainedSkillBonuses.OCULTISMO").value(5))
                .andExpect(jsonPath("$.derived.ritualsByElement.MORTE").value(1))
                .andExpect(jsonPath("$.derived.sanityPercent").value(75));
    }

    @Test
    void rejectsSheetFromAnotherSystem() throws Exception {
        long campaignId = createCampaign("DND_5E");
        long characterId = createCharacter(campaignId);

        mockMvc.perform(put(sheetUrl(campaignId, characterId))
                        .contentType(MediaType.APPLICATION_JSON).content(ORDEM_SHEET))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void validatesSheetFields() throws Exception {
        long campaignId = createCampaign("DND_5E");
        long characterId = createCharacter(campaignId);
        String invalid = DND_SHEET.replace("\"strength\": 8", "\"strength\": 42")
                .replace("\"current\": 20", "\"current\": 50");

        mockMvc.perform(put(sheetUrl(campaignId, characterId))
                        .contentType(MediaType.APPLICATION_JSON).content(invalid))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors['abilities.strength']").exists())
                .andExpect(jsonPath("$.errors['hitPoints.currentWithinMax']").exists());
    }

    @Test
    void returns404WhenSheetDoesNotExist() throws Exception {
        long campaignId = createCampaign("DND_5E");
        long characterId = createCharacter(campaignId);

        mockMvc.perform(get(sheetUrl(campaignId, characterId)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletingCharacterRemovesItsSheetFromMongo() throws Exception {
        long campaignId = createCampaign("DND_5E");
        long characterId = createCharacter(campaignId);
        mockMvc.perform(put(sheetUrl(campaignId, characterId))
                        .contentType(MediaType.APPLICATION_JSON).content(DND_SHEET))
                .andExpect(status().isOk());
        assertThat(sheetRepository.findByCharacterId(characterId)).isPresent();

        mockMvc.perform(delete("/api/campaigns/{cid}/characters/{id}", campaignId, characterId))
                .andExpect(status().isNoContent());

        assertThat(sheetRepository.findByCharacterId(characterId)).isEmpty();
    }

    private static String sheetUrl(long campaignId, long characterId) {
        return "/api/campaigns/%d/characters/%d/sheet".formatted(campaignId, characterId);
    }

    private long createCampaign(String gameSystem) throws Exception {
        String body = mockMvc.perform(post("/api/campaigns")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Campanha de fichas", "gameSystem": "%s"}
                                """.formatted(gameSystem)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(body, "$.id")).longValue();
    }

    private long createCharacter(long campaignId) throws Exception {
        String body = mockMvc.perform(post("/api/campaigns/{cid}/characters", campaignId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Personagem de teste"}
                                """))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(body, "$.id")).longValue();
    }
}
