package org.almir.wallets.mapper;

import org.almir.wallets.dto.TransactionResponseDTO;
import org.almir.wallets.entity.Card;
import org.almir.wallets.entity.Transaction;
import org.almir.wallets.enums.TransactionType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionMapperTest {
    private final TransactionMapper mapper = TransactionMapper.INSTANCE;

    @Test
    void toResponseDto_success() {
        Card sourceCard = Card.builder().id(1L).build();
        Card targetCard = Card.builder().id(2L).build();

        Transaction transaction = Transaction.builder()
                .id(100L)
                .amount(250.0)
                .type(TransactionType.TRANSFER)
                .sourceCard(sourceCard)
                .targetCard(targetCard)
                .build();

        TransactionResponseDTO dto = mapper.toResponseDto(transaction);

        assertNotNull(dto);
        assertEquals(transaction.getId(), dto.id());
        assertEquals(transaction.getAmount(), dto.amount());
        assertEquals(transaction.getType(), dto.transactionType());
        assertEquals(sourceCard.getId(), dto.sourceCardId());
        assertEquals(targetCard.getId(), dto.targetCardId());
    }
}
