package fr.dssi.phishingawareness.campaigns.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "campaigns")
public class CampaignEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 200)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 64)
    private CampaignStatus status;

    @Column(name = "owner_id")
    private UUID ownerId;

    @Column(name = "validator_id")
    private UUID validatorId;

    @Column(name = "landing_page_id")
    private UUID landingPageId;

    @Column(name = "internal_domain_allowlist", nullable = false, columnDefinition = "text")
    private String internalDomainAllowlist;

    @Column(name = "validation_required", nullable = false)
    private boolean validationRequired;

    @Column(name = "validated_at")
    private Instant validatedAt;

    @Column(name = "scheduled_at")
    private Instant scheduledAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected CampaignEntity() {
    }

    public CampaignEntity(UUID id, String name, CampaignStatus status, UUID ownerId, UUID landingPageId, String internalDomainAllowlist, boolean validationRequired, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.ownerId = ownerId;
        this.landingPageId = landingPageId;
        this.internalDomainAllowlist = internalDomainAllowlist;
        this.validationRequired = validationRequired;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CampaignStatus getStatus() {
        return status;
    }

    public void setStatus(CampaignStatus status) {
        this.status = status;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }

    public UUID getValidatorId() {
        return validatorId;
    }

    public void setValidatorId(UUID validatorId) {
        this.validatorId = validatorId;
    }

    public UUID getLandingPageId() {
        return landingPageId;
    }

    public void setLandingPageId(UUID landingPageId) {
        this.landingPageId = landingPageId;
    }

    public String getInternalDomainAllowlist() {
        return internalDomainAllowlist;
    }

    public void setInternalDomainAllowlist(String internalDomainAllowlist) {
        this.internalDomainAllowlist = internalDomainAllowlist;
    }

    public boolean isValidationRequired() {
        return validationRequired;
    }

    public void setValidationRequired(boolean validationRequired) {
        this.validationRequired = validationRequired;
    }

    public Instant getValidatedAt() {
        return validatedAt;
    }

    public void setValidatedAt(Instant validatedAt) {
        this.validatedAt = validatedAt;
    }

    public Instant getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(Instant scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
