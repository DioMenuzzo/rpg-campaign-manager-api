package com.rpgmanager.sheet;

import com.rpgmanager.sheet.dto.SheetResponse;
import com.rpgmanager.sheet.model.CharacterSheetData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/campaigns/{campaignId}/characters/{characterId}/sheet")
@Tag(name = "Fichas de personagem")
public class CharacterSheetController {

    private final CharacterSheetService service;

    public CharacterSheetController(CharacterSheetService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Busca a ficha do personagem, com valores calculados (modificadores, bônus...)")
    public SheetResponse get(@PathVariable Long campaignId, @PathVariable Long characterId) {
        return service.get(campaignId, characterId);
    }

    @PutMapping
    @Operation(summary = "Cria ou substitui a ficha. O campo 'system' deve ser o mesmo da campanha "
            + "(DND_5E, ORDEM_PARANORMAL ou OTHER)")
    public SheetResponse save(@PathVariable Long campaignId, @PathVariable Long characterId,
                              @Valid @RequestBody CharacterSheetData sheet) {
        return service.save(campaignId, characterId, sheet);
    }

    @DeleteMapping
    @Operation(summary = "Remove a ficha (o personagem continua existindo)")
    public ResponseEntity<Void> delete(@PathVariable Long campaignId, @PathVariable Long characterId) {
        service.delete(campaignId, characterId);
        return ResponseEntity.noContent().build();
    }
}
