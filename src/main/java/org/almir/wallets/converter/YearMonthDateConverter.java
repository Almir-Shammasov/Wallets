package org.almir.wallets.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.LocalDate;
import java.time.YearMonth;

@Converter
public class YearMonthDateConverter implements AttributeConverter<YearMonth, LocalDate> {

    @Override
    public LocalDate convertToDatabaseColumn(YearMonth yearMonth) {
        if (yearMonth == null) {
            throw new IllegalArgumentException("YearMonth cannot be null");
        }
        return yearMonth.atDay(1);
    }

    @Override
    public YearMonth convertToEntityAttribute(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("LocalDate cannot be null");
        }
        return YearMonth.from(date);
    }
}
