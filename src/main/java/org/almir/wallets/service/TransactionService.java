package org.almir.wallets.service;

import org.almir.wallets.dto.TransactionResponseDTO;
import org.almir.wallets.entity.Transaction;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for managing transaction-related operations.
 */
public interface TransactionService {
    /**
     * Transfers funds between two cards owned by the same user.
     *
     * @param sourceCardId the ID of the source card
     * @param targetCardId the ID of the target card
     * @param amount the amount to transfer
     * @return the created transaction
     */
    Transaction transfer(Long sourceCardId, Long targetCardId, double amount);

    /**
     * Withdraws funds from a card, checking limits.
     *
     * @param cardId the ID of the card
     * @param amount the amount to withdraw
     * @return the created transaction
     */
    Transaction withdraw(Long cardId, double amount);

    /**
     * Retrieves a paginated list of transactions for a user, filtered by their role.
     *
     * @param userId the ID of the user whose transactions are to be retrieved
     * @param role the role of the user (ADMIN, USER) to determine access level
     * @param pageable the pagination information (page number, size, sorting)
     * @return a list of TransactionResponseDTO containing transaction details
     */
    List<TransactionResponseDTO> getAllTransactions(Long userId, String role, Pageable pageable);

    /**
     * Retrieves all transactions associated with a given card number.
     *
     * @param cardNumber the card number to filter transactions
     * @return a list of TransactionResponseDto containing transaction details
     */
    List<TransactionResponseDTO> getTransactionsByCardNumber(String cardNumber, Long userId, String role);
}
