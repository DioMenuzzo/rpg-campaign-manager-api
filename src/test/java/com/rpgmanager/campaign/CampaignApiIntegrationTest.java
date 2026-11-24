package com.rpgmanager.campaign;

import com.jayway.jsonpath.JsonPath;
import com.rpgmanager.support.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CampaignApiIntegrationTest extends IntegrationTestBase {

    @Test
    void createsAndFetchesCampaign() throws Exception {
        long id = createCampaign("Campanha de teste", "DND_5E");

        mockMvc.perform(get("/api/campaigns/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Campanha de teste"))
                .andExpect(jsonPath("$.status").value("PLANNING"))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void rejectsInvalidCampaignWithFieldErrors() throws Exception {
        mockMvc.perform(post("/api/campaigns")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": ""}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").exists())
                .andExpect(jsonPath("$.errors.gameSystem").exists());
    }

    @Test
    void returnsProblemDetailWhenCampaignDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/campaigns/{id}", 999_999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Recurso não encontrado"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void characterIsScopedToItsCampaign() throws Exception {
        long campaignA = createCampaign("Campanha A", "DND_5E");
        long campaignB = createCampaign("Campanha B", "ORDEM_PARANORMAL");

        long characterId = idFrom(mockMvc.perform(post("/api/campaigns/{cid}/characters", campaignA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Thalia", "characterClass": "Maga", "level": 3}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("ALIVE"))
                .andExpect(jsonPath("$.campaignId").value(campaignA))
                .andReturn());

        mockMvc.perform(get("/api/campaigns/{cid}/characters/{id}", campaignB, characterId))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/campaigns/{cid}/characters", campaignA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Thalia"))
                .andExpect(jsonPath("$.page.totalElements").value(1));
    }

    @Test
    void deletingCampaignCascadesToNpcs() throws Exception {
        long campaignId = createCampaign("Campanha efêmera", "OTHER");

        mockMvc.perform(post("/api/campaigns/{cid}/npcs", campaignId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Taverneiro Bram", "attitude": "ALLY"}
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/api/campaigns/{id}", campaignId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/campaigns/{cid}/npcs", campaignId))
                .andExpect(status().isNotFound());
    }

    @Test
    void rejectsDuplicateSessionNumber() throws Exception {
        long campaignId = createCampaign("Campanha com sessões", "DND_5E");
        String body = """
                {"sessionNumber": 1, "title": "Sessão zero", "scheduledAt": "2026-12-05T20:00:00-03:00"}
                """;

        mockMvc.perform(post("/api/campaigns/{cid}/sessions", campaignId)
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SCHEDULED"))
                .andExpect(jsonPath("$.scheduledAt").value("2026-12-05T23:00:00Z"));

        mockMvc.perform(post("/api/campaigns/{cid}/sessions", campaignId)
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict());
    }

    private long createCampaign(String name, String gameSystem) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/campaigns")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "%s", "gameSystem": "%s", "gameMaster": "Mestre"}
                                """.formatted(name, gameSystem)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/campaigns/")))
                .andReturn();
        return idFrom(result);
    }

    private static long idFrom(MvcResult result) throws Exception {
        Number id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");
        return id.longValue();
    }
}
