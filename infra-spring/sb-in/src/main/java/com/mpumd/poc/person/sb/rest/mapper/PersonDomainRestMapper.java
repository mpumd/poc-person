package com.mpumd.poc.person.sb.rest.mapper;

import com.mpumd.poc.person.context.aggregat.Gender;
import com.mpumd.poc.person.context.aggregat.Nationality;
import com.mpumd.poc.person.context.command.GenderChangeCommand;
import com.mpumd.poc.person.context.command.PersonRegistrationCommand;
import com.mpumd.poc.person.sb.rest.resource.GenderChangeResource;
import com.mpumd.poc.person.sb.rest.resource.RegisterPersonResource;
import org.jspecify.annotations.NullUnmarked;

import static java.util.Optional.ofNullable;

@NullUnmarked // the mapper is not under null control because it doesn't know if the value is mandatory or not.
public final class PersonDomainRestMapper {
    private PersonDomainRestMapper() {
    }

    public static PersonRegistrationCommand toDomain(RegisterPersonResource resource) {
        return PersonRegistrationCommand.builder()
                .firstName(resource.firstName())
                .lastName(resource.lastName())
                .birthDate(resource.birthDate())
                .birthPlace(resource.birthPlace())
                .gender(ofNullable(resource.gender()).map(Gender::valueOfName).orElse(null))
                .nationality(ofNullable(resource.nationality()).map(Nationality::valueOf).orElse(null))
                .build();
    }

    public static GenderChangeCommand toDomain(GenderChangeResource resource) {
        return new GenderChangeCommand(
                ofNullable(resource.gender()).map(Gender::valueOfName).orElse(null),
                resource.changeDate()
        );
    }
}
