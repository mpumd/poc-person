package com.mpumd.poc.person.sb.jpa.mapper;

import com.mpumd.poc.person.context.aggregat.Person;
import com.mpumd.poc.person.sb.jpa.entity.PersonEntity;
import lombok.experimental.UtilityClass;

import java.util.Optional;

@UtilityClass
public class DomainJPAMapper {

    public PersonEntity toJpa(Person person) {
        var builder = PersonEntity.builder()
                .id(person.id())
                .firstName(person.firstName())
                .lastName(person.lastName())
                .birthDate(person.birthDate())
                .birthPlace(person.birthPlace());

        Optional.ofNullable(person.gender())
                .map(Enum::toString)
                .ifPresent(builder::gender);

        Optional.ofNullable(person.nationality())
                .map(Enum::toString)
                .ifPresent(builder::nationality);

        return builder.build();
    }
}
