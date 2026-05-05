package fr.dssi.phishingawareness.quizzes.repository;

import fr.dssi.phishingawareness.quizzes.entity.QuizEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizRepository extends JpaRepository<QuizEntity, UUID> {
}
