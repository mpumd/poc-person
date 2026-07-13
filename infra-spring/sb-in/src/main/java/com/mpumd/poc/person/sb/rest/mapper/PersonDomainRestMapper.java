package com.mpumd.poc.person.sb.rest.mapper;

import com.mpumd.poc.person.application.command.GenderChangeCommand;
import com.mpumd.poc.person.application.command.PersonRegistrationCommand;
import com.mpumd.poc.person.context.aggregat.Gender;
import com.mpumd.poc.person.context.aggregat.Nationality;
import com.mpumd.poc.person.sb.rest.resource.GenderChangeResource;
import com.mpumd.poc.person.sb.rest.resource.RegisterPersonResource;

import java.util.UUID;

import static java.util.Optional.ofNullable;

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

    public static GenderChangeCommand toDomain(UUID uuid, GenderChangeResource resource) {
        return new GenderChangeCommand(
                uuid,
                Gender.valueOfName(resource.gender()),
                resource.changeDate()
        );
    }
}
