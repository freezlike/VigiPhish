package fr.dssi.phishingawareness.quizzes.controller;

import fr.dssi.phishingawareness.quizzes.dto.QuizDtos.QuizRequest;
import fr.dssi.phishingawareness.quizzes.dto.QuizDtos.QuizResponse;
import fr.dssi.phishingawareness.quizzes.service.QuizService;
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
@RequestMapping("/api/admin/quizzes")
@PreAuthorize("hasAnyRole('DSSI_ADMIN','CAMPAIGN_MANAGER')")
public class QuizController {

    private final QuizService service;

    public QuizController(QuizService service) {
        this.service = service;
    }

    @GetMapping
    public List<QuizResponse> list() { return service.list(); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public QuizResponse create(@Valid @RequestBody QuizRequest request) { return service.create(request); }

    @PutMapping("/{id}")
    public QuizResponse update(@PathVariable UUID id, @Valid @RequestBody QuizRequest request) { return service.update(id, request); }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) { service.delete(id); }
}
