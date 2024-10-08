package com.mpumd.poc.person.context.aggregat;

import com.mpumd.poc.person.context.command.PersonRegistrationCommand;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Getter
public class Person {
    private final UUID id;
    private final String firstName;
    private final String lastName;
    private final Gender gender;
    private final ZonedDateTime birthDate;
    private final String birthPlace; // age calculated
    private final Nationality nationality;
    //    private final BiometricPrint biometricPrint; // facial recognition, fingerprint recognition, and iris recognition.

    private PhysicalAppearance physicalAppearance;
//    private final Personality personality;

//    private final Career career;

//    private final Hobby hobby;
//    private final SocialLife socialLife;
//    private final LifeStyle lifeStyle;
//    private final LifePurpose lifePurpose;

//    private final Challenge challenge;

//    private final SignificantPossessions significantPossessions;
//

    private Person(@NonNull PersonRegistrationCommand cmd, @NonNull UUID id) {
        this.id = id;
        this.firstName = Optional.ofNullable(cmd.firstName())
                .filter(s -> !s.isBlank())
                .orElseThrow(() -> new IllegalArgumentException("firstName must not be empty"));
        this.lastName = Optional.ofNullable(cmd.lastName())
                .filter(s -> !s.isBlank())
                .orElseThrow(() -> new IllegalArgumentException("lastName must not be empty"));
        this.birthDate = Optional.ofNullable(cmd.birthDate())
                .orElseThrow(() -> new IllegalArgumentException("birthDate must not be null"));
        this.gender = Optional.ofNullable(cmd.gender())
                .orElseThrow(() -> new IllegalArgumentException("gender must not be null"));
        this.birthPlace = Optional.ofNullable(cmd.birthPlace())
                .filter(s -> !s.isBlank())
                .orElseThrow(() -> new IllegalArgumentException("birthPlace must not be empty"));
        this.nationality = Optional.ofNullable(cmd.nationality())
                .orElseThrow(() -> new IllegalArgumentException("nationality must not be null"));
    }

    public static Person register(PersonRegistrationCommand cmd) {
        return new Person(cmd, UUID.randomUUID());
    }

    public void informPhysicalAppearance(short size, short weight, EyesColor eyesColor) {
        this.physicalAppearance = PhysicalAppearance.inform(size, weight, eyesColor);
    }

    public short calculateAge() {
        return (short) ChronoUnit.YEARS.between(birthDate, ZonedDateTime.now());
    }
}
