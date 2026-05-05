package fr.dssi.phishingawareness.templates.service;

import fr.dssi.phishingawareness.audit.service.AuditService;
import fr.dssi.phishingawareness.shared.exception.BadRequestException;
import fr.dssi.phishingawareness.shared.exception.NotFoundException;
import fr.dssi.phishingawareness.templates.dto.EmailTemplateDtos.EmailTemplateRequest;
import fr.dssi.phishingawareness.templates.dto.EmailTemplateDtos.EmailTemplateResponse;
import fr.dssi.phishingawareness.templates.entity.EmailTemplateEntity;
import fr.dssi.phishingawareness.templates.repository.EmailTemplateRepository;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmailTemplateService {

    private final EmailTemplateRepository repository;
    private final AuditService auditService;

    public EmailTemplateService(EmailTemplateRepository repository, AuditService auditService) {
        this.repository = repository;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public List<EmailTemplateResponse> list() { return repository.findAll().stream().map(this::toResponse).toList(); }

    @Transactional
    public EmailTemplateResponse create(EmailTemplateRequest request) {
        Instant now = Instant.now();
        EmailTemplateEntity entity = repository.save(new EmailTemplateEntity(UUID.randomUUID(), request.name(), request.subject(), request.educationalContext(), request.body(), now, now));
        auditService.record("EMAIL_TEMPLATE_CREATED", "EMAIL_TEMPLATE", entity.getId(), Map.of("name", entity.getName()));
        return toResponse(entity);
    }

    @Transactional
    public EmailTemplateResponse update(UUID id, EmailTemplateRequest request) {
        EmailTemplateEntity entity = require(id);
        entity.setName(request.name());
        entity.setSubject(request.subject());
        entity.setEducationalContext(request.educationalContext());
        entity.setBody(request.body());
        entity.setUpdatedAt(Instant.now());
        auditService.record("EMAIL_TEMPLATE_UPDATED", "EMAIL_TEMPLATE", id, Map.of("name", entity.getName()));
        return toResponse(repository.save(entity));
    }

    @Transactional
    public void delete(UUID id) {
        EmailTemplateEntity entity = require(id);
        try {
            repository.delete(entity);
            repository.flush();
            auditService.record("EMAIL_TEMPLATE_DELETED", "EMAIL_TEMPLATE", id, Map.of("name", entity.getName()));
        } catch (DataIntegrityViolationException exception) {
            throw new BadRequestException("EMAIL_TEMPLATE_IN_USE", "Email template cannot be deleted while it is used by another resource");
        }
    }

    private EmailTemplateEntity require(UUID id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("EmailTemplate", id));
    }

    private EmailTemplateResponse toResponse(EmailTemplateEntity entity) {
        return new EmailTemplateResponse(entity.getId(), entity.getName(), entity.getSubject(), entity.getEducationalContext(), entity.getBody());
    }
}
