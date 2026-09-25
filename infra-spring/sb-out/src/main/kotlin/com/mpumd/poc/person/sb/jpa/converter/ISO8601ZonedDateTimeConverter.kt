package com.mpumd.poc.person.sb.jpa.converter

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

private val ISO8601_FORMATTER: DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

/**
 * The ISO8601 date format is like `2024-06-19T13:15:26.990898895+02:00[Europe/Zurich]`.
 * I want to keep this offset of timezone `+02:00` with the date in the DB. Hibernate by default
 * convert from [ZonedDateTime] to [java.time.Instant] and finally to [java.sql.Timestamp] so loose
 * the offset.
 * Two possible solutions :
 * - this following converter,
 * - just put a String type for a date and convert manually in the mapper.
 */
@Converter(autoApply = true)
class ISO8601ZonedDateTimeConverter : AttributeConverter<ZonedDateTime, String> {

    override fun convertToDatabaseColumn(zdt: ZonedDateTime?): String? =
        zdt?.format(ISO8601_FORMATTER)

    override fun convertToEntityAttribute(value: String?): ZonedDateTime? =
        value?.let { ZonedDateTime.parse(it, ISO8601_FORMATTER) }
}
