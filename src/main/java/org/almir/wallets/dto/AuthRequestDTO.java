package org.almir.wallets.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthRequestDTO(
        @NotBlank String email,
        @NotBlank String password
) {
}
