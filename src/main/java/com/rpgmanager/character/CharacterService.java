package com.rpgmanager.character;

import com.rpgmanager.campaign.Campaign;
import com.rpgmanager.campaign.CampaignService;
import com.rpgmanager.campaign.GameSystem;
import com.rpgmanager.character.dto.CharacterRequest;
import com.rpgmanager.character.dto.CharacterResponse;
import com.rpgmanager.common.exception.ResourceNotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CharacterService {

    private final PlayerCharacterRepository repository;
    private final CampaignService campaignService;
    private final ApplicationEventPublisher events;

    public CharacterService(PlayerCharacterRepository repository, CampaignService campaignService,
                            ApplicationEventPublisher events) {
        this.repository = repository;
        this.campaignService = campaignService;
        this.events = events;
    }

    public Page<CharacterResponse> list(Long campaignId, Pageable pageable) {
        campaignService.ensureExists(campaignId);
        return repository.findByCampaignId(campaignId, pageable).map(CharacterResponse::from);
    }

    public CharacterResponse get(Long campaignId, Long id) {
        return CharacterResponse.from(findEntity(campaignId, id));
    }

    @Transactional
    public CharacterResponse create(Long campaignId, CharacterRequest request) {
        Campaign campaign = campaignService.findEntity(campaignId);
        PlayerCharacter pc = new PlayerCharacter(campaign, request.name(), request.playerName(),
                request.characterClass(), request.level(), request.status(), request.backstory());
        return CharacterResponse.from(repository.saveAndFlush(pc));
    }

    @Transactional
    public CharacterResponse update(Long campaignId, Long id, CharacterRequest request) {
        PlayerCharacter pc = findEntity(campaignId, id);
        pc.update(request.name(), request.playerName(), request.characterClass(),
                request.level(), request.status(), request.backstory());
        return CharacterResponse.from(repository.saveAndFlush(pc));
    }

    @Transactional
    public void delete(Long campaignId, Long id) {
        repository.delete(findEntity(campaignId, id));
        events.publishEvent(new CharacterDeletedEvent(campaignId, id));
    }

    /**
     * Sistema de RPG da campanha do personagem (404 se o personagem não existir na campanha).
     * Usado pelo módulo de fichas para validar o formato da ficha.
     */
    public GameSystem gameSystemOf(Long campaignId, Long id) {
        return findEntity(campaignId, id).getCampaign().getGameSystem();
    }

    private PlayerCharacter findEntity(Long campaignId, Long id) {
        return repository.findByIdAndCampaignId(id, campaignId)
                .orElseThrow(() -> new ResourceNotFoundException("Personagem", id));
    }
}
