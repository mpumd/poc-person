package com.mpumd.poc.person.context.aggregat;


import com.mpumd.poc.person.context.command.PersonRegisterCommand;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.FieldPredicates;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@Slf4j
class PersonTest {

    Faker faker = new Faker();
    EasyRandom easyRandom = new EasyRandom();

    PersonRegisterCommand prc;

    {
        prc = easyRandom.nextObject(PersonRegisterCommand.class);
        assertThat(prc).hasNoNullFieldsOrProperties();
    }

    @Test
    void dontMissTest_TDD_Approch_please() {
        // instance attribut + logger
        assertEquals(Person.class.getDeclaredFields().length, 8);
    }

    @Test
    void shouldThrowExIfCommandIsNull() {
        assertThatThrownBy(() -> Person.register(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("cmd is marked non-null but is null");
    }

    @Test
    void OK_personAggregateRoot() {
        var firstName = faker.name().firstName();
        var lastName = faker.name().lastName();
        var birthDate = LocalDateTime.of(faker.timeAndDate().birthday(0, 150), LocalTime.now());
        log.info("firstName {}, lastName {}", firstName, lastName);
        var gender = Gender.ALIEN;
        var birthPlace = faker.space().planet();
        var nationality = Nationality.TT;

        var prc = mock(PersonRegisterCommand.class);
        when(prc.firstName()).thenReturn(firstName);
        when(prc.lastName()).thenReturn(lastName);
        when(prc.birthDate()).thenReturn(birthDate);
        when(prc.gender()).thenReturn(gender);
        when(prc.birthPlace()).thenReturn(birthPlace);
        when(prc.nationality()).thenReturn(nationality);

        Person register = Person.register(prc);
        assertThat(register)
                .extracting("firstName", "lastName", "birthDate", "gender", "birthPlace",
                        "nationality")
                .containsExactly(firstName, lastName, birthDate, gender, birthPlace,
                        nationality);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void KO_firstName(String val) throws IllegalAccessException, NoSuchFieldException {
        Field field = PersonRegisterCommand.class.getDeclaredField("firstName");
        field.setAccessible(true);
        field.set(prc, val);


        assertThatThrownBy(() -> Person.register(prc))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("firstName must not be empty");
    }

    @ParameterizedTest
    @NullAndEmptySource
    void KO_lastName(String val) throws NoSuchFieldException, IllegalAccessException {
        Field field = PersonRegisterCommand.class.getDeclaredField("lastName");
        field.setAccessible(true);
        field.set(prc, val);

        assertThatThrownBy(() -> Person.register(prc))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("lastName must not be empty");
    }

    @Test
    void KO_birthDate() throws NoSuchFieldException, IllegalAccessException {
        Field field = PersonRegisterCommand.class.getDeclaredField("birthDate");
        field.setAccessible(true);
        field.set(prc, null);

        assertThatThrownBy(() -> Person.register(prc))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("birthDate must not be null");
    }

    @Test
    void KO_gender() throws NoSuchFieldException, IllegalAccessException {
        Field field = PersonRegisterCommand.class.getDeclaredField("gender");
        field.setAccessible(true);
        field.set(prc, null);

        assertThatThrownBy(() -> Person.register(prc))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("gender must not be null");
    }

    @ParameterizedTest
    @NullAndEmptySource
    void KO_birthPlace(String val) throws NoSuchFieldException, IllegalAccessException {
        Field field = PersonRegisterCommand.class.getDeclaredField("birthPlace");
        field.setAccessible(true);
        field.set(prc, val);

        assertThatThrownBy(() -> Person.register(prc))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("birthPlace must not be empty");
    }

    @Test
    void KO_nationality() throws NoSuchFieldException, IllegalAccessException {
        Field field = PersonRegisterCommand.class.getDeclaredField("nationality");
        field.setAccessible(true);
        field.set(prc, null);

        assertThatThrownBy(() -> Person.register(prc))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("nationality must not be null");
    }

    @Test
    void should_informPhysicalAppearanceAndSetInstanceAttribut() {
        // generate fullfilled Person instance except physicalAppearance field
        Predicate<Field> physicalAppearancePredicate = FieldPredicates.named("physicalAppearance")
                .and(FieldPredicates.inClass(Person.class));
        var person = new EasyRandom(new EasyRandomParameters().excludeField(physicalAppearancePredicate))
                .nextObject(Person.class);
        assertThat(person).hasNoNullFieldsOrPropertiesExcept("physicalAppearance");

        short size = 170;
        short weight = 70;
        EyesColor eyesColor = EyesColor.BLUE;

        // GIVEN
        var physicalAppearanceMock = mock(PhysicalAppearance.class);
        try (var physicalAppearanceMS = mockStatic(PhysicalAppearance.class)) {
            physicalAppearanceMS.when(() -> PhysicalAppearance.inform(size, weight, eyesColor))
                    .thenReturn(physicalAppearanceMock);

            // WHEN
            person.informPhysicalAppearance(size, weight, eyesColor);

            // THEN
            assertThat(person).extracting("physicalAppearance")
                    .isEqualTo(physicalAppearanceMock);
        }
    }

    @Test
    @DisplayName("calcul a correct age related to the birthDate and an instant in the time ")
    void shouldCalculateCorrectAge() throws NoSuchFieldException, IllegalAccessException {
        var person = easyRandom.nextObject(Person.class);
        assertThat(person).hasNoNullFieldsOrProperties();

        var birthDate = LocalDateTime.of(1990, 1, 5, 23, 42);
        var now = LocalDateTime.now();
        Field field = Person.class.getDeclaredField("birthDate");
        field.setAccessible(true);
        field.set(person, birthDate);

        long expectedAge = ChronoUnit.YEARS.between(birthDate, now);
        assertEquals(expectedAge, person.calculateAge(), "the age calcule must be " + expectedAge);

    }
}