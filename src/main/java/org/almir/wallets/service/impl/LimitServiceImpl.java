package org.almir.wallets.service.impl;

import lombok.RequiredArgsConstructor;
import org.almir.wallets.entity.Card;
import org.almir.wallets.entity.Limit;
import org.almir.wallets.entity.User;
import org.almir.wallets.enums.LimitType;
import org.almir.wallets.exception.CardAccessDeniedException;
import org.almir.wallets.exception.LimitAlreadyExistsException;
import org.almir.wallets.exception.LimitNotFoundException;
import org.almir.wallets.repository.LimitRepository;
import org.almir.wallets.service.LimitService;
import org.almir.wallets.utils.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LimitServiceImpl implements LimitService {
    private final LimitRepository limitRepository;
    private final SecurityUtils securityUtils;

    @Override
    @Transactional
    public Limit createLimit(long cardId, String limitType, double amount) {
        validateAmount(amount);

        Card card = securityUtils.getCardById(cardId);

        LimitType parsedLimitType;
        try {
            parsedLimitType = LimitType.valueOf(limitType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid limit type: " + limitType);
        }

        if (limitRepository.findByCardId(cardId).stream()
                .anyMatch(limit -> limit.getType() == parsedLimitType)) {
            throw new LimitAlreadyExistsException("Limit of type " + limitType + " already exists for card: " + cardId);
        }

        Limit limit = Limit.builder()
                .card(card)
                .type(parsedLimitType)
                .amount(amount)
                .build();

        return limitRepository.save(limit);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Limit> getLimits(long cardId) {
        User currentUser = securityUtils.getCurrentUser();
        Card card = securityUtils.getCardById(cardId);
        long userId = currentUser.getId();

        if (currentUser.getRole().name().equals("USER") && card.getUser().getId() != userId) {
            throw new CardAccessDeniedException("User does not own card: " + card.getId());
        }
        return limitRepository.findByCardId(cardId);
    }

    @Override
    @Transactional
    public Limit updateLimit(long limitId, double amount) {
        validateAmount(amount);

        Limit limit = limitRepository.findById(limitId)
                .orElseThrow(() -> new LimitNotFoundException("Limit not found: " + limitId));

        limit.setAmount(amount);

        return limitRepository.save(limit);
    }

    @Override
    @Transactional
    public void deleteLimit(long limitId) {
        Limit limit = limitRepository.findById(limitId)
                .orElseThrow(() -> new LimitNotFoundException("Limit not found: " + limitId));

        limitRepository.delete(limit);
    }


    private void validateAmount(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Limit amount must be positive");
        }
    }
}
