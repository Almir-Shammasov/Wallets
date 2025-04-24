package org.almir.wallets.service;

import org.almir.wallets.dto.TransactionResponseDTO;
import org.almir.wallets.entity.Card;
import org.almir.wallets.entity.Limit;
import org.almir.wallets.entity.Transaction;
import org.almir.wallets.entity.User;
import org.almir.wallets.enums.CardStatus;
import org.almir.wallets.enums.LimitType;
import org.almir.wallets.enums.Role;
import org.almir.wallets.enums.TransactionType;
import org.almir.wallets.exception.CardAccessDeniedException;
import org.almir.wallets.exception.InsufficientFundsException;
import org.almir.wallets.exception.OverLimitException;
import org.almir.wallets.mapper.TransactionMapper;
import org.almir.wallets.repository.CardRepository;
import org.almir.wallets.repository.LimitRepository;
import org.almir.wallets.repository.TransactionRepository;
import org.almir.wallets.repository.UserRepository;
import org.almir.wallets.service.impl.TransactionServiceImpl;
import org.almir.wallets.utils.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TransactionServiceImplTest {
    private CardRepository cardRepository;
    private UserRepository userRepository;
    private TransactionRepository transactionRepository;
    private LimitRepository limitRepository;
    private TransactionMapper transactionMapper;
    private SecurityUtils securityUtils;

    private TransactionServiceImpl transactionService;
    private User user;
    private Card card1, card2;

    @BeforeEach
    void setUp() {
        cardRepository = mock(CardRepository.class);
        userRepository = mock(UserRepository.class);
        transactionRepository = mock(TransactionRepository.class);
        limitRepository = mock(LimitRepository.class);
        transactionMapper = mock(TransactionMapper.class);
        securityUtils = mock(SecurityUtils.class);

        transactionService = new TransactionServiceImpl(
                cardRepository, userRepository, transactionRepository,
                limitRepository, transactionMapper, securityUtils);

        user = User.builder().id(1L).role(Role.USER).build();
        card1 = Card.builder().id(10L).user(user).status(CardStatus.ACTIVE).balance(1000.0).build();
        card2 = Card.builder().id(20L).user(user).status(CardStatus.ACTIVE).balance(500.0).build();

        when(securityUtils.getCurrentUser()).thenReturn(user);
    }

    @Test
    void transfer_success() {
        when(cardRepository.findById(10L)).thenReturn(Optional.of(card1));
        when(cardRepository.findById(20L)).thenReturn(Optional.of(card2));
        when(transactionRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        Transaction tx = transactionService.transfer(10L, 20L, 100.0);

        assertEquals(900.0, card1.getBalance());
        assertEquals(600.0, card2.getBalance());
        assertEquals(TransactionType.TRANSFER, tx.getType());
    }

    @Test
    void transfer_insufficientFunds() {
        card1.setBalance(50);
        when(cardRepository.findById(10L)).thenReturn(Optional.of(card1));
        when(cardRepository.findById(20L)).thenReturn(Optional.of(card2));

        assertThrows(InsufficientFundsException.class, () ->
                transactionService.transfer(10L, 20L, 100.0));
    }

    @Test
    void transfer_cardNotActive() {
        card1.setStatus(CardStatus.BLOCKED);
        when(cardRepository.findById(10L)).thenReturn(Optional.of(card1));
        when(cardRepository.findById(20L)).thenReturn(Optional.of(card2));

        assertThrows(IllegalStateException.class, () ->
                transactionService.transfer(10L, 20L, 100.0));
    }

    @Test
    void withdraw_success() {
        when(securityUtils.getCardById(10L)).thenReturn(card1);
        when(transactionRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        Transaction tx = transactionService.withdraw(10L, 200.0);

        assertEquals(800.0, card1.getBalance());
        assertEquals(TransactionType.WITHDRAW, tx.getType());
    }

    @Test
    void withdraw_insufficientBalance() {
        card1.setBalance(50);
        when(securityUtils.getCardById(10L)).thenReturn(card1);

        assertThrows(InsufficientFundsException.class, () ->
                transactionService.withdraw(10L, 100.0));
    }

    @Test
    void withdraw_overLimit_daily() {
        Limit dailyLimit = new Limit(1L, LimitType.DAILY, 100.0, card1);
        Transaction t1 = Transaction.builder().amount(90.0).transactionTime(LocalDateTime.now()).sourceCard(card1).build();

        when(securityUtils.getCardById(10L)).thenReturn(card1);
        when(limitRepository.findByCardId(10L)).thenReturn(List.of(dailyLimit));
        when(transactionRepository.findBySourceCardId(10L)).thenReturn(List.of(t1));

        assertThrows(OverLimitException.class, () ->
                transactionService.withdraw(10L, 20.0));
    }

    @Test
    void getAllTransactions_admin() {
        Transaction t = Transaction.builder().id(1L).amount(100.0).type(TransactionType.TRANSFER)
                .sourceCard(card1).targetCard(card2).transactionTime(LocalDateTime.now()).build();

        when(transactionRepository.findAll()).thenReturn(List.of(t));
        when(transactionMapper.toResponseDto(t)).thenReturn(
                new TransactionResponseDTO(1L, 100.0, TransactionType.TRANSFER, 10L, 20L)
        );

        List<TransactionResponseDTO> result = transactionService.getAllTransactions(1L, "ADMIN", Pageable.unpaged());

        assertEquals(1, result.size());
        assertEquals(100.0, result.get(0).amount());
    }

    @Test
    void getAllTransactions_user() {
        when(cardRepository.findByUserId(1L)).thenReturn(List.of(card1));
        Transaction t = Transaction.builder().id(2L).amount(200.0).type(TransactionType.WITHDRAW)
                .sourceCard(card1).transactionTime(LocalDateTime.now()).build();

        when(transactionRepository.findByCardId(10L)).thenReturn(List.of(t));
        when(transactionMapper.toResponseDto(t)).thenReturn(
                new TransactionResponseDTO(2L, 200.0, TransactionType.WITHDRAW, 10L, 0L)
        );

        List<TransactionResponseDTO> result = transactionService.getAllTransactions(1L, "USER", Pageable.unpaged());

        assertEquals(1, result.size());
        assertEquals(200.0, result.get(0).amount());
    }

    @Test
    void getTransactionsByCardNumber_success() {
        when(cardRepository.findByCardNumber("1234567812345678")).thenReturn(Optional.of(card1));
        Transaction t = Transaction.builder().id(3L).amount(150.0).type(TransactionType.WITHDRAW)
                .sourceCard(card1).transactionTime(LocalDateTime.now()).build();

        when(transactionRepository.findByCardId(10L)).thenReturn(List.of(t));
        when(transactionMapper.toResponseDto(t)).thenReturn(
                new TransactionResponseDTO(3L, 150.0, TransactionType.WITHDRAW, 10L, 0L)
        );

        List<TransactionResponseDTO> result = transactionService.getTransactionsByCardNumber("1234567812345678", 1L, "USER");

        assertEquals(1, result.size());
        assertEquals(150.0, result.get(0).amount());
    }

    @Test
    void getTransactionsByCardNumber_accessDenied() {
        card1.setUser(User.builder().id(99L).build());
        when(cardRepository.findByCardNumber("123")).thenReturn(Optional.of(card1));

        assertThrows(CardAccessDeniedException.class, () ->
                transactionService.getTransactionsByCardNumber("123", 1L, "USER"));
    }

    @Test
    void validateAmount_negative() {
        assertThrows(IllegalArgumentException.class, () ->
                transactionService.transfer(10L, 20L, -10));
    }
}
