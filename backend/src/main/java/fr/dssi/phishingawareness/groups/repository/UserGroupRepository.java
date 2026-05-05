package fr.dssi.phishingawareness.groups.repository;

import fr.dssi.phishingawareness.groups.entity.UserGroupEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserGroupRepository extends JpaRepository<UserGroupEntity, UUID> {
}
