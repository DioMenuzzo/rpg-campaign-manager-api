package com.rpgmanager.campaign;

import com.rpgmanager.campaign.dto.CampaignRequest;
import com.rpgmanager.campaign.dto.CampaignResponse;
import com.rpgmanager.common.exception.ResourceNotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CampaignService {

    private final CampaignRepository repository;
    private final ApplicationEventPublisher events;

    public CampaignService(CampaignRepository repository, ApplicationEventPublisher events) {
        this.repository = repository;
        this.events = events;
    }

    public Page<CampaignResponse> list(GameSystem gameSystem, CampaignStatus status, Pageable pageable) {
        return repository.search(gameSystem, status, pageable).map(CampaignResponse::from);
    }

    public CampaignResponse get(Long id) {
        return CampaignResponse.from(findEntity(id));
    }

    @Transactional
    public CampaignResponse create(CampaignRequest request) {
        Campaign campaign = new Campaign(
                request.name(), request.gameSystem(), request.status(),
                request.gameMaster(), request.description());
        return CampaignResponse.from(repository.saveAndFlush(campaign));
    }

    @Transactional
    public CampaignResponse update(Long id, CampaignRequest request) {
        Campaign campaign = findEntity(id);
        campaign.update(request.name(), request.gameSystem(), request.status(),
                request.gameMaster(), request.description());
        return CampaignResponse.from(repository.saveAndFlush(campaign));
    }

    /**
     * Personagens, NPCs e sessões são removidos em cascata pelo PostgreSQL (ON DELETE CASCADE).
     * As fichas no MongoDB são removidas por um listener do evento, após o commit.
     */
    @Transactional
    public void delete(Long id) {
        repository.delete(findEntity(id));
        events.publishEvent(new CampaignDeletedEvent(id));
    }

    /** Usado pelos outros módulos para obter a campanha-pai (404 se não existir). */
    public Campaign findEntity(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campanha", id));
    }

    public void ensureExists(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Campanha", id);
        }
    }
}
