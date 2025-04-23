package org.almir.wallets.dto;

import org.almir.wallets.enums.Role;

public record UserResponseDTO(
        Long id,
        String email,
        String name,
        Role role
) {
}
