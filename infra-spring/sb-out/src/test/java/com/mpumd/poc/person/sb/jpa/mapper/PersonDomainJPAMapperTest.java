package com.mpumd.poc.person.sb.jpa.mapper;

import com.mpumd.poc.person.context.aggregat.Gender;
import com.mpumd.poc.person.context.aggregat.Nationality;
import com.mpumd.poc.person.context.aggregat.Person;
import com.mpumd.poc.person.context.query.PersonSearchQuery;
import com.mpumd.poc.person.sb.jpa.entity.PersonEntity;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Map;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class PersonDomainJPAMapperTest {

    EasyRandom easyRandom = new EasyRandom();

    @Test
    void mapAllFilledValuesFromAggregatToJPAEntities() {
        var aggregatRoot = easyRandom.nextObject(Person.class);
        assertThat(aggregatRoot).hasNoNullFieldsOrProperties();

        PersonEntity entity = PersonDomainJPAMapper.toJpa(aggregatRoot);

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

        PersonEntity entity = PersonDomainJPAMapper.toJpa(aggregatRoot);

        assertThat(entity)
                .isNotNull()
                .hasAllNullFieldsOrProperties();
    }

    @Test
    void mapAllFilledValuesFromQueryToJPAEntities() {
        // given
        var query = PersonSearchQuery.builder()
                .firstName("mpu")
                .lastName("md")
                .gender(Gender.MALE)
                .birthDate(ZonedDateTime.parse("2003-10-03T15:30:00+01:00"))
                .birthPlace("anywhere")
                .build();
        assertThat(query).hasNoNullFieldsOrProperties();

        // when
        PersonEntity entity = PersonDomainJPAMapper.toJpa(query);

        // then entity
        assertThat(entity)
                .usingRecursiveComparison()
                .ignoringFields("nationality", "id", "genderChangeHistory")
                .withEnumStringComparison()
                .isEqualTo(query);

        // then entity.genderHistory
        assertThat((Map<LocalDateTime, Gender>) ReflectionTestUtils.getField(entity, "genderChangeHistory"))
                .hasSize(1)
                .containsExactly(entry(LocalDateTime.parse("2003-10-03T15:30:00"), Gender.MALE));
    }

    @Test
    void mapAllNullValuesFromQueryToJPAEntities() {
        // given
        PersonSearchQuery query = mock();
        assertThat(query).hasAllNullFieldsOrProperties();

        // when
        PersonEntity entity = PersonDomainJPAMapper.toJpa(query);

        // then entity
        assertThat(entity)
                .isNotNull()
                .hasAllNullFieldsOrProperties();

    }

    @Test
    void mapAllFilledValuesFromJPAEntitiesToAggregat() {
        var entity = easyRandom.nextObject(PersonEntity.class);
        assertThat(entity).hasNoNullFieldsOrProperties();
        ReflectionTestUtils.setField(entity, "nationality", Nationality.FR.toString());

        Person aggregateRoot = PersonDomainJPAMapper.toDomain(entity);

        assertThat(aggregateRoot)
                .isNotNull()
                .usingRecursiveComparison()
                .withEnumStringComparison()
                .ignoringFields("physicalAppearance")
                .isEqualTo(entity);
    }

    @Test
    void mapAllNullValuesFromJPAEntitiesToAggregat() {
        PersonEntity entity = mock();
        assertThat(entity).hasAllNullFieldsOrProperties();

        Person aggregateRoot = PersonDomainJPAMapper.toDomain(entity);

        assertThat(aggregateRoot)
                .isNotNull()
                .hasAllNullFieldsOrPropertiesExcept("genderChangeHistory");

        assertThat(aggregateRoot.genderChangeHistory()).isEmpty();
    }
}