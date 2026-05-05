package fr.dssi.phishingawareness.templates.controller;

import fr.dssi.phishingawareness.templates.dto.EmailTemplateDtos.EmailTemplateRequest;
import fr.dssi.phishingawareness.templates.dto.EmailTemplateDtos.EmailTemplateResponse;
import fr.dssi.phishingawareness.templates.service.EmailTemplateService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/email-templates")
@PreAuthorize("hasAnyRole('DSSI_ADMIN','CAMPAIGN_MANAGER')")
public class EmailTemplateController {

    private final EmailTemplateService service;

    public EmailTemplateController(EmailTemplateService service) {
        this.service = service;
    }

    @GetMapping
    public List<EmailTemplateResponse> list() { return service.list(); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmailTemplateResponse create(@Valid @RequestBody EmailTemplateRequest request) { return service.create(request); }

    @PutMapping("/{id}")
    public EmailTemplateResponse update(@PathVariable UUID id, @Valid @RequestBody EmailTemplateRequest request) { return service.update(id, request); }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) { service.delete(id); }
}
