package fr.dssi.phishingawareness.templates.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "email_templates")
public class EmailTemplateEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, length = 255)
    private String subject;

    @Column(name = "educational_context", nullable = false, columnDefinition = "text")
    private String educationalContext;

    @Column(nullable = false, columnDefinition = "text")
    private String body;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected EmailTemplateEntity() {
    }

    public EmailTemplateEntity(UUID id, String name, String subject, String educationalContext, String body, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.subject = subject;
        this.educationalContext = educationalContext;
        this.body = body;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getEducationalContext() { return educationalContext; }
    public void setEducationalContext(String educationalContext) { this.educationalContext = educationalContext; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
