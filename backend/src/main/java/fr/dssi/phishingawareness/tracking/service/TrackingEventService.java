package fr.dssi.phishingawareness.tracking.service;

import fr.dssi.phishingawareness.shared.exception.BadRequestException;
import fr.dssi.phishingawareness.tracking.dto.TrackingDtos.PublicTrackingEventRequest;
import fr.dssi.phishingawareness.tracking.dto.TrackingDtos.PublicTrackingEventResponse;
import fr.dssi.phishingawareness.tracking.entity.CampaignTargetEntity;
import fr.dssi.phishingawareness.tracking.entity.TrackingEventEntity;
import fr.dssi.phishingawareness.tracking.entity.TrackingEventType;
import fr.dssi.phishingawareness.tracking.repository.CampaignTargetRepository;
import fr.dssi.phishingawareness.tracking.repository.TrackingEventRepository;
import java.time.Instant;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TrackingEventService {

    private static final Set<TrackingEventType> PUBLIC_EVENT_TYPES = EnumSet.of(
            TrackingEventType.EMAIL_OPENED,
            TrackingEventType.LINK_CLICKED,
            TrackingEventType.SUBMITTED_FORM,
            TrackingEventType.TRAINING_VIEWED,
            TrackingEventType.TRAINING_COMPLETED,
            TrackingEventType.QUIZ_COMPLETED);

    private final TrackingEventRepository eventRepository;
    private final CampaignTargetRepository targetRepository;
    private final TokenService tokenService;

    public TrackingEventService(TrackingEventRepository eventRepository, CampaignTargetRepository targetRepository, TokenService tokenService) {
        this.eventRepository = eventRepository;
        this.targetRepository = targetRepository;
        this.tokenService = tokenService;
    }

    @Transactional
    public PublicTrackingEventResponse recordPublicEvent(PublicTrackingEventRequest request) {
        if (!PUBLIC_EVENT_TYPES.contains(request.eventType())) {
            throw new BadRequestException("TRACKING_EVENT_NOT_ALLOWED", "This event type is not allowed on public endpoints");
        }

        CampaignTargetEntity target = targetRepository.findByTokenHash(tokenService.hashToken(request.token()))
                .orElseThrow(() -> new BadRequestException("INVALID_TRACKING_TOKEN", "Invalid or expired tracking token"));
        if (target.getExpiresAt().isBefore(Instant.now())) {
            throw new BadRequestException("INVALID_TRACKING_TOKEN", "Invalid or expired tracking token");
        }

        Instant now = Instant.now();
        Map<String, Object> metadata = request.eventType() == TrackingEventType.SUBMITTED_FORM
                ? Map.of("formContentStored", false)
                : Map.of("source", "public");
        eventRepository.save(new TrackingEventEntity(
                UUID.randomUUID(),
                target.getCampaignId(),
                target.getUserId(),
                request.eventType(),
                now,
                metadata));
        target.setLastEventAt(now);
        targetRepository.save(target);
        return new PublicTrackingEventResponse("RECORDED");
    }

    @Transactional
    public void recordAdminEvent(UUID campaignId, UUID userId, TrackingEventType eventType) {
        eventRepository.save(new TrackingEventEntity(UUID.randomUUID(), campaignId, userId, eventType, Instant.now(), Map.of("source", "admin")));
    }
}
