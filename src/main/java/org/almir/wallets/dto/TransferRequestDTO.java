package org.almir.wallets.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TransferRequestDTO(
        @NotNull long sourceCardId,
        @NotNull long targetCardId,
        @NotNull @Positive double amount
) {
}
