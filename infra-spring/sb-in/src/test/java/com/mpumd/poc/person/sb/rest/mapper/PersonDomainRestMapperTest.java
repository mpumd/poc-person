package com.mpumd.poc.person.sb.rest.mapper;

import com.mpumd.poc.person.context.aggregat.Gender;
import com.mpumd.poc.person.context.aggregat.Nationality;
import com.mpumd.poc.person.context.command.PersonRegistrationCommand;
import com.mpumd.poc.person.sb.rest.RandomRecordFiller;
import com.mpumd.poc.person.sb.rest.resource.PersonRegisterResource;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class PersonDomainRestMapperTest {

    @Test
    void mapAllFilledValuesFromRestResourceToAggregat() throws Exception {
        var resource = RandomRecordFiller.fillRandomly(PersonRegisterResource.class, Map.of(
                "gender", Gender.ALIEN.toString(),
                "nationality", Nationality.TT.toString()
                ));
        assertThat(resource).hasNoNullFieldsOrProperties();

        PersonRegistrationCommand command = PersonDomainRestMapper.toDomain(resource);

        assertThat(command)
                .usingRecursiveComparison()
                .withEnumStringComparison()
                .isEqualTo(resource);
    }

    @Test
    void mapAllNullValuesFromRestResourceToAggregat() {
        PersonRegisterResource resource = mock();
        assertThat(resource).hasAllNullFieldsOrProperties();

        PersonRegistrationCommand command = PersonDomainRestMapper.toDomain(resource);

        assertThat(command)
                .isNotNull()
                .hasAllNullFieldsOrProperties();
    }
}