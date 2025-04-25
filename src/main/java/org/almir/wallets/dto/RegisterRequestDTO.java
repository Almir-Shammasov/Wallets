package org.almir.wallets.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "DTO used for user registration")
public record RegisterRequestDTO(
        @Schema(description = "User's email address", example = "user@example.com")
        @NotBlank String email,
        @Schema(description = "User's password", example = "password123")
        @NotBlank String password,
        @Schema(description = "User's full name", example = "Alice Smith")
        @NotBlank String name
) {
}
