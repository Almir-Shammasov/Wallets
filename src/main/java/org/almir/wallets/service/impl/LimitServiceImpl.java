package org.almir.wallets.service.impl;

import lombok.RequiredArgsConstructor;
import org.almir.wallets.entity.Card;
import org.almir.wallets.entity.Limit;
import org.almir.wallets.enums.LimitType;
import org.almir.wallets.exception.CardNotFoundException;
import org.almir.wallets.exception.LimitAlreadyExistsException;
import org.almir.wallets.exception.LimitNotFoundException;
import org.almir.wallets.repository.CardRepository;
import org.almir.wallets.repository.LimitRepository;
import org.almir.wallets.repository.UserRepository;
import org.almir.wallets.service.LimitService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LimitServiceImpl implements LimitService {
    private final LimitRepository limitRepository;
    private final CardRepository cardRepository;
    @Override
    public Limit createLimit(Long cardId, String limitType, double amount) {
        validateAmount(amount);

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card not found: " + cardId));

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
    public Limit updateLimit(Long limitId, double amount) {
        validateAmount(amount);

        Limit limit = limitRepository.findById(limitId)
                .orElseThrow(() -> new LimitNotFoundException("Limit not found: " + limitId));

        limit.setAmount(amount);

        return limitRepository.save(limit);
    }

    @Override
    public void deleteLimit(Long limitId) {
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
