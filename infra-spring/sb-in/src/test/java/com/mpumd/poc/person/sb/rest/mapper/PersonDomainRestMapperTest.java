package com.mpumd.poc.person.sb.rest.mapper;

import com.mpumd.poc.person.context.aggregat.Gender;
import com.mpumd.poc.person.context.aggregat.Nationality;
import com.mpumd.poc.person.context.command.GenderChangeCommand;
import com.mpumd.poc.person.context.command.PersonRegistrationCommand;
import com.mpumd.poc.person.sb.rest.RandomRecordFiller;
import com.mpumd.poc.person.sb.rest.resource.GenderChangeResource;
import com.mpumd.poc.person.sb.rest.resource.PersonRegisterResource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;

class PersonDomainRestMapperTest {

    @Test
    void mapAllFilledValuesFromPersonRegisterResourceToCmd() throws Exception {
        var resource = RandomRecordFiller.fillRandomly(PersonRegisterResource.class, Map.of(
                "gender", Gender.ALIEN.toString().toLowerCase(),
                "nationality", Nationality.TT.toString()
        ));
        assertThat(resource).hasNoNullFieldsOrProperties();

        PersonRegistrationCommand command = PersonDomainRestMapper.toDomain(resource);

        assertThat(command)
                .usingRecursiveComparison()
                .ignoringFields("gender", "nationality")
                .isEqualTo(resource);

        assertThat(resource.gender()).isEqualToIgnoringCase(command.gender().name());
        assertThat(resource.nationality()).isEqualToIgnoringCase(command.nationality().name());
    }

    @Test
    void mapAllNullValuesFromPersonRegisterResourceToAggregat() {
        PersonRegisterResource resource = mock();
        assertThat(resource).hasAllNullFieldsOrProperties();

        PersonRegistrationCommand command = PersonDomainRestMapper.toDomain(resource);

        assertThat(command)
                .isNotNull()
                .hasAllNullFieldsOrProperties();
    }

    @Test
    void mapAllFilledValuesFromChangeSexCommandToAggregat() {
        var resource = new GenderChangeResource("FeMale", LocalDateTime.now());
        UUID uuid = UUID.randomUUID();

        GenderChangeCommand command = PersonDomainRestMapper.toDomain(uuid, resource);

        assertThat(command)
                .usingRecursiveComparison()
                .withEqualsForFields((Enum e, String s) -> e.name().equalsIgnoreCase(s), "gender")
                .ignoringFields("id")
                .isEqualTo(resource);

        Assertions.assertEquals(uuid, command.id());
    }

    @Test
    void mapAllNullValuesFromChangeSexCommandToAggregat() {
        // given
        var resource = new GenderChangeResource(null, null);
        try (var commandConstructor = mockConstruction(GenderChangeCommand.class)) {

            // when
            PersonDomainRestMapper.toDomain(null, resource);

            // then
            assertThat(commandConstructor.constructed())
                    .hasSize(1)
                    .first()
                    .hasAllNullFieldsOrProperties();
        }
    }
}