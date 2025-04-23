package org.almir.wallets.dto;

import org.almir.wallets.enums.LimitType;

public record LimitResponseDTO(
        Long id,
        Long cardId,
        LimitType type,
        double amount
) {
}
