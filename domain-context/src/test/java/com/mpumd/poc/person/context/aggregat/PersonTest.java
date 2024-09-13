package com.mpumd.poc.person.context.aggregat;


import com.mpumd.poc.person.context.command.PersonRegisterCommand;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@Slf4j
class PersonTest {

//    @Mock
//    PersonRegisterCommand prc;

    Faker faker = new Faker();
    EasyRandom easyRandom = new EasyRandom();

    @Test
    void dontMissTest_TDD_please() {
        // instance attribut + logger
        assertEquals(Person.class.getDeclaredFields().length, 4);
    }

    @Test
    void buildValidPerson() {
        var firstName = faker.name().firstName();
        var lastName = faker.name().lastName();
        var birthDate = LocalDateTime.of(faker.timeAndDate().birthday(0, 150), LocalTime.now());
        log.info("firstName {}, lastName {}", firstName, lastName);

        var prc = Mockito.mock(PersonRegisterCommand.class);
        when(prc.firstName()).thenReturn(firstName);
        when(prc.lastName()).thenReturn(lastName);
        when(prc.birthDate()).thenReturn(birthDate);

        Person register = Person.register(prc);
        assertThat(register)
                .extracting("firstName", "lastName", "birthDate")
                .containsExactly(firstName, lastName, birthDate);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void throwExForEmptyFirstName(String val) throws IllegalAccessException, NoSuchFieldException {
        var prc = easyRandom.nextObject(PersonRegisterCommand.class);
        assertThat(prc).hasNoNullFieldsOrProperties();

        Field field = PersonRegisterCommand.class.getDeclaredField("firstName");
        field.setAccessible(true);
        field.set(prc, val);


        assertThatThrownBy(() -> Person.register(prc))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("firstName must not be empty");
    }

    @ParameterizedTest
    @NullAndEmptySource
    void throwExForEmptyLastName(String val) throws NoSuchFieldException, IllegalAccessException {
        PersonRegisterCommand prc = easyRandom.nextObject(PersonRegisterCommand.class);
        assertThat(prc).hasNoNullFieldsOrProperties();

        Field field = PersonRegisterCommand.class.getDeclaredField("lastName");
        field.setAccessible(true);
        field.set(prc, val);

        assertThatThrownBy(() -> Person.register(prc))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("lastName must not be empty");
    }

    @Test
    void throwExForEmptyBirthDate() throws NoSuchFieldException, IllegalAccessException {
        PersonRegisterCommand prc = easyRandom.nextObject(PersonRegisterCommand.class);
        assertThat(prc).hasNoNullFieldsOrProperties();

        Field field = PersonRegisterCommand.class.getDeclaredField("birthDate");
        field.setAccessible(true);
        field.set(prc, null);

        assertThatThrownBy(() -> Person.register(prc))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("birthDate must not be null");
    }
}