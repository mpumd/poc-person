package com.mpumd.poc.person.application.command;

import com.mpumd.poc.person.context.aggregat.Nationality;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static com.mpumd.poc.person.context.aggregat.Gender.ALIEN;
import static org.assertj.core.api.Assertions.assertThat;

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

}
