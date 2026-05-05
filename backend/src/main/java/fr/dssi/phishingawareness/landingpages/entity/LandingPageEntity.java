package fr.dssi.phishingawareness.landingpages.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "landing_pages")
public class LandingPageEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(name = "educational_message", nullable = false, columnDefinition = "text")
    private String educationalMessage;

    @Column(nullable = false, columnDefinition = "text")
    private String content;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected LandingPageEntity() {
    }

    public LandingPageEntity(UUID id, String name, String educationalMessage, String content, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.educationalMessage = educationalMessage;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEducationalMessage() { return educationalMessage; }
    public void setEducationalMessage(String educationalMessage) { this.educationalMessage = educationalMessage; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
