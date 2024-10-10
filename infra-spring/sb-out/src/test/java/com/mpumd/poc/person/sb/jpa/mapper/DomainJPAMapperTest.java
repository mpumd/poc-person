package com.mpumd.poc.person.sb.jpa.mapper;

import com.mpumd.poc.person.context.aggregat.Person;
import com.mpumd.poc.person.sb.jpa.entity.PersonEntity;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class DomainJPAMapperTest {

    EasyRandom easyRandom = new EasyRandom();

    @Test
    void mapAllValuesFromAggregatToJPAEntities() {
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
    void mapNoNullValuesFromAggregatToJPAEntities() {
        Person aggregatRoot = mock();
        assertThat(aggregatRoot).hasAllNullFieldsOrProperties();

        PersonEntity entity = DomainJPAMapper.toJpa(aggregatRoot);

        assertThat(entity)
                .isNotNull()
                .hasAllNullFieldsOrProperties();
    }
}