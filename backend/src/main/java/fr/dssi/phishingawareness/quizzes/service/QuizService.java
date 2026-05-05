package fr.dssi.phishingawareness.quizzes.service;

import fr.dssi.phishingawareness.audit.service.AuditService;
import fr.dssi.phishingawareness.quizzes.dto.QuizDtos.QuizRequest;
import fr.dssi.phishingawareness.quizzes.dto.QuizDtos.QuizResponse;
import fr.dssi.phishingawareness.quizzes.entity.QuizEntity;
import fr.dssi.phishingawareness.quizzes.repository.QuizRepository;
import fr.dssi.phishingawareness.shared.exception.NotFoundException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QuizService {

    private final QuizRepository repository;
    private final AuditService auditService;

    public QuizService(QuizRepository repository, AuditService auditService) {
        this.repository = repository;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public List<QuizResponse> list() { return repository.findAll().stream().map(this::toResponse).toList(); }

    @Transactional
    public QuizResponse create(QuizRequest request) {
        Instant now = Instant.now();
        QuizEntity entity = repository.save(new QuizEntity(UUID.randomUUID(), request.name(), request.description(), now, now));
        auditService.record("QUIZ_CREATED", "QUIZ", entity.getId(), Map.of("name", entity.getName()));
        return toResponse(entity);
    }

    @Transactional
    public QuizResponse update(UUID id, QuizRequest request) {
        QuizEntity entity = require(id);
        entity.setName(request.name());
        entity.setDescription(request.description());
        entity.setUpdatedAt(Instant.now());
        auditService.record("QUIZ_UPDATED", "QUIZ", id, Map.of("name", entity.getName()));
        return toResponse(repository.save(entity));
    }

    @Transactional
    public void delete(UUID id) {
        repository.delete(require(id));
        auditService.record("QUIZ_DELETED", "QUIZ", id, Map.of());
    }

    private QuizEntity require(UUID id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Quiz", id));
    }

    private QuizResponse toResponse(QuizEntity entity) {
        return new QuizResponse(entity.getId(), entity.getName(), entity.getDescription());
    }
}
