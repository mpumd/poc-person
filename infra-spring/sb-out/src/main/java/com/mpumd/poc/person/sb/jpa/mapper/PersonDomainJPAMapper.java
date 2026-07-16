package com.mpumd.poc.person.sb.jpa.mapper;

import com.mpumd.poc.person.context.aggregat.Nationality;
import com.mpumd.poc.person.context.aggregat.Person;
import com.mpumd.poc.person.context.aggregat.PersonRehydrator;
import com.mpumd.poc.person.context.utils.builder.PersonSnapshotBuilder;
import com.mpumd.poc.person.context.query.PersonSearchQuery;
import com.mpumd.poc.person.sb.jpa.entity.PersonJPAEntity;
import org.jspecify.annotations.NullUnmarked;

import java.util.Map;

import static java.util.Optional.ofNullable;

@NullUnmarked // the mapper is not under null control because it doesn't know if the value is mandatory or not.
public final class PersonDomainJPAMapper extends PersonRehydrator {
    private PersonDomainJPAMapper() {
    }

    public static PersonJPAEntity toJpa(Person person) {
        var snapshot = person.toMementoSnapshot();
        var jpaBuilder = PersonJPAEntity.builder()
                .id(snapshot.id())
                .firstName(snapshot.firstName())
                .lastName(snapshot.lastName())
                .birthDate(snapshot.birthDate())
                .birthPlace(snapshot.birthPlace());

        ofNullable(snapshot.genderChangeHistory())
                .filter(map -> map.size() > 0)
                .ifPresent(jpaBuilder::genderChangeHistory);

        ofNullable(snapshot.nationality())
                .map(Enum::toString)
                .ifPresent(jpaBuilder::nationality);

        return jpaBuilder.build();
    }

    public static PersonJPAEntity toJpa(PersonSearchQuery query) {
        var builder = PersonJPAEntity.builder()
                .firstName(query.firstName())
                .lastName(query.lastName())
                .birthDate(query.birthDate())
                .birthPlace(query.birthPlace());

        ofNullable(query.gender())
                .map(gd -> Map.of(query.birthDate().toLocalDateTime(), gd))
                .ifPresent(builder::genderChangeHistory);

        return builder.build();
    }

    public static Person toDomain(PersonJPAEntity jpaEntity) {
        var snapshotBuilder = PersonSnapshotBuilder.personSnapshot()
                .id(jpaEntity.id())
                .firstName(jpaEntity.firstName())
                .lastName(jpaEntity.lastName())
                .birthDate(jpaEntity.birthDate())
                .birthPlace(jpaEntity.birthPlace());

        ofNullable(jpaEntity.nationality()).map(Nationality::valueOfName).ifPresent(snapshotBuilder::nationality);
        ofNullable(jpaEntity.genderChangeHistory()).ifPresent(snapshotBuilder::genderChangeHistory);

        return rehydrate(snapshotBuilder.build());
    }
}
