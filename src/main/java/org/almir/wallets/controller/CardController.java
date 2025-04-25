package org.almir.wallets.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.almir.wallets.dto.CardRequestDTO;
import org.almir.wallets.dto.CardResponseDTO;
import org.almir.wallets.entity.Card;
import org.almir.wallets.mapper.CardMapper;
import org.almir.wallets.service.CardService;
import org.almir.wallets.utils.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cards")
@Tag(name = "Card API", description = "Operations related to cards")
public class CardController {
    private final CardService cardService;
    private final CardMapper cardMapper;
    private final SecurityUtils securityUtils;

    @PostMapping
    @Operation(summary = "Create a new card",
            description = "Creates a card for the specified user")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Card successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<CardResponseDTO> createCard(
            @Parameter(description = "Card creation request DTO") @Valid @RequestBody CardRequestDTO cardRequest) {
        Card card = cardService.createCard(
                cardRequest.userId(),
                cardRequest.cardNumber(),
                cardRequest.expiryDate(),
                cardRequest.initialBalance()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(cardMapper.toResponseDto(card));
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get cards by user ID",
            description = "Returns a paginated list of cards belonging to the specified user")
    public ResponseEntity<Page<CardResponseDTO>> getUserCards(
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "User ID") @PathVariable long userId
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CardResponseDTO> cards = cardService.getUserCards(userId, pageable)
                .map(cardMapper::toResponseDto);

        return ResponseEntity.ok(cards);
    }

    @GetMapping
    @Operation(summary = "Get all cards",
            description = "Returns a paginated list of all cards")
    public ResponseEntity<Page<CardResponseDTO>> getCards(
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CardResponseDTO> cards = cardService.getCards(pageable)
                .map(cardMapper::toResponseDto);

        return ResponseEntity.ok(cards);
    }

    @PutMapping("/block/{cardId}")
    @Operation(summary = "Block a card",
            description = "Immediately blocks a card by card ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Card successfully blocked"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<Void> blockCard(
            @Parameter(description = "Card ID to block") @PathVariable long cardId) throws AccessDeniedException {
        cardService.blockCard(cardId);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/request-block/{cardId}")
    @Operation(summary = "Request to block a card",
            description = "User-initiated request to block a card")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Request accepted"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<Void> requestBlockCard(
            @Parameter(description = "Card ID to request block for") @PathVariable Long cardId) {
        long userId = securityUtils.getCurrentUser().getId();

        cardService.requestBlockCard(userId, cardId);
        return ResponseEntity.accepted().build();
    }

    @PutMapping("/activate/{cardId}")
    @Operation(summary = "Activate a card",
            description = "Activates a previously blocked card")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Card successfully activated"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<Void> activateCard(
            @Parameter(description = "Card ID to activate") @PathVariable Long cardId) {
        long userId = securityUtils.getCurrentUser().getId();

        cardService.activateCard(userId, cardId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete/{cardId}")
    @Operation(summary = "Delete a card", description = "Deletes the specified card")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Card successfully deleted"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<Void> deleteCard(
            @Parameter(description = "Card ID to delete") @PathVariable Long cardId) {
        long userId = securityUtils.getCurrentUser().getId();

        cardService.deleteCard(userId, cardId);
        return ResponseEntity.noContent().build();
    }
}
