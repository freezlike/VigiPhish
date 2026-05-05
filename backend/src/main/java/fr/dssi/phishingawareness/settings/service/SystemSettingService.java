package fr.dssi.phishingawareness.settings.service;

import fr.dssi.phishingawareness.audit.service.AuditService;
import fr.dssi.phishingawareness.settings.dto.SystemSettingDtos.SystemSettingRequest;
import fr.dssi.phishingawareness.settings.dto.SystemSettingDtos.SystemSettingResponse;
import fr.dssi.phishingawareness.settings.entity.SystemSettingEntity;
import fr.dssi.phishingawareness.settings.repository.SystemSettingRepository;
import fr.dssi.phishingawareness.shared.exception.NotFoundException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SystemSettingService {

    private final SystemSettingRepository repository;
    private final AuditService auditService;

    public SystemSettingService(SystemSettingRepository repository, AuditService auditService) {
        this.repository = repository;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public List<SystemSettingResponse> list() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional
    public SystemSettingResponse upsert(SystemSettingRequest request) {
        SystemSettingEntity setting = repository.findByKey(request.key())
                .orElseGet(() -> new SystemSettingEntity(UUID.randomUUID(), request.key(), request.value(), request.description(), Instant.now()));
        setting.setValue(request.value());
        setting.setDescription(request.description());
        setting.setUpdatedAt(Instant.now());
        SystemSettingEntity saved = repository.save(setting);
        auditService.record("SYSTEM_SETTING_UPSERTED", "SYSTEM_SETTING", saved.getId(), Map.of("key", saved.getKey()));
        return toResponse(saved);
    }

    @Transactional
    public void delete(String key) {
        SystemSettingEntity setting = repository.findByKey(key).orElseThrow(() -> new NotFoundException("SystemSetting", key));
        repository.delete(setting);
        auditService.record("SYSTEM_SETTING_DELETED", "SYSTEM_SETTING", setting.getId(), Map.of("key", key));
    }

    private SystemSettingResponse toResponse(SystemSettingEntity entity) {
        return new SystemSettingResponse(entity.getId(), entity.getKey(), entity.getValue(), entity.getDescription(), entity.getUpdatedAt());
    }
}
