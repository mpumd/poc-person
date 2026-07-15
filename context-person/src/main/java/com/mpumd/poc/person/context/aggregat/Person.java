package com.mpumd.poc.person.context.aggregat;


import com.mpumd.poc.person.context.command.GenderChangeCommand;
import com.mpumd.poc.person.context.command.PersonRegistrationCommand;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

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

    public static Person register(@NonNull PersonRegistrationCommand cmd) {
        var birthDateTruncateMillis = cmd.birthDate().truncatedTo(ChronoUnit.SECONDS);

        return Person.allArgsBuilder()
                .id(UUID.randomUUID())
                .firstName(cmd.firstName())
                .lastName(cmd.lastName())
                .birthDate(birthDateTruncateMillis)
                .birthPlace(cmd.birthPlace())
                .genders(Map.of(birthDateTruncateMillis.toLocalDateTime(), cmd.gender()))
                .nationality(cmd.nationality())
                .build();
    }

    // TODO maybe it exist a better way like a pattern to instanciate the person ; a protected constructor or abstract factory...
    @Builder(builderMethodName = "allArgsBuilder")
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
}
