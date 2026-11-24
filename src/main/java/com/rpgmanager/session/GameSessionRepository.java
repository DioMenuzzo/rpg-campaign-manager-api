package com.rpgmanager.session;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface GameSessionRepository extends JpaRepository<GameSession, Long> {

    @Query("""
            select s from GameSession s
            where s.campaign.id = :campaignId
              and (:status is null or s.status = :status)
            """)
    Page<GameSession> search(@Param("campaignId") Long campaignId,
                             @Param("status") SessionStatus status,
                             Pageable pageable);

    Optional<GameSession> findByIdAndCampaignId(Long id, Long campaignId);

    boolean existsByCampaignIdAndSessionNumber(Long campaignId, Integer sessionNumber);

    boolean existsByCampaignIdAndSessionNumberAndIdNot(Long campaignId, Integer sessionNumber, Long id);
}
