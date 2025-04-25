package org.almir.wallets.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Authentication response containing JWT token and user email.")
public record AuthResponseDTO(
        @Schema(description = "JWT access token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String token,
        @Schema(description = "Authenticated user's email address", example = "user@example.com")
        String email
) {
}
