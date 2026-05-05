package fr.dssi.phishingawareness.tracking.repository;

import fr.dssi.phishingawareness.tracking.entity.TrackingEventEntity;
import fr.dssi.phishingawareness.tracking.entity.TrackingEventType;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrackingEventRepository extends JpaRepository<TrackingEventEntity, UUID> {

    long countByCampaignIdAndEventType(UUID campaignId, TrackingEventType eventType);

    List<TrackingEventEntity> findByCampaignId(UUID campaignId);
}
