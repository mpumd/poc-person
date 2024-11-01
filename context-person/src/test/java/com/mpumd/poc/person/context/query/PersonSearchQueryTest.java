package com.mpumd.poc.person.context.query;

import com.mpumd.poc.person.context.aggregat.Gender;
import com.mpumd.poc.person.context.aggregat.Person;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PersonSearchQueryTest {

    EasyRandom easyRandom = new EasyRandom();

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

    @Test
    void constructWithPerson() {
        var person = easyRandom.nextObject(Person.class);
        assertThat(person).hasNoNullFieldsOrProperties();

        PersonSearchQuery query = new PersonSearchQuery(person);

        assertThat(query)
                .usingRecursiveComparison()
                .ignoringFields("gender")// ignore in query
                .isEqualTo(person);

        assertEquals(query.gender(), person.genderChangeHistory().lastEntry().getValue());
    }
}