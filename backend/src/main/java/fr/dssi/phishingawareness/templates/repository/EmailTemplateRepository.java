package fr.dssi.phishingawareness.templates.repository;

import fr.dssi.phishingawareness.templates.entity.EmailTemplateEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailTemplateRepository extends JpaRepository<EmailTemplateEntity, UUID> {
}
