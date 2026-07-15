package com.mpumd.poc.person.context.command;

import com.mpumd.poc.person.context.aggregat.Gender;
import com.mpumd.poc.person.context.aggregat.Nationality;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.time.ZonedDateTime;

import static com.mpumd.poc.person.context.aggregat.Gender.ALIEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PersonRegistrationCommandTest {

    @Test
    void OK_buildCommandByBuilder() {
        var builder = PersonRegistrationCommand.builder();
        PersonRegistrationCommand cmd = builder
                .firstName("mpu")
                .lastName("md")
                .birthDate(ZonedDateTime.now())
                .birthPlace("nowwhere")
                .gender(ALIEN)
                .nationality(Nationality.FR)
                .build();

        assertThat(cmd)
                .usingRecursiveComparison()
                .isEqualTo(builder);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void KO_firstName(String val) {
        assertThatThrownBy(() -> PersonRegistrationCommand.builder()
                .firstName(val).lastName("last").birthDate(ZonedDateTime.now()).birthPlace("place")
                .gender(Gender.MALE).nationality(Nationality.FR).build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("firstName must not be empty");
    }

    @ParameterizedTest
    @NullAndEmptySource
    void KO_lastName(String val) {
        assertThatThrownBy(() -> PersonRegistrationCommand.builder()
                .firstName("first").lastName(val).birthDate(ZonedDateTime.now()).birthPlace("place")
                .gender(Gender.MALE).nationality(Nationality.FR).build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("lastName must not be empty");
    }

    @Test
    void KO_birthDate() {
        assertThatThrownBy(() -> PersonRegistrationCommand.builder()
                .firstName("first").lastName("last").birthDate(null).birthPlace("place")
                .gender(Gender.MALE).nationality(Nationality.FR).build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("birthDate must not be null");
    }

    @ParameterizedTest
    @NullAndEmptySource
    void KO_birthPlace(String val) {
        assertThatThrownBy(() -> PersonRegistrationCommand.builder()
                .firstName("first").lastName("last").birthDate(ZonedDateTime.now()).birthPlace(val)
                .gender(Gender.MALE).nationality(Nationality.FR).build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("birthPlace must not be empty");
    }

    @Test
    void KO_gender() {
        assertThatThrownBy(() -> PersonRegistrationCommand.builder()
                .firstName("first").lastName("last").birthDate(ZonedDateTime.now()).birthPlace("place")
                .gender(null).nationality(Nationality.FR).build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("gender must not be null");
    }

    @Test
    void KO_nationality() {
        assertThatThrownBy(() -> PersonRegistrationCommand.builder()
                .firstName("first").lastName("last").birthDate(ZonedDateTime.now()).birthPlace("place")
                .gender(Gender.MALE).nationality(null).build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("nationality must not be null");
    }

}
