package fr.dssi.phishingawareness.campaigns.repository;

import fr.dssi.phishingawareness.campaigns.entity.CampaignEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CampaignRepository extends JpaRepository<CampaignEntity, UUID> {
}
