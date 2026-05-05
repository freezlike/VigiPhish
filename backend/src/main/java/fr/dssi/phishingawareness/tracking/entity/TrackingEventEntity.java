package fr.dssi.phishingawareness.tracking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "campaign_events")
public class TrackingEventEntity {

    @Id
    private UUID id;

    @Column(name = "campaign_id", nullable = false)
    private UUID campaignId;

    @Column(name = "user_id")
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 64)
    private TrackingEventType eventType;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> metadata;

    protected TrackingEventEntity() {
    }

    public TrackingEventEntity(UUID id, UUID campaignId, UUID userId, TrackingEventType eventType, Instant occurredAt, Map<String, Object> metadata) {
        this.id = id;
        this.campaignId = campaignId;
        this.userId = userId;
        this.eventType = eventType;
        this.occurredAt = occurredAt;
        this.metadata = metadata;
    }

    public UUID getId() { return id; }
    public UUID getCampaignId() { return campaignId; }
    public UUID getUserId() { return userId; }
    public TrackingEventType getEventType() { return eventType; }
    public Instant getOccurredAt() { return occurredAt; }
    public Map<String, Object> getMetadata() { return metadata; }
}
