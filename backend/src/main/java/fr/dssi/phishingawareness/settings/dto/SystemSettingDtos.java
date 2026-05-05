package fr.dssi.phishingawareness.settings.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.time.Instant;
import java.util.UUID;

public final class SystemSettingDtos {

    private SystemSettingDtos() {
    }

    public record SystemSettingRequest(
            @NotBlank @Pattern(regexp = "^[A-Z0-9_.-]{3,120}$") String key,
            @NotBlank String value,
            String description
    ) {
    }

    public record SystemSettingResponse(
            UUID id,
            String key,
            String value,
            String description,
            Instant updatedAt
    ) {
    }
}
