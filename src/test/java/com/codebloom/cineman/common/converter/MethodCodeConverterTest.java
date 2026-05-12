package com.codebloom.cineman.common.converter;

import com.codebloom.cineman.common.enums.Method;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MethodCodeConverterTest {

    private final MethodCodeConverter converter = new MethodCodeConverter();

    @Test
    void convertToDatabaseColumn_shouldKeepLegacyDeleteCodeStable() {
        assertEquals(0, converter.convertToDatabaseColumn(Method.GET));
        assertEquals(1, converter.convertToDatabaseColumn(Method.POST));
        assertEquals(2, converter.convertToDatabaseColumn(Method.PUT));
        assertEquals(3, converter.convertToDatabaseColumn(Method.PATCH));
        assertEquals(4, converter.convertToDatabaseColumn(Method.DELETE));
        assertEquals(5, converter.convertToDatabaseColumn(Method.OPTIONS));
        assertNull(converter.convertToDatabaseColumn(null));
    }

    @Test
    void convertToEntityAttribute_shouldRestoreMethodFromStableCode() {
        assertEquals(Method.DELETE, converter.convertToEntityAttribute(4));
        assertEquals(Method.OPTIONS, converter.convertToEntityAttribute(5));
        assertNull(converter.convertToEntityAttribute(null));
    }

    @Test
    void convertToEntityAttribute_shouldRejectUnknownCode() {
        assertThrows(IllegalArgumentException.class, () -> converter.convertToEntityAttribute(99));
    }
}
