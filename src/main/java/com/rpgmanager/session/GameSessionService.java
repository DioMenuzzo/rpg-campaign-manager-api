package com.rpgmanager.session;

import com.rpgmanager.campaign.Campaign;
import com.rpgmanager.campaign.CampaignService;
import com.rpgmanager.common.exception.BusinessConflictException;
import com.rpgmanager.common.exception.ResourceNotFoundException;
import com.rpgmanager.session.dto.SessionRequest;
import com.rpgmanager.session.dto.SessionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.OffsetDateTime;

@Service
@Transactional(readOnly = true)
public class GameSessionService {

    private final GameSessionRepository repository;
    private final CampaignService campaignService;

    public GameSessionService(GameSessionRepository repository, CampaignService campaignService) {
        this.repository = repository;
        this.campaignService = campaignService;
    }

    public Page<SessionResponse> list(Long campaignId, SessionStatus status, Pageable pageable) {
        campaignService.ensureExists(campaignId);
        return repository.search(campaignId, status, pageable).map(SessionResponse::from);
    }

    public SessionResponse get(Long campaignId, Long id) {
        return SessionResponse.from(findEntity(campaignId, id));
    }

    @Transactional
    public SessionResponse create(Long campaignId, SessionRequest request) {
        Campaign campaign = campaignService.findEntity(campaignId);
        if (repository.existsByCampaignIdAndSessionNumber(campaignId, request.sessionNumber())) {
            throw duplicateNumber(request.sessionNumber());
        }
        GameSession session = new GameSession(campaign, request.sessionNumber(), request.title(),
                toInstant(request.scheduledAt()), request.status(), request.summary());
        return SessionResponse.from(repository.saveAndFlush(session));
    }

    @Transactional
    public SessionResponse update(Long campaignId, Long id, SessionRequest request) {
        GameSession session = findEntity(campaignId, id);
        if (repository.existsByCampaignIdAndSessionNumberAndIdNot(campaignId, request.sessionNumber(), id)) {
            throw duplicateNumber(request.sessionNumber());
        }
        session.update(request.sessionNumber(), request.title(), toInstant(request.scheduledAt()),
                request.status(), request.summary());
        return SessionResponse.from(repository.saveAndFlush(session));
    }

    @Transactional
    public void delete(Long campaignId, Long id) {
        repository.delete(findEntity(campaignId, id));
    }

    private GameSession findEntity(Long campaignId, Long id) {
        return repository.findByIdAndCampaignId(id, campaignId)
                .orElseThrow(() -> new ResourceNotFoundException("Sessão", id));
    }

    private static Instant toInstant(OffsetDateTime dateTime) {
        return dateTime != null ? dateTime.toInstant() : null;
    }

    private static BusinessConflictException duplicateNumber(Integer number) {
        return new BusinessConflictException("Já existe uma sessão de número %d nesta campanha".formatted(number));
    }
}
