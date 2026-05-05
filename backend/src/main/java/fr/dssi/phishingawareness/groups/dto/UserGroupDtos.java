package fr.dssi.phishingawareness.groups.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public final class UserGroupDtos {

    private UserGroupDtos() {
    }

    public record UserGroupRequest(
            @NotBlank String name,
            String description
    ) {
    }

    public record UserGroupResponse(
            UUID id,
            String name,
            String description
    ) {
    }
}
