package org.almir.wallets.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.almir.wallets.enums.CardStatus;

import java.time.YearMonth;

@Schema(description = "DTO representing card details returned in responses")
public record CardResponseDTO(
        @Schema(description = "Unique identifier of the card", example = "101")
        long id,
        @Schema(description = "Masked card number", example = "**** **** **** 5678")
        String maskedNumber,
        @Schema(description = "Name of the card owner", example = "John Doe")
        String ownerName,
        @Schema(description = "Card expiration date in the format YYYY-MM", example = "2027-05")
        YearMonth expiryDate,
        @Schema(description = "Current status of the card", example = "ACTIVE")
        CardStatus status,
        @Schema(description = "Current balance on the card", example = "1200.00")
        double balance
) {
}
