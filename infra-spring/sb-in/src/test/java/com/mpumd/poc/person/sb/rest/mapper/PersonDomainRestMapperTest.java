package com.mpumd.poc.person.sb.rest.mapper;

import com.mpumd.poc.person.context.aggregat.Gender;
import com.mpumd.poc.person.context.aggregat.Nationality;
import com.mpumd.poc.person.application.command.GenderChangeCommand;
import com.mpumd.poc.person.application.command.PersonRegistrationCommand;
import com.mpumd.poc.person.sb.rest.resource.GenderChangeResource;
import com.mpumd.poc.person.sb.rest.resource.RegisterPersonResource;
import org.instancio.Instancio;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

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

        GenderChangeCommand command = PersonDomainRestMapper.toDomain(resource);

        assertThat(command)
                .usingRecursiveComparison()
                .withEqualsForFields((Enum e, String s) -> e.name().equalsIgnoreCase(s), "gender")
                .isEqualTo(resource);
    }

    @Test
    @Disabled // TODO command move mandatory field
    void mapAllNullValuesFromChangeSexCommandToAggregat() {
        // given
        var resource = new GenderChangeResource(null, null);
        try (var commandConstructor = mockConstruction(GenderChangeCommand.class)) {

            // when
            PersonDomainRestMapper.toDomain(resource);

            // then
            assertThat(commandConstructor.constructed())
                    .hasSize(1)
                    .first()
                    .hasAllNullFieldsOrProperties();
        }
    }
}