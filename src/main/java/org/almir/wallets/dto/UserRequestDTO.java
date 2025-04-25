package org.almir.wallets.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.almir.wallets.enums.Role;

@Schema(description = "DTO representing user registration details")
public record UserRequestDTO(
        @Schema(description = "User email address", example = "user@example.com")
        @NotBlank String email,
        @Schema(description = "User password", example = "securePassword123")
        @NotBlank String password,
        @Schema(description = "User full name", example = "John Doe")
        @NotBlank String name,
        @Schema(description = "User role in the system", example = "USER")
        Role role
) {
}
