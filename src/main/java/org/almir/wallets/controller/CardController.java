package org.almir.wallets.controller;

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
public class CardController {
    private final CardService cardService;
    private final CardMapper cardMapper;
    private final SecurityUtils securityUtils;

    @PostMapping
    public ResponseEntity<CardResponseDTO> createCard(@Valid @RequestBody CardRequestDTO cardRequest) {
        Card card = cardService.createCard(
                cardRequest.userId(),
                cardRequest.cardNumber(),
                cardRequest.expiryDate(),
                cardRequest.initialBalance()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(cardMapper.toResponseDto(card));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Page<CardResponseDTO>> getUserCards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @PathVariable long userId
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CardResponseDTO> cards = cardService.getUserCards(userId, pageable)
                .map(cardMapper::toResponseDto);

        return ResponseEntity.ok(cards);
    }

    @GetMapping
    public ResponseEntity<Page<CardResponseDTO>> getCards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CardResponseDTO> cards = cardService.getCards(pageable)
                .map(cardMapper::toResponseDto);

        return ResponseEntity.ok(cards);
    }

    @PutMapping("/block/{cardId}")
    public ResponseEntity<Void> blockCard(@PathVariable long cardId) throws AccessDeniedException {
        cardService.blockCard(cardId);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/request-block/{cardId}")
    public ResponseEntity<Void> requestBlockCard(@PathVariable Long cardId) {
        long userId = securityUtils.getCurrentUser().getId();

        cardService.requestBlockCard(userId, cardId);
        return ResponseEntity.accepted().build();
    }

    @PutMapping("/activate/{cardId}")
    public ResponseEntity<Void> activateCard(@PathVariable Long cardId) {
        long userId = securityUtils.getCurrentUser().getId();

        cardService.activateCard(userId, cardId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete/{cardId}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long cardId) {
        long userId = securityUtils.getCurrentUser().getId();

        cardService.deleteCard(userId, cardId);
        return ResponseEntity.noContent().build();
    }
}
