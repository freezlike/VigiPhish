package fr.dssi.phishingawareness.tracking;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.dssi.phishingawareness.audit.service.AuditService;
import fr.dssi.phishingawareness.campaigns.entity.CampaignEntity;
import fr.dssi.phishingawareness.campaigns.entity.CampaignStatus;
import fr.dssi.phishingawareness.campaigns.service.CampaignService;
import fr.dssi.phishingawareness.tracking.dto.TrackingDtos.CampaignTargetRequest;
import fr.dssi.phishingawareness.tracking.entity.CampaignTargetEntity;
import fr.dssi.phishingawareness.tracking.repository.CampaignTargetRepository;
import fr.dssi.phishingawareness.tracking.service.CampaignTargetService;
import fr.dssi.phishingawareness.tracking.service.TokenService;
import fr.dssi.phishingawareness.users.entity.UserEntity;
import fr.dssi.phishingawareness.users.entity.UserRole;
import fr.dssi.phishingawareness.users.service.UserService;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CampaignTargetServiceTest {

    @Mock
    private CampaignTargetRepository targetRepository;

    @Mock
    private CampaignService campaignService;

    @Mock
    private UserService userService;

    @Mock
    private TokenService tokenService;

    @Mock
    private AuditService auditService;

    private CampaignTargetService service;

    @BeforeEach
    void setUp() {
        service = new CampaignTargetService(targetRepository, campaignService, userService, tokenService, auditService);
        when(targetRepository.save(any(CampaignTargetEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void returnsRawTokenOnceAndStoresOnlyHash() {
        UUID campaignId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Instant expiresAt = Instant.now().plusSeconds(3600);
        when(campaignService.requireCampaign(campaignId)).thenReturn(new CampaignEntity(campaignId, "Campaign", CampaignStatus.DRAFT, userId, null, "example.internal", true, Instant.now(), Instant.now()));
        when(userService.requireUser(userId)).thenReturn(new UserEntity(userId, "user@example.internal", "User", UserRole.ROLE_USER, true, Instant.now(), Instant.now()));
        when(tokenService.generateRawToken()).thenReturn("raw-token");
        when(tokenService.hashToken("raw-token")).thenReturn("a".repeat(64));

        var response = service.create(campaignId, new CampaignTargetRequest(userId, expiresAt));

        assertThat(response.rawToken()).isEqualTo("raw-token");
        ArgumentCaptor<CampaignTargetEntity> targetCaptor = ArgumentCaptor.forClass(CampaignTargetEntity.class);
        verify(targetRepository).save(targetCaptor.capture());
        assertThat(targetCaptor.getValue().getTokenHash()).isEqualTo("a".repeat(64));
        assertThat(targetCaptor.getValue().getTokenHash()).doesNotContain("raw-token");
        assertThat(targetCaptor.getValue().getExpiresAt()).isEqualTo(expiresAt);
    }
}
