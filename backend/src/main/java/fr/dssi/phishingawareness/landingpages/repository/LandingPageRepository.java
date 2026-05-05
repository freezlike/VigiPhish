package fr.dssi.phishingawareness.landingpages.repository;

import fr.dssi.phishingawareness.landingpages.entity.LandingPageEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LandingPageRepository extends JpaRepository<LandingPageEntity, UUID> {
}
