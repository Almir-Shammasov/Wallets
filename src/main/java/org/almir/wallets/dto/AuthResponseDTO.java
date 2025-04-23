package org.almir.wallets.dto;

public record AuthResponseDTO(
        String token,
        String email
) {
}
