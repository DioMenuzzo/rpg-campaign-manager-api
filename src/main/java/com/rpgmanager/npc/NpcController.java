package com.rpgmanager.npc;

import com.rpgmanager.npc.dto.NpcRequest;
import com.rpgmanager.npc.dto.NpcResponse;
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
@RequestMapping("/api/campaigns/{campaignId}/npcs")
@Tag(name = "NPCs")
public class NpcController {

    private final NpcService service;

    public NpcController(NpcService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lista os NPCs de uma campanha (filtro opcional por atitude)")
    public Page<NpcResponse> list(@PathVariable Long campaignId,
                                  @RequestParam(required = false) NpcAttitude attitude,
                                  @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        return service.list(campaignId, attitude, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um NPC")
    public NpcResponse get(@PathVariable Long campaignId, @PathVariable Long id) {
        return service.get(campaignId, id);
    }

    @PostMapping
    @Operation(summary = "Cria um NPC na campanha")
    public ResponseEntity<NpcResponse> create(@PathVariable Long campaignId,
                                              @Valid @RequestBody NpcRequest request) {
        NpcResponse created = service.create(campaignId, request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um NPC")
    public NpcResponse update(@PathVariable Long campaignId, @PathVariable Long id,
                              @Valid @RequestBody NpcRequest request) {
        return service.update(campaignId, id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um NPC")
    public ResponseEntity<Void> delete(@PathVariable Long campaignId, @PathVariable Long id) {
        service.delete(campaignId, id);
        return ResponseEntity.noContent().build();
    }
}
