package fr.dssi.phishingawareness.templates.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public final class EmailTemplateDtos {

    private EmailTemplateDtos() {
    }

    public record EmailTemplateRequest(
            @NotBlank String name,
            @NotBlank String subject,
            @NotBlank String educationalContext,
            @NotBlank String body
    ) {
    }

    public record EmailTemplateResponse(
            UUID id,
            String name,
            String subject,
            String educationalContext,
            String body
    ) {
    }
}
