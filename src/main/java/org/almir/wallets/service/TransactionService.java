package org.almir.wallets.service;

import org.almir.wallets.entity.Transaction;

public interface TransactionService {
    /**
     * Transfers funds between two cards owned by the same user.
     * @param sourceCardId the ID of the source card
     * @param targetCardId the ID of the target card
     * @param amount the amount to transfer
     * @return the created transaction
     */
    Transaction transfer(Long sourceCardId, Long targetCardId, double amount);

    /**
     * Withdraws funds from a card, checking limits.
     * @param cardId the ID of the card
     * @param amount the amount to withdraw
     * @return the created transaction
     */
    Transaction withdraw(Long cardId, double amount);
}
