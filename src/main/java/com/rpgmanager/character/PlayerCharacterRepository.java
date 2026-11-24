package com.rpgmanager.character;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlayerCharacterRepository extends JpaRepository<PlayerCharacter, Long> {

    Page<PlayerCharacter> findByCampaignId(Long campaignId, Pageable pageable);

    Optional<PlayerCharacter> findByIdAndCampaignId(Long id, Long campaignId);
}
