package com.mpumd.poc.person.application.command;

import com.mpumd.poc.person.context.aggregat.Gender;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class GenderChangeCommandTest {

    @Test
    void OK() {
        val cmd = new GenderChangeCommand(UUID.randomUUID(), Gender.FEMALE, LocalDateTime.now());
        assertThat(cmd).hasNoNullFieldsOrProperties();
    }
}
