package fr.dssi.phishingawareness.tracking.dto;

import fr.dssi.phishingawareness.tracking.entity.TrackingEventType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public final class TrackingDtos {

    private TrackingDtos() {
    }

    public record CampaignTargetRequest(
            @NotNull UUID userId,
            @Future Instant expiresAt
    ) {
    }

    public record CampaignTargetResponse(
            UUID id,
            UUID campaignId,
            UUID userId,
            Instant expiresAt,
            Instant lastEventAt
    ) {
    }

    public record CampaignTargetCreatedResponse(
            CampaignTargetResponse target,
            String rawToken
    ) {
    }

    public record CampaignTargetEmailImportRequest(
            @NotEmpty List<@NotBlank @Email String> emails,
            @Future Instant expiresAt
    ) {
    }

    public record CampaignTargetEmailImportResponse(
            int requested,
            int createdUsers,
            int createdTargets,
            int skippedExistingTargets,
            List<CampaignTargetCreatedResponse> targets
    ) {
    }

    public record PublicTrackingEventRequest(
            @NotBlank String token,
            @NotNull TrackingEventType eventType
    ) {
    }

    public record PublicTrackingEventResponse(
            String status
    ) {
    }
}
