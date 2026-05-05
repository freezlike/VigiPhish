package fr.dssi.phishingawareness.quizzes.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public final class QuizDtos {

    private QuizDtos() {
    }

    public record QuizRequest(
            @NotBlank String name,
            @NotBlank String description
    ) {
    }

    public record QuizResponse(
            UUID id,
            String name,
            String description
    ) {
    }
}
