package fr.dssi.phishingawareness.landingpages.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public final class LandingPageDtos {

    private LandingPageDtos() {
    }

    public record LandingPageRequest(
            @NotBlank String name,
            @NotBlank String educationalMessage,
            @NotBlank String content
    ) {
    }

    public record LandingPageResponse(
            UUID id,
            String name,
            String educationalMessage,
            String content
    ) {
    }

    public record PublicAwarenessPageResponse(
            String title,
            String educationalMessage,
            String content
    ) {
    }
}
