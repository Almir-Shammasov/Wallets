package org.almir.wallets.converter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.*;

public class YearMonthDateConverterTest {
    private YearMonthDateConverter converter;

    @BeforeEach
    void setUp() {
        converter = new YearMonthDateConverter();
    }

    @Test
    void convertToDatabaseColumn_success() {
        YearMonth yearMonth = YearMonth.of(2025, 4);
        LocalDate expectedDate = LocalDate.of(2025, 4, 1);

        LocalDate result = converter.convertToDatabaseColumn(yearMonth);

        assertNotNull(result);
        assertEquals(expectedDate, result);
    }

    @Test
    void convertToDatabaseColumn_null_throwsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> converter.convertToDatabaseColumn(null)
        );

        assertEquals("YearMonth cannot be null", exception.getMessage());
    }

    @Test
    void convertToEntityAttribute_success() {
        LocalDate date = LocalDate.of(2023, 12, 15);
        YearMonth expected = YearMonth.of(2023, 12);

        YearMonth result = converter.convertToEntityAttribute(date);

        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void convertToEntityAttribute_null_throwsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> converter.convertToEntityAttribute(null)
        );

        assertEquals("LocalDate cannot be null", exception.getMessage());
    }
}
