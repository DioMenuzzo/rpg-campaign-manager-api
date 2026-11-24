package com.rpgmanager.character;

import com.rpgmanager.character.dto.CharacterRequest;
import com.rpgmanager.character.dto.CharacterResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/campaigns/{campaignId}/characters")
@Tag(name = "Personagens")
public class CharacterController {

    private final CharacterService service;

    public CharacterController(CharacterService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lista os personagens de uma campanha")
    public Page<CharacterResponse> list(@PathVariable Long campaignId,
                                        @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        return service.list(campaignId, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um personagem")
    public CharacterResponse get(@PathVariable Long campaignId, @PathVariable Long id) {
        return service.get(campaignId, id);
    }

    @PostMapping
    @Operation(summary = "Cria um personagem na campanha")
    public ResponseEntity<CharacterResponse> create(@PathVariable Long campaignId,
                                                    @Valid @RequestBody CharacterRequest request) {
        CharacterResponse created = service.create(campaignId, request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um personagem")
    public CharacterResponse update(@PathVariable Long campaignId, @PathVariable Long id,
                                    @Valid @RequestBody CharacterRequest request) {
        return service.update(campaignId, id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um personagem")
    public ResponseEntity<Void> delete(@PathVariable Long campaignId, @PathVariable Long id) {
        service.delete(campaignId, id);
        return ResponseEntity.noContent().build();
    }
}
