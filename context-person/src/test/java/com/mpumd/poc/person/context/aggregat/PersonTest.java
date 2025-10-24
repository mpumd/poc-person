package com.mpumd.poc.person.context.aggregat;


import com.mpumd.poc.person.context.command.GenderChangeCommand;
import com.mpumd.poc.person.context.command.InformPhysicalAppearanceCommand;
import com.mpumd.poc.person.context.command.PersonRegistrationCommand;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@Slf4j
class PersonTest {

    Faker faker = new Faker();
    PersonRegistrationCommand prc = Instancio.create(PersonRegistrationCommand.class);

    @BeforeEach
    void setUp() {
        assertThat(prc).hasNoNullFieldsOrProperties();
    }

    @Test
    @Tag("PITestIgnore")
    void dontMissTest_TDD_Approch_please_NotMutated() {
        // instance attribut + logger
        assertEquals(Person.class.getDeclaredFields().length, 9);
    }

    @Test
    void KO_NullCommand() {
        assertThatThrownBy(() -> Person.register(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("cmd is marked non-null but is null");
    }

    @Test
    void KO_NullUUID() {
        try (var mockedUUID = mockStatic(UUID.class)) {
            mockedUUID.when(UUID::randomUUID).thenReturn(null);
            assertThatThrownBy(() -> Person.register(prc))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("id is marked non-null but is null");
        }
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

        var prc = mock(PersonRegistrationCommand.class);
        when(prc.firstName()).thenReturn(firstName);
        when(prc.lastName()).thenReturn(lastName);
        when(prc.birthDate()).thenReturn(birthDate);
        when(prc.gender()).thenReturn(gender);
        when(prc.birthPlace()).thenReturn(birthPlace);
        when(prc.nationality()).thenReturn(nationality);

        // when
        Person person = Person.register(prc);

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


    @Test
    void should_informPhysicalAppearanceAndSetInstanceAttribut() {
        // generate fullfilled Person instance except physicalAppearance field
        var person = Instancio.of(Person.class)
                .ignore(field("physicalAppearance"))
                .create();
        assertThat(person).hasNoNullFieldsOrPropertiesExcept("physicalAppearance");

        InformPhysicalAppearanceCommand informPhysicalAppearanceCommand = mock();

        // GIVEN
        var physicalAppearanceMock = mock(PhysicalAppearance.class);
        try (var physicalAppearanceMS = mockStatic(PhysicalAppearance.class)) {
            physicalAppearanceMS.when(() -> PhysicalAppearance.inform(informPhysicalAppearanceCommand))
                    .thenReturn(physicalAppearanceMock);

            // WHEN
            person.informPhysicalAppearance(informPhysicalAppearanceCommand);

            // THEN
            assertThat(person).extracting("physicalAppearance")
                    .isEqualTo(physicalAppearanceMock);
        }
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
    void canChangeOfSex() {
        var person = Instancio.create(Person.class);
        assertThat(person).hasNoNullFieldsOrProperties();

        var history = new TreeMap(Map.of(person.birthDate().toLocalDateTime(), Gender.MALE));
        ReflectionTestUtils.setField(person, "genderChangeHistory", history);
        var changeDate = LocalDateTime.now();
        var changeSexCommand = new GenderChangeCommand(UUID.randomUUID(), Gender.FEMALE, changeDate);

        person.changeSex(changeSexCommand);

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
        var changeSexCommand = new GenderChangeCommand(UUID.randomUUID(), Gender.FEMALE, changeDate);

        assertThatThrownBy(() -> person.changeSex(changeSexCommand))
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
        var changeSexCommand = new GenderChangeCommand(UUID.randomUUID(), Gender.ALIEN, changeDate);

        assertThatThrownBy(() -> person.changeSex(changeSexCommand))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("%s can't become a Alien. No sugery exist to do that", person.lastName());
    }

    @Test
    void throwExWhenTheChangeSexCmdIsNull() {
        var person = Instancio.create(Person.class);
        assertThat(person).hasNoNullFieldsOrProperties();

        assertThatThrownBy(() -> person.changeSex(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("command is marked non-null but is null");
    }
}