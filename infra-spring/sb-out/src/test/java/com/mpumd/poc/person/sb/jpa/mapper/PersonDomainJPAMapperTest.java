package com.mpumd.poc.person.sb.jpa.mapper;

import com.mpumd.poc.person.context.aggregat.Gender;
import com.mpumd.poc.person.context.aggregat.Person;
import com.mpumd.poc.person.context.query.PersonSearchQuery;
import com.mpumd.poc.person.sb.jpa.entity.GenderHistoryEntity;
import com.mpumd.poc.person.sb.jpa.entity.PersonEntity;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
                .ignoringFields("genderHistory", "genders")
                .isEqualTo(aggregatRoot);

        assertThat(collectFromListEntityGenderToMap(entity.genderHistory())).containsAllEntriesOf(aggregatRoot.genders());
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
                .ignoringFields("nationality", "id", "genderHistory")
                .withEnumStringComparison()
                .isEqualTo(query);

        // then entity.genderHistory
        assertThat((List<GenderHistoryEntity>) ReflectionTestUtils.getField(entity, "genderHistory"))
                .hasSize(1)
                .first()
                .extracting("gender", "changeDate")
                .containsExactly(Gender.MALE, LocalDateTime.parse("2003-10-03T15:30:00"));
    }

    @Test
    void mapAllFilledValuesForGenders() {
        var genders = Map.of(
                LocalDateTime.parse("2023-10-01T15:30"), Gender.FEMALE,
                LocalDateTime.parse("1984-01-01T08:30"), Gender.MALE
        );

        List<GenderHistoryEntity> history = PersonDomainJPAMapper.toJpa(genders);

        assertThat(collectFromListEntityGenderToMap(history))
                .containsAllEntriesOf(genders);

    }

    @ParameterizedTest
    @NullAndEmptySource
    void mapNullOrEmptyValuesForGenders(Map<LocalDateTime, Gender> gendersHistory) {
        List<GenderHistoryEntity> history = PersonDomainJPAMapper.toJpa(gendersHistory);
        assertThat(history).isNull();
    }

    private static Map<LocalDateTime, Gender> collectFromListEntityGenderToMap(List<GenderHistoryEntity> genderHistory) {
        return genderHistory.stream().collect(Collectors.toMap(
                GenderHistoryEntity::changeDate,
                GenderHistoryEntity::gender
        ));
    }
}