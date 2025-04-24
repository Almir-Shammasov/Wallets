package org.almir.wallets.repository;

import org.almir.wallets.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findBySourceCardId(Long cardId);

    @Query("SELECT t FROM Transaction t WHERE t.sourceCard.cardNumber = :cardNumber OR t.targetCard.cardNumber = :cardNumber")
    List<Transaction> findByCardNumber(@Param("cardNumber") String cardNumber);

    @Query("SELECT t FROM Transaction t WHERE t.sourceCard.id = :cardId OR t.targetCard.id = :cardId")
    List<Transaction> findByCardId(@Param("cardId") long cardId);
}
