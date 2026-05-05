package fr.dssi.phishingawareness.users.dto;

import fr.dssi.phishingawareness.users.entity.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public final class UserDtos {

    private UserDtos() {
    }

    public record UserRequest(
            @NotBlank @Email String email,
            @NotBlank String displayName,
            @NotNull UserRole role,
            boolean active
    ) {
    }

    public record UserResponse(
            UUID id,
            String email,
            String displayName,
            UserRole role,
            boolean active
    ) {
    }
}
