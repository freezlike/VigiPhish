package fr.dssi.phishingawareness.campaigns.dto;

import fr.dssi.phishingawareness.campaigns.entity.CampaignStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

public final class CampaignDtos {

    private CampaignDtos() {
    }

    public record CampaignRequest(
            @NotBlank String name,
            UUID ownerId,
            UUID landingPageId,
            @NotBlank String internalDomainAllowlist,
            boolean validationRequired
    ) {
    }

    public record ScheduleCampaignRequest(
            @NotNull @Future Instant scheduledAt
    ) {
    }

    public record CampaignResponse(
            UUID id,
            String name,
            CampaignStatus status,
            UUID ownerId,
            UUID validatorId,
            UUID landingPageId,
            String internalDomainAllowlist,
            boolean validationRequired,
            Instant validatedAt,
            Instant scheduledAt
    ) {
    }
}
