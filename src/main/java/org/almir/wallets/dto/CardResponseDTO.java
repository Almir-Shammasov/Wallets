package org.almir.wallets.dto;

import org.almir.wallets.enums.CardStatus;

import java.time.YearMonth;

public record CardResponseDTO(
        long id,
        String maskedNumber,
        String ownerName,
        YearMonth expiryDate,
        CardStatus status,
        double balance
) {
}
