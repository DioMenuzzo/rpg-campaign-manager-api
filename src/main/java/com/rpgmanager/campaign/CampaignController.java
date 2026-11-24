package com.rpgmanager.campaign;

import com.rpgmanager.campaign.dto.CampaignRequest;
import com.rpgmanager.campaign.dto.CampaignResponse;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/campaigns")
@Tag(name = "Campanhas")
public class CampaignController {

    private final CampaignService service;

    public CampaignController(CampaignService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lista campanhas (filtros opcionais por sistema e status)")
    public Page<CampaignResponse> list(@RequestParam(required = false) GameSystem gameSystem,
                                       @RequestParam(required = false) CampaignStatus status,
                                       @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        return service.list(gameSystem, status, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma campanha")
    public CampaignResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    @Operation(summary = "Cria uma campanha")
    public ResponseEntity<CampaignResponse> create(@Valid @RequestBody CampaignRequest request) {
        CampaignResponse created = service.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza uma campanha")
    public CampaignResponse update(@PathVariable Long id, @Valid @RequestBody CampaignRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove uma campanha (e, em cascata, personagens, NPCs e sessões)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
