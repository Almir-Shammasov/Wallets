package org.almir.wallets.mapper;

import org.almir.wallets.dto.CardRequestDTO;
import org.almir.wallets.dto.CardResponseDTO;
import org.almir.wallets.entity.Card;
import org.almir.wallets.entity.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.*;

public class CardMapperTest {
    private final CardMapper mapper = CardMapper.INSTANCE;

    @Test
    void toResponseDto_success() {
        User user = User.builder()
                .id(1L)
                .name("Alice")
                .build();

        Card card = Card.builder()
                .id(10L)
                .cardNumber("1234567890123456")
                .maskedNumber("**** **** **** 3456")
                .balance(500.0)
                .status(org.almir.wallets.enums.CardStatus.ACTIVE)
                .expiryDate(YearMonth.from(LocalDate.of(2026, 12, 1)))
                .user(user)
                .build();

        CardResponseDTO dto = mapper.toResponseDto(card);

        assertNotNull(dto);
        assertEquals("Alice", dto.ownerName());
        assertEquals(card.getMaskedNumber(), dto.maskedNumber());
        assertEquals(card.getBalance(), dto.balance());
        assertEquals(card.getStatus(), dto.status());
        assertEquals(card.getExpiryDate(), dto.expiryDate());
    }

    @Test
    void toEntity_success() {
        CardRequestDTO dto = new CardRequestDTO(
                "1111222233334444",
                YearMonth.of(2027, 5),
                1000.0,
                1L
        );

        Card entity = mapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals(dto.cardNumber(), entity.getCardNumber());
        assertEquals(dto.initialBalance(), entity.getBalance());
        assertEquals(dto.expiryDate(), entity.getExpiryDate());

        assertNull(entity.getUser());
        assertNull(entity.getLimits());
        assertNull(entity.getMaskedNumber());
        assertNull(entity.getStatus());
    }
}
