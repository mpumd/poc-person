package com.mpumd.poc.person.context.command;

import com.mpumd.poc.person.context.aggregat.Nationality;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static com.mpumd.poc.person.context.aggregat.Gender.ALIEN;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PersonRegistrationCommandTest {

    @Test
    void shouldBuildOK() {
        var birthDate = ZonedDateTime.now();
        PersonRegistrationCommand pb = PersonRegistrationCommand.builder()
                .firstName("mpu")
                .lastName("md")
                .birthDate(birthDate)
                .birthPlace("nowwhere")
                .gender(ALIEN)
                .nationality(Nationality.FR)
                .build();

        assertEquals(pb.firstName(), "mpu");
        assertEquals(pb.lastName(), "md");
        assertEquals(pb.birthDate(), birthDate);
        assertEquals(pb.birthPlace(), "nowwhere");
        assertEquals(pb.gender(), ALIEN);
        assertEquals(pb.nationality(), Nationality.FR);
    }
}