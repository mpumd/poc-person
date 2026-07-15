package com.mpumd.poc.person.sb.rest.mapper;

import com.mpumd.poc.person.context.aggregat.Gender;
import com.mpumd.poc.person.context.aggregat.Nationality;
import com.mpumd.poc.person.context.command.GenderChangeCommand;
import com.mpumd.poc.person.context.command.PersonRegistrationCommand;
import com.mpumd.poc.person.sb.rest.resource.GenderChangeResource;
import com.mpumd.poc.person.sb.rest.resource.RegisterPersonResource;
import org.instancio.Instancio;
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
                .generate(field("gender"), gen -> gen.enumOf(Gender.class).as(g -> g.name().toLowerCase()))
                .generate(field("nationality"), gen -> gen.enumOf(Nationality.class).asString())
                .create();
        assertThat(resource).hasNoNullFieldsOrProperties();

        PersonRegistrationCommand command = PersonDomainRestMapper.toDomain(resource);

        assertThat(command)
                .usingRecursiveComparison()
                .withEqualsForFields(
                        (Enum<?> e, String s) -> e.name().equalsIgnoreCase(s),
                        "gender", "nationality")
                .isEqualTo(resource);
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