package fr.dssi.phishingawareness.audit.service;

import fr.dssi.phishingawareness.audit.dto.AuditLogDtos.AuditLogResponse;
import fr.dssi.phishingawareness.audit.entity.AuditLogEntity;
import fr.dssi.phishingawareness.audit.repository.AuditLogRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final Clock clock;

    @Autowired
    public AuditService(AuditLogRepository auditLogRepository) {
        this(auditLogRepository, Clock.systemUTC());
    }

    AuditService(AuditLogRepository auditLogRepository, Clock clock) {
        this.auditLogRepository = auditLogRepository;
        this.clock = clock;
    }

    @Transactional
    public AuditLogEntity record(String action, String targetType, UUID targetId, Map<String, Object> details) {
        AuditLogEntity auditLog = new AuditLogEntity(
                UUID.randomUUID(),
                currentActorId(),
                action,
                targetType,
                targetId,
                Instant.now(clock),
                details == null ? Map.of() : details);
        return auditLogRepository.save(auditLog);
    }

    @Transactional(readOnly = true)
    public List<AuditLogResponse> list() {
        return auditLogRepository.findAll().stream().map(this::toResponse).toList();
    }

    private UUID currentActorId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            return null;
        }
        try {
            return UUID.fromString(authentication.getName());
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    private AuditLogResponse toResponse(AuditLogEntity entity) {
        return new AuditLogResponse(
                entity.getId(),
                entity.getActorId(),
                entity.getAction(),
                entity.getTargetType(),
                entity.getTargetId(),
                entity.getOccurredAt(),
                entity.getDetails());
    }
}
