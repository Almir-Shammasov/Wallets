package org.almir.wallets.repository;

import org.almir.wallets.entity.Limit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LimitRepository extends JpaRepository<Limit, Long> {
    List<Limit> findByCardId(Long cardId);
}
