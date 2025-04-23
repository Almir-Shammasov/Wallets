package org.almir.wallets.dto;

import jakarta.validation.constraints.NotBlank;
import org.almir.wallets.enums.Role;

public record UserRequestDTO(
        @NotBlank String email,
        @NotBlank String password,
        @NotBlank String name,
        Role role
) {
}
