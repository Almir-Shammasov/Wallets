package org.almir.wallets.converter;

import org.almir.wallets.exception.CardNumberConvertedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

public class CardNumberConverterTest {
    private CardNumberConverter converter;

    @BeforeEach
    void setUp() {
        converter = new CardNumberConverter();
    }

    @Test
    void convertToDatabaseColumn_success() {
        String cardNumber = "1234567890123456";
        String encoded = converter.convertToDatabaseColumn(cardNumber);

        assertNotNull(encoded);
        assertEquals(Base64.getEncoder().encodeToString(cardNumber.getBytes()), encoded);
    }

    @Test
    void convertToDatabaseColumn_null_throwsException() {
        CardNumberConvertedException exception = assertThrows(
                CardNumberConvertedException.class,
                () -> converter.convertToDatabaseColumn(null)
        );

        assertEquals("Card number cannot be null", exception.getMessage());
    }

    @Test
    void convertToEntityAttribute_success() {
        String cardNumber = "1234567890123456";
        String encoded = Base64.getEncoder().encodeToString(cardNumber.getBytes());

        String decoded = converter.convertToEntityAttribute(encoded);

        assertNotNull(decoded);
        assertEquals(cardNumber, decoded);
    }

    @Test
    void convertToEntityAttribute_null_throwsException() {
        CardNumberConvertedException exception = assertThrows(
                CardNumberConvertedException.class,
                () -> converter.convertToEntityAttribute(null)
        );

        assertEquals("Encrypted card number cannot be null", exception.getMessage());
    }
}
