package fr.dssi.phishingawareness.audit.repository;

import fr.dssi.phishingawareness.audit.entity.AuditLogEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLogEntity, UUID> {
}
