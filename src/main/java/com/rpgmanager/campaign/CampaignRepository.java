package com.rpgmanager.campaign;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {

    /** Filtros opcionais: parâmetros nulos são ignorados. */
    @Query("""
            select c from Campaign c
            where (:gameSystem is null or c.gameSystem = :gameSystem)
              and (:status is null or c.status = :status)
            """)
    Page<Campaign> search(@Param("gameSystem") GameSystem gameSystem,
                          @Param("status") CampaignStatus status,
                          Pageable pageable);
}
