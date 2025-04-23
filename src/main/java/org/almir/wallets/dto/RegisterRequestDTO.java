package org.almir.wallets.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterRequestDTO(
    @NotBlank String email,
    @NotBlank String password,
    @NotBlank String name
) {
}
