package com.mpumd.poc.person.sb.jpa.converter;

import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ISO8601ZonedDateTimeConverterTest {

    ISO8601ZonedDateTimeConverter converter = new ISO8601ZonedDateTimeConverter();

    // without timezone location
    static String exampleDateISO8601 = "2024-10-24T12:10:24.840701948+02:00";

    @Test
    void convertFromZonedDateTimeToString() {
        var result = converter.convertToDatabaseColumn(ZonedDateTime.parse(exampleDateISO8601));
        assertEquals(exampleDateISO8601, result);
    }

    @Test
    void convertFromNullZonedDateTimeToNullString() {
        var result = converter.convertToDatabaseColumn(null);
        assertNull(result);
    }

    @Test
    void convertFromStringToZonedDateTime() {
        var result = converter.convertToEntityAttribute(exampleDateISO8601);
        assertEquals(exampleDateISO8601, result.toString());
    }

    @Test
    void convertFromNullStringToNullZonedDateTime() {
        var result = converter.convertToEntityAttribute(exampleDateISO8601);
        assertEquals(exampleDateISO8601, result.toString());
    }
}