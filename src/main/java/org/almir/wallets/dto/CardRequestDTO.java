package org.almir.wallets.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.YearMonth;

public record CardRequestDTO(
        @NotBlank @Pattern(regexp = "\\d{16}") String cardNumber,
        @NotNull YearMonth expiryDate,
        @NotNull double initialBalance,
        @NotNull long userId
        ) {
}
