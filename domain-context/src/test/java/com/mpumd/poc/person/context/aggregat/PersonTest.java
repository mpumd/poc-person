package com.mpumd.poc.person.context.aggregat;


import com.mpumd.poc.person.context.command.PersonRegisterCommand;
import lombok.extern.slf4j.Slf4j;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.Mockito;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@Slf4j
class PersonTest {

//    @Mock
//    PersonRegisterCommand prc;

//    Faker faker = new Faker();

    EasyRandom easyRandom = new EasyRandom();


    @Test
    void buildValidPerson() {
        var prc = Mockito.mock(PersonRegisterCommand.class);
        when(prc.firstName()).thenReturn("mpu");
        when(prc.lastName()).thenReturn("md");

        Person register = Person.register(prc);
        assertThat(register)
                .extracting("firstName")
                .isEqualTo("mpu");
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
    @NullAndEmptySource()
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
}