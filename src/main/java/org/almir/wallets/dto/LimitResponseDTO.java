package org.almir.wallets.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.almir.wallets.enums.LimitType;

@Schema(description = "DTO returned when retrieving a card's spending limit")
public record LimitResponseDTO(
        @Schema(description = "Unique identifier of the limit", example = "301")
        Long id,
        @Schema(description = "ID of the card the limit is associated with", example = "101")
        Long cardId,
        @Schema(description = "Type of the limit (e.g., DAILY, MONTHLY)", example = "MONTHLY")
        LimitType type,
        @Schema(description = "Maximum allowed amount for the limit", example = "1000.00")
        double amount
) {
}
