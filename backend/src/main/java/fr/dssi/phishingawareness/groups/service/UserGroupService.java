package fr.dssi.phishingawareness.groups.service;

import fr.dssi.phishingawareness.audit.service.AuditService;
import fr.dssi.phishingawareness.groups.dto.UserGroupDtos.UserGroupRequest;
import fr.dssi.phishingawareness.groups.dto.UserGroupDtos.UserGroupResponse;
import fr.dssi.phishingawareness.groups.entity.UserGroupEntity;
import fr.dssi.phishingawareness.groups.repository.UserGroupRepository;
import fr.dssi.phishingawareness.shared.exception.NotFoundException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserGroupService {

    private final UserGroupRepository repository;
    private final AuditService auditService;

    public UserGroupService(UserGroupRepository repository, AuditService auditService) {
        this.repository = repository;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public List<UserGroupResponse> list() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional
    public UserGroupResponse create(UserGroupRequest request) {
        UserGroupEntity group = repository.save(new UserGroupEntity(UUID.randomUUID(), request.name(), request.description(), Instant.now()));
        auditService.record("USER_GROUP_CREATED", "USER_GROUP", group.getId(), Map.of("name", group.getName()));
        return toResponse(group);
    }

    @Transactional
    public UserGroupResponse update(UUID id, UserGroupRequest request) {
        UserGroupEntity group = require(id);
        group.setName(request.name());
        group.setDescription(request.description());
        auditService.record("USER_GROUP_UPDATED", "USER_GROUP", id, Map.of("name", group.getName()));
        return toResponse(repository.save(group));
    }

    @Transactional
    public void delete(UUID id) {
        repository.delete(require(id));
        auditService.record("USER_GROUP_DELETED", "USER_GROUP", id, Map.of());
    }

    private UserGroupEntity require(UUID id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("UserGroup", id));
    }

    private UserGroupResponse toResponse(UserGroupEntity entity) {
        return new UserGroupResponse(entity.getId(), entity.getName(), entity.getDescription());
    }
}
