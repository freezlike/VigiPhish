package fr.dssi.phishingawareness.reports.controller;

import fr.dssi.phishingawareness.reports.dto.ReportDtos.CampaignReportResponse;
import fr.dssi.phishingawareness.reports.service.ReportService;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/campaigns/{campaignId}/summary")
    @PreAuthorize("hasAnyRole('DSSI_ADMIN','REPORT_VIEWER')")
    public CampaignReportResponse campaignSummary(@PathVariable UUID campaignId) {
        return reportService.campaignSummary(campaignId);
    }
}
