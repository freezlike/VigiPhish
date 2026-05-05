package fr.dssi.phishingawareness.settings.controller;

import fr.dssi.phishingawareness.settings.dto.SystemSettingDtos.SystemSettingRequest;
import fr.dssi.phishingawareness.settings.dto.SystemSettingDtos.SystemSettingResponse;
import fr.dssi.phishingawareness.settings.service.SystemSettingService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/system-settings")
@PreAuthorize("hasRole('DSSI_ADMIN')")
public class SystemSettingController {

    private final SystemSettingService service;

    public SystemSettingController(SystemSettingService service) {
        this.service = service;
    }

    @GetMapping
    public List<SystemSettingResponse> list() { return service.list(); }

    @PostMapping
    public SystemSettingResponse upsert(@Valid @RequestBody SystemSettingRequest request) { return service.upsert(request); }

    @DeleteMapping("/{key}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String key) { service.delete(key); }
}
