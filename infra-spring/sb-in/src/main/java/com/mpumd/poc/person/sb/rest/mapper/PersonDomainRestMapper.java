package com.mpumd.poc.person.sb.rest.mapper;

import com.mpumd.poc.person.context.aggregat.Gender;
import com.mpumd.poc.person.context.aggregat.Nationality;
import com.mpumd.poc.person.context.command.PersonRegistrationCommand;
import com.mpumd.poc.person.sb.rest.resource.PersonRegisterResource;
import lombok.experimental.UtilityClass;

import java.util.Optional;

@UtilityClass
public class PersonDomainRestMapper {

    public PersonRegistrationCommand toDomain(PersonRegisterResource resource) {
        return PersonRegistrationCommand.builder()
                .firstName(resource.firstName())
                .lastName(resource.lastName())
                .birthDate(resource.birthDate())
                .birthPlace(resource.birthPlace())
                .gender(Optional.ofNullable(resource.gender()).map(Gender::valueOf).orElse(null))
                .nationality(Optional.ofNullable(resource.nationality()).map(Nationality::valueOf).orElse(null))
                .build();
    }
}
