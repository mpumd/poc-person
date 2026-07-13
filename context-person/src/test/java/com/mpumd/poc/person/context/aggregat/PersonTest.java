package com.mpumd.poc.person.context.aggregat;


import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.TreeMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
class PersonTest {

    Faker faker = new Faker();

    @Test
    @Tag("PITestIgnore")
    void dontMissTest_TDD_Approch_please_NotMutated() {
        // instance attribut + logger
        assertEquals(Person.class.getDeclaredFields().length, 9);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void KO_firstName(String val) {
        assertThatThrownBy(() -> Person.register(val, "last", ZonedDateTime.now(), "place", Gender.MALE, Nationality.FR))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("firstName must not be empty");
    }

    @ParameterizedTest
    @NullAndEmptySource
    void KO_lastName(String val) {
        assertThatThrownBy(() -> Person.register("first", val, ZonedDateTime.now(), "place", Gender.MALE, Nationality.FR))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("lastName must not be empty");
    }

    @Test
    void KO_birthDate() {
        assertThatThrownBy(() -> Person.register("first", "last", null, "place", Gender.MALE, Nationality.FR))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("birthDate must not be null");
    }

    @ParameterizedTest
    @NullAndEmptySource
    void KO_birthPlace(String val) {
        assertThatThrownBy(() -> Person.register("first", "last", ZonedDateTime.now(), val, Gender.MALE, Nationality.FR))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("birthPlace must not be empty");
    }

    @Test
    void KO_gender() {
        assertThatThrownBy(() -> Person.register("first", "last", ZonedDateTime.now(), "place", null, Nationality.FR))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("gender must not be null");
    }

    @Test
    void KO_nationality() {
        assertThatThrownBy(() -> Person.register("first", "last", ZonedDateTime.now(), "place", Gender.MALE, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("nationality must not be null");
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

        log.info("firstName {}, lastName {}", firstName, lastName);
        var gender = Gender.ALIEN;
        var birthPlace = faker.space().planet();
        var nationality = Nationality.TT;

        // when
        Person person = Person.register(firstName, lastName, birthDate, birthPlace, gender, nationality);

        var birthDateWithNoMilliSec = birthDate.truncatedTo(ChronoUnit.SECONDS);

        // then
        assertThat(person)
                .extracting("firstName", "lastName", "birthDate", "genderChangeHistory", "birthPlace",
                        "nationality")
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
    void throwExWhenChangeSexWithNullGender() {
        var person = Instancio.create(Person.class);
        assertThat(person).hasNoNullFieldsOrProperties();

        assertThatThrownBy(() -> person.changeSex(null, LocalDateTime.now()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("gender is marked non-null but is null");
    }

    @Test
    void throwExWhenChangeSexWithNullChangeDate() {
        var person = Instancio.create(Person.class);
        assertThat(person).hasNoNullFieldsOrProperties();

        assertThatThrownBy(() -> person.changeSex(Gender.FEMALE, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("changeDate is marked non-null but is null");
    }

    @Test
    void canChangeOfSex() {
        var person = Instancio.create(Person.class);
        assertThat(person).hasNoNullFieldsOrProperties();

        var history = new TreeMap(Map.of(person.birthDate().toLocalDateTime(), Gender.MALE));
        ReflectionTestUtils.setField(person, "genderChangeHistory", history);
        var changeDate = LocalDateTime.now();

        person.changeSex(Gender.FEMALE, changeDate);

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

        assertThatThrownBy(() -> person.changeSex(Gender.FEMALE, changeDate))
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

        assertThatThrownBy(() -> person.changeSex(Gender.ALIEN, changeDate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("%s can't become a Alien. No sugery exist to do that", person.lastName());
    }

}
