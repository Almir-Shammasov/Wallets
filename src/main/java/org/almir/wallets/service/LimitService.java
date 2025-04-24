package org.almir.wallets.service;

import org.almir.wallets.entity.Limit;

import java.util.List;

/**
 * Service interface for managing transaction limits associated with cards.
 */
public interface LimitService {
    /**
     * Creates a new transaction limit for a specified card.
     *
     * @param cardId the ID of the card to associate the limit with
     * @param limitType the type of limit (e.g., DAILY, MONTHLY)
     * @param amount the maximum allowed amount for the limit
     * @return the created Limit entity
     */
    Limit createLimit(long cardId, String limitType, double amount);

    /**
     * Retrieves all transaction limits associated with a specified card.
     *
     * @param cardId the ID of the card to retrieve limits for
     * @return a list of Limit entities associated with the card
     */
    List<Limit> getLimits(long cardId);

    /**
     * Updates an existing transaction limit with a new amount.
     *
     * @param limitId the ID of the limit to update
     * @param amount the new maximum allowed amount for the limit
     * @return the updated Limit entity
     */
    Limit updateLimit(long limitId, double amount);

    /**
     * Deletes a transaction limit by its ID.
     *
     * @param limitId the ID of the limit to delete
     */
    void deleteLimit(long limitId);
}
