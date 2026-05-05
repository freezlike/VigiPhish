package fr.dssi.phishingawareness.tracking.repository;

import fr.dssi.phishingawareness.tracking.entity.CampaignTargetEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CampaignTargetRepository extends JpaRepository<CampaignTargetEntity, UUID> {

    Optional<CampaignTargetEntity> findByTokenHash(String tokenHash);

    List<CampaignTargetEntity> findByCampaignId(UUID campaignId);

    boolean existsByCampaignIdAndUserId(UUID campaignId, UUID userId);
}
