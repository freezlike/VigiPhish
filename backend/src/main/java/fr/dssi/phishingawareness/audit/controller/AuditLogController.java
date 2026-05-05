package fr.dssi.phishingawareness.audit.controller;

import fr.dssi.phishingawareness.audit.dto.AuditLogDtos.AuditLogResponse;
import fr.dssi.phishingawareness.audit.service.AuditService;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/audit-logs")
public class AuditLogController {

    private final AuditService auditService;

    public AuditLogController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('DSSI_ADMIN','AUDITOR')")
    public List<AuditLogResponse> list() {
        return auditService.list();
    }
}
