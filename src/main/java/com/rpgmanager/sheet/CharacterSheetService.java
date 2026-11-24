package com.rpgmanager.sheet;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rpgmanager.campaign.GameSystem;
import com.rpgmanager.character.CharacterService;
import com.rpgmanager.common.exception.BusinessRuleException;
import com.rpgmanager.common.exception.ResourceNotFoundException;
import com.rpgmanager.sheet.dto.SheetResponse;
import com.rpgmanager.sheet.model.CharacterSheetData;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Fichas ficam no MongoDB; o personagem e a campanha, no PostgreSQL. Toda operação começa validando
 * no PostgreSQL que o personagem existe e pertence à campanha (404 caso contrário).
 */
@Service
public class CharacterSheetService {

    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() { };

    private final CharacterSheetRepository repository;
    private final CharacterService characterService;
    private final ObjectMapper objectMapper;

    public CharacterSheetService(CharacterSheetRepository repository,
                                 CharacterService characterService,
                                 ObjectMapper objectMapper) {
        this.repository = repository;
        this.characterService = characterService;
        this.objectMapper = objectMapper;
    }

    public SheetResponse get(Long campaignId, Long characterId) {
        characterService.gameSystemOf(campaignId, characterId);
        return toResponse(findSheet(characterId));
    }

    /** Cria ou substitui a ficha do personagem (PUT idempotente). */
    public SheetResponse save(Long campaignId, Long characterId, CharacterSheetData data) {
        GameSystem campaignSystem = characterService.gameSystemOf(campaignId, characterId);
        if (data.system() != campaignSystem) {
            throw new BusinessRuleException("A campanha usa o sistema %s, mas a ficha enviada é do sistema %s"
                    .formatted(campaignSystem, data.system()));
        }

        CharacterSheet sheet = repository.findByCharacterId(characterId)
                .orElseGet(() -> new CharacterSheet(characterId, campaignId));
        Map<String, Object> document = objectMapper.convertValue(data, MAP_TYPE);
        document.putIfAbsent("system", data.system().name());  // discriminador usado na leitura
        sheet.replaceContent(data.system(), document);
        return toResponse(repository.save(sheet));
    }

    public void delete(Long campaignId, Long characterId) {
        characterService.gameSystemOf(campaignId, characterId);
        repository.delete(findSheet(characterId));
    }

    private CharacterSheet findSheet(Long characterId) {
        return repository.findByCharacterId(characterId)
                .orElseThrow(() -> new ResourceNotFoundException("Ficha do personagem", characterId));
    }

    private SheetResponse toResponse(CharacterSheet sheet) {
        CharacterSheetData data = objectMapper.convertValue(sheet.getData(), CharacterSheetData.class);
        return new SheetResponse(
                sheet.getId(),
                sheet.getCharacterId(),
                sheet.getCampaignId(),
                sheet.getSystem(),
                data,
                data.computeDerived(),
                sheet.getVersion(),
                sheet.getUpdatedAt());
    }
}
