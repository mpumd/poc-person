package com.mpumd.poc.person.context.query;

import com.mpumd.poc.person.context.aggregat.Gender;
import lombok.Builder;

import java.time.ZonedDateTime;

@Builder
public record PersonSearchQuery(
        String firstName,
        String lastName,
        Gender gender,
        ZonedDateTime birthDate,
        String birthPlace
) {
}
