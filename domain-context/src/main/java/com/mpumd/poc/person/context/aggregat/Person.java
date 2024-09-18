package com.mpumd.poc.person.context.aggregat;

import com.mpumd.poc.person.context.command.PersonRegisterCommand;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
public class Person {
    private final String firstName;
    private final String lastName;
    private final LocalDateTime birthDate;
    private final Gender gender;
    private final String birthPlace; // age calculated
    private final Nationality nationality;

    private PhysicalAppearance physicalAppearance;

//    private final Career career;
//    private final Personality personality;
//    private final Hobby hobby;
//    private final SocialLife socialLife;
//    private final LifeStyle lifeStyle;
//    private final LifePurpose lifePurpose;
//    private final Challenge challenge;
//    private final SignificantPossessions significantPossessions;

    private Person(@NonNull PersonRegisterCommand cmd) {
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

    // TODO caculateAge() // valeur calculer
    public static Person register(PersonRegisterCommand cmd) {
        return new Person(cmd);
    }

    public void informPhysicalAppearance(short size, short weight, EyesColor eyesColor) {
        this.physicalAppearance = PhysicalAppearance.inform(size, weight, eyesColor);
    }


}
