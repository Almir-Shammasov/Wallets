package org.almir.wallets.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.almir.wallets.enums.TransactionType;

@Schema(description = "DTO representing the response of a transaction")
public record TransactionResponseDTO(
        @Schema(description = "Unique identifier for the transaction", example = "100")
        long id,
        @Schema(description = "Amount involved in the transaction", example = "500.0")
        double amount,
        @Schema(description = "Type of the transaction (e.g., transfer, withdrawal)", example = "TRANSFER")
        TransactionType transactionType,
        @Schema(description = "ID of the source card involved in the transaction", example = "101")
        long sourceCardId,
        @Schema(description = "ID of the target card involved in the transaction", example = "202")
        long targetCardId
) {
}
