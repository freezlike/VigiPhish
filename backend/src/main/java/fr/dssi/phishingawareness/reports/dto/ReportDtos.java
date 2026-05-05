package fr.dssi.phishingawareness.reports.dto;

import fr.dssi.phishingawareness.tracking.entity.TrackingEventType;
import java.util.Map;
import java.util.UUID;

public final class ReportDtos {

    private ReportDtos() {
    }

    public record CampaignReportResponse(
            UUID campaignId,
            long targets,
            Map<TrackingEventType, Long> events
    ) {
    }
}
