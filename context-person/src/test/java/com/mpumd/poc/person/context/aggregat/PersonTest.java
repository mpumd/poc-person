package com.mpumd.poc.person.context.aggregat;


import com.mpumd.poc.person.context.command.GenderChangeCommand;
import com.mpumd.poc.person.context.command.PersonRegistrationCommand;
import com.mpumd.poc.person.context.exception.PersonRehydrationException;
import com.mpumd.poc.person.context.utils.builder.PersonSnapshotBuilder;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@Slf4j
class PersonTest {

    Faker faker = new Faker();

    @Test
    @Tag("PITestIgnore")
    void dontMissTest_TDD_Approch_please_NotMutated() {
        // instance attribut + logger
        assertEquals(Person.class.getDeclaredFields().length, 9);
    }

    @Test
    void OK_personAggregateRoot() {
        // given
        var firstName = faker.name().firstName();
        var lastName = faker.name().lastName();
        var birthDate = ZonedDateTime.of(
                faker.timeAndDate().birthday(0, 150),
                LocalTime.now(),
                ZoneId.of("Europe/Paris"));
        var birthDateWithNoMilliSec = birthDate.truncatedTo(ChronoUnit.SECONDS);

        log.info("firstName {}, lastName {}", firstName, lastName);
        var gender = Gender.ALIEN;
        var birthPlace = faker.space().planet();
        var nationality = Nationality.TT;

        // when
        Person person = Person.register(PersonRegistrationCommand.builder()
                .firstName(firstName)
                .lastName(lastName)
                .birthDate(birthDate)
                .birthPlace(birthPlace)
                .gender(gender)
                .nationality(nationality)
                .build()
        );

        // then
        assertThat(person)
                .extracting("firstName", "lastName", "birthDate",
                        "genderChangeHistory", "birthPlace", "nationality")
                .containsExactly(firstName, lastName, birthDateWithNoMilliSec,
                        Map.of(birthDateWithNoMilliSec.toLocalDateTime(), gender),
                        birthPlace, nationality);

        assertThat(person)
                .extracting("id")
                .isNotNull();
    }

//    @Test
//    void should_informPhysicalAppearanceAndSetInstanceAttribut() {
//        // generate fullfilled Person instance except physicalAppearance field
//        var person = Instancio.of(Person.class)
//                .ignore(field("physicalAppearance"))
//                .create();
//        assertThat(person).hasNoNullFieldsOrPropertiesExcept("physicalAppearance");
//
//        InformPhysicalAppearanceCommand informPhysicalAppearanceCommand = mock();
//
//        // GIVEN
//        var physicalAppearanceMock = mock(PhysicalAppearance.class);
//        try (var physicalAppearanceMS = mockStatic(PhysicalAppearance.class)) {
//            physicalAppearanceMS.when(() -> PhysicalAppearance.inform())
//                    .thenReturn(physicalAppearanceMock);
//
//            // WHEN
//            person.informPhysicalAppearance(informPhysicalAppearanceCommand);
//
//            // THEN
//            assertThat(person).extracting("physicalAppearance")
//                    .isEqualTo(physicalAppearanceMock);
//        }
//    }

    @Test
    void OK_rehydrationFromSnapshot() {
        var snapshot = PersonSnapshotBuilder.personSnapshot()
                .id(UUID.randomUUID())
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .birthDate(ZonedDateTime.now())
                .birthPlace(faker.space().planet())
                .genderChangeHistory(Map.of(LocalDateTime.now(), Gender.FEMALE))
                .nationality(Nationality.FR)
                .build();

        var person = Person.fromMementoSnapshot(snapshot);

        assertThat(person.toMementoSnapshot())
                .usingRecursiveComparison()
                .isEqualTo(snapshot);
    }

