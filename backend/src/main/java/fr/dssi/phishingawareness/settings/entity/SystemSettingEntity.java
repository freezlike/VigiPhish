package fr.dssi.phishingawareness.settings.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "system_settings")
public class SystemSettingEntity {

    @Id
    private UUID id;

    @Column(name = "setting_key", nullable = false, unique = true, length = 120)
    private String key;

    @Column(name = "setting_value", nullable = false, columnDefinition = "text")
    private String value;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected SystemSettingEntity() {
    }

    public SystemSettingEntity(UUID id, String key, String value, String description, Instant updatedAt) {
        this.id = id;
        this.key = key;
        this.value = value;
        this.description = description;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public String getKey() { return key; }
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
