package org.almir.wallets.utils;

import lombok.RequiredArgsConstructor;
import org.almir.wallets.entity.Card;
import org.almir.wallets.entity.User;
import org.almir.wallets.enums.Role;
import org.almir.wallets.exception.CardAccessDeniedException;
import org.almir.wallets.exception.CardNotFoundException;
import org.almir.wallets.exception.UserNotFoundException;
import org.almir.wallets.repository.CardRepository;
import org.almir.wallets.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtils {
    private final UserRepository userRepository;
    private final CardRepository cardRepository;

    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Current user not found: " + email));
    }

    public Card getCardById(long cardId) {
        return cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card not found: " + cardId));
    }

    public void checkAdminAccess() {
        User user = getCurrentUser();

        if (!user.getRole().equals(Role.ADMIN)) {
            throw new AccessDeniedException("Admin access required");
        }
    }

    public void checkAdminRole() {
        User currentUser = getCurrentUser();

        if (!currentUser.getRole().name().equals(Role.ADMIN.name())) {
            throw new CardAccessDeniedException("Only admin can manage limits");
        }
    }
}
