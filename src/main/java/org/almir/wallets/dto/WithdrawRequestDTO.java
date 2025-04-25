package org.almir.wallets.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "DTO for requesting a withdrawal from a specific card")
public record WithdrawRequestDTO(
        @Schema(description = "Unique identifier of the card from which the withdrawal will be made", example = "101")
        @NotNull long cardId,
        @Schema(description = "Amount to be withdrawn", example = "100.00")
        @NotNull @Positive double amount
) {
}
