package org.almir.wallets.dto;

import org.almir.wallets.enums.TransactionType;

public record TransactionResponseDTO(
        long id,
        double amount,
        TransactionType transactionType,
        long sourceCardId,
        long targetCardId
) {
}
