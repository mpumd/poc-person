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
        var birthDateTruncateMillis = cmd.birthDate().truncatedTo(ChronoUnit.SECONDS);

        this.id = id;
        this.firstName = cmd.firstName();
        this.lastName = cmd.lastName();
        this.birthDate = birthDateTruncateMillis;
        this.birthPlace = cmd.birthPlace();
        this.nationality = cmd.nationality();
        this.genderChangeHistory.put(birthDateTruncateMillis.toLocalDateTime(), cmd.gender());
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
