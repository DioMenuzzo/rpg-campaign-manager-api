package com.rpgmanager.npc;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface NpcRepository extends JpaRepository<Npc, Long> {

    @Query("""
            select n from Npc n
            where n.campaign.id = :campaignId
              and (:attitude is null or n.attitude = :attitude)
            """)
    Page<Npc> search(@Param("campaignId") Long campaignId,
                     @Param("attitude") NpcAttitude attitude,
                     Pageable pageable);

    Optional<Npc> findByIdAndCampaignId(Long id, Long campaignId);
}
