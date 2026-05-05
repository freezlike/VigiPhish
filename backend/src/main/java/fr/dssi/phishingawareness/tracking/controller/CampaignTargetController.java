package fr.dssi.phishingawareness.tracking.controller;

import fr.dssi.phishingawareness.tracking.dto.TrackingDtos.CampaignTargetCreatedResponse;
import fr.dssi.phishingawareness.tracking.dto.TrackingDtos.CampaignTargetEmailImportRequest;
import fr.dssi.phishingawareness.tracking.dto.TrackingDtos.CampaignTargetEmailImportResponse;
import fr.dssi.phishingawareness.tracking.dto.TrackingDtos.CampaignTargetRequest;
import fr.dssi.phishingawareness.tracking.dto.TrackingDtos.CampaignTargetResponse;
import fr.dssi.phishingawareness.tracking.service.CampaignTargetService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/campaigns/{campaignId}/targets")
@PreAuthorize("hasAnyRole('DSSI_ADMIN','CAMPAIGN_MANAGER')")
public class CampaignTargetController {

    private final CampaignTargetService service;

    public CampaignTargetController(CampaignTargetService service) {
        this.service = service;
    }

    @GetMapping
    public List<CampaignTargetResponse> list(@PathVariable UUID campaignId) {
        return service.list(campaignId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CampaignTargetCreatedResponse create(@PathVariable UUID campaignId, @Valid @RequestBody CampaignTargetRequest request) {
        return service.create(campaignId, request);
    }

    @PostMapping("/import-emails")
    @ResponseStatus(HttpStatus.CREATED)
    public CampaignTargetEmailImportResponse importEmails(@PathVariable UUID campaignId, @Valid @RequestBody CampaignTargetEmailImportRequest request) {
        return service.importEmails(campaignId, request);
    }
}
