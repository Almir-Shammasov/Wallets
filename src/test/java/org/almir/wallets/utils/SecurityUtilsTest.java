package org.almir.wallets.utils;

import org.almir.wallets.entity.Card;
import org.almir.wallets.entity.User;
import org.almir.wallets.enums.Role;
import org.almir.wallets.exception.CardAccessDeniedException;
import org.almir.wallets.exception.CardNotFoundException;
import org.almir.wallets.exception.UserNotFoundException;
import org.almir.wallets.repository.CardRepository;
import org.almir.wallets.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SecurityUtilsTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private CardRepository cardRepository;

    private SecurityUtils securityUtils;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        securityUtils = new SecurityUtils(userRepository, cardRepository);

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(
                new UsernamePasswordAuthenticationToken("user@example.com", null)
        );
        SecurityContextHolder.setContext(context);
    }

    @Test
    void getCurrentUser_success() {
        User user = User.builder().email("user@example.com").role(Role.USER).build();
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        User result = securityUtils.getCurrentUser();
        assertEquals("user@example.com", result.getEmail());
    }

    @Test
    void getCurrentUser_userNotFound() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> securityUtils.getCurrentUser());
    }

    @Test
    void getCardById_success() {
        Card card = Card.builder().id(1L).build();
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        Card result = securityUtils.getCardById(1L);
        assertEquals(1L, result.getId());
    }

    @Test
    void getCardById_notFound() {
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class, () -> securityUtils.getCardById(1L));
    }

    @Test
    void checkAdminAccess_success() {
        User user = User.builder().email("user@example.com").role(Role.ADMIN).build();
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        assertDoesNotThrow(() -> securityUtils.checkAdminAccess());
    }

    @Test
    void checkAdminAccess_accessDenied() {
        User user = User.builder().email("user@example.com").role(Role.USER).build();
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> securityUtils.checkAdminAccess());
    }

    @Test
    void checkAdminRole_success() {
        User user = User.builder().email("user@example.com").role(Role.ADMIN).build();
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        assertDoesNotThrow(() -> securityUtils.checkAdminRole());
    }

    @Test
    void checkAdminRole_denied() {
        User user = User.builder().email("user@example.com").role(Role.USER).build();
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        assertThrows(CardAccessDeniedException.class, () -> securityUtils.checkAdminRole());
    }
}
