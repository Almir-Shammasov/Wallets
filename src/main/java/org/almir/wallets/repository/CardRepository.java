package org.almir.wallets.repository;

import org.almir.wallets.entity.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {
    Optional<Card> findByCardNumber(String cardNumber);
    Page<Card> findByUserId(long userId, Pageable pageable);
    Page<Card> findByBlockRequested(Boolean blockRequested, Pageable pageable);
    Page<Card> findByUserIdAndBlockRequested(Long userId, Boolean blockRequested, Pageable pageable);
}
