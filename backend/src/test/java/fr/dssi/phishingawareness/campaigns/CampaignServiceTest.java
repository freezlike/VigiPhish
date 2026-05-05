package fr.dssi.phishingawareness.campaigns;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.dssi.phishingawareness.audit.service.AuditService;
import fr.dssi.phishingawareness.campaigns.dto.CampaignDtos.CampaignRequest;
import fr.dssi.phishingawareness.campaigns.dto.CampaignDtos.ScheduleCampaignRequest;
import fr.dssi.phishingawareness.campaigns.entity.CampaignEntity;
import fr.dssi.phishingawareness.campaigns.entity.CampaignStatus;
import fr.dssi.phishingawareness.campaigns.repository.CampaignRepository;
import fr.dssi.phishingawareness.campaigns.service.CampaignService;
import fr.dssi.phishingawareness.shared.exception.BadRequestException;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CampaignServiceTest {

    @Mock
    private CampaignRepository campaignRepository;

    @Mock
    private AuditService auditService;

    private CampaignService campaignService;

    @BeforeEach
    void setUp() {
        campaignService = new CampaignService(campaignRepository, auditService);
    }

    @Test
    void appliesExpectedCampaignLifecycle() {
        when(campaignRepository.save(any(CampaignEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        UUID campaignId = UUID.randomUUID();
        CampaignEntity campaign = campaign(campaignId, CampaignStatus.DRAFT);
        when(campaignRepository.findById(campaignId)).thenReturn(Optional.of(campaign));

        assertThat(campaignService.submitForValidation(campaignId).status()).isEqualTo(CampaignStatus.PENDING_VALIDATION);
        assertThat(campaignService.validate(campaignId).status()).isEqualTo(CampaignStatus.VALIDATED);
        assertThat(campaignService.schedule(campaignId, new ScheduleCampaignRequest(Instant.now().plusSeconds(3600))).status()).isEqualTo(CampaignStatus.SCHEDULED);
        assertThat(campaignService.start(campaignId).status()).isEqualTo(CampaignStatus.RUNNING);
        assertThat(campaignService.complete(campaignId).status()).isEqualTo(CampaignStatus.COMPLETED);

        verify(auditService).record("CAMPAIGN_COMPLETED", "CAMPAIGN", campaignId, java.util.Map.of());
    }

    @Test
    void rejectsInvalidLifecycleTransition() {
        UUID campaignId = UUID.randomUUID();
        when(campaignRepository.findById(campaignId)).thenReturn(Optional.of(campaign(campaignId, CampaignStatus.DRAFT)));

        assertThatThrownBy(() -> campaignService.validate(campaignId))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Expected PENDING_VALIDATION");
    }

    @Test
    void createsCampaignAsDraft() {
        when(campaignRepository.save(any(CampaignEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        CampaignRequest request = new CampaignRequest("Awareness Q2", UUID.randomUUID(), "example.internal", true);

        assertThat(campaignService.create(request).status()).isEqualTo(CampaignStatus.DRAFT);
    }

    private CampaignEntity campaign(UUID id, CampaignStatus status) {
        Instant now = Instant.now();
        return new CampaignEntity(id, "Campaign", status, UUID.randomUUID(), "example.internal", true, now, now);
    }
}
