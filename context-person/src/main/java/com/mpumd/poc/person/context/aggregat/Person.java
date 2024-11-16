package com.mpumd.poc.person.context.aggregat;

import com.mpumd.poc.person.context.command.GenderChangeCommand;
import com.mpumd.poc.person.context.command.InformPhysicalAppearanceCommand;
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

    private PhysicalAppearance physicalAppearance;
//    private final Personality personality;
//    private final Career career;
//  private final Health
    // private final Contact contact

//    private final Hobby hobby;
//    private final SocialLife socialLife;
//    private final LifeStyle lifeStyle;
//    private final LifePurpose lifePurpose;

//    private final SignificantPossessions significantPossessions;

    public static Person register(PersonRegistrationCommand cmd) {
        return new Person(cmd, UUID.randomUUID());
    }

    private Person(@NonNull PersonRegistrationCommand cmd, @NonNull UUID id) {
        this.id = id;
        this.firstName = cmd.firstName();
        this.lastName = cmd.lastName();
        this.birthDate = cmd.birthDate();
        this.birthPlace = cmd.birthPlace();
        this.nationality = cmd.nationality();
        this.genderChangeHistory.put(cmd.birthDate().toLocalDateTime(), cmd.gender());

//        this.firstName = ofNullable(cmd.firstName())
//                .filter(s -> !s.isBlank())
//                .orElseThrow(() -> new IllegalArgumentException("firstName must not be empty"));
//        this.lastName = ofNullable(cmd.lastName())
//                .filter(s -> !s.isBlank())
//                .orElseThrow(() -> new IllegalArgumentException("lastName must not be empty"));
//        this.birthDate = ofNullable(cmd.birthDate())
//                .orElseThrow(() -> new IllegalArgumentException("birthDate must not be null"));
//        this.birthPlace = ofNullable(cmd.birthPlace())
//                .filter(s -> !s.isBlank())
//                .orElseThrow(() -> new IllegalArgumentException("birthPlace must not be empty"));
//        genderChangeHistory.put(cmd.birthDate().toLocalDateTime(), cmd.gender());
//                .map(gender -> Map.of(cmd.birthDate().toLocalDateTime(), gender))
//                .orElseThrow(() -> new IllegalArgumentException("gender must not be null"));
//        );
//        this.nationality = ofNullable(cmd.nationality())
//                .orElseThrow(() -> new IllegalArgumentException("nationality must not be null"));
    }
    // TODO maybe it exist a better way like a pattern to instanciate the person ; a protected constructor or abstract factory...

    @Builder(builderMethodName = "builderFromRepository")
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

    public void informPhysicalAppearance(InformPhysicalAppearanceCommand cmd) {
        this.physicalAppearance = PhysicalAppearance.inform(cmd);
    }

    public short calculateAge() {
        return (short) ChronoUnit.YEARS.between(birthDate, ZonedDateTime.now());
    }

    // TODO move to physicalAppearance
    public void changeSex(@NonNull GenderChangeCommand command) {
        if (Gender.ALIEN.equals(command.gender())) {
            throw new IllegalArgumentException("%s can't become a Alien. No sugery exist to do that".formatted(lastName));
        } else if (this.genderChangeHistory.lastEntry().getValue().equals(command.gender())) {
            throw new IllegalArgumentException("%s is already a %s".formatted(lastName, command.gender()));
        }

        genderChangeHistory.putIfAbsent(command.changeDate(), command.gender());
    }
}
