package org.almir.wallets.service;

import org.almir.wallets.entity.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.nio.file.AccessDeniedException;
import java.time.YearMonth;

public interface CardService {

    /**
     * Creates a new card for a user (admin only).
     * @param userId the ID of the user
     * @param cardNumber the card number (16 digits)
     * @param expiryDate the expiry date
     * @param initialBalance the initial balance
     * @return the created card
     */
    Card createCard(long userId, String cardNumber, YearMonth expiryDate, double initialBalance);

    /**
     * Retrieves paginated list of cards for a user.
     * @param userId the ID of the user
     * @param pageable pagination parameters
     * @return page of cards
     */
    Page<Card> getUserCards(long userId, Pageable pageable);

    Page<Card> getCards(Pageable pageable);

    /**
     * Blocks a card (user or admin).
     * @param cardId the ID of the card
     * @param userId the ID of the user (for user role validation)
     */
    void blockCard(long cardId) throws AccessDeniedException;

    void requestBlockCard(Long userId, Long cardId);
    void activateCard(Long userId, Long cardId);
    void deleteCard(Long userId, Long cardId);
}
