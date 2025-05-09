package org.almir.wallets.entity;

import jakarta.persistence.*;
import lombok.*;
import org.almir.wallets.converter.CardNumberConverter;
import org.almir.wallets.converter.YearMonthDateConverter;
import org.almir.wallets.enums.CardStatus;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "card_number", nullable = false, unique = true)
    @Convert(converter = CardNumberConverter.class)
    private String cardNumber;

    @Column(name = "masked_number", nullable = false)
    private String maskedNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "expiry_date", nullable = false)
    @Convert(converter = YearMonthDateConverter.class)
    private YearMonth expiryDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CardStatus status;

    @Column(name = "balance", nullable = false)
    private double balance;

    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Limit> limits = new ArrayList<>();

    @Column(name = "block_requested", nullable = false)
    private boolean blockRequested;
}
