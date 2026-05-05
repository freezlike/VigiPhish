package fr.dssi.phishingawareness.reports.service;

import fr.dssi.phishingawareness.campaigns.service.CampaignService;
import fr.dssi.phishingawareness.reports.dto.ReportDtos.CampaignReportResponse;
import fr.dssi.phishingawareness.tracking.entity.TrackingEventType;
import fr.dssi.phishingawareness.tracking.repository.CampaignTargetRepository;
import fr.dssi.phishingawareness.tracking.repository.TrackingEventRepository;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportService {

    private final CampaignService campaignService;
    private final CampaignTargetRepository targetRepository;
    private final TrackingEventRepository eventRepository;

    public ReportService(CampaignService campaignService, CampaignTargetRepository targetRepository, TrackingEventRepository eventRepository) {
        this.campaignService = campaignService;
        this.targetRepository = targetRepository;
        this.eventRepository = eventRepository;
    }

    @Transactional(readOnly = true)
    public CampaignReportResponse campaignSummary(UUID campaignId) {
        campaignService.requireCampaign(campaignId);
        Map<TrackingEventType, Long> counts = new EnumMap<>(TrackingEventType.class);
        for (TrackingEventType eventType : TrackingEventType.values()) {
            counts.put(eventType, eventRepository.countByCampaignIdAndEventType(campaignId, eventType));
        }
        return new CampaignReportResponse(campaignId, targetRepository.findByCampaignId(campaignId).size(), counts);
    }
}
