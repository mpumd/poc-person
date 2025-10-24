package com.mpumd.poc.person.sb.rest.mapper;

import com.mpumd.poc.person.context.aggregat.Gender;
import com.mpumd.poc.person.context.aggregat.Nationality;
import com.mpumd.poc.person.context.command.GenderChangeCommand;
import com.mpumd.poc.person.context.command.PersonRegistrationCommand;
import com.mpumd.poc.person.sb.rest.resource.GenderChangeResource;
import com.mpumd.poc.person.sb.rest.resource.RegisterPersonResource;
import org.instancio.Instancio;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;

class PersonDomainRestMapperTest {

    @Test
    void mapAllFilledValuesFromPersonRegisterResourceToCmd() {
        var resource = Instancio.of(RegisterPersonResource.class)
                .set(field("gender"), Gender.ALIEN.toString().toLowerCase())
                .set(field("nationality"), Nationality.TT.toString())
                .create();
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
        RegisterPersonResource resource = mock();
        assertThat(resource).hasAllNullFieldsOrProperties();

        try (var prcStaticMock = mockConstruction(PersonRegistrationCommand.class)) {
            PersonRegistrationCommand command = PersonDomainRestMapper.toDomain(resource);

            assertThat(command)
                    .isNotNull()
                    .hasAllNullFieldsOrProperties();

            assertThat(prcStaticMock.constructed())
                    .hasSize(1)
                    .first()
                    .isEqualTo(command);
        }
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