package com.mpumd.poc.person.context.command;

import com.mpumd.poc.person.context.aggregat.Nationality;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static com.mpumd.poc.person.context.aggregat.Gender.ALIEN;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PersonRegisterCommandTest {

    @Test
    void shouldBuildOK() {
        OffsetDateTime now = OffsetDateTime.now();
        PersonRegisterCommand pb = PersonRegisterCommand.builder()
                .firstName("mpu")
                .lastName("md")
                .birthDate(now)
                .birthPlace("nowwhere")
                .gender(ALIEN)
                .nationality(Nationality.FR)
                .build();

        assertEquals(pb.firstName(), "mpu");
        assertEquals(pb.lastName(), "md");
        assertEquals(pb.birthDate(), now);
        assertEquals(pb.birthPlace(), "nowwhere");
        assertEquals(pb.gender(), ALIEN);
        assertEquals(pb.nationality(), Nationality.FR);
    }
}