package fr.dssi.phishingawareness.shared.health;

import java.time.Instant;

public record HealthStatusDto(
        String status,
        String application,
        Instant checkedAt
) {
}
