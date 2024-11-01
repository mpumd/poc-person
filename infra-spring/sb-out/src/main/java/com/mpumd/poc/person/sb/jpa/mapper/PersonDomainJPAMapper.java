package com.mpumd.poc.person.sb.jpa.mapper;

import com.mpumd.poc.person.context.aggregat.Person;
import com.mpumd.poc.person.context.query.PersonSearchQuery;
import com.mpumd.poc.person.sb.jpa.entity.PersonEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Map;

import static java.util.Optional.ofNullable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PersonDomainJPAMapper {

    public static PersonEntity toJpa(Person person) {
        var builder = PersonEntity.builder()
                .id(person.id())
                .firstName(person.firstName())
                .lastName(person.lastName())
                .birthDate(person.birthDate())
                .birthPlace(person.birthPlace());

        ofNullable(person.genderChangeHistory())
                .filter(map -> map.size() > 0)
                .ifPresent(builder::genderChangeHistory);

        ofNullable(person.nationality())
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

        ofNullable(query.gender())
                .map(gd -> Map.of(query.birthDate().toLocalDateTime(), gd))
                .ifPresent(builder::genderChangeHistory);

        return builder.build();
    }
}
