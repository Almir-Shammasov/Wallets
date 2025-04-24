package org.almir.wallets.service;

import org.almir.wallets.entity.Card;
import org.almir.wallets.entity.Limit;
import org.almir.wallets.entity.User;
import org.almir.wallets.enums.LimitType;
import org.almir.wallets.enums.Role;
import org.almir.wallets.exception.CardAccessDeniedException;
import org.almir.wallets.exception.LimitAlreadyExistsException;
import org.almir.wallets.exception.LimitNotFoundException;
import org.almir.wallets.repository.LimitRepository;
import org.almir.wallets.service.impl.LimitServiceImpl;
import org.almir.wallets.utils.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LimitServiceImplTest {
    private LimitRepository limitRepository;
    private SecurityUtils securityUtils;
    private LimitService limitService;

    @BeforeEach
    void setUp() {
        limitRepository = mock(LimitRepository.class);
        securityUtils = mock(SecurityUtils.class);
        limitService = new LimitServiceImpl(limitRepository, securityUtils);
    }

    @Test
    void createLimit_success() {
        Card card = new Card();
        card.setId(1L);
        when(securityUtils.getCardById(1L)).thenReturn(card);
        when(limitRepository.findByCardId(1L)).thenReturn(List.of());
        when(limitRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Limit limit = limitService.createLimit(1L, "daily", 1000.0);

        assertNotNull(limit);
        assertEquals(1000.0, limit.getAmount());
        assertEquals(LimitType.DAILY, limit.getType());
    }

    @Test
    void createLimit_alreadyExists() {
        Card card = new Card();
        card.setId(1L);
        when(securityUtils.getCardById(1L)).thenReturn(card);
        Limit existingLimit = new Limit();
        existingLimit.setType(LimitType.DAILY);
        when(limitRepository.findByCardId(1L)).thenReturn(List.of(existingLimit));

        assertThrows(LimitAlreadyExistsException.class,
                () -> limitService.createLimit(1L, "daily", 500.0));
    }

    @Test
    void getLimits_userHasAccess() {
        User user = new User();
        user.setId(1L);
        user.setRole(Role.USER);

        Card card = new Card();
        card.setId(1L);
        card.setUser(user);

        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(securityUtils.getCardById(1L)).thenReturn(card);
        when(limitRepository.findByCardId(1L)).thenReturn(List.of(new Limit()));

        List<Limit> limits = limitService.getLimits(1L);

        assertNotNull(limits);
        assertFalse(limits.isEmpty());
    }

    @Test
    void getLimits_userDoesNotHaveAccess() {
        User user = new User();
        user.setId(1L);
        user.setRole(Role.USER);

        User anotherUser = new User();
        anotherUser.setId(2L);

        Card card = new Card();
        card.setId(1L);
        card.setUser(anotherUser);

        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(securityUtils.getCardById(1L)).thenReturn(card);

        assertThrows(CardAccessDeniedException.class,
                () -> limitService.getLimits(1L));
    }

    @Test
    void updateLimit_success() {
        Limit limit = new Limit();
        limit.setId(1L);
        limit.setAmount(500.0);
        when(limitRepository.findById(1L)).thenReturn(Optional.of(limit));
        when(limitRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Limit updated = limitService.updateLimit(1L, 1000.0);

        assertNotNull(updated);
        assertEquals(1000.0, updated.getAmount());
    }

    @Test
    void updateLimit_notFound() {
        when(limitRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(LimitNotFoundException.class, () -> limitService.updateLimit(1L, 500.0));
    }

    @Test
    void deleteLimit_success() {
        Limit limit = new Limit();
        limit.setId(1L);
        when(limitRepository.findById(1L)).thenReturn(Optional.of(limit));

        limitService.deleteLimit(1L);

        verify(limitRepository).delete(limit);
    }

    @Test
    void deleteLimit_notFound() {
        when(limitRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(LimitNotFoundException.class, () -> limitService.deleteLimit(1L));
    }
}
