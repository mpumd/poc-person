package com.mpumd.poc.person.context.command;

import com.mpumd.poc.person.context.aggregat.Nationality;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.ZonedDateTime;

import static com.mpumd.poc.person.context.aggregat.Gender.ALIEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PersonRegistrationCommandTest {
    EasyRandom easyRandom = new EasyRandom();
    PersonRegistrationCommand.Builder prcBuilder;

    @BeforeEach
    void setUp() {
        prcBuilder = easyRandom.nextObject(PersonRegistrationCommand.Builder.class);
        assertThat(prcBuilder).hasNoNullFieldsOrProperties();
    }

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
        ReflectionTestUtils.setField(prcBuilder, "firstName", val);

        assertThatThrownBy(prcBuilder::build)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("firstName must not be empty");
    }

    @ParameterizedTest
    @NullAndEmptySource
    void KO_lastName(String val) {
        ReflectionTestUtils.setField(prcBuilder, "lastName", val);

        assertThatThrownBy(prcBuilder::build)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("lastName must not be empty");
    }

    @Test
    void KO_birthDate() {
        ReflectionTestUtils.setField(prcBuilder, "birthDate", null);

        assertThatThrownBy(prcBuilder::build)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("birthDate must not be null");
    }

    @Test
    void KO_gender() {
        ReflectionTestUtils.setField(prcBuilder, "gender", null);

        assertThatThrownBy(prcBuilder::build)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("gender must not be null");
    }

    @ParameterizedTest
    @NullAndEmptySource
    void KO_birthPlace(String val) {
        ReflectionTestUtils.setField(prcBuilder, "birthPlace", val);

        assertThatThrownBy(prcBuilder::build)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("birthPlace must not be empty");
    }

    @Test
    void KO_nationality() {
        ReflectionTestUtils.setField(prcBuilder, "nationality", null);

        assertThatThrownBy(prcBuilder::build)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("nationality must not be null");
    }

}