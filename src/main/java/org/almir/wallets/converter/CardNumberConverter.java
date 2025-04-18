package org.almir.wallets.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.almir.wallets.exception.CardNumberConvertedException;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Converter
public class CardNumberConverter implements AttributeConverter<String, String> {
    @Override
    public String convertToDatabaseColumn(String cardNumber) {
        if(cardNumber == null) {
            throw new CardNumberConvertedException("Card number cannot be null");
        }
        return Base64.getEncoder().encodeToString(cardNumber.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String convertToEntityAttribute(String encryptedCardNumber) {
        if(encryptedCardNumber == null) {
            throw new CardNumberConvertedException("Encrypted card number cannot be null");
        }
        return new String(Base64.getDecoder().decode(encryptedCardNumber));
    }
}
