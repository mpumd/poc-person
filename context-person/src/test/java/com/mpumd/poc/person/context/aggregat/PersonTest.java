package com.mpumd.poc.person.context.aggregat;


import com.mpumd.poc.person.context.command.InformPhysicalAppearanceCommand;
import com.mpumd.poc.person.context.command.PersonRegistrationCommand;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.FieldPredicates;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@Slf4j
class PersonTest {

    Faker faker = new Faker();
    EasyRandom easyRandom = new EasyRandom();

    PersonRegistrationCommand prc;

    {
        prc = easyRandom.nextObject(PersonRegistrationCommand.class);
        assertThat(prc).hasNoNullFieldsOrProperties();
    }

    @Test
    @Tag("PITestIgnore")
    void dontMissTest_TDD_Approch_please_NotMutated() {
        // instance attribut + logger
        assertEquals(Person.class.getDeclaredFields().length, 9);
    }

    @Test
    void shouldThrowExIfCommandIsNull() {
        assertThatThrownBy(() -> Person.register(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("cmd is marked non-null but is null");
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

        // then
        assertThat(person)
                .extracting("firstName", "lastName", "birthDate", "genderChangeHistory", "birthPlace",
                        "nationality")
                .containsExactly(firstName, lastName, birthDate,
                        Map.of(prc.birthDate().toLocalDateTime(), gender),
                        birthPlace, nationality);

        assertThat(person)
                .extracting("id")
                .isNotNull();
    }

    @ParameterizedTest
    @NullAndEmptySource
    void KO_firstName(String val) {
        ReflectionTestUtils.setField(prc, "firstName", val);

        assertThatThrownBy(() -> Person.register(prc))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("firstName must not be empty");
    }

    @ParameterizedTest
    @NullAndEmptySource
    void KO_lastName(String val) {
        ReflectionTestUtils.setField(prc, "lastName", val);

        assertThatThrownBy(() -> Person.register(prc))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("lastName must not be empty");
    }

    @Test
    void KO_birthDate() {
        ReflectionTestUtils.setField(prc, "birthDate", null);

        assertThatThrownBy(() -> Person.register(prc))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("birthDate must not be null");
    }

    @Test
    void KO_gender() {
        ReflectionTestUtils.setField(prc, "gender", null);

        assertThatThrownBy(() -> Person.register(prc))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("gender must not be null");
    }

    @ParameterizedTest
    @NullAndEmptySource
    void KO_birthPlace(String val) {
        ReflectionTestUtils.setField(prc, "birthPlace", val);

        assertThatThrownBy(() -> Person.register(prc))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("birthPlace must not be empty");
    }

    @Test
    void KO_nationality() {
        ReflectionTestUtils.setField(prc, "nationality", null);

        assertThatThrownBy(() -> Person.register(prc))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("nationality must not be null");
    }

    @Test
    void KO_UUID() {
        try (var mockedUUID = mockStatic(UUID.class)) {
            mockedUUID.when(UUID::randomUUID).thenReturn(null);
            assertThatThrownBy(() -> Person.register(prc))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("id is marked non-null but is null");
        }
    }

    @Test
    void should_informPhysicalAppearanceAndSetInstanceAttribut() {
        // generate fullfilled Person instance except physicalAppearance field
        Predicate<Field> physicalAppearancePredicate = FieldPredicates
                .named("physicalAppearance")
                .and(FieldPredicates.inClass(Person.class));
        var person = new EasyRandom(new EasyRandomParameters().excludeField(physicalAppearancePredicate))
                .nextObject(Person.class);
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
        var person = easyRandom.nextObject(Person.class);
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
        var person = easyRandom.nextObject(Person.class);
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
        var person = easyRandom.nextObject(Person.class);
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
        var person = easyRandom.nextObject(Person.class);
        assertThat(person).hasNoNullFieldsOrProperties();

        var history = new TreeMap(Map.of(person.birthDate().toLocalDateTime(), Gender.MALE));
        ReflectionTestUtils.setField(person, "genderChangeHistory", history);
        var changeDate = LocalDateTime.now();

        assertThatThrownBy(() -> person.changeSex(Gender.ALIEN, changeDate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("%s can't become a Alien. No sugery exist to do that", person.lastName());
    }

    @ParameterizedTest
    @CsvSource(nullValues = {"null"}, value = {
            "sex,null,null",
            "date,MALE,null,"
    })
    void throwExOnChangeSexCallWithNullArgs(String fieldName, Gender gender, LocalDateTime date) {
        var person = easyRandom.nextObject(Person.class);
        assertThat(person).hasNoNullFieldsOrProperties();

        assertThatThrownBy(() -> person.changeSex(gender, date))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("%s is marked non-null but is null", fieldName);
    }
}