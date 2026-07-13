package com.mpumd.poc.person.context.aggregat;


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

import static com.mpumd.poc.person.context.utils.ObjectsUtils.notBlank;
import static java.util.Optional.ofNullable;

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

    public static Person register(String firstName, String lastName,
                                  ZonedDateTime birthDate, String birthPlace,
                                  Gender gender, Nationality nationality) {
        ofNullable(birthDate).orElseThrow(() -> new IllegalArgumentException("birthDate must not be null"));
        ofNullable(gender).orElseThrow(() -> new IllegalArgumentException("gender must not be null"));
        ofNullable(nationality).orElseThrow(() -> new IllegalArgumentException("nationality must not be null"));

        var birthDateTruncateMillis = birthDate.truncatedTo(ChronoUnit.SECONDS);

        return Person.allArgsBuilder()
                .id(UUID.randomUUID())
                .firstName(notBlank(firstName, "firstName must not be empty"))
                .lastName(notBlank(lastName, "lastName must not be empty"))
                .birthDate(birthDateTruncateMillis)
                .birthPlace(notBlank(birthPlace, "birthPlace must not be empty"))
                .genders(Map.of(birthDateTruncateMillis.toLocalDateTime(), gender))
                .nationality(nationality)
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
    public void changeSex(@NonNull Gender gender,
                          @NonNull LocalDateTime changeDate) {
        if (Gender.ALIEN.equals(gender)) {
            throw new IllegalArgumentException("%s can't become a Alien. No sugery exist to do that".formatted(lastName));
        } else if (this.genderChangeHistory.lastEntry().getValue().equals(gender)) {
            throw new IllegalArgumentException("%s is already a %s".formatted(lastName, gender));
        }

        genderChangeHistory.putIfAbsent(changeDate, gender);
    }
}
