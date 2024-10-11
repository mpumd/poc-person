package com.mpumd.poc.person.sb.jpa.mapper;

import com.mpumd.poc.person.context.aggregat.Gender;
import com.mpumd.poc.person.context.aggregat.Person;
import com.mpumd.poc.person.context.query.PersonSearchQuery;
import com.mpumd.poc.person.sb.jpa.entity.PersonEntity;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class DomainJPAMapperTest {

    EasyRandom easyRandom = new EasyRandom();

    @Test
    void mapAllFilledValuesFromAggregatToJPAEntities() {
        var aggregatRoot = easyRandom.nextObject(Person.class);
        assertThat(aggregatRoot).hasNoNullFieldsOrProperties();

        PersonEntity entity = DomainJPAMapper.toJpa(aggregatRoot);

        assertThat(entity)
                .isNotNull()
                .usingRecursiveComparison()
                .withEnumStringComparison()
                .isEqualTo(aggregatRoot);
    }

    @Test
    void mapAllNullValuesFromAggregatToJPAEntities() {
        Person aggregatRoot = mock();
        assertThat(aggregatRoot).hasAllNullFieldsOrProperties();

        PersonEntity entity = DomainJPAMapper.toJpa(aggregatRoot);

        assertThat(entity)
                .isNotNull()
                .hasAllNullFieldsOrProperties();
    }

    @Test
    void mapAllFilledValuesFromQueryToJPAEntities() {
        var query = PersonSearchQuery.builder()
                .firstName("mpu")
                .lastName("md")
                .gender(Gender.MALE)
                .birthDate(ZonedDateTime.parse("2003-10-03T15:30:00+01:00"))
                .birthPlace("anywhere")
                .build();

        assertThat(query).hasNoNullFieldsOrProperties();

        PersonEntity entity = DomainJPAMapper.toJpa(query);

        assertThat(entity)
                .usingRecursiveComparison()
                .ignoringFields("nationality", "id")
                .withEnumStringComparison()
                .isEqualTo(query);
    }
}