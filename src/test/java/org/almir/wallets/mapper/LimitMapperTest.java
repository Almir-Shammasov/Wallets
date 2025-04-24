package org.almir.wallets.mapper;

import org.almir.wallets.dto.LimitRequestDTO;
import org.almir.wallets.dto.LimitResponseDTO;
import org.almir.wallets.entity.Card;
import org.almir.wallets.entity.Limit;
import org.almir.wallets.enums.LimitType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class LimitMapperTest {
    private final LimitMapper mapper = LimitMapper.INSTANCE;

    @Test
    void toResponseDto_success() {
        Card card = Card.builder().id(1L).build();
        Limit limit = Limit.builder()
                .id(10L)
                .amount(500.0)
                .type(LimitType.DAILY)
                .card(card)
                .build();

        LimitResponseDTO dto = mapper.toResponseDto(limit);

        assertNotNull(dto);
        assertEquals(limit.getId(), dto.id());
        assertEquals(limit.getAmount(), dto.amount());
        assertEquals(limit.getType(), dto.type());
        assertEquals(card.getId(), dto.cardId());
    }

    @Test
    void toEntity_success() {
        LimitRequestDTO dto = new LimitRequestDTO(1L, LimitType.DAILY, 1000.0);

        Limit entity = mapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals(dto.amount(), entity.getAmount());
        assertEquals(LimitType.valueOf("DAILY"), entity.getType());
    }
}
