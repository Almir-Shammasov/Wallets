package org.almir.wallets.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.YearMonth;

@Schema(description = "DTO representing a request to create a new card")
public record CardRequestDTO(
        @Schema(description = "Card number consisting of 16 digits", example = "1111111111111111")
        @NotBlank @Pattern(regexp = "\\d{16}") String cardNumber,
        @Schema(description = "Card expiration date in the format YYYY-MM", example = "2027-05")
        @NotNull YearMonth expiryDate,
        @Schema(description = "Initial balance for the card", example = "1000.0")
        @NotNull double initialBalance,
        @Schema(description = "ID of the user to whom the card will belong", example = "1")
        @NotNull long userId
        ) {
}
