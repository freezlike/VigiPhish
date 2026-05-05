package fr.dssi.phishingawareness.users.service;

import fr.dssi.phishingawareness.audit.service.AuditService;
import fr.dssi.phishingawareness.shared.exception.NotFoundException;
import fr.dssi.phishingawareness.users.dto.UserDtos.UserRequest;
import fr.dssi.phishingawareness.users.dto.UserDtos.UserResponse;
import fr.dssi.phishingawareness.users.entity.UserEntity;
import fr.dssi.phishingawareness.users.entity.UserRole;
import fr.dssi.phishingawareness.users.repository.UserRepository;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AuditService auditService;

    public UserService(UserRepository userRepository, AuditService auditService) {
        this.userRepository = userRepository;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> list() {
        return userRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public UserResponse get(UUID id) {
        return toResponse(requireUser(id));
    }

    @Transactional
    public UserResponse create(UserRequest request) {
        Instant now = Instant.now();
        UserEntity user = new UserEntity(UUID.randomUUID(), request.email(), request.displayName(), request.role(), request.active(), now, now);
        UserEntity saved = userRepository.save(user);
        auditService.record("USER_CREATED", "USER", saved.getId(), Map.of("email", saved.getEmail()));
        return toResponse(saved);
    }

    @Transactional
    public UserResponse update(UUID id, UserRequest request) {
        UserEntity user = requireUser(id);
        user.setEmail(request.email());
        user.setDisplayName(request.displayName());
        user.setRole(request.role());
        user.setActive(request.active());
        user.setUpdatedAt(Instant.now());
        UserEntity saved = userRepository.save(user);
        auditService.record("USER_UPDATED", "USER", saved.getId(), Map.of("email", saved.getEmail()));
        return toResponse(saved);
    }

    @Transactional
    public void delete(UUID id) {
        UserEntity user = requireUser(id);
        userRepository.delete(user);
        auditService.record("USER_DELETED", "USER", id, Map.of("email", user.getEmail()));
    }

    @Transactional
    public UserEntity findOrCreateLearner(String email) {
        String normalizedEmail = email.trim().toLowerCase();
        return userRepository.findByEmail(normalizedEmail).orElseGet(() -> {
            Instant now = Instant.now();
            UserEntity user = new UserEntity(
                    UUID.randomUUID(),
                    normalizedEmail,
                    displayNameFromEmail(normalizedEmail),
                    UserRole.ROLE_USER,
                    true,
                    now,
                    now);
            UserEntity saved = userRepository.save(user);
            auditService.record("USER_CREATED_FROM_CAMPAIGN_IMPORT", "USER", saved.getId(), Map.of("email", saved.getEmail()));
            return saved;
        });
    }

    public UserEntity requireUser(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User", id));
    }

    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email.trim().toLowerCase()).isPresent();
    }

    private String displayNameFromEmail(String email) {
        int atIndex = email.indexOf('@');
        return atIndex > 0 ? email.substring(0, atIndex) : email;
    }

    private UserResponse toResponse(UserEntity entity) {
        return new UserResponse(entity.getId(), entity.getEmail(), entity.getDisplayName(), entity.getRole(), entity.isActive());
    }
}
