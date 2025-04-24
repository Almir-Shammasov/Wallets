package org.almir.wallets.service;

import org.almir.wallets.entity.Limit;

import java.util.List;

public interface LimitService {
    Limit createLimit(long cardId, String limitType, double amount);
    List<Limit> getLimits(long cardId);
    Limit updateLimit(long limitId, double amount);
    void deleteLimit(long limitId);
}
