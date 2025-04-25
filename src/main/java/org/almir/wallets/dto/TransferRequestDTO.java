package org.almir.wallets.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "DTO representing the transfer request details")
public record TransferRequestDTO(
        @Schema(description = "ID of the source card", example = "101")
        @NotNull long sourceCardId,
        @Schema(description = "ID of the target card", example = "202")
        @NotNull long targetCardId,
        @Schema(description = "Amount to be transferred", example = "100.50")
        @NotNull @Positive double amount
) {
}