    @Test
    void KO_rehydrationWithoutSnapshot() {
        assertThatThrownBy(() -> Person.fromMementoSnapshot(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("personSnapshot is marked non-null but is null");
    }

    static Stream<Arguments> corruptedSnapshots() {
        var id = UUID.randomUUID();
        var birthDate = ZonedDateTime.now();
        var changeDate = LocalDateTime.now();
        var history = Map.of(changeDate, Gender.MALE);

        var nullGenderHistory = new HashMap<LocalDateTime, Gender>();
        nullGenderHistory.put(changeDate, null);
        var nullDateHistory = new HashMap<LocalDateTime, Gender>();
        nullDateHistory.put(null, Gender.MALE);

        return Stream.of(
                arguments(new PersonSnapshot(null, "mpu", "md", birthDate, "mars", history, Nationality.FR),
                        "id must not be null"),
                arguments(new PersonSnapshot(id, null, "md", birthDate, "mars", history, Nationality.FR),
                        "person %s: firstName must not be blank".formatted(id)),
                arguments(new PersonSnapshot(id, "  ", "md", birthDate, "mars", history, Nationality.FR),
                        "person %s: firstName must not be blank".formatted(id)),
                arguments(new PersonSnapshot(id, "mpu", null, birthDate, "mars", history, Nationality.FR),
                        "person %s: lastName must not be blank".formatted(id)),
                arguments(new PersonSnapshot(id, "mpu", " ", birthDate, "mars", history, Nationality.FR),
                        "person %s: lastName must not be blank".formatted(id)),
                arguments(new PersonSnapshot(id, "mpu", "md", null, "mars", history, Nationality.FR),
                        "person %s: birthDate must not be null".formatted(id)),
                arguments(new PersonSnapshot(id, "mpu", "md", birthDate, null, history, Nationality.FR),
                        "person %s: birthPlace must not be blank".formatted(id)),
                arguments(new PersonSnapshot(id, "mpu", "md", birthDate, "", history, Nationality.FR),
                        "person %s: birthPlace must not be blank".formatted(id)),
                arguments(new PersonSnapshot(id, "mpu", "md", birthDate, "mars", null, Nationality.FR),
                        "person %s: genderChangeHistory must not be null".formatted(id)),
                arguments(new PersonSnapshot(id, "mpu", "md", birthDate, "mars", nullGenderHistory, Nationality.FR),
                        "person %s: genderChangeHistory contains a null entry [%s=null]".formatted(id, changeDate)),
                arguments(new PersonSnapshot(id, "mpu", "md", birthDate, "mars", nullDateHistory, Nationality.FR),
                        "person %s: genderChangeHistory contains a null entry [null=MALE]".formatted(id)),
                arguments(new PersonSnapshot(id, "mpu", "md", birthDate, "mars", history, null),
                        "person %s: nationality must not be null".formatted(id))
        );
    }

    @ParameterizedTest
    @MethodSource("corruptedSnapshots")
    void KO_rehydrationFromCorruptedSnapshot(PersonSnapshot snapshot, String expectedMessage) {
        assertThatThrownBy(() -> Person.fromMementoSnapshot(snapshot))
                .isInstanceOf(PersonRehydrationException.class)
                .hasMessage(expectedMessage);
    }

    @Test
    @DisplayName("calcul a correct age related to the birthDate and an instant in the time ")
    void shouldCalculateCorrectAge() {
        var person = Instancio.create(Person.class);
        assertThat(person).hasNoNullFieldsOrProperties();

        var birthDate = ZonedDateTime.of(
                1990, 1, 5,
                23, 42, 34, 0,
                ZoneId.of("Europe/Paris"));


        ReflectionTestUtils.setField(person, "birthDate", birthDate);

        long expectedAge = ChronoUnit.YEARS.between(birthDate, ZonedDateTime.now());
        assertEquals(expectedAge, person.calculateAge(), "the age calcule must be " + expectedAge);
    }

    @Test
    void KO_RegisterCommand() {
        assertThatThrownBy(() -> Person.register(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("cmd is marked non-null but is null");
    }

    @Test
    void KO_ChangeSexCommand() {
        var person = Instancio.create(Person.class);
        assertThat(person).hasNoNullFieldsOrProperties();
        assertThatThrownBy(() -> person.changeSex(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("cmd is marked non-null but is null");
    }

    @Test
    void canChangeOfSex() {
        var person = Instancio.create(Person.class);
        assertThat(person).hasNoNullFieldsOrProperties();

        var history = new TreeMap(Map.of(person.birthDate().toLocalDateTime(), Gender.MALE));
        ReflectionTestUtils.setField(person, "genderChangeHistory", history);
        var changeDate = LocalDateTime.now();

        person.changeSex(new GenderChangeCommand(Gender.FEMALE, changeDate));

        assertThat(person)
                .extracting("genderChangeHistory")
                .asInstanceOf(InstanceOfAssertFactories.MAP)
                .hasSize(2)
                .containsEntry(changeDate, Gender.FEMALE);
    }

    @Test
    void throwExWhenTryToChangeSexWithTheSameSex() {
        var person = Instancio.create(Person.class);
        assertThat(person).hasNoNullFieldsOrProperties();

        var history = new TreeMap(Map.of(person.birthDate().toLocalDateTime(), Gender.FEMALE));
        ReflectionTestUtils.setField(person, "genderChangeHistory", history);
        var changeDate = LocalDateTime.now();

        assertThatThrownBy(() -> person.changeSex(new GenderChangeCommand(Gender.FEMALE, changeDate)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("%s is already a FEMALE", person.lastName());
    }

    @Test
    void throwExWhenChangeSexToAnAlien() {
        var person = Instancio.create(Person.class);
        assertThat(person).hasNoNullFieldsOrProperties();

        var history = new TreeMap(Map.of(person.birthDate().toLocalDateTime(), Gender.MALE));
        ReflectionTestUtils.setField(person, "genderChangeHistory", history);
        var changeDate = LocalDateTime.now();

        assertThatThrownBy(() -> person.changeSex(new GenderChangeCommand(Gender.ALIEN, changeDate)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("%s can't become a Alien. No sugery exist to do that", person.lastName());
    }

}
