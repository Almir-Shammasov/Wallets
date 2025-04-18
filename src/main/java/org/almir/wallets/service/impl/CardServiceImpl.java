package org.almir.wallets.service.impl;

import lombok.RequiredArgsConstructor;
import org.almir.wallets.entity.Card;
import org.almir.wallets.entity.User;
import org.almir.wallets.enums.CardStatus;
import org.almir.wallets.exception.*;
import org.almir.wallets.repository.CardRepository;
import org.almir.wallets.repository.UserRepository;
import org.almir.wallets.service.CardService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {
    private static final Pattern CARD_NUMBER_PATTERN = Pattern.compile("\\d{16}");
    private static final String MASKED_NUMBER_FORMAT = "**** **** **** %s";

    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Card createCard(long userId, String cardNumber, YearMonth expiryDate, double initialBalance) {
        validateCardNumber(cardNumber);
        validateExpiryDate(expiryDate);
        validateBalance(initialBalance);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));

        Card card = Card.builder()
                .cardNumber(cardNumber)
                .maskedNumber(maskCardNumber(cardNumber))
                .user(user)
                .expiryDate(expiryDate)
                .status(CardStatus.ACTIVE)
                .balance(initialBalance)
                .build();

        return cardRepository.save(card);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Card> getUserCards(long userId, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found: " + userId);
        }

        return cardRepository.findByUserId(userId, pageable);
    }

    @Override
    @Transactional
    public void blockCard(long cardId, long userId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card not found: " + cardId));

        if (!card.getUser().getId().equals(userId)) {
            throw new CardAccessDeniedException("User does not own this card");
        }

        if (card.getStatus() != CardStatus.ACTIVE) {
            throw new IllegalStateException("Card is not active");
        }

        card.setStatus(CardStatus.BLOCKED);
        cardRepository.save(card);
    }

    private void validateCardNumber(String cardNumber) {
        if (cardNumber == null || !CARD_NUMBER_PATTERN.matcher(cardNumber).matches()) {
            throw new InvalidCardNumberException("Card number must be 16 digits");
        }
    }

    private void validateExpiryDate(YearMonth expiryDate) {
        if (expiryDate == null || expiryDate.isBefore(YearMonth.now())) {
            throw new InvalidExpiryDateException("The card has expired");
        }
    }

    private void validateBalance(double balance) {
        if (balance < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative");
        }
    }

    private String maskCardNumber(String cardNumber) {
        String lastFourDigits = cardNumber.substring(cardNumber.length() - 4);
        return String.format(MASKED_NUMBER_FORMAT, lastFourDigits);
    }
}
