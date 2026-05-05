package fr.dssi.phishingawareness.campaigns.controller;

import fr.dssi.phishingawareness.campaigns.dto.CampaignDtos.CampaignRequest;
import fr.dssi.phishingawareness.campaigns.dto.CampaignDtos.CampaignResponse;
import fr.dssi.phishingawareness.campaigns.dto.CampaignDtos.ScheduleCampaignRequest;
import fr.dssi.phishingawareness.campaigns.service.CampaignService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/campaigns")
public class CampaignController {

    private final CampaignService campaignService;

    public CampaignController(CampaignService campaignService) {
        this.campaignService = campaignService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('DSSI_ADMIN','CAMPAIGN_MANAGER','CAMPAIGN_VALIDATOR','REPORT_VIEWER')")
    public List<CampaignResponse> list() { return campaignService.list(); }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('DSSI_ADMIN','CAMPAIGN_MANAGER','CAMPAIGN_VALIDATOR','REPORT_VIEWER')")
    public CampaignResponse get(@PathVariable UUID id) { return campaignService.get(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('DSSI_ADMIN','CAMPAIGN_MANAGER')")
    public CampaignResponse create(@Valid @RequestBody CampaignRequest request) { return campaignService.create(request); }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('DSSI_ADMIN','CAMPAIGN_MANAGER')")
    public CampaignResponse update(@PathVariable UUID id, @Valid @RequestBody CampaignRequest request) { return campaignService.update(id, request); }

    @PostMapping("/{id}/submit-validation")
    @PreAuthorize("hasAnyRole('DSSI_ADMIN','CAMPAIGN_MANAGER')")
    public CampaignResponse submitForValidation(@PathVariable UUID id) { return campaignService.submitForValidation(id); }

    @PostMapping("/{id}/validate")
    @PreAuthorize("hasAnyRole('DSSI_ADMIN','CAMPAIGN_VALIDATOR')")
    public CampaignResponse validate(@PathVariable UUID id) { return campaignService.validate(id); }

    @PostMapping("/{id}/schedule")
    @PreAuthorize("hasAnyRole('DSSI_ADMIN','CAMPAIGN_MANAGER')")
    public CampaignResponse schedule(@PathVariable UUID id, @Valid @RequestBody ScheduleCampaignRequest request) { return campaignService.schedule(id, request); }

    @PostMapping("/{id}/start")
    @PreAuthorize("hasAnyRole('DSSI_ADMIN','CAMPAIGN_MANAGER')")
    public CampaignResponse start(@PathVariable UUID id) { return campaignService.start(id); }

    @PostMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('DSSI_ADMIN','CAMPAIGN_MANAGER')")
    public CampaignResponse complete(@PathVariable UUID id) { return campaignService.complete(id); }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('DSSI_ADMIN','CAMPAIGN_MANAGER')")
    public CampaignResponse cancel(@PathVariable UUID id) { return campaignService.cancel(id); }
}
