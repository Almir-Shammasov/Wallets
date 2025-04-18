package org.almir.wallets.repository;

import org.almir.wallets.entity.Limit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LimitRepository extends JpaRepository<Limit, Long> {
}
