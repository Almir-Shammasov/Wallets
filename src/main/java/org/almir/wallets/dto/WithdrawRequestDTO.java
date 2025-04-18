package org.almir.wallets.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record WithdrawRequestDTO(
        @NotNull long cardId,
        @NotNull @Positive double amount
) {
}
