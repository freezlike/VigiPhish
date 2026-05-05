package fr.dssi.phishingawareness.tracking;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.dssi.phishingawareness.shared.exception.BadRequestException;
import fr.dssi.phishingawareness.tracking.dto.TrackingDtos.PublicTrackingEventRequest;
import fr.dssi.phishingawareness.tracking.entity.CampaignTargetEntity;
import fr.dssi.phishingawareness.tracking.entity.TrackingEventEntity;
import fr.dssi.phishingawareness.tracking.entity.TrackingEventType;
import fr.dssi.phishingawareness.tracking.repository.CampaignTargetRepository;
import fr.dssi.phishingawareness.tracking.repository.TrackingEventRepository;
import fr.dssi.phishingawareness.tracking.service.TokenService;
import fr.dssi.phishingawareness.tracking.service.TrackingEventService;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TrackingEventServiceTest {

    @Mock
    private TrackingEventRepository eventRepository;

    @Mock
    private CampaignTargetRepository targetRepository;

    @Mock
    private TokenService tokenService;

    private TrackingEventService service;

    @BeforeEach
    void setUp() {
        service = new TrackingEventService(eventRepository, targetRepository, tokenService);
    }

    @Test
    void recordsAllowedPublicEventWithoutFormContent() {
        CampaignTargetEntity target = new CampaignTargetEntity(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "hash", Instant.now().plusSeconds(3600), Instant.now());
        when(tokenService.hashToken("raw-token")).thenReturn("hash");
        when(targetRepository.findByTokenHash("hash")).thenReturn(Optional.of(target));

        var response = service.recordPublicEvent(new PublicTrackingEventRequest("raw-token", TrackingEventType.SUBMITTED_FORM));

        assertThat(response.status()).isEqualTo("RECORDED");
        ArgumentCaptor<TrackingEventEntity> eventCaptor = ArgumentCaptor.forClass(TrackingEventEntity.class);
        verify(eventRepository).save(eventCaptor.capture());
        assertThat(eventCaptor.getValue().getEventType()).isEqualTo(TrackingEventType.SUBMITTED_FORM);
        assertThat(eventCaptor.getValue().getMetadata()).containsEntry("formContentStored", false);
        assertThat(eventCaptor.getValue().getMetadata()).doesNotContainKeys("password", "formFields");
    }

    @Test
    void rejectsDisallowedPublicEventType() {
        assertThatThrownBy(() -> service.recordPublicEvent(new PublicTrackingEventRequest("raw-token", TrackingEventType.EMAIL_SENT)))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("not allowed");

        verify(eventRepository, never()).save(any());
    }

    @Test
    void rejectsExpiredToken() {
        CampaignTargetEntity target = new CampaignTargetEntity(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "hash", Instant.now().minusSeconds(1), Instant.now());
        when(tokenService.hashToken("raw-token")).thenReturn("hash");
        when(targetRepository.findByTokenHash("hash")).thenReturn(Optional.of(target));

        assertThatThrownBy(() -> service.recordPublicEvent(new PublicTrackingEventRequest("raw-token", TrackingEventType.LINK_CLICKED)))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Invalid or expired");

        verify(eventRepository, never()).save(any());
    }
}
