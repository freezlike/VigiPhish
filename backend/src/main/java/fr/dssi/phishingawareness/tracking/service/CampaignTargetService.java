package fr.dssi.phishingawareness.tracking.service;

import fr.dssi.phishingawareness.audit.service.AuditService;
import fr.dssi.phishingawareness.campaigns.service.CampaignService;
import fr.dssi.phishingawareness.shared.exception.BadRequestException;
import fr.dssi.phishingawareness.tracking.dto.TrackingDtos.CampaignTargetCreatedResponse;
import fr.dssi.phishingawareness.tracking.dto.TrackingDtos.CampaignTargetEmailImportRequest;
import fr.dssi.phishingawareness.tracking.dto.TrackingDtos.CampaignTargetEmailImportResponse;
import fr.dssi.phishingawareness.tracking.dto.TrackingDtos.CampaignTargetRequest;
import fr.dssi.phishingawareness.tracking.dto.TrackingDtos.CampaignTargetResponse;
import fr.dssi.phishingawareness.tracking.entity.CampaignTargetEntity;
import fr.dssi.phishingawareness.tracking.repository.CampaignTargetRepository;
import fr.dssi.phishingawareness.users.entity.UserEntity;
import fr.dssi.phishingawareness.users.service.UserService;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CampaignTargetService {

    private final CampaignTargetRepository targetRepository;
    private final CampaignService campaignService;
    private final UserService userService;
    private final TokenService tokenService;
    private final AuditService auditService;

    public CampaignTargetService(CampaignTargetRepository targetRepository, CampaignService campaignService, UserService userService, TokenService tokenService, AuditService auditService) {
        this.targetRepository = targetRepository;
        this.campaignService = campaignService;
        this.userService = userService;
        this.tokenService = tokenService;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public List<CampaignTargetResponse> list(UUID campaignId) {
        campaignService.requireCampaign(campaignId);
        return targetRepository.findByCampaignId(campaignId).stream().map(this::toResponse).toList();
    }

    @Transactional
    public CampaignTargetCreatedResponse create(UUID campaignId, CampaignTargetRequest request) {
        campaignService.requireCampaign(campaignId);
        userService.requireUser(request.userId());
        if (targetRepository.existsByCampaignIdAndUserId(campaignId, request.userId())) {
            throw new BadRequestException("CAMPAIGN_TARGET_ALREADY_EXISTS", "User is already targeted by this campaign");
        }
        return createTarget(campaignId, request.userId(), request.expiresAt());
    }

    @Transactional
    public CampaignTargetEmailImportResponse importEmails(UUID campaignId, CampaignTargetEmailImportRequest request) {
        campaignService.requireCampaign(campaignId);
        LinkedHashSet<String> uniqueEmails = new LinkedHashSet<>();
        request.emails().forEach(email -> uniqueEmails.add(email.trim().toLowerCase()));

        int createdUsers = 0;
        int skippedExistingTargets = 0;
        List<CampaignTargetCreatedResponse> targets = new ArrayList<>();
        for (String email : uniqueEmails) {
            boolean existingUser = userService.existsByEmail(email);
            UserEntity user = userService.findOrCreateLearner(email);
            if (!existingUser) {
                createdUsers++;
            }
            if (targetRepository.existsByCampaignIdAndUserId(campaignId, user.getId())) {
                skippedExistingTargets++;
                continue;
            }
            targets.add(createTarget(campaignId, user.getId(), request.expiresAt()));
        }

        auditService.record("CAMPAIGN_TARGETS_IMPORTED_FROM_EMAILS", "CAMPAIGN", campaignId, Map.of(
                "requested", request.emails().size(),
                "createdUsers", createdUsers,
                "createdTargets", targets.size(),
                "skippedExistingTargets", skippedExistingTargets));
        return new CampaignTargetEmailImportResponse(request.emails().size(), createdUsers, targets.size(), skippedExistingTargets, targets);
    }

    private CampaignTargetCreatedResponse createTarget(UUID campaignId, UUID userId, Instant requestedExpiresAt) {
        Instant expiresAt = requestedExpiresAt == null ? Instant.now().plus(14, ChronoUnit.DAYS) : requestedExpiresAt;
        String rawToken = tokenService.generateRawToken();
        CampaignTargetEntity target = new CampaignTargetEntity(
                UUID.randomUUID(),
                campaignId,
                userId,
                tokenService.hashToken(rawToken),
                expiresAt,
                Instant.now());
        CampaignTargetEntity saved = targetRepository.save(target);
        auditService.record("CAMPAIGN_TARGET_CREATED", "CAMPAIGN_TARGET", saved.getId(), Map.of("campaignId", campaignId.toString()));
        return new CampaignTargetCreatedResponse(toResponse(saved), rawToken);
    }

    private CampaignTargetResponse toResponse(CampaignTargetEntity entity) {
        return new CampaignTargetResponse(entity.getId(), entity.getCampaignId(), entity.getUserId(), entity.getExpiresAt(), entity.getLastEventAt());
    }
}
