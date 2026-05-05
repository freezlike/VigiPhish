package fr.dssi.phishingawareness.audit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.dssi.phishingawareness.audit.entity.AuditLogEntity;
import fr.dssi.phishingawareness.audit.repository.AuditLogRepository;
import fr.dssi.phishingawareness.audit.service.AuditService;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuditServiceTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @Test
    void recordsAdministrativeAuditLog() {
        AuditService service = new AuditService(auditLogRepository);
        when(auditLogRepository.save(org.mockito.ArgumentMatchers.any(AuditLogEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        UUID targetId = UUID.randomUUID();

        service.record("CAMPAIGN_CREATED", "CAMPAIGN", targetId, Map.of("name", "Campaign"));

        ArgumentCaptor<AuditLogEntity> auditCaptor = ArgumentCaptor.forClass(AuditLogEntity.class);
        verify(auditLogRepository).save(auditCaptor.capture());
        assertThat(auditCaptor.getValue().getId()).isNotNull();
        assertThat(auditCaptor.getValue().getAction()).isEqualTo("CAMPAIGN_CREATED");
        assertThat(auditCaptor.getValue().getTargetType()).isEqualTo("CAMPAIGN");
        assertThat(auditCaptor.getValue().getTargetId()).isEqualTo(targetId);
        assertThat(auditCaptor.getValue().getDetails()).containsEntry("name", "Campaign");
    }
}
