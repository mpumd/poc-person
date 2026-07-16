package com.mpumd.poc.person.context.aggregat;


import com.mpumd.poc.person.context.command.GenderChangeCommand;
import com.mpumd.poc.person.context.command.PersonRegistrationCommand;
import com.mpumd.poc.person.context.exception.PersonRehydrationException;
import com.mpumd.poc.person.context.utils.builder.PersonSnapshotBuilder;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import static com.mpumd.poc.person.context.utils.ObjectsUtils.notBlank;
import static com.mpumd.poc.person.context.utils.ObjectsUtils.notNull;

@Slf4j
@Getter
public class Person {
    private final UUID id;
    private final String firstName;
    private final String lastName;

    private final TreeMap<LocalDateTime, Gender> genderChangeHistory = new TreeMap<>();

    private final ZonedDateTime birthDate;
    private final String birthPlace; // age calculated
    private final Nationality nationality;
    //    private final BiometricPrint biometricPrint; // facial recognition, fingerprint recognition, and iris recognition.

    private PhysicalAppearance physicalAppearance = PhysicalAppearance.EMPTY;
//    private final Personality personality;
//    private final Career career;
//  private final Health
    // private final Contact contact

//    private final Hobby hobby;
//    private final SocialLife socialLife;
//    private final LifeStyle lifeStyle;
//    private final LifePurpose lifePurpose;

//    private final SignificantPossessions significantPossessions;

    // private: business creation goes through register, rehydration through PersonRehydrator
    private Person(UUID id, String firstName, String lastName, ZonedDateTime birthDate, String birthPlace,
                   Map<LocalDateTime, Gender> genders, Nationality nationality) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.birthPlace = birthPlace;
        this.nationality = nationality;
        this.genderChangeHistory.putAll(genders);
    }

    public static Person register(@NonNull PersonRegistrationCommand cmd) {
        var birthDateTruncateMillis = cmd.birthDate().truncatedTo(ChronoUnit.SECONDS);

        return new Person(
                UUID.randomUUID(),
                cmd.firstName(),
                cmd.lastName(),
                birthDateTruncateMillis,
                cmd.birthPlace(),
                Map.of(birthDateTruncateMillis.toLocalDateTime(), cmd.gender()),
                cmd.nationality());
    }

    public void informPhysicalAppearance(short size, short weight, EyesColor eyesColor, HairColor hairColor) {
        this.physicalAppearance = PhysicalAppearance.inform()
                .size(size)
                .weight(weight)
                .eyesColor(eyesColor)
                .hairColor(hairColor)
                .build();
    }

    public short calculateAge() {
        return (short) ChronoUnit.YEARS.between(birthDate, ZonedDateTime.now());
    }


    // TODO move to physicalAppearance
    public void changeSex(@NonNull GenderChangeCommand cmd) {
        if (Gender.ALIEN.equals(cmd.gender())) {
            throw new IllegalArgumentException("%s can't become a Alien. No sugery exist to do that".formatted(lastName));
        } else if (this.genderChangeHistory.lastEntry().getValue().equals(cmd.gender())) {
            throw new IllegalArgumentException("%s is already a %s".formatted(lastName, cmd.gender()));
        }

        genderChangeHistory.putIfAbsent(cmd.changeDate(), cmd.gender());
    }

    // Load from repository using Memento pattern

    public PersonSnapshot toMementoSnapshot() {
        return PersonSnapshotBuilder.personSnapshot()
                .id(id)
                .firstName(firstName)
                .lastName(lastName)
                .birthDate(birthDate)
                .birthPlace(birthPlace)
                .genderChangeHistory(Collections.unmodifiableNavigableMap(new TreeMap<>(genderChangeHistory)))
                .nationality(nationality)
                .build();
    }

    static Person fromMementoSnapshot(@NonNull PersonSnapshot personSnapshot) {
        var id = notNull(personSnapshot.id(), () -> new PersonRehydrationException("id must not be null"));
        notBlank(personSnapshot.firstName(), () -> new PersonRehydrationException("person %s: firstName must not be blank", id));
        notBlank(personSnapshot.lastName(), () -> new PersonRehydrationException("person %s: lastName must not be blank", id));
        notNull(personSnapshot.birthDate(), () -> new PersonRehydrationException("person %s: birthDate must not be null", id));
        notBlank(personSnapshot.birthPlace(), () -> new PersonRehydrationException("person %s: birthPlace must not be blank", id));
        notNull(personSnapshot.genderChangeHistory(), () -> new PersonRehydrationException("person %s: genderChangeHistory must not be null", id))
                .forEach((date, gender) -> {
                    if (date == null || gender == null) {
                        throw new PersonRehydrationException(
                                "person %s: genderChangeHistory contains a null entry [%s=%s]", id, date, gender);
                    }
                });
        notNull(personSnapshot.nationality(), () -> new PersonRehydrationException("person %s: nationality must not be null", id));

        return new Person(personSnapshot.id(), personSnapshot.firstName(), personSnapshot.lastName(), personSnapshot.birthDate(),
                personSnapshot.birthPlace(), personSnapshot.genderChangeHistory(), personSnapshot.nationality());
    }
}
