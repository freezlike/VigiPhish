package fr.dssi.phishingawareness.campaigns.service;

import fr.dssi.phishingawareness.audit.service.AuditService;
import fr.dssi.phishingawareness.campaigns.dto.CampaignDtos.CampaignRequest;
import fr.dssi.phishingawareness.campaigns.dto.CampaignDtos.CampaignResponse;
import fr.dssi.phishingawareness.campaigns.dto.CampaignDtos.ScheduleCampaignRequest;
import fr.dssi.phishingawareness.campaigns.entity.CampaignEntity;
import fr.dssi.phishingawareness.campaigns.entity.CampaignStatus;
import fr.dssi.phishingawareness.campaigns.repository.CampaignRepository;
import fr.dssi.phishingawareness.landingpages.repository.LandingPageRepository;
import fr.dssi.phishingawareness.shared.exception.BadRequestException;
import fr.dssi.phishingawareness.shared.exception.NotFoundException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final LandingPageRepository landingPageRepository;
    private final AuditService auditService;

    public CampaignService(CampaignRepository campaignRepository, LandingPageRepository landingPageRepository, AuditService auditService) {
        this.campaignRepository = campaignRepository;
        this.landingPageRepository = landingPageRepository;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public List<CampaignResponse> list() {
        return campaignRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public CampaignResponse get(UUID id) {
        return toResponse(requireCampaign(id));
    }

    @Transactional
    public CampaignResponse create(CampaignRequest request) {
        validateLandingPage(request.landingPageId());
        Instant now = Instant.now();
        CampaignEntity campaign = new CampaignEntity(
                UUID.randomUUID(),
                request.name(),
                CampaignStatus.DRAFT,
                request.ownerId(),
                request.landingPageId(),
                request.internalDomainAllowlist(),
                request.validationRequired(),
                now,
                now);
        CampaignEntity saved = campaignRepository.save(campaign);
        auditService.record("CAMPAIGN_CREATED", "CAMPAIGN", saved.getId(), Map.of("name", saved.getName()));
        return toResponse(saved);
    }

    @Transactional
    public CampaignResponse update(UUID id, CampaignRequest request) {
        CampaignEntity campaign = requireCampaign(id);
        ensureMutable(campaign);
        validateLandingPage(request.landingPageId());
        campaign.setName(request.name());
        campaign.setOwnerId(request.ownerId());
        campaign.setLandingPageId(request.landingPageId());
        campaign.setInternalDomainAllowlist(request.internalDomainAllowlist());
        campaign.setValidationRequired(request.validationRequired());
        campaign.setUpdatedAt(Instant.now());
        CampaignEntity saved = campaignRepository.save(campaign);
        auditService.record("CAMPAIGN_UPDATED", "CAMPAIGN", id, Map.of("status", saved.getStatus().name()));
        return toResponse(saved);
    }

    @Transactional
    public CampaignResponse submitForValidation(UUID id) {
        CampaignEntity campaign = requireCampaign(id);
        transition(campaign, CampaignStatus.DRAFT, CampaignStatus.PENDING_VALIDATION);
        auditService.record("CAMPAIGN_SUBMITTED_FOR_VALIDATION", "CAMPAIGN", id, Map.of());
        return toResponse(campaignRepository.save(campaign));
    }

    @Transactional
    public CampaignResponse validate(UUID id) {
        CampaignEntity campaign = requireCampaign(id);
        transition(campaign, CampaignStatus.PENDING_VALIDATION, CampaignStatus.VALIDATED);
        campaign.setValidatedAt(Instant.now());
        auditService.record("CAMPAIGN_VALIDATED", "CAMPAIGN", id, Map.of());
        return toResponse(campaignRepository.save(campaign));
    }

    @Transactional
    public CampaignResponse schedule(UUID id, ScheduleCampaignRequest request) {
        CampaignEntity campaign = requireCampaign(id);
        transition(campaign, CampaignStatus.VALIDATED, CampaignStatus.SCHEDULED);
        campaign.setScheduledAt(request.scheduledAt());
        auditService.record("CAMPAIGN_SCHEDULED", "CAMPAIGN", id, Map.of("scheduledAt", request.scheduledAt().toString()));
        return toResponse(campaignRepository.save(campaign));
    }

    @Transactional
    public CampaignResponse start(UUID id) {
        CampaignEntity campaign = requireCampaign(id);
        transition(campaign, CampaignStatus.SCHEDULED, CampaignStatus.RUNNING);
        auditService.record("CAMPAIGN_STARTED", "CAMPAIGN", id, Map.of());
        return toResponse(campaignRepository.save(campaign));
    }

    @Transactional
    public CampaignResponse complete(UUID id) {
        CampaignEntity campaign = requireCampaign(id);
        transition(campaign, CampaignStatus.RUNNING, CampaignStatus.COMPLETED);
        auditService.record("CAMPAIGN_COMPLETED", "CAMPAIGN", id, Map.of());
        return toResponse(campaignRepository.save(campaign));
    }

    @Transactional
    public CampaignResponse cancel(UUID id) {
        CampaignEntity campaign = requireCampaign(id);
        if (campaign.getStatus() == CampaignStatus.COMPLETED || campaign.getStatus() == CampaignStatus.CANCELLED) {
            throw new BadRequestException("INVALID_CAMPAIGN_TRANSITION", "Completed or cancelled campaigns cannot be cancelled");
        }
        campaign.setStatus(CampaignStatus.CANCELLED);
        campaign.setUpdatedAt(Instant.now());
        auditService.record("CAMPAIGN_CANCELLED", "CAMPAIGN", id, Map.of());
        return toResponse(campaignRepository.save(campaign));
    }

    public CampaignEntity requireCampaign(UUID id) {
        return campaignRepository.findById(id).orElseThrow(() -> new NotFoundException("Campaign", id));
    }

    private void ensureMutable(CampaignEntity campaign) {
        if (campaign.getStatus() == CampaignStatus.RUNNING || campaign.getStatus() == CampaignStatus.COMPLETED || campaign.getStatus() == CampaignStatus.CANCELLED) {
            throw new BadRequestException("CAMPAIGN_NOT_MUTABLE", "Campaign cannot be edited in status " + campaign.getStatus());
        }
    }

    private void transition(CampaignEntity campaign, CampaignStatus expected, CampaignStatus next) {
        if (campaign.getStatus() != expected) {
            throw new BadRequestException("INVALID_CAMPAIGN_TRANSITION", "Expected " + expected + " but was " + campaign.getStatus());
        }
        campaign.setStatus(next);
        campaign.setUpdatedAt(Instant.now());
    }

    private void validateLandingPage(UUID landingPageId) {
        if (landingPageId != null && !landingPageRepository.existsById(landingPageId)) {
            throw new NotFoundException("LandingPage", landingPageId);
        }
    }

    private CampaignResponse toResponse(CampaignEntity entity) {
        return new CampaignResponse(
                entity.getId(),
                entity.getName(),
                entity.getStatus(),
                entity.getOwnerId(),
                entity.getValidatorId(),
                entity.getLandingPageId(),
                entity.getInternalDomainAllowlist(),
                entity.isValidationRequired(),
                entity.getValidatedAt(),
                entity.getScheduledAt());
    }
}
