package com.rpgmanager.campaign;

import com.rpgmanager.campaign.dto.CampaignRequest;
import com.rpgmanager.campaign.dto.CampaignResponse;
import com.rpgmanager.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CampaignService {

    private final CampaignRepository repository;

    public CampaignService(CampaignRepository repository) {
        this.repository = repository;
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

    /** Personagens, NPCs e sessões são removidos em cascata pelo banco (ON DELETE CASCADE). */
    @Transactional
    public void delete(Long id) {
        repository.delete(findEntity(id));
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
