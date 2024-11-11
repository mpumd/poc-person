package com.mpumd.poc.person.sb.rest.mapper;

import com.mpumd.poc.person.context.aggregat.Gender;
import com.mpumd.poc.person.context.aggregat.Nationality;
import com.mpumd.poc.person.context.command.GenderChangeCommand;
import com.mpumd.poc.person.context.command.PersonRegistrationCommand;
import com.mpumd.poc.person.sb.rest.resource.GenderChangeResource;
import com.mpumd.poc.person.sb.rest.resource.PersonRegisterResource;
import lombok.experimental.UtilityClass;

import java.util.Optional;
import java.util.UUID;

@UtilityClass
public class PersonDomainRestMapper {

    public PersonRegistrationCommand toDomain(PersonRegisterResource resource) {
        return PersonRegistrationCommand.builder()
                .firstName(resource.firstName())
                .lastName(resource.lastName())
                .birthDate(resource.birthDate())
                .birthPlace(resource.birthPlace())
                .gender(Optional.ofNullable(resource.gender()).map(Gender::valueOfName).orElse(null))
                .nationality(Optional.ofNullable(resource.nationality()).map(Nationality::valueOf).orElse(null))
                .build();
    }

    public static GenderChangeCommand toDomain(UUID uuid, GenderChangeResource resource) {
        return new GenderChangeCommand(
                uuid,
                Optional.ofNullable(resource.gender()).map(Gender::valueOfName).orElse(null),
                resource.changeDate()
        );
    }
}
