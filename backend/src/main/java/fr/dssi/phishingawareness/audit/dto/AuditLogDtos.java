package fr.dssi.phishingawareness.audit.dto;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public final class AuditLogDtos {

    private AuditLogDtos() {
    }

    public record AuditLogResponse(
            UUID id,
            UUID actorId,
            String action,
            String targetType,
            UUID targetId,
            Instant occurredAt,
            Map<String, Object> details
    ) {
    }
}
