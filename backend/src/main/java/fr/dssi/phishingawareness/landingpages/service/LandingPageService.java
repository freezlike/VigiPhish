package fr.dssi.phishingawareness.landingpages.service;

import fr.dssi.phishingawareness.audit.service.AuditService;
import fr.dssi.phishingawareness.campaigns.entity.CampaignEntity;
import fr.dssi.phishingawareness.campaigns.repository.CampaignRepository;
import fr.dssi.phishingawareness.landingpages.dto.LandingPageDtos.LandingPageRequest;
import fr.dssi.phishingawareness.landingpages.dto.LandingPageDtos.LandingPageResponse;
import fr.dssi.phishingawareness.landingpages.dto.LandingPageDtos.PublicAwarenessPageResponse;
import fr.dssi.phishingawareness.landingpages.entity.LandingPageEntity;
import fr.dssi.phishingawareness.landingpages.repository.LandingPageRepository;
import fr.dssi.phishingawareness.shared.exception.BadRequestException;
import fr.dssi.phishingawareness.shared.exception.NotFoundException;
import fr.dssi.phishingawareness.tracking.entity.CampaignTargetEntity;
import fr.dssi.phishingawareness.tracking.repository.CampaignTargetRepository;
import fr.dssi.phishingawareness.tracking.service.TokenService;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LandingPageService {

    private final LandingPageRepository repository;
    private final CampaignRepository campaignRepository;
    private final CampaignTargetRepository targetRepository;
    private final TokenService tokenService;
    private final AuditService auditService;

    public LandingPageService(LandingPageRepository repository, CampaignRepository campaignRepository, CampaignTargetRepository targetRepository, TokenService tokenService, AuditService auditService) {
        this.repository = repository;
        this.campaignRepository = campaignRepository;
        this.targetRepository = targetRepository;
        this.tokenService = tokenService;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public List<LandingPageResponse> list() { return repository.findAll().stream().map(this::toResponse).toList(); }

    @Transactional(readOnly = true)
    public PublicAwarenessPageResponse getPublicAwarenessPage(String token) {
        CampaignTargetEntity target = targetRepository.findByTokenHash(tokenService.hashToken(token))
                .orElseThrow(() -> new BadRequestException("INVALID_TRACKING_TOKEN", "Invalid or expired tracking token"));
        if (target.getExpiresAt().isBefore(Instant.now())) {
            throw new BadRequestException("INVALID_TRACKING_TOKEN", "Invalid or expired tracking token");
        }

        CampaignEntity campaign = campaignRepository.findById(target.getCampaignId())
                .orElseThrow(() -> new BadRequestException("INVALID_TRACKING_TOKEN", "Invalid or expired tracking token"));
        if (campaign.getLandingPageId() == null) {
            return defaultPublicPage();
        }

        return repository.findById(campaign.getLandingPageId())
                .map(page -> new PublicAwarenessPageResponse(page.getName(), page.getEducationalMessage(), page.getContent()))
                .orElseGet(this::defaultPublicPage);
    }

    @Transactional
    public LandingPageResponse create(LandingPageRequest request) {
        Instant now = Instant.now();
        LandingPageEntity entity = repository.save(new LandingPageEntity(UUID.randomUUID(), request.name(), request.educationalMessage(), request.content(), now, now));
        auditService.record("LANDING_PAGE_CREATED", "LANDING_PAGE", entity.getId(), Map.of("name", entity.getName()));
        return toResponse(entity);
    }

    @Transactional
    public LandingPageResponse update(UUID id, LandingPageRequest request) {
        LandingPageEntity entity = require(id);
        entity.setName(request.name());
        entity.setEducationalMessage(request.educationalMessage());
        entity.setContent(request.content());
        entity.setUpdatedAt(Instant.now());
        auditService.record("LANDING_PAGE_UPDATED", "LANDING_PAGE", id, Map.of("name", entity.getName()));
        return toResponse(repository.save(entity));
    }

    @Transactional
    public void delete(UUID id) {
        repository.delete(require(id));
        auditService.record("LANDING_PAGE_DELETED", "LANDING_PAGE", id, Map.of());
    }

    private LandingPageEntity require(UUID id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("LandingPage", id));
    }

    private LandingPageResponse toResponse(LandingPageEntity entity) {
        return new LandingPageResponse(entity.getId(), entity.getName(), entity.getEducationalMessage(), entity.getContent());
    }

    private PublicAwarenessPageResponse defaultPublicPage() {
        return new PublicAwarenessPageResponse(
                "Sensibilisation interne",
                "Cette page explique les signaux de vigilance et ne collecte aucune donnee sensible.",
                "Verifiez le contexte, controlez les liens, evitez les actions precipitees et signalez les messages suspects via les canaux internes.");
    }
}
