package com.mpumd.poc.person.sb.jpa.mapper;

import com.mpumd.poc.person.context.aggregat.Gender;
import com.mpumd.poc.person.context.aggregat.Person;
import com.mpumd.poc.person.context.query.PersonSearchQuery;
import com.mpumd.poc.person.sb.jpa.entity.GenderHistoryEntity;
import com.mpumd.poc.person.sb.jpa.entity.PersonEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
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

        ofNullable(person.genders())
                .map(PersonDomainJPAMapper::toJpa)
                .ifPresent(builder::genderHistory);

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
                .map(gd -> List.of(new GenderHistoryEntity(gd, query.birthDate().toLocalDateTime())))
                .ifPresent(builder::genderHistory);

        return builder.build();
    }

    public static List<GenderHistoryEntity> toJpa(Map<LocalDateTime, Gender> genders) {
        if (genders == null || genders.isEmpty()) return null;

        return genders.entrySet()
                .stream()
                .map(e -> new GenderHistoryEntity(null, null, e.getValue(), e.getKey()))
                .toList();
    }

}
