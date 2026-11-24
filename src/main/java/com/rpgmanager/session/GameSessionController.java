package com.rpgmanager.session;

import com.rpgmanager.session.dto.SessionRequest;
import com.rpgmanager.session.dto.SessionResponse;
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
@RequestMapping("/api/campaigns/{campaignId}/sessions")
@Tag(name = "Sessões")
public class GameSessionController {

    private final GameSessionService service;

    public GameSessionController(GameSessionService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lista as sessões de uma campanha (filtro opcional por status)")
    public Page<SessionResponse> list(@PathVariable Long campaignId,
                                      @RequestParam(required = false) SessionStatus status,
                                      @PageableDefault(size = 20, sort = "sessionNumber") Pageable pageable) {
        return service.list(campaignId, status, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma sessão")
    public SessionResponse get(@PathVariable Long campaignId, @PathVariable Long id) {
        return service.get(campaignId, id);
    }

    @PostMapping
    @Operation(summary = "Agenda/registra uma sessão")
    public ResponseEntity<SessionResponse> create(@PathVariable Long campaignId,
                                                  @Valid @RequestBody SessionRequest request) {
        SessionResponse created = service.create(campaignId, request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza uma sessão (remarcar, concluir com resumo, cancelar)")
    public SessionResponse update(@PathVariable Long campaignId, @PathVariable Long id,
                                  @Valid @RequestBody SessionRequest request) {
        return service.update(campaignId, id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove uma sessão")
    public ResponseEntity<Void> delete(@PathVariable Long campaignId, @PathVariable Long id) {
        service.delete(campaignId, id);
        return ResponseEntity.noContent().build();
    }
}
