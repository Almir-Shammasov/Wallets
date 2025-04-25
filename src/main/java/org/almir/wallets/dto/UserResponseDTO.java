package org.almir.wallets.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.almir.wallets.enums.Role;

@Schema(description = "DTO representing user details in the system")
public record UserResponseDTO(
        @Schema(description = "Unique identifier of the user", example = "1")
        Long id,
        @Schema(description = "User email address", example = "user@example.com")
        String email,
        @Schema(description = "User full name", example = "John Doe")
        String name,
        @Schema(description = "User role in the system", example = "USER")
        Role role
) {
}
