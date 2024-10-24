package com.mpumd.poc.person.sb.jpa.mapper;

import com.mpumd.poc.person.context.aggregat.Person;
import com.mpumd.poc.person.context.query.PersonSearchQuery;
import com.mpumd.poc.person.sb.jpa.entity.PersonEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.UtilityClass;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PersonDomainJPAMapper {

    public static PersonEntity toJpa(Person person) {
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

    public static PersonEntity toJpa(PersonSearchQuery query) {
        var builder = PersonEntity.builder()
                .firstName(query.firstName())
                .lastName(query.lastName())
                .birthDate(query.birthDate())
                .birthPlace(query.birthPlace());

        Optional.ofNullable(query.gender())
                .map(Enum::toString)
                .ifPresent(builder::gender);

        return builder.build();
    }

}
