package org.almir.wallets.service.impl;

import lombok.RequiredArgsConstructor;
import org.almir.wallets.entity.Card;
import org.almir.wallets.entity.Limit;
import org.almir.wallets.entity.Transaction;
import org.almir.wallets.entity.User;
import org.almir.wallets.enums.CardStatus;
import org.almir.wallets.enums.LimitType;
import org.almir.wallets.enums.TransactionType;
import org.almir.wallets.exception.CardAccessDeniedException;
import org.almir.wallets.exception.CardNotFoundException;
import org.almir.wallets.exception.InsufficientFundsException;
import org.almir.wallets.exception.OverLimitException;
import org.almir.wallets.repository.CardRepository;
import org.almir.wallets.repository.LimitRepository;
import org.almir.wallets.repository.TransactionRepository;
import org.almir.wallets.service.TransactionService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final CardRepository cardRepository;
    private final TransactionRepository transactionRepository;
    private final LimitRepository limitRepository;

    @Override
    public Transaction transfer(Long sourceCardId, Long targetCardId, double amount) {
        long userId = cardRepository.findById(sourceCardId)
                .map(Card::getUser)
                .map(User::getId)
                .orElseThrow(() -> new CardNotFoundException("Source card not found: " + sourceCardId));

        validateAmount(amount);

        Card sourceCard = cardRepository.findById(sourceCardId)
                .orElseThrow(() -> new CardNotFoundException("Source card not found: " + sourceCardId));
        Card targetCard = cardRepository.findById(targetCardId)
                .orElseThrow(() -> new CardNotFoundException("Target card not found: " + targetCardId));

        validateCardOwnership(userId, sourceCard, targetCard);
        validateCardStatus(sourceCard);
        validateCardStatus(targetCard);
        validateBalance(sourceCard, amount);
        checkLimits(sourceCard, amount);

        sourceCard.setBalance(sourceCard.getBalance() - amount);
        targetCard.setBalance(targetCard.getBalance() + amount);

        Transaction transaction = Transaction.builder()
                .amount(amount)
                .type(TransactionType.TRANSFER)
                .transactionTime(LocalDateTime.now())
                .sourceCard(sourceCard)
                .targetCard(targetCard)
                .build();

        cardRepository.save(sourceCard);
        cardRepository.save(targetCard);

        return  transactionRepository.save(transaction);
    }

    @Override
    public Transaction withdraw(Long cardId, double amount) {
        long userId = cardRepository.findById(cardId)
                .map(Card::getUser)
                .map(User::getId)
                .orElseThrow(() -> new CardNotFoundException("Card not found: " + cardId));

        validateAmount(amount);

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card not found: " + cardId));

        validateCardOwnership(userId, card);
        validateCardStatus(card);
        validateBalance(card, amount);
        checkLimits(card, amount);

        card.setBalance(card.getBalance() - amount);

        Transaction transaction = Transaction.builder()
                .amount(amount)
                .type(TransactionType.WITHDRAW)
                .transactionTime(LocalDateTime.now())
                .sourceCard(card)
                .build();

        cardRepository.save(card);

        return transactionRepository.save(transaction);
    }

    private void validateAmount(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }

    private void validateCardOwnership(long userId, Card... cards) {
        for (Card card : cards) {
            if (!card.getUser().getId().equals(userId)) {
                throw new CardAccessDeniedException("User does not own card: " + card.getId());
            }
        }
    }

    private void validateCardStatus(Card card) {
        if (card.getStatus() != CardStatus.ACTIVE) {
            throw new IllegalStateException("Card is not active: " + card.getId());
        }
    }

    private void validateBalance(Card card, double amount) {
        if (card.getBalance() < amount) {
            throw new InsufficientFundsException("Insufficient balance on card: " + card.getId());
        }
    }

    private void checkLimits(Card card, double amount) {
        List<Limit> limits = limitRepository.findByCardId(card.getId());
        LocalDate today = LocalDate.now();
        LocalDate thisMonth = today.withDayOfMonth(1);

        for (Limit limit : limits) {
            double spent = calculateSpentAmount(card, limit.getType(), today, thisMonth);
            if (spent + amount > limit.getAmount()) {
                throw new OverLimitException("Limit exceeded for " + limit.getType() + ": " + limit.getAmount());
            }
        }
    }

    private double calculateSpentAmount(Card card, LimitType limitType, LocalDate today, LocalDate month) {
        List<Transaction> transactions = transactionRepository.findBySourceCardId(card.getId());
        return transactions.stream()
                .filter(t -> {
                    LocalDate txDate = t.getTransactionTime().toLocalDate();
                    return limitType == LimitType.DAILY ? txDate.equals(today) : txDate.isAfter(month);
                })
                .mapToDouble(Transaction::getAmount)
                .sum();
    }
}
