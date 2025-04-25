package org.almir.wallets.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.almir.wallets.enums.LimitType;

@Schema(description = "DTO used to create or update a spending limit for a card")
public record LimitRequestDTO(
        @Schema(description = "ID of the card the limit is applied to", example = "101")
        Long cardId,
        @Schema(description = "Type of the limit (e.g., DAILY, MONTHLY)", example = "DAILY")
        LimitType type,
        @Schema(description = "Maximum allowed amount for the specified limit type", example = "500.00")
        double amount
) {
}
