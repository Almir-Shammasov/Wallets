package org.almir.wallets.service;

import org.almir.wallets.entity.Card;
import org.almir.wallets.entity.User;
import org.almir.wallets.enums.CardStatus;
import org.almir.wallets.enums.Role;
import org.almir.wallets.exception.CardAccessDeniedException;
import org.almir.wallets.exception.InvalidCardNumberException;
import org.almir.wallets.exception.InvalidCardStatusException;
import org.almir.wallets.exception.InvalidExpiryDateException;
import org.almir.wallets.mapper.CardMapper;
import org.almir.wallets.repository.CardRepository;
import org.almir.wallets.repository.UserRepository;
import org.almir.wallets.service.impl.CardServiceImpl;
import org.almir.wallets.utils.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.YearMonth;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CardServiceImplTest {
    private CardRepository cardRepository;
    private UserRepository userRepository;
    private CardMapper cardMapper;
    private SecurityUtils securityUtils;
    private CardServiceImpl cardService;

    private final User adminUser = User.builder().id(1L).role(Role.ADMIN).build();
    private final User regularUser = User.builder().id(2L).role(Role.USER).build();
    private final YearMonth futureDate = YearMonth.now().plusMonths(1);

    @BeforeEach
    void setUp() {
        cardRepository = mock(CardRepository.class);
        userRepository = mock(UserRepository.class);
        cardMapper = mock(CardMapper.class);
        securityUtils = mock(SecurityUtils.class);
        cardService = new CardServiceImpl(cardRepository, userRepository, cardMapper, securityUtils);
    }

    @Test
    void createCard_success() {
        String cardNumber = "1234567812345678";
        double balance = 100.0;

        when(cardRepository.existsByCardNumber(cardNumber)).thenReturn(false);
        doNothing().when(securityUtils).checkAdminAccess();
        when(userRepository.findById(1L)).thenReturn(Optional.of(adminUser));
        when(cardMapper.toEntity(any())).thenReturn(new Card());

        cardService.createCard(1L, cardNumber, futureDate, balance);

        verify(cardRepository).save(any(Card.class));
    }

    @Test
    void createCard_invalidNumber() {
        assertThrows(InvalidCardNumberException.class, () ->
                cardService.createCard(1L, "invalid", futureDate, 100));
    }

    @Test
    void createCard_expiredDate() {
        YearMonth past = YearMonth.now().minusMonths(1);
        assertThrows(InvalidExpiryDateException.class, () ->
                cardService.createCard(1L, "1234567812345678", past, 100));
    }

    @Test
    void createCard_negativeBalance() {
        assertThrows(IllegalArgumentException.class, () ->
                cardService.createCard(1L, "1234567812345678", futureDate, -100));
    }

    @Test
    void getUserCards_self_access_success() {
        when(userRepository.existsById(2L)).thenReturn(true);
        when(securityUtils.getCurrentUser()).thenReturn(regularUser);
        when(cardRepository.findByUserId(eq(2L), any())).thenReturn(Page.empty());

        Page<Card> result = cardService.getUserCards(2L, Pageable.unpaged());

        assertNotNull(result);
    }

    @Test
    void getUserCards_accessDenied() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(securityUtils.getCurrentUser()).thenReturn(regularUser);

        assertThrows(CardAccessDeniedException.class, () ->
                cardService.getUserCards(1L, Pageable.unpaged()));
    }

    @Test
    void blockCard_success() {
        Card card = new Card();
        card.setStatus(CardStatus.ACTIVE);

        doNothing().when(securityUtils).checkAdminAccess();
        when(securityUtils.getCardById(1L)).thenReturn(card);

        cardService.blockCard(1L);

        assertEquals(CardStatus.BLOCKED, card.getStatus());
        verify(cardRepository).save(card);
    }

    @Test
    void blockCard_invalidStatus() {
        Card card = new Card();
        card.setStatus(CardStatus.BLOCKED);

        when(securityUtils.getCardById(1L)).thenReturn(card);
        doNothing().when(securityUtils).checkAdminAccess();

        assertThrows(InvalidCardStatusException.class, () -> cardService.blockCard(1L));
    }

    @Test
    void requestBlockCard_success() {
        Card card = new Card();
        card.setUser(regularUser);
        card.setStatus(CardStatus.ACTIVE);

        when(securityUtils.getCardById(1L)).thenReturn(card);

        cardService.requestBlockCard(2L, 1L);

        assertTrue(card.isBlockRequested());
        verify(cardRepository).save(card);
    }

    @Test
    void requestBlockCard_alreadyRequested() {
        Card card = new Card();
        card.setUser(regularUser);
        card.setStatus(CardStatus.ACTIVE);
        card.setBlockRequested(true);

        when(securityUtils.getCardById(1L)).thenReturn(card);

        assertThrows(InvalidCardStatusException.class, () ->
                cardService.requestBlockCard(2L, 1L));
    }

    @Test
    void activateCard_success() {
        Card card = new Card();
        card.setStatus(CardStatus.BLOCKED);

        when(securityUtils.getCardById(1L)).thenReturn(card);
        doNothing().when(securityUtils).checkAdminAccess();
        when(userRepository.findById(1L)).thenReturn(Optional.of(adminUser));

        cardService.activateCard(1L, 1L);

        assertEquals(CardStatus.ACTIVE, card.getStatus());
        verify(cardRepository).save(card);
    }

    @Test
    void activateCard_wrongRole() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(regularUser));
        Card card = new Card();
        card.setStatus(CardStatus.BLOCKED);

        when(securityUtils.getCardById(1L)).thenReturn(card);
        doNothing().when(securityUtils).checkAdminAccess();

        assertThrows(CardAccessDeniedException.class, () -> cardService.activateCard(2L, 1L));
    }

    @Test
    void deleteCard_success() {
        Card card = new Card();

        when(securityUtils.getCardById(1L)).thenReturn(card);
        doNothing().when(securityUtils).checkAdminAccess();

        cardService.deleteCard(1L, 1L);

        verify(cardRepository).delete(card);
    }
}
