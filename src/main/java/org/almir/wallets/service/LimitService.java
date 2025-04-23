package org.almir.wallets.service;

import org.almir.wallets.entity.Limit;

public interface LimitService {
    Limit createLimit(Long cardId, String limitType, double amount);
    Limit updateLimit(Long limitId, double amount);
    void deleteLimit(Long limitId);
}
