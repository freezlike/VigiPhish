package fr.dssi.phishingawareness.settings.repository;

import fr.dssi.phishingawareness.settings.entity.SystemSettingEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemSettingRepository extends JpaRepository<SystemSettingEntity, UUID> {
    Optional<SystemSettingEntity> findByKey(String key);
}
