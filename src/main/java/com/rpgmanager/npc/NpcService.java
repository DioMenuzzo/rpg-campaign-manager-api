package com.rpgmanager.npc;

import com.rpgmanager.campaign.Campaign;
import com.rpgmanager.campaign.CampaignService;
import com.rpgmanager.common.exception.ResourceNotFoundException;
import com.rpgmanager.npc.dto.NpcRequest;
import com.rpgmanager.npc.dto.NpcResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class NpcService {

    private final NpcRepository repository;
    private final CampaignService campaignService;

    public NpcService(NpcRepository repository, CampaignService campaignService) {
        this.repository = repository;
        this.campaignService = campaignService;
    }

    public Page<NpcResponse> list(Long campaignId, NpcAttitude attitude, Pageable pageable) {
        campaignService.ensureExists(campaignId);
        return repository.search(campaignId, attitude, pageable).map(NpcResponse::from);
    }

    public NpcResponse get(Long campaignId, Long id) {
        return NpcResponse.from(findEntity(campaignId, id));
    }

    @Transactional
    public NpcResponse create(Long campaignId, NpcRequest request) {
        Campaign campaign = campaignService.findEntity(campaignId);
        Npc npc = new Npc(campaign, request.name(), request.role(), request.location(),
                request.attitude(), request.alive(), request.description(), request.secretNotes());
        return NpcResponse.from(repository.saveAndFlush(npc));
    }

    @Transactional
    public NpcResponse update(Long campaignId, Long id, NpcRequest request) {
        Npc npc = findEntity(campaignId, id);
        npc.update(request.name(), request.role(), request.location(), request.attitude(),
                request.alive(), request.description(), request.secretNotes());
        return NpcResponse.from(repository.saveAndFlush(npc));
    }

    @Transactional
    public void delete(Long campaignId, Long id) {
        repository.delete(findEntity(campaignId, id));
    }

    private Npc findEntity(Long campaignId, Long id) {
        return repository.findByIdAndCampaignId(id, campaignId)
                .orElseThrow(() -> new ResourceNotFoundException("NPC", id));
    }
}
