package com.mpumd.poc.person.context.aggregat;

import org.jilt.Builder;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Memento of the {@link Person} state, the single contract crossing the domain / persistence border.
 * Inert data: building one applies no business rule, only {@link PersonRehydrator} can turn it back
 * into a living aggregate.
 */
@Builder(packageName = "com.mpumd.poc.person.context.utils.builder")
public record PersonSnapshot(
        UUID id,
        String firstName,
        String lastName,
        ZonedDateTime birthDate,
        String birthPlace,
        Map<LocalDateTime, Gender> genderChangeHistory,
        Nationality nationality) {
}
