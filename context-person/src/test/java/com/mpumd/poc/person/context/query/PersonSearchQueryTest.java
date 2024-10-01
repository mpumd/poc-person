package com.mpumd.poc.person.context.query;

import com.mpumd.poc.person.context.aggregat.Gender;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PersonSearchQueryTest {

    @Test
    void shouldFullFillQuery() {
        var query = PersonSearchQuery.builder()
                .firstName("mpu")
                .lastName("md")
                .gender(Gender.FEMALE)
                .birthDate(ZonedDateTime.parse("2023-10-01T15:30:00+01:00"))
                .birthPlace("San Francisco")
                .build();

        assertEquals(query.firstName(), "mpu");
        assertEquals(query.lastName(), "md");
        assertEquals(query.gender(), Gender.FEMALE);
        assertEquals(query.birthDate(), ZonedDateTime.parse("2023-10-01T15:30:00+01:00"));
        assertEquals(query.birthPlace(), "San Francisco");
    }
}