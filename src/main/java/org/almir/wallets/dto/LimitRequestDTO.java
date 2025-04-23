package org.almir.wallets.dto;

import org.almir.wallets.enums.LimitType;

public record LimitRequestDTO(
        Long cardId,
        LimitType type,
        double amount
) {
}
