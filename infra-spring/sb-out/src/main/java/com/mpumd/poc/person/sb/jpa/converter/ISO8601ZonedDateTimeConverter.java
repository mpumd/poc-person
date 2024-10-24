package com.mpumd.poc.person.sb.jpa.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * The ISO8601 date format is like {@code 2024-06-19T13:15:26.990898895+02:00[Europe/Zurich]}.
 * I want to keep this offset of timezone {@code +02:00} with the date in the DB. Hibernate by default convert from {@code ZonedDateTime} to
 * {@code Instant} and finally to {@code Timestamp} so loose the offset.
 * Two possible solutions :
 * - this following converter,
 * - just put a String type for a date and convert manually in the mapper.
 */
@Converter(autoApply = true)
public class ISO8601ZonedDateTimeConverter implements AttributeConverter<ZonedDateTime, String> {

    private static final DateTimeFormatter ISO8601_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    @Override
    public String convertToDatabaseColumn(ZonedDateTime zdt) {
        if (zdt == null) {
            return null;
        }
        return zdt.format(ISO8601_FORMATTER);
    }

    @Override
    public ZonedDateTime convertToEntityAttribute(String value) {
        if (value == null) {
            return null;
        }
        return ZonedDateTime.parse(value, ISO8601_FORMATTER);
    }
}