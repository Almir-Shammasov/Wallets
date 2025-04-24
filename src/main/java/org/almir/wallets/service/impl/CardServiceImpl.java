package org.almir.wallets.service.impl;

import lombok.RequiredArgsConstructor;
import org.almir.wallets.dto.CardRequestDTO;
import org.almir.wallets.entity.Card;
import org.almir.wallets.entity.User;
import org.almir.wallets.enums.CardStatus;
import org.almir.wallets.enums.Role;
import org.almir.wallets.exception.*;
import org.almir.wallets.mapper.CardMapper;
import org.almir.wallets.repository.CardRepository;
import org.almir.wallets.repository.UserRepository;
import org.almir.wallets.service.CardService;
import org.almir.wallets.utils.SecurityUtils;
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
    private final CardMapper cardMapper;
    private final SecurityUtils securityUtils;

    @Override
    @Transactional
    public Card createCard(long userId, String cardNumber, YearMonth expiryDate, double initialBalance) {
        if(cardRepository.existsByCardNumber(cardNumber)) {
            throw new CardAlreadyExistsException("Card id already exist " + cardNumber);
        }
        securityUtils.checkAdminAccess();
        validateCardNumber(cardNumber);
        validateExpiryDate(expiryDate);
        validateBalance(initialBalance);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));

        CardRequestDTO requestDto = new CardRequestDTO(cardNumber, expiryDate, initialBalance, userId);
        Card card = cardMapper.toEntity(requestDto);
        card.setUser(user);
        card.setMaskedNumber(maskCardNumber(cardNumber));
        card.setStatus(CardStatus.ACTIVE);

        return cardRepository.save(card);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Card> getUserCards(long userId, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found: " + userId);
        }

        User currentUser = securityUtils.getCurrentUser();
        long expectedUserId = currentUser.getId();

        if (expectedUserId != userId && currentUser.getRole().name().equals("USER")) {
            throw new CardAccessDeniedException("Access denied to cards of user: " + userId);
        }

        return cardRepository.findByUserId(userId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Card> getCards(Pageable pageable) {
        return cardRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public void blockCard(long cardId) {
        securityUtils.checkAdminAccess();
        Card card = securityUtils.getCardById(cardId);

        if (card.getStatus() != CardStatus.ACTIVE) {
            throw new InvalidCardStatusException("Card is not active: " + cardId);
        }

        card.setStatus(CardStatus.BLOCKED);
        cardRepository.save(card);
    }

    @Override
    @Transactional
    public void requestBlockCard(Long userId, Long cardId) {
        Card card = securityUtils.getCardById(cardId);

        if (!card.getUser().getId().equals(userId)) {
            throw new CardAccessDeniedException("User does not own card: " + cardId);
        }

        if (card.getStatus() != CardStatus.ACTIVE) {
            throw new InvalidCardStatusException("Card is not active: " + cardId);
        }

        if (card.isBlockRequested()) {
            throw new InvalidCardStatusException("Block request already exists for card: " + cardId);
        }

        card.setBlockRequested(true);
        cardRepository.save(card);
    }

    @Override
    @Transactional
    public void activateCard(Long userId, Long cardId) {
        securityUtils.checkAdminAccess();
        Card card = securityUtils.getCardById(cardId);

        if (userRepository.findById(userId)
                .map(User::getRole)
                .map(Role::name)
                .filter(Role.ADMIN.name()::equals)
                .isEmpty()) {
            throw new CardAccessDeniedException("Only admin can activate cards: " + cardId);
        }

        if (card.getStatus() != CardStatus.BLOCKED) {
            throw new InvalidCardStatusException("Card is not blocked: " + cardId);
        }

        card.setStatus(CardStatus.ACTIVE);
        cardRepository.save(card);
    }

    @Override
    @Transactional
    public void deleteCard(Long userId, Long cardId) {
        securityUtils.checkAdminAccess();
        Card card = securityUtils.getCardById(cardId);

        cardRepository.delete(card);
    }

    private void validateCardNumber(String cardNumber) {
        if (cardNumber == null || !CARD_NUMBER_PATTERN.matcher(cardNumber).matches()) {
            throw new InvalidCardNumberException("Card number must be 16 digits");
        }
    }

    private void validateExpiryDate(YearMonth expiryDate) {
        if (expiryDate == null) {
            throw new InvalidExpiryDateException("Expiry date cannot be null");
        }
        if (expiryDate.isBefore(YearMonth.now())) {
            throw new InvalidExpiryDateException("Expiry date must be in the future");
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
