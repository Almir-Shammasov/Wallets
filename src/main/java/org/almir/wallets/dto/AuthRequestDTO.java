package org.almir.wallets.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Authentication request containing user credentials.")
public record AuthRequestDTO(
        @Schema(description = "User's email address", example = "user@example.com", required = true)
        @NotBlank String email,
        @Schema(description = "User's password", example = "P@ssw0rd", required = true)
        @NotBlank String password
) {
}
