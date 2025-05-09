package org.almir.wallets.service;

import org.almir.wallets.entity.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.nio.file.AccessDeniedException;
import java.time.YearMonth;

/**
 * Service interface for managing card-related operations.
 */
public interface CardService {

    /**
     * Creates a new card for a user (admin only).
     *
     * @param userId the ID of the user
     * @param cardNumber the card number (16 digits)
     * @param expiryDate the expiry date
     * @param initialBalance the initial balance
     * @return the created card
     */
    Card createCard(long userId, String cardNumber, YearMonth expiryDate, double initialBalance);

    /**
     * Retrieves paginated list of cards for a user.
     *
     * @param userId the ID of the user
     * @param pageable pagination parameters
     * @return page of cards
     */
    Page<Card> getUserCards(long userId, Pageable pageable);

    /**
     * Retrieves paginated list of all cards
     *
     * @param pageable pagination parameters
     * @return page of cards
     */
    Page<Card> getCards(Pageable pageable);

    /**
     * Blocks a card (user or admin).
     *
     * @param cardId the ID of the card
     * @param userId the ID of the user (for user role validation)
     */
    void blockCard(long cardId) throws AccessDeniedException;

    /**
     * Requests to block a card for a specific user.
     *
     * @param userId the ID of the user requesting the block
     * @param cardId the ID of the card to block
     */
    void requestBlockCard(Long userId, Long cardId);

    /**
     * Activates a previously blocked card for a specific user.
     *
     * @param userId the ID of the user requesting the activation
     * @param cardId the ID of the card to activate
     */
    void activateCard(Long userId, Long cardId);

    /**
     * Deletes a card for a specific user.
     *
     * @param userId the ID of the user requesting the deletion
     * @param cardId the ID of the card to delete
     */
    void deleteCard(Long userId, Long cardId);
}
