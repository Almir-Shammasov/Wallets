package org.almir.wallets.repository;

import org.almir.wallets.entity.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {
    Optional<Card> findByCardNumber(String cardNumber);
    Page<Card> findByUserId(long userId, Pageable pageable);
    List<Card> findByUserId(long userId);

    boolean existsByCardNumber(String cardNumber);
}
